import React, { useEffect, useState } from 'react';
import { ArrowLeft, Play, Star } from 'lucide-react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  LearningMode,
  ScreenType,
} from '../types/game';
import {
  PlayKidsBrandLogo,
  PlayoraAnimatedSceneBackground,
  PlayoraBottomNavBar,
  SmilingStarMascot,
} from '../components/MascotAndHeader';
import { ShapeGraphic } from '../components/ShapeDrawables';
import { CORE_SHAPES } from '../data/colors';

export const PlayoraSplashScreen: React.FC<{
  reducedMotion?: boolean;
  onSplashFinished: () => void;
}> = ({ reducedMotion = false, onSplashFinished }) => {
  const [progress, setProgress] = useState(25);

  useEffect(() => {
    const t1 = setTimeout(() => setProgress(100), 80);
    const t2 = setTimeout(() => onSplashFinished(), 750);
    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
    };
  }, [onSplashFinished]);

  return (
    <PlayoraAnimatedSceneBackground
      theme="DEFAULT_SKY_HILLS"
      reducedMotion={reducedMotion}
      onClick={onSplashFinished}
      className="cursor-pointer"
    >
      <div
        data-testid="splash_screen"
        className="flex-1 flex flex-col items-center justify-between px-6 py-10"
      >
        <div />
        <div className="flex flex-col items-center">
          <SmilingStarMascot size={136} isHappy={true} showBook={true} reducedMotion={reducedMotion} />
          <div className="mt-4">
            <PlayKidsBrandLogo showTaglineRibbon={true} />
          </div>
        </div>

        <div className="w-full max-w-xs flex flex-col items-center">
          <div className="w-full h-5 bg-white/95 rounded-full p-1 shadow border-2 border-white">
            <div
              className="h-full rounded-full bg-gradient-to-r from-[#2563EB] via-[#3978D5] to-[#38BDF8] transition-all duration-700"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="mt-2.5 text-white font-extrabold text-lg drop-shadow">লোড হচ্ছে...</p>
        </div>
      </div>
    </PlayoraAnimatedSceneBackground>
  );
};

export const PlayoraWelcomeScreen: React.FC<{
  audio: BanglaAudioEngine;
  reducedMotion?: boolean;
  onStartExploring: () => void;
}> = ({ audio, reducedMotion = false, onStartExploring }) => {
  const handleStart = () => {
    audio.playButtonTap();
    audio.speak('হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।');
    onStartExploring();
  };

  return (
    <PlayoraAnimatedSceneBackground theme="DEFAULT_SKY_HILLS" reducedMotion={reducedMotion}>
      <div className="flex-1 flex flex-col items-center justify-between px-6 py-8">
        {/* Top Speech Bubble */}
        <div className="w-full flex flex-col items-center mt-2">
          <div className="w-full max-w-md bg-white/95 rounded-3xl p-6 shadow-xl border-4 border-white text-center">
            <h1 className="text-3xl font-extrabold text-[#243B80]">হ্যালো!</h1>
            <p className="mt-2 text-xl font-extrabold text-[#243B80] leading-relaxed">
              চলো, আজকে আমরা
              <br />
              নতুন কিছু শিখি।
            </p>
          </div>
          <div className="w-0 h-0 border-l-[14px] border-l-transparent border-r-[14px] border-r-transparent border-t-[18px] border-t-white" />
        </div>

        {/* Center Waving Learning Star Mascot */}
        <SmilingStarMascot
          size={160}
          isHappy={true}
          showBook={false}
          reducedMotion={reducedMotion}
        />

        {/* Glossy Purple Pill Button */}
        <div className="w-full max-w-sm flex flex-col items-center">
          <button
            type="button"
            data-testid="welcome_explore_button"
            onClick={handleStart}
            className="w-full h-15 rounded-full bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white font-extrabold text-xl shadow-xl border-2 border-white flex items-center justify-center gap-3 active:scale-95 transition-transform cursor-pointer"
          >
            <Play className="w-6 h-6 fill-white" />
            <span>শুরু করি</span>
          </button>

          <button
            type="button"
            data-testid="welcome_play_circle_button"
            onClick={handleStart}
            className="mt-4 flex items-center gap-2.5 px-3 py-1.5 rounded-full cursor-pointer"
          >
            <span className="w-3 h-3 rounded-full bg-white" />
            <span className="w-2.5 h-2.5 rounded-full bg-white/65" />
            <span className="w-2.5 h-2.5 rounded-full bg-white/65" />
          </button>
        </div>
      </div>
    </PlayoraAnimatedSceneBackground>
  );
};

export const MyProgressScreen: React.FC<{
  allProgress: Record<GameTypeId, GameProgressData>;
  learningMode: LearningMode;
  audio: BanglaAudioEngine;
  onOpenLevelMap: (gameType: GameTypeId) => void;
  onNavigateTab: (target: ScreenType) => void;
  onBack: () => void;
}> = ({ allProgress, audio, onOpenLevelMap, onNavigateTab, onBack }) => {
  const gameIds = Object.keys(GAME_TYPES) as GameTypeId[];
  const totalStars = gameIds.reduce((acc, id) => acc + (allProgress[id]?.starsEarned || 0), 0);

  const totalCompletedLevels = gameIds.reduce((acc, id) => {
    const p = allProgress[id];
    if (!p) return acc;
    const uniqueCompleted = new Set([...p.coreCompletedLevels, ...p.completedLevels]).size;
    const unlocked = Math.max(p.unlockedLevel, p.extendedUnlockedLevel);
    return acc + Math.min(25, Math.max(uniqueCompleted, unlocked - 1));
  }, 0);

  return (
    <PlayoraAnimatedSceneBackground theme="DEFAULT_SKY_HILLS">
      <div className="flex-1 flex flex-col justify-between">
        {/* Top Purple Header Bar */}
        <div className="flex items-center justify-between gap-3 px-4 pt-3 pb-2">
          <button
            type="button"
            data-testid="progress_back_button"
            onClick={() => {
              audio.playButtonTap();
              onBack();
            }}
            className="w-12 h-12 rounded-full bg-gradient-to-b from-[#9B5DE5] to-[#7C3AED] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>

          <div className="flex-1 bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white rounded-3xl py-2.5 px-4 shadow-md border-2 border-white text-center font-extrabold text-xl">
            আমার অগ্রগতি
          </div>

          <SmilingStarMascot size={46} showBook={true} />
        </div>

        {/* Content */}
        <div className="flex-1 px-4 py-2 space-y-3 overflow-y-auto">
          <div className="bg-white/95 rounded-3xl p-4 shadow-lg border-2 border-white space-y-3.5">
            {gameIds.map((id, idx) => {
              const gType = GAME_TYPES[id];
              const gProgress = allProgress[id];
              const unlockedLvl = Math.max(
                gProgress?.unlockedLevel || 1,
                gProgress?.extendedUnlockedLevel || 1
              );
              const uniqueComp = new Set([
                ...(gProgress?.coreCompletedLevels || []),
                ...(gProgress?.completedLevels || []),
              ]).size;
              const completedCount = Math.min(25, Math.max(1, Math.max(uniqueComp, unlockedLvl - 1)));
              const pct = Math.min(100, Math.max(4, Math.round((completedCount * 100) / 25)));

              return (
                <div key={id}>
                  <div
                    data-testid={`progress_game_card_${id}`}
                    onClick={() => {
                      audio.playEffect('CARD_SELECT');
                      onOpenLevelMap(id);
                    }}
                    className="flex items-center gap-3 p-1.5 rounded-2xl hover:bg-slate-50 cursor-pointer transition-colors"
                  >
                    <div
                      style={{
                        backgroundColor: gType.lightBg,
                        borderColor: gType.accentBorder,
                      }}
                      className="w-14 h-14 rounded-2xl border-2 flex items-center justify-center shrink-0"
                    >
                      {id === 'emoji_match' && <span className="text-3xl">🍎</span>}
                      {id === 'shape_match' && (
                        <ShapeGraphic shape={CORE_SHAPES[1]} color="#F59E0B" sizePx={36} />
                      )}
                      {id === 'color_shape_challenge' && <span className="text-3xl">🎨</span>}
                      {id === 'color_paper_match' && <span className="text-3xl">📄</span>}
                    </div>

                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <span className="font-extrabold text-base text-[#243B80]">
                          {gType.titleBn}
                        </span>
                        <span className="font-extrabold text-sm text-[#243B80]">
                          {completedCount}/25
                        </span>
                      </div>

                      <div className="flex items-center gap-2.5 mt-1.5">
                        <div className="flex-1 h-3.5 bg-slate-200 rounded-full overflow-hidden">
                          <div
                            className="h-full bg-gradient-to-r from-[#4ADE80] to-[#16A34A] rounded-full"
                            style={{ width: `${pct}%` }}
                          />
                        </div>
                        <span className="font-extrabold text-xs text-[#243B80] w-9 text-right">
                          {pct}%
                        </span>
                      </div>
                    </div>
                  </div>
                  {idx < gameIds.length - 1 && <hr className="border-slate-200/70 mt-3" />}
                </div>
              );
            })}
          </div>

          {/* Total Progress Ribbon Card */}
          <div className="bg-gradient-to-r from-[#E11D48] via-[#EA580C] to-[#D97706] rounded-3xl p-4 shadow-lg border-2 border-white flex items-center justify-center gap-4 text-white">
            <div className="w-14 h-14 rounded-full bg-[#FFD166] border-2 border-white shadow flex items-center justify-center shrink-0">
              <Star className="w-8 h-8 text-[#D97706] fill-[#D97706]" />
            </div>
            <div className="text-center">
              <div className="font-extrabold text-base">মোট অগ্রগতি</div>
              <div className="font-black text-2xl">{totalCompletedLevels}/100</div>
              <div className="text-xs font-bold text-[#FFD166]">
                Total Stars : {totalStars} • মোট তারা: {totalStars}
              </div>
            </div>
          </div>
        </div>

        <PlayoraBottomNavBar currentScreen="MY_PROGRESS" onSelectTab={onNavigateTab} />
      </div>
    </PlayoraAnimatedSceneBackground>
  );
};
