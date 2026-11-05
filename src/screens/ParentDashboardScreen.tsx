import React, { useState } from 'react';
import {
  ArrowLeft,
  BarChart3,
  BookOpen,
  Info,
  Lock,
  RefreshCw,
  Sliders,
  Unlock,
  Volume2,
} from 'lucide-react';
import {
  BanglaAudioEngine,
  VOICE_PREVIEW_SAMPLES,
} from '../audio/BanglaAudioEngine';
import {
  AudioSettingsState,
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  ScreenType,
  SkillMasterySnapshot,
} from '../types/game';
import { ALL_10_COLORS, ALL_25_SHAPES } from '../data/colors';
import {
  PlayKidsSettingsDialog,
  PlayoraAnimatedSceneBackground,
  PlayoraBottomNavBar,
} from '../components/MascotAndHeader';
import { ShapeGraphic } from '../components/ShapeDrawables';

export const ParentDashboardScreen: React.FC<{
  allProgress: Record<GameTypeId, GameProgressData>;
  skillMastery: SkillMasterySnapshot;
  audio: BanglaAudioEngine;
  audioSettings: AudioSettingsState;
  onBack: () => void;
  onUpdateAudioSettings: (newSettings: AudioSettingsState) => void;
  onUnlockAllLevels: () => void;
  onResetAllProgress: () => void;
  onNavigateTab: (target: ScreenType) => void;
}> = ({
  allProgress,
  skillMastery,
  audio,
  audioSettings,
  onBack,
  onUpdateAudioSettings,
  onUnlockAllLevels,
  onResetAllProgress,
  onNavigateTab,
}) => {
  const [isGateUnlocked, setIsGateUnlocked] = useState(false);
  const num1 = 5;
  const num2 = 4;
  const [inputAnswer, setInputAnswer] = useState('');
  const [gateError, setGateError] = useState(false);

  const [showResetConfirmDialog, setShowResetConfirmDialog] = useState(false);
  const [showSettingsModal, setShowSettingsModal] = useState(false);
  const [unlockSuccessBanner, setUnlockSuccessBanner] = useState(false);
  const [activePreviewSampleId, setActivePreviewSampleId] = useState<string | null>(null);

  const gameIds = Object.keys(GAME_TYPES) as GameTypeId[];

  if (!isGateUnlocked) {
    return (
      <PlayoraAnimatedSceneBackground
        theme="DEFAULT_SKY_HILLS"
        reducedMotion={audioSettings.reducedMotion}
      >
        <div className="flex-1 flex flex-col justify-between">
          <div className="flex items-center gap-3 px-4 pt-3 pb-2">
            <button
              type="button"
              onClick={onBack}
              className="w-12 h-12 rounded-full bg-gradient-to-b from-[#9B5DE5] to-[#7C3AED] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer"
            >
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex-1 bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white rounded-3xl py-2.5 px-4 shadow-md border-2 border-white text-center font-extrabold text-xl">
              অভিভাবক প্যানেল
            </div>
          </div>

          <div className="flex-1 flex items-center justify-center p-6">
            <div className="bg-white/95 rounded-3xl p-6 shadow-xl border-3 border-white max-w-md w-full flex flex-col items-center text-center">
              <div className="w-16 h-16 rounded-full bg-[#F3E8FF] flex items-center justify-center text-[#9B5DE5]">
                <Lock className="w-8 h-8" />
              </div>

              <h2 className="mt-3 text-xl font-extrabold text-[#243B80]">
                Parent Gate • অভিভাবক নিরাপত্তা যাচাই
              </h2>
              <p className="mt-1 text-xs font-semibold text-slate-600">
                শিশুদের অজান্তে সেটিংস পরিবর্তন রোধে উত্তরটি লিখুন:
              </p>

              <div className="my-4 text-3xl font-black text-[#243B80]">
                {num1} + {num2} = কত?
              </div>

              <input
                type="text"
                data-testid="parent_gate_input"
                value={inputAnswer}
                onChange={e => {
                  setInputAnswer(e.target.value);
                  setGateError(false);
                }}
                placeholder={`উত্তর লিখুন (যেমন: ${num1 + num2})`}
                className="w-full h-12 rounded-2xl border-2 border-slate-300 px-4 text-center font-bold text-base focus:border-[#9B5DE5] focus:outline-none"
              />

              {gateError && (
                <p className="mt-1.5 text-xs font-bold text-[#E53935]">
                  সঠিক উত্তর নয়, আবার চেষ্টা করুন।
                </p>
              )}

              <button
                type="button"
                data-testid="parent_gate_submit"
                onClick={() => {
                  const normalized = inputAnswer.trim().replace(/৯/g, '9');
                  if (normalized === (num1 + num2).toString()) {
                    setIsGateUnlocked(true);
                  } else {
                    setGateError(true);
                  }
                }}
                className="mt-5 w-full h-13 rounded-2xl bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white font-extrabold text-base shadow-md border-2 border-white cursor-pointer active:scale-95"
              >
                ড্যাশবোর্ডে প্রবেশ করুন
              </button>
            </div>
          </div>

          <PlayoraBottomNavBar
            currentScreen="PARENT_DASHBOARD"
            onSelectTab={target => {
              audio.playButtonTap();
              onNavigateTab(target);
            }}
          />
        </div>
      </PlayoraAnimatedSceneBackground>
    );
  }

  return (
    <PlayoraAnimatedSceneBackground
      theme="DEFAULT_SKY_HILLS"
      reducedMotion={audioSettings.reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top Bar */}
        <div className="flex items-center gap-3 px-4 pt-3 pb-2">
          <button
            type="button"
            data-testid="parent_dashboard_back_button"
            onClick={onBack}
            className="w-12 h-12 rounded-full bg-gradient-to-b from-[#9B5DE5] to-[#7C3AED] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>

          <div className="flex-1 bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white rounded-3xl py-2 px-4 shadow-md border-2 border-white flex items-center justify-center gap-2 font-extrabold text-xl">
            <span>👨‍👩‍👧</span>
            <span>অভিভাবক প্যানেল</span>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 px-4 py-2 space-y-3.5 overflow-y-auto">
          {/* 2x3 Quick-Action Tile Grid */}
          <div className="bg-white/95 rounded-3xl p-3.5 shadow-lg border-2 border-white grid grid-cols-2 gap-2.5">
            <button
              type="button"
              data-testid="parent_tile_progress"
              onClick={() => {
                audio.playButtonTap();
                onNavigateTab('MY_PROGRESS');
              }}
              className="p-3 rounded-2xl bg-[#EAF4FF] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <BarChart3 className="w-5 h-5 text-[#3978D5]" />
              <span>শিশুর অগ্রগতি</span>
            </button>

            <button
              type="button"
              data-testid="parent_tile_voice_preview"
              onClick={() => {
                const first = VOICE_PREVIEW_SAMPLES[0];
                setActivePreviewSampleId(first.key);
                audio.playVoicePreviewSample(first);
              }}
              className="p-3 rounded-2xl bg-[#F3E8FF] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <Volume2 className="w-5 h-5 text-[#9B5DE5]" />
              <span>ভয়েস প্রিভিউ</span>
            </button>

            <button
              type="button"
              data-testid="parent_tile_audio_settings"
              onClick={() => {
                audio.playButtonTap();
                setShowSettingsModal(true);
              }}
              className="p-3 rounded-2xl bg-[#F3E8FF] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <Sliders className="w-5 h-5 text-[#9B5DE5]" />
              <span>অডিও সেটিংস</span>
            </button>

            <button
              type="button"
              data-testid="parent_tile_physical_practice"
              onClick={() => {
                audio.playButtonTap();
                onNavigateTab('PHYSICAL_PRACTICE');
              }}
              className="p-3 rounded-2xl bg-[#E8F7F1] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <BookOpen className="w-5 h-5 text-[#2E7D32]" />
              <span>বাস্তব অনুশীলন</span>
            </button>

            <button
              type="button"
              data-testid="parent_tile_reset_progress"
              onClick={() => {
                audio.playButtonTap();
                setShowResetConfirmDialog(true);
              }}
              className="p-3 rounded-2xl bg-[#FFF0ED] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <RefreshCw className="w-5 h-5 text-[#E53935]" />
              <span>অগ্রগতি রিসেট</span>
            </button>

            <button
              type="button"
              data-testid="parent_tile_about_app"
              onClick={() => {
                audio.playButtonTap();
                setShowSettingsModal(true);
              }}
              className="p-3 rounded-2xl bg-[#FFF9E6] flex items-center gap-2.5 font-extrabold text-xs text-[#243B80] cursor-pointer active:scale-95"
            >
              <Info className="w-5 h-5 text-[#D97706]" />
              <span>অ্যাপ সম্পর্কে</span>
            </button>
          </div>

          {/* 1. Voice Preview & Audio Settings Section */}
          <div className="bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white space-y-3">
            <div>
              <h3 className="font-extrabold text-sm text-[#243B80]">
                Voice Preview • কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর)
              </h3>
              <p className="text-[11px] font-semibold text-slate-500">
                Youthful Boy-Like Bengali Voice (10–12 Years • bn-BD Offline)
              </p>
            </div>

            <div className="bg-[#E8F7F1] rounded-2xl p-3 text-xs font-bold text-[#2E7D32]">
              কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর • একক অফলাইন ভয়েস সক্রিয়) — অফলাইন কিশোর বাংলা ভয়েস ক্লিপ সক্রিয় আছে।
            </div>

            <div className="text-xs font-bold text-[#243B80]">
              নমুনা বাক্যগুলো চালিয়ে কণ্ঠস্বর পরীক্ষা করুন ({VOICE_PREVIEW_SAMPLES.length}টি):
            </div>

            <div className="space-y-1.5">
              {VOICE_PREVIEW_SAMPLES.map((sample, idx) => {
                const isPlaying = activePreviewSampleId === sample.key;
                return (
                  <button
                    key={sample.key}
                    type="button"
                    data-testid={`voice_preview_sample_${idx + 1}`}
                    onClick={() => {
                      setActivePreviewSampleId(sample.key);
                      audio.playVoicePreviewSample(sample);
                    }}
                    className={`w-full p-2.5 rounded-2xl border flex items-center justify-between text-left transition-all cursor-pointer ${
                      isPlaying
                        ? 'bg-[#EAF4FF] border-[#3978D5]'
                        : 'bg-slate-50 border-slate-200 hover:bg-slate-100'
                    }`}
                  >
                    <div className="pr-2">
                      <div className="text-[11px] font-bold text-[#3978D5]">
                        {idx + 1}. {sample.titleBn}
                      </div>
                      <div className="text-xs font-bold text-[#243B80]">
                        "{sample.banglaScript}"
                      </div>
                    </div>
                    <div className="w-9 h-9 rounded-full bg-[#3978D5] text-white flex items-center justify-center shrink-0">
                      <Volume2 className="w-4 h-4" />
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          {/* 2. Game-wise Progress Summary */}
          <div className="space-y-2.5">
            {gameIds.map(id => {
              const gType = GAME_TYPES[id];
              const pData = allProgress[id];
              const extLvl = Math.min(
                25,
                Math.max(1, Math.max(pData?.unlockedLevel || 1, pData?.extendedUnlockedLevel || 1))
              );
              const chapterNum = Math.floor((extLvl - 1) / 5) + 1;

              return (
                <div
                  key={id}
                  className="bg-white/95 rounded-2xl p-3.5 shadow border border-white"
                >
                  <div className="flex items-center justify-between">
                    <span
                      style={{ color: gType.themeColor }}
                      className="font-extrabold text-sm"
                    >
                      {gType.titleEn} ({gType.titleBn})
                    </span>
                    <span
                      style={{ backgroundColor: gType.lightBg }}
                      className="text-xs font-bold text-[#243B80] px-2.5 py-1 rounded-lg"
                    >
                      অধ্যায় {chapterNum} • লেভেল {extLvl}/২৫
                    </span>
                  </div>

                  <div className="grid grid-cols-4 gap-2 mt-2.5 text-center">
                    <div>
                      <div className="text-[10px] text-slate-500">সম্পন্ন লেভেল</div>
                      <div className="font-bold text-xs text-[#243B80]">
                        {pData?.completedLevels.length || 0}/২৫ টি
                      </div>
                    </div>
                    <div>
                      <div className="text-[10px] text-slate-500">স্বাধীন সফলতা</div>
                      <div className="font-bold text-xs text-[#243B80]">
                        {pData?.independentSuccesses || 0} বার
                      </div>
                    </div>
                    <div>
                      <div className="text-[10px] text-slate-500">ইঙ্গিত ব্যবহার</div>
                      <div className="font-bold text-xs text-[#243B80]">
                        {pData?.hintsUsed || 0} বার
                      </div>
                    </div>
                    <div>
                      <div className="text-[10px] text-slate-500">বাস্তব খেলা</div>
                      <div
                        className={`font-bold text-xs ${
                          pData?.physicalCompleted ? 'text-[#2E7D32]' : 'text-slate-500'
                        }`}
                      >
                        {pData?.physicalCompleted ? '✓ সম্পন্ন' : 'বাকি'}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* 3. Individual Colour & Shape Skill Mastery */}
          <div className="bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white space-y-3">
            <h4 className="font-bold text-xs text-[#243B80]">
              🎨 রং চেনার দক্ষতা (১০টি রং):
            </h4>
            <div className="grid grid-cols-5 gap-2">
              {ALL_10_COLORS.map(col => {
                const count = skillMastery.colorAccuracyMap[col.id] || 0;
                return (
                  <div key={col.id} className="flex flex-col items-center">
                    <div
                      style={{ backgroundColor: col.color, borderColor: col.darkColor }}
                      className="w-7 h-7 rounded-full border-2"
                    />
                    <span className="text-[11px] font-semibold text-[#243B80] mt-0.5">
                      {col.nameBn}
                    </span>
                    <span className="text-[11px] font-bold text-[#2E7D32]">{count}✓</span>
                  </div>
                );
              })}
            </div>

            <h4 className="font-bold text-xs text-[#243B80] pt-2">
              📐 আকৃতি চেনার দক্ষতা (২৫টি দ্বিমাত্রিক আকৃতি):
            </h4>
            <div className="grid grid-cols-5 gap-2">
              {ALL_25_SHAPES.map(sh => {
                const count = skillMastery.shapeAccuracyMap[sh.id] || 0;
                return (
                  <div key={sh.id} className="flex flex-col items-center text-center">
                    <ShapeGraphic shape={sh} color="#3978D5" sizePx={24} />
                    <span className="text-[10px] font-semibold text-[#243B80] truncate max-w-full mt-0.5">
                      {sh.nameBn}
                    </span>
                    <span className="text-[10px] font-bold text-[#2E7D32]">{count}✓</span>
                  </div>
                );
              })}
            </div>
          </div>

          {/* 4. Parent Level Control (Unlock All 100 Levels) */}
          <div className="bg-[#EAF4FF] rounded-3xl p-4 shadow border-2 border-white space-y-2">
            <h4 className="font-bold text-sm text-[#243B80]">
              অভিভাবক অনুশীলন নিয়ন্ত্রণ (১০০টি লেভেল)
            </h4>
            <p className="text-xs text-slate-600">
              আপনি চাইলে ৪টি খেলার সব ১০০টি লেভেল একসাথে খুলে দিতে পারেন অথবা অগ্রগতি রিসেট করতে পারেন।
            </p>

            {unlockSuccessBanner && (
              <div className="p-2 rounded-xl bg-[#E8F7F1] text-[#2E7D32] font-bold text-xs text-center">
                ✓ সব ১০০টি লেভেল আনলক করা হয়েছে!
              </div>
            )}

            <div className="grid grid-cols-2 gap-2.5 pt-1">
              <button
                type="button"
                data-testid="parent_unlock_all_button"
                onClick={() => {
                  audio.playSuccessChime();
                  onUnlockAllLevels();
                  setUnlockSuccessBanner(true);
                }}
                className="h-11 rounded-2xl bg-[#3978D5] text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow cursor-pointer active:scale-95"
              >
                <Unlock className="w-4 h-4" />
                <span>সব লেভেল খুলুন</span>
              </button>

              <button
                type="button"
                onClick={() => setShowResetConfirmDialog(true)}
                className="h-11 rounded-2xl bg-white text-[#E53935] border-2 border-[#FF7777] font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer active:scale-95"
              >
                <RefreshCw className="w-4 h-4" />
                <span>অগ্রগতি রিসেট</span>
              </button>
            </div>
          </div>
        </div>

        <PlayoraBottomNavBar
          currentScreen="PARENT_DASHBOARD"
          onSelectTab={target => {
            audio.playButtonTap();
            onNavigateTab(target);
          }}
        />
      </div>

      {showSettingsModal && (
        <PlayKidsSettingsDialog
          audioSettings={audioSettings}
          onUpdateAudioSettings={updated => {
            audio.updateSettings(updated);
            onUpdateAudioSettings(updated);
          }}
          onDismiss={() => setShowSettingsModal(false)}
        />
      )}

      {showResetConfirmDialog && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 max-w-sm w-full border-3 border-[#FF7777] text-center space-y-4">
            <h3 className="text-lg font-extrabold text-[#243B80]">
              আপনি কি সব অগ্রগতি রিসেট করতে চান?
            </h3>
            <p className="text-xs text-slate-600">
              এটি করলে অর্জিত তারা এবং আনলক করা লেভেল আবার প্রথম থেকে শুরু হবে।
            </p>
            <div className="flex gap-2.5">
              <button
                type="button"
                onClick={() => setShowResetConfirmDialog(false)}
                className="flex-1 h-11 rounded-2xl bg-slate-100 text-slate-700 font-bold text-xs cursor-pointer"
              >
                বাতিল
              </button>
              <button
                type="button"
                data-testid="confirm_reset_button"
                onClick={() => {
                  onResetAllProgress();
                  setShowResetConfirmDialog(false);
                  setUnlockSuccessBanner(false);
                }}
                className="flex-1 h-11 rounded-2xl bg-[#E53935] text-white font-bold text-xs cursor-pointer"
              >
                হ্যাঁ, রিসেট করুন
              </button>
            </div>
          </div>
        </div>
      )}
    </PlayoraAnimatedSceneBackground>
  );
};
