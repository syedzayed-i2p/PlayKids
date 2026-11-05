import React, { useState } from 'react';
import { Grid, Lock, Play, Settings, Star, Volume2, VolumeX } from 'lucide-react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  AudioSettingsState,
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  LearningMode,
  toBnDigits,
} from '../types/game';
import {
  LearningModeSwitcherBar,
  MascotSpeechPrompt,
  PlayKidsBrandLogo,
  PlayKidsSettingsDialog,
  PlayoraAnimatedSceneBackground,
  PlayoraBottomNavBar,
  SmilingStarMascot,
} from '../components/MascotAndHeader';
import { ShapeGraphic } from '../components/ShapeDrawables';
import { CORE_SHAPES } from '../data/colors';

export const HomeScreen: React.FC<{
  allProgress: Record<GameTypeId, GameProgressData>;
  learningMode: LearningMode;
  lastPlayedGame: GameTypeId;
  audio: BanglaAudioEngine;
  audioSettings: AudioSettingsState;
  onSelectMode: (mode: LearningMode) => void;
  onSelectGame: (gameType: GameTypeId) => void;
  onContinueLearning: (gameType: GameTypeId, resumeLevel: number) => void;
  onOpenLevelMap: (gameType: GameTypeId) => void;
  onOpenMyProgress: () => void;
  onOpenPhysicalPractice: () => void;
  onOpenParentDashboard: () => void;
  onToggleVoiceMute: () => void;
  onUpdateAudioSettings: (newSettings: AudioSettingsState) => void;
}> = ({
  allProgress,
  learningMode,
  lastPlayedGame,
  audio,
  audioSettings,
  onSelectMode,
  onSelectGame,
  onContinueLearning,
  onOpenLevelMap,
  onOpenMyProgress,
  onOpenPhysicalPractice,
  onOpenParentDashboard,
  onToggleVoiceMute,
  onUpdateAudioSettings,
}) => {
  const welcomeText = 'হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।';
  const [showSettingsDialog, setShowSettingsDialog] = useState(false);

  const gameIds = Object.keys(GAME_TYPES) as GameTypeId[];
  const totalStars = gameIds.reduce((acc, id) => acc + (allProgress[id]?.starsEarned || 0), 0);
  const physicalCompletedCount = gameIds.filter(id => allProgress[id]?.physicalCompleted).length;

  const resumeProgress = allProgress[lastPlayedGame];
  const resumeLevel =
    learningMode === 'core'
      ? Math.min(5, Math.max(1, resumeProgress?.unlockedLevel || 1))
      : Math.min(
          25,
          Math.max(
            1,
            Math.max(
              resumeProgress?.unlockedLevel || 1,
              resumeProgress?.extendedUnlockedLevel || 1
            )
          )
        );
  const resumeChapter = Math.min(5, Math.max(1, Math.floor((resumeLevel - 1) / 5) + 1));

  return (
    <PlayoraAnimatedSceneBackground
      theme="DEFAULT_SKY_HILLS"
      reducedMotion={audioSettings.reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        <div className="flex-1 pb-4 space-y-2">
          {/* 1. Top Header Bar */}
          <div className="flex items-center justify-between px-4 pt-3 pb-1">
            <div className="flex items-center gap-2.5">
              <button
                type="button"
                data-testid="home_top_button"
                onClick={() => {
                  audio.playButtonTap();
                  audio.speak(welcomeText);
                }}
                className="w-12 h-12 rounded-full bg-[#EAF4FF] border-2 border-white shadow-md flex items-center justify-center text-2xl cursor-pointer active:scale-95"
              >
                👦
              </button>
              <div>
                <div className="text-white font-extrabold text-sm leading-tight drop-shadow-xs">
                  স্বাগতম,
                </div>
                <div className="text-white font-extrabold text-lg leading-tight drop-shadow-xs">
                  ছোট শিক্ষার্থী!
                </div>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <button
                type="button"
                data-testid="home_voice_toggle"
                onClick={() => {
                  audio.playButtonTap();
                  onToggleVoiceMute();
                }}
                className={`w-12 h-12 rounded-full border-2 border-white shadow-md flex items-center justify-center text-white cursor-pointer active:scale-95 ${
                  audioSettings.voiceEnabled
                    ? 'bg-gradient-to-b from-[#4FA3FF] to-[#2563EB]'
                    : 'bg-slate-400'
                }`}
                title="কণ্ঠস্বর চালু বা বন্ধ"
              >
                {audioSettings.voiceEnabled ? (
                  <Volume2 className="w-6 h-6" />
                ) : (
                  <VolumeX className="w-6 h-6" />
                )}
              </button>

              <button
                type="button"
                data-testid="home_settings_button"
                onClick={() => {
                  audio.playButtonTap();
                  setShowSettingsDialog(true);
                }}
                className="w-12 h-12 rounded-full bg-gradient-to-b from-[#4FA3FF] to-[#2563EB] border-2 border-white shadow-md flex items-center justify-center text-white cursor-pointer active:scale-95"
                title="সেটিংস"
              >
                <Settings className="w-6 h-6" />
              </button>
            </div>
          </div>

          {/* 2. Brand Logo + Star Counter + Mascot Speech Prompt */}
          <div className="px-4 flex flex-col items-center relative">
            <PlayKidsBrandLogo showTaglineRibbon={true} />
            <button
              type="button"
              onClick={() => {
                audio.playButtonTap();
                onOpenMyProgress();
              }}
              className="absolute right-4 top-1 bg-[#FFD166] border-2 border-white shadow px-3 py-1 rounded-2xl flex items-center gap-1 cursor-pointer active:scale-95"
            >
              <Star className="w-4 h-4 text-[#D97706] fill-[#D97706]" />
              <span className="font-extrabold text-xs text-[#243B80]">{totalStars}</span>
            </button>
          </div>

          <MascotSpeechPrompt
            text={welcomeText}
            onReplayAudio={() => audio.replayInstruction(welcomeText)}
          />

          {/* 3. Learning Mode Switcher */}
          <LearningModeSwitcherBar
            currentMode={learningMode}
            onSelectMode={mode => {
              audio.playButtonTap();
              onSelectMode(mode);
            }}
          />

          {/* 4. 2x2 Grid of 4 Large Glossy PlayKids Game Cards */}
          <div className="px-3.5 grid grid-cols-2 gap-3 pt-1">
            {gameIds.map(id => {
              const gType = GAME_TYPES[id];
              const gProgress = allProgress[id];
              const activeLevel =
                learningMode === 'core'
                  ? Math.min(5, Math.max(1, gProgress?.unlockedLevel || 1))
                  : Math.min(
                      25,
                      Math.max(
                        1,
                        Math.max(
                          gProgress?.unlockedLevel || 1,
                          gProgress?.extendedUnlockedLevel || 1
                        )
                      )
                    );
              const maxLevelCount = learningMode === 'core' ? 5 : 25;
              const pct = Math.min(100, Math.max(12, Math.round((activeLevel / maxLevelCount) * 100)));

              const cardBgClass =
                id === 'emoji_match'
                  ? 'from-[#FFF8D6] via-[#FFE082] to-[#FCD34D]'
                  : id === 'shape_match'
                  ? 'from-[#DFFAF0] via-[#86EFAC] to-[#4ADE80]'
                  : id === 'color_shape_challenge'
                  ? 'from-[#F3E8FF] via-[#D8B4FE] to-[#C084FC]'
                  : 'from-[#FFE5EC] via-[#FDA4AF] to-[#FB7185]';

              return (
                <div
                  key={id}
                  data-testid={`home_game_card_${id}`}
                  onClick={() => {
                    audio.playEffect('CARD_SELECT');
                    onSelectGame(id);
                  }}
                  className={`bg-gradient-to-b ${cardBgClass} rounded-3xl p-3 shadow-lg border-3 border-white flex flex-col items-center cursor-pointer active:scale-98 transition-transform`}
                >
                  {/* Top Row: Stars & Map button */}
                  <div className="w-full flex items-center justify-between">
                    <span className="bg-white/90 px-2 py-0.5 rounded-lg font-extrabold text-xs text-[#243B80]">
                      ⭐ {gProgress?.starsEarned || 0}
                    </span>

                    <button
                      type="button"
                      data-testid={`level_map_btn_${id}`}
                      onClick={e => {
                        e.stopPropagation();
                        audio.playButtonTap();
                        onOpenLevelMap(id);
                      }}
                      className="bg-white/90 px-2 py-0.5 rounded-lg font-bold text-[11px] text-[#243B80] flex items-center gap-1 hover:bg-white cursor-pointer"
                    >
                      <Grid className="w-3 h-3" />
                      <span>ম্যাপ</span>
                    </button>
                  </div>

                  {/* Center Illustration */}
                  <div className="h-18 w-full flex items-center justify-center my-1">
                    {id === 'emoji_match' && (
                      <div className="w-15 h-15 rounded-full bg-gradient-to-br from-[#FF6B6B] to-[#E11D48] shadow-md flex items-center justify-center text-4xl border-2 border-white/80">
                        🍎
                      </div>
                    )}
                    {id === 'shape_match' && (
                      <div className="flex items-center -space-x-2">
                        <ShapeGraphic shape={CORE_SHAPES[1]} color="#E53935" sizePx={48} />
                        <ShapeGraphic shape={CORE_SHAPES[0]} color="#3978D5" sizePx={44} />
                      </div>
                    )}
                    {id === 'color_shape_challenge' && (
                      <div className="w-15 h-15 rounded-full bg-gradient-to-br from-[#FDE68A] to-[#F59E0B] shadow-md border-2 border-white flex items-center justify-center text-3xl">
                        🎨
                      </div>
                    )}
                    {id === 'color_paper_match' && (
                      <div className="flex items-center justify-center -space-x-2">
                        {['#9333EA', '#EF4444', '#FACC15', '#22C55E'].map((col, idx) => (
                          <div
                            key={col}
                            style={{
                              backgroundColor: col,
                              transform: `rotate(${(idx - 1.5) * 10}deg)`,
                            }}
                            className="w-7 h-11 rounded-md border-2 border-white shadow-xs"
                          />
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="font-extrabold text-base text-[#243B80] text-center truncate w-full">
                    {gType.titleBn}
                  </div>
                  <div className="font-bold text-[11px] text-[#243B80]/85 text-center truncate w-full">
                    ({gType.titleEn})
                  </div>

                  <div className="w-full h-2 bg-white/60 rounded-full overflow-hidden mt-2">
                    <div
                      className="h-full bg-[#243B80] rounded-full"
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                  <div className="mt-1 font-bold text-[10px] text-[#243B80]">
                    লেভেল {activeLevel}/{maxLevelCount}
                  </div>
                </div>
              );
            })}
          </div>

          {/* 5. Continue Learning Banner */}
          <div className="px-3.5 pt-1">
            <div
              data-testid="home_continue_learning_button"
              onClick={() => {
                audio.playEffect('CARD_SELECT');
                onContinueLearning(lastPlayedGame, resumeLevel);
              }}
              className="bg-white/95 rounded-3xl p-3.5 shadow-lg border-2 border-white flex items-center justify-between gap-3 cursor-pointer active:scale-98"
            >
              <SmilingStarMascot
                size={48}
                showBook={true}
                reducedMotion={audioSettings.reducedMotion}
              />
              <div className="flex-1 min-w-0">
                <div className="font-extrabold text-lg text-[#243B80] leading-tight">
                  চালিয়ে যাও
                </div>
                <div className="font-bold text-xs text-slate-600 truncate">
                  {GAME_TYPES[lastPlayedGame].titleBn} - অধ্যায় {toBnDigits(resumeChapter)}, স্তর{' '}
                  {toBnDigits(resumeLevel)}
                </div>
              </div>
              <div className="w-12 h-12 rounded-full bg-gradient-to-b from-[#4ADE80] to-[#16A34A] border-2 border-white shadow flex items-center justify-center text-white shrink-0">
                <Play className="w-6 h-6 fill-white" />
              </div>
            </div>
          </div>

          {/* 6. Secondary Quick Access Pills */}
          <div className="px-3.5 grid grid-cols-2 gap-2.5 pt-1">
            <button
              type="button"
              data-testid="home_physical_practice_button"
              onClick={() => {
                audio.playButtonTap();
                onOpenPhysicalPractice();
              }}
              className="h-12 rounded-2xl bg-white/95 border-2 border-[#56BFA4] font-bold text-xs text-[#2E7D32] flex items-center justify-center shadow-xs cursor-pointer active:scale-95"
            >
              🎯 বাস্তব অনুশীলন ({toBnDigits(physicalCompletedCount)}/৪)
            </button>

            <button
              type="button"
              data-testid="home_parent_dashboard_button"
              onClick={() => {
                audio.playButtonTap();
                onOpenParentDashboard();
              }}
              className="h-12 rounded-2xl bg-white/95 border-2 border-[#9B5DE5] font-bold text-xs text-[#9B5DE5] flex items-center justify-center gap-1.5 shadow-xs cursor-pointer active:scale-95"
            >
              <Lock className="w-4 h-4" />
              <span>অভিভাবক প্যানেল</span>
            </button>
          </div>
        </div>

        <PlayoraBottomNavBar
          currentScreen="HOME"
          onSelectTab={target => {
            audio.playButtonTap();
            if (target === 'MY_PROGRESS') onOpenMyProgress();
            else if (target === 'PHYSICAL_PRACTICE') onOpenPhysicalPractice();
            else if (target === 'PARENT_DASHBOARD') onOpenParentDashboard();
          }}
        />
      </div>

      {showSettingsDialog && (
        <PlayKidsSettingsDialog
          audioSettings={audioSettings}
          onUpdateAudioSettings={updated => {
            audio.updateSettings(updated);
            onUpdateAudioSettings(updated);
          }}
          onDismiss={() => setShowSettingsDialog(false)}
        />
      )}
    </PlayoraAnimatedSceneBackground>
  );
};
