import React, { useEffect } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  BarChart3,
  Grid,
  Home,
  Lock,
  BookOpen,
  Pause,
  Play,
  RefreshCw,
  Shield,
  Star,
  Volume2,
  VolumeX,
  X,
} from 'lucide-react';
import confetti from 'canvas-confetti';
import {
  AudioSettingsState,
  LEARNING_MODES,
  LearningMode,
  ScreenType,
  toBnDigits,
} from '../types/game';
import { BrandConfig } from '../data/brand';

export type WorldTheme =
  | 'DEFAULT_SKY_HILLS'
  | 'EMOJI_DISCOVERY_WORLD'
  | 'MAGICAL_SHAPE_GARDEN'
  | 'RAINBOW_DISCOVERY_WORLD'
  | 'CREATIVE_ART_STUDIO';

export const PlayoraAnimatedSceneBackground: React.FC<{
  theme?: WorldTheme;
  reducedMotion?: boolean;
  children: React.ReactNode;
  onClick?: () => void;
  className?: string;
}> = ({ theme = 'DEFAULT_SKY_HILLS', reducedMotion = false, children, onClick, className = '' }) => {
  const bgGradient =
    theme === 'EMOJI_DISCOVERY_WORLD'
      ? 'from-[#56B4FF] via-[#9EE2FF] to-[#E6F7FF]'
      : theme === 'MAGICAL_SHAPE_GARDEN'
      ? 'from-[#4FB8FF] via-[#8FE3D9] to-[#E3FBF3]'
      : theme === 'RAINBOW_DISCOVERY_WORLD'
      ? 'from-[#6B8EFF] via-[#B4A7FF] to-[#F3E8FF]'
      : theme === 'CREATIVE_ART_STUDIO'
      ? 'from-[#FF8E9E] via-[#FFC2B4] to-[#FFF0ED]'
      : 'from-[#56B4FF] via-[#9BE0FF] to-[#EAF4FF]';

  return (
    <div
      onClick={onClick}
      className={`relative min-h-screen w-full overflow-x-hidden bg-gradient-to-b ${bgGradient} flex flex-col justify-between ${className}`}
    >
      {/* Decorative clouds and hills */}
      <div className="pointer-events-none fixed inset-0 overflow-hidden z-0">
        <div
          className={`absolute top-8 left-6 w-28 h-10 bg-white/65 rounded-full blur-[1px] ${
            !reducedMotion ? 'animate-float' : ''
          }`}
        />
        <div
          className={`absolute top-16 right-8 w-36 h-12 bg-white/60 rounded-full blur-[1px] ${
            !reducedMotion ? 'animate-float' : ''
          }`}
          style={{ animationDelay: '1.2s' }}
        />
        {/* Bottom rolling green hills */}
        <svg
          viewBox="0 0 1440 320"
          className="absolute bottom-0 left-0 w-full h-36 md:h-48 object-cover preserve-3d"
          preserveAspectRatio="none"
        >
          <path
            fill="#8CE64E"
            fillOpacity="0.8"
            d="M0,192L80,186.7C160,181,320,171,480,181.3C640,192,800,224,960,218.7C1120,213,1280,171,1360,149.3L1440,128L1440,320L1360,320C1280,320,1120,320,960,320C800,320,640,320,480,320C320,320,160,320,80,320L0,320Z"
          />
          <path
            fill="#57C828"
            fillOpacity="0.9"
            d="M0,256L80,240C160,224,320,192,480,197.3C640,203,800,245,960,250.7C1120,256,1280,224,1360,208L1440,192L1440,320L1360,320C1280,320,1120,320,960,320C800,320,640,320,480,320C320,320,160,320,80,320L0,320Z"
          />
        </svg>
      </div>

      <div className="relative z-10 flex flex-col flex-1 w-full max-w-2xl mx-auto">{children}</div>
    </div>
  );
};

export const SmilingStarMascot: React.FC<{
  size?: number;
  isHappy?: boolean;
  showBook?: boolean;
  reducedMotion?: boolean;
  className?: string;
}> = ({ size = 64, isHappy = false, showBook = true, reducedMotion = false, className = '' }) => {
  const starPoints = () => {
    const pts: string[] = [];
    const cx = 50;
    const cy = showBook ? 44 : 50;
    const outerR = showBook ? 41 : 44;
    const innerR = outerR * 0.53;
    let angle = -Math.PI / 2;
    const step = Math.PI / 5;
    for (let i = 0; i < 5; i++) {
      pts.push(`${(cx + outerR * Math.cos(angle)).toFixed(1)},${(cy + outerR * Math.sin(angle)).toFixed(1)}`);
      angle += step;
      pts.push(`${(cx + innerR * Math.cos(angle)).toFixed(1)},${(cy + innerR * Math.sin(angle)).toFixed(1)}`);
      angle += step;
    }
    return pts.join(' ');
  };

  return (
    <div
      style={{ width: size, height: size }}
      className={`inline-flex items-center justify-center shrink-0 ${
        !reducedMotion ? (isHappy ? 'animate-gentle-pulse' : 'animate-float') : ''
      } ${className}`}
    >
      <svg viewBox="0 0 100 100" className="w-full h-full drop-shadow-md">
        <defs>
          <radialGradient id="starGoldGrad" cx="40%" cy="35%" r="65%">
            <stop offset="0%" stopColor="#FFF8D6" />
            <stop offset="55%" stopColor="#FFD166" />
            <stop offset="100%" stopColor="#F59E0B" />
          </radialGradient>
          <linearGradient id="bookBlueGrad" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stopColor="#3978D5" />
            <stop offset="100%" stopColor="#243B80" />
          </linearGradient>
        </defs>
        {/* Star Body */}
        <polygon
          points={starPoints()}
          fill="url(#starGoldGrad)"
          stroke="#E09100"
          strokeWidth="3"
          strokeLinejoin="round"
        />
        {/* Eyes */}
        <circle cx="41" cy={showBook ? 42 : 48} r="3.4" fill="#243B80" />
        <circle cx="59" cy={showBook ? 42 : 48} r="3.4" fill="#243B80" />
        <circle cx="40" cy={showBook ? 41 : 47} r="1.2" fill="#FFFFFF" />
        <circle cx="58" cy={showBook ? 41 : 47} r="1.2" fill="#FFFFFF" />
        {/* Warm Smile */}
        <path
          d={
            showBook
              ? 'M 43 48 Q 50 54 57 48'
              : 'M 43 54 Q 50 60 57 54'
          }
          fill="none"
          stroke="#243B80"
          strokeWidth="3"
          strokeLinecap="round"
        />
        {/* Open Book at Base with Play Symbol */}
        {showBook && (
          <g>
            <polygon
              points="23,56 50,61 77,56 77,89 50,94 23,89"
              fill="#FFFFFF"
            />
            <polygon
              points="21,59 50,64 79,59 79,92 50,97 21,92"
              fill="url(#bookBlueGrad)"
              stroke="#FFFFFF"
              strokeWidth="2"
              strokeLinejoin="round"
            />
            <line x1="50" y1="64" x2="50" y2="97" stroke="#1E293B" strokeWidth="2.5" />
            {/* Play Triangle on Right Book Page */}
            <polygon points="60,72 69,77.5 60,83" fill="#FFFFFF" />
          </g>
        )}
      </svg>
    </div>
  );
};

export const PlayKidsBrandLogo: React.FC<{
  showTaglineRibbon?: boolean;
}> = ({ showTaglineRibbon = true }) => {
  return (
    <div className="flex flex-col items-center select-none">
      <div className="flex items-center gap-2 bg-white/95 px-4 py-1.5 rounded-full shadow-md border-2 border-white">
        <SmilingStarMascot size={36} showBook={true} />
        <div className="flex items-baseline font-extrabold tracking-tight text-2xl">
          <span className="text-[#3978D5]">{BrandConfig.BRAND_PRIMARY_PART}</span>
          <span className="text-[#F59E0B]">{BrandConfig.BRAND_SECONDARY_PART}</span>
        </div>
      </div>
      {showTaglineRibbon && (
        <div className="mt-1 bg-[#243B80] text-white text-xs font-bold px-3.5 py-0.5 rounded-full shadow border border-white/80">
          {BrandConfig.BRAND_TAGLINE_BN}
        </div>
      )}
    </div>
  );
};

export const MascotSpeechPrompt: React.FC<{
  text: string;
  onReplayAudio: () => void;
  isHighlighted?: boolean;
}> = ({ text, onReplayAudio, isHighlighted = false }) => {
  return (
    <div
      className={`w-full px-4 py-1`}
    >
      <div
        className={`flex items-center justify-between gap-3 px-3.5 py-2.5 rounded-3xl shadow-md border-2 transition-colors ${
          isHighlighted
            ? 'bg-[#FFF9E6] border-[#FFD166]'
            : 'bg-white/95 border-[#3978D5]/40'
        }`}
      >
        <SmilingStarMascot size={46} isHappy={isHighlighted} showBook={false} />
        <p className="flex-1 text-[#243B80] font-bold text-base leading-snug">{text}</p>
        <button
          type="button"
          data-testid="speaker_replay_button"
          onClick={onReplayAudio}
          className="w-12 h-12 rounded-full bg-gradient-to-b from-[#4FA3FF] to-[#2563EB] text-white flex items-center justify-center shadow-md border-2 border-white active:scale-95 transition-transform shrink-0 cursor-pointer"
          title="নির্দেশনা পুনরায় শোনো"
        >
          <Volume2 className="w-6 h-6" />
        </button>
      </div>
    </div>
  );
};

export const LearningModeSwitcherBar: React.FC<{
  currentMode: LearningMode;
  onSelectMode: (mode: LearningMode) => void;
}> = ({ currentMode, onSelectMode }) => {
  const modes: LearningMode[] = ['core', 'extended'];
  return (
    <div className="w-full px-4 py-1">
      <div className="grid grid-cols-2 gap-1.5 bg-white/90 p-1 rounded-2xl border border-[#3978D5]/30 shadow-sm">
        {modes.map(m => {
          const info = LEARNING_MODES[m];
          const isSelected = currentMode === m;
          const activeBg =
            m === 'core' ? 'bg-[#3978D5] text-white shadow' : 'bg-[#9B5DE5] text-white shadow';
          return (
            <button
              key={m}
              type="button"
              data-testid={`mode_switch_${m}`}
              onClick={() => onSelectMode(m)}
              className={`h-9 rounded-xl font-bold text-xs px-2 truncate transition-all cursor-pointer ${
                isSelected ? activeBg : 'text-[#243B80] hover:bg-slate-100'
              }`}
            >
              {info.titleBn}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export const GameTopBar: React.FC<{
  title: string;
  currentLevel: number;
  starsCount: number;
  onBackClick: () => void;
  onOpenLevelMap?: () => void;
}> = ({ title, currentLevel, starsCount, onBackClick, onOpenLevelMap }) => {
  const chapterNum = Math.min(5, Math.max(1, Math.floor((currentLevel - 1) / 5) + 1));
  const levelInChapter = ((currentLevel - 1) % 5) + 1;
  const progressPct = Math.min(100, Math.max(20, (levelInChapter / 5) * 100));
  const cleanTitle = title.includes('•') ? title.split('•')[1].trim() : title;

  return (
    <div className="w-full px-3 pt-2 pb-1 flex flex-col items-center">
      <div className="w-full flex items-center gap-2">
        <button
          type="button"
          data-testid="game_back_button"
          onClick={onBackClick}
          className="w-12 h-12 rounded-full bg-gradient-to-b from-[#4FA3FF] to-[#2563EB] text-white flex items-center justify-center shadow-md border-2 border-white active:scale-95 transition-transform shrink-0 cursor-pointer"
          title="হোমে ফিরে যাও"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>

        <button
          type="button"
          data-testid="game_level_pill"
          onClick={onOpenLevelMap}
          className="flex-1 bg-[#243B80] text-white rounded-3xl py-1.5 px-3 shadow-md border-2 border-white/90 flex flex-col items-center justify-center cursor-pointer active:scale-98"
        >
          <span className="font-extrabold text-sm sm:text-base leading-tight">
            অধ্যায় {toBnDigits(chapterNum)} : স্তর {toBnDigits(currentLevel)}
          </span>
          <span className="text-[#FFD166] font-bold text-xs leading-tight">{cleanTitle}</span>
        </button>

        <button
          type="button"
          onClick={onOpenLevelMap}
          className="h-11 px-3 rounded-2xl bg-white/95 border-2 border-[#FFD166] shadow flex items-center gap-1.5 shrink-0 cursor-pointer"
        >
          <Star className="w-5 h-5 text-[#F59E0B] fill-[#F59E0B]" />
          <span className="font-extrabold text-sm text-[#243B80]">{toBnDigits(starsCount)}</span>
        </button>
      </div>

      {/* 3-Star Chapter Progress Bar */}
      <div className="w-3/4 flex items-center gap-2 mt-1">
        <div className="flex -space-x-1">
          {[1, 2, 3].map(s => (
            <Star
              key={s}
              className={`w-4 h-4 ${
                s <= 2 || levelInChapter >= 3
                  ? 'text-[#FFB300] fill-[#FFB300]'
                  : 'text-slate-300 fill-slate-300'
              }`}
            />
          ))}
        </div>
        <div className="flex-1 h-2.5 bg-white/85 rounded-full border border-white overflow-hidden">
          <div
            className="h-full bg-gradient-to-r from-[#FFE066] to-[#F59E0B] rounded-full transition-all duration-300"
            style={{ width: `${progressPct}%` }}
          />
        </div>
      </div>
    </div>
  );
};

export const GameplayControlBar: React.FC<{
  currentLevel: number;
  maxLevel: number;
  hasPreviousLevel: boolean;
  hasNextUnlockedLevel: boolean;
  onPreviousLevel: () => void;
  onRestartLevel: () => void;
  onPauseClick: () => void;
  onNextLevel: () => void;
  onLockedNextAttempt: () => void;
}> = ({
  hasPreviousLevel,
  hasNextUnlockedLevel,
  onPreviousLevel,
  onRestartLevel,
  onPauseClick,
  onNextLevel,
  onLockedNextAttempt,
}) => {
  return (
    <div className="w-full px-3.5 py-1 grid grid-cols-4 gap-1.5">
      <button
        type="button"
        data-testid="nav_prev_level_btn"
        disabled={!hasPreviousLevel}
        onClick={onPreviousLevel}
        className={`h-10 rounded-xl font-bold text-xs flex items-center justify-center gap-1 border-2 transition-all ${
          hasPreviousLevel
            ? 'bg-white text-[#243B80] border-[#3978D5]/40 shadow-sm active:scale-95 cursor-pointer'
            : 'bg-white/50 text-slate-400 border-slate-200 cursor-not-allowed'
        }`}
      >
        <ArrowLeft className="w-4 h-4" />
        <span>আগেরটি</span>
      </button>

      <button
        type="button"
        data-testid="nav_restart_level_btn"
        onClick={onRestartLevel}
        className="h-10 rounded-xl bg-white text-[#243B80] border-2 border-[#3978D5]/40 shadow-sm font-bold text-xs flex items-center justify-center gap-1 active:scale-95 cursor-pointer"
      >
        <RefreshCw className="w-4 h-4" />
        <span>রিস্টার্ট</span>
      </button>

      <button
        type="button"
        data-testid="nav_pause_btn"
        onClick={onPauseClick}
        className="h-10 rounded-xl bg-[#FFD166] text-[#243B80] border-2 border-[#F59E0B] shadow-sm font-bold text-xs flex items-center justify-center gap-1 active:scale-95 cursor-pointer"
      >
        <Pause className="w-4 h-4" />
        <span>বিরতি</span>
      </button>

      <button
        type="button"
        data-testid="nav_next_level_btn"
        onClick={() => {
          if (hasNextUnlockedLevel) {
            onNextLevel();
          } else {
            onLockedNextAttempt();
          }
        }}
        className={`h-10 rounded-xl font-bold text-xs flex items-center justify-center gap-1 border-2 transition-all cursor-pointer ${
          hasNextUnlockedLevel
            ? 'bg-[#56BFA4] text-white border-white shadow-sm active:scale-95'
            : 'bg-white/75 text-slate-500 border-slate-300'
        }`}
      >
        <span>পরেরটি</span>
        {hasNextUnlockedLevel ? <ArrowRight className="w-4 h-4" /> : <Lock className="w-3.5 h-3.5" />}
      </button>
    </div>
  );
};

export const GamePauseDialog: React.FC<{
  levelNumber: number;
  gameTitle?: string;
  onResume: () => void;
  onRestart: () => void;
  onOpenLevelMap: () => void;
  onGoHome: () => void;
}> = ({
  levelNumber,
  gameTitle = BrandConfig.APPROVED_BRAND_NAME,
  onResume,
  onRestart,
  onOpenLevelMap,
  onGoHome,
}) => {
  return (
    <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl border-4 border-[#3978D5] shadow-2xl max-w-sm w-full p-6 flex flex-col items-center text-center animate-in fade-in zoom-in-95 duration-200">
        <SmilingStarMascot size={72} showBook={true} />
        <h3 className="mt-3 text-xl font-extrabold text-[#243B80]">খেলা বিরতিতে আছে</h3>
        <p className="text-sm font-bold text-slate-600 mt-0.5">
          {gameTitle} • স্তর {toBnDigits(levelNumber)}
        </p>

        <div className="w-full space-y-2.5 mt-5">
          <button
            type="button"
            data-testid="pause_resume_btn"
            onClick={onResume}
            className="w-full h-13 rounded-2xl bg-gradient-to-r from-[#22C55E] to-[#16A34A] text-white font-extrabold text-base shadow-md flex items-center justify-center gap-2 border-2 border-white cursor-pointer active:scale-95"
          >
            <Play className="w-5 h-5 fill-white" />
            <span>খেলা চালিয়ে যাও</span>
          </button>

          <button
            type="button"
            data-testid="pause_restart_btn"
            onClick={onRestart}
            className="w-full h-12 rounded-2xl bg-white text-[#243B80] font-bold text-sm border-2 border-[#3978D5] flex items-center justify-center gap-2 cursor-pointer active:scale-95"
          >
            <RefreshCw className="w-4 h-4" />
            <span>এই স্তর আবার শুরু করো</span>
          </button>

          <button
            type="button"
            data-testid="pause_level_map_btn"
            onClick={onOpenLevelMap}
            className="w-full h-12 rounded-2xl bg-white text-[#9B5DE5] font-bold text-sm border-2 border-[#9B5DE5] flex items-center justify-center gap-2 cursor-pointer active:scale-95"
          >
            <Grid className="w-4 h-4" />
            <span>লেভেল ম্যাপ দেখো</span>
          </button>

          <button
            type="button"
            data-testid="pause_home_btn"
            onClick={onGoHome}
            className="w-full h-12 rounded-2xl bg-white text-[#E53935] font-bold text-sm border-2 border-[#FF7777] flex items-center justify-center gap-2 cursor-pointer active:scale-95"
          >
            <Home className="w-4 h-4" />
            <span>হোম স্ক্রিনে ফিরে যাও</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export const GameSuccessDialog: React.FC<{
  levelNumber: number;
  starsAwarded?: number;
  titleText?: string;
  messageText?: string;
  onDismiss: () => void;
  onNextLevel: () => void;
  onGoPhysicalPractice: () => void;
}> = ({
  levelNumber,
  starsAwarded = 3,
  titleText = 'দারুণ হয়েছে!',
  messageText = 'এই স্তরটি সফলভাবে সম্পন্ন হয়েছে। চলো, পরের ধাপে যাই!',
  onDismiss,
  onNextLevel,
  onGoPhysicalPractice,
}) => {
  useEffect(() => {
    try {
      confetti({
        particleCount: 55,
        spread: 65,
        origin: { y: 0.6 },
      });
    } catch {
      // ignore
    }
  }, []);

  return (
    <div className="fixed inset-0 z-50 bg-black/55 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl border-4 border-[#FFD166] shadow-2xl max-w-sm w-full p-6 flex flex-col items-center text-center">
        <SmilingStarMascot size={84} isHappy={true} showBook={true} />

        <div className="flex items-center justify-center gap-2 my-2">
          {[1, 2, 3].map(s => (
            <Star
              key={s}
              className={`w-9 h-9 ${
                s <= starsAwarded
                  ? 'text-[#FFB300] fill-[#FFB300] drop-shadow'
                  : 'text-slate-300 fill-slate-200'
              }`}
            />
          ))}
        </div>

        <span className="px-4 py-1 rounded-full bg-gradient-to-r from-[#9B5DE5] to-[#7C3AED] text-white font-extrabold text-sm shadow">
          অধ্যায় সম্পন্ন! • স্তর {toBnDigits(levelNumber)}
        </span>

        <h3 className="mt-2.5 text-2xl font-extrabold text-[#243B80]">{titleText}</h3>
        <p className="text-sm font-bold text-slate-600 mt-1">{messageText}</p>

        <div className="w-full space-y-2.5 mt-5">
          <button
            type="button"
            data-testid="success_replay_btn"
            onClick={onDismiss}
            className="w-full h-12 rounded-full bg-gradient-to-r from-[#8B5CF6] to-[#6D28D9] text-white font-extrabold text-base shadow-md flex items-center justify-center gap-2 border-2 border-white cursor-pointer active:scale-95"
          >
            <span>আবার খেলি</span>
            <RefreshCw className="w-4 h-4" />
          </button>

          <button
            type="button"
            data-testid="success_next_level_btn"
            onClick={onNextLevel}
            className="w-full h-13 rounded-full bg-gradient-to-r from-[#FFE066] to-[#F59E0B] text-[#243B80] font-extrabold text-base shadow-md flex items-center justify-center gap-2 border-2 border-white cursor-pointer active:scale-95"
          >
            <span>পরের স্তর</span>
            <ArrowRight className="w-5 h-5" />
          </button>

          <button
            type="button"
            data-testid="success_physical_btn"
            onClick={onGoPhysicalPractice}
            className="w-full h-11 rounded-full bg-[#FFF9E6] text-[#243B80] font-bold text-xs border-2 border-[#FFD166] flex items-center justify-center gap-2 cursor-pointer active:scale-95"
          >
            <span>🎯 এবার বাস্তবে খেলি</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export const PlayoraBottomNavBar: React.FC<{
  currentScreen: ScreenType;
  onSelectTab: (screen: ScreenType) => void;
}> = ({ currentScreen, onSelectTab }) => {
  const tabs: { screen: ScreenType; labelBn: string; icon: React.ReactNode; testId: string }[] = [
    {
      screen: 'HOME',
      labelBn: 'হোম',
      icon: <Home className="w-5 h-5" />,
      testId: 'bottom_nav_home',
    },
    {
      screen: 'MY_PROGRESS',
      labelBn: 'অগ্রগতি',
      icon: <BarChart3 className="w-5 h-5" />,
      testId: 'bottom_nav_progress',
    },
    {
      screen: 'PHYSICAL_PRACTICE',
      labelBn: 'অনুশীলন',
      icon: <BookOpen className="w-5 h-5" />,
      testId: 'bottom_nav_physical',
    },
    {
      screen: 'PARENT_DASHBOARD',
      labelBn: 'অভিভাবক',
      icon: <Shield className="w-5 h-5" />,
      testId: 'bottom_nav_parent',
    },
  ];

  return (
    <div className="w-full px-3 pb-3 pt-1">
      <div className="bg-white/95 border-2 border-white shadow-lg rounded-3xl p-1.5 grid grid-cols-4 gap-1.5">
        {tabs.map(t => {
          const isSelected = currentScreen === t.screen;
          return (
            <button
              key={t.screen}
              type="button"
              data-testid={t.testId}
              onClick={() => onSelectTab(t.screen)}
              className={`py-2 px-1 rounded-2xl flex flex-col items-center justify-center gap-0.5 font-extrabold text-xs transition-all cursor-pointer ${
                isSelected
                  ? 'bg-gradient-to-b from-[#3978D5] to-[#243B80] text-white shadow'
                  : 'text-[#243B80] hover:bg-slate-100'
              }`}
            >
              {t.icon}
              <span>{t.labelBn}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
};

export const PlayoraIllustratedPictureBadge: React.FC<{
  symbol: string;
  badgeColor: string;
  sizePx?: number;
}> = ({ symbol, badgeColor, sizePx = 56 }) => {
  return (
    <div
      style={{ width: sizePx, height: sizePx, backgroundColor: badgeColor }}
      className="rounded-2xl border-2 border-white shadow-inner flex items-center justify-center select-none shrink-0"
    >
      <span style={{ fontSize: Math.round(sizePx * 0.58) }} className="leading-none">
        {symbol}
      </span>
    </div>
  );
};

export const PlayKidsSettingsDialog: React.FC<{
  audioSettings: AudioSettingsState;
  onUpdateAudioSettings: (s: AudioSettingsState) => void;
  onDismiss: () => void;
}> = ({ audioSettings, onUpdateAudioSettings, onDismiss }) => {
  return (
    <div className="fixed inset-0 z-50 bg-black/55 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-gradient-to-b from-[#EAF4FF] to-[#F7F8FC] rounded-3xl border-4 border-white shadow-2xl max-w-md w-full overflow-hidden">
        {/* Screen 12 Purple Header matching PDF Reference */}
        <div className="bg-gradient-to-r from-[#7C3AED] via-[#8B5CF6] to-[#6D28D9] px-4 py-3.5 flex items-center justify-between text-white shadow-md">
          <button
            type="button"
            onClick={onDismiss}
            className="w-10 h-10 rounded-full bg-white/20 hover:bg-white/30 text-white flex items-center justify-center border border-white/60 active:scale-95 transition-transform cursor-pointer"
            title="ফিরে যাও"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h3 className="text-xl font-extrabold tracking-wide">সেটিংস</h3>
          <button
            type="button"
            onClick={onDismiss}
            className="w-10 h-10 rounded-full bg-white/20 hover:bg-white/30 text-white flex items-center justify-center border border-white/60 cursor-pointer"
            title="বন্ধ করো"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-5 space-y-3">
          {/* 1. Voice Guidance */}
          <label className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white cursor-pointer">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#EAF4FF] text-[#3987D5] flex items-center justify-center font-extrabold">
                {audioSettings.voiceEnabled ? (
                  <Volume2 className="w-5 h-5 text-[#3987D5]" />
                ) : (
                  <VolumeX className="w-5 h-5 text-slate-400" />
                )}
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">কণ্ঠ নির্দেশনা</div>
                <div className="text-xs font-semibold text-[#778197]">
                  মৃদু ও মিষ্টি বাংলা কণ্ঠস্বর (১০–১৪ বছর)
                </div>
              </div>
            </div>
            <input
              type="checkbox"
              checked={audioSettings.voiceEnabled}
              onChange={e =>
                onUpdateAudioSettings({ ...audioSettings, voiceEnabled: e.target.checked })
              }
              className="w-6 h-6 accent-[#22C55E] cursor-pointer"
            />
          </label>

          {/* 2. Sound Effects */}
          <label className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white cursor-pointer">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#F3E8FF] text-[#9B5DE5] flex items-center justify-center font-extrabold">
                <Volume2 className="w-5 h-5" />
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">সাউন্ড ইফেক্ট</div>
                <div className="text-xs font-semibold text-[#778197]">
                  ট্যাপ, কার্ড ও পুরস্কারের শব্দ
                </div>
              </div>
            </div>
            <input
              type="checkbox"
              checked={audioSettings.soundEffectsEnabled}
              onChange={e =>
                onUpdateAudioSettings({ ...audioSettings, soundEffectsEnabled: e.target.checked })
              }
              className="w-6 h-6 accent-[#22C55E] cursor-pointer"
            />
          </label>

          {/* 3. Background Music */}
          <label className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white cursor-pointer">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#EAF4FF] text-[#3987D5] flex items-center justify-center font-extrabold">
                🎵
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">পটভূমি সংগীত</div>
                <div className="text-xs font-semibold text-[#778197]">
                  মৃদু আবহ সংগীত (ঐচ্ছিক)
                </div>
              </div>
            </div>
            <input
              type="checkbox"
              checked={audioSettings.backgroundMusicEnabled}
              onChange={e =>
                onUpdateAudioSettings({
                  ...audioSettings,
                  backgroundMusicEnabled: e.target.checked,
                })
              }
              className="w-6 h-6 accent-[#22C55E] cursor-pointer"
            />
          </label>

          {/* 4. Language row matching Screen 12 */}
          <div className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#EAF4FF] text-[#243B80] flex items-center justify-center font-extrabold text-sm">
                অ/A
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">ভাষা</div>
                <div className="text-xs font-semibold text-[#778197]">খেলার নির্দেশনা ও ইন্টারফেস</div>
              </div>
            </div>
            <span className="px-3.5 py-1.5 rounded-xl bg-[#EAF4FF] text-[#173B78] font-extrabold text-sm border border-[#3987D5]/30">
              বাংলা ▾
            </span>
          </div>

          {/* 5. Offline Mode row matching Screen 12 */}
          <div className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#DCFCE7] text-[#16A34A] flex items-center justify-center font-extrabold">
                ⬇
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">অফলাইন মোড</div>
                <div className="text-xs font-bold text-[#16A34A]">ইনস্টল করা আছে (১১৬টি ভয়েস ফাইল)</div>
              </div>
            </div>
            <span className="w-7 h-7 rounded-full bg-[#22C55E] text-white flex items-center justify-center font-bold text-sm shadow">
              ✓
            </span>
          </div>

          {/* 6. Reduced Motion */}
          <label className="flex items-center justify-between p-3.5 rounded-2xl bg-white shadow-sm border-2 border-white cursor-pointer">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#FFF9E6] text-[#D97706] flex items-center justify-center font-extrabold">
                ✨
              </div>
              <div>
                <div className="font-extrabold text-base text-[#263247]">অ্যানিমেশন নিয়ন্ত্রণ</div>
                <div className="text-xs font-semibold text-[#778197]">কম নড়াচড়া (Reduced Motion)</div>
              </div>
            </div>
            <input
              type="checkbox"
              checked={audioSettings.reducedMotion}
              onChange={e =>
                onUpdateAudioSettings({ ...audioSettings, reducedMotion: e.target.checked })
              }
              className="w-6 h-6 accent-[#22C55E] cursor-pointer"
            />
          </label>

          <button
            type="button"
            onClick={onDismiss}
            className="mt-2 w-full h-12 rounded-2xl bg-gradient-to-r from-[#3987D5] to-[#173B78] text-white font-extrabold text-base shadow-md cursor-pointer active:scale-95 transition-transform"
          >
            সংরক্ষণ করুন ও ফিরে যান
          </button>
        </div>
      </div>
    </div>
  );
};
