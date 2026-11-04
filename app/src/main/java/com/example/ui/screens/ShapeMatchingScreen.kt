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
fun ShapeMatchingScreen(
    audio: BanglaAudioEngine,
    progress: GameProgressData,
    learningMode: LearningMode = LearningMode.CORE_CURRICULUM,
    initialLevelOverride: Int? = null,
    onSelectMode: (LearningMode) -> Unit = {},
    onOpenLevelMap: () -> Unit = {},
    onBack: () -> Unit,
    onRecordAttempt: (isSuccess: Boolean, isIndependent: Boolean, shouldAdvance: Boolean, completedLevel: Int) -> Unit,
    onRecordSkillMastery: (shapeId: String) -> Unit = {},
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
    // Tab 0 = 2D Shape Matching (25 Shapes: 5 Core + 20 Additional), Tab 1 = 3D Shape Discovery (8 3D Shapes)
    var activeTab by remember { mutableIntStateOf(0) }

    val audioSettings by audio.audioSettings.collectAsState()

    // Shapes for current level (1..25), covering all 25 2D shapes (5 Core + 20 Additional)
    val levelShapes: List<KidShape> = remember(learningMode, level, restartTrigger) {
        if (learningMode == LearningMode.CORE_CURRICULUM) {
            when (level) {
                1 -> listOf(KidShape.CIRCLE, KidShape.TRIANGLE)
                2 -> listOf(KidShape.CIRCLE, KidShape.TRIANGLE, KidShape.SQUARE)
                3 -> KidShape.CORE_SHAPES
                4 -> KidShape.CORE_SHAPES.shuffled()
                else -> KidShape.CORE_SHAPES
            }
        } else {
            when (level) {
                1 -> listOf(KidShape.CIRCLE, KidShape.TRIANGLE)
                2 -> listOf(KidShape.SQUARE, KidShape.STAR)
                3 -> listOf(KidShape.CIRCLE, KidShape.TRIANGLE, KidShape.SQUARE)
                4 -> listOf(KidShape.TRIANGLE, KidShape.STAR, KidShape.HEART)
                5 -> listOf(KidShape.CIRCLE, KidShape.TRIANGLE, KidShape.SQUARE, KidShape.STAR)
                6 -> KidShape.CORE_SHAPES
                7, 8 -> KidShape.CORE_SHAPES.shuffled()
                9 -> listOf(KidShape.RECTANGLE, KidShape.OVAL, KidShape.CIRCLE, KidShape.SQUARE)
                10 -> listOf(KidShape.DIAMOND, KidShape.SEMICIRCLE, KidShape.TRIANGLE, KidShape.CIRCLE)
                11 -> listOf(KidShape.CRESCENT, KidShape.STAR, KidShape.HEART, KidShape.OVAL, KidShape.RECTANGLE)
                12 -> listOf(KidShape.CIRCLE, KidShape.RECTANGLE, KidShape.OVAL, KidShape.DIAMOND, KidShape.SEMICIRCLE, KidShape.CRESCENT)
                13 -> listOf(KidShape.PENTAGON, KidShape.HEXAGON, KidShape.SQUARE, KidShape.TRIANGLE)
                14 -> listOf(KidShape.OCTAGON, KidShape.TRAPEZOID, KidShape.PENTAGON, KidShape.HEXAGON)
                15 -> listOf(KidShape.PARALLELOGRAM, KidShape.TRAPEZOID, KidShape.RECTANGLE, KidShape.DIAMOND, KidShape.SQUARE)
                16 -> listOf(KidShape.PENTAGON, KidShape.HEXAGON, KidShape.OCTAGON, KidShape.TRAPEZOID, KidShape.PARALLELOGRAM)
                17 -> listOf(KidShape.CROSS, KidShape.ARROW, KidShape.KITE, KidShape.CIRCLE)
                18 -> listOf(KidShape.HEPTAGON, KidShape.DECAGON, KidShape.PENTAGON, KidShape.HEXAGON)
                19 -> listOf(KidShape.RIGHT_TRIANGLE, KidShape.RING, KidShape.FOUR_POINT_STAR, KidShape.CLOUD_SHAPE, KidShape.SHIELD, KidShape.STAR)
                20 -> KidShape.CORE_SHAPES // Silhouette challenge
                21 -> listOf(KidShape.CLOUD_SHAPE, KidShape.SHIELD, KidShape.FOUR_POINT_STAR, KidShape.RING, KidShape.STAR, KidShape.HEART)
                22 -> listOf(KidShape.CIRCLE, KidShape.SQUARE, KidShape.STAR, KidShape.HEXAGON, KidShape.CROSS, KidShape.KITE)
                23 -> listOf(KidShape.TRIANGLE, KidShape.RIGHT_TRIANGLE, KidShape.FOUR_POINT_STAR, KidShape.DIAMOND, KidShape.ARROW, KidShape.PENTAGON)
                24 -> listOf(KidShape.CIRCLE, KidShape.OVAL, KidShape.RING, KidShape.HEXAGON, KidShape.HEPTAGON, KidShape.OCTAGON)
                else -> KidShape.ALL_25_SHAPES.shuffled().take(6)
            }
        }
    }

    val isSilhouetteMode = learningMode == LearningMode.EXTENDED_PRACTICE && level == 20
    val isSizeVariationMode = learningMode == LearningMode.EXTENDED_PRACTICE && level == 22
    val isRotatedMode = learningMode == LearningMode.EXTENDED_PRACTICE && level == 23

    var remainingShapes by remember(learningMode, level, restartTrigger) {
        mutableStateOf(levelShapes.shuffled())
    }

    val currentTargetShape = remainingShapes.firstOrNull()

    var placedShapes by remember(learningMode, level, restartTrigger) {
        mutableStateOf(setOf<KidShape>())
    }

    var errorCount by remember(currentTargetShape, restartTrigger) { mutableIntStateOf(0) }
    var hintActive by remember(currentTargetShape, restartTrigger) { mutableStateOf(false) }
    var demoActive by remember(currentTargetShape, restartTrigger) { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("ত্রিভুজটি খুঁজে দাও।") }

    // 3D Shape Discovery State
    var selected3DShape by remember { mutableStateOf(Kid3DShape.SPHERE) }
    var quiz3DTarget by remember { mutableStateOf(Kid3DShape.ALL_3D_SHAPES.first()) }
    var quiz3DMatches by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            audio.stopAndClearQueue()
        }
    }

    fun playInstruction(isManualReplay: Boolean = false) {
        if (activeTab == 1) {
            val prompt = "ত্রিমাত্রিক আকৃতি: ${selected3DShape.nameBn}। উদাহরণ: ${selected3DShape.realLifeExampleBn}।"
            speechText = prompt
            if (isManualReplay) {
                audio.replayInstruction(prompt)
            } else {
                audio.speak(
                    text = prompt,
                    dedupeKey = "shape3d_${selected3DShape.id}",
                    isManualReplay = false,
                    queueAfterPraise = true
                )
            }
        } else {
            currentTargetShape?.let { shape ->
                val prompt = "${shape.nameBn}টি খুঁজে দাও।"
                speechText = prompt
                if (isManualReplay) {
                    audio.replayInstruction(prompt)
                } else {
                    audio.speak(
                        text = prompt,
                        dedupeKey = "shape2d_${learningMode.id}_L${level}_R${restartTrigger}_${shape.id}_${placedShapes.size}",
                        isManualReplay = false,
                        queueAfterPraise = true
                    )
                }
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

    LaunchedEffect(currentTargetShape, restartTrigger, activeTab, selected3DShape) {
        if (activeTab == 0) {
            if (currentTargetShape != null) {
                playInstruction(isManualReplay = false)
            } else if (placedShapes.size == levelShapes.size && levelShapes.isNotEmpty()) {
                audio.playLevelCompleteFanfare()
                audio.speak(
                    text = "এই ধাপটি সম্পন্ন হয়েছে।",
                    dedupeKey = "shape_complete_${learningMode.id}_L${level}_R${restartTrigger}",
                    queueAfterPraise = true
                )
                onRecordAttempt(true, errorCount == 0, level < maxLevel, level)
                showSuccessDialog = true
            }
        } else {
            playInstruction(isManualReplay = false)
        }
    }

    // Distinct vibrant colors for each of the 25 geometric shapes
    fun getShapeColor(shape: KidShape): Color = when (shape) {
        KidShape.CIRCLE -> KidRed
        KidShape.TRIANGLE -> KidYellowDark
        KidShape.SQUARE -> KidBlue
        KidShape.STAR -> KidGreen
        KidShape.HEART -> KidOrange
        KidShape.RECTANGLE -> KidPurple
        KidShape.OVAL -> KidPink
        KidShape.DIAMOND -> PrimaryBlue
        KidShape.SEMICIRCLE -> MintGreen
        KidShape.CRESCENT -> Color(0xFFF59E0B)
        KidShape.PENTAGON -> WarmCoral
        KidShape.HEXAGON -> DeepLavender
        KidShape.OCTAGON -> KidRedDark
        KidShape.TRAPEZOID -> KidGreenDark
        KidShape.PARALLELOGRAM -> KidBrown
        KidShape.CROSS -> PrimaryBlue
        KidShape.ARROW -> KidOrangeDark
        KidShape.KITE -> KidPurpleDark
        KidShape.HEPTAGON -> KidGreen
        KidShape.DECAGON -> WarmCoral
        KidShape.RIGHT_TRIANGLE -> PlayBlue
        KidShape.RING -> Color(0xFFF59E0B)
        KidShape.FOUR_POINT_STAR -> CreativePurple
        KidShape.CLOUD_SHAPE -> Color(0xFF38BDF8)
        KidShape.SHIELD -> Color(0xFF16A34A)
    }

    val levelDef = remember(level) {
        CurriculumLibrary.getLevelsForGame(GameType.SHAPE_MATCH).getOrNull(level - 1)
    }

    val hasNextUnlocked = level < maxLevel && ((level + 1) <= maxUnlocked || (level + 1) in activeCompletedSet || level in activeCompletedSet)

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.MAGICAL_SHAPE_GARDEN,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(Color.White.copy(alpha = 0.88f))) {
                    GameTopBar(
                        title = "${GameType.SHAPE_MATCH.titleEn} • ${GameType.SHAPE_MATCH.titleBn}",
                        serialNumber = GameType.SHAPE_MATCH.serialName,
                        currentLevel = level,
                        starsCount = progress.starsEarned,
                        onBackClick = onBack,
                        themeColor = GameType.SHAPE_MATCH.themeColor,
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
                        themeColor = GameType.SHAPE_MATCH.themeColor,
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
                            if (mode == LearningMode.CORE_CURRICULUM) activeTab = 0
                            onSelectMode(mode)
                        }
                    )
                    // Dedicated 2D (25 Shapes) vs 3D Shape Discovery (8 Shapes) Tab Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("tab_2d_shapes")
                                .clickable {
                                    audio.playButtonTap()
                                    activeTab = 0
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (activeTab == 0) LearningMint else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (activeTab == 0) Color.White else BorderSoft)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "📐 ২ডি আকৃতি (২৫টি)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeTab == 0) Color.White else DeepRoyalBlue
                                    )
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("tab_3d_shapes")
                                .clickable {
                                    audio.playButtonTap()
                                    activeTab = 1
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (activeTab == 1) CreativePurple else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (activeTab == 1) Color.White else BorderSoft)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🧊 ৩ডি ত্রিমাত্রিক আকৃতি (৮টি)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeTab == 1) Color.White else DeepRoyalBlue
                                    )
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (activeTab == 1) {
                // Dedicated 3D Shape Discovery Section (8 3D Shapes)
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
                        isHighlighted = false
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .shadow(6.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White.copy(alpha = 0.94f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(SoftLavender)
                                    .border(2.dp, CreativePurple, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Shape3DGraphic(
                                    shape3D = selected3DShape,
                                    primaryColor = CreativePurple,
                                    sizeDp = 62.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "3D Shape Discovery • ত্রিমাত্রিক আকৃতি",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CreativePurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = selected3DShape.nameBn,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepRoyalBlue
                                    )
                                )
                                Text(
                                    text = "বাস্তব উদাহরণ: ${selected3DShape.realLifeExampleBn}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = KidGreenDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "চলো খুঁজে বের করি: ${quiz3DTarget.nameBn} কোথায়? (সফল: $quiz3DMatches)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = PlayBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(Kid3DShape.ALL_3D_SHAPES) { shape3d ->
                            val isSelected = shape3d == selected3DShape
                            val isQuizTarget = shape3d == quiz3DTarget

                            Surface(
                                modifier = Modifier
                                    .height(108.dp)
                                    .shadow(4.dp, RoundedCornerShape(20.dp))
                                    .testTag("shape_3d_card_${shape3d.id}")
                                    .clickable {
                                        selected3DShape = shape3d
                                        if (isQuizTarget) {
                                            audio.playSuccessChime()
                                            quiz3DMatches++
                                            val msg = "সাবাশ! এটি হলো ${shape3d.nameBn} (${shape3d.realLifeExampleBn})!"
                                            speechText = msg
                                            audio.speak(msg)
                                            quiz3DTarget = Kid3DShape.ALL_3D_SHAPES.filter { it != shape3d }.random()
                                        } else {
                                            audio.playCardSelectChime()
                                            val msg = "এটি ${shape3d.nameBn}। যেমন: ${shape3d.realLifeExampleBn}।"
                                            speechText = msg
                                            audio.speak(msg)
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) SoftLavender else Color.White.copy(alpha = 0.94f),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.5.dp else 2.dp,
                                    color = if (isSelected) CreativePurple else Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Shape3DGraphic(
                                        shape3D = shape3d,
                                        primaryColor = if (isSelected) CreativePurple else PlayBlue,
                                        sizeDp = 52.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = shape3d.nameBn,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DeepRoyalBlue
                                            )
                                        )
                                        Text(
                                            text = shape3d.realLifeExampleBn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SecondaryText,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Standard 2D Shape Matching Gameplay (25 Shapes, 25 Levels)
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

                    // Center 3D Target Shape Showcase Card (Matching Reference Image 5)
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
                                        color = LearningMint,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SoftMint
                                ) {
                                    Text(
                                        text = "সম্পন্ন: ${placedShapes.size}/${levelShapes.size}",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = DeepRoyalBlue,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (currentTargetShape != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(94.dp)
                                            .clip(RoundedCornerShape(22.dp))
                                            .background(SoftSky)
                                            .border(2.5.dp, PlayBlue.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ShapeGraphic(
                                            shape = currentTargetShape,
                                            color = getShapeColor(currentTargetShape),
                                            sizeDp = 66.dp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "একই আকৃতিটি খুঁজে বসাও:",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SecondaryText,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = currentTargetShape.nameBn,
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepRoyalBlue,
                                                fontSize = 23.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (levelShapes.size > 4) 3 else 2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(levelShapes) { shape ->
                            val isPlaced = shape in placedShapes
                            val isTarget = shape == currentTargetShape
                            val shouldHighlight = (hintActive || demoActive) && isTarget

                            Surface(
                                modifier = Modifier
                                    .height(106.dp)
                                    .shadow(if (shouldHighlight) 8.dp else 4.dp, RoundedCornerShape(22.dp))
                                    .testTag("shape_slot_${shape.id}")
                                    .clickable {
                                        if (isPlaced) return@clickable
                                        audio.playEffect(SoundEffectType.CARD_PLACE)

                                        if (isTarget) {
                                            audio.playSuccessChime()
                                            audio.speakPraise()
                                            speechText = "খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।"
                                            if (errorCount == 0) {
                                                onRecordSkillMastery(shape.id)
                                            }
                                            placedShapes = placedShapes + shape
                                            remainingShapes = remainingShapes.filter { it != shape }
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
                                                    speechText = "সঠিক কার্ডটি বেছে নাও: ${currentTargetShape?.nameBn}।"
                                                    audio.speak("সঠিক কার্ডটি বেছে নাও।")
                                                    hintActive = true
                                                    demoActive = true
                                                }
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(22.dp),
                                color = when {
                                    shouldHighlight -> SoftSunshine
                                    isPlaced -> SoftMint.copy(alpha = 0.85f)
                                    else -> Color.White.copy(alpha = 0.95f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (shouldHighlight) 3.5.dp else 2.5.dp,
                                    color = when {
                                        shouldHighlight -> Color(0xFFF59E0B)
                                        isPlaced -> LearningMint
                                        else -> Color.White
                                    }
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
                                        shape = shape,
                                        color = getShapeColor(shape),
                                        sizeDp = if (isSizeVariationMode && !isPlaced) 38.dp else 54.dp,
                                        isOutlineOnly = false,
                                        isSilhouette = !isPlaced && isSilhouetteMode,
                                        outlineColor = if (shouldHighlight) Color(0xFFD97706) else Color(0xFF94A3B8),
                                        rotationDegrees = if (isRotatedMode && !isPlaced && shape != KidShape.CIRCLE) 35f else 0f
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (demoActive && isTarget) "👉 ${shape.nameBn}" else shape.nameBn,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = if (isPlaced) KidGreenDark else DeepRoyalBlue,
                                            fontWeight = FontWeight.Bold,
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
                        placedShapes = emptySet()
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
