package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Screen 1 — PlayKids Splash Screen (`১. স্প্ল্যাশ স্ক্রিন`):
 * - Silent, calm visual introduction (no startup jingle or unexpected auto-voice)
 * - Shows the PlayKids brand logo, Learning Star with open book, glossy progress bar, and "লোড হচ্ছে..."
 */
@Composable
fun PlayoraSplashScreen(
    reducedMotion: Boolean = false,
    onSplashFinished: () -> Unit
) {
    var progressValue by remember { mutableFloatStateOf(0.20f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        progressValue = 1.0f
        delay(850L)
        onSplashFinished()
    }

    PlayoraAnimatedSceneBackground(
        worldTheme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = reducedMotion,
        modifier = Modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .clickable { onSplashFinished() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SmilingStarMascot(
                    size = 128,
                    isHappy = true,
                    showBook = true,
                    reducedMotion = reducedMotion
                )
                Spacer(modifier = Modifier.height(10.dp))
                PlayKidsBrandLogo(
                    variant = PlayKidsLogoVariant.PRIMARY_HORIZONTAL,
                    showTaglineRibbon = true
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.76f)
                        .height(22.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(3.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF2563EB), PlayBlue, Color(0xFF38BDF8))
                                    )
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "লোড হচ্ছে...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

/**
 * Screen 2 — PlayKids Welcome Screen (`২. স্বাগতম স্ক্রিন`):
 * - No automatic voice playback on launch (silent until user interacts)
 * - Large white rounded speech bubble with tail pointing to waving Learning Star:
 *   "হ্যালো!" / "চলো, আজকে আমরা নতুন কিছু শিখি।"
 * - Glossy purple pill primary button `[ ▶ শুরু করি ]` + 3 indicator dots
 */
@Composable
fun PlayoraWelcomeScreen(
    audio: BanglaAudioEngine,
    reducedMotion: Boolean = false,
    onStartExploring: () -> Unit
) {
    PlayoraAnimatedSceneBackground(
        worldTheme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = reducedMotion
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Speech Bubble + Tail (Matching Screen 2 of V8.0 Reference Collage)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .shadow(10.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "হ্যালো!",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepRoyalBlue,
                                fontSize = 34.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "চলো, আজকে আমরা\nনতুন কিছু শিখি।",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepRoyalBlue,
                                fontSize = 21.sp,
                                lineHeight = 29.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Speech bubble pointer triangle
                Canvas(modifier = Modifier.size(width = 32.dp, height = 18.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width * 0.55f, size.height)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }
            }

            // Center Waving Learning Star Mascot
            SmilingStarMascot(
                size = 156,
                isHappy = true,
                showBook = false,
                mood = MascotMood.WELCOME_WAVE,
                reducedMotion = reducedMotion
            )

            // Glossy Purple Pill Button "[ ▶ শুরু করি ]" + Indicator Dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlayKidsPrimaryButton(
                    text = "শুরু করি",
                    onClick = {
                        audio.playButtonTap()
                        audio.speak("হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।")
                        onStartExploring()
                    },
                    modifier = Modifier.fillMaxWidth(0.86f),
                    gradient = PlayKidsTokens.PurplePillGradient,
                    textColor = Color.White,
                    leadingIcon = Icons.Default.PlayArrow,
                    height = 62.dp,
                    testTag = "welcome_explore_button"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Page Indicator Dots + Circular Play Button for accessibility/test compatibility
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .testTag("welcome_play_circle_button")
                        .clickable {
                            audio.playButtonTap()
                            audio.speak("হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।")
                            onStartExploring()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                }
            }
        }
    }
}

/**
 * Screen 10 — PlayKids "আমার অগ্রগতি" (My Progress) Screen:
 * Matches Screen 10 of V8.0 Reference Collage:
 * - Purple header bar: "আমার অগ্রগতি"
 * - White rounded container card showing all 4 games with custom icons, green progress bars,
 *   completed fraction (`X/25`) and percentage (`Y%`)
 * - Bottom glossy red/gold ribbon card: "মোট অগ্রগতি" (`X/100`) + Total Stars
 */
@Composable
fun MyProgressScreen(
    allProgress: Map<GameType, GameProgressData>,
    learningMode: LearningMode,
    audio: BanglaAudioEngine,
    onSelectGame: (GameType) -> Unit,
    onOpenLevelMap: (GameType) -> Unit,
    onNavigateTab: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val totalStars = allProgress.values.sumOf { it.starsEarned }
    val totalCompletedLevels = GameType.entries.sumOf { gType ->
        val p = allProgress[gType] ?: GameProgressData(gType)
        val completed = (p.coreCompletedLevels + p.completedLevels).size
        val unlocked = maxOf(p.unlockedLevel, p.extendedUnlockedLevel).coerceIn(1, 25)
        maxOf(completed, unlocked - 1).coerceIn(0, 25)
    }

    PlayoraAnimatedSceneBackground(worldTheme = PlayoraWorldTheme.DEFAULT_SKY_HILLS) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Purple Header Bar ("আমার অগ্রগতি")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
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
                    gradient = PlayKidsTokens.PurpleHeaderGradient,
                    testTag = "progress_back_button"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .background(PlayKidsTokens.PurpleHeaderGradient)
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "আমার অগ্রগতি",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )
                    }
                }

                SmilingStarMascot(size = 46, showBook = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // White Rounded Card containing the 4 Game Progress Rows (Matching Screen 10)
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            GameType.entries.forEachIndexed { index, gameType ->
                                val gProgress = allProgress[gameType] ?: GameProgressData(gameType)
                                val unlockedLvl = maxOf(gProgress.unlockedLevel, gProgress.extendedUnlockedLevel).coerceIn(1, 25)
                                val completedCount = maxOf(
                                    (gProgress.coreCompletedLevels + gProgress.completedLevels).size,
                                    unlockedLvl - 1
                                ).coerceIn(1, 25)
                                val pct = ((completedCount * 100) / 25).coerceIn(4, 100)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .testTag("progress_game_card_${gameType.id}")
                                        .clickable {
                                            audio.playCardSelectChime()
                                            onOpenLevelMap(gameType)
                                        }
                                        .padding(vertical = 4.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom Game Icon Badge matching Screen 10
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(gameType.lightBg)
                                            .border(1.5.dp, gameType.accentBorder, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when (gameType) {
                                            GameType.EMOJI_MATCH -> Text("🍎", fontSize = 30.sp)
                                            GameType.SHAPE_MATCH -> ShapeGraphic(KidShape.TRIANGLE, Color(0xFFF59E0B), sizeDp = 34.dp)
                                            GameType.COLOR_SHAPE_CHALLENGE -> Text("🎨", fontSize = 28.sp)
                                            GameType.COLOR_PAPER_MATCH -> {
                                                Canvas(modifier = Modifier.size(36.dp)) {
                                                    val cols = listOf(KidRed, StarYellow, LearningMint, PlayBlue)
                                                    val angs = listOf(-16f, -5f, 6f, 16f)
                                                    cols.forEachIndexed { idx, col ->
                                                        rotate(angs[idx]) {
                                                            drawRoundRect(
                                                                color = col,
                                                                topLeft = Offset(size.width * 0.25f, size.height * 0.15f),
                                                                size = Size(size.width * 0.48f, size.height * 0.68f),
                                                                cornerRadius = CornerRadius(4f, 4f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = gameType.titleBn,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = DeepRoyalBlue,
                                                    fontSize = 18.sp
                                                )
                                            )
                                            Text(
                                                text = "$completedCount/25",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontFamily = NunitoFontFamily,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = DeepRoyalBlue,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(14.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFE2E8F0))
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(pct / 100f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(
                                                            Brush.horizontalGradient(
                                                                listOf(Color(0xFF4ADE80), Color(0xFF16A34A))
                                                            )
                                                        )
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "$pct%",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontFamily = NunitoFontFamily,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = DeepRoyalBlue
                                                )
                                            )
                                        }
                                    }
                                }

                                if (index < GameType.entries.size - 1) {
                                    HorizontalDivider(color = BorderSoft.copy(alpha = 0.55f))
                                }
                            }
                        }
                    }
                }

                // Bottom Red & Gold Ribbon Card ("মোট অগ্রগতি X/100" + Total Stars)
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PlayKidsTokens.CrimsonTotalRibbonGradient)
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Gold Rosette Star Medal
                            Surface(
                                shape = CircleShape,
                                color = StarYellow,
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White),
                                modifier = Modifier.size(54.dp),
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "মোট অগ্রগতি",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "$totalCompletedLevels/100",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = NunitoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 28.sp
                                    )
                                )
                                Text(
                                    text = "Total Stars : $totalStars • মোট তারা: $totalStars",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = NunitoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = StarYellow
                                    )
                                )
                            }
                        }
                    }
                }
            }

            PlayoraBottomNavBar(
                currentScreen = Screen.MY_PROGRESS,
                onSelectTab = { target ->
                    audio.playButtonTap()
                    onNavigateTab(target)
                }
            )
        }
    }
}
