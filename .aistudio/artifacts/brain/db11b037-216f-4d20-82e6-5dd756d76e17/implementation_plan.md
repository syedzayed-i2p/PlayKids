# PlayKids V7.0 — Complete Voice Conflict Resolution, Youthful Bengali Voice & Full App Optimization

This update permanently eliminates the simultaneous dual-voice playback defect by establishing a single, serialized `VoicePlaybackCoordinator` with strict mutual exclusion and request deduplication, replaces all dynamic TTS fallbacks with a complete offline library of 10–12-year-old youthful Bengali male voice recordings, and optimizes gameplay, level navigation, and UI performance across all four educational games.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions incorporate your confirmed preferences and govern the V7.0 voice and optimization upgrade:

- **Confirmed Decision 1 — Sequential Praise-to-Prompt Serialization**: When a child selects a correct answer, the praise clip plays first and the next target instruction is queued to play automatically after the praise finishes (`OnCompletionListener`), whereas manual Speaker Replay taps, retry feedback, or screen navigation interrupt immediately and clear any pending queue.
- **Confirmed Decision 2 — Complete Bundled Youthful Boy Audio Library & Strict TTS Lockout**: All game prompts—including the V7.0 Part 3 scripts, all 10 colours, all 25 2D shapes, all 8 3D shapes, combined colour-shape prompts, and emoji/picture prompts—are pre-bundled as normalized offline audio clips in the 10–12-year-old boy vocal profile. Whenever the bundled offline voice pack is present, the device's older Android `TextToSpeech` engine is strictly locked out so the mature fallback voice can never activate.
- **Confirmed Decision 3 — Purposeful, Calm Narration Scope**: Spoken narration triggers exclusively on initial/new target prompts, answer feedback (praise or gentle retry), level completion, and manual speaker replay taps. Card selection taps and UI navigation buttons play only gentle, normalized sound effects.

---

## 1. Overview & Core Concept

- **What It Does**: Diagnoses and fixes the root causes of dual-voice overlap, upgrades the narrator to a consistent 10–12-year-old friendly Bangladeshi boy vocal character across every static and dynamic prompt, and verifies all 4 games, 100+ sequential levels, interactive buttons, and lifecycle states.
- **Target Audience / Persona**: Preschool and early-primary children aged 3 to 6 years in Bangladesh and their parents/teachers.
- **Key Value**: Guarantees that at most **one** calm, natural youthful Bengali voice stream can ever be audible at any moment, with zero mature TTS leakage, zero recomposition duplicates, and instant cleanup when switching screens or backgrounding the app.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Silent Launch & Welcome**: Cold launch displays the Splash screen and transitions to the Welcome screen without unexpected auto-speech. Tapping the Play (`▶`) button plays `"হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।"` using the single youthful boy voice and enters the Home Dashboard.
  2. **Entering a Game & Target Prompt**: Selecting any of the 4 games cancels any prior speech, clears the queue, and speaks the current target prompt once (deduplicated against Compose recompositions).
  3. **Answering & Sequential Feedback**: Tapping the correct match plays the subtle chime and speaks `"খুব ভালো হয়েছে! এটি সঠিক উত্তর।"`. The next target's instruction waits in the single-slot coordinator queue and plays smoothly only after the praise clip completes.
  4. **Manual Replay & Rapid Tapping**: Tapping the Speaker Replay button (`🔊`) explicitly bypasses deduplication (`isManualReplay = true`), stops any active narration immediately, clears queued items, and plays the current instruction cleanly from the start.
  5. **Screen Exit, Pause & Lifecycle Transitions**: Pressing Pause, Back, Home, Level Map, or backgrounding the app (`onPause`/`onStop`) immediately halts active narration and flushes pending speech requests.
- **Visual Identity & Theme**:
  - Preserves the **PlayKids** brand identity (`APPROVED_BRAND_NAME = "PlayKids"`), Learning Star + Open Book + Play Symbol (`▶`) mascot across all 8 logo variants, vibrant 3D world backgrounds, and high-contrast Material 3 cards with 48dp+ child-friendly touch targets.
- **Interactive Feedback & Motion**:
  - Smooth spring-based scale feedback on cards, gentle normalized sound effects with automatic voice-priority ducking, and `reducedMotion` support for low-end devices.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Root-Cause Fix for Dual-Voice Overlap (`MediaPlayer` vs. `TextToSpeech` Race)**
  - *Root Cause Identified*: Previously, when a child matched an item, `speakPraise()` started `MediaPlayer` for the bundled `.mp3` praise clip while `LaunchedEffect` simultaneously triggered `speak(nextPrompt)` on the next frame. When `nextPrompt` contained a dynamic shape or colour name not in the 18 static files, `speak()` fell through to Android `TextToSpeech` without stopping `MediaPlayer`—causing the bundled voice and the device's mature Android TTS voice to speak at the exact same time.
  - *Chosen Approach*: Replace ad-hoc playback with a single `VoicePlaybackCoordinator` that enforces mutual exclusion (`stopAllActiveEngines()` before any new playback), queues follow-up target instructions behind active praise clips, and bundles offline audio clips for all dynamic shape, colour, and combination prompts so Android TTS is completely bypassed when bundled assets exist.
  - *Why*: Eliminates both simultaneous engine overlap and sequential voice-character switching.
- **Decision 2: Speech Request Deduplication & Lifecycle Ownership**
  - *Chosen Approach*: Assign each speech event a deterministic key (`screenId + level + roundIndex + promptText`). Automatic `LaunchedEffect` triggers with the same key are ignored if already spoken for the active state, while manual Speaker Replay taps pass `forceReplay = true`. Attach `DisposableEffect` on every game screen and `onPause`/`onStop` hooks in the host activity to stop narration on exit.
  - *Why*: Prevents Compose recompositions, configuration states, or rapid navigation from firing duplicate narrations.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌───────────────────────────────────────────────────────────────────────────┐
│                        MainActivity Lifecycle Host                        │
│        (onPause / onStop / Screen Navigation -> stopAndClearQueue)        │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
        ┌─────────────────────────────┼─────────────────────────────┐
        ▼                             ▼                             ▼
┌───────────────┐             ┌───────────────┐             ┌───────────────┐
│  4 Game UIs   │             │  Level Map &  │             │ParentDashboard│
│ (Emoji,Shape, │             │PhysicalPract. │             │ Voice Preview │
│ColorShape,Pap)│             │    Screens    │             │ & Audio Toggles│
└───────┬───────┘             └───────┬───────┘             └───────┬───────┘
        │                             │                             │
        └─────────────────────────────┼─────────────────────────────┘
                                      ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                   VoicePlaybackCoordinator (Single Owner)                 │
│  • Request Deduplication (lastDedupeKey vs. forceManualReplay)            │
│  • Single-Slot Follow-Up Queue (Praise finishes -> Next Prompt starts)    │
│  • Hard Mutual Exclusion (Stops MediaPlayer & TTS before any start)       │
│  • AudioFocus Request (AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)                │
└──────────────────┬─────────────────────────────────────┬──────────────────┘
                   │                                     │
                   ▼ (Priority 1: Exclusive)             ▼ (Priority 2: Only if
┌─────────────────────────────────────┐   ┌─────────────────────────────────┐
│     VoiceAssetRegistry & Player     │   │   Fallback Bengali TTS Engine   │
│  • Complete 10–12yo Boy Offline MP3 │   │  • Strictly DISABLED when local │
│    Library (Intro, Praise, Retry,   │   │    bundled voice pack exists    │
│    Shapes, Colours, Combinations)   │   │  • Never switches to English    │
└─────────────────────────────────────┘   └─────────────────────────────────┘
```

### Data Model & State
- **`SpeechRequest`**: Immutable request record containing `requestId`, `dedupeKey`, `banglaText`, `queueMode` (`INTERRUPT_IMMEDIATELY` vs. `QUEUE_AFTER_CURRENT`), and `isManualReplay`.
- **`VoiceAssetRegistry`**: Maps all V7.0 Part 3 scripts, game introductions, praise/retry phrases, all 10 colours, all 25 2D shapes, all 8 3D shapes, and composite colour-shape instructions to offline `.mp3` assets in `assets/voice/youthful_boy_bn/`. Also supports seamless chained clip playback for two-part composite prompts (e.g., `[colour clip] + [shape clip]`) so every 200-card Extended Colour+Shape combination uses the exact same youthful boy recording without ever invoking Android TTS.
- **`SoundEffectManager`**: Synthesizes the 6 gentle, normalized PCM sound effects on a dedicated background dispatcher with automatic volume ducking whenever `VoicePlaybackCoordinator.isVoiceCurrentlyPlaying()` is true, and reuses static PCM buffers in memory to eliminate per-tap allocations and `Thread.sleep` blocking.

### Interactive Component & State Mapping
- **Cold Launch & Welcome (`TEST 1`)**: Splash transitions silently to Welcome; tapping Play (`▶`) dispatches `INTERRUPT_IMMEDIATELY` for `"হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।"`.
- **Game Instruction & Answer Flow (`TEST 2, 3, 4`)**:
  - Opening any game dispatches the target instruction with a state-bound `dedupeKey`.
  - Wrong answer dispatches `INTERRUPT_IMMEDIATELY` with `"আবার চেষ্টা করো।"` or `"একটু ভালো করে দেখো।"`.
  - Correct answer dispatches `INTERRUPT_IMMEDIATELY` with `"খুব ভালো হয়েছে! এটি সঠিক উত্তর।"`, and the subsequent round's target prompt is enqueued with `QUEUE_AFTER_CURRENT` so it plays cleanly after praise completion.
- **Speaker Replay & Rapid Navigation (`TEST 5, 6, 7, 8, 9, 10`)**:
  - Rapid taps on the Speaker Replay button cancel the active `MediaPlayer` instance and restart the single instruction without overlapping.
  - Navigating between games, opening Pause, or returning to Home invokes `stopAndClearQueue()`, guaranteeing zero bleed-over narration.
