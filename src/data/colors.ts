import { Kid3DShape, KidColor, KidShape } from '../types/game';

export const KID_COLORS: KidColor[] = [
  // 5 Core Colors
  {
    id: 'red',
    nameBn: 'লাল',
    color: '#E53935',
    lightColor: '#FFEBEE',
    darkColor: '#C62828',
    isCore: true,
    patternSymbol: '●',
  },
  {
    id: 'yellow',
    nameBn: 'হলুদ',
    color: '#FBC02D',
    lightColor: '#FFFDE7',
    darkColor: '#F57F17',
    isCore: true,
    patternSymbol: '★',
  },
  {
    id: 'blue',
    nameBn: 'নীল',
    color: '#1E88E5',
    lightColor: '#E3F2FD',
    darkColor: '#1565C0',
    isCore: true,
    patternSymbol: '▲',
  },
  {
    id: 'green',
    nameBn: 'সবুজ',
    color: '#43A047',
    lightColor: '#E8F5E9',
    darkColor: '#2E7D32',
    isCore: true,
    patternSymbol: '■',
  },
  {
    id: 'orange',
    nameBn: 'কমলা',
    color: '#FB8C00',
    lightColor: '#FFF3E0',
    darkColor: '#E65100',
    isCore: true,
    patternSymbol: '◆',
  },

  // 5 Extended Colors (Mode B)
  {
    id: 'pink',
    nameBn: 'গোলাপি',
    color: '#E91E63',
    lightColor: '#FCE4EC',
    darkColor: '#AD1457',
    isCore: false,
    patternSymbol: '♥',
  },
  {
    id: 'purple',
    nameBn: 'বেগুনি',
    color: '#8E24AA',
    lightColor: '#F3E5F5',
    darkColor: '#6A1B9A',
    isCore: false,
    patternSymbol: '✦',
  },
  {
    id: 'brown',
    nameBn: 'বাদামি',
    color: '#795548',
    lightColor: '#EFEBE9',
    darkColor: '#4E342E',
    isCore: false,
    patternSymbol: '⬟',
  },
  {
    id: 'black',
    nameBn: 'কালো',
    color: '#263238',
    lightColor: '#ECEFF1',
    darkColor: '#11171A',
    isCore: false,
    patternSymbol: '⬢',
  },
  {
    id: 'white',
    nameBn: 'সাদা',
    color: '#FAFAFA',
    lightColor: '#FFFFFF',
    darkColor: '#90A4AE',
    isCore: false,
    patternSymbol: '○',
  },
];

export const CORE_COLORS = KID_COLORS.filter(c => c.isCore);
export const EXTENDED_ONLY_COLORS = KID_COLORS.filter(c => !c.isCore);
export const ALL_10_COLORS = KID_COLORS;

export const KID_SHAPES: KidShape[] = [
  // 5 Original Core Shapes
  { id: 'circle', nameBn: 'বৃত্ত', isCore: true },
  { id: 'triangle', nameBn: 'ত্রিভুজ', isCore: true },
  { id: 'square', nameBn: 'বর্গ', isCore: true },
  { id: 'star', nameBn: 'তারা', isCore: true },
  { id: 'heart', nameBn: 'হার্ট', isCore: true },

  // 20 Additional 2D Shapes (Total = 25 2D Shapes)
  { id: 'rectangle', nameBn: 'আয়তক্ষেত্র', isCore: false },
  { id: 'oval', nameBn: 'ডিম্বাকৃতি', isCore: false },
  { id: 'diamond', nameBn: 'হীরক আকৃতি', isCore: false },
  { id: 'semicircle', nameBn: 'অর্ধবৃত্ত', isCore: false },
  { id: 'crescent', nameBn: 'অর্ধচন্দ্র', isCore: false },
  { id: 'pentagon', nameBn: 'পঞ্চভুজ', isCore: false },
  { id: 'hexagon', nameBn: 'ষড়ভুজ', isCore: false },
  { id: 'octagon', nameBn: 'অষ্টভুজ', isCore: false },
  { id: 'trapezoid', nameBn: 'ট্রাপিজিয়াম', isCore: false },
  { id: 'parallelogram', nameBn: 'সমান্তর চতুর্ভুজ', isCore: false },
  { id: 'cross', nameBn: 'যোগচিহ্ন', isCore: false },
  { id: 'arrow', nameBn: 'তীরচিহ্ন', isCore: false },
  { id: 'kite', nameBn: 'ঘুড়ি আকৃতি', isCore: false },
  { id: 'heptagon', nameBn: 'সপ্তভুজ', isCore: false },
  { id: 'decagon', nameBn: 'দশভুজ', isCore: false },
  { id: 'right_triangle', nameBn: 'সমকোণী ত্রিভুজ', isCore: false },
  { id: 'ring', nameBn: 'বলয় আকৃতি', isCore: false },
  { id: 'four_point_star', nameBn: 'চারকোণা তারা', isCore: false },
  { id: 'cloud_shape', nameBn: 'মেঘ আকৃতি', isCore: false },
  { id: 'shield', nameBn: 'ঢাল আকৃতি', isCore: false },
];

export const CORE_SHAPES = KID_SHAPES.filter(s => s.isCore);
export const ALL_20_SHAPES = KID_SHAPES.slice(0, 20);
export const ALL_25_SHAPES = KID_SHAPES;

export const KID_3D_SHAPES: Kid3DShape[] = [
  {
    id: 'sphere',
    nameBn: 'গোলক',
    realWorldExampleBn: 'ফুটবল বা মার্বেলের মতো গোল',
    descriptionBn: 'চারদিক থেকেই মসৃণ ও গোলাকার ত্রিমাত্রিক আকৃতি।',
    primaryColor: '#E53935',
  },
  {
    id: 'cube',
    nameBn: 'ঘনক',
    realWorldExampleBn: 'লুডুর ছক্কা বা রুবিকস কিউব',
    descriptionBn: '৬টি সমান বর্গাকার তলবিশিষ্ট ত্রিমাত্রিক আকৃতি।',
    primaryColor: '#1E88E5',
  },
  {
    id: 'cuboid',
    nameBn: 'আয়তঘন',
    realWorldExampleBn: 'বই, ইট বা জুতার বাক্স',
    descriptionBn: '৬টি আয়তাকার তলবিশিষ্ট ত্রিমাত্রিক বাক্স আকৃতি।',
    primaryColor: '#43A047',
  },
  {
    id: 'cylinder',
    nameBn: 'বেলন',
    realWorldExampleBn: 'পানির গ্লাস বা কৌটা',
    descriptionBn: 'উপরে ও নিচে বৃত্তাকার এবং মাঝখানে গোলাকার স্তম্ভ।',
    primaryColor: '#FB8C00',
  },
  {
    id: 'cone',
    nameBn: 'শঙ্কু',
    realWorldExampleBn: 'আইসক্রিম কোন বা জন্মদিনের টুপি',
    descriptionBn: 'নিচে বৃত্তাকার তল এবং উপরে একটি সূঁচালো শীর্ষবিন্দু।',
    primaryColor: '#E91E63',
  },
  {
    id: 'pyramid',
    nameBn: 'পিরামিড',
    realWorldExampleBn: 'মিশরের পিরামিড বা তাঁবু',
    descriptionBn: 'চারপাশে ত্রিভুজাকার ঢালু তল যা উপরে এক বিন্দুতে মেলে।',
    primaryColor: '#F57F17',
  },
  {
    id: 'tri_prism',
    nameBn: 'ত্রিভুজাকার প্রিজম',
    realWorldExampleBn: 'খেলার তাঁবু বা প্রিজম কাঁচ',
    descriptionBn: 'দুই প্রান্তে ত্রিভুজ এবং পাশে আয়তাকার তল।',
    primaryColor: '#8E24AA',
  },
  {
    id: 'rect_prism',
    nameBn: 'আয়তাকার প্রিজম',
    realWorldExampleBn: 'আলমারি বা লম্বা বাক্স',
    descriptionBn: 'সমান্তরাল আয়তাকার প্রান্তবিশিষ্ট ত্রিমাত্রিক ঘনবস্তু।',
    primaryColor: '#56BFA4',
  },
];
