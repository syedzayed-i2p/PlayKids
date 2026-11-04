package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BanglaAudioEngine
import com.example.audio.VoiceScriptRegistry
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    allProgress: Map<GameType, GameProgressData>,
    learningMode: LearningMode,
    lastPlayedGame: GameType = GameType.SHAPE_MATCH,
    audio: BanglaAudioEngine,
    onSelectMode: (LearningMode) -> Unit,
    onSelectGame: (GameType) -> Unit,
    onContinueLearning: (GameType, Int) -> Unit = { g, _ -> onSelectGame(g) },
    onOpenLevelMap: (GameType) -> Unit,
    onOpenMyProgress: () -> Unit = {},
    onOpenPhysicalPractice: () -> Unit,
    onOpenParentDashboard: () -> Unit,
    onToggleVoiceMute: () -> Unit,
    onUpdateAudioSettings: (AudioSettingsState) -> Unit = {}
) {
    val welcomeText = VoiceScriptRegistry.SCRIPTS["welcome"]?.banglaScript
        ?: "হ্যালো! চলো, আজকে আমরা নতুন কিছু শিখি।"

    val audioSettings by audio.audioSettings.collectAsState()
    val bundledVoiceAssetsPresent by audio.bundledVoiceAssetsPresent.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    val totalStars = allProgress.values.sumOf { it.starsEarned }
    val physicalCompletedCount = allProgress.values.count { it.physicalCompleted }
    val totalCompletedLevels = allProgress.values.sumOf {
        (it.coreCompletedLevels + it.completedLevels).size
    }

    val resumeProgress = allProgress[lastPlayedGame] ?: GameProgressData(lastPlayedGame)
    val resumeLevel = if (learningMode == LearningMode.CORE_CURRICULUM) {
        resumeProgress.unlockedLevel.coerceIn(1, 5)
    } else {
        maxOf(resumeProgress.unlockedLevel, resumeProgress.extendedUnlockedLevel).coerceIn(1, 25)
    }
    val resumeChapter = ((resumeLevel - 1) / 5 + 1).coerceIn(1, 5)

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                PlayoraBottomNavBar(
                    currentScreen = Screen.HOME,
                    onSelectTab = { target ->
                        audio.playButtonTap()
                        when (target) {
                            Screen.HOME -> Unit
                            Screen.MY_PROGRESS -> onOpenMyProgress()
                            Screen.PHYSICAL_PRACTICE -> onOpenPhysicalPractice()
                            Screen.PARENT_DASHBOARD -> onOpenParentDashboard()
                            else -> Unit
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Header Bar (Matching Screen 3 of V8.0 Reference Collage):
                // Learner Avatar + "স্বাগতম, ছোট শিক্ষার্থী!" | Voice Toggle + Settings Gear Button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Learner Avatar Badge (also carries home_top_button testTag)
                            Surface(
                                modifier = Modifier
                                    .size(50.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .testTag("home_top_button")
                                    .clickable {
                                        audio.playButtonTap()
                                        audio.speak(welcomeText)
                                    },
                                shape = CircleShape,
                                color = SoftSky,
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "👦", fontSize = 26.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "স্বাগতম,",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "ছোট শিক্ষার্থী!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 17.sp
                                    )
                                )
                            }
                        }

                        // Right Controls: Voice Toggle + Settings Gear Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlayKidsIconButton(
                                icon = if (audioSettings.voiceEnabled)
                                    Icons.AutoMirrored.Filled.VolumeUp
                                else
                                    Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "কণ্ঠস্বর চালু বা বন্ধ",
                                onClick = {
                                    audio.playButtonTap()
                                    onToggleVoiceMute()
                                },
                                size = 48.dp,
                                gradient = if (audioSettings.voiceEnabled)
                                    PlayKidsTokens.BlueButtonGradient
                                else
                                    PlayKidsTokens.SlateDisabledGradient,
                                testTag = "home_voice_toggle"
                            )

                            PlayKidsIconButton(
                                icon = Icons.Default.Settings,
                                contentDescription = "সেটিংস",
                                onClick = {
                                    audio.playButtonTap()
                                    showSettingsDialog = true
                                },
                                size = 48.dp,
                                gradient = PlayKidsTokens.BlueButtonGradient,
                                testTag = "home_settings_button"
                            )
                        }
                    }
                }

                // 2. Center PlayKids Logo + Mascot Prompt + Star Counter Pill
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            PlayKidsBrandLogo(
                                variant = PlayKidsLogoVariant.PRIMARY_HORIZONTAL,
                                showTaglineRibbon = true
                            )

                            // Floating Total Stars Pill on the right
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        audio.playButtonTap()
                                        onOpenMyProgress()
                                    },
                                shape = RoundedCornerShape(18.dp),
                                color = StarYellow,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "মোট তারা",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$totalStars",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = NunitoFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepRoyalBlue
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        MascotSpeechPrompt(
                            text = welcomeText,
                            onReplayAudio = { audio.speak(welcomeText) }
                        )
                    }
                }

                // 3. Learning Mode Switcher (Core vs Extended 100+ Levels)
                item {
                    LearningModeSwitcherBar(
                        currentMode = learningMode,
                        onSelectMode = { mode ->
                            audio.playButtonTap()
                            onSelectMode(mode)
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 4. 2x2 Grid of 4 Large Glossy PlayKids Game Cards (Matching Screen 3 Reference)
                item {
                    val gamePairs = GameType.entries.chunked(2)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        gamePairs.forEach { rowGames ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowGames.forEach { gameType ->
                                    val gProgress = allProgress[gameType] ?: GameProgressData(gameType)
                                    PlayoraHomeGameCard(
                                        gameType = gameType,
                                        progress = gProgress,
                                        learningMode = learningMode,
                                        onPlayClick = {
                                            audio.playCardSelectChime()
                                            onSelectGame(gameType)
                                        },
                                        onOpenMapClick = {
                                            audio.playButtonTap()
                                            onOpenLevelMap(gameType)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 5. "চালিয়ে যাও" White Rounded Banner with Green Play Button (Matching Screen 3)
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .shadow(8.dp, RoundedCornerShape(26.dp))
                            .clip(RoundedCornerShape(26.dp))
                            .testTag("home_continue_learning_button")
                            .clickable {
                                audio.playCardSelectChime()
                                onContinueLearning(lastPlayedGame, resumeLevel)
                            },
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SmilingStarMascot(
                                size = 48,
                                showBook = true,
                                reducedMotion = audioSettings.reducedMotion
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "চালিয়ে যাও",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepRoyalBlue,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "${lastPlayedGame.titleBn} - অধ্যায় ${PlayKidsTokens.toBnDigits(resumeChapter)}, স্তর ${PlayKidsTokens.toBnDigits(resumeLevel)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SecondaryText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Glossy Green Circular Play Button
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PlayKidsTokens.GreenSuccessGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "চালিয়ে যাও",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Quick Access Pills for Physical Practice & Parent Dashboard
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .testTag("home_physical_practice_button")
                                .clickable {
                                    audio.playButtonTap()
                                    onOpenPhysicalPractice()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.92f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, LearningMint)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🎯 বাস্তব অনুশীলন ($physicalCompletedCount/৪)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = KidGreenDark
                                    )
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .testTag("home_parent_dashboard_button")
                                .clickable {
                                    audio.playButtonTap()
                                    onOpenParentDashboard()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.92f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, CreativePurple)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CreativePurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "অভিভাবক প্যানেল",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CreativePurple
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Screen 12 — PlayKids Settings Modal Dialog (`১২. সেটিংস`)
        if (showSettingsDialog) {
            PlayKidsSettingsDialog(
                audioSettings = audioSettings,
                bundledVoiceAssetsPresent = bundledVoiceAssetsPresent,
                onUpdateAudioSettings = { updated ->
                    audio.updateAudioSettings(updated)
                    onUpdateAudioSettings(updated)
                },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

@Composable
private fun PlayoraHomeGameCard(
    gameType: GameType,
    progress: GameProgressData,
    learningMode: LearningMode,
    onPlayClick: () -> Unit,
    onOpenMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeLevel = if (learningMode == LearningMode.CORE_CURRICULUM) {
        progress.unlockedLevel.coerceIn(1, 5)
    } else {
        maxOf(progress.unlockedLevel, progress.extendedUnlockedLevel).coerceIn(1, 25)
    }
    val maxLevelCount = if (learningMode == LearningMode.CORE_CURRICULUM) 5 else 25
    val progressFraction = (activeLevel.toFloat() / maxLevelCount.toFloat()).coerceIn(0.12f, 1f)

    // Matching Screen 3 of V8.0 Reference Collage:
    // 1. ছবি মিলাও -> Warm Gold-Yellow card with 3D glossy red apple
    // 2. আকৃতি মিলাও -> Vibrant Green card with Red Triangle + Blue Circle
    // 3. রং ও আকৃতি -> Soft Lavender-Purple card with Artist Palette + Shapes
    // 4. রঙিন কাগজ মিলাও -> Vibrant Pink-Rose card with Fanned 4 Coloured Craft Papers
    val cardGradient = when (gameType) {
        GameType.EMOJI_MATCH -> PlayKidsTokens.EmojiGoldCardGradient
        GameType.SHAPE_MATCH -> PlayKidsTokens.ShapeGreenCardGradient
        GameType.COLOR_SHAPE_CHALLENGE -> PlayKidsTokens.ColorShapePurpleCardGradient
        GameType.COLOR_PAPER_MATCH -> PlayKidsTokens.ColorPaperRoseCardGradient
    }

    val textColor = DeepRoyalBlue

    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .testTag("home_game_card_${gameType.id}")
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(cardGradient)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: Stars count + Map button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.88f)
                ) {
                    Text(
                        text = "⭐ ${progress.starsEarned}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepRoyalBlue
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .testTag("level_map_btn_${gameType.id}")
                        .clickable { onOpenMapClick() },
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "স্তর ম্যাপ",
                            tint = DeepRoyalBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ম্যাপ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepRoyalBlue,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Center Custom 3D Game World Illustration Box (Matching Screen 3)
            Box(
                modifier = Modifier
                    .height(74.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (gameType) {
                    GameType.EMOJI_MATCH -> {
                        // Glossy 3D Red Apple with Green Leaf (Matching Screen 3)
                        Canvas(modifier = Modifier.size(66.dp)) {
                            val cx = size.width * 0.5f
                            val cy = size.height * 0.56f
                            val r = size.width * 0.36f
                            // Soft shadow
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.16f),
                                radius = r,
                                center = Offset(cx, cy + 5f)
                            )
                            // Apple body
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF6B6B), Color(0xFFE11D48), Color(0xFF9F1239)),
                                    center = Offset(cx - r * 0.3f, cy - r * 0.3f),
                                    radius = r * 1.35f
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                            // Glossy highlight
                            drawOval(
                                color = Color.White.copy(alpha = 0.55f),
                                topLeft = Offset(cx - r * 0.55f, cy - r * 0.6f),
                                size = Size(r * 0.45f, r * 0.3f)
                            )
                            // Stem
                            drawRoundRect(
                                color = Color(0xFF78350F),
                                topLeft = Offset(cx - 3f, cy - r - 10f),
                                size = Size(6f, 14f),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                            // Green leaf
                            val leafPath = Path().apply {
                                moveTo(cx + 2f, cy - r - 4f)
                                quadraticTo(cx + 18f, cy - r - 16f, cx + 22f, cy - r - 2f)
                                quadraticTo(cx + 12f, cy - r + 4f, cx + 2f, cy - r - 4f)
                                close()
                            }
                            drawPath(leafPath, color = Color(0xFF22C55E))
                        }
                    }

                    GameType.SHAPE_MATCH -> {
                        // 3D Red Triangle + Blue Circle overlapping (Matching Screen 3)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            ShapeGraphic(
                                shape = KidShape.TRIANGLE,
                                color = KidRed,
                                sizeDp = 50.dp
                            )
                            ShapeGraphic(
                                shape = KidShape.CIRCLE,
                                color = PlayBlue,
                                sizeDp = 46.dp
                            )
                        }
                    }

                    GameType.COLOR_SHAPE_CHALLENGE -> {
                        // Golden Artist Palette with colourful paint dots (Matching Screen 3)
                        Canvas(modifier = Modifier.size(68.dp)) {
                            val w = size.width
                            val h = size.height
                            drawOval(
                                color = Color.Black.copy(alpha = 0.14f),
                                topLeft = Offset(w * 0.1f, h * 0.18f),
                                size = Size(w * 0.8f, h * 0.7f)
                            )
                            drawOval(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFFDE68A), Color(0xFFF59E0B))
                                ),
                                topLeft = Offset(w * 0.1f, h * 0.14f),
                                size = Size(w * 0.8f, h * 0.7f)
                            )
                            // Thumb hole
                            drawCircle(
                                color = Color.White,
                                radius = w * 0.09f,
                                center = Offset(w * 0.66f, h * 0.56f)
                            )
                            // Paint dots (Red, Green, Blue, Purple)
                            drawCircle(Color(0xFFEF4444), radius = w * 0.085f, center = Offset(w * 0.30f, h * 0.36f))
                            drawCircle(Color(0xFF22C55E), radius = w * 0.085f, center = Offset(w * 0.46f, h * 0.28f))
                            drawCircle(Color(0xFF3B82F6), radius = w * 0.085f, center = Offset(w * 0.64f, h * 0.34f))
                            drawCircle(Color(0xFFA855F7), radius = w * 0.085f, center = Offset(w * 0.34f, h * 0.56f))
                        }
                    }

                    GameType.COLOR_PAPER_MATCH -> {
                        // Fanned 4 Colored Craft Papers (Purple, Red, Yellow, Green)
                        Canvas(modifier = Modifier.size(width = 110.dp, height = 66.dp)) {
                            val paperColors = listOf(
                                Color(0xFF9333EA),
                                Color(0xFFEF4444),
                                Color(0xFFFACC15),
                                Color(0xFF22C55E)
                            )
                            val angles = listOf(-18f, -6f, 6f, 18f)
                            paperColors.forEachIndexed { idx, col ->
                                val cx = size.width * (0.28f + idx * 0.15f)
                                val cy = size.height * 0.55f
                                rotate(angles[idx], pivot = Offset(cx, cy)) {
                                    drawRoundRect(
                                        color = Color.White,
                                        topLeft = Offset(cx - 20f, cy - 28f),
                                        size = Size(40f, 54f),
                                        cornerRadius = CornerRadius(8f, 8f)
                                    )
                                    drawRoundRect(
                                        color = col,
                                        topLeft = Offset(cx - 18f, cy - 26f),
                                        size = Size(36f, 50f),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Bengali Game Title (Matching Screen 3: "ছবি মিলাও", "আকৃতি মিলাও", "রং ও আকৃতি", "রঙিন কাগজ মিলাও")
            Text(
                text = gameType.titleBn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Secondary English Subtitle in parentheses (e.g. "(Emoji Matching)")
            Text(
                text = "(${gameType.titleEn})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NunitoFontFamily,
                    color = textColor.copy(alpha = 0.88f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress Bar inside Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.55f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(DeepRoyalBlue)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "লেভেল $activeLevel/$maxLevelCount",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    }
}
