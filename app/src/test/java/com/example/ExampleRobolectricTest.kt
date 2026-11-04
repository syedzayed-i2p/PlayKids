package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.BanglaAudioEngine
import com.example.audio.VoiceScriptRegistry
import com.example.data.AudioSettingsState
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Mandatory V7.0 Audio Conflict Regression Tests (Part 11: TEST 1 through TEST 10).
 *
 * Verifies on the Android runtime (via Robolectric) that:
 * - At most ONE spoken narration stream is ever active (`maxConcurrentStreamsObserved <= 1`).
 * - The old mature Android TTS voice is strictly locked out (`isAndroidTtsFallbackLockedOut() == true`)
 *   when the bundled youthful boy voice pack (`assets/voice/youthful_boy_bn/`) is present.
 * - Praise -> Next target instruction serializes via the single-slot follow-up queue without overlapping.
 * - Rapid speaker replay taps, rapid game switching, pause/resume, Home navigation, and voice mute
 *   settings obey strict mutual exclusion and deduplication.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  private lateinit var context: Context
  private lateinit var audioEngine: BanglaAudioEngine

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    audioEngine = BanglaAudioEngine(context)
  }

  @After
  fun tearDown() {
    audioEngine.release()
  }

  @Test
  fun test1_ColdLaunchOnlyOneVoiceSourceActiveAndMatureTtsLockedOut() {
    // On cold launch (before user taps Play on Welcome screen), zero voice streams are playing
    assertEquals(
        "Cold launch must not auto-play unexpected speech",
        0,
        audioEngine.getActiveVoiceStreamCount()
    )
    assertTrue(
        "Bundled youthful boy voice assets must be detected on launch",
        audioEngine.bundledVoiceAssetsPresent.value
    )
    assertTrue(
        "Android system TTS must be strictly locked out when bundled voice pack is present",
        audioEngine.isAndroidTtsFallbackLockedOut()
    )

    // Trigger Welcome greeting when Play button is pressed
    audioEngine.speak("হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।", dedupeKey = "welcome_play")
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
    assertEquals(listOf("welcome.mp3"), audioEngine.lastPlayedAssetSequence.value)
  }

  @Test
  fun test2_OpenEmojiMatchingPlayInstructionSingleNarratorAudible() {
    audioEngine.speak(
        text = "একই ছবিটি খুঁজে বের করো।",
        dedupeKey = "emoji_core_L1_R0_smile",
        isManualReplay = false
    )
    assertEquals(
        "Exactly one narrator stream must be active",
        1,
        audioEngine.getActiveVoiceStreamCount()
    )
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
    assertEquals(listOf("emoji_find_same.mp3"), audioEngine.lastPlayedAssetSequence.value)
    assertTrue(audioEngine.isAndroidTtsFallbackLockedOut())
  }

  @Test
  fun test3_SelectWrongAnswerOnlyOneRetryInstructionPlayed() {
    // Initial prompt is active
    audioEngine.speak("ত্রিভুজটি খুঁজে দাও।", dedupeKey = "shape_L1")
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())

    // Child selects wrong answer -> retry immediately interrupts previous prompt without overlap
    audioEngine.speakGentleRetry("আবার চেষ্টা করো।")
    assertEquals(
        "Only one retry voice stream may be active",
        1,
        audioEngine.getActiveVoiceStreamCount()
    )
    assertEquals(
        "Max concurrent streams must never exceed 1",
        1,
        audioEngine.getMaxConcurrentStreamsObserved()
    )
    assertEquals("আবার চেষ্টা করো।", audioEngine.getLastSpokenSentence())
    assertEquals(listOf("retry_try_again.mp3"), audioEngine.lastPlayedAssetSequence.value)
  }

  @Test
  fun test4_SelectCorrectAnswerPraisePlaysOnceAndNextPromptQueuesSequentially() {
    // Child selects correct answer -> Praise starts immediately
    audioEngine.speakPraise("খুব ভালো হয়েছে! এটি সঠিক উত্তর।")
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
    assertEquals("খুব ভালো হয়েছে! এটি সঠিক উত্তর।", audioEngine.getLastSpokenSentence())
    assertEquals(listOf("praise_correct_full.mp3"), audioEngine.lastPlayedAssetSequence.value)

    // Next round's LaunchedEffect fires on the next frame while praise is still playing
    audioEngine.speak(
        text = "নীল রংটি খুঁজে বের করো।",
        dedupeKey = "paper_L1_next",
        queueAfterPraise = true
    )

    // Verify they DO NOT overlap: praise is still the sole active clip, and next prompt is queued
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
    assertEquals("খুব ভালো হয়েছে! এটি সঠিক উত্তর।", audioEngine.getLastSpokenSentence())
    assertEquals("নীল রংটি খুঁজে বের করো।", audioEngine.getPendingFollowUpSentence())

    // When praise clip completes, the queued next instruction starts automatically
    audioEngine.notifyCurrentClipCompletedForTest()
    assertNull(audioEngine.getPendingFollowUpSentence())
    assertEquals("নীল রংটি খুঁজে বের করো।", audioEngine.getLastSpokenSentence())
    assertEquals(listOf("color_blue.mp3"), audioEngine.lastPlayedAssetSequence.value)
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
  }

  @Test
  fun test5_RapidlyTapSpeakerReplayButtonSerializedNoOverlap() {
    repeat(10) {
      audioEngine.replayInstruction("লাল তারাটি নির্বাচন করো।")
      assertEquals(
          "Rapid replay taps must never exceed 1 active stream",
          1,
          audioEngine.getActiveVoiceStreamCount()
      )
    }
    assertEquals(
        "Max concurrent streams across 10 rapid taps must be 1",
        1,
        audioEngine.getMaxConcurrentStreamsObserved()
    )
    assertEquals(10, audioEngine.getTotalSpeakDispatches())
    assertEquals(listOf("combo_red_star.mp3"), audioEngine.lastPlayedAssetSequence.value)
  }

  @Test
  fun test6_MoveRapidlyBetweenAllFourGamesPreviousVoiceStopped() {
    // Game 1: Emoji Matching
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = "g1")
    audioEngine.stopAndClearQueue()
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())

    // Game 2: Shape Matching
    audioEngine.speak("ত্রিভুজটি খুঁজে দাও।", dedupeKey = "g2")
    audioEngine.stopAndClearQueue()
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())

    // Game 3: Colour + Shape Challenge (with queued follow-up)
    audioEngine.speakPraise("খুব ভালো হয়েছে!")
    audioEngine.speak("লাল তারাটি নির্বাচন করো।", dedupeKey = "g3", queueAfterPraise = true)
    assertNotNull(audioEngine.getPendingFollowUpSentence())
    audioEngine.stopAndClearQueue()
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())
    assertNull("Pending follow-up from previous game must be cleared on exit", audioEngine.getPendingFollowUpSentence())

    // Game 4: Colour Paper Matching
    audioEngine.speak("নীল রংটি খুঁজে বের করো।", dedupeKey = "g4")
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
  }

  @Test
  fun test7_PauseAndResumeDoesNotRestartOldSpeechUnexpectedly() {
    val dedupeKey = "shape2d_core_L1_R0_triangle_0"
    audioEngine.speak("ত্রিভুজটি খুঁজে দাও।", dedupeKey = dedupeKey)
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
    val dispatchesBeforePause = audioEngine.getTotalSpeakDispatches()

    // User presses Pause -> stops active speech without wiping dedupeKey
    audioEngine.stopAndClearQueue(resetDedupeKey = false)
    assertEquals("Speech must stop immediately on Pause", 0, audioEngine.getActiveVoiceStreamCount())

    // User presses Resume -> if Compose recomposes with the same dedupeKey, it is deduplicated
    audioEngine.speak("ত্রিভুজটি খুঁজে দাও।", dedupeKey = dedupeKey, isManualReplay = false)
    assertEquals(
        "Resuming must not re-trigger old automatic speech",
        dispatchesBeforePause,
        audioEngine.getTotalSpeakDispatches()
    )
    assertTrue(audioEngine.getDeduplicatedSkipCount() >= 1)
  }

  @Test
  fun test8_ReturnToHomeAndReopenGameNoDuplicateRecompositionNarration() {
    val key = "emoji_core_L1_R0_apple"
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = key)
    val countAfterFirst = audioEngine.getTotalSpeakDispatches()

    // Simulated duplicate Compose recomposition with identical key
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = key)
    assertEquals(
        "Duplicate recomposition with same key must be skipped",
        countAfterFirst,
        audioEngine.getTotalSpeakDispatches()
    )

    // Exit to Home
    audioEngine.stopAndClearQueue(resetDedupeKey = true)
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())

    // Reopen game fresh -> plays initial instruction once
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = key)
    assertEquals(countAfterFirst + 1, audioEngine.getTotalSpeakDispatches())
    assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
  }

  @Test
  fun test9_DisableAndEnableVoiceSettingsObeyedAcrossAllSources() {
    // Disable voice in AudioSettingsState
    audioEngine.updateAudioSettings(AudioSettingsState(voiceEnabled = false))
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())

    val dispatchesWhileMuted = audioEngine.getTotalSpeakDispatches()
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = "muted_1")
    audioEngine.speakPraise("খুব ভালো হয়েছে!")
    audioEngine.replayInstruction("ত্রিভুজটি খুঁজে দাও।")
    assertEquals(
        "No speech may dispatch while voiceEnabled is false",
        dispatchesWhileMuted,
        audioEngine.getTotalSpeakDispatches()
    )
    assertEquals(0, audioEngine.getActiveVoiceStreamCount())

    // Re-enable voice
    audioEngine.updateAudioSettings(AudioSettingsState(voiceEnabled = true))
    audioEngine.speak("একই ছবিটি খুঁজে বের করো।", dedupeKey = "unmuted_1")
    assertEquals(dispatchesWhileMuted + 1, audioEngine.getTotalSpeakDispatches())
    assertEquals(1, audioEngine.getActiveVoiceStreamCount())
  }

  @Test
  fun test10_OfflineAudioAssetsFunctionalWithoutInternet() {
    val bundledFiles = audioEngine.getBundledAssetFileNames()
    assertTrue(
        "At least 120 offline youthful boy MP3 clips must be bundled in APK assets",
        bundledFiles.size >= 120
    )

    // Test every V7.0 Part 3 preview sample in offline mode
    for (sample in VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES) {
      assertTrue(
          "Preview asset ${sample.assetFileName} must be present in offline assets",
          bundledFiles.contains(sample.assetFileName)
      )
      audioEngine.playVoicePreviewSample(sample)
      assertEquals(1, audioEngine.getActiveVoiceStreamCount())
      assertEquals(1, audioEngine.getMaxConcurrentStreamsObserved())
    }
  }
}
