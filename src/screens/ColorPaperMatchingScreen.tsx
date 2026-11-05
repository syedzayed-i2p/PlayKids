import React, { useEffect, useMemo, useState } from 'react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  KidColor,
  LearningMode,
} from '../types/game';
import {
  ALL_10_COLORS,
  CORE_COLORS,
  EXTENDED_ONLY_COLORS,
  KID_COLORS,
} from '../data/colors';
import { getLevelsForGame } from '../data/curriculum';
import {
  GameplayControlBar,
  GamePauseDialog,
  GameSuccessDialog,
  GameTopBar,
  LearningModeSwitcherBar,
  MascotSpeechPrompt,
  PlayoraAnimatedSceneBackground,
} from '../components/MascotAndHeader';

function shuffleArray<T>(arr: T[]): T[] {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

export const ColorPaperMatchingScreen: React.FC<{
  audio: BanglaAudioEngine;
  progress: GameProgressData;
  learningMode: LearningMode;
  initialLevelOverride?: number | null;
  onSelectMode: (mode: LearningMode) => void;
  onOpenLevelMap: () => void;
  onBack: () => void;
  onRecordAttempt: (
    isSuccess: boolean,
    isIndependent: boolean,
    shouldAdvance: boolean,
    completedLevel: number
  ) => void;
  onRecordSkillMastery: (colorId: string) => void;
  onRecordHint: () => void;
  onGoPhysicalPractice: () => void;
}> = ({
  audio,
  progress,
  learningMode,
  initialLevelOverride,
  onSelectMode,
  onOpenLevelMap,
  onBack,
  onRecordAttempt,
  onRecordSkillMastery,
  onRecordHint,
  onGoPhysicalPractice,
}) => {
  const maxLevel = learningMode === 'core' ? 5 : 25;
  const maxUnlocked =
    learningMode === 'core'
      ? Math.min(5, Math.max(1, progress.unlockedLevel))
      : Math.min(25, Math.max(1, Math.max(progress.unlockedLevel, progress.extendedUnlockedLevel)));

  const activeCompletedSet = new Set(
    learningMode === 'core' ? progress.coreCompletedLevels : progress.completedLevels
  );

  const [level, setLevel] = useState<number>(() => {
    const def = Math.min(maxLevel, Math.max(1, maxUnlocked));
    return Math.min(maxLevel, Math.max(1, initialLevelOverride ?? def));
  });

  const [restartTrigger, setRestartTrigger] = useState(0);
  const [showPauseDialog, setShowPauseDialog] = useState(false);

  const byId = (id: string) => KID_COLORS.find(c => c.id === id) || CORE_COLORS[0];

  const levelTrays: KidColor[] = useMemo(() => {
    if (learningMode === 'core') {
      if (level === 1) return [byId('red'), byId('yellow')];
      if (level === 2) return [byId('red'), byId('yellow'), byId('blue')];
      if (level === 4) return shuffleArray(CORE_COLORS);
      return CORE_COLORS;
    } else {
      switch (level) {
        case 1:
          return [byId('red'), byId('yellow')];
        case 2:
          return [byId('red'), byId('yellow'), byId('blue')];
        case 3:
          return [byId('red'), byId('yellow'), byId('blue'), byId('green')];
        case 4:
          return CORE_COLORS;
        case 5:
          return [byId('blue'), byId('red'), byId('green')];
        case 6:
          return [byId('orange'), byId('yellow'), byId('blue'), byId('red')];
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
          return shuffleArray(CORE_COLORS);
        case 13:
          return [byId('pink'), byId('purple'), byId('red'), byId('blue')];
        case 14:
          return [byId('pink'), byId('purple'), byId('brown'), byId('yellow'), byId('green')];
        case 15:
        case 16:
          return EXTENDED_ONLY_COLORS;
        case 17:
          return [
            byId('red'),
            byId('yellow'),
            byId('blue'),
            byId('pink'),
            byId('purple'),
            byId('orange'),
          ];
        case 18:
          return [
            byId('green'),
            byId('orange'),
            byId('brown'),
            byId('black'),
            byId('white'),
            byId('pink'),
          ];
        case 19:
        case 20:
          return shuffleArray(ALL_10_COLORS).slice(0, 6);
        case 21:
        case 22:
          return shuffleArray(ALL_10_COLORS).slice(0, 7);
        case 23:
        case 24:
        case 25:
          return shuffleArray(ALL_10_COLORS).slice(0, 8);
        default:
          return CORE_COLORS;
      }
    }
  }, [learningMode, level]);

  const initialPaperQueue: KidColor[] = useMemo(() => {
    const multiLevels = [9, 10, 11, 12, 16, 19, 20, 21, 22, 23, 24, 25];
    if (learningMode === 'extended' && multiLevels.includes(level)) {
      return shuffleArray([...levelTrays, ...shuffleArray(levelTrays).slice(0, 3)]);
    }
    return shuffleArray(levelTrays);
  }, [learningMode, level, levelTrays, restartTrigger]);

  const [remainingPapers, setRemainingPapers] = useState<KidColor[]>(initialPaperQueue);
  const [trayPaperCounts, setTrayPaperCounts] = useState<Record<string, number>>({});
  const [errorCount, setErrorCount] = useState(0);
  const [hintActive, setHintActive] = useState(false);
  const [demoActive, setDemoActive] = useState(false);
  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [speechText, setSpeechText] = useState('নীল রংটি খুঁজে বের করো।');

  useEffect(() => {
    setRemainingPapers(initialPaperQueue);
    setTrayPaperCounts({});
    setErrorCount(0);
    setHintActive(false);
    setDemoActive(false);
    setShowSuccessDialog(false);
  }, [initialPaperQueue]);

  const currentTargetPaper = remainingPapers[0] || null;

  useEffect(() => {
    if (currentTargetPaper) {
      const prompt = `${currentTargetPaper.nameBn} রংটি খুঁজে বের করো।`;
      setSpeechText(prompt);
      audio.speak(
        prompt,
        `paper_${learningMode}_L${level}_R${restartTrigger}_rem${remainingPapers.length}_${currentTargetPaper.id}`,
        false,
        true
      );
    } else if (remainingPapers.length === 0 && initialPaperQueue.length > 0) {
      audio.playLevelCompleteFanfare();
      audio.speak(
        'এই ধাপটি সম্পন্ন হয়েছে।',
        `paper_complete_${learningMode}_L${level}_R${restartTrigger}`,
        false,
        true
      );
      onRecordAttempt(true, errorCount === 0, level < maxLevel, level);
      setShowSuccessDialog(true);
    }
  }, [currentTargetPaper, remainingPapers.length, initialPaperQueue.length]);

  const restartCurrentLevel = () => {
    audio.stopAndClearQueue();
    audio.playButtonTap();
    setShowSuccessDialog(false);
    setShowPauseDialog(false);
    setRestartTrigger(prev => prev + 1);
  };

  const levelDef = useMemo(
    () => getLevelsForGame('color_paper_match')[level - 1],
    [level]
  );

  const hasNextUnlocked =
    level < maxLevel &&
    (level + 1 <= maxUnlocked || activeCompletedSet.has(level + 1) || activeCompletedSet.has(level));

  const placedCount = initialPaperQueue.length - remainingPapers.length;

  return (
    <PlayoraAnimatedSceneBackground
      theme="CREATIVE_ART_STUDIO"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top bar */}
        <div className="bg-white/90 shadow-xs pb-1">
          <GameTopBar
            title={`${GAME_TYPES.color_paper_match.titleEn} • ${GAME_TYPES.color_paper_match.titleBn}`}
            currentLevel={level}
            starsCount={progress.starsEarned}
            onBackClick={onBack}
            onOpenLevelMap={onOpenLevelMap}
          />
          <GameplayControlBar
            currentLevel={level}
            maxLevel={maxLevel}
            hasPreviousLevel={level > 1}
            hasNextUnlockedLevel={hasNextUnlocked}
            onPreviousLevel={() => {
              if (level > 1) {
                audio.stopAndClearQueue();
                audio.playButtonTap();
                setLevel(l => l - 1);
              }
            }}
            onRestartLevel={restartCurrentLevel}
            onPauseClick={() => {
              audio.stopAndClearQueue();
              audio.playButtonTap();
              setShowPauseDialog(true);
            }}
            onNextLevel={() => {
              if (hasNextUnlocked && level < maxLevel) {
                audio.stopAndClearQueue();
                audio.playButtonTap();
                setLevel(l => l + 1);
              }
            }}
            onLockedNextAttempt={() => {
              audio.playTryAgainChime();
              setSpeechText('আগের ধাপটি শেষ করে এটি খোলো।');
              audio.replayInstruction('আগের ধাপটি শেষ করে এটি খোলো।');
            }}
          />
          <LearningModeSwitcherBar
            currentMode={learningMode}
            onSelectMode={mode => {
              audio.stopAndClearQueue();
              audio.playButtonTap();
              onSelectMode(mode);
            }}
          />
        </div>

        {/* Main Gameplay */}
        <div className="flex-1 flex flex-col items-center px-4 py-2 space-y-2.5 overflow-y-auto">
          <MascotSpeechPrompt
            text={speechText}
            onReplayAudio={() =>
              audio.replayInstruction(
                currentTargetPaper
                  ? `${currentTargetPaper.nameBn} রংটি খুঁজে বের করো।`
                  : speechText
              )
            }
            isHighlighted={hintActive || demoActive}
          />

          {/* Upper Hand Card: Colored Craft Paper Sheet */}
          <div className="w-full bg-white/95 rounded-3xl p-3.5 shadow-md border-2 border-white flex flex-col items-center">
            <div className="w-full flex items-center justify-between">
              <span className="font-bold text-xs text-[#FF7777]">
                {levelDef?.titleBn || `লেভেল ${level}`}
              </span>
              <span className="bg-[#FFF0ED] text-[#243B80] font-extrabold text-xs px-3 py-1 rounded-xl">
                কাগজ: {placedCount}/{initialPaperQueue.length}
              </span>
            </div>

            {currentTargetPaper && (
              <div
                style={{
                  backgroundColor: currentTargetPaper.color,
                  borderColor:
                    currentTargetPaper.id === 'white' || currentTargetPaper.id === 'yellow'
                      ? currentTargetPaper.darkColor
                      : '#FFFFFF',
                }}
                className="mt-2.5 w-44 h-19 rounded-2xl border-3 shadow-md flex items-center justify-center"
              >
                <span
                  style={{
                    color:
                      currentTargetPaper.id === 'white' || currentTargetPaper.id === 'yellow'
                        ? '#243B80'
                        : '#FFFFFF',
                  }}
                  className="font-extrabold text-base"
                >
                  {currentTargetPaper.patternSymbol} {currentTargetPaper.nameBn} কাগজ
                </span>
              </div>
            )}
          </div>

          <div className="bg-white/90 px-4 py-1 rounded-xl font-bold text-xs text-[#243B80]">
            সঠিক রঙের ট্রেতে ট্যাপ করে কাগজটি রাখো:
          </div>

          {/* Trays List */}
          <div className="w-full space-y-2.5 pb-4">
            {levelTrays.map(trayColor => {
              const countInTray = trayPaperCounts[trayColor.id] || 0;
              const isTarget = trayColor.id === currentTargetPaper?.id;
              const shouldHighlight = (hintActive || demoActive) && isTarget;
              const isLightSwatch = trayColor.id === 'white' || trayColor.id === 'yellow';

              return (
                <button
                  key={trayColor.id}
                  type="button"
                  data-testid={`color_tray_${trayColor.id}`}
                  onClick={() => {
                    audio.playEffect('CARD_PLACE');

                    if (isTarget && currentTargetPaper) {
                      audio.playSuccessChime();
                      audio.speakPraise();
                      setSpeechText('খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।');
                      if (errorCount === 0) {
                        onRecordSkillMastery(trayColor.id);
                      }
                      setTrayPaperCounts(prev => ({
                        ...prev,
                        [trayColor.id]: (prev[trayColor.id] || 0) + 1,
                      }));
                      setRemainingPapers(prev => prev.slice(1));
                      setErrorCount(0);
                      setHintActive(false);
                      setDemoActive(false);
                    } else {
                      audio.playTryAgainChime();
                      const nextErr = errorCount + 1;
                      setErrorCount(nextErr);
                      onRecordHint();
                      if (nextErr === 1) {
                        setSpeechText('আবার চেষ্টা করো।');
                        audio.speak('আবার চেষ্টা করো।');
                      } else if (nextErr === 2) {
                        setSpeechText('একটু ভালো করে দেখো।');
                        audio.speak('একটু ভালো করে দেখো।');
                        setHintActive(true);
                      } else {
                        setSpeechText(`সঠিক কার্ডটি বেছে নাও: ${currentTargetPaper?.nameBn}।`);
                        audio.speak('সঠিক কার্ডটি বেছে নাও।');
                        setHintActive(true);
                        setDemoActive(true);
                      }
                    }
                  }}
                  style={{
                    backgroundColor: shouldHighlight ? '#FFF9E6' : trayColor.lightColor,
                    borderColor: shouldHighlight ? '#F59E0B' : trayColor.darkColor,
                  }}
                  className="w-full h-18 rounded-2xl px-4 border-3 shadow flex items-center justify-between cursor-pointer active:scale-98 transition-all"
                >
                  <div className="flex items-center gap-3.5">
                    <div
                      style={{
                        backgroundColor: trayColor.color,
                        borderColor: trayColor.darkColor,
                      }}
                      className="w-11 h-11 rounded-xl border-2 flex items-center justify-center font-bold text-sm"
                    >
                      <span style={{ color: isLightSwatch ? '#243B80' : '#FFFFFF' }}>
                        {trayColor.patternSymbol}
                      </span>
                    </div>
                    <span className="font-extrabold text-base text-[#243B80]">
                      {demoActive && isTarget
                        ? `👉 ${trayColor.nameBn} ট্রে`
                        : `${trayColor.nameBn} ট্রে`}
                    </span>
                  </div>

                  {countInTray > 0 ? (
                    <span className="bg-[#43A047] text-white font-bold text-xs px-3 py-1.5 rounded-xl">
                      {countInTray > 1 ? `✓ ${countInTray} টি কাগজ` : '✓ কাগজ রাখা হয়েছে'}
                    </span>
                  ) : (
                    <span className="bg-white/85 text-[#243B80] font-bold text-xs px-3 py-1 rounded-lg">
                      এখানে রাখো
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {showPauseDialog && (
        <GamePauseDialog
          levelNumber={level}
          onResume={() => setShowPauseDialog(false)}
          onRestart={restartCurrentLevel}
          onOpenLevelMap={() => {
            setShowPauseDialog(false);
            onOpenLevelMap();
          }}
          onGoHome={() => {
            setShowPauseDialog(false);
            onBack();
          }}
        />
      )}

      {showSuccessDialog && (
        <GameSuccessDialog
          levelNumber={level}
          starsAwarded={errorCount === 0 ? 3 : 2}
          onDismiss={() => {
            setShowSuccessDialog(false);
            restartCurrentLevel();
          }}
          onNextLevel={() => {
            setShowSuccessDialog(false);
            if (level < maxLevel) setLevel(l => l + 1);
            setTrayPaperCounts({});
          }}
          onGoPhysicalPractice={() => {
            setShowSuccessDialog(false);
            audio.playEffect('PHYSICAL_TRANSITION');
            onGoPhysicalPractice();
          }}
        />
      )}
    </PlayoraAnimatedSceneBackground>
  );
};
