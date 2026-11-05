import React, { useEffect, useMemo, useState } from 'react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  Kid3DShape,
  KidShape,
  LearningMode,
} from '../types/game';
import { ALL_25_SHAPES, CORE_SHAPES, KID_3D_SHAPES, KID_SHAPES } from '../data/colors';
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
import { Shape3DGraphic, ShapeGraphic } from '../components/ShapeDrawables';

function shuffleArray<T>(arr: T[]): T[] {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

const SHAPE_COLORS: Record<string, string> = {
  circle: '#E53935',
  triangle: '#F57F17',
  square: '#1E88E5',
  star: '#43A047',
  heart: '#FB8C00',
  rectangle: '#8E24AA',
  oval: '#E91E63',
  diamond: '#3978D5',
  semicircle: '#56BFA4',
  crescent: '#F59E0B',
  pentagon: '#FF7777',
  hexagon: '#9B5DE5',
  octagon: '#C62828',
  trapezoid: '#2E7D32',
  parallelogram: '#795548',
  cross: '#3978D5',
  arrow: '#E65100',
  kite: '#6A1B9A',
  heptagon: '#43A047',
  decagon: '#FF7777',
  right_triangle: '#3978D5',
  ring: '#F59E0B',
  four_point_star: '#9B5DE5',
  cloud_shape: '#38BDF8',
  shield: '#16A34A',
};

export const ShapeMatchingScreen: React.FC<{
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
  onRecordSkillMastery: (shapeId: string) => void;
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
  const [activeTab, setActiveTab] = useState<0 | 1>(0);

  const byId = (id: string) => KID_SHAPES.find(s => s.id === id) || CORE_SHAPES[0];

  const levelShapes: KidShape[] = useMemo(() => {
    if (learningMode === 'core') {
      if (level === 1) return [byId('circle'), byId('triangle')];
      if (level === 2) return [byId('circle'), byId('triangle'), byId('square')];
      if (level === 4) return shuffleArray(CORE_SHAPES);
      return CORE_SHAPES;
    } else {
      switch (level) {
        case 1:
          return [byId('circle'), byId('triangle')];
        case 2:
          return [byId('square'), byId('star')];
        case 3:
          return [byId('circle'), byId('triangle'), byId('square')];
        case 4:
          return [byId('triangle'), byId('star'), byId('heart')];
        case 5:
          return [byId('circle'), byId('triangle'), byId('square'), byId('star')];
        case 6:
          return CORE_SHAPES;
        case 7:
        case 8:
          return shuffleArray(CORE_SHAPES);
        case 9:
          return [byId('rectangle'), byId('oval'), byId('circle'), byId('square')];
        case 10:
          return [byId('diamond'), byId('semicircle'), byId('triangle'), byId('circle')];
        case 11:
          return [byId('crescent'), byId('star'), byId('heart'), byId('oval'), byId('rectangle')];
        case 12:
          return [
            byId('circle'),
            byId('rectangle'),
            byId('oval'),
            byId('diamond'),
            byId('semicircle'),
            byId('crescent'),
          ];
        case 13:
          return [byId('pentagon'), byId('hexagon'), byId('square'), byId('triangle')];
        case 14:
          return [byId('octagon'), byId('trapezoid'), byId('pentagon'), byId('hexagon')];
        case 15:
          return [
            byId('parallelogram'),
            byId('trapezoid'),
            byId('rectangle'),
            byId('diamond'),
            byId('square'),
          ];
        case 16:
          return [
            byId('pentagon'),
            byId('hexagon'),
            byId('octagon'),
            byId('trapezoid'),
            byId('parallelogram'),
          ];
        case 17:
          return [byId('cross'), byId('arrow'), byId('kite'), byId('circle')];
        case 18:
          return [byId('heptagon'), byId('decagon'), byId('pentagon'), byId('hexagon')];
        case 19:
          return [
            byId('right_triangle'),
            byId('ring'),
            byId('four_point_star'),
            byId('cloud_shape'),
            byId('shield'),
            byId('star'),
          ];
        case 20:
          return CORE_SHAPES;
        case 21:
          return [
            byId('cloud_shape'),
            byId('shield'),
            byId('four_point_star'),
            byId('ring'),
            byId('star'),
            byId('heart'),
          ];
        case 22:
          return [
            byId('circle'),
            byId('square'),
            byId('star'),
            byId('hexagon'),
            byId('cross'),
            byId('kite'),
          ];
        case 23:
          return [
            byId('triangle'),
            byId('right_triangle'),
            byId('four_point_star'),
            byId('diamond'),
            byId('arrow'),
            byId('pentagon'),
          ];
        case 24:
          return [
            byId('circle'),
            byId('oval'),
            byId('ring'),
            byId('hexagon'),
            byId('heptagon'),
            byId('octagon'),
          ];
        default:
          return shuffleArray(ALL_25_SHAPES).slice(0, 6);
      }
    }
  }, [learningMode, level]);

  const isSilhouetteMode = learningMode === 'extended' && level === 20;
  const isSizeVariationMode = learningMode === 'extended' && level === 22;
  const isRotatedMode = learningMode === 'extended' && level === 23;

  const [remainingShapes, setRemainingShapes] = useState<KidShape[]>(() =>
    shuffleArray(levelShapes)
  );
  const [placedShapeIds, setPlacedShapeIds] = useState<string[]>([]);
  const [errorCount, setErrorCount] = useState(0);
  const [hintActive, setHintActive] = useState(false);
  const [demoActive, setDemoActive] = useState(false);
  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [speechText, setSpeechText] = useState('ত্রিভুজটি খুঁজে দাও।');

  // 3D Shape Discovery State
  const [selected3DShape, setSelected3DShape] = useState<Kid3DShape>(KID_3D_SHAPES[0]);
  const [quiz3DTarget, setQuiz3DTarget] = useState<Kid3DShape>(KID_3D_SHAPES[0]);
  const [quiz3DMatches, setQuiz3DMatches] = useState(0);

  useEffect(() => {
    setRemainingShapes(shuffleArray(levelShapes));
    setPlacedShapeIds([]);
    setErrorCount(0);
    setHintActive(false);
    setDemoActive(false);
    setShowSuccessDialog(false);
  }, [levelShapes, restartTrigger]);

  const currentTargetShape = remainingShapes[0] || null;

  useEffect(() => {
    if (activeTab === 1) {
      const prompt = `ত্রিমাত্রিক আকৃতি: ${selected3DShape.nameBn}। উদাহরণ: ${selected3DShape.realWorldExampleBn}।`;
      setSpeechText(prompt);
      audio.speak(prompt, `shape3d_${selected3DShape.id}`, false, true);
    } else if (currentTargetShape) {
      const prompt = `${currentTargetShape.nameBn}টি খুঁজে দাও।`;
      setSpeechText(prompt);
      audio.speak(
        prompt,
        `shape2d_${learningMode}_L${level}_R${restartTrigger}_${currentTargetShape.id}_${placedShapeIds.length}`,
        false,
        true
      );
    } else if (placedShapeIds.length === levelShapes.length && levelShapes.length > 0) {
      audio.playLevelCompleteFanfare();
      audio.speak(
        'এই ধাপটি সম্পন্ন হয়েছে।',
        `shape_complete_${learningMode}_L${level}_R${restartTrigger}`,
        false,
        true
      );
      onRecordAttempt(true, errorCount === 0, level < maxLevel, level);
      setShowSuccessDialog(true);
    }
  }, [activeTab, selected3DShape, currentTargetShape, placedShapeIds.length]);

  const restartCurrentLevel = () => {
    audio.stopAndClearQueue();
    audio.playButtonTap();
    setShowSuccessDialog(false);
    setShowPauseDialog(false);
    setRestartTrigger(prev => prev + 1);
  };

  const levelDef = useMemo(
    () => getLevelsForGame('shape_match')[level - 1],
    [level]
  );

  const hasNextUnlocked =
    level < maxLevel &&
    (level + 1 <= maxUnlocked || activeCompletedSet.has(level + 1) || activeCompletedSet.has(level));

  return (
    <PlayoraAnimatedSceneBackground
      theme="MAGICAL_SHAPE_GARDEN"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top bar */}
        <div className="bg-white/90 shadow-xs pb-1.5">
          <GameTopBar
            title={`${GAME_TYPES.shape_match.titleEn} • ${GAME_TYPES.shape_match.titleBn}`}
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
              if (mode === 'core') setActiveTab(0);
              onSelectMode(mode);
            }}
          />

          {/* 2D vs 3D Shape Discovery Tab Switcher */}
          <div className="px-4 pt-1 grid grid-cols-2 gap-2">
            <button
              type="button"
              data-testid="tab_2d_shapes"
              onClick={() => {
                audio.playButtonTap();
                setActiveTab(0);
              }}
              className={`h-9 rounded-xl font-bold text-xs border transition-all cursor-pointer ${
                activeTab === 0
                  ? 'bg-[#56BFA4] text-white border-white shadow'
                  : 'bg-white text-[#243B80] border-slate-200'
              }`}
            >
              📐 ২ডি আকৃতি (২৫টি)
            </button>

            <button
              type="button"
              data-testid="tab_3d_shapes"
              onClick={() => {
                audio.playButtonTap();
                setActiveTab(1);
              }}
              className={`h-9 rounded-xl font-bold text-xs border transition-all cursor-pointer ${
                activeTab === 1
                  ? 'bg-[#9B5DE5] text-white border-white shadow'
                  : 'bg-white text-[#243B80] border-slate-200'
              }`}
            >
              🧊 ৩ডি ত্রিমাত্রিক আকৃতি (৮টি)
            </button>
          </div>
        </div>

        {activeTab === 1 ? (
          /* 3D Shape Discovery Section */
          <div className="flex-1 flex flex-col items-center px-4 py-2 space-y-2.5 overflow-y-auto">
            <MascotSpeechPrompt
              text={speechText}
              onReplayAudio={() => audio.replayInstruction(speechText)}
            />

            <div className="w-full bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white flex items-center gap-4">
              <div className="w-22 h-22 rounded-2xl bg-[#F3E8FF] border-2 border-[#9B5DE5] flex items-center justify-center shrink-0">
                <Shape3DGraphic shape3D={selected3DShape} sizePx={64} />
              </div>
              <div className="flex-1">
                <div className="text-[11px] font-bold text-[#9B5DE5]">
                  3D Shape Discovery • ত্রিমাত্রিক আকৃতি
                </div>
                <div className="text-xl font-extrabold text-[#243B80]">
                  {selected3DShape.nameBn}
                </div>
                <div className="text-xs font-bold text-[#2E7D32] mt-0.5">
                  বাস্তব উদাহরণ: {selected3DShape.realWorldExampleBn}
                </div>
                <div className="text-xs font-extrabold text-[#3978D5] mt-1">
                  চলো খুঁজে বের করি: {quiz3DTarget.nameBn} কোথায়? (সফল: {quiz3DMatches})
                </div>
              </div>
            </div>

            <div className="w-full grid grid-cols-2 gap-2.5 pb-4">
              {KID_3D_SHAPES.map(shape3d => {
                const isSelected = shape3d.id === selected3DShape.id;
                const isQuizTarget = shape3d.id === quiz3DTarget.id;

                return (
                  <button
                    key={shape3d.id}
                    type="button"
                    data-testid={`shape_3d_card_${shape3d.id}`}
                    onClick={() => {
                      setSelected3DShape(shape3d);
                      if (isQuizTarget) {
                        audio.playSuccessChime();
                        setQuiz3DMatches(m => m + 1);
                        const msg = `সাবাশ! এটি হলো ${shape3d.nameBn} (${shape3d.realWorldExampleBn})!`;
                        setSpeechText(msg);
                        audio.speak(msg);
                        const others = KID_3D_SHAPES.filter(s => s.id !== shape3d.id);
                        setQuiz3DTarget(others[Math.floor(Math.random() * others.length)]);
                      } else {
                        audio.playEffect('CARD_SELECT');
                        const msg = `এটি ${shape3d.nameBn}। যেমন: ${shape3d.realWorldExampleBn}।`;
                        setSpeechText(msg);
                        audio.speak(msg);
                      }
                    }}
                    className={`h-26 rounded-2xl p-3 flex items-center gap-3 text-left shadow transition-all cursor-pointer active:scale-95 ${
                      isSelected
                        ? 'bg-[#F3E8FF] border-2 border-[#9B5DE5]'
                        : 'bg-white/95 border-2 border-white'
                    }`}
                  >
                    <Shape3DGraphic shape3D={shape3d} sizePx={52} />
                    <div className="flex-1 min-w-0">
                      <div className="font-extrabold text-sm text-[#243B80]">
                        {shape3d.nameBn}
                      </div>
                      <div className="text-[11px] font-medium text-slate-600 line-clamp-2">
                        {shape3d.realWorldExampleBn}
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        ) : (
          /* Standard 2D Shape Matching Gameplay */
          <div className="flex-1 flex flex-col items-center px-4 py-2 space-y-2.5 overflow-y-auto">
            <MascotSpeechPrompt
              text={speechText}
              onReplayAudio={() =>
                audio.replayInstruction(
                  currentTargetShape ? `${currentTargetShape.nameBn}টি খুঁজে দাও।` : speechText
                )
              }
              isHighlighted={hintActive || demoActive}
            />

            {/* Target Shape Showcase Card */}
            <div className="w-full bg-white/95 rounded-3xl p-3.5 shadow-md border-2 border-white">
              <div className="flex items-center justify-between">
                <span className="font-bold text-xs text-[#56BFA4]">
                  {levelDef?.titleBn || `লেভেল ${level}`}
                </span>
                <span className="bg-[#E8F7F1] text-[#243B80] font-extrabold text-xs px-3 py-1 rounded-xl">
                  সম্পন্ন: {placedShapeIds.length}/{levelShapes.length}
                </span>
              </div>

              {currentTargetShape && (
                <div className="flex items-center justify-center gap-4 mt-2">
                  <div className="w-22 h-22 rounded-2xl bg-[#EAF4FF] border-2 border-[#3978D5]/50 flex items-center justify-center">
                    <ShapeGraphic
                      shape={currentTargetShape}
                      color={SHAPE_COLORS[currentTargetShape.id] || '#3978D5'}
                      sizePx={64}
                    />
                  </div>
                  <div>
                    <div className="text-xs font-semibold text-slate-500">
                      একই আকৃতিটি খুঁজে বসাও:
                    </div>
                    <div className="text-2xl font-extrabold text-[#243B80]">
                      {currentTargetShape.nameBn}
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Shapes Grid */}
            <div
              className={`w-full grid ${
                levelShapes.length > 4 ? 'grid-cols-3' : 'grid-cols-2'
              } gap-3 pb-4`}
            >
              {levelShapes.map(shape => {
                const isPlaced = placedShapeIds.includes(shape.id);
                const isTarget = shape.id === currentTargetShape?.id;
                const shouldHighlight = (hintActive || demoActive) && isTarget;

                return (
                  <button
                    key={shape.id}
                    type="button"
                    data-testid={`shape_slot_${shape.id}`}
                    onClick={() => {
                      if (isPlaced) return;
                      audio.playEffect('CARD_PLACE');

                      if (isTarget && currentTargetShape) {
                        audio.playSuccessChime();
                        audio.speakPraise();
                        setSpeechText('খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।');
                        if (errorCount === 0) {
                          onRecordSkillMastery(shape.id);
                        }
                        setPlacedShapeIds(prev => [...prev, shape.id]);
                        setRemainingShapes(prev => prev.filter(s => s.id !== shape.id));
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
                          setSpeechText(`সঠিক কার্ডটি বেছে নাও: ${currentTargetShape?.nameBn}।`);
                          audio.speak('সঠিক কার্ডটি বেছে নাও।');
                          setHintActive(true);
                          setDemoActive(true);
                        }
                      }
                    }}
                    className={`h-28 rounded-3xl p-2 flex flex-col items-center justify-center shadow transition-all cursor-pointer active:scale-95 ${
                      shouldHighlight
                        ? 'bg-[#FFF9E6] border-3 border-[#F59E0B] shadow-lg'
                        : isPlaced
                        ? 'bg-[#E8F7F1] border-2 border-[#56BFA4]'
                        : 'bg-white/95 border-2 border-white'
                    }`}
                  >
                    <ShapeGraphic
                      shape={shape}
                      color={SHAPE_COLORS[shape.id] || '#3978D5'}
                      sizePx={isSizeVariationMode && !isPlaced ? 38 : 54}
                      isSilhouette={!isPlaced && isSilhouetteMode}
                      rotationDegrees={
                        isRotatedMode && !isPlaced && shape.id !== 'circle' ? 35 : 0
                      }
                    />
                    <span
                      className={`mt-1.5 font-bold text-xs truncate max-w-full ${
                        isPlaced ? 'text-[#2E7D32]' : 'text-[#243B80]'
                      }`}
                    >
                      {demoActive && isTarget ? `👉 ${shape.nameBn}` : shape.nameBn}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>
        )}
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
            setPlacedShapeIds([]);
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
