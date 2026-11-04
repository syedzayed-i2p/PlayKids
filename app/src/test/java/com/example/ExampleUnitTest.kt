package com.example

import com.example.audio.VoiceScriptRegistry
import com.example.data.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testPlayKidsBrandConfigAndLogoVariantsV7() {
    assertEquals("PlayKids", BrandConfig.APPROVED_BRAND_NAME)
    assertEquals("Play", BrandConfig.BRAND_PRIMARY_PART)
    assertEquals("Kids", BrandConfig.BRAND_SECONDARY_PART)
    assertEquals("Play • Match • Learn • Grow", BrandConfig.BRAND_TAGLINE)
    assertEquals("6.0", BrandConfig.APP_VERSION)
    assertTrue(BrandConfig.PROVISIONAL_BRAND_NOTICE.contains("PlayKids"))
    assertEquals("Must support all 8 logo variants", 8, PlayKidsLogoVariant.entries.size)
  }

  @Test
  fun testCurriculumCoreNineEmojisPreserved() {
    val coreEmojis = CurriculumLibrary.CORE_EMOJIS
    assertEquals("Should have exactly 9 core emojis", 9, coreEmojis.size)
    val symbols = coreEmojis.map { it.first }
    assertTrue(symbols.contains("😀"))
    assertTrue(symbols.contains("❤️"))
    assertTrue(symbols.contains("👍"))
    assertTrue(symbols.contains("⭐"))
    assertTrue(symbols.contains("😂"))
    assertTrue(symbols.contains("😎"))
    assertTrue(symbols.contains("🌸"))
    assertTrue(symbols.contains("⚽"))
    assertTrue(symbols.contains("🍎"))
  }

  @Test
  fun testOptionalExtensionEmojisPreserved() {
    val extraEmojis = CurriculumLibrary.EXTRA_EMOJIS
    assertEquals(3, extraEmojis.size)
    val symbols = extraEmojis.map { it.first }
    assertTrue(symbols.contains("🍌"))
    assertTrue(symbols.contains("☀️"))
    assertTrue(symbols.contains("🦋"))
  }

  @Test
  fun testExtendedPictureCategoriesFiveCategories() {
    assertEquals(8, CurriculumLibrary.FRUITS_AND_FOOD.size)
    assertEquals(10, CurriculumLibrary.ANIMALS.size)
    assertTrue(CurriculumLibrary.ANIMALS.any { it.id == "panda" })
    assertEquals(8, CurriculumLibrary.NATURE.size)
    assertEquals(6, CurriculumLibrary.VEHICLES.size)
    assertEquals(10, CurriculumLibrary.DAILY_OBJECTS.size)
    assertTrue(
        "Total picture library should exceed 50 items",
        CurriculumLibrary.EXTENDED_PICTURE_LIBRARY.size >= 50
    )
  }

  @Test
  fun testCoreAndExtendedColorsAndShapes() {
    assertEquals("5 Core Colors must be preserved", 5, KidColor.CORE_COLORS.size)
    assertEquals("5 Extended Colors must be present", 5, KidColor.EXTENDED_ONLY_COLORS.size)
    assertEquals("10 Total Colors must be present", 10, KidColor.ALL_10_COLORS.size)

    assertEquals("5 Core Shapes must be preserved", 5, KidShape.CORE_SHAPES.size)
    assertEquals("10 Combo Shapes must be present", 10, KidShape.COMBO_10_SHAPES.size)
    assertEquals("20 Combo 2D Shapes must be present", 20, KidShape.ALL_20_SHAPES.size)
    assertEquals(
        "25 Total 2D Shapes (5 Core + 20 Additional) must be present",
        25,
        KidShape.ALL_25_SHAPES.size
    )
    assertEquals("8 3D Shapes must be present for discovery", 8, Kid3DShape.ALL_3D_SHAPES.size)
  }

  @Test
  fun testTwentyFiveCoreAndTwoHundredExtendedCombinations() {
    val coreCards = CurriculumLibrary.ALL_COLOR_SHAPE_CARDS
    assertEquals("5 core colors * 5 core shapes = 25 cards", 25, coreCards.size)
    assertEquals(25, coreCards.map { it.id }.toSet().size)

    val extended100Cards = CurriculumLibrary.EXTENDED_100_COLOR_SHAPE_CARDS
    assertEquals("10 colors * 10 shapes = 100 extended cards", 100, extended100Cards.size)

    val extended200Cards = CurriculumLibrary.EXTENDED_200_COLOR_SHAPE_CARDS
    assertEquals("10 colors * 20 shapes = 200 extended cards", 200, extended200Cards.size)
    assertEquals(200, extended200Cards.map { it.id }.toSet().size)
  }

  @Test
  fun testOneHundredDefinedLevelsAcrossFourGamesAndFiveChapters() {
    assertEquals(5, CurriculumLibrary.CHAPTERS.size)
    var totalLevels = 0
    for (game in GameType.entries) {
      val levels = CurriculumLibrary.getLevelsForGame(game)
      assertEquals("Each game must have 25 levels (5 chapters * 5 levels)", 25, levels.size)
      assertEquals(25, levels.map { it.titleBn }.toSet().size)
      totalLevels += levels.size
    }
    assertEquals("Total levels across 4 games must be 100", 100, totalLevels)
  }

  @Test
  fun testV7Part3BenchmarkVoiceScriptsAndPreviewSamples() {
    assertEquals(
        "Must have all 9 V7.0 Part 3 voice preview benchmark samples",
        9,
        VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.size
    )
    val previewSentences = VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.map { it.banglaSentence }
    assertTrue(previewSentences.contains("হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।"))
    assertTrue(previewSentences.contains("একই ছবিটি খুঁজে বের করো।"))
    assertTrue(previewSentences.contains("ত্রিভুজটি খুঁজে দাও।"))
    assertTrue(previewSentences.contains("নীল রংটি খুঁজে বের করো।"))
    assertTrue(previewSentences.contains("লাল তারাটি নির্বাচন করো।"))
    assertTrue(previewSentences.contains("খুব ভালো হয়েছে! এটি সঠিক উত্তর।"))
    assertTrue(previewSentences.contains("আবার চেষ্টা করো।"))
    assertTrue(previewSentences.contains("চমৎকার! এবার পরের ধাপে যাই।"))
    assertTrue(previewSentences.contains("এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।"))

    val praise1 = VoiceScriptRegistry.randomPraise()
    val praise2 = VoiceScriptRegistry.randomPraise()
    assertNotEquals("Consecutive praise phrases should vary naturally", praise1, praise2)
  }

  @Test
  fun testZeroBannedNicknamesInVoiceScriptsAndSanitizer() {
    val allScripts =
        VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.map { it.banglaSentence } +
            VoiceScriptRegistry.PRAISE_VARIATIONS +
            VoiceScriptRegistry.GENTLE_RETRY_VARIATIONS +
            VoiceScriptRegistry.SCRIPTS.values.map { it.banglaScript }

    for (sentence in allScripts) {
      for (banned in VoiceScriptRegistry.BANNED_EXPRESSIONS) {
        assertFalse(
            "Sentence '$sentence' must not contain banned expression '$banned'",
            sentence.contains(banned)
        )
      }
    }

    val sanitized = VoiceScriptRegistry.sanitizeScript("হ্যালো সোনামণি! চলো আজকে খেলি।")
    assertFalse(sanitized.contains("সোনামণি"))
  }

  @Test
  fun testDefaultAudioSettingsBackgroundMusicDisabledByDefault() {
    val defaultSettings = AudioSettingsState()
    assertTrue("Voice should be enabled by default", defaultSettings.voiceEnabled)
    assertTrue("Sound effects should be enabled by default", defaultSettings.soundEffectsEnabled)
    assertFalse(
        "Background music must be disabled by default",
        defaultSettings.backgroundMusicEnabled
    )
  }

  @Test
  fun testComplete123YouthfulBoyVoiceAssetsAndAll200DynamicPromptsResolveToBundledFiles() {
    val assetsRoot =
        listOf(File("src/main/assets"), File("app/src/main/assets")).firstOrNull { it.exists() }
    assertNotNull("Assets directory must exist", assetsRoot)

    val voiceDir = File(assetsRoot!!, VoiceScriptRegistry.ASSET_VOICE_FOLDER)
    assertTrue("Youthful boy voice folder must exist: ${voiceDir.path}", voiceDir.exists())

    // 1. Verify all registered SCRIPTS exist on disk
    for (entry in VoiceScriptRegistry.SCRIPTS.values) {
      val mp3File = File(voiceDir, entry.assetFileName)
      assertTrue("Registered audio file ${entry.assetFileName} must exist", mp3File.exists())
      assertTrue("Registered audio file ${entry.assetFileName} must be non-empty", mp3File.length() > 500L)
    }

    // 2. Verify all 25 2D shapes resolve to existing bundled MP3 files
    for (shape in KidShape.ALL_25_SHAPES) {
      val seq = VoiceScriptRegistry.resolveAssetSequence("${shape.nameBn}টি খুঁজে দাও।")
      assertTrue("Shape ${shape.id} must resolve to bundled MP3", seq.isNotEmpty())
      seq.forEach { fname ->
        assertTrue("Shape MP3 $fname must exist", File(voiceDir, fname).exists())
      }
    }

    // 3. Verify all 10 colours resolve to existing bundled MP3 files
    for (color in KidColor.ALL_10_COLORS) {
      val seq = VoiceScriptRegistry.resolveAssetSequence("${color.nameBn} রংটি খুঁজে বের করো।")
      assertTrue("Colour ${color.id} must resolve to bundled MP3", seq.isNotEmpty())
      seq.forEach { fname ->
        assertTrue("Colour MP3 $fname must exist", File(voiceDir, fname).exists())
      }
    }

    // 4. Verify all 200 Extended Colour+Shape combinations resolve to existing bundled MP3 files
    for (card in CurriculumLibrary.EXTENDED_200_COLOR_SHAPE_CARDS) {
      val prompt = "${card.color.nameBn} ${card.shape.nameBn}টি নির্বাচন করো।"
      val seq = VoiceScriptRegistry.resolveAssetSequence(prompt)
      assertTrue(
          "Extended combination ${card.id} ('$prompt') must resolve to bundled MP3 sequence",
          seq.isNotEmpty()
      )
      seq.forEach { fname ->
        assertTrue(
            "Combination MP3 $fname for ${card.id} must exist on disk",
            File(voiceDir, fname).exists()
        )
      }
    }

    // 5. Verify all 8 3D shapes resolve to existing bundled MP3 files
    for (s3d in Kid3DShape.ALL_3D_SHAPES) {
      val prompt = "ত্রিমাত্রিক আকৃতি: ${s3d.nameBn}। উদাহরণ: ${s3d.realLifeExampleBn}।"
      val seq = VoiceScriptRegistry.resolveAssetSequence(prompt)
      assertTrue("3D Shape ${s3d.id} must resolve to bundled MP3", seq.isNotEmpty())
      seq.forEach { fname ->
        assertTrue("3D Shape MP3 $fname must exist", File(voiceDir, fname).exists())
      }
    }
  }

  @Test
  fun testV8DesignSystemTokensAndGameTitles() {
    assertEquals("ছবি মিলাও", GameType.EMOJI_MATCH.titleBn)
    assertEquals("আকৃতি মিলাও", GameType.SHAPE_MATCH.titleBn)
    assertEquals("রং ও আকৃতি", GameType.COLOR_SHAPE_CHALLENGE.titleBn)
    assertEquals("রঙিন কাগজ মিলাও", GameType.COLOR_PAPER_MATCH.titleBn)
    assertEquals("১২", com.example.ui.components.PlayKidsTokens.toBnDigits(12))
    assertEquals("১০০", com.example.ui.components.PlayKidsTokens.toBnDigits(100))
  }
}
