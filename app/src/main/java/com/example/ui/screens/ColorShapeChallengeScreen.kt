package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.audio.SoundEffectType
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ColorShapeChallengeScreen(
    audio: BanglaAudioEngine,
    progress: GameProgressData,
    learningMode: LearningMode = LearningMode.CORE_CURRICULUM,
    initialLevelOverride: Int? = null,
    onSelectMode: (LearningMode) -> Unit = {},
    onOpenLevelMap: () -> Unit = {},
    onBack: () -> Unit,
    onRecordAttempt: (isSuccess: Boolean, isIndependent: Boolean, shouldAdvance: Boolean, completedLevel: Int) -> Unit,
    onRecordSkillMastery: (colorId: String?, shapeId: String?) -> Unit = { _, _ -> },
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

    // Stage 1: Color Only, Stage 2: Shape Only, Stage 3: Color + Shape Combined
    var currentPhase by remember(learningMode, level) {
        val defaultStage = if (learningMode == LearningMode.EXTENDED_PRACTICE) {
            when (level) {
                in 1..4, 13, 14 -> 1
                in 5..8, 15, 16, 17 -> 2
                else -> 3
            }
        } else {
            1
        }
        mutableIntStateOf(defaultStage)
    }

    var targetColor by remember { mutableStateOf(KidColor.RED) }
    var targetShape by remember { mutableStateOf(KidShape.STAR) }
    var candidateCards by remember { mutableStateOf(listOf<ColorShapeCard>()) }

    var solvedCountInPhase by remember(learningMode, level, restartTrigger) { mutableIntStateOf(0) }
    val requiredSolves = 5

    var errorCount by remember { mutableIntStateOf(0) }
    var hintActive by remember { mutableStateOf(false) }
    var demoActive by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("") }

    fun setupNewRound() {
        val useExtendedColors = learningMode == LearningMode.EXTENDED_PRACTICE && level >= 13
        val useExtendedShapes = learningMode == LearningMode.EXTENDED_PRACTICE && level >= 15
        val useAll20Shapes = learningMode == LearningMode.EXTENDED_PRACTICE && level >= 17

        val availableColors = when {
            learningMode == LearningMode.EXTENDED_PRACTICE && level in 13..14 -> KidColor.EXTENDED_ONLY_COLORS
            useExtendedColors -> KidColor.ALL_10_COLORS
            else -> KidColor.CORE_COLORS
        }

        val availableShapes = when {
            learningMode == LearningMode.EXTENDED_PRACTICE && level in 15..16 ->
                KidShape.COMBO_10_SHAPES
            useAll20Shapes -> KidShape.ALL_20_SHAPES
            useExtendedShapes -> KidShape.COMBO_10_SHAPES
            else -> KidShape.CORE_SHAPES
        }

        val activeLibrary = if (useExtendedColors || useExtendedShapes || useAll20Shapes) {
            CurriculumLibrary.EXTENDED_200_COLOR_SHAPE_CARDS.filter {
                it.color in availableColors && it.shape in availableShapes
            }
        } else {
            CurriculumLibrary.ALL_COLOR_SHAPE_CARDS
        }

        val randColor = availableColors.random()
        val randShape = availableShapes.random()
        targetColor = randColor
        targetShape = randShape
        errorCount = 0
        hintActive = false
        demoActive = false

        val targetCard = activeLibrary.firstOrNull {
            it.color == randColor && it.shape == randShape
        } ?: CurriculumLibrary.EXTENDED_200_COLOR_SHAPE_CARDS.first {
            it.color == randColor && it.shape == randShape
        }

        val distractorCount = when {
            level <= 2 -> 2
            level <= 6 -> 3
            else -> 5
        }

        val distractors = when (currentPhase) {
            1 -> {
                activeLibrary
                    .filter { it.color != randColor }
                    .shuffled()
                    .distinctBy { it.color }
                    .take(distractorCount)
            }
            2 -> {
                activeLibrary
                    .filter { it.shape != randShape }
                    .shuffled()
                    .distinctBy { it.shape }
                    .take(distractorCount)
            }
            else -> {
                val sameColorWrongShape = activeLibrary.filter { it.color == randColor && it.shape != randShape }.shuffled().take(1)
                val wrongColorSameShape = activeLibrary.filter { it.color != randColor && it.shape == randShape }.shuffled().take(1)
                val others = activeLibrary.filter {
                    it.id != targetCard.id && it !in sameColorWrongShape && it !in wrongColorSameShape
                }.shuffled().take((distractorCount - sameColorWrongShape.size - wrongColorSameShape.size).coerceAtLeast(1))
                (sameColorWrongShape + wrongColorSameShape + others).take(distractorCount)
            }
        }

        candidateCards = (listOf(targetCard) + distractors).shuffled()

        when (currentPhase) {
            1 -> {
                speechText = "${randColor.nameBn} রংটি খুঁজে বের করো।"
                audio.speak(
                    text = speechText,
                    dedupeKey = "cs_p1_${learningMode.id}_L${level}_S${solvedCountInPhase}_${randColor.id}",
                    queueAfterPraise = true
                )
            }
            2 -> {
                speechText = "${randShape.nameBn}টি খুঁজে দাও।"
                audio.speak(
                    text = speechText,
                    dedupeKey = "cs_p2_${learningMode.id}_L${level}_S${solvedCountInPhase}_${randShape.id}",
                    queueAfterPraise = true
                )
            }
            else -> {
                speechText = "${randColor.nameBn} ${randShape.nameBn}টি নির্বাচন করো।"
                audio.speak(
                    text = speechText,
                    dedupeKey = "cs_p3_${learningMode.id}_L${level}_S${solvedCountInPhase}_${randColor.id}_${randShape.id}",
                    queueAfterPraise = true
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audio.stopAndClearQueue()
        }
    }

    fun restartCurrentLevel() {
        audio.stopAndClearQueue()
        audio.playButtonTap()
        showSuccessDialog = false
        showPauseDialog = false
        solvedCountInPhase = 0
        restartTrigger++
        setupNewRound()
    }

    LaunchedEffect(learningMode, level, currentPhase, restartTrigger) {
        setupNewRound()
    }

    val levelDef = remember(level) {
        CurriculumLibrary.getLevelsForGame(GameType.COLOR_SHAPE_CHALLENGE).getOrNull(level - 1)
    }

    val hasNextUnlocked = level < maxLevel && ((level + 1) <= maxUnlocked || (level + 1) in activeCompletedSet || level in activeCompletedSet)

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.RAINBOW_DISCOVERY_WORLD,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(Color.White.copy(alpha = 0.88f))) {
                    GameTopBar(
                        title = "${GameType.COLOR_SHAPE_CHALLENGE.titleEn} • ${GameType.COLOR_SHAPE_CHALLENGE.titleBn}",
                        serialNumber = GameType.COLOR_SHAPE_CHALLENGE.serialName,
                        currentLevel = level,
                        starsCount = progress.starsEarned,
                        onBackClick = onBack,
                        themeColor = GameType.COLOR_SHAPE_CHALLENGE.themeColor,
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
                        themeColor = GameType.COLOR_SHAPE_CHALLENGE.themeColor,
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
                // Three Pedagogical Stages Selector (Preserved Order: 1. Color -> 2. Shape -> 3. Color + Shape)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        1 to "১. শুধু রং",
                        2 to "২. শুধু আকৃতি",
                        3 to "৩. রং + আকৃতি"
                    ).forEach { (phaseNum, label) ->
                        val isSelected = currentPhase == phaseNum
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (currentPhase != phaseNum) {
                                        audio.playButtonTap()
                                        currentPhase = phaseNum
                                        solvedCountInPhase = 0
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CreativePurple else Color.White.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color.White else BorderSoft
                            ),
                            shadowElevation = if (isSelected) 4.dp else 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DeepRoyalBlue,
                                        fontSize = 12.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                MascotSpeechPrompt(
                    text = speechText,
                    onReplayAudio = { audio.replayInstruction(speechText) },
                    isHighlighted = hintActive || demoActive
                )

                // Mission / Visual Clue Board (Color + Shape = Target)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = levelDef?.titleBn ?: "লেভেল $level",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = CreativePurple,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftLavender
                            ) {
                                Text(
                                    text = "অগ্রগতি: $solvedCountInPhase/$requiredSolves",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = DeepRoyalBlue,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            when (currentPhase) {
                                1 -> {
                                    Box(
                                        modifier = Modifier
                                            .size(66.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(targetColor.color)
                                            .border(2.5.dp, targetColor.darkColor, RoundedCornerShape(18.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = targetColor.patternSymbol,
                                            color = if (targetColor == KidColor.WHITE || targetColor == KidColor.YELLOW) DeepRoyalBlue else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "১ম ধাপ: শুধু রং চেনা",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                                        )
                                        Text(
                                            text = "${targetColor.nameBn} রং",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepRoyalBlue
                                            )
                                        )
                                    }
                                }
                                2 -> {
                                    Box(
                                        modifier = Modifier
                                            .size(66.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(SoftSky)
                                            .border(2.dp, PlayBlue, RoundedCornerShape(18.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ShapeGraphic(shape = targetShape, color = PlayBlue, sizeDp = 48.dp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "২য় ধাপ: শুধু আকৃতি চেনা",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                                        )
                                        Text(
                                            text = targetShape.nameBn,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepRoyalBlue
                                            )
                                        )
                                    }
                                }
                                else -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(targetColor.color)
                                                .border(2.dp, targetColor.darkColor, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = targetColor.patternSymbol,
                                                color = if (targetColor == KidColor.WHITE || targetColor == KidColor.YELLOW) DeepRoyalBlue else Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = " + ",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(SoftSky)
                                                .border(1.5.dp, PlayBlue, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ShapeGraphic(
                                                shape = targetShape,
                                                color = DeepRoyalBlue,
                                                sizeDp = 32.dp,
                                                isOutlineOnly = true,
                                                outlineColor = DeepRoyalBlue
                                            )
                                        }
                                        Text(
                                            text = " = ",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(58.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(targetColor.lightColor)
                                                .border(2.dp, targetColor.darkColor, RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ShapeGraphic(shape = targetShape, color = targetColor.color, sizeDp = 42.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Candidate Cards Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (candidateCards.size > 4) 3 else 2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(candidateCards) { card ->
                        val isCorrect = when (currentPhase) {
                            1 -> card.color == targetColor
                            2 -> card.shape == targetShape
                            else -> card.color == targetColor && card.shape == targetShape
                        }
                        val shouldHighlight = (hintActive || demoActive) && isCorrect

                        Surface(
                            modifier = Modifier
                                .height(112.dp)
                                .shadow(if (shouldHighlight) 8.dp else 4.dp, RoundedCornerShape(22.dp))
                                .testTag("color_shape_card_${card.id}")
                                .clickable {
                                    audio.playEffect(SoundEffectType.CARD_SELECT)

                                    if (isCorrect) {
                                        audio.playSuccessChime()
                                        audio.speakPraise()
                                        if (errorCount == 0) {
                                            when (currentPhase) {
                                                1 -> onRecordSkillMastery(targetColor.id, null)
                                                2 -> onRecordSkillMastery(null, targetShape.id)
                                                else -> onRecordSkillMastery(targetColor.id, targetShape.id)
                                            }
                                        }
                                        solvedCountInPhase++

                                        if (solvedCountInPhase >= requiredSolves) {
                                            audio.playLevelCompleteFanfare()
                                            audio.speak("এই ধাপটি সম্পন্ন হয়েছে।")
                                            onRecordAttempt(true, errorCount == 0, level < maxLevel, level)
                                            showSuccessDialog = true
                                        } else {
                                            setupNewRound()
                                        }
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
                                                speechText = "সঠিক কার্ডটি বেছে নাও।"
                                                audio.speak("সঠিক কার্ডটি বেছে নাও।")
                                                hintActive = true
                                                demoActive = true
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(22.dp),
                            color = if (shouldHighlight) SoftSunshine else Color.White.copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (shouldHighlight) 3.5.dp else 2.5.dp,
                                color = if (shouldHighlight) Color(0xFFF59E0B) else Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                ShapeGraphic(
                                    shape = card.shape,
                                    color = card.color.color,
                                    sizeDp = 52.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (demoActive && isCorrect) "👉 ${card.nameBn}" else card.nameBn,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepRoyalBlue,
                                        fontSize = 12.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
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
                        if (learningMode == LearningMode.CORE_CURRICULUM && currentPhase < 3) {
                            currentPhase++
                        } else if (level < maxLevel) {
                            level++
                        }
                        solvedCountInPhase = 0
                        setupNewRound()
                    },
                    onGoPhysicalPractice = {
                        showSuccessDialog = false
                        audio.playEffect(SoundEffectType.PHYSICAL_TRANSITION)
                        onGoPhysicalPractice()
                    },
                    levelNumber = level,
                    starsAwarded = if (errorCount == 0) 3 else 2,
                    titleText = "খুব ভালো হয়েছে!",
                    messageText = "এই ধাপটি সম্পন্ন হয়েছে। চলো, পরের ধাপে যাই।"
                )
            }
        }
    }
}
