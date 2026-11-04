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
fun EmojiMatchingScreen(
    audio: BanglaAudioEngine,
    progress: GameProgressData,
    learningMode: LearningMode = LearningMode.CORE_CURRICULUM,
    initialLevelOverride: Int? = null,
    onSelectMode: (LearningMode) -> Unit = {},
    onOpenLevelMap: () -> Unit = {},
    onBack: () -> Unit,
    onRecordAttempt: (isSuccess: Boolean, isIndependent: Boolean, shouldAdvance: Boolean, completedLevel: Int) -> Unit,
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

    // Select items pool based on Mode and Level (1..25)
    val levelItems: List<PictureCardItem> = remember(learningMode, level, restartTrigger) {
        if (learningMode == LearningMode.CORE_CURRICULUM) {
            val pairCount = when (level) {
                1 -> 2
                2 -> 3
                3 -> 4
                4 -> 6
                else -> 9
            }
            CurriculumLibrary.CORE_PICTURE_ITEMS.take(pairCount)
        } else {
            when (level) {
                1, 2 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(2)
                3, 4 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(3)
                5 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(4)
                6 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(5)
                7, 8 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(6)
                9 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(7)
                10 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(8)
                11 -> CurriculumLibrary.CORE_PICTURE_ITEMS.take(9)
                12 -> {
                    val extras = CurriculumLibrary.EXTRA_EMOJIS.mapIndexed { idx, p ->
                        PictureCardItem("ext_$idx", p.first, p.second, PictureCategory.OPTIONAL_EMOJI, SoftSunshine)
                    }
                    CurriculumLibrary.CORE_PICTURE_ITEMS + extras
                }
                13 -> CurriculumLibrary.FRUITS_AND_FOOD.take(4)
                14 -> CurriculumLibrary.FRUITS_AND_FOOD.take(6)
                15 -> CurriculumLibrary.FRUITS_AND_FOOD.take(8)
                16 -> CurriculumLibrary.ANIMALS.take(5)
                17 -> CurriculumLibrary.ANIMALS.take(7)
                18 -> CurriculumLibrary.NATURE.take(5)
                19 -> CurriculumLibrary.NATURE.take(7)
                20 -> CurriculumLibrary.VEHICLES.take(6)
                21 -> CurriculumLibrary.DAILY_OBJECTS.take(5)
                22 -> CurriculumLibrary.DAILY_OBJECTS.take(8)
                23 -> (CurriculumLibrary.FRUITS_AND_FOOD.take(3) + CurriculumLibrary.ANIMALS.take(3))
                24 -> (CurriculumLibrary.NATURE.take(3) + CurriculumLibrary.VEHICLES.take(2) + CurriculumLibrary.DAILY_OBJECTS.take(2))
                else -> (CurriculumLibrary.CORE_PICTURE_ITEMS.take(3) + CurriculumLibrary.FRUITS_AND_FOOD.take(2) + CurriculumLibrary.ANIMALS.take(2) + CurriculumLibrary.DAILY_OBJECTS.take(2))
            }
        }
    }

    var remainingItems by remember(learningMode, level, restartTrigger) {
        mutableStateOf(levelItems.shuffled())
    }

    val currentTargetItem = remainingItems.firstOrNull()

    var matchedItems by remember(learningMode, level, restartTrigger) {
        mutableStateOf(listOf<PictureCardItem>())
    }

    val candidateOptions = remember(learningMode, level, restartTrigger, currentTargetItem) {
        levelItems.shuffled()
    }

    var errorCount by remember(currentTargetItem, restartTrigger) { mutableIntStateOf(0) }
    var hintActive by remember(currentTargetItem, restartTrigger) { mutableStateOf(false) }
    var demoActive by remember(currentTargetItem, restartTrigger) { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("একই ছবিটি খুঁজে বের করো।") }

    DisposableEffect(Unit) {
        onDispose {
            audio.stopAndClearQueue()
        }
    }

    fun playInstruction(isManualReplay: Boolean = false) {
        currentTargetItem?.let { item ->
            val prompt = "একই ছবিটি খুঁজে বের করো: ${item.nameBn}।"
            speechText = prompt
            if (isManualReplay) {
                audio.replayInstruction("একই ছবিটি খুঁজে বের করো।")
            } else {
                audio.speak(
                    text = "একই ছবিটি খুঁজে বের করো।",
                    dedupeKey = "emoji_${learningMode.id}_L${level}_R${restartTrigger}_${item.id}",
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

    LaunchedEffect(currentTargetItem, restartTrigger) {
        if (currentTargetItem != null) {
            playInstruction(isManualReplay = false)
        } else if (matchedItems.size == levelItems.size && levelItems.isNotEmpty()) {
            audio.playLevelCompleteFanfare()
            audio.speak(
                text = "এই ধাপটি সম্পন্ন হয়েছে।",
                dedupeKey = "emoji_complete_${learningMode.id}_L${level}_R${restartTrigger}",
                queueAfterPraise = true
            )
            onRecordAttempt(true, errorCount == 0, level < maxLevel, level)
            showSuccessDialog = true
        }
    }

    val levelDef = remember(level) {
        CurriculumLibrary.getLevelsForGame(GameType.EMOJI_MATCH).getOrNull(level - 1)
    }

    val hasNextUnlocked = level < maxLevel && ((level + 1) <= maxUnlocked || (level + 1) in activeCompletedSet || level in activeCompletedSet)

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.EMOJI_DISCOVERY_WORLD,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(Color.White.copy(alpha = 0.88f))) {
                    GameTopBar(
                        title = "${GameType.EMOJI_MATCH.titleEn} • ${GameType.EMOJI_MATCH.titleBn}",
                        serialNumber = GameType.EMOJI_MATCH.serialName,
                        currentLevel = level,
                        starsCount = progress.starsEarned,
                        onBackClick = onBack,
                        themeColor = GameType.EMOJI_MATCH.themeColor,
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
                        themeColor = GameType.EMOJI_MATCH.themeColor,
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

                // Upper Matching Table Area (3D Picture Card + Partner Slot)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.94f),
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
                                    color = PlayBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftSky
                            ) {
                                Text(
                                    text = "মিলেছে: ${matchedItems.size}/${levelItems.size}",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = DeepRoyalBlue,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Target Card in Hand with 3D Illustrated Medallion
                            Surface(
                                modifier = Modifier.size(92.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = currentTargetItem?.badgeColor ?: SoftSunshine,
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, PlayBlue),
                                shadowElevation = 4.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    PlayoraIllustratedPictureBadge(
                                        symbol = currentTargetItem?.symbol ?: "✨",
                                        badgeColor = currentTargetItem?.badgeColor ?: SoftSunshine,
                                        sizeDp = 52.dp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentTargetItem?.nameBn ?: "",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepRoyalBlue
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "+",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = PlayBlue,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // Partner Placement Slot
                            Surface(
                                modifier = Modifier.size(92.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = if (demoActive) SoftMint else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 2.5.dp,
                                    color = if (hintActive || demoActive) StarYellow else Color(0xFF94A3B8)
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (demoActive && currentTargetItem != null) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            PlayoraIllustratedPictureBadge(
                                                symbol = currentTargetItem.symbol,
                                                badgeColor = currentTargetItem.badgeColor,
                                                sizeDp = 50.dp
                                            )
                                            Text(
                                                text = "এখানে বসবে",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = KidGreenDark,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "?",
                                            fontSize = 34.sp,
                                            color = SecondaryText,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Playable Board Cards Grid (Tap to Select & Place)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "একই ছবিটিতে ট্যাপ করো:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue
                            )
                        )
                        Text(
                            text = currentTargetItem?.category?.titleBn ?: "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PlayBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (candidateOptions.size > 4) 3 else 2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(candidateOptions) { cardItem ->
                        val isTarget = cardItem.symbol == currentTargetItem?.symbol
                        val shouldHighlight = (hintActive || demoActive) && isTarget

                        Surface(
                            modifier = Modifier
                                .height(102.dp)
                                .shadow(if (shouldHighlight) 8.dp else 4.dp, RoundedCornerShape(22.dp))
                                .testTag("emoji_card_${cardItem.symbol}")
                                .clickable {
                                    audio.playEffect(SoundEffectType.CARD_SELECT)
                                    if (isTarget && currentTargetItem != null) {
                                        audio.playSuccessChime()
                                        audio.speakPraise()
                                        speechText = "খুব ভালো হয়েছে! হ্যাঁ, এটি সঠিক উত্তর।"
                                        matchedItems = matchedItems + cardItem
                                        remainingItems = remainingItems.drop(1)
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
                                                speechText = "সঠিক কার্ডটি বেছে নাও: ${currentTargetItem?.nameBn}।"
                                                audio.speak("সঠিক কার্ডটি বেছে নাও।")
                                                hintActive = true
                                                demoActive = true
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(22.dp),
                            color = if (shouldHighlight) SoftSunshine else Color.White.copy(alpha = 0.96f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (shouldHighlight) 3.5.dp else 2.5.dp,
                                color = if (shouldHighlight) Color(0xFFF59E0B) else StarYellow
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PlayoraIllustratedPictureBadge(
                                    symbol = cardItem.symbol,
                                    badgeColor = cardItem.badgeColor,
                                    sizeDp = 56.dp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = cardItem.nameBn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = DeepRoyalBlue,
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
                        matchedItems = emptyList()
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
