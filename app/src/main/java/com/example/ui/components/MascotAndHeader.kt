package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BrandConfig
import com.example.data.LearningMode
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class MascotMood {
    IDLE_FLOAT,
    WELCOME_WAVE,
    HAPPY_BOUNCE,
    THINKING,
    ENCOURAGING,
    CELEBRATING
}

/**
 * PlayKids Signature Modern Learning Star Mascot & Emblem (Version 6.0):
 * - Clean, balanced, elegant Golden Learning Star (no oversized eyes or cheap 3D distortion)
 * - Open Educational Book held at the base
 * - Subtle Play Symbol (▶) cleanly integrated on the right page of the open book
 */
@Composable
fun SmilingStarMascot(
    modifier: Modifier = Modifier,
    size: Int = 64,
    isHappy: Boolean = false,
    showBook: Boolean = true,
    isMonochrome: Boolean = false,
    monochromeTint: Color = DeepRoyalBlue,
    mood: MascotMood = if (isHappy) MascotMood.CELEBRATING else MascotMood.IDLE_FLOAT,
    reducedMotion: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "playkids_mascot")
    val animatedBounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_bounce"
    )
    val blinkCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mascot_blink"
    )
    val isBlinking = !reducedMotion && blinkCycle > 0.95f

    val bounceOffset = if (reducedMotion) 0f else animatedBounce
    val scaleAnim by animateFloatAsState(
        targetValue = if ((isHappy || mood == MascotMood.CELEBRATING) && !reducedMotion) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "mascot_scale"
    )

    Box(
        modifier = modifier
            .offset(y = bounceOffset.dp)
            .scale(scaleAnim)
            .size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = if (showBook) h * 0.44f else h * 0.50f
            val outerR = minOf(w, h) * (if (showBook) 0.43f else 0.45f)
            val innerR = outerR * 0.53f

            if (!isMonochrome) {
                // Soft Warm Halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x44FFD166), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = outerR * 1.22f
                    ),
                    radius = outerR * 1.22f,
                    center = Offset(cx, cy)
                )
            }

            // Balanced 5-Pointed Learning Star Path
            val starPath = Path().apply {
                var angle = (-PI / 2.0).toFloat()
                val step = (PI / 5.0).toFloat()
                moveTo(cx + outerR * cos(angle), cy + outerR * sin(angle))
                for (i in 0 until 5) {
                    angle += step
                    lineTo(cx + innerR * cos(angle), cy + innerR * sin(angle))
                    angle += step
                    lineTo(cx + outerR * cos(angle), cy + outerR * sin(angle))
                }
                close()
            }

            if (isMonochrome) {
                drawPath(
                    path = starPath,
                    color = monochromeTint,
                    style = Stroke(width = 3.8f, join = StrokeJoin.Round, cap = StrokeCap.Round)
                )
            } else {
                // Subtle warm lower edge
                val shadowStarPath = Path().apply {
                    addPath(starPath, Offset(0f, outerR * 0.06f))
                }
                drawPath(
                    path = shadowStarPath,
                    color = Color(0xFFE09100),
                    style = Fill
                )

                // Smooth Golden Fill
                drawPath(
                    path = starPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF8D6),
                            Color(0xFFFFD166),
                            Color(0xFFF59E0B)
                        ),
                        center = Offset(cx - outerR * 0.14f, cy - outerR * 0.18f),
                        radius = outerR * 1.12f
                    ),
                    style = Fill
                )
                drawPath(
                    path = starPath,
                    color = Color(0xFFE09100),
                    style = Stroke(width = 3.2f, join = StrokeJoin.Round, cap = StrokeCap.Round)
                )
            }

            // Proportional, Calm, Friendly Eyes (Balanced — never oversized or distorted)
            val eyeOffsetX = outerR * 0.21f
            val eyeY = cy - outerR * 0.05f
            val eyeR = outerR * 0.072f
            val featureColor = if (isMonochrome) monochromeTint else DeepRoyalBlue

            if (isBlinking) {
                drawLine(
                    color = featureColor,
                    start = Offset(cx - eyeOffsetX - eyeR, eyeY),
                    end = Offset(cx - eyeOffsetX + eyeR, eyeY),
                    strokeWidth = 2.8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = featureColor,
                    start = Offset(cx + eyeOffsetX - eyeR, eyeY),
                    end = Offset(cx + eyeOffsetX + eyeR, eyeY),
                    strokeWidth = 2.8f,
                    cap = StrokeCap.Round
                )
            } else {
                drawCircle(
                    color = featureColor,
                    radius = eyeR,
                    center = Offset(cx - eyeOffsetX, eyeY)
                )
                drawCircle(
                    color = featureColor,
                    radius = eyeR,
                    center = Offset(cx + eyeOffsetX, eyeY)
                )
                if (!isMonochrome) {
                    drawCircle(
                        color = Color.White,
                        radius = eyeR * 0.34f,
                        center = Offset(cx - eyeOffsetX - eyeR * 0.25f, eyeY - eyeR * 0.25f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = eyeR * 0.34f,
                        center = Offset(cx + eyeOffsetX - eyeR * 0.25f, eyeY - eyeR * 0.25f)
                    )
                }
            }

            // Gentle Warm Smile
            val smileWidth = outerR * 0.28f
            val smileHeight = outerR * 0.16f
            drawArc(
                color = featureColor,
                startAngle = 18f,
                sweepAngle = 144f,
                useCenter = false,
                topLeft = Offset(cx - smileWidth / 2f, cy + outerR * 0.03f),
                size = Size(smileWidth, smileHeight),
                style = Stroke(width = 3.0f, cap = StrokeCap.Round)
            )

            // Open Book + Subtle Play Symbol (▶)
            if (showBook) {
                val bookTop = cy + outerR * 0.28f
                val bookBottom = h * 0.93f
                val bookHalfW = outerR * 0.68f
                val spineDip = outerR * 0.11f

                val bookCoverPath = Path().apply {
                    moveTo(cx - bookHalfW, bookTop)
                    lineTo(cx, bookTop + spineDip)
                    lineTo(cx + bookHalfW, bookTop)
                    lineTo(cx + bookHalfW, bookBottom)
                    lineTo(cx, bookBottom + spineDip)
                    lineTo(cx - bookHalfW, bookBottom)
                    close()
                }

                if (isMonochrome) {
                    drawPath(
                        path = bookCoverPath,
                        color = monochromeTint,
                        style = Stroke(width = 3.2f, join = StrokeJoin.Round)
                    )
                    drawLine(
                        color = monochromeTint,
                        start = Offset(cx, bookTop + spineDip),
                        end = Offset(cx, bookBottom + spineDip),
                        strokeWidth = 2.8f
                    )
                } else {
                    // White Pages peeking at top
                    val pagesPath = Path().apply {
                        moveTo(cx - bookHalfW * 0.94f, bookTop - 3.5f)
                        lineTo(cx, bookTop + spineDip - 2.5f)
                        lineTo(cx + bookHalfW * 0.94f, bookTop - 3.5f)
                        lineTo(cx + bookHalfW * 0.94f, bookBottom - 2.5f)
                        lineTo(cx, bookBottom + spineDip - 2.5f)
                        lineTo(cx - bookHalfW * 0.94f, bookBottom - 2.5f)
                        close()
                    }
                    drawPath(pagesPath, Color.White)

                    // Royal Blue Open Book Cover
                    drawPath(
                        path = bookCoverPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(PlayBlue, DeepRoyalBlue),
                            startY = bookTop,
                            endY = bookBottom
                        )
                    )
                    drawPath(
                        path = bookCoverPath,
                        color = Color.White.copy(alpha = 0.88f),
                        style = Stroke(width = 2.2f, join = StrokeJoin.Round)
                    )

                    // Center Spine
                    drawLine(
                        color = DeepRoyalBlue,
                        start = Offset(cx, bookTop + spineDip),
                        end = Offset(cx, bookBottom + spineDip),
                        strokeWidth = 3.0f
                    )
                }

                // Subtle Play Symbol (▶) on Right Cover of Open Book
                val playCenterX = cx + bookHalfW * 0.48f
                val playCenterY = (bookTop + bookBottom) * 0.53f
                val playRadius = outerR * 0.14f
                val playTrianglePath = Path().apply {
                    moveTo(playCenterX - playRadius * 0.55f, playCenterY - playRadius * 0.72f)
                    lineTo(playCenterX + playRadius * 0.80f, playCenterY)
                    lineTo(playCenterX - playRadius * 0.55f, playCenterY + playRadius * 0.72f)
                    close()
                }
                drawPath(
                    path = playTrianglePath,
                    color = if (isMonochrome) monochromeTint else Color.White,
                    style = Fill
                )
            }
        }
    }
}

/**
 * PlayKids Speech & Voice Instruction Banner with Speaker Button and Mascot (Screens 5-8)
 */
@Composable
fun MascotSpeechPrompt(
    text: String,
    onReplayAudio: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(5.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = if (isHighlighted) SoftSunshine else Color.White.copy(alpha = 0.97f),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.2.dp,
            color = if (isHighlighted) StarYellow else PlayBlue.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmilingStarMascot(size = 46, isHappy = isHighlighted, showBook = false)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepRoyalBlue,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            PlayKidsIconButton(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "নির্দেশনা পুনরায় শোনো",
                onClick = onReplayAudio,
                containerColor = PlayBlue,
                size = 48.dp,
                testTag = "speaker_replay_button"
            )
        }
    }
}

@Composable
fun LearningModeSwitcherBar(
    currentMode: LearningMode,
    onSelectMode: (LearningMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.90f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PlayBlue.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LearningMode.entries.forEach { mode ->
                val isSelected = mode == currentMode
                val activeColor = if (mode == LearningMode.CORE_CURRICULUM) PlayBlue else CreativePurple
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .testTag("mode_switch_${mode.id}")
                        .clickable { onSelectMode(mode) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) activeColor else Color.Transparent,
                    shadowElevation = if (isSelected) 3.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                        Text(
                            text = mode.titleBn,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else DeepRoyalBlue,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * PlayKids V8.0 Game Screen Top Bar (Matches Screens 5, 6, 7, 8 of UI Collage):
 * Circular Back Button | "অধ্যায় X : স্তর Y" Dark-Blue Pill + 3-Star Progress Bar | Star Badge
 */
@Composable
fun GameTopBar(
    title: String,
    serialNumber: String,
    currentLevel: Int,
    starsCount: Int,
    onBackClick: () -> Unit,
    themeColor: Color,
    modifier: Modifier = Modifier,
    learningMode: LearningMode = LearningMode.CORE_CURRICULUM,
    onOpenLevelMap: (() -> Unit)? = null
) {
    val chapterNum = ((currentLevel - 1) / 5 + 1).coerceIn(1, 5)
    val levelInChapter = ((currentLevel - 1) % 5 + 1).coerceIn(1, 5)
    val chapterBn = PlayKidsTokens.toBnDigits(chapterNum)
    val levelBn = PlayKidsTokens.toBnDigits(currentLevel)
    val progressFraction = (levelInChapter.toFloat() / 5f).coerceIn(0.20f, 1.0f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayKidsIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "হোমে ফিরে যাও",
                onClick = onBackClick,
                containerColor = PlayBlue,
                size = 48.dp,
                testTag = "game_back_button"
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Center Pill: "অধ্যায় ১ : স্তর ৪" + Game Name
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .then(
                        if (onOpenLevelMap != null) Modifier.clickable { onOpenLevelMap() } else Modifier
                    )
                    .testTag("game_level_pill"),
                shape = RoundedCornerShape(22.dp),
                color = DeepRoyalBlue,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.90f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "অধ্যায় $chapterBn : স্তর $levelBn",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = title.substringAfter("• ").trim().ifEmpty { title },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StarYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Star Counter Pill + Level Map Quick Trigger
            Surface(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (onOpenLevelMap != null) Modifier.clickable { onOpenLevelMap() } else Modifier
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, StarYellow)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "অর্জিত তারা",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = PlayKidsTokens.toBnDigits(starsCount),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepRoyalBlue
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3-Star Chapter Progress Bar matching Screens 5, 6, 7, 8 of UI Collage
        Row(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                for (s in 1..3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (s <= 2 || levelInChapter >= 3) Color(0xFFFFB300) else Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFE066), Color(0xFFF59E0B))
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun GameplayControlBar(
    currentLevel: Int,
    maxLevel: Int,
    hasPreviousLevel: Boolean,
    hasNextUnlockedLevel: Boolean,
    onPreviousLevel: () -> Unit,
    onRestartLevel: () -> Unit,
    onPauseClick: () -> Unit,
    onNextLevel: () -> Unit,
    themeColor: Color = PlayBlue,
    onOpenLevelMap: () -> Unit = {},
    onLockedNextAttempt: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    GameplayControlBar(
        currentLevel = currentLevel,
        maxUnlockedLevel = if (hasNextUnlockedLevel) currentLevel + 1 else currentLevel,
        maxTotalLevels = maxLevel,
        onPreviousLevel = onPreviousLevel,
        onRestartLevel = onRestartLevel,
        onPauseGame = onPauseClick,
        onOpenLevelMap = onOpenLevelMap,
        onNextLevel = onNextLevel,
        onLockedNextAttempt = onLockedNextAttempt,
        modifier = modifier
    )
}

@Composable
fun GameplayControlBar(
    currentLevel: Int,
    maxUnlockedLevel: Int,
    maxTotalLevels: Int,
    onPreviousLevel: () -> Unit,
    onRestartLevel: () -> Unit,
    onPauseGame: () -> Unit,
    onOpenLevelMap: () -> Unit,
    onNextLevel: () -> Unit,
    onLockedNextAttempt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canGoPrev = currentLevel > 1
    val canGoNext = currentLevel < maxUnlockedLevel && currentLevel < maxTotalLevels

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Previous Level Button
        PlayKidsSecondaryButton(
            text = "আগেরটি",
            onClick = onPreviousLevel,
            enabled = canGoPrev,
            leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
            height = 40.dp,
            modifier = Modifier.weight(1f),
            testTag = "nav_prev_level_btn"
        )

        // 2. Restart Level Button
        PlayKidsSecondaryButton(
            text = "রিস্টার্ট",
            onClick = onRestartLevel,
            leadingIcon = Icons.Default.Refresh,
            height = 40.dp,
            modifier = Modifier.weight(1f),
            testTag = "nav_restart_level_btn"
        )

        // 3. Pause / Menu Button
        PlayKidsSecondaryButton(
            text = "বিরতি",
            onClick = onPauseGame,
            containerColor = StarYellow,
            contentColor = DeepRoyalBlue,
            borderColor = Color(0xFFF59E0B),
            leadingIcon = Icons.Default.Pause,
            height = 40.dp,
            modifier = Modifier.weight(0.95f),
            testTag = "nav_pause_btn"
        )

        // 4. Next Level Button (Active if next level unlocked, otherwise triggers locked message)
        PlayKidsSecondaryButton(
            text = "পরেরটি",
            onClick = {
                if (canGoNext) {
                    onNextLevel()
                } else {
                    onLockedNextAttempt()
                }
            },
            containerColor = if (canGoNext) LearningMint else Color.White.copy(alpha = 0.72f),
            contentColor = if (canGoNext) Color.White else SecondaryText,
            borderColor = if (canGoNext) Color.White else BorderSoft,
            trailingIcon = if (canGoNext) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Lock,
            height = 40.dp,
            modifier = Modifier.weight(1.05f),
            testTag = "nav_next_level_btn"
        )
    }
}

@Composable
fun GamePauseDialog(
    levelNumber: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onOpenLevelMap: () -> Unit,
    onGoHome: () -> Unit,
    gameTitle: String = BrandConfig.APPROVED_BRAND_NAME
) {
    GamePauseDialog(
        gameTitle = gameTitle,
        currentLevel = levelNumber,
        onResume = onResume,
        onRestart = onRestart,
        onOpenLevelMap = onOpenLevelMap,
        onGoHome = onGoHome
    )
}

@Composable
fun GamePauseDialog(
    gameTitle: String,
    currentLevel: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onOpenLevelMap: () -> Unit,
    onGoHome: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CardSurface,
            shadowElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(3.dp, PlayBlue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SmilingStarMascot(size = 72, showBook = true)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "খেলা বিরতিতে আছে",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepRoyalBlue
                    )
                )
                Text(
                    text = "$gameTitle • স্তর ${PlayKidsTokens.toBnDigits(currentLevel)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                PlayKidsPrimaryButton(
                    text = "খেলা চালিয়ে যাও",
                    onClick = onResume,
                    gradient = PlayKidsTokens.GreenPrimaryGradient,
                    leadingIcon = Icons.Default.PlayArrow,
                    modifier = Modifier.fillMaxWidth(),
                    height = 54.dp,
                    testTag = "pause_resume_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlayKidsSecondaryButton(
                    text = "এই স্তর আবার শুরু করো",
                    onClick = onRestart,
                    leadingIcon = Icons.Default.Refresh,
                    borderColor = PlayBlue,
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    testTag = "pause_restart_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlayKidsSecondaryButton(
                    text = "লেভেল ম্যাপ দেখো",
                    onClick = onOpenLevelMap,
                    leadingIcon = Icons.Default.GridView,
                    borderColor = CreativePurple,
                    contentColor = CreativePurple,
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    testTag = "pause_level_map_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PlayKidsSecondaryButton(
                    text = "হোম স্ক্রিনে ফিরে যাও",
                    onClick = onGoHome,
                    leadingIcon = Icons.Default.Home,
                    borderColor = JoyCoral,
                    contentColor = JoyCoral,
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    testTag = "pause_home_btn"
                )
            }
        }
    }
}

