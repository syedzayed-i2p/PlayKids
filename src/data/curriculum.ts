import {
  ChapterInfo,
  ColorShapeCard,
  GameTypeId,
  LevelDefinition,
  PictureCardItem,
} from '../types/game';
import { ALL_10_COLORS, ALL_20_SHAPES, CORE_COLORS, CORE_SHAPES } from './colors';

export const CORE_EMOJIS: [string, string][] = [
  ['😀', 'হাসিমুখ'],
  ['❤️', 'হার্ট'],
  ['👍', 'থাম্বস আপ'],
  ['⭐', 'তারা'],
  ['😂', 'হাসতে হাসতে চোখে পানি'],
  ['😎', 'সানগ্লাস মুখ'],
  ['🌸', 'ফুল'],
  ['⚽', 'ফুটবল'],
  ['🍎', 'আপেল'],
];

export const EXTRA_EMOJIS: [string, string][] = [
  ['🍌', 'কলা'],
  ['☀️', 'সূর্য'],
  ['🦋', 'প্রজাপতি'],
];

export const ALL_EMOJIS = [...CORE_EMOJIS, ...EXTRA_EMOJIS];

export const FRUITS: PictureCardItem[] = [
  { id: 'mango', symbol: '🥭', nameBn: 'আম', category: 'FRUITS', badgeColor: '#FFF8E1' },
  { id: 'banana', symbol: '🍌', nameBn: 'কলা', category: 'FRUITS', badgeColor: '#FFFDE7' },
  { id: 'apple', symbol: '🍎', nameBn: 'আপেল', category: 'FRUITS', badgeColor: '#FFEBEE' },
  { id: 'orange_fruit', symbol: '🍊', nameBn: 'কমলা', category: 'FRUITS', badgeColor: '#FFF3E0' },
  { id: 'grapes', symbol: '🍇', nameBn: 'আঙুর', category: 'FRUITS', badgeColor: '#F3E5F5' },
  { id: 'watermelon', symbol: '🍉', nameBn: 'তরমুজ', category: 'FRUITS', badgeColor: '#FFEBEE' },
  { id: 'guava', symbol: '🍐', nameBn: 'পেয়ারা', category: 'FRUITS', badgeColor: '#E8F5E9' },
  { id: 'strawberry', symbol: '🍓', nameBn: 'স্ট্রবেরি', category: 'FRUITS', badgeColor: '#FCE4EC' },
];

export const ANIMALS: PictureCardItem[] = [
  { id: 'panda', symbol: '🐼', nameBn: 'পান্ডা', category: 'ANIMALS', badgeColor: '#E8F5E9' },
  { id: 'cat', symbol: '🐱', nameBn: 'বিড়াল', category: 'ANIMALS', badgeColor: '#FFF8E1' },
  { id: 'dog', symbol: '🐶', nameBn: 'কুকুর', category: 'ANIMALS', badgeColor: '#EFEBE9' },
  { id: 'elephant', symbol: '🐘', nameBn: 'হাতি', category: 'ANIMALS', badgeColor: '#ECEFF1' },
  { id: 'lion', symbol: '🦁', nameBn: 'সিংহ', category: 'ANIMALS', badgeColor: '#FFF3E0' },
  { id: 'tiger', symbol: '🐯', nameBn: 'বাঘ', category: 'ANIMALS', badgeColor: '#FFF8E1' },
  { id: 'fish', symbol: '🐟', nameBn: 'মাছ', category: 'ANIMALS', badgeColor: '#E3F2FD' },
  { id: 'bird', symbol: '🐦', nameBn: 'পাখি', category: 'ANIMALS', badgeColor: '#E8F5E9' },
  { id: 'rabbit', symbol: '🐰', nameBn: 'খরগোশ', category: 'ANIMALS', badgeColor: '#FCE4EC' },
  { id: 'butterfly', symbol: '🦋', nameBn: 'প্রজাপতি', category: 'ANIMALS', badgeColor: '#EDE7F6' },
];

export const NATURE: PictureCardItem[] = [
  { id: 'sun', symbol: '☀️', nameBn: 'সূর্য', category: 'NATURE', badgeColor: '#FFFDE7' },
  { id: 'moon', symbol: '🌙', nameBn: 'চাঁদ', category: 'NATURE', badgeColor: '#EDE7F6' },
  { id: 'cloud', symbol: '☁️', nameBn: 'মেঘ', category: 'NATURE', badgeColor: '#E3F2FD' },
  { id: 'rain', symbol: '🌧️', nameBn: 'বৃষ্টি', category: 'NATURE', badgeColor: '#E1F5FE' },
  { id: 'tree', symbol: '🌳', nameBn: 'গাছ', category: 'NATURE', badgeColor: '#E8F5E9' },
  { id: 'leaf', symbol: '🍃', nameBn: 'পাতা', category: 'NATURE', badgeColor: '#F1F8E9' },
  { id: 'flower', symbol: '🌸', nameBn: 'ফুল', category: 'NATURE', badgeColor: '#FCE4EC' },
  { id: 'rainbow', symbol: '🌈', nameBn: 'রংধনু', category: 'NATURE', badgeColor: '#EAF4FF' },
];

export const VEHICLES: PictureCardItem[] = [
  { id: 'car', symbol: '🚗', nameBn: 'গাড়ি', category: 'VEHICLES', badgeColor: '#FFEBEE' },
  { id: 'bus', symbol: '🚌', nameBn: 'বাস', category: 'VEHICLES', badgeColor: '#FFFDE7' },
  { id: 'train', symbol: '🚆', nameBn: 'ট্রেন', category: 'VEHICLES', badgeColor: '#E8EAF6' },
  { id: 'airplane', symbol: '✈️', nameBn: 'বিমান', category: 'VEHICLES', badgeColor: '#E3F2FD' },
  { id: 'boat', symbol: '⛵', nameBn: 'নৌকা', category: 'VEHICLES', badgeColor: '#E0F7FA' },
  { id: 'bicycle', symbol: '🚲', nameBn: 'সাইকেল', category: 'VEHICLES', badgeColor: '#E8F5E9' },
];

export const DAILY_OBJECTS: PictureCardItem[] = [
  { id: 'book', symbol: '📚', nameBn: 'বই', category: 'DAILY_OBJECTS', badgeColor: '#EDE7F6' },
  { id: 'ball', symbol: '⚽', nameBn: 'বল', category: 'DAILY_OBJECTS', badgeColor: '#ECEFF1' },
  { id: 'clock', symbol: '⏰', nameBn: 'ঘড়ি', category: 'DAILY_OBJECTS', badgeColor: '#FFF3E0' },
  { id: 'cup', symbol: '☕', nameBn: 'কাপ', category: 'DAILY_OBJECTS', badgeColor: '#EFEBE9' },
  { id: 'spoon', symbol: '🥄', nameBn: 'চামচ', category: 'DAILY_OBJECTS', badgeColor: '#F5F5F5' },
  { id: 'glasses', symbol: '👓', nameBn: 'চশমা', category: 'DAILY_OBJECTS', badgeColor: '#E3F2FD' },
  { id: 'shoe', symbol: '👟', nameBn: 'জুতা', category: 'DAILY_OBJECTS', badgeColor: '#FFEBEE' },
  { id: 'umbrella', symbol: '☂️', nameBn: 'ছাতা', category: 'DAILY_OBJECTS', badgeColor: '#F3E5F5' },
  { id: 'bag', symbol: '🎒', nameBn: 'ব্যাগ', category: 'DAILY_OBJECTS', badgeColor: '#FFF8E1' },
  { id: 'toy', symbol: '🧸', nameBn: 'খেলনা', category: 'DAILY_OBJECTS', badgeColor: '#FFF0ED' },
];

export const CORE_PICTURE_ITEMS: PictureCardItem[] = CORE_EMOJIS.map((pair, idx) => ({
  id: `core_${idx}`,
  symbol: pair[0],
  nameBn: pair[1],
  category: 'CORE_EMOJI',
  badgeColor: '#FFF9E6',
}));

export const EXTENDED_41_PICTURES: PictureCardItem[] = [
  ...FRUITS,
  ...ANIMALS,
  ...NATURE,
  ...VEHICLES,
  ...DAILY_OBJECTS,
];

export const EXTENDED_PICTURE_LIBRARY: PictureCardItem[] = [
  ...CORE_PICTURE_ITEMS,
  ...EXTENDED_41_PICTURES,
];

// 25 Core Combinations
export const ALL_COLOR_SHAPE_CARDS: ColorShapeCard[] = CORE_COLORS.flatMap(color =>
  CORE_SHAPES.map(shape => ({
    id: `${color.id}_${shape.id}`,
    color,
    shape,
    nameBn: `${color.nameBn} ${shape.nameBn}`,
  }))
);

// 200 Combinations (10 Colors x 20 2D Shapes)
export const EXTENDED_200_COLOR_SHAPE_CARDS: ColorShapeCard[] = ALL_10_COLORS.flatMap(color =>
  ALL_20_SHAPES.map(shape => ({
    id: `${color.id}_${shape.id}`,
    color,
    shape,
    nameBn: `${color.nameBn} ${shape.nameBn}`,
  }))
);

export const CHAPTERS: ChapterInfo[] = [
  {
    chapterNumber: 1,
    titleEn: 'Chapter 1',
    subtitleEn: 'Learn & Identify',
    titleBn: 'অধ্যায় ১: নতুন করে শিখি',
    subtitleBn: 'খুব সহজ পরিচয় ও প্রথম ধাপ (লেভেল ১–৫)',
    levelStart: 1,
    levelEnd: 5,
    themeColor: '#9B5DE5',
    badgeIcon: '🌱',
  },
  {
    chapterNumber: 2,
    titleEn: 'Chapter 2',
    subtitleEn: 'Recognize & Explore',
    titleBn: 'অধ্যায় ২: চিনে দেখি',
    subtitleBn: 'মৌলিক উপাদান চেনা ও মেলানো (লেভেল ৬–১০)',
    levelStart: 6,
    levelEnd: 10,
    themeColor: '#F59E0B',
    badgeIcon: '🔍',
  },
  {
    chapterNumber: 3,
    titleEn: 'Chapter 3',
    subtitleEn: 'Match & Practice',
    titleBn: 'অধ্যায় ৩: মিলিয়ে দেখি',
    subtitleBn: 'পূর্ণ মূল পাঠ্যক্রম অনুশীলন (লেভেল ১১–১৫)',
    levelStart: 11,
    levelEnd: 15,
    themeColor: '#EC4899',
    badgeIcon: '🧩',
  },
  {
    chapterNumber: 4,
    titleEn: 'Chapter 4',
    subtitleEn: 'Advanced Practice',
    titleBn: 'অধ্যায় ৪: আরও অনুশীলন',
    subtitleBn: 'নতুন ছবি, রং ও আকৃতির জগত (লেভেল ১৬–২০)',
    levelStart: 16,
    levelEnd: 20,
    themeColor: '#D97706',
    badgeIcon: '🚀',
  },
  {
    chapterNumber: 5,
    titleEn: 'Chapter 5',
    subtitleEn: 'Mastery Challenge',
    titleBn: 'অধ্যায় ৫: দক্ষতার চ্যালেঞ্জ',
    subtitleBn: 'চূড়ান্ত চ্যালেঞ্জ ও পূর্ণ দক্ষতা (লেভেল ২১–২৫)',
    levelStart: 21,
    levelEnd: 25,
    themeColor: '#EA580C',
    badgeIcon: '🏆',
  },
];

export function getLevelsForGame(gameType: GameTypeId): LevelDefinition[] {
  return Array.from({ length: 25 }, (_, i) => {
    const lvl = i + 1;
    const chap = Math.floor((lvl - 1) / 5) + 1;
    switch (gameType) {
      case 'emoji_match':
        return createEmojiLevel(lvl, chap);
      case 'shape_match':
        return createShapeLevel(lvl, chap);
      case 'color_shape_challenge':
        return createColorShapeLevel(lvl, chap);
      case 'color_paper_match':
        return createColorPaperLevel(lvl, chap);
    }
  });
}

function createEmojiLevel(lvl: number, chap: number): LevelDefinition {
  const meta: Record<number, [string, string, number]> = {
    1: ['হাসিমুখ ও হার্ট', '২ জোড়া সহজ ইমোজি মেলানো', 2],
    2: ['তারা ও থাম্বস আপ', '২ জোড়া পরিচিত ইমোজি', 2],
    3: ['আনন্দের ৩ জোড়া', '৩ জোড়া মূল ইমোজি মেলানো', 3],
    4: ['ফুল, বল ও আপেল', '৩ জোড়া ছবি ও ইমোজি', 3],
    5: ['৪ জোড়া ইমোজি বোর্ড', '৪টি ইমোজির স্থান পরিবর্তন', 4],
    6: ['৫ জোড়া মনোযোগ খেলা', '৫ জোড়া ইমোজি পাশাপাশি রাখা', 5],
    7: ['৬ জোড়া ইমোজি চ্যালেঞ্জ', '৬ জোড়া কার্ড থেকে সঠিকটি খোঁজা', 6],
    8: ['দ্রুত ইমোজি শনাক্তকরণ', '৬ জোড়া এলোমেলো কার্ড', 6],
    9: ['৭ জোড়া মূল ইমোজি', 'মূল পাঠ্যক্রমের ৭টি ইমোজি', 7],
    10: ['৮ জোড়া মূল ইমোজি', 'মূল পাঠ্যক্রমের ৮টি ইমোজি', 8],
    11: ['পূর্ণ ৯টি মূল ইমোজি', 'রেফারেন্সের সব ৯টি ইমোজি', 9],
    12: ['১২ জোড়া মাস্টার বোর্ড', '৯টি মূল ও ৩টি অতিরিক্ত ইমোজি', 12],
    13: ['মিষ্টি ফলের ঝুড়ি ১', 'আম, কলা, আপেল ও কমলা মেলানো', 4],
    14: ['মিষ্টি ফলের ঝুড়ি ২', 'আঙুর, তরমুজ, পেয়ারা ও স্ট্রবেরি', 5],
    15: ['৮টি ফলের সমাহার', 'সব ৮টি পরিচিত ফল মেলানো', 6],
    16: ['পোষা ও বনের প্রাণী ১', 'পান্ডা, বিড়াল, কুকুর, হাতি ও সিংহ', 5],
    17: ['প্রাণীদের মেলা ২', 'বাঘ, মাছ, পাখি, খরগোশ ও প্রজাপতি', 6],
    18: ['সুন্দর প্রকৃতি ১', 'সূর্য, চাঁদ, মেঘ ও বৃষ্টি', 4],
    19: ['সবুজ প্রকৃতি ২', 'গাছ, পাতা, ফুল ও রংধনু', 6],
    20: ['যানবাহন চেনা', 'গাড়ি, বাস, ট্রেন, বিমান, নৌকা ও সাইকেল', 6],
    21: ['পড়ার টেবিলের বস্তু', 'বই, বল, ঘড়ি, চশমা ও ব্যাগ', 5],
    22: ['দৈনন্দিন জিনিসপত্র', 'কাপ, চামচ, জুতা, ছাতা ও খেলনা', 6],
    23: ['ফল ও প্রাণীর মিশ্রণ', 'ভিন্ন ক্যাটাগরির ৮ জোড়া ছবি', 8],
    24: ['প্রকৃতি ও যানবাহন মিশ্রণ', '৯ জোড়া ছবির মনোযোগ পরীক্ষা', 9],
    25: ['মহাপ্রস্তুতি গ্র্যান্ড চ্যালেঞ্জ', 'সব ক্যাটাগরির ১০ জোড়া কার্ড', 10],
  };

  const item = meta[lvl] || ['মাস্টার লেভেল', 'কার্ড মেলানো', 6];
  return {
    gameType: 'emoji_match',
    levelNumber: lvl,
    chapterNumber: chap,
    titleBn: `লেভেল ${lvl}: ${item[0]}`,
    objectiveBn: item[1],
    voiceInstructionBn: 'একই ছবিটিতে ট্যাপ করো।',
    taskCount: Math.min(8, Math.max(5, item[2])),
    optionCount: item[2],
    isCoreCurriculumCompatible: lvl <= 12,
  };
}

function createShapeLevel(lvl: number, chap: number): LevelDefinition {
  const meta: Record<number, [string, string, number]> = {
    1: ['বৃত্ত ও ত্রিভুজ', '২টি মৌলিক আকৃতি চেনা', 2],
    2: ['বর্গ ও তারা', 'চারকোণা ও তারা আকৃতি মেলানো', 2],
    3: ['তিন আকৃতির বোর্ড', 'বৃত্ত, ত্রিভুজ ও বর্গ', 3],
    4: ['হার্ট ও তারার খেলা', '৩টি আকৃতির মধ্যে মিল খোঁজা', 3],
    5: ['৪টি মূল আকৃতি', '৪টি জ্যামিতিক ঘর পূরণ', 4],
    6: ['পাঁচ আকৃতির পরিচয়', 'বৃত্ত, ত্রিভুজ, বর্গ, তারা ও হার্ট', 5],
    7: ['এলোমেলো ৫ আকৃতি', 'স্থান পরিবর্তন করে ৫টি আকৃতি', 5],
    8: ['আউটলাইন শেইপ বোর্ড', 'রেখাচিত্র দেখে ৫টি মূল আকৃতি বসানো', 5],
    9: ['আয়তক্ষেত্র ও ডিম্বাকৃতি', 'নতুন ২টি আকৃতির সাথে পরিচয়', 4],
    10: ['হীরক ও অর্ধবৃত্ত', 'ডায়মন্ড ও অর্ধবৃত্ত চেনা', 4],
    11: ['অর্ধচন্দ্রের খেলা', 'চন্দ্রাকৃতি ও জ্যামিতিক বোর্ড', 5],
    12: ['পঞ্চভুজ ও ষড়ভুজ', '৫ ও ৬ বাহুর আকৃতি চেনা', 5],
    13: ['অষ্টভুজ ও ট্রাপিজিয়াম', 'উন্নত জ্যামিতিক আকৃতি', 5],
    14: ['সমান্তর চতুর্ভুজ', 'চার বাহুর বিশেষ আকৃতি অনুশীলন', 5],
    15: ['যোগচিহ্ন ও তীরচিহ্ন', 'ক্রস এবং অ্যারো আকৃতি মেলানো', 5],
    16: ['ঘুড়ি আকৃতির খেলা', 'কাইট, হীরক ও ত্রিভুজের তুলনা', 5],
    17: ['সপ্তভুজ ও দশভুজ', '৭ ও ১০ বাহুর বহুভুজ চেনা', 5],
    18: ['সমকোণী ত্রিভুজ ও বলয়', 'নতুন জ্যামিতিক আকৃতির পরিচয়', 6],
    19: ['চারকোণা তারা, মেঘ ও ঢাল', '২৫টি আকৃতির সম্পূর্ণ সম্ভার', 6],
    20: ['ছায়া আকৃতি (Silhouette)', 'গাঢ় ছায়া দেখে সঠিক আকৃতি খোঁজা', 5],
    21: ['ভিন্ন রঙের একই আকৃতি', 'রং ভিন্ন হলেও আকৃতি মেলানো', 5],
    22: ['বড় ও ছোট আকৃতি', 'আকারের ভিন্নতায় সঠিক আকৃতি চেনা', 6],
    23: ['ঘোরানো আকৃতি (Rotated)', 'ভিন্ন কোণে রাখা আকৃতি শনাক্তকরণ', 6],
    24: ['সদৃশ আকৃতির পার্থক্য', 'বর্গ, আয়তক্ষেত্র, হীরক ও ঘুড়ি বাছাই', 6],
    25: ['২৫ আকৃতির গ্র্যান্ড মাস্টার', 'সব ২৫টি দ্বিমাত্রিক আকৃতির চূড়ান্ত পরীক্ষা', 6],
  };

  const item = meta[lvl] || ['মাস্টার আকৃতি', 'আকৃতি মেলানো', 5];
  return {
    gameType: 'shape_match',
    levelNumber: lvl,
    chapterNumber: chap,
    titleBn: `লেভেল ${lvl}: ${item[0]}`,
    objectiveBn: item[1],
    voiceInstructionBn: 'একই আকৃতিটি খুঁজে নিচে ট্যাপ করে বসাও।',
    taskCount: Math.min(8, Math.max(5, item[2])),
    optionCount: item[2],
    isCoreCurriculumCompatible: lvl <= 8,
  };
}

function createColorShapeLevel(lvl: number, chap: number): LevelDefinition {
  const meta: Record<number, [string, string, number]> = {
    1: ['লাল, হলুদ ও নীল', '১ম ধাপ: মৌলিক রং চেনা', 3],
    2: ['সবুজ ও কমলা রং', '১ম ধাপ: ৫টি মূল রং শনাক্তকরণ', 4],
    3: ['পাঁচ রঙের চ্যালেঞ্জ', '১ম ধাপ: শুধু রং দেখে কার্ড বাছাই', 5],
    4: ['রঙের দ্রুত বাছাই', '১ম ধাপ: ৫টি মূল রঙের দক্ষতা', 5],
    5: ['বৃত্ত, ত্রিভুজ ও বর্গ', '২য় ধাপ: শুধু আকৃতি দেখে কার্ড বাছাই', 3],
    6: ['তারা ও হার্ট খোঁজা', '২য় ধাপ: ৫টি মূল আকৃতি শনাক্তকরণ', 4],
    7: ['রঙিন কার্ডে আকৃতি চেনা', '২য় ধাপ: রং উপেক্ষা করে আকৃতি নির্বাচন', 5],
    8: ['পাঁচ আকৃতির পূর্ণ দক্ষতা', '২য় ধাপ: মূল ৫ আকৃতির চূড়ান্ত যাচাই', 5],
    9: ['রং + আকৃতি শুরু', '৩য় ধাপ: লাল তারা ও হলুদ ত্রিভুজ খোঁজা', 4],
    10: ['নীল হার্ট ও সবুজ বৃত্ত', '৩য় ধাপ: দুই বৈশিষ্ট্য একসঙ্গে মেলানো', 4],
    11: ['কমলা বর্গ চ্যালেঞ্জ', '৩য় ধাপ: ২৫টি মূল কম্বিনেশন অনুশীলন', 6],
    12: ['২৫ কার্ডের চূড়ান্ত ধাপ', '৩য় ধাপ: পূর্ণ ২৫টি মূল কার্ডের পরীক্ষা', 6],
    13: ['নতুন ৫টি রঙের জগত', 'গোলাপি, বেগুনি, বাদামি, কালো ও সাদা', 4],
    14: ['১০টি রঙের মহড়া', 'মূল ৫টি ও নতুন ৫টি রঙের কার্ড', 5],
    15: ['আয়তক্ষেত্র, ডিম্বাকৃতি ও হীরক', 'নতুন আকৃতি শনাক্তকরণ অনুশীলন', 5],
    16: ['অর্ধবৃত্ত, অর্ধচন্দ্র ও পঞ্চভুজ', 'বর্ধিত জ্যামিতিক আকৃতি বাছাই', 5],
    17: ['ষড়ভুজ, অষ্টভুজ ও ট্রাপিজিয়াম', 'বহুভুজ আকৃতির কার্ড নির্বাচন', 6],
    18: ['যোগচিহ্ন, তীরচিহ্ন ও ঘুড়ি', 'নতুন চিহ্ন ও আকৃতি চেনা', 6],
    19: ['সপ্তভুজ ও দশভুজ পরিচয়', '২০টি আকৃতির সম্পূর্ণ পরিচিতি', 6],
    20: ['গোলাপি বৃত্ত ও বেগুনি আয়তক্ষেত্র', 'নতুন রং ও আকৃতির কম্বিনেশন', 5],
    21: ['বাদামি হীরক ও কালো তারা', '২০০ কম্বিনেশন লাইব্রেরির অনুশীলন ১', 6],
    22: ['সাদা ষড়ভুজ ও গোলাপি হার্ট', '২০০ কম্বিনেশন লাইব্রেরির অনুশীলন ২', 6],
    23: ['লাল তীরচিহ্ন ও নীল ঘুড়ি', '২০ আকৃতি ও ১০ রঙের দ্বৈত পরীক্ষা', 6],
    24: ['হলুদ অর্ধচন্দ্র ও সবুজ যোগচিহ্ন', 'সদৃশ বিকল্প থেকে সঠিক কার্ড বাছাই', 6],
    25: ['২০০ কার্ডের গ্র্যান্ড চ্যালেঞ্জ', '১০ রং × ২০ আকৃতির পূর্ণ দক্ষতা', 6],
  };

  const item = meta[lvl] || ['রং ও আকৃতি', 'কার্ড বাছাই', 4];
  return {
    gameType: 'color_shape_challenge',
    levelNumber: lvl,
    chapterNumber: chap,
    titleBn: `লেভেল ${lvl}: ${item[0]}`,
    objectiveBn: item[1],
    voiceInstructionBn: 'রং এবং আকৃতি দুটোই মিলিয়ে সঠিক কার্ডটি নাও।',
    taskCount: 5,
    optionCount: item[2],
    isCoreCurriculumCompatible: lvl <= 12,
  };
}

function createColorPaperLevel(lvl: number, chap: number): LevelDefinition {
  const meta: Record<number, [string, string, number]> = {
    1: ['লাল ও হলুদ পেপার', '২টি রঙের ট্রেতে কাগজ রাখা', 2],
    2: ['নীল ও সবুজ পেপার', '৩টি রঙের ট্রেতে কাগজ মেলানো', 3],
    3: ['কমলা পেপার যোগ', '৪টি রঙের ট্রের সাথে পরিচয়', 4],
    4: ['পাঁচ রঙের মূল ট্রে', 'লাল, হলুদ, নীল, সবুজ ও কমলা ট্রে', 5],
    5: ['ট্রে স্থান পরিবর্তন ১', 'এলোমেলো ট্রেতে সঠিক কাগজ রাখা', 3],
    6: ['ট্রে স্থান পরিবর্তন ২', '৪টি পরিবর্তনশীল রঙের ট্রে', 4],
    7: ['পাঁচ ট্রের বিন্যাস', '৫টি মূল রঙের ট্রে নতুন ক্রমে', 5],
    8: ['দ্রুত পেপার মেলানো', '৫টি রঙিন পেপার সঠিক ঘরে সাজানো', 5],
    9: ['একাধিক পেপার সর্টিং ১', '৬টি রঙিন কাগজ ৫টি ট্রেতে রাখা', 5],
    10: ['একাধিক পেপার সর্টিং ২', '৮টি এলোমেলো কাগজ সঠিক ট্রেতে ভাগ করা', 5],
    11: ['১০টি পেপারের বান্ডিল', 'প্রতিটি মূল রঙে ২টি করে কাগজ সাজানো', 5],
    12: ['মূল কালার পেপার মাস্টার', 'পাঁচ রঙের পূর্ণাঙ্গ ট্রে চ্যালেঞ্জ', 5],
    13: ['গোলাপি ও বেগুনি পেপার', 'নতুন রঙের পেপার ও ট্রের পরিচয়', 4],
    14: ['বাদামি পেপার যোগ', 'নতুন ও মূল রঙের ৫টি ট্রে', 5],
    15: ['কালো ও সাদা পেপার', '৫টি নতুন রঙের ট্রে অনুশীলন', 5],
    16: ['নতুন রঙের পেপার সর্টিং', 'গোলাপি থেকে সাদা পেপার মেলানো', 5],
    17: ['৬ রঙের বড় ট্রে বোর্ড', 'মূল ও অতিরিক্ত রঙের একত্রে অনুশীলন', 6],
    18: ['৭ রঙের পেপার সর্টিং', '৭টি রঙের কাগজ ও ট্রে মেলানো', 6],
    19: ['৮ রঙের পেপার চ্যালেঞ্জ', '৮টি ভিন্ন রঙের কাগজ ও ট্রে', 6],
    20: ['ব্যাচ পেপার সর্টিং ১', '৯টি মিশ্র রঙের কাগজ সঠিক ট্রেতে রাখা', 6],
    21: ['ব্যাচ পেপার সর্টিং ২', '১০টি মিশ্র রঙের কাগজ বাছাই', 6],
    22: ['প্যাটার্ন ও রং মিলানো', 'চিহ্ন ও রং দেখে সঠিক ট্রে নির্বাচন', 6],
    23: ['পরিবর্তনশীল ১০ রঙের ট্রে', '১০টি রঙের মধ্যে দ্রুত বাছাই', 6],
    24: ['১২টি পেপারের মেগা বান্ডিল', '১০টি রঙের পেপার নির্ভুলভাবে সাজানো', 6],
    25: ['কালার পেপার গ্র্যান্ড মাস্টার', '১০টি রঙের সম্পূর্ণ দক্ষতা যাচাই', 6],
  };

  const item = meta[lvl] || ['কালার পেপার', 'ট্রেতে রাখা', 4];
  return {
    gameType: 'color_paper_match',
    levelNumber: lvl,
    chapterNumber: chap,
    titleBn: `লেভেল ${lvl}: ${item[0]}`,
    objectiveBn: item[1],
    voiceInstructionBn: 'রঙিন কাগজটি নিয়ে একই রঙের ট্রেতে রাখো।',
    taskCount: Math.min(8, Math.max(5, item[2])),
    optionCount: item[2],
    isCoreCurriculumCompatible: lvl <= 12,
  };
}
