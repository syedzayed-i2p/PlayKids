package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

/**
 * PlayKids V8.0 Level Completion & Reward Screen (Screen 9: "৯. স্তর সম্পন্ন ও পুরস্কার"):
 * - Smiling 3D Learning Star at top
 * - 3 large golden 3D stars
 * - Purple ribbon banner ("অধ্যায় সম্পন্ন! • স্তর X সম্পন্ন!")
 * - Bold heading ("দারুণ হয়েছে!") & subtitle ("এই স্তরটি সফলভাবে সম্পন্ন হয়েছে।")
 * - Glossy Purple 3D Pill Button ("আবার খেলি ↻")
 * - Glossy Golden 3D Pill Button ("পরের স্তর ➔")
 * - Secondary button for Physical Practice ("বাস্তব অনুশীলন")
 */
@Composable
fun GameSuccessDialog(
    onDismiss: () -> Unit,
    onNextLevel: () -> Unit,
    onGoPhysicalPractice: () -> Unit,
    levelNumber: Int,
    starsAwarded: Int = 3,
    onPlayAgain: () -> Unit = onDismiss,
    titleText: String = "দারুণ হয়েছে!",
    messageText: String = "এই স্তরটি সফলভাবে সম্পন্ন হয়েছে।"
) {
    val levelBn = PlayKidsTokens.toBnDigits(levelNumber)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            shadowElevation = 14.dp,
            border = androidx.compose.foundation.BorderStroke(3.dp, StarYellow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFD8B4FE).copy(alpha = 0.38f),
                                Color(0xFFEAF4FF),
                                Color.White
                            )
                        )
                    )
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebrating 3D Learning Star Mascot
                SmilingStarMascot(size = 88, isHappy = true, showBook = false)

                Spacer(modifier = Modifier.height(6.dp))

                // 3 Golden Stars Row matching Screen 9 of PlayKids UI Collage
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayStars = starsAwarded.coerceIn(1, 3)
                    for (i in 1..3) {
                        val isEarned = i <= displayStars || i <= 2
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "অর্জিত তারা",
                            tint = if (isEarned) Color(0xFFFFB300) else Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(if (i == 2) 50.dp else 40.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Purple Ribbon Banner: "অধ্যায় সম্পন্ন! • স্তর X"
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CreativePurple,
                    shadowElevation = 5.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Text(
                        text = "অধ্যায় সম্পন্ন! • স্তর $levelBn",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepRoyalBlue,
                        fontSize = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Glossy Purple Pill Button: "আবার খেলি ↻"
                PlayKidsPrimaryButton(
                    text = "আবার খেলি",
                    onClick = onPlayAgain,
                    gradient = PlayKidsTokens.PurplePrimaryGradient,
                    trailingIcon = Icons.Default.Refresh,
                    modifier = Modifier.fillMaxWidth(),
                    height = 52.dp,
                    testTag = "dialog_play_again_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Glossy Golden Pill Button: "পরের স্তর ➔"
                PlayKidsPrimaryButton(
                    text = "পরের স্তর",
                    onClick = onNextLevel,
                    gradient = PlayKidsTokens.GoldPrimaryGradient,
                    contentColor = DeepRoyalBlue,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth(),
                    height = 54.dp,
                    testTag = "dialog_next_level_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Secondary Button for Physical Practice
                PlayKidsSecondaryButton(
                    text = "🎯 বাস্তব উপকরণ দিয়ে অনুশীলন করি",
                    onClick = onGoPhysicalPractice,
                    containerColor = SoftMint,
                    contentColor = KidGreenDark,
                    borderColor = LearningMint,
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    testTag = "dialog_physical_practice_button"
                )
            }
        }
    }
}
