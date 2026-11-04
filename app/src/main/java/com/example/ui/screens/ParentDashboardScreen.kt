package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.audio.VoiceScriptRegistry
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ParentDashboardScreen(
    allProgress: Map<GameType, GameProgressData>,
    skillMastery: SkillMasterySnapshot = SkillMasterySnapshot(),
    audio: BanglaAudioEngine,
    onBack: () -> Unit,
    onUpdateAudioSettings: (AudioSettingsState) -> Unit = {},
    onUnlockAllLevels: () -> Unit = {},
    onResetAllProgress: () -> Unit,
    onNavigateTab: (Screen) -> Unit = {}
) {
    var isGateUnlocked by remember { mutableStateOf(false) }
    val num1 = 5
    val num2 = 4
    var inputAnswer by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var unlockSuccessBanner by remember { mutableStateOf(false) }
    var activePreviewSampleId by remember { mutableStateOf<String?>(null) }

    val isBengaliTtsAvailable by audio.isBengaliVoiceAvailable.collectAsState()
    val selectedVoiceName by audio.selectedVoiceName.collectAsState()
    val bundledVoiceAssetsPresent by audio.bundledVoiceAssetsPresent.collectAsState()
    val audioSettings by audio.audioSettings.collectAsState()

    val scrollState = rememberScrollState()

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = audioSettings.reducedMotion
    ) {
        if (!isGateUnlocked) {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayKidsIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যাও",
                            onClick = onBack,
                            size = 48.dp,
                            gradient = PlayKidsTokens.PurpleHeaderGradient
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(22.dp)),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(PlayKidsTokens.PurpleHeaderGradient)
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "অভিভাবক প্যানেল",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    PlayoraBottomNavBar(
                        currentScreen = Screen.PARENT_DASHBOARD,
                        onSelectTab = { target ->
                            audio.playButtonTap()
                            onNavigateTab(target)
                        }
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SoftLavender,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = CreativePurple,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Parent Gate • অভিভাবক নিরাপত্তা যাচাই",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepRoyalBlue
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "শিশুদের অজান্তে সেটিংস পরিবর্তন রোধে উত্তরটি লিখুন:",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "$num1 + $num2 = কত?",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepRoyalBlue,
                                    fontSize = 28.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = inputAnswer,
                                onValueChange = {
                                    inputAnswer = it
                                    gateError = false
                                },
                                placeholder = { Text("উত্তর লিখুন (যেমন: ${num1 + num2})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = gateError,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("parent_gate_input")
                            )

                            if (gateError) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "সঠিক উত্তর নয়, আবার চেষ্টা করুন।",
                                    color = KidRed,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            PlayKidsPrimaryButton(
                                text = "ড্যাশবোর্ডে প্রবেশ করুন",
                                onClick = {
                                    val normalized = inputAnswer.trim()
                                        .replace('৯', '9')
                                    if (normalized == (num1 + num2).toString()) {
                                        isGateUnlocked = true
                                    } else {
                                        gateError = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                gradient = PlayKidsTokens.PurplePillGradient,
                                height = 54.dp,
                                testTag = "parent_gate_submit"
                            )
                        }
                    }
                }
            }
            return@PlayoraAnimatedSceneBackground
        }

        // Screen 11 — Unlocked PlayKids Parent Dashboard ("অভিভাবক প্যানেল")
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayKidsIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ফিরে যাও",
                        onClick = onBack,
                        size = 48.dp,
                        gradient = PlayKidsTokens.PurpleHeaderGradient,
                        testTag = "parent_dashboard_back_button"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(6.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(PlayKidsTokens.PurpleHeaderGradient)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "👨‍👩‍👧", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অভিভাবক প্যানেল",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 21.sp
                                )
                            )
                        }
                    }
                }
            },
            bottomBar = {
                PlayoraBottomNavBar(
                    currentScreen = Screen.PARENT_DASHBOARD,
                    onSelectTab = { target ->
                        audio.playButtonTap()
                        onNavigateTab(target)
                    }
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Screen 11 — 2x3 Quick-Action Tile Grid (Matching V8.0 Reference Collage Screen 11)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Row 1: শিশুর অগ্রগতি | ভয়েস প্রিভিউ
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ParentQuickActionTile(
                                titleBn = "শিশুর অগ্রগতি",
                                icon = Icons.Default.BarChart,
                                iconTint = PlayBlue,
                                bgColor = SoftSky,
                                testTag = "parent_tile_progress",
                                onClick = {
                                    audio.playButtonTap()
                                    onNavigateTab(Screen.MY_PROGRESS)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ParentQuickActionTile(
                                titleBn = "ভয়েস প্রিভিউ",
                                icon = Icons.AutoMirrored.Filled.VolumeUp,
                                iconTint = CreativePurple,
                                bgColor = SoftLavender,
                                testTag = "parent_tile_voice_preview",
                                onClick = {
                                    val firstSample = VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.first()
                                    activePreviewSampleId = firstSample.id
                                    audio.playVoicePreviewSample(firstSample)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 2: অডিও সেটিংস | বাস্তব অনুশীলন
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ParentQuickActionTile(
                                titleBn = "অডিও সেটিংস",
                                icon = Icons.Default.Tune,
                                iconTint = CreativePurple,
                                bgColor = SoftLavender,
                                testTag = "parent_tile_audio_settings",
                                onClick = {
                                    audio.playButtonTap()
                                    showSettingsModal = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ParentQuickActionTile(
                                titleBn = "বাস্তব অনুশীলন",
                                icon = Icons.Default.MenuBook,
                                iconTint = KidGreenDark,
                                bgColor = SoftMint,
                                testTag = "parent_tile_physical_practice",
                                onClick = {
                                    audio.playButtonTap()
                                    onNavigateTab(Screen.PHYSICAL_PRACTICE)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 3: অগ্রগতি রিসেট | অ্যাপ সম্পর্কে
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ParentQuickActionTile(
                                titleBn = "অগ্রগতি রিসেট",
                                icon = Icons.Default.Refresh,
                                iconTint = KidRed,
                                bgColor = SoftCoral,
                                testTag = "parent_tile_reset_progress",
                                onClick = {
                                    audio.playButtonTap()
                                    showResetConfirmDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ParentQuickActionTile(
                                titleBn = "অ্যাপ সম্পর্কে",
                                icon = Icons.Default.Info,
                                iconTint = Color(0xFFD97706),
                                bgColor = SoftSunshine,
                                testTag = "parent_tile_about_app",
                                onClick = {
                                    audio.playButtonTap()
                                    showSettingsModal = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Voice Preview & Audio Settings Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = PlayBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Voice Preview • কিশোর বাংলা কণ্ঠস্বর (১০–১২ বছর)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = NunitoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepRoyalBlue
                                    )
                                )
                                Text(
                                    text = "Youthful Boy-Like Bengali Voice (10–12 Years • bn-BD Offline)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isBengaliTtsAvailable || bundledVoiceAssetsPresent) SoftMint else SoftCoral,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = selectedVoiceName,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBengaliTtsAvailable || bundledVoiceAssetsPresent) KidGreenDark else KidRedDark
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (bundledVoiceAssetsPresent) {
                                        "অফলাইন কিশোর বাংলা ভয়েস ক্লিপ (assets/voice/youthful_boy_bn/) সক্রিয় আছে।"
                                    } else if (isBengaliTtsAvailable) {
                                        "কিশোর বাংলা কণ্ঠের প্রসোডি প্রোফাইল (bn-BD YouthfulBoy 10–12yo) সক্রিয় আছে।"
                                    } else {
                                        "ডিভাইসে বাংলা TTS সক্রিয় নেই। ভিজ্যুয়াল ও সাউন্ড ইফেক্টস চালু আছে।"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(color = DeepRoyalBlue)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "নমুনা বাক্যগুলো চালিয়ে কণ্ঠস্বর পরীক্ষা করুন (${VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.size}টি):",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        VoiceScriptRegistry.VOICE_PREVIEW_SAMPLES.forEachIndexed { idx, sample ->
                            val isPlayingThis = activePreviewSampleId == sample.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .testTag("voice_preview_sample_${idx + 1}")
                                    .clickable {
                                        activePreviewSampleId = sample.id
                                        audio.playVoicePreviewSample(sample)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isPlayingThis) SoftSky else SoftCloud,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isPlayingThis) 1.5.dp else 1.dp,
                                    color = if (isPlayingThis) PlayBlue else BorderSoft
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${idx + 1}. ${sample.titleBn}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = PlayBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "\"${sample.banglaSentence}\"",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = DeepRoyalBlue
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        shape = CircleShape,
                                        color = PlayBlue,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "শুনুন",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Audio Settings Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("বাংলা ভয়েস নির্দেশনা (Voice)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Switch(
                                checked = audioSettings.voiceEnabled,
                                onCheckedChange = {
                                    val updated = audioSettings.copy(voiceEnabled = it)
                                    audio.updateAudioSettings(updated)
                                    onUpdateAudioSettings(updated)
                                },
                                modifier = Modifier.testTag("parent_switch_voice")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("কোমল সাউন্ড ইফেক্টস (Sound Effects)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Switch(
                                checked = audioSettings.soundEffectsEnabled,
                                onCheckedChange = {
                                    val updated = audioSettings.copy(soundEffectsEnabled = it)
                                    audio.updateAudioSettings(updated)
                                    onUpdateAudioSettings(updated)
                                },
                                modifier = Modifier.testTag("parent_switch_sfx")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("মৃদু আবহ সংগীত (Background Music)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Switch(
                                checked = audioSettings.backgroundMusicEnabled,
                                onCheckedChange = {
                                    val updated = audioSettings.copy(backgroundMusicEnabled = it)
                                    audio.updateAudioSettings(updated)
                                    onUpdateAudioSettings(updated)
                                },
                                modifier = Modifier.testTag("parent_switch_bgm")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("অ্যানিমেশন নিয়ন্ত্রণ (Reduced Motion)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Switch(
                                checked = audioSettings.reducedMotion,
                                onCheckedChange = {
                                    val updated = audioSettings.copy(reducedMotion = it)
                                    audio.updateAudioSettings(updated)
                                    onUpdateAudioSettings(updated)
                                },
                                modifier = Modifier.testTag("parent_switch_reduced_motion")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Game-wise Progress Summary (4 Games x 25 Levels = 100 Levels)
                GameType.entries.forEach { gType ->
                    val pData = allProgress[gType] ?: GameProgressData(gType)
                    val extLvl = maxOf(pData.unlockedLevel, pData.extendedUnlockedLevel).coerceIn(1, 25)
                    val chapterNum = ((extLvl - 1) / 5) + 1

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .shadow(4.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${gType.titleEn} (${gType.titleBn})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = NunitoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = gType.themeColor
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = gType.lightBg
                                ) {
                                    Text(
                                        text = "অধ্যায় $chapterNum • লেভেল $extLvl/২৫",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = DeepRoyalBlue,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("সম্পন্ন লেভেল", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                                    Text("${pData.completedLevels.size}/২৫ টি", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("স্বাধীন সফলতা", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                                    Text("${pData.independentSuccesses} বার", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("ইঙ্গিত ব্যবহার", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                                    Text("${pData.hintsUsed} বার", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("বাস্তব খেলা", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                                    Text(
                                        if (pData.physicalCompleted) "✓ সম্পন্ন" else "বাকি",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (pData.physicalCompleted) KidGreenDark else SecondaryText
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Individual Colour (10) & Shape (25) Skill Mastery
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎨 রং চেনার দক্ষতা (১০টি রং):",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        KidColor.ALL_10_COLORS.chunked(5).forEach { rowColors ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowColors.forEach { col ->
                                    val count = skillMastery.colorAccuracyMap[col.id] ?: 0
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(col.color)
                                                .border(1.5.dp, col.darkColor, CircleShape)
                                        )
                                        Text(
                                            text = col.nameBn,
                                            style = MaterialTheme.typography.labelSmall.copy(color = DeepRoyalBlue)
                                        )
                                        Text(
                                            text = "${count}✓",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = KidGreenDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "📐 আকৃতি চেনার দক্ষতা (২৫টি দ্বিমাত্রিক আকৃতি):",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        KidShape.ALL_25_SHAPES.chunked(5).forEach { rowShapes ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowShapes.forEach { sh ->
                                    val count = skillMastery.shapeAccuracyMap[sh.id] ?: 0
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        ShapeGraphic(shape = sh, color = PlayBlue, sizeDp = 24.dp)
                                        Text(
                                            text = sh.nameBn,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = DeepRoyalBlue,
                                                fontSize = 10.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${count}✓",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = KidGreenDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Parent Level Control (Unlock All 100 Levels)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = SoftSky,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "অভিভাবক অনুশীলন নিয়ন্ত্রণ (১০০টি লেভেল)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আপনি চাইলে ৪টি খেলার সব ১০০টি লেভেল একসাথে উন্মুক্ত করতে পারেন যেন শিশু যেকোনো অধ্যায় অনুশীলন করতে পারে।",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepRoyalBlue)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onUnlockAllLevels()
                                unlockSuccessBanner = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("unlock_all_levels_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlayBlue)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (unlockSuccessBanner) "সব ১০০টি লেভেল উন্মুক্ত করা হয়েছে ✓" else "সব ১০০টি লেভেল আনলক করুন",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Reset All Progress Button
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("reset_progress_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, KidRed),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.9f),
                        contentColor = KidRed
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset Progress • সমস্ত অগ্রগতি রিসেট করুন",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Settings & About Section (Central PlayKids Brand Config & 8 Logo Variants)
                var selectedLogoVariant by remember { mutableStateOf(PlayKidsLogoVariant.PRIMARY_HORIZONTAL) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .testTag("parent_about_brand_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PlayBlue.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "About ${BrandConfig.APPROVED_BRAND_NAME} • সংস্করণ ${BrandConfig.APP_VERSION}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepRoyalBlue
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PlayKidsBrandLogo(
                            variant = selectedLogoVariant,
                            showTaglineRibbon = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "লোগো সংস্করণ (${selectedLogoVariant.titleEn}):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PlayBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PlayKidsLogoVariant.entries.chunked(4).forEach { rowVariants ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rowVariants.forEach { v ->
                                    val isCurrent = v == selectedLogoVariant
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedLogoVariant = v },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrent) PlayBlue else SoftSky
                                    ) {
                                        Text(
                                            text = v.titleBn,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isCurrent) Color.White else DeepRoyalBlue,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 5.dp),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = BrandConfig.PRIVACY_AND_SAFETY_NOTICE_BN,
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepRoyalBlue),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = BrandConfig.PROVISIONAL_BRAND_NOTICE,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SecondaryText,
                                fontSize = 10.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (showResetConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showResetConfirmDialog = false },
                    title = { Text("অগ্রগতি রিসেট করবেন?", fontWeight = FontWeight.Bold) },
                    text = { Text("সমস্ত গেমের লেভেল, তারা, দক্ষতা রেকর্ড ও অনুশীলনের তথ্য মুছে নতুন করে শুরু হবে।") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showResetConfirmDialog = false
                                unlockSuccessBanner = false
                                onResetAllProgress()
                            }
                        ) {
                            Text("হ্যাঁ, রিসেট করুন", color = KidRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetConfirmDialog = false }) {
                            Text("বাতিল")
                        }
                    }
                )
            }

            if (showSettingsModal) {
                PlayKidsSettingsDialog(
                    audioSettings = audioSettings,
                    bundledVoiceAssetsPresent = bundledVoiceAssetsPresent,
                    onUpdateAudioSettings = { updated ->
                        audio.updateAudioSettings(updated)
                        onUpdateAudioSettings(updated)
                    },
                    onDismiss = { showSettingsModal = false }
                )
            }
        }
    }
}

@Composable
private fun ParentQuickActionTile(
    titleBn: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = titleBn,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = titleBn,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepRoyalBlue,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
