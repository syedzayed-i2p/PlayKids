import { useEffect, useRef, useState } from 'react';
import { BanglaAudioEngine } from './audio/BanglaAudioEngine';
import {
  AudioSettingsState,
  GameTypeId,
  LearningMode,
  ScreenType,
} from './types/game';
import {
  createDefaultProgressMap,
  createDefaultSkillMastery,
  loadStoredAppData,
  saveStoredAppData,
  StoredAppData,
} from './services/progressStorage';
import {
  MyProgressScreen,
  PlayoraSplashScreen,
  PlayoraWelcomeScreen,
} from './screens/PlayoraIntroAndProgressScreens';
import { HomeScreen } from './screens/HomeScreen';
import { LevelSelectionScreen } from './screens/LevelSelectionScreen';
import { EmojiMatchingScreen } from './screens/EmojiMatchingScreen';
import { ShapeMatchingScreen } from './screens/ShapeMatchingScreen';
import { ColorShapeChallengeScreen } from './screens/ColorShapeChallengeScreen';
import { ColorPaperMatchingScreen } from './screens/ColorPaperMatchingScreen';
import { PhysicalPracticeScreen } from './screens/PhysicalPracticeScreen';
import { ParentDashboardScreen } from './screens/ParentDashboardScreen';

export function App() {
  const audioRef = useRef<BanglaAudioEngine | null>(null);
  if (!audioRef.current) {
    audioRef.current = new BanglaAudioEngine();
  }
  const audio = audioRef.current;

  const [store, setStore] = useState<StoredAppData>(() => loadStoredAppData());
  const [currentScreen, setCurrentScreen] = useState<ScreenType>('SPLASH');
  const [activeGameForMap, setActiveGameForMap] = useState<GameTypeId>('emoji_match');
  const [selectedLevelOverride, setSelectedLevelOverride] = useState<number | null>(null);
  const [physicalInitialGame, setPhysicalInitialGame] = useState<GameTypeId>('emoji_match');

  useEffect(() => {
    audio.updateSettings(store.audioSettings);
    saveStoredAppData(store);
  }, [store, audio]);

  const setLearningMode = (mode: LearningMode) => {
    setStore(prev => ({ ...prev, learningMode: mode }));
  };

  const updateAudioSettings = (newSettings: AudioSettingsState) => {
    setStore(prev => ({ ...prev, audioSettings: newSettings }));
  };

  const navigateToGame = (gameType: GameTypeId, levelOverride: number | null = null) => {
    audio.stopAndClearQueue();
    setSelectedLevelOverride(levelOverride);
    setStore(prev => ({ ...prev, lastPlayedGame: gameType }));
    switch (gameType) {
      case 'emoji_match':
        setCurrentScreen('GAME_EMOJI');
        break;
      case 'shape_match':
        setCurrentScreen('GAME_SHAPE');
        break;
      case 'color_shape_challenge':
        setCurrentScreen('GAME_COLOR_SHAPE');
        break;
      case 'color_paper_match':
        setCurrentScreen('GAME_COLOR_PAPER');
        break;
    }
  };

  const handleBottomTabNavigation = (target: ScreenType) => {
    audio.stopAndClearQueue();
    setCurrentScreen(target);
  };

  const recordAttempt = (
    gameType: GameTypeId,
    isSuccess: boolean,
    isIndependent: boolean,
    shouldAdvanceLevel: boolean,
    completedLevelNumber: number,
    mode: LearningMode
  ) => {
    setStore(prev => {
      const current = prev.progressMap[gameType];
      const nextPlayed = current.totalPlayed + 1;
      if (!isSuccess) {
        return {
          ...prev,
          lastPlayedGame: gameType,
          progressMap: {
            ...prev.progressMap,
            [gameType]: { ...current, totalPlayed: nextPlayed },
          },
        };
      }

      const earnedBonus = isIndependent ? 2 : 1;
      const nextStars = current.starsEarned + earnedBonus;
      const nextSucc = isIndependent
        ? current.independentSuccesses + 1
        : current.independentSuccesses;

      if (mode === 'core') {
        const compSet = Array.from(
          new Set([...current.coreCompletedLevels, completedLevelNumber])
        );
        const nextCore = shouldAdvanceLevel
          ? Math.min(5, Math.max(current.unlockedLevel, completedLevelNumber + 1))
          : current.unlockedLevel;
        return {
          ...prev,
          lastPlayedGame: gameType,
          progressMap: {
            ...prev.progressMap,
            [gameType]: {
              ...current,
              totalPlayed: nextPlayed,
              starsEarned: nextStars,
              independentSuccesses: nextSucc,
              coreCompletedLevels: compSet,
              unlockedLevel: nextCore,
            },
          },
        };
      } else {
        const compSet = Array.from(
          new Set([...current.completedLevels, completedLevelNumber])
        );
        const nextExt = shouldAdvanceLevel
          ? Math.min(25, Math.max(current.extendedUnlockedLevel, completedLevelNumber + 1))
          : current.extendedUnlockedLevel;
        return {
          ...prev,
          lastPlayedGame: gameType,
          progressMap: {
            ...prev.progressMap,
            [gameType]: {
              ...current,
              totalPlayed: nextPlayed,
              starsEarned: nextStars,
              independentSuccesses: nextSucc,
              completedLevels: compSet,
              extendedUnlockedLevel: nextExt,
            },
          },
        };
      }
    });
  };

  const recordSkillMastery = (colorId?: string, shapeId?: string) => {
    setStore(prev => {
      const nextColors = { ...prev.skillMastery.colorAccuracyMap };
      const nextShapes = { ...prev.skillMastery.shapeAccuracyMap };
      if (colorId) {
        nextColors[colorId] = (nextColors[colorId] || 0) + 1;
      }
      if (shapeId) {
        nextShapes[shapeId] = (nextShapes[shapeId] || 0) + 1;
      }
      return {
        ...prev,
        skillMastery: {
          colorAccuracyMap: nextColors,
          shapeAccuracyMap: nextShapes,
        },
      };
    });
  };

  const recordHintUsed = (gameType: GameTypeId) => {
    setStore(prev => {
      const current = prev.progressMap[gameType];
      return {
        ...prev,
        progressMap: {
          ...prev.progressMap,
          [gameType]: {
            ...current,
            hintsUsed: current.hintsUsed + 1,
          },
        },
      };
    });
  };

  const setPhysicalPracticeCompleted = (gameType: GameTypeId, completed: boolean) => {
    setStore(prev => {
      const current = prev.progressMap[gameType];
      return {
        ...prev,
        progressMap: {
          ...prev.progressMap,
          [gameType]: {
            ...current,
            physicalCompleted: completed,
          },
        },
      };
    });
  };

  const unlockAllLevelsForAllGames = () => {
    setStore(prev => {
      const nextMap = { ...prev.progressMap };
      (Object.keys(nextMap) as GameTypeId[]).forEach(id => {
        nextMap[id] = {
          ...nextMap[id],
          unlockedLevel: 5,
          extendedUnlockedLevel: 25,
        };
      });
      return { ...prev, progressMap: nextMap };
    });
  };

  const resetAllProgress = () => {
    setStore(prev => ({
      ...prev,
      progressMap: createDefaultProgressMap(),
      skillMastery: createDefaultSkillMastery(),
    }));
  };

  switch (currentScreen) {
    case 'SPLASH':
      return (
        <PlayoraSplashScreen
          reducedMotion={store.audioSettings.reducedMotion}
          onSplashFinished={() => setCurrentScreen('WELCOME')}
        />
      );

    case 'WELCOME':
      return (
        <PlayoraWelcomeScreen
          audio={audio}
          reducedMotion={store.audioSettings.reducedMotion}
          onStartExploring={() => setCurrentScreen('HOME')}
        />
      );

    case 'HOME':
      return (
        <HomeScreen
          allProgress={store.progressMap}
          learningMode={store.learningMode}
          lastPlayedGame={store.lastPlayedGame}
          audio={audio}
          audioSettings={store.audioSettings}
          onSelectMode={setLearningMode}
          onSelectGame={g => navigateToGame(g, null)}
          onContinueLearning={(g, lvl) => navigateToGame(g, lvl)}
          onOpenLevelMap={g => {
            audio.stopAndClearQueue();
            setActiveGameForMap(g);
            setCurrentScreen('LEVEL_MAP');
          }}
          onOpenMyProgress={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('MY_PROGRESS');
          }}
          onOpenPhysicalPractice={() => {
            audio.stopAndClearQueue();
            setPhysicalInitialGame('emoji_match');
            setCurrentScreen('PHYSICAL_PRACTICE');
          }}
          onOpenParentDashboard={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('PARENT_DASHBOARD');
          }}
          onToggleVoiceMute={() => {
            updateAudioSettings({
              ...store.audioSettings,
              voiceEnabled: !store.audioSettings.voiceEnabled,
            });
          }}
          onUpdateAudioSettings={updateAudioSettings}
        />
      );

    case 'MY_PROGRESS':
      return (
        <MyProgressScreen
          allProgress={store.progressMap}
          learningMode={store.learningMode}
          audio={audio}
          onOpenLevelMap={g => {
            setActiveGameForMap(g);
            setCurrentScreen('LEVEL_MAP');
          }}
          onNavigateTab={handleBottomTabNavigation}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
        />
      );

    case 'LEVEL_MAP':
      return (
        <LevelSelectionScreen
          gameType={activeGameForMap}
          progress={store.progressMap[activeGameForMap]}
          learningMode={store.learningMode}
          audio={audio}
          onSelectMode={setLearningMode}
          onSelectLevel={lvl => navigateToGame(activeGameForMap, lvl)}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
        />
      );

    case 'GAME_EMOJI':
      return (
        <EmojiMatchingScreen
          audio={audio}
          progress={store.progressMap.emoji_match}
          learningMode={store.learningMode}
          initialLevelOverride={selectedLevelOverride}
          onSelectMode={m => {
            setSelectedLevelOverride(null);
            setLearningMode(m);
          }}
          onOpenLevelMap={() => {
            setActiveGameForMap('emoji_match');
            setCurrentScreen('LEVEL_MAP');
          }}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onRecordAttempt={(succ, ind, adv, lvl) =>
            recordAttempt('emoji_match', succ, ind, adv, lvl, store.learningMode)
          }
          onRecordHint={() => recordHintUsed('emoji_match')}
          onGoPhysicalPractice={() => {
            audio.stopAndClearQueue();
            setPhysicalInitialGame('emoji_match');
            setCurrentScreen('PHYSICAL_PRACTICE');
          }}
        />
      );

    case 'GAME_SHAPE':
      return (
        <ShapeMatchingScreen
          audio={audio}
          progress={store.progressMap.shape_match}
          learningMode={store.learningMode}
          initialLevelOverride={selectedLevelOverride}
          onSelectMode={m => {
            setSelectedLevelOverride(null);
            setLearningMode(m);
          }}
          onOpenLevelMap={() => {
            setActiveGameForMap('shape_match');
            setCurrentScreen('LEVEL_MAP');
          }}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onRecordAttempt={(succ, ind, adv, lvl) =>
            recordAttempt('shape_match', succ, ind, adv, lvl, store.learningMode)
          }
          onRecordSkillMastery={shapeId => recordSkillMastery(undefined, shapeId)}
          onRecordHint={() => recordHintUsed('shape_match')}
          onGoPhysicalPractice={() => {
            audio.stopAndClearQueue();
            setPhysicalInitialGame('shape_match');
            setCurrentScreen('PHYSICAL_PRACTICE');
          }}
        />
      );

    case 'GAME_COLOR_SHAPE':
      return (
        <ColorShapeChallengeScreen
          audio={audio}
          progress={store.progressMap.color_shape_challenge}
          learningMode={store.learningMode}
          initialLevelOverride={selectedLevelOverride}
          onSelectMode={m => {
            setSelectedLevelOverride(null);
            setLearningMode(m);
          }}
          onOpenLevelMap={() => {
            setActiveGameForMap('color_shape_challenge');
            setCurrentScreen('LEVEL_MAP');
          }}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onRecordAttempt={(succ, ind, adv, lvl) =>
            recordAttempt('color_shape_challenge', succ, ind, adv, lvl, store.learningMode)
          }
          onRecordSkillMastery={(colorId, shapeId) => recordSkillMastery(colorId, shapeId)}
          onRecordHint={() => recordHintUsed('color_shape_challenge')}
          onGoPhysicalPractice={() => {
            audio.stopAndClearQueue();
            setPhysicalInitialGame('color_shape_challenge');
            setCurrentScreen('PHYSICAL_PRACTICE');
          }}
        />
      );

    case 'GAME_COLOR_PAPER':
      return (
        <ColorPaperMatchingScreen
          audio={audio}
          progress={store.progressMap.color_paper_match}
          learningMode={store.learningMode}
          initialLevelOverride={selectedLevelOverride}
          onSelectMode={m => {
            setSelectedLevelOverride(null);
            setLearningMode(m);
          }}
          onOpenLevelMap={() => {
            setActiveGameForMap('color_paper_match');
            setCurrentScreen('LEVEL_MAP');
          }}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onRecordAttempt={(succ, ind, adv, lvl) =>
            recordAttempt('color_paper_match', succ, ind, adv, lvl, store.learningMode)
          }
          onRecordSkillMastery={colorId => recordSkillMastery(colorId, undefined)}
          onRecordHint={() => recordHintUsed('color_paper_match')}
          onGoPhysicalPractice={() => {
            audio.stopAndClearQueue();
            setPhysicalInitialGame('color_paper_match');
            setCurrentScreen('PHYSICAL_PRACTICE');
          }}
        />
      );

    case 'PHYSICAL_PRACTICE':
      return (
        <PhysicalPracticeScreen
          initialGameType={physicalInitialGame}
          allProgress={store.progressMap}
          audio={audio}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onTogglePhysicalDone={setPhysicalPracticeCompleted}
          onNavigateTab={handleBottomTabNavigation}
        />
      );

    case 'PARENT_DASHBOARD':
      return (
        <ParentDashboardScreen
          allProgress={store.progressMap}
          skillMastery={store.skillMastery}
          audio={audio}
          audioSettings={store.audioSettings}
          onBack={() => {
            audio.stopAndClearQueue();
            setCurrentScreen('HOME');
          }}
          onUpdateAudioSettings={updateAudioSettings}
          onUnlockAllLevels={unlockAllLevelsForAllGames}
          onResetAllProgress={resetAllProgress}
          onNavigateTab={handleBottomTabNavigation}
        />
      );
  }
}

export default App;
