package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.data.*
import com.example.ui.components.LearningModeSwitcherBar
import com.example.ui.components.PlayKidsIconButton
import com.example.ui.components.PlayKidsTokens
import com.example.ui.components.PlayoraAnimatedSceneBackground
import com.example.ui.components.PlayoraWorldTheme
import com.example.ui.theme.*

@Composable
fun LevelSelectionScreen(
    gameType: GameType,
    progress: GameProgressData,
    learningMode: LearningMode,
    audio: BanglaAudioEngine,
    onSelectMode: (LearningMode) -> Unit,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allLevels = CurriculumLibrary.getLevelsForGame(gameType)
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

    var lockedFeedbackBanner by remember { mutableStateOf<String?>(null) }

    val displayedChapters = CurriculumLibrary.CHAPTERS

    var selectedChapterNum by remember(learningMode, maxUnlocked) {
        val defaultChapter = ((maxUnlocked - 1) / 5 + 1).coerceIn(1, displayedChapters.size)
        mutableIntStateOf(defaultChapter)
    }

    val activeChapter = displayedChapters.find { it.chapterNumber == selectedChapterNum }
        ?: displayedChapters.first()

    val audioSettings by audio.audioSettings.collectAsState()

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 6.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Screen 4 Top Header: Back Button + Bengali Game Title + Stars Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PlayKidsIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যাও",
                            onClick = {
                                audio.playButtonTap()
                                onBack()
                            },
                            size = 48.dp,
                            gradient = PlayKidsTokens.BlueButtonGradient,
                            testTag = "level_map_back_button"
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .shadow(4.dp, RoundedCornerShape(22.dp)),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White.copy(alpha = 0.94f),
                            border = androidx.compose.foundation.BorderStroke(2.5.dp, PlayBlue)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = gameType.titleBn,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepRoyalBlue,
                                        fontSize = 21.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = StarYellow,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${progress.starsEarned}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = NunitoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepRoyalBlue
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dark Navy Pill Subtitle ("অধ্যায় ১ : মৌলিক আকৃতি") matching Screen 4
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeepRoyalBlue,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "অধ্যায় ${PlayKidsTokens.toBnDigits(activeChapter.chapterNumber)} : ${activeChapter.titleBn}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            ),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LearningModeSwitcherBar(
                        currentMode = learningMode,
                        onSelectMode = { mode ->
                            audio.playButtonTap()
                            lockedFeedbackBanner = null
                            onSelectMode(mode)
                        }
                    )

                    // Locked Level Feedback Banner
                    if (lockedFeedbackBanner != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = StarYellow,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "🔒 $lockedFeedbackBanner",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepRoyalBlue
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom 5-Chapter Selector Bar ("অধ্যায় ১" .. "অধ্যায় ৫") matching Screen 4
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        displayedChapters.forEach { chap ->
                            val isSelected = chap.chapterNumber == selectedChapterNum
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .testTag("chapter_tab_${chap.chapterNumber}")
                                    .clickable {
                                        audio.playButtonTap()
                                        selectedChapterNum = chap.chapterNumber
                                        if (chap.chapterNumber > 1 && learningMode == LearningMode.CORE_CURRICULUM) {
                                            onSelectMode(LearningMode.EXTENDED_PRACTICE)
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) CreativePurple else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.5.dp,
                                    color = if (isSelected) Color.White else BorderSoft
                                ),
                                shadowElevation = if (isSelected) 4.dp else 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "অধ্যায়",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else DeepRoyalBlue
                                        )
                                    )
                                    Text(
                                        text = PlayKidsTokens.toBnDigits(chap.chapterNumber),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else DeepRoyalBlue,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            val visibleLevels = if (learningMode == LearningMode.CORE_CURRICULUM) {
                allLevels.take(5)
            } else {
                allLevels
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cozy Destination Cottage Illustration at Top of Hill (Matching Screen 4)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeChapter.subtitleBn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepRoyalBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Cozy cottage vector on the hill
                        Canvas(modifier = Modifier.size(width = 76.dp, height = 58.dp)) {
                            val w = size.width
                            val h = size.height
                            // House body
                            drawRoundRect(
                                color = Color(0xFFFFF8E1),
                                topLeft = Offset(w * 0.22f, h * 0.42f),
                                size = Size(w * 0.56f, h * 0.52f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                            // Orange roof
                            val roof = Path().apply {
                                moveTo(w * 0.12f, h * 0.46f)
                                lineTo(w * 0.50f, h * 0.10f)
                                lineTo(w * 0.88f, h * 0.46f)
                                close()
                            }
                            drawPath(roof, color = Color(0xFFF97316))
                            // Door
                            drawRoundRect(
                                color = Color(0xFF92400E),
                                topLeft = Offset(w * 0.43f, h * 0.62f),
                                size = Size(w * 0.14f, h * 0.32f),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Winding Path Nodes in rows of 3 along a curved S-road
                val levelRows = visibleLevels.chunked(3)
                itemsIndexed(levelRows) { rowIndex, rowLevels ->
                    val isReversedRow = rowIndex % 2 == 1
                    val orderedRow = if (isReversedRow) rowLevels.reversed() else rowLevels

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(124.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Winding Stone Path Canvas behind the level nodes
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val startX = if (isReversedRow) w * 0.82f else w * 0.18f
                            val endX = if (isReversedRow) w * 0.18f else w * 0.82f

                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(startX, h * 0.52f),
                                end = Offset(endX, h * 0.52f),
                                strokeWidth = 44f,
                                cap = StrokeCap.Round
                            )
                            if (rowIndex < levelRows.size - 1) {
                                drawLine(
                                    color = Color(0xFFE2E8F0),
                                    start = Offset(endX, h * 0.52f),
                                    end = Offset(endX, h),
                                    strokeWidth = 44f,
                                    cap = StrokeCap.Round
                                )
                            }
                            if (rowIndex > 0) {
                                drawLine(
                                    color = Color(0xFFE2E8F0),
                                    start = Offset(startX, 0f),
                                    end = Offset(startX, h * 0.52f),
                                    strokeWidth = 44f,
                                    cap = StrokeCap.Round
                                )
                            }
                            drawLine(
                                color = Color.White.copy(alpha = 0.9f),
                                start = Offset(startX, h * 0.52f),
                                end = Offset(endX, h * 0.52f),
                                strokeWidth = 6f,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            orderedRow.forEach { lvlDef ->
                                val isCompleted = lvlDef.levelNumber in activeCompletedSet
                                val isUnlocked = isCompleted || lvlDef.levelNumber <= maxUnlocked
                                val isCurrent = isUnlocked && !isCompleted && lvlDef.levelNumber == maxUnlocked

                                PlayoraWindingLevelNode(
                                    levelDef = lvlDef,
                                    isCompleted = isCompleted,
                                    isUnlocked = isUnlocked,
                                    isCurrent = isCurrent,
                                    onClick = {
                                        if (isUnlocked) {
                                            lockedFeedbackBanner = null
                                            audio.playCardSelectChime()
                                            onSelectLevel(lvlDef.levelNumber)
                                        } else {
                                            audio.playTryAgainChime()
                                            val lockMsg = "আগের ধাপটি শেষ করে লেভেল ${lvlDef.levelNumber} খোলো।"
                                            lockedFeedbackBanner = lockMsg
                                            audio.speak("আগের ধাপটি শেষ করে এটি খোলো।")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayoraWindingLevelNode(
    levelDef: LevelDefinition,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(92.dp)
            .testTag("level_item_${levelDef.levelNumber}")
            .clickable { onClick() }
    ) {
        // Circular 3D Level Node Button (Matching Screen 4: Gold for unlocked/completed, Purple for current, Silver for locked)
        val nodeGradient = when {
            isCurrent -> PlayKidsTokens.PurplePillGradient
            isUnlocked -> PlayKidsTokens.GoldNextGradient
            else -> PlayKidsTokens.SlateDisabledGradient
        }

        Surface(
            modifier = Modifier
                .size(if (isCurrent) 66.dp else 60.dp)
                .shadow(if (isUnlocked) 8.dp else 3.dp, CircleShape),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(
                width = if (isCurrent) 3.5.dp else 2.5.dp,
                color = if (isCurrent) StarYellow else Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(nodeGradient),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = "${levelDef.levelNumber}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCurrent) Color.White else DeepRoyalBlue,
                            fontSize = 24.sp
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "লক করা",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Stars below unlocked node (Matching Screen 4)
        if (isUnlocked) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { starIdx ->
                    val filled = isCompleted || (isCurrent && starIdx == 0)
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (filled) Color(0xFFFFB300) else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(if (starIdx == 1) 16.dp else 13.dp)
                    )
                }
            }
        }
    }
}
