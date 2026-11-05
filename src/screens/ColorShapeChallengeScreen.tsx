import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  ColorShapeCard,
  GAME_TYPES,
  GameProgressData,
  KidColor,
  KidShape,
  LearningMode,
} from '../types/game';
import {
  ALL_10_COLORS,
  ALL_20_SHAPES,
  CORE_COLORS,
  CORE_SHAPES,
  EXTENDED_ONLY_COLORS,
  KID_SHAPES,
} from '../data/colors';
import {
  ALL_COLOR_SHAPE_CARDS,
  EXTENDED_200_COLOR_SHAPE_CARDS,
  getLevelsForGame,
} from '../data/curriculum';
import {
  GameplayControlBar,
  GamePauseDialog,
  GameSuccessDialog,
  GameTopBar,
  LearningModeSwitcherBar,
  MascotSpeechPrompt,
  PlayoraAnimatedSceneBackground,
} from '../components/MascotAndHeader';
import { ShapeGraphic } from '../components/ShapeDrawables';

function shuffleArray<T>(arr: T[]): T[] {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

export const ColorShapeChallengeScreen: React.FC<{
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
  onRecordSkillMastery: (colorId?: string, shapeId?: string) => void;
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

  const getDefaultPhase = useCallback((mode: LearningMode, lvl: number): 1 | 2 | 3 => {
    if (mode === 'extended') {
      if ((lvl >= 1 && lvl <= 4) || lvl === 13 || lvl === 14) return 1;
      if ((lvl >= 5 && lvl <= 8) || (lvl >= 15 && lvl <= 17)) return 2;
      return 3;
    }
    return 1;
  }, []);

  const [currentPhase, setCurrentPhase] = useState<1 | 2 | 3>(() =>
    getDefaultPhase(learningMode, level)
  );

  useEffect(() => {
    setCurrentPhase(getDefaultPhase(learningMode, level));
  }, [learningMode, level, getDefaultPhase]);

  const [targetColor, setTargetColor] = useState<KidColor>(CORE_COLORS[0]);
  const [targetShape, setTargetShape] = useState<KidShape>(CORE_SHAPES[3]);
  const [candidateCards, setCandidateCards] = useState<ColorShapeCard[]>([]);
  const [solvedCountInPhase, setSolvedCountInPhase] = useState(0);
  const requiredSolves = 5;

  const [errorCount, setErrorCount] = useState(0);
  const [hintActive, setHintActive] = useState(false);
  const [demoActive, setDemoActive] = useState(false);
  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [speechText, setSpeechText] = useState('');

  const setupNewRound = useCallback(
    (solveIdx: number) => {
      const useExtendedColors = learningMode === 'extended' && level >= 13;
      const useExtendedShapes = learningMode === 'extended' && level >= 15;
      const useAll20Shapes = learningMode === 'extended' && level >= 17;

      const availableColors =
        learningMode === 'extended' && (level === 13 || level === 14)
          ? EXTENDED_ONLY_COLORS
          : useExtendedColors
          ? ALL_10_COLORS
          : CORE_COLORS;

      const combo10Shapes = KID_SHAPES.slice(0, 10);
      const availableShapes =
        learningMode === 'extended' && (level === 15 || level === 16)
          ? combo10Shapes
          : useAll20Shapes
          ? ALL_20_SHAPES
          : useExtendedShapes
          ? combo10Shapes
          : CORE_SHAPES;

      const activeLibrary =
        useExtendedColors || useExtendedShapes || useAll20Shapes
          ? EXTENDED_200_COLOR_SHAPE_CARDS.filter(
              c =>
                availableColors.some(ac => ac.id === c.color.id) &&
                availableShapes.some(as => as.id === c.shape.id)
            )
          : ALL_COLOR_SHAPE_CARDS;

      const randColor = availableColors[Math.floor(Math.random() * availableColors.length)];
      const randShape = availableShapes[Math.floor(Math.random() * availableShapes.length)];
      setTargetColor(randColor);
      setTargetShape(randShape);
      setErrorCount(0);
      setHintActive(false);
      setDemoActive(false);

      const targetCard =
        activeLibrary.find(c => c.color.id === randColor.id && c.shape.id === randShape.id) ||
        EXTENDED_200_COLOR_SHAPE_CARDS[0];

      const distractorCount = level <= 2 ? 2 : level <= 6 ? 3 : 5;

      let distractors: ColorShapeCard[] = [];
      if (currentPhase === 1) {
        const seenColors = new Set<string>([randColor.id]);
        for (const item of shuffleArray(activeLibrary)) {
          if (!seenColors.has(item.color.id)) {
            seenColors.add(item.color.id);
            distractors.push(item);
            if (distractors.length >= distractorCount) break;
          }
        }
      } else if (currentPhase === 2) {
        const seenShapes = new Set<string>([randShape.id]);
        for (const item of shuffleArray(activeLibrary)) {
          if (!seenShapes.has(item.shape.id)) {
            seenShapes.add(item.shape.id);
            distractors.push(item);
            if (distractors.length >= distractorCount) break;
          }
        }
      } else {
        const sameColorWrongShape = shuffleArray(
          activeLibrary.filter(c => c.color.id === randColor.id && c.shape.id !== randShape.id)
        ).slice(0, 1);
        const wrongColorSameShape = shuffleArray(
          activeLibrary.filter(c => c.color.id !== randColor.id && c.shape.id === randShape.id)
        ).slice(0, 1);
        const others = shuffleArray(
          activeLibrary.filter(
            c =>
              c.id !== targetCard.id &&
              !sameColorWrongShape.some(x => x.id === c.id) &&
              !wrongColorSameShape.some(x => x.id === c.id)
          )
        ).slice(
          0,
          Math.max(1, distractorCount - sameColorWrongShape.length - wrongColorSameShape.length)
        );
        distractors = [...sameColorWrongShape, ...wrongColorSameShape, ...others].slice(
          0,
          distractorCount
        );
      }

      setCandidateCards(shuffleArray([targetCard, ...distractors]));

      if (currentPhase === 1) {
        const prompt = `${randColor.nameBn} রংটি খুঁজে বের করো।`;
        setSpeechText(prompt);
        audio.speak(
          prompt,
          `cs_p1_${learningMode}_L${level}_S${solveIdx}_${randColor.id}`,
          false,
          true
        );
      } else if (currentPhase === 2) {
        const prompt = `${randShape.nameBn}টি খুঁজে দাও।`;
        setSpeechText(prompt);
        audio.speak(
          prompt,
          `cs_p2_${learningMode}_L${level}_S${solveIdx}_${randShape.id}`,
          false,
          true
        );
      } else {
        const prompt = `${randColor.nameBn} ${randShape.nameBn}টি নির্বাচন করো।`;
        setSpeechText(prompt);
        audio.speak(
          prompt,
          `cs_p3_${learningMode}_L${level}_S${solveIdx}_${randColor.id}_${randShape.id}`,
          false,
          true
        );
      }
    },
    [audio, currentPhase, learningMode, level]
  );

  useEffect(() => {
    setSolvedCountInPhase(0);
    setupNewRound(0);
  }, [learningMode, level, currentPhase, restartTrigger, setupNewRound]);

  const restartCurrentLevel = () => {
    audio.stopAndClearQueue();
    audio.playButtonTap();
    setShowSuccessDialog(false);
    setShowPauseDialog(false);
    setSolvedCountInPhase(0);
    setRestartTrigger(prev => prev + 1);
  };

  const levelDef = useMemo(
    () => getLevelsForGame('color_shape_challenge')[level - 1],
    [level]
  );

  const hasNextUnlocked =
    level < maxLevel &&
    (level + 1 <= maxUnlocked || activeCompletedSet.has(level + 1) || activeCompletedSet.has(level));

  return (
    <PlayoraAnimatedSceneBackground
      theme="RAINBOW_DISCOVERY_WORLD"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top bar */}
        <div className="bg-white/90 shadow-xs pb-1">
          <GameTopBar
            title={`${GAME_TYPES.color_shape_challenge.titleEn} • ${GAME_TYPES.color_shape_challenge.titleBn}`}
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
          {/* 3 Pedagogical Stages Selector */}
          <div className="w-full grid grid-cols-3 gap-1.5">
            {(
              [
                [1, '১. শুধু রং'],
                [2, '২. শুধু আকৃতি'],
                [3, '৩. রং + আকৃতি'],
              ] as [1 | 2 | 3, string][]
            ).map(([phaseNum, label]) => {
              const isSelected = currentPhase === phaseNum;
              return (
                <button
                  key={phaseNum}
                  type="button"
                  onClick={() => {
                    if (currentPhase !== phaseNum) {
                      audio.playButtonTap();
                      setCurrentPhase(phaseNum);
                      setSolvedCountInPhase(0);
                    }
                  }}
                  className={`h-9 rounded-xl font-bold text-xs border transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#9B5DE5] text-white border-white shadow'
                      : 'bg-white/90 text-[#243B80] border-slate-200'
                  }`}
                >
                  {label}
                </button>
              );
            })}
          </div>

          <MascotSpeechPrompt
            text={speechText}
            onReplayAudio={() => audio.replayInstruction(speechText)}
            isHighlighted={hintActive || demoActive}
          />

          {/* Mission / Visual Clue Board */}
          <div className="w-full bg-white/95 rounded-3xl p-3.5 shadow-md border-2 border-white">
            <div className="flex items-center justify-between">
              <span className="font-bold text-xs text-[#9B5DE5]">
                {levelDef?.titleBn || `লেভেল ${level}`}
              </span>
              <span className="bg-[#F3E8FF] text-[#243B80] font-extrabold text-xs px-3 py-1 rounded-xl">
                অগ্রগতি: {solvedCountInPhase}/{requiredSolves}
              </span>
            </div>

            <div className="flex items-center justify-center gap-3 mt-2.5">
              {currentPhase === 1 && (
                <>
                  <div
                    style={{
                      backgroundColor: targetColor.color,
                      borderColor: targetColor.darkColor,
                    }}
                    className="w-16 h-16 rounded-2xl border-2 flex items-center justify-center font-extrabold text-xl"
                  >
                    <span
                      style={{
                        color:
                          targetColor.id === 'white' || targetColor.id === 'yellow'
                            ? '#243B80'
                            : '#FFFFFF',
                      }}
                    >
                      {targetColor.patternSymbol}
                    </span>
                  </div>
                  <div>
                    <div className="text-xs text-slate-500 font-semibold">১ম ধাপ: শুধু রং চেনা</div>
                    <div className="text-xl font-extrabold text-[#243B80]">
                      {targetColor.nameBn} রং
                    </div>
                  </div>
                </>
              )}

              {currentPhase === 2 && (
                <>
                  <div className="w-16 h-16 rounded-2xl bg-[#EAF4FF] border-2 border-[#3978D5] flex items-center justify-center">
                    <ShapeGraphic shape={targetShape} color="#3978D5" sizePx={46} />
                  </div>
                  <div>
                    <div className="text-xs text-slate-500 font-semibold">
                      ২য় ধাপ: শুধু আকৃতি চেনা
                    </div>
                    <div className="text-xl font-extrabold text-[#243B80]">
                      {targetShape.nameBn}
                    </div>
                  </div>
                </>
              )}

              {currentPhase === 3 && (
                <div className="flex items-center gap-2">
                  <div
                    style={{
                      backgroundColor: targetColor.color,
                      borderColor: targetColor.darkColor,
                    }}
                    className="w-12 h-12 rounded-xl border-2 flex items-center justify-center font-bold text-sm"
                  >
                    <span
                      style={{
                        color:
                          targetColor.id === 'white' || targetColor.id === 'yellow'
                            ? '#243B80'
                            : '#FFFFFF',
                      }}
                    >
                      {targetColor.patternSymbol}
                    </span>
                  </div>
                  <span className="font-extrabold text-xl text-[#243B80]">+</span>
                  <div className="w-12 h-12 rounded-xl bg-[#EAF4FF] border-2 border-[#3978D5] flex items-center justify-center">
                    <ShapeGraphic
                      shape={targetShape}
                      color="#243B80"
                      sizePx={32}
                      isOutlineOnly={true}
                      outlineColor="#243B80"
                    />
                  </div>
                  <span className="font-extrabold text-xl text-[#243B80]">=</span>
                  <div
                    style={{
                      backgroundColor: targetColor.lightColor,
                      borderColor: targetColor.darkColor,
                    }}
                    className="w-15 h-15 rounded-2xl border-2 flex items-center justify-center"
                  >
                    <ShapeGraphic shape={targetShape} color={targetColor.color} sizePx={42} />
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Candidate Cards Grid */}
          <div
            className={`w-full grid ${
              candidateCards.length > 4 ? 'grid-cols-3' : 'grid-cols-2'
            } gap-2.5 pb-4`}
          >
            {candidateCards.map(card => {
              const isCorrect =
                currentPhase === 1
                  ? card.color.id === targetColor.id
                  : currentPhase === 2
                  ? card.shape.id === targetShape.id
                  : card.color.id === targetColor.id && card.shape.id === targetShape.id;

              const shouldHighlight = (hintActive || demoActive) && isCorrect;

              return (
                <button
                  key={card.id}
                  type="button"
                  data-testid={`color_shape_card_${card.id}`}
                  onClick={() => {
                    audio.playEffect('CARD_SELECT');

                    if (isCorrect) {
                      audio.playSuccessChime();
                      audio.speakPraise();
                      if (errorCount === 0) {
                        if (currentPhase === 1) onRecordSkillMastery(targetColor.id, undefined);
                        else if (currentPhase === 2)
                          onRecordSkillMastery(undefined, targetShape.id);
                        else onRecordSkillMastery(targetColor.id, targetShape.id);
                      }

                      const nextSolves = solvedCountInPhase + 1;
                      setSolvedCountInPhase(nextSolves);

                      if (nextSolves >= requiredSolves) {
                        audio.playLevelCompleteFanfare();
                        audio.speak('এই ধাপটি সম্পন্ন হয়েছে।');
                        onRecordAttempt(true, errorCount === 0, level < maxLevel, level);
                        setShowSuccessDialog(true);
                      } else {
                        setupNewRound(nextSolves);
                      }
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
                        setSpeechText('সঠিক কার্ডটি বেছে নাও।');
                        audio.speak('সঠিক কার্ডটি বেছে নাও।');
                        setHintActive(true);
                        setDemoActive(true);
                      }
                    }
                  }}
                  className={`h-28 rounded-3xl p-2 flex flex-col items-center justify-center shadow transition-all cursor-pointer active:scale-95 ${
                    shouldHighlight
                      ? 'bg-[#FFF9E6] border-3 border-[#F59E0B] shadow-lg'
                      : 'bg-white/95 border-2 border-white'
                  }`}
                >
                  <ShapeGraphic shape={card.shape} color={card.color.color} sizePx={52} />
                  <span className="mt-1.5 font-bold text-xs text-[#243B80] truncate max-w-full">
                    {demoActive && isCorrect ? `👉 ${card.nameBn}` : card.nameBn}
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
            if (learningMode === 'core' && currentPhase < 3) {
              setCurrentPhase(p => (p + 1) as 1 | 2 | 3);
            } else if (level < maxLevel) {
              setLevel(l => l + 1);
            }
            setSolvedCountInPhase(0);
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
