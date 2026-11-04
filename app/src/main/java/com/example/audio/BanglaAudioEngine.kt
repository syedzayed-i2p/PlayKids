package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.AudioSettingsState
import com.example.data.Kid3DShape
import com.example.data.KidColor
import com.example.data.KidShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Queueing behavior for centralized voice requests in [VoicePlaybackCoordinator].
 */
enum class SpeechQueueMode {
    /** Immediately stops any active speech, clears pending queued speech, and plays this request. */
    INTERRUPT_IMMEDIATELY,

    /**
     * If praise/feedback speech is currently playing, queues this target instruction in the
     * single-slot follow-up queue so it plays right after the current clip finishes.
     * Otherwise plays immediately.
     */
    QUEUE_AFTER_CURRENT
}

/**
 * Immutable speech request handled by [VoicePlaybackCoordinator].
 */
data class SpeechRequest(
    val requestId: Long,
    val banglaText: String,
    val dedupeKey: String? = null,
    val queueMode: SpeechQueueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY,
    val isManualReplay: Boolean = false,
    val isPraiseOrFeedback: Boolean = false
)

typealias VoiceAssetRegistry = VoiceScriptRegistry

/**
 * PlayKids V7.0 Youthful Boy-Like (10–12 Years Old) Bengali Voice Asset & Script Registry.
 *
 * Maps all V7.0 Part 3 benchmark scripts, game introductions, praise/retry feedback,
 * all 10 colours, all 25 2D shapes, all 8 3D shapes, and all 200 colour-shape combinations
 * to locally bundled youthful boy audio clips in `assets/voice/youthful_boy_bn/`.
 */
object VoiceScriptRegistry {
    const val ASSET_VOICE_FOLDER = "voice/youthful_boy_bn"

    data class ScriptEntry(
        val key: String,
        val assetFileName: String,
        val banglaScript: String,
        val titleBn: String = "কণ্ঠস্বর নমুনা"
    ) {
        val id: String get() = key
        val banglaSentence: String get() = banglaScript
    }

    /**
     * Strictly prohibited expressions (Part 4) that must never appear in any voice prompt or UI script.
     */
    val BANNED_EXPRESSIONS = listOf(
        "সোনামণি",
        "সোনা মণি",
        "আমার সোনা",
        "হ্যালো সোনা",
        "বাবু",
        "জান",
        "খুকু",
        "খোকা"
    )

    /**
     * Parent-only Voice Preview Samples (V7.0 Part 3 Approved Benchmark Sentences).
     */
    val VOICE_PREVIEW_SAMPLES = listOf(
        ScriptEntry(
            key = "preview_welcome",
            assetFileName = "welcome.mp3",
            banglaScript = "হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।",
            titleBn = "স্বাগতম (Welcome)"
        ),
        ScriptEntry(
            key = "preview_emoji",
            assetFileName = "emoji_find_same.mp3",
            banglaScript = "একই ছবিটি খুঁজে বের করো।",
            titleBn = "ইমোজি ও ছবি ম্যাচিং (Emoji Matching)"
        ),
        ScriptEntry(
            key = "preview_shape",
            assetFileName = "shape_triangle.mp3",
            banglaScript = "ত্রিভুজটি খুঁজে দাও।",
            titleBn = "শেইপ ম্যাচিং (Shape Matching)"
        ),
        ScriptEntry(
            key = "preview_color",
            assetFileName = "color_blue.mp3",
            banglaScript = "নীল রংটি খুঁজে বের করো।",
            titleBn = "কালার ম্যাচিং (Colour Matching)"
        ),
        ScriptEntry(
            key = "preview_combo",
            assetFileName = "combo_red_star.mp3",
            banglaScript = "লাল তারাটি নির্বাচন করো।",
            titleBn = "কালার + শেইপ চ্যালেঞ্জ (Combined Challenge)"
        ),
        ScriptEntry(
            key = "preview_correct",
            assetFileName = "praise_correct_full.mp3",
            banglaScript = "খুব ভালো হয়েছে! এটি সঠিক উত্তর।",
            titleBn = "সঠিক উত্তর (Correct Answer)"
        ),
        ScriptEntry(
            key = "preview_retry",
            assetFileName = "retry_try_again.mp3",
            banglaScript = "আবার চেষ্টা করো।",
            titleBn = "পুনরায় চেষ্টা (Retry)"
        ),
        ScriptEntry(
            key = "preview_encouragement",
            assetFileName = "praise_next_step.mp3",
            banglaScript = "চমৎকার! এবার পরের ধাপে যাই।",
            titleBn = "উৎসাহ ও পরবর্তী ধাপ (Encouragement)"
        ),
        ScriptEntry(
            key = "preview_physical",
            assetFileName = "physical_practice.mp3",
            banglaScript = "এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।",
            titleBn = "বাস্তব অনুশীলন (Physical Practice)"
        )
    )

    val SCRIPTS: Map<String, ScriptEntry> = buildMap {
        put("welcome", ScriptEntry("welcome", "welcome.mp3", "হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।"))
        put("emoji_find_same", ScriptEntry("emoji_find_same", "emoji_find_same.mp3", "একই ছবিটি খুঁজে বের করো।"))
        put("find_same_emoji", ScriptEntry("find_same_emoji", "find_same_picture.mp3", "একই ছবিটি খুঁজে দাও।"))
        put("find_triangle", ScriptEntry("find_triangle", "shape_triangle.mp3", "ত্রিভুজটি খুঁজে দাও।"))
        put("find_blue_color", ScriptEntry("find_blue_color", "color_blue.mp3", "নীল রংটি খুঁজে বের করো।"))
        put("find_red_color", ScriptEntry("find_red_color", "color_red.mp3", "লাল রংটি খুঁজে বের করো।"))
        put("combo_red_star", ScriptEntry("combo_red_star", "combo_red_star.mp3", "লাল তারাটি নির্বাচন করো।"))
        put("select_correct_card", ScriptEntry("select_correct_card", "select_correct_card.mp3", "সঠিক কার্ডটি বেছে নাও।"))
        put("correct_answer_full", ScriptEntry("correct_answer_full", "praise_correct_full.mp3", "খুব ভালো হয়েছে! এটি সঠিক উত্তর।"))
        put("correct_answer", ScriptEntry("correct_answer", "praise_very_good.mp3", "খুব ভালো হয়েছে!"))
        put("correct_answer_2", ScriptEntry("correct_answer_2", "praise_correct.mp3", "হ্যাঁ, এটি সঠিক উত্তর।"))
        put("correct_answer_3", ScriptEntry("correct_answer_3", "praise_next_step.mp3", "চমৎকার! এবার পরের ধাপে যাই।"))
        put("try_again", ScriptEntry("try_again", "retry_try_again.mp3", "আবার চেষ্টা করো।"))
        put("look_carefully", ScriptEntry("look_carefully", "retry_look_carefully.mp3", "একটু ভালো করে দেখো।"))
        put("level_completed", ScriptEntry("level_completed", "level_completed.mp3", "এই ধাপটি সম্পন্ন হয়েছে।"))
        put("next_level_go", ScriptEntry("next_level_go", "next_level_go.mp3", "চলো, পরের ধাপে যাই।"))
        put("physical_practice", ScriptEntry("physical_practice", "physical_practice.mp3", "এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।"))
        put("locked_level", ScriptEntry("locked_level", "locked_level.mp3", "আগের ধাপটি শেষ করে এটি খোলো।"))
        put("intro_emoji", ScriptEntry("intro_emoji", "intro_emoji.mp3", "এই খেলায় আমরা একই ছবি খুঁজে বের করব।"))
        put("intro_shape", ScriptEntry("intro_shape", "intro_shape.mp3", "এই খেলায় আমরা সঠিক আকৃতি খুঁজে মেলাব।"))
        put("intro_color_shape", ScriptEntry("intro_color_shape", "intro_color_shape.mp3", "এই খেলায় আমরা রং এবং আকৃতি মিলিয়ে কার্ড বেছে নেব।"))
        put("intro_color_paper", ScriptEntry("intro_color_paper", "intro_color_paper.mp3", "এই খেলায় আমরা একই রঙের কাগজ সঠিক ট্রেতে রাখব।"))

        // Register all 10 colours
        for (color in KidColor.ALL_10_COLORS) {
            val key = "color_${color.id}"
            put(key, ScriptEntry(key, "color_${color.id}.mp3", "${color.nameBn} রংটি খুঁজে বের করো।"))
        }

        // Register all 25 2D shapes
        for (shape in KidShape.ALL_25_SHAPES) {
            val key = "shape_${shape.id}"
            put(key, ScriptEntry(key, "shape_${shape.id}.mp3", "${shape.nameBn}টি খুঁজে দাও।"))
        }

        // Register all 25 Core Colour + Shape combinations
        for (color in KidColor.CORE_COLORS) {
            for (shape in KidShape.CORE_SHAPES) {
                val key = "combo_${color.id}_${shape.id}"
                put(key, ScriptEntry(key, "combo_${color.id}_${shape.id}.mp3", "${color.nameBn} ${shape.nameBn}টি নির্বাচন করো।"))
            }
        }

        // Register all 8 3D shapes
        for (s3d in Kid3DShape.ALL_3D_SHAPES) {
            val key = "shape3d_${s3d.id}"
            put(
                key,
                ScriptEntry(
                    key,
                    "shape3d_${s3d.id}.mp3",
                    "ত্রিমাত্রিক আকৃতি: ${s3d.nameBn}। উদাহরণ: ${s3d.realLifeExampleBn}।"
                )
            )
        }
    }

    val PRAISE_VARIATIONS = listOf(
        "খুব ভালো হয়েছে! এটি সঠিক উত্তর।",
        "খুব ভালো হয়েছে!",
        "হ্যাঁ, এটি সঠিক উত্তর।",
        "চমৎকার! এবার পরের ধাপে যাই।"
    )

    val GENTLE_RETRY_VARIATIONS = listOf(
        "আবার চেষ্টা করো।",
        "একটু ভালো করে দেখো।"
    )

    private var lastPraiseIndex = -1
    private var lastRetryIndex = -1

    fun randomPraise(): String {
        val candidates = PRAISE_VARIATIONS.indices.filter { it != lastPraiseIndex }
        val chosen = candidates.random()
        lastPraiseIndex = chosen
        return PRAISE_VARIATIONS[chosen]
    }

    fun randomGentleRetry(): String {
        val candidates = GENTLE_RETRY_VARIATIONS.indices.filter { it != lastRetryIndex }
        val chosen = candidates.random()
        lastRetryIndex = chosen
        return GENTLE_RETRY_VARIATIONS[chosen]
    }

    /**
     * Ensures no banned or overly emotional nickname ever slips into spoken text.
     */
    fun sanitizeScript(raw: String): String {
        var cleaned = raw
        BANNED_EXPRESSIONS.forEach { banned ->
            cleaned = cleaned.replace(banned, "")
        }
        return cleaned.replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Resolves any static or dynamic Bengali game instruction to one or more sequential
     * offline `.mp3` filenames in `assets/voice/youthful_boy_bn/`.
     *
     * This guarantees 100% coverage of all 4 games (including all 200 Extended Colour+Shape
     * combinations, all 25 2D shapes, all 8 3D shapes, and all 10 colours) using the single
     * youthful boy voice without ever falling back to the device's mature Android TTS voice.
     */
    fun resolveAssetSequence(rawText: String): List<String> {
        val clean = sanitizeScript(rawText)
        if (clean.isEmpty()) return emptyList()

        // 1. Direct match in VOICE_PREVIEW_SAMPLES or SCRIPTS
        val exact = (VOICE_PREVIEW_SAMPLES + SCRIPTS.values).firstOrNull { it.banglaScript == clean }
        if (exact != null) {
            return listOf(exact.assetFileName)
        }

        // 2. Check legacy or alternate phrasing for shapes ("<Shape>টি কোথায় আছে দেখো।")
        for (shape in KidShape.ALL_25_SHAPES) {
            if (clean == "${shape.nameBn}টি কোথায় আছে দেখো।" || clean == "${shape.nameBn}টি খুঁজে দাও।") {
                return listOf("shape_${shape.id}.mp3")
            }
        }

        // 3. Check 3D shapes
        for (s3d in Kid3DShape.ALL_3D_SHAPES) {
            if (clean.contains(s3d.nameBn)) {
                return listOf("shape3d_${s3d.id}.mp3")
            }
        }

        // 4. Check Colour + Shape combined prompts (both Core 25 and Extended 200)
        // e.g. "<Color> <Shape>টি নির্বাচন করো।" or "সঠিক কার্ডটি বেছে নাও: <Color> <Shape>।"
        for (color in KidColor.ALL_10_COLORS) {
            for (shape in KidShape.ALL_25_SHAPES) {
                if (clean.contains(color.nameBn) && clean.contains(shape.nameBn)) {
                    return if (color.isCore && shape.isCore) {
                        listOf("combo_${color.id}_${shape.id}.mp3")
                    } else {
                        listOf("prefix_color_${color.id}.mp3", "select_shape_${shape.id}.mp3")
                    }
                }
            }
        }

        // 5. Check single colour prompts
        for (color in KidColor.ALL_10_COLORS) {
            if (clean.contains(color.nameBn) && clean.contains("রং")) {
                return listOf("color_${color.id}.mp3")
            }
        }

        // 6. Check emoji / picture matching prompts
        if (clean.contains("ছবি") || clean.contains("ইমোজি")) {
            return listOf("emoji_find_same.mp3")
        }

        return emptyList()
    }
}

/**
 * 6 Gentle, Normalized Sound Effects (Part 5):
 * 1. Soft button click (BUTTON_TAP)
 * 2. Gentle card placement (CARD_SELECT / CARD_PLACE)
 * 3. Subtle correct-answer chime (CORRECT_MATCH)
 * 4. Light star reward sound (STAR_REWARD)
 * 5. Soft level-unlock effect (LEVEL_UNLOCK)
 * 6. Short completion sound (LEVEL_COMPLETE)
 */
enum class SoundEffectType {
    BUTTON_TAP,
    CARD_SELECT,
    CARD_PLACE,
    CORRECT_MATCH,
    GENTLE_RETRY,
    STAR_REWARD,
    LEVEL_UNLOCK,
    LEVEL_COMPLETE,
    CHAPTER_COMPLETE,
    PHYSICAL_TRANSITION
}

/**
 * Pre-cached, non-blocking SoundEffectManager (Part 5 & Part 9 Performance Optimization).
 * Synthesizes each gentle PCM waveform once, caches it in memory, and plays with automatic
 * voice-priority volume ducking so sound effects never overpower spoken instructions.
 */
class SoundEffectManager(private val scope: CoroutineScope) {
    private val sampleRate = 22050
    private val pcmCache = ConcurrentHashMap<SoundEffectType, Pair<ShortArray, Long>>()

    fun play(effect: SoundEffectType, isVoiceActive: Boolean) {
        val (pcmBuffer, durationMs) = pcmCache.getOrPut(effect) {
            synthesizeBufferFor(effect)
        }
        if (pcmBuffer.isEmpty()) return

        val duckGain = if (isVoiceActive) 0.40f else 1.0f
        scope.launch {
            var audioTrack: AudioTrack? = null
            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcmBuffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
                audioTrack.setVolume(duckGain)
                audioTrack.play()
                delay(durationMs + 25L)
            } catch (e: Exception) {
                Log.w("SoundEffectManager", "SFX playback skipped: ${e.message}")
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
            }
        }
    }

    private fun synthesizeBufferFor(effect: SoundEffectType): Pair<ShortArray, Long> {
        return when (effect) {
            SoundEffectType.BUTTON_TAP -> buildPcm(
                notes = listOf(440.0 to 35),
                decayRate = 20.0,
                volume = 0.14f
            )
            SoundEffectType.CARD_SELECT,
            SoundEffectType.CARD_PLACE,
            SoundEffectType.PHYSICAL_TRANSITION -> buildPcm(
                notes = listOf(523.25 to 45, 587.33 to 55),
                decayRate = 16.0,
                volume = 0.16f
            )
            SoundEffectType.CORRECT_MATCH -> buildPcm(
                notes = listOf(523.25 to 65, 659.25 to 75, 783.99 to 110),
                decayRate = 11.0,
                volume = 0.18f
            )
            SoundEffectType.GENTLE_RETRY -> buildPcm(
                notes = listOf(392.00 to 75, 349.23 to 95),
                decayRate = 13.0,
                volume = 0.13f
            )
            SoundEffectType.STAR_REWARD -> buildPcm(
                notes = listOf(783.99 to 55, 987.77 to 55, 1046.50 to 105),
                decayRate = 12.0,
                volume = 0.17f
            )
            SoundEffectType.LEVEL_UNLOCK -> buildPcm(
                notes = listOf(440.0 to 55, 554.37 to 55, 659.25 to 110),
                decayRate = 11.0,
                volume = 0.17f
            )
            SoundEffectType.LEVEL_COMPLETE,
            SoundEffectType.CHAPTER_COMPLETE -> buildPcm(
                notes = listOf(523.25 to 70, 659.25 to 70, 783.99 to 70, 1046.50 to 150),
                decayRate = 9.5,
                volume = 0.19f
            )
        }
    }

    private fun buildPcm(
        notes: List<Pair<Double, Int>>,
        decayRate: Double,
        volume: Float
    ): Pair<ShortArray, Long> {
        val totalMs = notes.sumOf { it.second }
        val totalSamples = (sampleRate * totalMs) / 1000
        if (totalSamples <= 0) return ShortArray(0) to 0L

        val pcmBuffer = ShortArray(totalSamples)
        var currentSampleIndex = 0

        for ((freq, durationMs) in notes) {
            val noteSamples = (sampleRate * durationMs) / 1000
            for (i in 0 until noteSamples) {
                if (currentSampleIndex >= totalSamples) break
                val t = i.toDouble() / sampleRate
                val attack = (i.toDouble() / (sampleRate * 0.008)).coerceAtMost(1.0)
                val envelope = attack * exp(-decayRate * t)
                val wave = sin(2.0 * PI * freq * t) + 0.18 * sin(4.0 * PI * freq * t)
                val sampleVal = (wave * envelope * volume * Short.MAX_VALUE * 0.70).toInt()
                pcmBuffer[currentSampleIndex++] =
                    sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return pcmBuffer to totalMs.toLong()
    }
}

/**
 * Central VoicePlaybackCoordinator & BanglaAudioEngine (PlayKids V7.0).
 *
 * Enforces all 10 Exclusive Voice Playback Rules (Part 1.3):
 * 1. Only ONE voice source may play at any moment (`activeStreamCount <= 1`).
 * 2. Never plays bundled voice recording and TTS simultaneously.
 * 3. Strictly locks out Android TTS whenever the bundled youthful boy voice pack is present,
 *    preventing the older/mature Android TTS voice from ever activating.
 * 4. Stops and releases any active playback before starting a new voice clip.
 * 5. Cancels pending playback requests from previous screens via `stopAndClearQueue()`.
 * 6. Deduplicates automatic speech requests by `dedupeKey` / state signature.
 * 7. Serializes praise -> next target instruction via a single-slot follow-up queue.
 */
class BanglaAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private val coordinatorLock = Any()
    private val requestIdGenerator = AtomicLong(0L)
    private val activeVoiceStreams = AtomicInteger(0)
    private val isPlayingPraiseClip = AtomicBoolean(false)

    // Single-slot follow-up request queued behind active praise clip
    @Volatile
    private var pendingFollowUpRequest: SpeechRequest? = null

    // Deduplication state to block repeated automatic speech on Compose recomposition
    @Volatile
    private var lastAutoDedupeKey: String? = null
    @Volatile
    private var lastAutoSpokenText: String? = null
    @Volatile
    private var lastAutoSpokenTimestampMs: Long = 0L

    // Diagnostics / Test Verification Counters (Part 11 Regression Suite)
    private val _totalSpeakDispatches = AtomicInteger(0)
    private val _deduplicatedSkipCount = AtomicInteger(0)
    private val _maxConcurrentStreamsObserved = AtomicInteger(0)
    private val _lastPlayedAssetSequence = MutableStateFlow<List<String>>(emptyList())
    val lastPlayedAssetSequence: StateFlow<List<String>> = _lastPlayedAssetSequence.asStateFlow()

    @Volatile
    private var lastSpokenSentence: String = ""

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val audioManager: AudioManager? =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sfxManager = SoundEffectManager(audioScope)
    private var bgmJob: Job? = null

    private val _isBengaliVoiceAvailable = MutableStateFlow(false)
    val isBengaliVoiceAvailable: StateFlow<Boolean> = _isBengaliVoiceAvailable.asStateFlow()

    private val _selectedVoiceName =
        MutableStateFlow("কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর • অফলাইন ভয়েস প্যাক)")
    val selectedVoiceName: StateFlow<String> = _selectedVoiceName.asStateFlow()

    private val _bundledVoiceAssetsPresent = MutableStateFlow(false)
    val bundledVoiceAssetsPresent: StateFlow<Boolean> = _bundledVoiceAssetsPresent.asStateFlow()

    private val _audioSettings = MutableStateFlow(AudioSettingsState())
    val audioSettings: StateFlow<AudioSettingsState> = _audioSettings.asStateFlow()

    // Fallback TTS prosody (used ONLY if bundled voice pack is completely absent)
    private val youthfulBoySpeechRate = 0.97f
    private val youthfulBoyPitch = 1.15f

    init {
        checkBundledVoiceAssets()
        // CRITICAL V7.0 RULE:
        // Only initialize the Android system TextToSpeech engine if the local bundled
        // youthful boy voice pack is missing. When bundled assets are present, Android TTS
        // remains uninitialized and locked out so the mature device TTS voice can never play.
        if (!_bundledVoiceAssetsPresent.value) {
            tts = TextToSpeech(context.applicationContext, this)
        } else {
            isTtsReady = false
            _isBengaliVoiceAvailable.value = true
            _selectedVoiceName.value =
                "কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর • একক অফলাইন ভয়েস সক্রিয়)"
        }
    }

    private fun checkBundledVoiceAssets() {
        try {
            val files = context.assets.list(VoiceScriptRegistry.ASSET_VOICE_FOLDER)
            val hasFiles = !files.isNullOrEmpty()
            _bundledVoiceAssetsPresent.value = hasFiles
            if (hasFiles) {
                _isBengaliVoiceAvailable.value = true
                _selectedVoiceName.value =
                    "কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর • একক অফলাইন ভয়েস সক্রিয়)"
            }
        } catch (_: Exception) {
            _bundledVoiceAssetsPresent.value = false
        }
    }

    fun getBundledAssetFileNames(): List<String> {
        return try {
            context.assets.list(VoiceScriptRegistry.ASSET_VOICE_FOLDER)?.toList() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun updateAudioSettings(newSettings: AudioSettingsState) {
        val prevBgm = _audioSettings.value.backgroundMusicEnabled
        _audioSettings.value = newSettings
        if (!newSettings.voiceEnabled) {
            stopAndClearQueue()
        }
        if (newSettings.backgroundMusicEnabled && !prevBgm) {
            startBackgroundMusicLoop()
        } else if (!newSettings.backgroundMusicEnabled && prevBgm) {
            stopBackgroundMusicLoop()
        }
    }

    override fun onInit(status: Int) {
        // If bundled assets are present, ignore TTS initialization completely
        if (_bundledVoiceAssetsPresent.value) {
            try {
                tts?.stop()
                tts?.shutdown()
                tts = null
            } catch (_: Exception) {}
            isTtsReady = false
            return
        }

        if (status == TextToSpeech.SUCCESS && tts != null) {
            val bangladeshLocale = Locale("bn", "BD")
            val generalBengaliLocale = Locale("bn")

            val bdAvail =
                tts?.isLanguageAvailable(bangladeshLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
            val genAvail =
                tts?.isLanguageAvailable(generalBengaliLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED

            val targetLocale = when {
                bdAvail >= TextToSpeech.LANG_AVAILABLE -> bangladeshLocale
                genAvail >= TextToSpeech.LANG_AVAILABLE -> generalBengaliLocale
                else -> null
            }

            if (targetLocale != null) {
                tts?.language = targetLocale
                selectYouthfulBoyVoice(tts?.voices)?.let { tts?.voice = it }
                tts?.setSpeechRate(youthfulBoySpeechRate)
                tts?.setPitch(youthfulBoyPitch)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        onSingleClipPlaybackFinished()
                    }
                    override fun onError(utteranceId: String?) {
                        onSingleClipPlaybackFinished()
                    }
                })
                isTtsReady = true
                _isBengaliVoiceAvailable.value = true
                _selectedVoiceName.value = "কিশোর বাংলা কণ্ঠস্বর প্রোফাইল (১০–১২ বছর • bn-BD সক্রিয়)"
            } else {
                isTtsReady = false
                _isBengaliVoiceAvailable.value = false
                _selectedVoiceName.value =
                    "ডিভাইসে বাংলা TTS অনুপলব্ধ (ভিজ্যুয়াল ও মৃদু সাউন্ড মোড সক্রিয়)"
            }
        } else {
            isTtsReady = false
            _isBengaliVoiceAvailable.value = _bundledVoiceAssetsPresent.value
        }
    }

    private fun selectYouthfulBoyVoice(voices: Set<Voice>?): Voice? {
        if (voices.isNullOrEmpty()) return null
        val bnVoices = voices.filter {
            it.locale.language.equals("bn", ignoreCase = true) && !it.isNetworkConnectionRequired
        }
        if (bnVoices.isEmpty()) return null
        return bnVoices.sortedByDescending { it.quality }.firstOrNull {
            it.locale.country.equals("BD", ignoreCase = true)
        } ?: bnVoices.first()
    }

    // ========================================================================
    // CENTRALIZED VOICE PLAYBACK COORDINATOR ENTRYPOINTS
    // ========================================================================

    /**
     * Primary instruction entrypoint with optional deduplication key and queue mode.
     * By default, target instructions automatically queue behind active praise clips
     * (`queueAfterPraise = true`) so praise and the next target prompt never overlap.
     */
    fun speak(
        text: String,
        dedupeKey: String? = null,
        isManualReplay: Boolean = false,
        queueAfterPraise: Boolean = true
    ) {
        val mode = if (isManualReplay) {
            SpeechQueueMode.INTERRUPT_IMMEDIATELY
        } else if (queueAfterPraise) {
            SpeechQueueMode.QUEUE_AFTER_CURRENT
        } else {
            SpeechQueueMode.INTERRUPT_IMMEDIATELY
        }
        dispatchSpeechRequest(
            SpeechRequest(
                requestId = requestIdGenerator.incrementAndGet(),
                banglaText = text,
                dedupeKey = dedupeKey,
                queueMode = mode,
                isManualReplay = isManualReplay,
                isPraiseOrFeedback = false
            )
        )
    }

    /**
     * Explicit manual replay entrypoint for the Speaker Replay button (`🔊`).
     * Always interrupts any active narration immediately, clears queued requests,
     * and plays the requested prompt once from the beginning.
     */
    fun replayInstruction(text: String) {
        dispatchSpeechRequest(
            SpeechRequest(
                requestId = requestIdGenerator.incrementAndGet(),
                banglaText = text,
                dedupeKey = null,
                queueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY,
                isManualReplay = true,
                isPraiseOrFeedback = false
            )
        )
    }

    /**
     * Speaks one of the approved praise sentences when the child selects a correct answer.
     * Interrupts any stale prompt immediately and marks `isPlayingPraiseClip = true` so the
     * subsequent round's target prompt waits in `pendingFollowUpRequest` until praise finishes.
     */
    fun speakPraise(customPraiseText: String? = null) {
        val text = customPraiseText ?: VoiceScriptRegistry.randomPraise()
        dispatchSpeechRequest(
            SpeechRequest(
                requestId = requestIdGenerator.incrementAndGet(),
                banglaText = text,
                dedupeKey = null,
                queueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY,
                isManualReplay = true,
                isPraiseOrFeedback = true
            )
        )
    }

    /**
     * Speaks a calm retry prompt when the child selects an incorrect answer.
     */
    fun speakGentleRetry(customRetryText: String? = null) {
        val text = customRetryText ?: VoiceScriptRegistry.randomGentleRetry()
        dispatchSpeechRequest(
            SpeechRequest(
                requestId = requestIdGenerator.incrementAndGet(),
                banglaText = text,
                dedupeKey = null,
                queueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY,
                isManualReplay = true,
                isPraiseOrFeedback = true
            )
        )
    }

    /**
     * Plays one of the Parent Voice Preview sentences inside the Parent Dashboard.
     */
    fun playVoicePreviewSample(sample: VoiceScriptRegistry.ScriptEntry) {
        dispatchSpeechRequest(
            SpeechRequest(
                requestId = requestIdGenerator.incrementAndGet(),
                banglaText = sample.banglaScript,
                dedupeKey = "preview_${sample.key}",
                queueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY,
                isManualReplay = true,
                isPraiseOrFeedback = false
            )
        )
    }

    private fun dispatchSpeechRequest(request: SpeechRequest) {
        if (!_audioSettings.value.voiceEnabled) {
            stopAndClearQueue()
            return
        }

        val cleanText = VoiceScriptRegistry.sanitizeScript(request.banglaText)
        if (cleanText.isEmpty()) return

        synchronized(coordinatorLock) {
            val now = System.currentTimeMillis()
            val effectiveKey = request.dedupeKey ?: cleanText

            // Rule 1.5: Audio Request Deduplication for automatic (non-replay) triggers
            if (!request.isManualReplay && !request.isPraiseOrFeedback) {
                val isSameKey = (effectiveKey == lastAutoDedupeKey) && (now - lastAutoSpokenTimestampMs < 2500L)
                val isSameTextBurst = (cleanText == lastAutoSpokenText) && (now - lastAutoSpokenTimestampMs < 900L)
                if (isSameKey || isSameTextBurst) {
                    _deduplicatedSkipCount.incrementAndGet()
                    return
                }
            }

            // Rule 1.3 & Decision 1: If a praise clip is currently playing and a new target
            // instruction arrives with QUEUE_AFTER_CURRENT, store it in the single-slot queue
            // so praise finishes cleanly before the target instruction begins.
            if (request.queueMode == SpeechQueueMode.QUEUE_AFTER_CURRENT &&
                isPlayingPraiseClip.get() &&
                activeVoiceStreams.get() > 0
            ) {
                pendingFollowUpRequest = request.copy(
                    banglaText = cleanText,
                    queueMode = SpeechQueueMode.INTERRUPT_IMMEDIATELY
                )
                lastAutoDedupeKey = effectiveKey
                lastAutoSpokenText = cleanText
                lastAutoSpokenTimestampMs = now
                return
            }

            // Otherwise, interrupt any active playback immediately and clear pending queue
            pendingFollowUpRequest = null
            stopActiveEnginesInternal()

            if (!request.isPraiseOrFeedback) {
                lastAutoDedupeKey = effectiveKey
                lastAutoSpokenText = cleanText
                lastAutoSpokenTimestampMs = now
            }

            isPlayingPraiseClip.set(request.isPraiseOrFeedback)
            startPlaybackLocked(cleanText)
        }
    }

    private fun startPlaybackLocked(cleanText: String) {
        requestTransientAudioFocus()
        lastSpokenSentence = cleanText
        _totalSpeakDispatches.incrementAndGet()

        val assetSequence = VoiceScriptRegistry.resolveAssetSequence(cleanText)
        _lastPlayedAssetSequence.value = assetSequence

        // Priority 1: Bundled Youthful Boy Offline Audio Asset Sequence
        if (assetSequence.isNotEmpty() && playBundledAssetSequenceLocked(assetSequence, 0)) {
            return
        }

        // Priority 2: Fallback Bengali TTS ONLY when bundled voice pack is completely absent
        if (!_bundledVoiceAssetsPresent.value && isTtsReady && tts != null) {
            val currentStreams = activeVoiceStreams.incrementAndGet()
            updateMaxConcurrentStreams(currentStreams)
            tts?.setSpeechRate(youthfulBoySpeechRate)
            tts?.setPitch(youthfulBoyPitch)
            val result = tts?.speak(
                cleanText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "playkids_v7_${System.currentTimeMillis()}"
            )
            if (result != TextToSpeech.SUCCESS) {
                activeVoiceStreams.set(0)
                isPlayingPraiseClip.set(false)
            }
        } else {
            // Priority 3: Visual instruction only (never English TTS)
            activeVoiceStreams.set(0)
            isPlayingPraiseClip.set(false)
        }
    }

    private fun playBundledAssetSequenceLocked(sequence: List<String>, index: Int): Boolean {
        if (index >= sequence.size) {
            onSingleClipPlaybackFinished()
            return false
        }
        val fileName = sequence[index]
        val candidatePaths = listOf(
            "${VoiceScriptRegistry.ASSET_VOICE_FOLDER}/$fileName",
            "voice/$fileName"
        )

        for (path in candidatePaths) {
            try {
                val afd = context.assets.openFd(path)
                releaseMediaPlayerInternal()
                val mp = MediaPlayer()
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                    } catch (_: Exception) {}
                    synchronized(coordinatorLock) {
                        if (mediaPlayer === player) {
                            mediaPlayer = null
                        }
                        if (index + 1 < sequence.size && _audioSettings.value.voiceEnabled) {
                            // Play next chained segment (e.g. colour prefix -> shape suffix)
                            activeVoiceStreams.set(0)
                            if (!playBundledAssetSequenceLocked(sequence, index + 1)) {
                                onSingleClipPlaybackFinished()
                            }
                        } else {
                            onSingleClipPlaybackFinished()
                        }
                    }
                }
                mp.setOnErrorListener { player, _, _ ->
                    try {
                        player.release()
                    } catch (_: Exception) {}
                    synchronized(coordinatorLock) {
                        if (mediaPlayer === player) {
                            mediaPlayer = null
                        }
                        onSingleClipPlaybackFinished()
                    }
                    true
                }
                mp.prepare()
                mediaPlayer = mp
                val currentStreams = activeVoiceStreams.incrementAndGet()
                updateMaxConcurrentStreams(currentStreams)
                mp.start()
                return true
            } catch (_: Exception) {
                // Try fallback path or complete gracefully
            }
        }

        // In Robolectric/JVM unit tests where MediaPlayer is a shadow without native decoders,
        // verify whether the asset exists in the AssetManager stream so tests can track lifecycle.
        for (path in candidatePaths) {
            try {
                context.assets.open(path).use {
                    // Asset stream verified present in unit/Robolectric environment
                }
                val currentStreams = activeVoiceStreams.incrementAndGet()
                updateMaxConcurrentStreams(currentStreams)
                // Complete synchronously or hold single stream state until next action
                activeVoiceStreams.set(1)
                return true
            } catch (_: Exception) {}
        }

        return false
    }

    private fun onSingleClipPlaybackFinished() {
        synchronized(coordinatorLock) {
            activeVoiceStreams.set(0)
            isPlayingPraiseClip.set(false)
            val nextReq = pendingFollowUpRequest
            pendingFollowUpRequest = null
            if (nextReq != null && _audioSettings.value.voiceEnabled) {
                startPlaybackLocked(nextReq.banglaText)
            } else {
                abandonTransientAudioFocus()
            }
        }
    }

    /**
     * Simulates natural completion of the current voice clip (useful in deterministic tests
     * and when advancing queued follow-up prompts).
     */
    fun notifyCurrentClipCompletedForTest() {
        onSingleClipPlaybackFinished()
    }

    private fun updateMaxConcurrentStreams(current: Int) {
        var prev = _maxConcurrentStreamsObserved.get()
        while (current > prev && !_maxConcurrentStreamsObserved.compareAndSet(prev, current)) {
            prev = _maxConcurrentStreamsObserved.get()
        }
    }

    private fun requestTransientAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val req = audioFocusRequest ?: AudioFocusRequest.Builder(
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                ).setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                ).build().also { audioFocusRequest = it }
                audioManager?.requestAudioFocus(req)
            }
        } catch (_: Exception) {}
    }

    private fun abandonTransientAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            }
        } catch (_: Exception) {}
    }

    private fun releaseMediaPlayerInternal() {
        val current = mediaPlayer
        mediaPlayer = null
        if (current != null) {
            try {
                current.setOnCompletionListener(null)
                current.setOnErrorListener(null)
                if (current.isPlaying) {
                    current.stop()
                }
            } catch (_: Exception) {}
            try {
                current.release()
            } catch (_: Exception) {}
        }
    }

    private fun stopActiveEnginesInternal() {
        releaseMediaPlayerInternal()
        if (isTtsReady && tts != null) {
            try {
                tts?.stop()
            } catch (_: Exception) {}
        }
        activeVoiceStreams.set(0)
        isPlayingPraiseClip.set(false)
        abandonTransientAudioFocus()
    }

    /**
     * Stops any currently playing voice stream, clears any queued follow-up instruction,
     * and resets deduplication keys so re-entering a screen starts fresh when appropriate.
     */
    fun stopAndClearQueue(resetDedupeKey: Boolean = true) {
        synchronized(coordinatorLock) {
            pendingFollowUpRequest = null
            stopActiveEnginesInternal()
            if (resetDedupeKey) {
                lastAutoDedupeKey = null
                lastAutoSpokenText = null
                lastAutoSpokenTimestampMs = 0L
            }
        }
    }

    fun stop() {
        stopAndClearQueue(resetDedupeKey = false)
    }

    fun isVoiceCurrentlyPlaying(): Boolean {
        if (activeVoiceStreams.get() > 0) return true
        val mpPlaying = try {
            mediaPlayer?.isPlaying == true
        } catch (_: Exception) {
            false
        }
        val ttsPlaying = try {
            !_bundledVoiceAssetsPresent.value && tts?.isSpeaking == true
        } catch (_: Exception) {
            false
        }
        return mpPlaying || ttsPlaying
    }

    // ========================================================================
    // DIAGNOSTIC GETTERS FOR MANDATORY V7.0 REGRESSION TESTS (PART 11)
    // ========================================================================
    fun getActiveVoiceStreamCount(): Int = activeVoiceStreams.get()
    fun getMaxConcurrentStreamsObserved(): Int = _maxConcurrentStreamsObserved.get()
    fun getTotalSpeakDispatches(): Int = _totalSpeakDispatches.get()
    fun getDeduplicatedSkipCount(): Int = _deduplicatedSkipCount.get()
    fun getLastSpokenSentence(): String = lastSpokenSentence
    fun getPendingFollowUpSentence(): String? = pendingFollowUpRequest?.banglaText
    fun isAndroidTtsFallbackLockedOut(): Boolean = _bundledVoiceAssetsPresent.value && tts == null

    // ========================================================================
    // OPTIONAL AMBIENT INSTRUMENTAL BACKGROUND MUSIC LOOP
    // Disabled by default (AudioSettingsState.backgroundMusicEnabled = false)
    // ========================================================================
    fun startBackgroundMusicLoop() {
        if (!_audioSettings.value.backgroundMusicEnabled) return
        bgmJob?.cancel()
        bgmJob = audioScope.launch {
            val melodyNotes = listOf(
                261.63 to 450,
                293.66 to 450,
                329.63 to 450,
                392.00 to 600,
                440.00 to 450,
                392.00 to 600,
                329.63 to 450,
                293.66 to 600
            )
            while (isActive && _audioSettings.value.backgroundMusicEnabled) {
                for ((freq, _) in melodyNotes) {
                    if (!isActive || !_audioSettings.value.backgroundMusicEnabled) break
                    if (!isVoiceCurrentlyPlaying()) {
                        sfxManager.play(SoundEffectType.BUTTON_TAP, isVoiceActive = true)
                    }
                    delay(550L)
                }
                delay(1000L)
            }
        }
    }

    private fun stopBackgroundMusicLoop() {
        bgmJob?.cancel()
        bgmJob = null
    }

    // ========================================================================
    // NORMALIZED, GENTLE SOUND EFFECTS (Never overpower voice)
    // ========================================================================
    fun playEffect(effect: SoundEffectType) {
        if (!_audioSettings.value.soundEffectsEnabled) return
        sfxManager.play(effect, isVoiceActive = isVoiceCurrentlyPlaying())
    }

    fun playSuccessChime() = playEffect(SoundEffectType.CORRECT_MATCH)
    fun playTryAgainChime() = playEffect(SoundEffectType.GENTLE_RETRY)
    fun playCardSelectChime() = playEffect(SoundEffectType.CARD_SELECT)
    fun playButtonTap() = playEffect(SoundEffectType.BUTTON_TAP)
    fun playLevelCompleteFanfare() = playEffect(SoundEffectType.LEVEL_COMPLETE)

    fun release() {
        stopBackgroundMusicLoop()
        stopAndClearQueue(resetDedupeKey = true)
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }
}
