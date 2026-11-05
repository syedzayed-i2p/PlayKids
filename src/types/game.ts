export type LearningMode = 'core' | 'extended';

export interface LearningModeInfo {
  id: LearningMode;
  titleBn: string;
  badgeBn: string;
  subtitleBn: string;
}

export const LEARNING_MODES: Record<LearningMode, LearningModeInfo> = {
  core: {
    id: 'core',
    titleBn: 'মূল প্রতিযোগিতা অনুশীলন',
    badgeBn: 'মোড ক • মূল পাঠ্যক্রম',
    subtitleBn: 'জাতীয় প্রতিযোগিতার ৪টি মূল খেলা ও নিয়ম',
  },
  extended: {
    id: 'extended',
    titleBn: 'অতিরিক্ত দক্ষতা অনুশীলন',
    badgeBn: 'মোড খ • ১০০+ লেভেল',
    subtitleBn: '৫টি অধ্যায়, ২৫টি করে লেভেল, ২৫টি আকৃতি ও ২০০টি চ্যালেঞ্জ',
  },
};

export type GameTypeId = 'emoji_match' | 'shape_match' | 'color_shape_challenge' | 'color_paper_match';

export interface GameTypeInfo {
  id: GameTypeId;
  serialName: string;
  titleEn: string;
  titleBn: string;
  shortSubtitle: string;
  themeColor: string; // hex
  lightBg: string;
  accentBorder: string;
}

export const GAME_TYPES: Record<GameTypeId, GameTypeInfo> = {
  emoji_match: {
    id: 'emoji_match',
    serialName: 'খেলা-ক',
    titleEn: 'Emoji Matching',
    titleBn: 'ছবি মিলাও',
    shortSubtitle: 'একই ছবি খুঁজে বের করো',
    themeColor: '#3978D5',
    lightBg: '#FFF8D6',
    accentBorder: '#F59E0B',
  },
  shape_match: {
    id: 'shape_match',
    serialName: 'খেলা-খ',
    titleEn: 'Shape Matching',
    titleBn: 'আকৃতি মিলাও',
    shortSubtitle: 'সঠিক আকৃতি চিনে মিলাও',
    themeColor: '#56BFA4',
    lightBg: '#DFFAF0',
    accentBorder: '#22C55E',
  },
  color_shape_challenge: {
    id: 'color_shape_challenge',
    serialName: 'খেলা-ঘ',
    titleEn: 'Colour + Shape',
    titleBn: 'রং ও আকৃতি',
    shortSubtitle: 'রং ও আকৃতি মিলিয়ে খেলা',
    themeColor: '#9B5DE5',
    lightBg: '#F3E8FF',
    accentBorder: '#A855F7',
  },
  color_paper_match: {
    id: 'color_paper_match',
    serialName: 'খেলা-গ',
    titleEn: 'Colour Paper',
    titleBn: 'রঙিন কাগজ মিলাও',
    shortSubtitle: 'একই রঙের কাগজ বক্সে রাখো',
    themeColor: '#FF7777',
    lightBg: '#FFE5EC',
    accentBorder: '#EC4899',
  },
};

export interface KidColor {
  id: string;
  nameBn: string;
  color: string;
  lightColor: string;
  darkColor: string;
  isCore: boolean;
  patternSymbol: string;
}

export interface KidShape {
  id: string;
  nameBn: string;
  isCore: boolean;
}

export interface Kid3DShape {
  id: string;
  nameBn: string;
  realWorldExampleBn: string;
  descriptionBn: string;
  primaryColor: string;
}

export type PictureCategory =
  | 'CORE_EMOJI'
  | 'OPTIONAL_EMOJI'
  | 'FRUITS'
  | 'ANIMALS'
  | 'NATURE'
  | 'VEHICLES'
  | 'DAILY_OBJECTS';

export interface PictureCardItem {
  id: string;
  symbol: string;
  nameBn: string;
  category: PictureCategory;
  badgeColor: string;
}

export interface ColorShapeCard {
  id: string;
  color: KidColor;
  shape: KidShape;
  nameBn: string;
}

export interface ChapterInfo {
  chapterNumber: number;
  titleEn: string;
  subtitleEn: string;
  titleBn: string;
  subtitleBn: string;
  levelStart: number;
  levelEnd: number;
  themeColor: string;
  badgeIcon: string;
}

export interface LevelDefinition {
  gameType: GameTypeId;
  levelNumber: number; // 1..25
  chapterNumber: number; // 1..5
  titleBn: string;
  objectiveBn: string;
  voiceInstructionBn: string;
  taskCount: number;
  optionCount: number;
  isCoreCurriculumCompatible: boolean;
}

export interface AudioSettingsState {
  voiceEnabled: boolean;
  soundEffectsEnabled: boolean;
  backgroundMusicEnabled: boolean;
  reducedMotion: boolean;
}

export interface SkillMasterySnapshot {
  colorAccuracyMap: Record<string, number>;
  shapeAccuracyMap: Record<string, number>;
}

export interface GameProgressData {
  gameType: GameTypeId;
  unlockedLevel: number; // Core mode level (1..5)
  coreCompletedLevels: number[];
  extendedUnlockedLevel: number; // Extended mode level (1..25)
  completedLevels: number[];
  starsEarned: number;
  totalPlayed: number;
  independentSuccesses: number;
  hintsUsed: number;
  physicalCompleted: boolean;
}

export type ScreenType =
  | 'SPLASH'
  | 'WELCOME'
  | 'HOME'
  | 'MY_PROGRESS'
  | 'LEVEL_MAP'
  | 'GAME_EMOJI'
  | 'GAME_SHAPE'
  | 'GAME_COLOR_SHAPE'
  | 'GAME_COLOR_PAPER'
  | 'PHYSICAL_PRACTICE'
  | 'PARENT_DASHBOARD';

export function toBnDigits(num: number | string): string {
  const bnDigits = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];
  return num
    .toString()
    .split('')
    .map(ch => {
      const parsed = parseInt(ch, 10);
      return !isNaN(parsed) ? bnDigits[parsed] : ch;
    })
    .join('');
}
