import React, { useState } from 'react';
import { ArrowLeft, Lock, Star } from 'lucide-react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  LearningMode,
  toBnDigits,
} from '../types/game';
import { CHAPTERS, getLevelsForGame } from '../data/curriculum';
import {
  LearningModeSwitcherBar,
  PlayoraAnimatedSceneBackground,
} from '../components/MascotAndHeader';

export const LevelSelectionScreen: React.FC<{
  gameType: GameTypeId;
  progress: GameProgressData;
  learningMode: LearningMode;
  audio: BanglaAudioEngine;
  onSelectMode: (mode: LearningMode) => void;
  onSelectLevel: (level: number) => void;
  onBack: () => void;
}> = ({ gameType, progress, learningMode, audio, onSelectMode, onSelectLevel, onBack }) => {
  const allLevels = getLevelsForGame(gameType);
  const maxUnlocked =
    learningMode === 'core'
      ? Math.min(5, Math.max(1, progress.unlockedLevel))
      : Math.min(25, Math.max(1, Math.max(progress.unlockedLevel, progress.extendedUnlockedLevel)));

  const activeCompletedSet = new Set(
    learningMode === 'core' ? progress.coreCompletedLevels : progress.completedLevels
  );

  const [lockedFeedbackBanner, setLockedFeedbackBanner] = useState<string | null>(null);
  const defaultChapter = Math.min(5, Math.max(1, Math.floor((maxUnlocked - 1) / 5) + 1));
  const [selectedChapterNum, setSelectedChapterNum] = useState<number>(defaultChapter);

  const activeChapter =
    CHAPTERS.find(c => c.chapterNumber === selectedChapterNum) || CHAPTERS[0];

  const visibleLevels = learningMode === 'core' ? allLevels.slice(0, 5) : allLevels;

  // Group into rows of 3 for S-curve winding map
  const rows: typeof visibleLevels[] = [];
  for (let i = 0; i < visibleLevels.length; i += 3) {
    rows.push(visibleLevels.slice(i, i + 3));
  }

  return (
    <PlayoraAnimatedSceneBackground
      theme="DEFAULT_SKY_HILLS"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top Bar */}
        <div className="flex flex-col items-center pt-3 pb-1">
          <div className="w-full flex items-center justify-between px-3.5 gap-2.5">
            <button
              type="button"
              data-testid="level_map_back_button"
              onClick={() => {
                audio.playButtonTap();
                onBack();
              }}
              className="w-12 h-12 rounded-full bg-gradient-to-b from-[#4FA3FF] to-[#2563EB] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer shrink-0"
            >
              <ArrowLeft className="w-6 h-6" />
            </button>

            <div className="flex-1 bg-white/95 rounded-3xl py-2 px-4 shadow-md border-2 border-[#3978D5] text-center">
              <span className="font-extrabold text-xl text-[#243B80]">
                {GAME_TYPES[gameType].titleBn}
              </span>
            </div>

            <div className="bg-[#FFD166] border-2 border-white shadow px-3 py-2 rounded-2xl flex items-center gap-1 shrink-0">
              <Star className="w-4 h-4 text-[#D97706] fill-[#D97706]" />
              <span className="font-extrabold text-sm text-[#243B80]">{progress.starsEarned}</span>
            </div>
          </div>

          <div className="mt-2 bg-[#243B80] text-white font-extrabold text-sm px-5 py-1.5 rounded-full shadow border-2 border-white">
            অধ্যায় {toBnDigits(activeChapter.chapterNumber)} : {activeChapter.titleBn}
          </div>

          <div className="w-full mt-1">
            <LearningModeSwitcherBar
              currentMode={learningMode}
              onSelectMode={mode => {
                audio.playButtonTap();
                setLockedFeedbackBanner(null);
                onSelectMode(mode);
              }}
            />
          </div>

          {lockedFeedbackBanner && (
            <div className="w-full px-4 mt-1">
              <div className="bg-[#FFD166] text-[#243B80] font-bold text-xs px-4 py-2 rounded-xl border-2 border-white shadow text-center">
                🔒 {lockedFeedbackBanner}
              </div>
            </div>
          )}
        </div>

        {/* Winding Map Nodes */}
        <div className="flex-1 px-4 py-3 space-y-4 overflow-y-auto">
          <div className="flex items-center justify-between px-2">
            <span className="font-bold text-xs text-[#243B80]">{activeChapter.subtitleBn}</span>
            <span className="text-3xl">🏡</span>
          </div>

          {rows.map((rowLevels, rowIndex) => {
            const isReversed = rowIndex % 2 === 1;
            const ordered = isReversed ? [...rowLevels].reverse() : rowLevels;

            return (
              <div
                key={rowIndex}
                className="relative py-3 flex items-center justify-around bg-white/35 rounded-3xl border border-white/60 shadow-xs"
              >
                {ordered.map(lvlDef => {
                  const isCompleted = activeCompletedSet.has(lvlDef.levelNumber);
                  const isUnlocked = isCompleted || lvlDef.levelNumber <= maxUnlocked;
                  const isCurrent =
                    isUnlocked && !isCompleted && lvlDef.levelNumber === maxUnlocked;

                  const nodeBg = isCurrent
                    ? 'bg-gradient-to-b from-[#9B5DE5] to-[#7C3AED] text-white border-[#FFD166]'
                    : isUnlocked
                    ? 'bg-gradient-to-b from-[#FFE066] to-[#F59E0B] text-[#243B80] border-white'
                    : 'bg-slate-200 text-slate-500 border-white';

                  return (
                    <button
                      key={lvlDef.levelNumber}
                      type="button"
                      data-testid={`level_item_${lvlDef.levelNumber}`}
                      onClick={() => {
                        if (isUnlocked) {
                          setLockedFeedbackBanner(null);
                          audio.playEffect('CARD_SELECT');
                          onSelectLevel(lvlDef.levelNumber);
                        } else {
                          audio.playTryAgainChime();
                          setLockedFeedbackBanner(
                            `আগের ধাপটি শেষ করে লেভেল ${lvlDef.levelNumber} খোলো।`
                          );
                          audio.speak('আগের ধাপটি শেষ করে এটি খোলো।');
                        }
                      }}
                      className="flex flex-col items-center w-22 cursor-pointer group"
                    >
                      <div
                        className={`w-15 h-15 rounded-full border-3 shadow-md flex items-center justify-center font-extrabold text-xl transition-transform group-active:scale-95 ${nodeBg}`}
                      >
                        {isUnlocked ? (
                          lvlDef.levelNumber
                        ) : (
                          <Lock className="w-6 h-6 text-slate-600" />
                        )}
                      </div>

                      {isUnlocked && (
                        <div className="flex items-center justify-center gap-0.5 mt-1">
                          {[0, 1, 2].map(starIdx => {
                            const filled = isCompleted || (isCurrent && starIdx === 0);
                            return (
                              <Star
                                key={starIdx}
                                className={`w-3.5 h-3.5 ${
                                  filled
                                    ? 'text-[#FFB300] fill-[#FFB300]'
                                    : 'text-white/80 fill-white/60'
                                }`}
                              />
                            );
                          })}
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>
            );
          })}
        </div>

        {/* Bottom 5-Chapter Selector Bar */}
        <div className="px-3 pb-3 pt-1">
          <div className="bg-white/95 rounded-3xl p-2 shadow-lg border-2 border-white grid grid-cols-5 gap-1.5">
            {CHAPTERS.map(chap => {
              const isSelected = chap.chapterNumber === selectedChapterNum;
              return (
                <button
                  key={chap.chapterNumber}
                  type="button"
                  data-testid={`chapter_tab_${chap.chapterNumber}`}
                  onClick={() => {
                    audio.playButtonTap();
                    setSelectedChapterNum(chap.chapterNumber);
                    if (chap.chapterNumber > 1 && learningMode === 'core') {
                      onSelectMode('extended');
                    }
                  }}
                  className={`py-2 px-1 rounded-2xl flex flex-col items-center justify-center border transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#9B5DE5] text-white border-white shadow'
                      : 'bg-white text-[#243B80] border-slate-200 hover:bg-slate-50'
                  }`}
                >
                  <span className="text-[11px] font-extrabold">অধ্যায়</span>
                  <span className="text-base font-extrabold">
                    {toBnDigits(chap.chapterNumber)}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </PlayoraAnimatedSceneBackground>
  );
};
