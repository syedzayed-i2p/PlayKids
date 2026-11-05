import React, { useEffect, useState } from 'react';
import { ArrowLeft, CheckCircle2, Circle, Volume2 } from 'lucide-react';
import { BanglaAudioEngine } from '../audio/BanglaAudioEngine';
import {
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  ScreenType,
} from '../types/game';
import { BrandConfig } from '../data/brand';
import { CORE_COLORS, CORE_SHAPES } from '../data/colors';
import {
  PlayoraAnimatedSceneBackground,
  PlayoraBottomNavBar,
  PlayoraIllustratedPictureBadge,
} from '../components/MascotAndHeader';
import { ShapeGraphic } from '../components/ShapeDrawables';

export const PhysicalPracticeScreen: React.FC<{
  initialGameType: GameTypeId;
  allProgress: Record<GameTypeId, GameProgressData>;
  audio: BanglaAudioEngine;
  onBack: () => void;
  onTogglePhysicalDone: (gameType: GameTypeId, done: boolean) => void;
  onNavigateTab: (target: ScreenType) => void;
}> = ({ initialGameType, allProgress, audio, onBack, onTogglePhysicalDone, onNavigateTab }) => {
  const [selectedGame, setSelectedGame] = useState<GameTypeId>(initialGameType);
  const currentProgress = allProgress[selectedGame];
  const isDone = !!currentProgress?.physicalCompleted;
  const gInfo = GAME_TYPES[selectedGame];

  const guidePrompt = 'এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।';

  useEffect(() => {
    audio.speak(guidePrompt, `phys_${selectedGame}`, false, true);
  }, [selectedGame, audio]);

  const gameIds = Object.keys(GAME_TYPES) as GameTypeId[];

  const equipmentList: Record<GameTypeId, string[]> = {
    emoji_match: [
      '১০ থেকে ১২ জোড়া বড় সাইজের লেমিনেটেড ইমোজি কার্ড (😀, ❤️, 👍, ⭐, 😂, 😎, 🌸, ⚽, 🍎)',
      'অতিরিক্ত অনুশীলনের জন্য ফল, প্রাণী ও যানবাহনের ছবির ফ্ল্যাশকার্ড',
      '১টি সমতল টেবিল বা ফ্লোর ম্যাট এবং ১টি কার্ড রাখার ট্রে',
    ],
    shape_match: [
      'কমপক্ষে ৫ ধরনের বড় শেইপ কাটআউট (বৃত্ত, ত্রিভুজ, চতুর্ভুজ, তারা, হার্ট)',
      'অতিরিক্ত অনুশীলনের জন্য আয়তক্ষেত্র, ডিম্বাকৃতি, হীরক, পঞ্চভুজ ও ষড়ভুজের কার্ডবোর্ড শেইপ',
      '১টি Matching Board বা আউটলাইন আঁকা আর্ট পেপার এবং ১টি শেইপ রাখার ট্রে',
    ],
    color_shape_challenge: [
      '৫টি মূল রঙের (লাল, হলুদ, নীল, সবুজ, কমলা) × ৫টি আকৃতির পূর্ণ ২৫টি লেমিনেটেড কম্বিনেশন কার্ড',
      'অতিরিক্ত ধাপের জন্য গোলাপি, বেগুনি, বাদামি, কালো ও সাদা রঙের অতিরিক্ত শেইপ কার্ড',
      '১টি কার্ড রাখার ট্রে এবং সমতল টেবিল',
    ],
    color_paper_match: [
      'লাল, হলুদ, নীল, সবুজ ও কমলা রঙের ৫টি বড় কালার পেপার বা কার্ড',
      'একই ৫টি রঙের ৫টি আলাদা ট্রে বা রঙিন ম্যাট',
      'উন্নত স্তরের জন্য গোলাপি, বেগুনি, বাদামি, কালো ও সাদা রঙের কাগজ',
    ],
  };

  const stepsList: Record<GameTypeId, string[]> = {
    emoji_match: [
      'ধাপ ১ (টেবিল সাজানো): টেবিলে ইমোজি কার্ডের একটি সেট সারিবদ্ধভাবে সাজিয়ে রাখুন।',
      'ধাপ ২ (কার্ড তোলা): শিশু ট্রে থেকে অপর সেটের একটি ইমোজি কার্ড হাতে নেবে।',
      'ধাপ ৩ (খোঁজা ও মেলানো): টেবিলে থাকা কার্ডগুলোর মধ্যে একই ইমোজি খুঁজে পাশাপাশি রাখবে।',
      'বড়দের টিপস: শুরুতে ৩-৪ জোড়া দিয়ে শুরু করুন এবং শিশু পারলে হাততালি দিয়ে উৎসাহ দিন।',
    ],
    shape_match: [
      'ধাপ ১ (পরিচিতি): প্রথমে সব শেইপ হাতে নিয়ে নাম বলে শিশুকে দেখান।',
      'ধাপ ২ (শেইপ হাতে নেওয়া): শিশু ট্রে থেকে একটি শেইপ (যেমন: ত্রিভুজ) হাতে নেবে।',
      'ধাপ ৩ (সঠিক ঘর খোঁজা ও বসানো): ম্যাচিং বোর্ডে একই শেইপের ঘরের ওপর সঠিকভাবে বসাবে।',
      'বড়দের টিপস: ঘরের কোণা ও বাহুগুলো আঙুল দিয়ে ছুঁয়ে দেখতে উৎসাহিত করুন।',
    ],
    color_shape_challenge: [
      "পর্যায় ১ (শুধু রং): বলুন 'লাল রং খুঁজে দাও!' শিশু যেকোনো আকৃতির লাল কার্ড দেবে।",
      "পর্যায় ২ (শুধু আকৃতি): বলুন 'তারা দাও!' শিশু যেকোনো রঙের তারা কার্ড দেবে।",
      "পর্যায় ৩ (রং + আকৃতি একসঙ্গে): বলুন 'সবুজ ত্রিভুজ দাও!' বা 'লাল তারা দাও!' শিশু উভয় বৈশিষ্ট্য মিলিয়ে সঠিক কার্ডটি তুলে দেবে।",
    ],
    color_paper_match: [
      'ধাপ ১ (ট্রে সাজানো): লাল, হলুদ, নীল, সবুজ ও কমলা রঙের ৫টি ট্রে পাশাপাশি সাজিয়ে রাখুন।',
      'ধাপ ২ (কাগজ তোলা): শিশু একটি রঙিন কাগজ হাতে নেবে এবং রঙের নাম বলবে।',
      'ধাপ ৩ (সঠিক ঘরে রাখা): একই রঙের ট্রে খুঁজে কাগজটি সেই ট্রের ভেতর রাখবে।',
      'বড়দের টিপস: কয়েকবার খেলার পর ট্রের জায়গা অদলবদল করে দিন যাতে শিশু রঙ চিনে রাখে।',
    ],
  };

  return (
    <PlayoraAnimatedSceneBackground
      theme="DEFAULT_SKY_HILLS"
      reducedMotion={audio.getSettings().reducedMotion}
    >
      <div className="flex-1 flex flex-col justify-between">
        {/* Top Bar */}
        <div className="flex items-center gap-2.5 px-3.5 pt-3 pb-2">
          <button
            type="button"
            data-testid="physical_back_button"
            onClick={() => {
              audio.playButtonTap();
              onBack();
            }}
            className="w-12 h-12 rounded-full bg-[#3978D5] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer shrink-0"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>

          <div className="flex-1 bg-[#56BFA4] text-white rounded-3xl py-1.5 px-4 shadow-md border-2 border-white text-center">
            <div className="font-extrabold text-base">
              {BrandConfig.APPROVED_BRAND_NAME} • বাস্তবে খেলি
            </div>
            <div className="font-bold text-xs text-white/95">{guidePrompt}</div>
          </div>

          <button
            type="button"
            onClick={() => audio.replayInstruction(guidePrompt)}
            className="w-12 h-12 rounded-full bg-[#3978D5] text-white flex items-center justify-center shadow-md border-2 border-white cursor-pointer shrink-0"
          >
            <Volume2 className="w-6 h-6" />
          </button>
        </div>

        {/* 4 Game Selector Tabs */}
        <div className="px-3.5 py-1 grid grid-cols-4 gap-1.5">
          {gameIds.map(id => {
            const gType = GAME_TYPES[id];
            const isSelected = id === selectedGame;
            return (
              <button
                key={id}
                type="button"
                onClick={() => {
                  audio.playButtonTap();
                  setSelectedGame(id);
                }}
                style={{
                  backgroundColor: isSelected ? gType.themeColor : 'rgba(255,255,255,0.9)',
                }}
                className={`h-10 rounded-2xl font-bold text-xs border transition-all cursor-pointer ${
                  isSelected
                    ? 'text-white border-white shadow'
                    : 'text-[#243B80] border-slate-200'
                }`}
              >
                {gType.serialName}
              </button>
            );
          })}
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 px-4 py-2 space-y-3 overflow-y-auto">
          {/* Header & Visual Arrangement Diagram */}
          <div className="bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white">
            <div className="flex items-center justify-between">
              <div>
                <div
                  style={{ color: gInfo.themeColor }}
                  className="font-extrabold text-xs uppercase"
                >
                  {gInfo.titleEn}
                </div>
                <div className="font-extrabold text-lg text-[#243B80]">
                  {gInfo.serialName}: {gInfo.titleBn}
                </div>
              </div>
              {isDone && (
                <span className="bg-[#43A047] text-white font-bold text-xs px-3 py-1 rounded-xl">
                  ✓ সম্পন্ন
                </span>
              )}
            </div>

            <div
              style={{
                backgroundColor: gInfo.lightBg,
                borderColor: gInfo.accentBorder,
              }}
              className="mt-3 rounded-2xl p-3 border-2 flex flex-col items-center"
            >
              <span className="font-bold text-xs text-[#243B80]">
                📐 টেবিলে উপকরণ সাজানোর নকশা:
              </span>
              <div className="flex items-center justify-center gap-2.5 mt-2 flex-wrap">
                {selectedGame === 'emoji_match' &&
                  ['😀', '❤️', '⭐', '🌸', '🍎'].map(em => (
                    <PlayoraIllustratedPictureBadge
                      key={em}
                      symbol={em}
                      badgeColor="#FFF9E6"
                      sizePx={42}
                    />
                  ))}
                {selectedGame === 'shape_match' &&
                  CORE_SHAPES.map(sh => (
                    <ShapeGraphic key={sh.id} shape={sh} color="#56BFA4" sizePx={36} />
                  ))}
                {selectedGame === 'color_paper_match' &&
                  CORE_COLORS.map(col => (
                    <div
                      key={col.id}
                      style={{ backgroundColor: col.color, borderColor: col.darkColor }}
                      className="w-11 h-8 rounded-lg border-2"
                    />
                  ))}
                {selectedGame === 'color_shape_challenge' && (
                  <>
                    <ShapeGraphic shape={CORE_SHAPES[3]} color="#E53935" sizePx={36} />
                    <ShapeGraphic shape={CORE_SHAPES[1]} color="#F57F17" sizePx={36} />
                    <ShapeGraphic shape={CORE_SHAPES[4]} color="#1E88E5" sizePx={36} />
                    <ShapeGraphic shape={CORE_SHAPES[0]} color="#43A047" sizePx={36} />
                    <ShapeGraphic shape={CORE_SHAPES[2]} color="#FB8C00" sizePx={36} />
                  </>
                )}
              </div>
            </div>
          </div>

          {/* Equipment List Section */}
          <div className="bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white">
            <h3 className="font-extrabold text-sm text-[#243B80]">
              📦 প্রয়োজনীয় উপকরণ ও প্রস্তুতি:
            </h3>
            <ul className="mt-2 space-y-1.5">
              {equipmentList[selectedGame].map((item, idx) => (
                <li key={idx} className="flex items-start gap-2 text-xs font-medium text-[#243B80]">
                  <span style={{ color: gInfo.themeColor }} className="font-black text-base leading-none">
                    •
                  </span>
                  <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Step-by-Step Execution Guide */}
          <div className="bg-white/95 rounded-3xl p-4 shadow-md border-2 border-white">
            <h3 className="font-extrabold text-sm text-[#3978D5]">
              🎯 বাস্তব খেলার ধাপসমূহ ও বড়দের টিপস:
            </h3>
            <div className="mt-2 space-y-2">
              {stepsList[selectedGame].map((step, idx) => (
                <div
                  key={idx}
                  className="bg-[#EAF4FF]/70 rounded-2xl p-2.5 flex items-start gap-2.5"
                >
                  <span
                    style={{ backgroundColor: gInfo.themeColor }}
                    className="w-6 h-6 rounded-full text-white font-bold text-xs flex items-center justify-center shrink-0"
                  >
                    {idx + 1}
                  </span>
                  <span className="text-xs font-semibold text-[#243B80]">{step}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Parent Confirmation Card */}
          <div
            className={`rounded-3xl p-4 shadow-md border-2 border-white flex flex-col items-center ${
              isDone ? 'bg-[#E8F7F1]' : 'bg-[#FFF9E6]'
            }`}
          >
            <span className="font-bold text-xs text-[#243B80]">
              অভিভাবক / শিক্ষকের নিশ্চিতকরণ
            </span>
            <button
              type="button"
              data-testid="physical_confirm_button"
              onClick={() => {
                const nextState = !isDone;
                onTogglePhysicalDone(selectedGame, nextState);
                if (nextState) {
                  audio.playSuccessChime();
                  audio.speak('খুব ভালো হয়েছে!');
                }
              }}
              className={`mt-2 w-full h-13 rounded-2xl font-extrabold text-base text-white shadow flex items-center justify-center gap-2 cursor-pointer active:scale-95 transition-transform ${
                isDone ? 'bg-[#43A047]' : 'bg-[#3978D5]'
              }`}
            >
              {isDone ? (
                <CheckCircle2 className="w-6 h-6" />
              ) : (
                <Circle className="w-6 h-6" />
              )}
              <span>{isDone ? 'আমরা অনুশীলন করেছি ✓' : 'আমরা অনুশীলন করেছি'}</span>
            </button>
          </div>
        </div>

        <PlayoraBottomNavBar
          currentScreen="PHYSICAL_PRACTICE"
          onSelectTab={target => {
            audio.playButtonTap();
            onNavigateTab(target);
          }}
        />
      </div>
    </PlayoraAnimatedSceneBackground>
  );
};
