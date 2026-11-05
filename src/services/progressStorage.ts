import {
  AudioSettingsState,
  GAME_TYPES,
  GameProgressData,
  GameTypeId,
  LearningMode,
  SkillMasterySnapshot,
} from '../types/game';
import { ALL_10_COLORS, ALL_25_SHAPES } from '../data/colors';

const STORAGE_KEY = 'playkids_v7_progress_store';

interface StoredAppData {
  progressMap: Record<GameTypeId, GameProgressData>;
  learningMode: LearningMode;
  lastPlayedGame: GameTypeId;
  audioSettings: AudioSettingsState;
  skillMastery: SkillMasterySnapshot;
}

const DEFAULT_AUDIO_SETTINGS: AudioSettingsState = {
  voiceEnabled: true,
  soundEffectsEnabled: true,
  backgroundMusicEnabled: false,
  reducedMotion: false,
};

function createDefaultProgressMap(): Record<GameTypeId, GameProgressData> {
  const map = {} as Record<GameTypeId, GameProgressData>;
  (Object.keys(GAME_TYPES) as GameTypeId[]).forEach(id => {
    map[id] = {
      gameType: id,
      unlockedLevel: 1,
      coreCompletedLevels: [],
      extendedUnlockedLevel: 1,
      completedLevels: [],
      starsEarned: 0,
      totalPlayed: 0,
      independentSuccesses: 0,
      hintsUsed: 0,
      physicalCompleted: false,
    };
  });
  return map;
}

function createDefaultSkillMastery(): SkillMasterySnapshot {
  const colorAccuracyMap: Record<string, number> = {};
  const shapeAccuracyMap: Record<string, number> = {};
  ALL_10_COLORS.forEach(c => {
    colorAccuracyMap[c.id] = 0;
  });
  ALL_25_SHAPES.forEach(s => {
    shapeAccuracyMap[s.id] = 0;
  });
  return { colorAccuracyMap, shapeAccuracyMap };
}

export function loadStoredAppData(): StoredAppData {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return {
        progressMap: createDefaultProgressMap(),
        learningMode: 'core',
        lastPlayedGame: 'emoji_match',
        audioSettings: DEFAULT_AUDIO_SETTINGS,
        skillMastery: createDefaultSkillMastery(),
      };
    }
    const parsed = JSON.parse(raw) as Partial<StoredAppData>;
    return {
      progressMap: { ...createDefaultProgressMap(), ...(parsed.progressMap || {}) },
      learningMode: parsed.learningMode || 'core',
      lastPlayedGame: parsed.lastPlayedGame || 'emoji_match',
      audioSettings: { ...DEFAULT_AUDIO_SETTINGS, ...(parsed.audioSettings || {}) },
      skillMastery: {
        colorAccuracyMap: {
          ...createDefaultSkillMastery().colorAccuracyMap,
          ...(parsed.skillMastery?.colorAccuracyMap || {}),
        },
        shapeAccuracyMap: {
          ...createDefaultSkillMastery().shapeAccuracyMap,
          ...(parsed.skillMastery?.shapeAccuracyMap || {}),
        },
      },
    };
  } catch {
    return {
      progressMap: createDefaultProgressMap(),
      learningMode: 'core',
      lastPlayedGame: 'emoji_match',
      audioSettings: DEFAULT_AUDIO_SETTINGS,
      skillMastery: createDefaultSkillMastery(),
    };
  }
}

export function saveStoredAppData(data: StoredAppData) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  } catch {
    // ignore storage errors
  }
}

export { createDefaultProgressMap, createDefaultSkillMastery, DEFAULT_AUDIO_SETTINGS };
export type { StoredAppData };
