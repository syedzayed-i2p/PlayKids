package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.example.audio.VoiceScriptRegistry
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PhysicalPracticeScreen(
    initialGameType: GameType,
    allProgress: Map<GameType, GameProgressData>,
    audio: BanglaAudioEngine,
    onBack: () -> Unit,
    onTogglePhysicalDone: (GameType, Boolean) -> Unit,
    onNavigateTab: (Screen) -> Unit = {}
) {
    var selectedGame by remember { mutableStateOf(initialGameType) }
    val currentProgress = allProgress[selectedGame] ?: GameProgressData(selectedGame)
    val isDone = currentProgress.physicalCompleted

    val scrollState = rememberScrollState()
    val audioSettings by audio.audioSettings.collectAsState()

    val guidePrompt = VoiceScriptRegistry.SCRIPTS["physical_practice"]?.banglaScript
        ?: "এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।"

    LaunchedEffect(selectedGame) {
        audio.speak(guidePrompt)
    }

    PlayoraAnimatedSceneBackground(
        theme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
        reducedMotion = audioSettings.reducedMotion
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            audio.playButtonTap()
                            onBack()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape)
                            .testTag("physical_back_button"),
                        shape = CircleShape,
                        color = PlayBlue,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "ফিরে যাও",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(4.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = LearningMint,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${BrandConfig.APPROVED_BRAND_NAME} • বাস্তবে খেলি",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "এবার বাস্তব উপকরণ দিয়ে অনুশীলন করি।",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        onClick = { audio.speak(guidePrompt) },
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        color = PlayBlue,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "শুনুন",
                                tint = Color.White
                            )
                        }
                    }
                }
            },
            bottomBar = {
                PlayoraBottomNavBar(
                    currentScreen = Screen.PHYSICAL_PRACTICE,
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
            ) {
                // 4 Game Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameType.entries.forEach { gType ->
                        val isSelected = gType == selectedGame
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    audio.playButtonTap()
                                    selectedGame = gType
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) gType.themeColor else Color.White.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color.White else BorderSoft
                            ),
                            shadowElevation = if (isSelected) 4.dp else 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = gType.serialName,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DeepRoyalBlue,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // Header & Visual Arrangement Diagram
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedGame.titleEn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = NunitoFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = selectedGame.themeColor
                                        )
                                    )
                                    Text(
                                        text = "${selectedGame.serialName}: ${selectedGame.titleBn}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepRoyalBlue
                                        )
                                    )
                                }

                                if (isDone) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = KidGreen
                                    ) {
                                        Text(
                                            text = "✓ সম্পন্ন",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Illustrated Table / Arrangement Diagram
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = selectedGame.lightBg,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, selectedGame.accentBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📐 টেবিলে উপকরণ সাজানোর নকশা:",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = DeepRoyalBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    when (selectedGame) {
                                        GameType.EMOJI_MATCH -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                listOf("😀", "❤️", "⭐", "🌸", "🍎").forEach { em ->
                                                    PlayoraIllustratedPictureBadge(
                                                        symbol = em,
                                                        badgeColor = SoftSunshine,
                                                        sizeDp = 44.dp
                                                    )
                                                }
                                            }
                                        }
                                        GameType.SHAPE_MATCH -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                KidShape.CORE_SHAPES.forEach { sh ->
                                                    ShapeGraphic(shape = sh, color = LearningMint, sizeDp = 38.dp)
                                                }
                                            }
                                        }
                                        GameType.COLOR_PAPER_MATCH -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                KidColor.CORE_COLORS.forEach { col ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(width = 44.dp, height = 34.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(col.color)
                                                            .border(2.dp, col.darkColor, RoundedCornerShape(8.dp))
                                                    )
                                                }
                                            }
                                        }
                                        GameType.COLOR_SHAPE_CHALLENGE -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                ShapeGraphic(KidShape.STAR, KidRed, sizeDp = 36.dp)
                                                ShapeGraphic(KidShape.TRIANGLE, KidYellowDark, sizeDp = 36.dp)
                                                ShapeGraphic(KidShape.HEART, KidBlue, sizeDp = 36.dp)
                                                ShapeGraphic(KidShape.CIRCLE, KidGreen, sizeDp = 36.dp)
                                                ShapeGraphic(KidShape.SQUARE, KidOrange, sizeDp = 36.dp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Equipment List Section
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
                                text = "📦 প্রয়োজনীয় উপকরণ ও প্রস্তুতি:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepRoyalBlue
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val equipmentList = when (selectedGame) {
                                GameType.EMOJI_MATCH -> listOf(
                                    "১০ থেকে ১২ জোড়া বড় সাইজের লেমিনেটেড ইমোজি কার্ড (😀, ❤️, 👍, ⭐, 😂, 😎, 🌸, ⚽, 🍎)",
                                    "অতিরিক্ত অনুশীলনের জন্য ফল, প্রাণী ও যানবাহনের ছবির ফ্ল্যাশকার্ড",
                                    "১টি সমতল টেবিল বা ফ্লোর ম্যাট এবং ১টি কার্ড রাখার ট্রে"
                                )
                                GameType.SHAPE_MATCH -> listOf(
                                    "কমপক্ষে ৫ ধরনের বড় শেইপ কাটআউট (বৃত্ত, ত্রিভুজ, চতুর্ভুজ, তারা, হার্ট)",
                                    "অতিরিক্ত অনুশীলনের জন্য আয়তক্ষেত্র, ডিম্বাকৃতি, হীরক, পঞ্চভুজ ও ষড়ভুজের কার্ডবোর্ড শেইপ",
                                    "১টি Matching Board বা আউটলাইন আঁকা আর্ট পেপার এবং ১টি শেইপ রাখার ট্রে"
                                )
                                GameType.COLOR_SHAPE_CHALLENGE -> listOf(
                                    "৫টি মূল রঙের (লাল, হলুদ, নীল, সবুজ, কমলা) × ৫টি আকৃতির পূর্ণ ২৫টি লেমিনেটেড কম্বিনেশন কার্ড",
                                    "অতিরিক্ত ধাপের জন্য গোলাপি, বেগুনি, বাদামি, কালো ও সাদা রঙের অতিরিক্ত শেইপ কার্ড",
                                    "১টি কার্ড রাখার ট্রে এবং সমতল টেবিল"
                                )
                                GameType.COLOR_PAPER_MATCH -> listOf(
                                    "লাল, হলুদ, নীল, সবুজ ও কমলা রঙের ৫টি বড় কালার পেপার বা কার্ড",
                                    "একই ৫টি রঙের ৫টি আলাদা ট্রে বা রঙিন ম্যাট",
                                    "উন্নত স্তরের জন্য গোলাপি, বেগুনি, বাদামি, কালো ও সাদা রঙের কাগজ"
                                )
                            }

                            equipmentList.forEach { item ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = "•",
                                        color = selectedGame.themeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = DeepRoyalBlue)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step-by-Step Execution Guide
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
                                text = "🎯 বাস্তব খেলার ধাপসমূহ ও বড়দের টিপস:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PlayBlue
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val stepsList = when (selectedGame) {
                                GameType.EMOJI_MATCH -> listOf(
                                    "ধাপ ১ (টেবিল সাজানো): টেবিলে ইমোজি কার্ডের একটি সেট সারিবদ্ধভাবে সাজিয়ে রাখুন।",
                                    "ধাপ ২ (কার্ড তোলা): শিশু ট্রে থেকে অপর সেটের একটি ইমোজি কার্ড হাতে নেবে।",
                                    "ধাপ ৩ (খোঁজা ও মেলানো): টেবিলে থাকা কার্ডগুলোর মধ্যে একই ইমোজি খুঁজে পাশাপাশি রাখবে।",
                                    "বড়দের টিপস: শুরুতে ৩-৪ জোড়া দিয়ে শুরু করুন এবং শিশু পারলে হাততালি দিয়ে উৎসাহ দিন।"
                                )
                                GameType.SHAPE_MATCH -> listOf(
                                    "ধাপ ১ (পরিচিতি): প্রথমে সব শেইপ হাতে নিয়ে নাম বলে শিশুকে দেখান।",
                                    "ধাপ ২ (শেইপ হাতে নেওয়া): শিশু ট্রে থেকে একটি শেইপ (যেমন: ত্রিভুজ) হাতে নেবে।",
                                    "ধাপ ৩ (সঠিক ঘর খোঁজা ও বসানো): ম্যাচিং বোর্ডে একই শেইপের ঘরের ওপর সঠিকভাবে বসাবে।",
                                    "বড়দের টিপস: ঘরের কোণা ও বাহুগুলো আঙুল দিয়ে ছুঁয়ে দেখতে উৎসাহিত করুন।"
                                )
                                GameType.COLOR_SHAPE_CHALLENGE -> listOf(
                                    "পর্যায় ১ (শুধু রং): বলুন 'লাল রং খুঁজে দাও!' শিশু যেকোনো আকৃতির লাল কার্ড দেবে।",
                                    "পর্যায় ২ (শুধু আকৃতি): বলুন 'তারা দাও!' শিশু যেকোনো রঙের তারা কার্ড দেবে।",
                                    "পর্যায় ৩ (রং + আকৃতি একসঙ্গে): বলুন 'সবুজ ত্রিভুজ দাও!' বা 'লাল তারা দাও!' শিশু উভয় বৈশিষ্ট্য মিলিয়ে সঠিক কার্ডটি তুলে দেবে।"
                                )
                                GameType.COLOR_PAPER_MATCH -> listOf(
                                    "ধাপ ১ (ট্রে সাজানো): লাল, হলুদ, নীল, সবুজ ও কমলা রঙের ৫টি ট্রে পাশাপাশি সাজিয়ে রাখুন।",
                                    "ধাপ ২ (কাগজ তোলা): শিশু একটি রঙিন কাগজ হাতে নেবে এবং রঙের নাম বলবে।",
                                    "ধাপ ৩ (সঠিক ঘরে রাখা): একই রঙের ট্রে খুঁজে কাগজটি সেই ট্রের ভেতর রাখবে।",
                                    "বড়দের টিপস: কয়েকবার খেলার পর ট্রের জায়গা অদলবদল করে দিন যাতে শিশু রঙ চিনে রাখে।"
                                )
                            }

                            stepsList.forEachIndexed { idx, step ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SoftSky.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = selectedGame.themeColor,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = DeepRoyalBlue,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Parent Confirmation Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = if (isDone) SoftMint else SoftSunshine,
                        border = androidx.compose.foundation.BorderStroke(
                            2.5.dp,
                            Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "অভিভাবক / শিক্ষকের নিশ্চিতকরণ",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = DeepRoyalBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val newState = !isDone
                                    onTogglePhysicalDone(selectedGame, newState)
                                    if (newState) {
                                        audio.playSuccessChime()
                                        audio.speak("দারুণ! বাস্তব অনুশীলন সম্পন্ন হয়েছে!")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("physical_confirm_button"),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDone) KidGreen else PlayBlue
                                )
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isDone) "আমরা অনুশীলন করেছি ✓" else "আমরা অনুশীলন করেছি",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 17.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
