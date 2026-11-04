package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.audio.SoundEffectType
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ColorPaperMatchingScreen(
    audio: BanglaAudioEngine,
    progress: GameProgressData,
    learningMode: LearningMode = LearningMode.CORE_CURRICULUM,
    initialLevelOverride: Int? = null,
    onSelectMode: (LearningMode) -> Unit = {},
    onOpenLevelMap: () -> Unit = {},
    onBack: () -> Unit,
    onRecordAttempt: (isSuccess: Boolean, isIndependent: Boolean, shouldAdvance: Boolean, completedLevel: Int) -> Unit,
    onRecordSkillMastery: (colorId: String) -> Unit = {},
    onRecordHint: () -> Unit,
    onGoPhysicalPractice: () -> Unit
) {
    val maxLevel = if (learningMode == LearningMode.CORE_CURRICULUM) 5 else 25
    val maxUnlocked = if (learningMode == LearningMode.CORE_CURRICULUM) {
        progress.unlockedLevel.coerceIn(1, 5)
    } else {
        maxOf(progress.unlockedLevel, progress.extendedUnlockedLevel).coerceIn(1, 25)
    }
    val activeCompletedSet = if (learningMode == LearningMode.CORE_CURRICULUM) {
        progress.coreCompletedLevels
    } else {
        progress.completedLevels
    }

    var level by remember(learningMode, initialLevelOverride) {
        val defaultLvl = maxUnlocked.coerceIn(1, maxLevel)
        mutableIntStateOf((initialLevelOverride ?: defaultLvl).coerceIn(1, maxLevel))
    }

    var restartTrigger by remember { mutableIntStateOf(0) }
    var showPauseDialog by remember { mutableStateOf(false) }

    val audioSettings by audio.audioSettings.collectAsState()

    // Active Trays for current level (1..25)
    val levelTrays: List<KidColor> = remember(learningMode, level, restartTrigger) {
        if (learningMode == LearningMode.CORE_CURRICULUM) {
            when (level) {
                1 -> listOf(KidColor.RED, KidColor.YELLOW)
                2 -> listOf(KidColor.RED, KidColor.YELLOW, KidColor.BLUE)
                3 -> KidColor.CORE_COLORS
                4 -> KidColor.CORE_COLORS.shuffled()
                else -> KidColor.CORE_COLORS
            }
        } else {
            when (level) {
                1 -> listOf(KidColor.RED, KidColor.YELLOW)
                2 -> listOf(KidColor.RED, KidColor.YELLOW, KidColor.BLUE)
                3 -> listOf(KidColor.RED, KidColor.YELLOW, KidColor.BLUE, KidColor.GREEN)
                4 -> KidColor.CORE_COLORS
                5 -> listOf(KidColor.BLUE, KidColor.RED, KidColor.GREEN)
                6 -> listOf(KidColor.ORANGE, KidColor.YELLOW, KidColor.BLUE, KidColor.RED)
                7, 8, 9, 10, 11, 12 -> KidColor.CORE_COLORS.shuffled()
                13 -> listOf(KidColor.PINK, KidColor.PURPLE, KidColor.RED, KidColor.BLUE)
                14 -> listOf(KidColor.PINK, KidColor.PURPLE, KidColor.BROWN, KidColor.YELLOW, KidColor.GREEN)
                15, 16 -> KidColor.EXTENDED_ONLY_COLORS
                17 -> listOf(KidColor.RED, KidColor.YELLOW, KidColor.BLUE, KidColor.PINK, KidColor.PURPLE, KidColor.ORANGE)
                18 -> listOf(KidColor.GREEN, KidColor.ORANGE, KidColor.BROWN, KidColor.BLACK, KidColor.WHITE, KidColor.PINK)
                19, 20 -> KidColor.ALL_10_COLORS.shuffled().take(6)
                21, 22 -> KidColor.ALL_10_COLORS.shuffled().take(7)
                23, 24, 25 -> KidColor.ALL_10_COLORS.shuffled().take(8)
                else -> KidColor.CORE_COLORS
            }
        }
    }

    // Papers queue to sort into trays
    val initialPaperQueue: List<KidColor> = remember(learningMode, level, levelTrays, restartTrigger) {
        if (learningMode == LearningMode.EXTENDED_PRACTICE && level in listOf(9, 10, 11, 12, 16, 19, 20, 21, 22, 23, 24, 25)) {
            (levelTrays + levelTrays.shuffled().take(3)).shuffled()
        } else {
            levelTrays.shuffled()
        }
    }

    var remainingPapers by remember(learningMode, level, restartTrigger) {
        mutableStateOf(initialPaperQueue)
    }

    val currentTargetPaper = remainingPapers.firstOrNull()

    var trayPaperCounts by remember(learningMode, level, restartTrigger) {
        mutableStateOf(mapOf<KidColor, Int>())
    }

    var errorCount by remember(remainingPapers.size, restartTrigger) { mutableIntStateOf(0) }
    var hintActive by remember(remainingPapers.size, restartTrigger) { mutableStateOf(false) }
    var demoActive by remember(remainingPapers.size, restartTrigger) { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("নীল রংটি খুঁজে বের করো।") }

    DisposableEffect(Unit) {
        onDispose {
            audio.stopAndClearQueue()
        }
    }

    fun playInstruction(isManualReplay: Boolean = false) {
        currentTargetPaper?.let { color ->
            val prompt = "${color.nameBn} রংটি খুঁজে বের করো।"
            speechText = prompt
            if (isManualReplay) {
                audio.replayInstruction(prompt)
            } else {
                audio.speak(
                    text = prompt,
                    dedupeKey = "paper_${learningMode.id}_L${level}_R${restartTrigger}_rem${remainingPapers.size}_${color.id}",
                    isManualReplay = false,
                    queueAfterPraise = true
                )
            }
        }
    }

    fun restartCurrentLevel() {
        audio.stopAndClearQueue()
        audio.playButtonTap()
        showSuccessDialog = false
        showPauseDialog = false
        restartTrigger++
    }

    LaunchedEffect(remainingPapers.size, currentTargetPaper, restartTrigger) {
        if (currentTargetPaper != null) {
            playInstruction(isManualReplay = false)
        } else if (remainingPapers.isEmpty() && initialPaperQueue.isNotEmpty()) {
            audio.playLevelCompleteFanfare()
            audio.speak(
                text = "এই ধাপটি সম্পন্ন হয়েছে।",
                dedupeKey = "paper_complete_${learningMode.id}_L${level}_R${restartTrigger}",
                queueAfterPraise = true
            )
            onRecordAttempt(true, errorCount == 0, level < maxLevel, level)
            showSuccessDialog = true
        }
    }

    val levelDef = remember(level) {
        CurriculumLibrary.getLevelsForGame(GameType.COLOR_PAPER_MATCH).getOrNull(level - 1)
    }

    val hasNextUnlocked = level < maxLevel && ((level + 1) <= maxUnlocked || (level + 1) in activeCompletedSet || level in activeCompletedSet)

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.CREATIVE_ART_STUDIO,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(Color.White.copy(alpha = 0.88f))) {
                    GameTopBar(
                        title = "${GameType.COLOR_PAPER_MATCH.titleEn} • ${GameType.COLOR_PAPER_MATCH.titleBn}",
                        serialNumber = GameType.COLOR_PAPER_MATCH.serialName,
                        currentLevel = level,
                        starsCount = progress.starsEarned,
                        onBackClick = onBack,
                        themeColor = GameType.COLOR_PAPER_MATCH.themeColor,
                        learningMode = learningMode,
                        onOpenLevelMap = onOpenLevelMap
                    )
                    GameplayControlBar(
                        currentLevel = level,
                        maxLevel = maxLevel,
                        hasPreviousLevel = level > 1,
                        hasNextUnlockedLevel = hasNextUnlocked,
                        onPreviousLevel = {
                            if (level > 1) {
                                audio.stopAndClearQueue()
                                audio.playButtonTap()
                                level--
                            }
                        },
                        onRestartLevel = { restartCurrentLevel() },
                        onPauseClick = {
                            audio.stopAndClearQueue(resetDedupeKey = false)
                            audio.playButtonTap()
                            showPauseDialog = true
                        },
                        onNextLevel = {
                            if (hasNextUnlocked && level < maxLevel) {
                                audio.stopAndClearQueue()
                                audio.playButtonTap()
                                level++
                            }
                        },
                        themeColor = GameType.COLOR_PAPER_MATCH.themeColor,
                        onOpenLevelMap = onOpenLevelMap,
                        onLockedNextAttempt = {
                            audio.playTryAgainChime()
                            speechText = "আগের ধাপটি শেষ করে এটি খোলো।"
                            audio.replayInstruction("আগের ধাপটি শেষ করে এটি খোলো।")
                        }
                    )
                    LearningModeSwitcherBar(
                        currentMode = learningMode,
                        onSelectMode = { mode ->
                            audio.stopAndClearQueue()
                            audio.playButtonTap()
                            onSelectMode(mode)
                        }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MascotSpeechPrompt(
                    text = speechText,
                    onReplayAudio = { playInstruction(isManualReplay = true) },
                    isHighlighted = hintActive || demoActive
                )

                // Upper Hand Card: Colored Craft Paper Sheet
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = levelDef?.titleBn ?: "লেভেল $level",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = JoyCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            val placedCount = initialPaperQueue.size - remainingPapers.size
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftCoral
                            ) {
                                Text(
                                    text = "কাগজ: $placedCount/${initialPaperQueue.size}",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = DeepRoyalBlue,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (currentTargetPaper != null) {
                            val isLightPaper = currentTargetPaper == KidColor.WHITE || currentTargetPaper == KidColor.YELLOW
                            Surface(
                                modifier = Modifier
                                    .size(width = 168.dp, height = 76.dp)
                                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                color = currentTargetPaper.color,
                                border = androidx.compose.foundation.BorderStroke(
                                    3.dp,
                                    if (isLightPaper) currentTargetPaper.darkColor else Color.White
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${currentTargetPaper.patternSymbol} ${currentTargetPaper.nameBn} কাগজ",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = if (isLightPaper) DeepRoyalBlue else Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "সঠিক রঙের ট্রেতে ট্যাপ করে কাগজটি রাখো:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepRoyalBlue
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Realistic 3D Trays List on the Wooden Studio Table
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(levelTrays) { trayColor ->
                        val countInTray = trayPaperCounts[trayColor] ?: 0
                        val isTarget = trayColor == currentTargetPaper
                        val shouldHighlight = (hintActive || demoActive) && isTarget
                        val isLightSwatch = trayColor == KidColor.WHITE || trayColor == KidColor.YELLOW

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .shadow(if (shouldHighlight) 8.dp else 5.dp, RoundedCornerShape(20.dp))
                                .testTag("color_tray_${trayColor.id}")
                                .clickable {
                                    audio.playEffect(SoundEffectType.CARD_PLACE)

                                    if (isTarget && currentTargetPaper != null) {
                                        audio.playSuccessChime()
                                        audio.speakPraise()
                                        speechText = "খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।"
                                        if (errorCount == 0) {
                                            onRecordSkillMastery(trayColor.id)
                                        }
                                        trayPaperCounts = trayPaperCounts + (trayColor to (countInTray + 1))
                                        remainingPapers = remainingPapers.drop(1)
                                        errorCount = 0
                                        hintActive = false
                                        demoActive = false
                                    } else {
                                        audio.playTryAgainChime()
                                        errorCount++
                                        onRecordHint()
                                        when (errorCount) {
                                            1 -> {
                                                speechText = "আবার চেষ্টা করো।"
                                                audio.speak("আবার চেষ্টা করো।")
                                            }
                                            2 -> {
                                                speechText = "একটু ভালো করে দেখো।"
                                                audio.speak("একটু ভালো করে দেখো।")
                                                hintActive = true
                                            }
                                            else -> {
                                                speechText = "সঠিক কার্ডটি বেছে নাও: ${currentTargetPaper?.nameBn}।"
                                                audio.speak("সঠিক কার্ডটি বেছে নাও।")
                                                hintActive = true
                                                demoActive = true
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(20.dp),
                            color = if (shouldHighlight) SoftSunshine else trayColor.lightColor,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (shouldHighlight) 3.5.dp else 3.dp,
                                color = if (shouldHighlight) Color(0xFFF59E0B) else trayColor.darkColor
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // 3D Tray Bin Swatch
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(trayColor.color)
                                            .border(2.5.dp, trayColor.darkColor, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = trayColor.patternSymbol,
                                            color = if (isLightSwatch) DeepRoyalBlue else Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Text(
                                        text = if (demoActive && isTarget) "👉 ${trayColor.nameBn} ট্রে" else "${trayColor.nameBn} ট্রে",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepRoyalBlue
                                        )
                                    )
                                }

                                if (countInTray > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = KidGreen
                                    ) {
                                        Text(
                                            text = if (countInTray > 1) "✓ $countInTray টি কাগজ" else "✓ কাগজ রাখা হয়েছে",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = "এখানে রাখো",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DeepRoyalBlue,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showPauseDialog) {
                GamePauseDialog(
                    levelNumber = level,
                    onResume = { showPauseDialog = false },
                    onRestart = { restartCurrentLevel() },
                    onOpenLevelMap = {
                        showPauseDialog = false
                        onOpenLevelMap()
                    },
                    onGoHome = {
                        showPauseDialog = false
                        onBack()
                    }
                )
            }

            if (showSuccessDialog) {
                GameSuccessDialog(
                    onDismiss = {
                        showSuccessDialog = false
                        restartCurrentLevel()
                    },
                    onNextLevel = {
                        showSuccessDialog = false
                        if (level < maxLevel) level++
                        trayPaperCounts = emptyMap()
                    },
                    onGoPhysicalPractice = {
                        showSuccessDialog = false
                        audio.playEffect(SoundEffectType.PHYSICAL_TRANSITION)
                        onGoPhysicalPractice()
                    },
                    levelNumber = level,
                    starsAwarded = if (errorCount == 0) 3 else 2
                )
            }
        }
    }
}
