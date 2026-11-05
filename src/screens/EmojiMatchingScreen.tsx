import React, { useEffect, useMemo, useState } from 'react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  LearningMode,
  PictureCardItem,
} from '../types/game';
import {
  ANIMALS,
  CORE_PICTURE_ITEMS,
  DAILY_OBJECTS,
  EXTRA_EMOJIS,
  FRUITS,
  getLevelsForGame,
  NATURE,
  VEHICLES,
} from '../data/curriculum';
import {
  GameplayControlBar,
  GamePauseDialog,
  GameSuccessDialog,
  GameTopBar,
  LearningModeSwitcherBar,
  MascotSpeechPrompt,
  PlayoraAnimatedSceneBackground,
  PlayoraIllustratedPictureBadge,
} from '../components/MascotAndHeader';

function shuffleArray<T>(arr: T[]): T[] {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

export const EmojiMatchingScreen: React.FC<{
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

  const levelItems: PictureCardItem[] = useMemo(() => {
    if (learningMode === 'core') {
      const pairCount =
        level === 1 ? 2 : level === 2 ? 3 : level === 3 ? 4 : level === 4 ? 6 : 9;
      return CORE_PICTURE_ITEMS.slice(0, pairCount);
    } else {
      switch (level) {
        case 1:
        case 2:
          return CORE_PICTURE_ITEMS.slice(0, 2);
        case 3:
        case 4:
          return CORE_PICTURE_ITEMS.slice(0, 3);
        case 5:
          return CORE_PICTURE_ITEMS.slice(0, 4);
        case 6:
          return CORE_PICTURE_ITEMS.slice(0, 5);
        case 7:
        case 8:
          return CORE_PICTURE_ITEMS.slice(0, 6);
        case 9:
          return CORE_PICTURE_ITEMS.slice(0, 7);
        case 10:
          return CORE_PICTURE_ITEMS.slice(0, 8);
        case 11:
          return CORE_PICTURE_ITEMS.slice(0, 9);
        case 12: {
          const extras: PictureCardItem[] = EXTRA_EMOJIS.map((p, idx) => ({
            id: `ext_${idx}`,
            symbol: p[0],
            nameBn: p[1],
            category: 'OPTIONAL_EMOJI',
            badgeColor: '#FFF9E6',
          }));
          return [...CORE_PICTURE_ITEMS, ...extras];
        }
        case 13:
          return FRUITS.slice(0, 4);
        case 14:
          return FRUITS.slice(0, 6);
        case 15:
          return FRUITS.slice(0, 8);
        case 16:
          return ANIMALS.slice(0, 5);
        case 17:
          return ANIMALS.slice(0, 7);
        case 18:
          return NATURE.slice(0, 5);
        case 19:
          return NATURE.slice(0, 7);
        case 20:
          return VEHICLES.slice(0, 6);
        case 21:
          return DAILY_OBJECTS.slice(0, 5);
        case 22:
          return DAILY_OBJECTS.slice(0, 8);
        case 23:
          return [...FRUITS.slice(0, 3), ...ANIMALS.slice(0, 3)];
        case 24:
          return [...NATURE.slice(0, 3), ...VEHICLES.slice(0, 2), ...DAILY_OBJECTS.slice(0, 2)];
        default:
          return [
            ...CORE_PICTURE_ITEMS.slice(0, 3),
            ...FRUITS.slice(0, 2),
            ...ANIMALS.slice(0, 2),
            ...DAILY_OBJECTS.slice(0, 2),
          ];
      }
    }
  }, [learningMode, level]);

  const [remainingItems, setRemainingItems] = useState<PictureCardItem[]>(() =>
    shuffleArray(levelItems)
  );
  const [matchedItems, setMatchedItems] = useState<PictureCardItem[]>([]);
  const [errorCount, setErrorCount] = useState(0);
  const [hintActive, setHintActive] = useState(false);
  const [demoActive, setDemoActive] = useState(false);
  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [speechText, setSpeechText] = useState('একই ছবিটি খুঁজে বের করো।');

  useEffect(() => {
    setRemainingItems(shuffleArray(levelItems));
    setMatchedItems([]);
    setErrorCount(0);
    setHintActive(false);
    setDemoActive(false);
    setShowSuccessDialog(false);
  }, [levelItems, restartTrigger]);

  const currentTargetItem = remainingItems[0] || null;

  const candidateOptions = useMemo(
    () => shuffleArray(levelItems),
    [levelItems, currentTargetItem, restartTrigger]
  );

  useEffect(() => {
    if (currentTargetItem) {
      const prompt = `একই ছবিটি খুঁজে বের করো: ${currentTargetItem.nameBn}।`;
      setSpeechText(prompt);
      audio.speak(
        'একই ছবিটি খুঁজে বের করো।',
        `emoji_${learningMode}_L${level}_R${restartTrigger}_${currentTargetItem.id}`,
        false,
        true
      );
    } else if (matchedItems.length === levelItems.length && levelItems.length > 0) {
      audio.playLevelCompleteFanfare();
      audio.speak(
        'এই ধাপটি সম্পন্ন হয়েছে।',
        `emoji_complete_${learningMode}_L${level}_R${restartTrigger}`,
        false,
        true
      );
      onRecordAttempt(true, errorCount === 0, level < maxLevel, level);
      setShowSuccessDialog(true);
    }
  }, [currentTargetItem, matchedItems.length, levelItems.length]);

  const restartCurrentLevel = () => {
    audio.stopAndClearQueue();
    audio.playButtonTap();
    setShowSuccessDialog(false);
    setShowPauseDialog(false);
    setRestartTrigger(prev => prev + 1);
  };

  const levelDef = useMemo(
    () => getLevelsForGame('emoji_match')[level - 1],
    [level]
  );

  const hasNextUnlocked =
    level < maxLevel &&
    (level + 1 <= maxUnlocked || activeCompletedSet.has(level + 1) || activeCompletedSet.has(level));

  return (
    <PlayoraAnimatedSceneBackground
      theme="EMOJI_DISCOVERY_WORLD"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top bar */}
        <div className="bg-white/90 shadow-xs pb-1">
          <GameTopBar
            title={`${GAME_TYPES.emoji_match.titleEn} • ${GAME_TYPES.emoji_match.titleBn}`}
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

        {/* Main Gameplay Area */}
        <div className="flex-1 flex flex-col items-center px-4 py-2 space-y-2.5 overflow-y-auto">
          <MascotSpeechPrompt
            text={speechText}
            onReplayAudio={() => audio.replayInstruction('একই ছবিটি খুঁজে বের করো।')}
            isHighlighted={hintActive || demoActive}
          />

          {/* Upper Matching Table Area */}
          <div className="w-full bg-white/95 rounded-3xl p-3.5 shadow-md border-2 border-white">
            <div className="flex items-center justify-between">
              <span className="font-bold text-xs text-[#3978D5]">
                {levelDef?.titleBn || `লেভেল ${level}`}
              </span>
              <span className="bg-[#EAF4FF] text-[#243B80] font-extrabold text-xs px-3 py-1 rounded-xl">
                মিলেছে: {matchedItems.length}/{levelItems.length}
              </span>
            </div>

            <div className="flex items-center justify-center gap-4 mt-2.5">
              {/* Target Card */}
              <div
                style={{ backgroundColor: currentTargetItem?.badgeColor || '#FFF9E6' }}
                className="w-24 h-24 rounded-2xl border-2 border-[#3978D5] shadow flex flex-col items-center justify-center p-1"
              >
                <PlayoraIllustratedPictureBadge
                  symbol={currentTargetItem?.symbol || '✨'}
                  badgeColor={currentTargetItem?.badgeColor || '#FFF9E6'}
                  sizePx={52}
                />
                <span className="mt-1 font-bold text-xs text-[#243B80] truncate max-w-full">
                  {currentTargetItem?.nameBn || ''}
                </span>
              </div>

              <span className="text-3xl font-extrabold text-[#3978D5]">+</span>

              {/* Partner Placement Slot */}
              <div
                className={`w-24 h-24 rounded-2xl border-2 flex flex-col items-center justify-center p-1 ${
                  demoActive
                    ? 'bg-[#E8F7F1] border-[#FFD166]'
                    : 'bg-slate-100 border-slate-400'
                }`}
              >
                {demoActive && currentTargetItem ? (
                  <>
                    <PlayoraIllustratedPictureBadge
                      symbol={currentTargetItem.symbol}
                      badgeColor={currentTargetItem.badgeColor}
                      sizePx={48}
                    />
                    <span className="mt-0.5 font-bold text-[10px] text-[#2E7D32]">এখানে বসবে</span>
                  </>
                ) : (
                  <span className="text-3xl font-extrabold text-slate-400">?</span>
                )}
              </div>
            </div>
          </div>

          {/* Instruction Sub-bar */}
          <div className="w-full bg-white/85 rounded-2xl px-4 py-1.5 flex items-center justify-between">
            <span className="font-bold text-xs text-[#243B80]">একই ছবিটিতে ট্যাপ করো:</span>
            <span className="font-bold text-xs text-[#3978D5]">
              {currentTargetItem?.nameBn || ''}
            </span>
          </div>

          {/* Playable Board Cards Grid */}
          <div
            className={`w-full grid ${
              candidateOptions.length > 4 ? 'grid-cols-3' : 'grid-cols-2'
            } gap-2.5 pb-4`}
          >
            {candidateOptions.map(cardItem => {
              const isTarget = cardItem.symbol === currentTargetItem?.symbol;
              const shouldHighlight = (hintActive || demoActive) && isTarget;

              return (
                <button
                  key={cardItem.id}
                  type="button"
                  data-testid={`emoji_card_${cardItem.symbol}`}
                  onClick={() => {
                    audio.playEffect('CARD_SELECT');
                    if (isTarget && currentTargetItem) {
                      audio.playSuccessChime();
                      audio.speakPraise();
                      setSpeechText('খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।');
                      setMatchedItems(prev => [...prev, cardItem]);
                      setRemainingItems(prev => prev.slice(1));
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
                        setSpeechText(`সঠিক কার্ডটি বেছে নাও: ${currentTargetItem?.nameBn}।`);
                        audio.speak('সঠিক কার্ডটি বেছে নাও।');
                        setHintActive(true);
                        setDemoActive(true);
                      }
                    }
                  }}
                  className={`h-26 rounded-3xl p-2 flex flex-col items-center justify-center shadow transition-all cursor-pointer active:scale-95 ${
                    shouldHighlight
                      ? 'bg-[#FFF9E6] border-3 border-[#F59E0B] shadow-lg'
                      : 'bg-white/95 border-2 border-[#FFD166]'
                  }`}
                >
                  <PlayoraIllustratedPictureBadge
                    symbol={cardItem.symbol}
                    badgeColor={cardItem.badgeColor}
                    sizePx={54}
                  />
                  <span className="mt-1 font-bold text-xs text-[#243B80] truncate max-w-full">
                    {cardItem.nameBn}
                  </span>
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
            setMatchedItems([]);
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
