import { AudioSettingsState } from '../types/game';
import { ALL_10_COLORS, ALL_25_SHAPES, KID_3D_SHAPES } from '../data/colors';

export type SoundEffectType =
  | 'BUTTON_TAP'
  | 'CARD_SELECT'
  | 'CARD_PLACE'
  | 'CORRECT_MATCH'
  | 'GENTLE_RETRY'
  | 'STAR_REWARD'
  | 'LEVEL_UNLOCK'
  | 'LEVEL_COMPLETE'
  | 'CHAPTER_COMPLETE'
  | 'PHYSICAL_TRANSITION';

export interface ScriptEntry {
  key: string;
  assetFileName: string;
  banglaScript: string;
  titleBn: string;
}

export const VOICE_PREVIEW_SAMPLES: ScriptEntry[] = [
  {
    key: 'preview_welcome',
    assetFileName: 'welcome.mp3',
    banglaScript: 'হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।',
    titleBn: 'স্বাগতম (Welcome)',
  },
  {
    key: 'preview_emoji',
    assetFileName: 'emoji_find_same.mp3',
    banglaScript: 'একই ছবিটি খুঁজে বের করো।',
    titleBn: 'ইমোজি ও ছবি ম্যাচিং (Emoji Matching)',
  },
  {
    key: 'preview_shape',
    assetFileName: 'shape_triangle.mp3',
    banglaScript: 'ত্রিভুজটি খুঁজে দাও।',
    titleBn: 'শেইপ ম্যাচিং (Shape Matching)',
  },
  {
    key: 'preview_color',
    assetFileName: 'color_blue.mp3',
    banglaScript: 'নীল রংটি খুঁজে বের করো।',
    titleBn: 'কালার ম্যাচিং (Colour Matching)',
  },
  {
    key: 'preview_combo',
    assetFileName: 'combo_red_star.mp3',
    banglaScript: 'লাল তারাটি নির্বাচন করো।',
    titleBn: 'কালার + শেইপ চ্যালেঞ্জ (Combined Challenge)',
  },
  {
    key: 'preview_correct',
    assetFileName: 'praise_correct_full.mp3',
    banglaScript: 'খুব ভালো হয়েছে! এটি সঠিক উত্তর।',
    titleBn: 'সঠিক উত্তর (Correct Answer)',
  },
  {
    key: 'preview_retry',
    assetFileName: 'retry_try_again.mp3',
    banglaScript: 'আবার চেষ্টা করো।',
    titleBn: 'পুনরায় চেষ্টা (Retry)',
  },
  {
    key: 'preview_encouragement',
    assetFileName: 'praise_next_step.mp3',
    banglaScript: 'চমৎকার! এবার পরের ধাপে যাই।',
    titleBn: 'উৎসাহ ও পরবর্তী ধাপ (Encouragement)',
  },
  {
    key: 'preview_physical',
    assetFileName: 'physical_practice.mp3',
    banglaScript: 'এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।',
    titleBn: 'বাস্তব অনুশীলন (Physical Practice)',
  },
];

const BANNED_EXPRESSIONS = [
  'সোনামণি',
  'সোনা মণি',
  'আমার সোনা',
  'হ্যালো সোনা',
  'বাবু',
  'জান',
  'খুকু',
  'খোকা',
];

export const PRAISE_VARIATIONS = [
  'খুব ভালো হয়েছে! এটি সঠিক উত্তর।',
  'খুব ভালো হয়েছে!',
  'হ্যাঁ, এটি সঠিক উত্তর।',
  'চমৎকার! এবার পরের ধাপে যাই।',
];

export const GENTLE_RETRY_VARIATIONS = [
  'আবার চেষ্টা করো।',
  'একটু ভালো করে দেখো।',
];

function sanitizeScript(raw: string): string {
  let cleaned = raw;
  for (const banned of BANNED_EXPRESSIONS) {
    cleaned = cleaned.split(banned).join('');
  }
  return cleaned.replace(/\s+/g, ' ').trim();
}

export function resolveAssetSequence(rawText: string): string[] {
  const clean = sanitizeScript(rawText);
  if (!clean) return [];

  // 1. Direct matches in previews or key phrases
  const preview = VOICE_PREVIEW_SAMPLES.find(s => s.banglaScript === clean);
  if (preview) return [preview.assetFileName];

  if (clean.includes('হ্যালো') || clean.includes('নতুন কিছু শিখি')) return ['welcome.mp3'];
  if (clean === 'এই ধাপটি সম্পন্ন হয়েছে।') return ['level_completed.mp3'];
  if (clean === 'চলো, পরের ধাপে যাই।') return ['next_level_go.mp3'];
  if (clean === 'আবার চেষ্টা করো।') return ['retry_try_again.mp3'];
  if (clean === 'একটু ভালো করে দেখো।') return ['retry_look_carefully.mp3'];
  if (clean === 'আগের ধাপটি শেষ করে এটি খোলো।') return ['locked_level.mp3'];
  if (clean === 'সঠিক কার্ডটি বেছে নাও।') return ['select_correct_card.mp3'];
  if (clean === 'খুব ভালো হয়েছে! এটি সঠিক উত্তর।') return ['praise_correct_full.mp3'];
  if (clean === 'খুব ভালো হয়েছে!') return ['praise_very_good.mp3'];
  if (clean === 'হ্যাঁ, এটি সঠিক উত্তর।') return ['praise_correct.mp3'];
  if (clean === 'চমৎকার! এবার পরের ধাপে যাই।') return ['praise_next_step.mp3'];
  if (clean.includes('বাস্তব উপকরণ')) return ['physical_practice.mp3'];

  // Game intros
  if (clean.includes('একই রঙের কাগজ')) return ['intro_color_paper.mp3'];
  if (clean.includes('রং এবং আকৃতি মিলিয়ে')) return ['intro_color_shape.mp3'];
  if (clean.includes('সঠিক আকৃতি খুঁজে')) return ['intro_shape.mp3'];

  // 2. Check 3D shapes
  for (const s3d of KID_3D_SHAPES) {
    if (clean.includes(s3d.nameBn)) {
      return [`shape3d_${s3d.id}.mp3`];
    }
  }

  // 3. Combined Color + Shape
  for (const color of ALL_10_COLORS) {
    for (const shape of ALL_25_SHAPES) {
      if (clean.includes(color.nameBn) && clean.includes(shape.nameBn)) {
        if (color.isCore && shape.isCore) {
          return [`combo_${color.id}_${shape.id}.mp3`];
        } else {
          return [`prefix_color_${color.id}.mp3`, `select_shape_${shape.id}.mp3`];
        }
      }
    }
  }

  // 4. Single Shape
  for (const shape of ALL_25_SHAPES) {
    if (clean.includes(shape.nameBn)) {
      return [`shape_${shape.id}.mp3`];
    }
  }

  // 5. Single Color
  for (const color of ALL_10_COLORS) {
    if (clean.includes(color.nameBn)) {
      return [`color_${color.id}.mp3`];
    }
  }

  // 6. Emoji / picture match
  if (clean.includes('ছবি') || clean.includes('ইমোজি')) {
    return ['emoji_find_same.mp3'];
  }

  return [];
}

class WebSoundEffectManager {
  private ctx: AudioContext | null = null;

  private getContext(): AudioContext | null {
    if (typeof window === 'undefined') return null;
    if (!this.ctx) {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioCtx) {
        this.ctx = new AudioCtx();
      }
    }
    if (this.ctx && this.ctx.state === 'suspended') {
      this.ctx.resume().catch(() => {});
    }
    return this.ctx;
  }

  play(effect: SoundEffectType, isVoiceActive: boolean) {
    const ctx = this.getContext();
    if (!ctx) return;

    const gainNode = ctx.createGain();
    const volume = isVoiceActive ? 0.08 : 0.18;
    gainNode.gain.setValueAtTime(volume, ctx.currentTime);
    gainNode.connect(ctx.destination);

    const playNotes = (notes: [number, number][]) => {
      let startTime = ctx.currentTime;
      notes.forEach(([freq, durationMs]) => {
        const osc = ctx.createOscillator();
        const noteGain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(freq, startTime);

        noteGain.gain.setValueAtTime(volume, startTime);
        noteGain.gain.exponentialRampToValueAtTime(0.0001, startTime + durationMs / 1000);

        osc.connect(noteGain);
        noteGain.connect(gainNode);

        osc.start(startTime);
        osc.stop(startTime + durationMs / 1000);
        startTime += durationMs / 1000;
      });
    };

    switch (effect) {
      case 'BUTTON_TAP':
        playNotes([[440.0, 40]]);
        break;
      case 'CARD_SELECT':
      case 'CARD_PLACE':
      case 'PHYSICAL_TRANSITION':
        playNotes([[523.25, 45], [587.33, 55]]);
        break;
      case 'CORRECT_MATCH':
        playNotes([[523.25, 65], [659.25, 75], [783.99, 110]]);
        break;
      case 'GENTLE_RETRY':
        playNotes([[392.0, 75], [349.23, 95]]);
        break;
      case 'STAR_REWARD':
        playNotes([[783.99, 55], [987.77, 55], [1046.5, 115]]);
        break;
      case 'LEVEL_UNLOCK':
        playNotes([[440.0, 55], [554.37, 55], [659.25, 110]]);
        break;
      case 'LEVEL_COMPLETE':
      case 'CHAPTER_COMPLETE':
        playNotes([[523.25, 70], [659.25, 70], [783.99, 70], [1046.5, 160]]);
        break;
    }
  }
}

export class BanglaAudioEngine {
  private currentAudio: HTMLAudioElement | null = null;
  private sfxManager = new WebSoundEffectManager();
  private settings: AudioSettingsState = {
    voiceEnabled: true,
    soundEffectsEnabled: true,
    backgroundMusicEnabled: false,
    reducedMotion: false,
  };

  private isPlayingPraise = false;
  private pendingRequest: { text: string; isManualReplay: boolean } | null = null;
  private lastAutoDedupeKey: string | null = null;
  private lastAutoSpokenTimestamp = 0;
  private lastPraiseIdx = -1;
  private lastRetryIdx = -1;
  private bgmInterval: number | null = null;

  constructor() {
    // Background music loop monitor
  }

  updateSettings(newSettings: AudioSettingsState) {
    const prevBgm = this.settings.backgroundMusicEnabled;
    this.settings = newSettings;
    if (!newSettings.voiceEnabled) {
      this.stopAndClearQueue();
    }
    if (newSettings.backgroundMusicEnabled && !prevBgm) {
      this.startBgm();
    } else if (!newSettings.backgroundMusicEnabled && prevBgm) {
      this.stopBgm();
    }
  }

  getSettings(): AudioSettingsState {
    return this.settings;
  }

  speak(text: string, dedupeKey?: string, isManualReplay = false, queueAfterPraise = true) {
    if (!this.settings.voiceEnabled) return;

    const clean = sanitizeScript(text);
    if (!clean) return;

    const now = Date.now();
    const effectiveKey = dedupeKey || clean;

    // Deduplication for automatic speech triggers
    if (!isManualReplay) {
      if (effectiveKey === this.lastAutoDedupeKey && now - this.lastAutoSpokenTimestamp < 2500) {
        return;
      }
    }

    if (queueAfterPraise && this.isPlayingPraise && this.currentAudio && !this.currentAudio.paused) {
      this.pendingRequest = { text: clean, isManualReplay };
      this.lastAutoDedupeKey = effectiveKey;
      this.lastAutoSpokenTimestamp = now;
      return;
    }

    this.pendingRequest = null;
    this.stopCurrentAudio();

    if (!isManualReplay) {
      this.lastAutoDedupeKey = effectiveKey;
      this.lastAutoSpokenTimestamp = now;
    }

    this.isPlayingPraise = false;
    this.playSequence(resolveAssetSequence(clean));
  }

  replayInstruction(text: string) {
    this.speak(text, undefined, true, false);
  }

  speakPraise(custom?: string) {
    if (!this.settings.voiceEnabled) return;

    let text = custom;
    if (!text) {
      const candidates = PRAISE_VARIATIONS.map((_, i) => i).filter(i => i !== this.lastPraiseIdx);
      const chosen = candidates[Math.floor(Math.random() * candidates.length)];
      this.lastPraiseIdx = chosen;
      text = PRAISE_VARIATIONS[chosen];
    }

    this.stopCurrentAudio();
    this.isPlayingPraise = true;
    this.playSequence(resolveAssetSequence(text), () => {
      this.isPlayingPraise = false;
      if (this.pendingRequest && this.settings.voiceEnabled) {
        const req = this.pendingRequest;
        this.pendingRequest = null;
        this.speak(req.text, undefined, req.isManualReplay, false);
      }
    });
  }

  speakGentleRetry(custom?: string) {
    if (!this.settings.voiceEnabled) return;

    let text = custom;
    if (!text) {
      const candidates = GENTLE_RETRY_VARIATIONS.map((_, i) => i).filter(i => i !== this.lastRetryIdx);
      const chosen = candidates[Math.floor(Math.random() * candidates.length)];
      this.lastRetryIdx = chosen;
      text = GENTLE_RETRY_VARIATIONS[chosen];
    }

    this.stopCurrentAudio();
    this.isPlayingPraise = true;
    this.playSequence(resolveAssetSequence(text), () => {
      this.isPlayingPraise = false;
    });
  }

  playVoicePreviewSample(sample: ScriptEntry) {
    this.stopAndClearQueue();
    this.playSequence([sample.assetFileName]);
  }

  playEffect(type: SoundEffectType) {
    if (!this.settings.soundEffectsEnabled) return;
    const isVoiceActive = !!(this.currentAudio && !this.currentAudio.paused);
    this.sfxManager.play(type, isVoiceActive);
  }

  playButtonTap() {
    this.playEffect('BUTTON_TAP');
  }

  playSuccessChime() {
    this.playEffect('CORRECT_MATCH');
  }

  playTryAgainChime() {
    this.playEffect('GENTLE_RETRY');
  }

  playLevelCompleteFanfare() {
    this.playEffect('LEVEL_COMPLETE');
  }

  stopAndClearQueue() {
    this.stopCurrentAudio();
    this.pendingRequest = null;
    this.isPlayingPraise = false;
  }

  private stopCurrentAudio() {
    if (this.currentAudio) {
      try {
        this.currentAudio.pause();
        this.currentAudio.currentTime = 0;
      } catch {
        // ignore
      }
      this.currentAudio = null;
    }
  }

  private playSequence(fileNames: string[], onDone?: () => void) {
    if (!fileNames || fileNames.length === 0) {
      onDone?.();
      return;
    }

    let currentIndex = 0;

    const playNext = () => {
      if (!this.settings.voiceEnabled || currentIndex >= fileNames.length) {
        this.currentAudio = null;
        onDone?.();
        return;
      }

      const fileName = fileNames[currentIndex];
      const audioUrl = `/voice/youthful_boy_bn/${fileName}`;
      const audio = new Audio(audioUrl);
      this.currentAudio = audio;

      audio.onended = () => {
        currentIndex++;
        playNext();
      };

      audio.onerror = () => {
        currentIndex++;
        playNext();
      };

      audio.play().catch(() => {
        // Autoplay policy or playback interruption
        currentIndex++;
        playNext();
      });
    };

    playNext();
  }

  private startBgm() {
    if (this.bgmInterval) return;
    // Play gentle soothing pentatonic notes every 6 seconds
    const notes = [261.63, 293.66, 329.63, 392.0, 440.0];
    this.bgmInterval = window.setInterval(() => {
      if (!this.settings.backgroundMusicEnabled) return;
      const note = notes[Math.floor(Math.random() * notes.length)];
      this.playGentleChime(note);
    }, 6000);
  }

  private stopBgm() {
    if (this.bgmInterval) {
      clearInterval(this.bgmInterval);
      this.bgmInterval = null;
    }
  }

  private playGentleChime(freq: number) {
    try {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (!AudioCtx) return;
      const ctx = new AudioCtx();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(freq, ctx.currentTime);
      gain.gain.setValueAtTime(0.02, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + 3.0);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 3.0);
    } catch {
      // ignore
    }
  }
}
