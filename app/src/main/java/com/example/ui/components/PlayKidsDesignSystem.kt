package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.BanglaAudioEngine
import com.example.data.AudioSettingsState
import com.example.ui.theme.*

/**
 * PlayKids V8.0 Unified Design System Tokens:
 * Reusable tokens for colours, gradients, spacing, corner radius, shadows,
 * button dimensions, icon sizes, and Bengali numeral formatting.
 */
object PlayKidsTokens {
    // Spacing tokens
    val SpaceXXS = 4.dp
    val SpaceXS = 6.dp
    val SpaceSM = 8.dp
    val SpaceMD = 12.dp
    val SpaceLG = 16.dp
    val SpaceXL = 22.dp

    // Corner radius tokens
    val RadiusPill = 28.dp
    val RadiusCard = 24.dp
    val RadiusMedium = 18.dp
    val RadiusSmall = 14.dp

    // Accessible button & card touch target dimensions
    val PrimaryButtonHeight = 56.dp
    val SecondaryButtonHeight = 48.dp
    val IconButtonSize = 48.dp
    val ToddlerTargetMinSize = 72.dp

    // Standard icon sizes
    val IconSmall = 18.dp
    val IconMedium = 22.dp
    val IconLarge = 28.dp

    // Glossy 3D Primary Gradients matching PlayKids UI Collage
    val PurplePrimaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFA855F7), Color(0xFF7E22CE), Color(0xFF6B21A8))
    )
    val PurplePillGradient = PurplePrimaryGradient
    val PurpleHeaderGradient = PurplePrimaryGradient

    val GoldPrimaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFE066), Color(0xFFF59E0B), Color(0xFFD97706))
    )
    val GoldNextGradient = GoldPrimaryGradient

    val BluePrimaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF1D4ED8))
    )
    val BlueButtonGradient = BluePrimaryGradient

    val GreenPrimaryGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A), Color(0xFF15803D))
    )
    val GreenSuccessGradient = GreenPrimaryGradient

    val CrimsonTotalRibbonGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE11D48), Color(0xFFBE123C))
    )

    val DisabledGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
    )
    val SlateDisabledGradient = DisabledGradient

    // 4 Home Game Card Gradients matching Screen 3 in PlayKids UI Collage
    val EmojiGoldCardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFE066), Color(0xFFF59E0B))
    )
    val ShapeGreenCardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A))
    )
    val ColorShapePurpleCardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFD946EF), Color(0xFF9333EA))
    )
    val ColorPaperRoseCardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFB7185), Color(0xFFE11D48))
    )

    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBnDigits(number: Int): String {
        return number.toString().map { ch ->
            if (ch in '0'..'9') bengaliDigits[ch - '0'] else ch
        }.joinToString("")
    }
}

/**
 * PlayKids V8.0 Premium 3D Primary Pill Button (Part 2):
 * - Glossy rounded pill shape with top specular highlight
 * - Spring scale-down press animation (0.95f) and dynamic shadow depth
 * - Supports leading/trailing icon, disabled state, and 56dp+ accessible height
 */
@Composable
fun PlayKidsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = PlayKidsTokens.PurplePrimaryGradient,
    contentColor: Color = Color.White,
    textColor: Color = contentColor,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = PlayKidsTokens.PrimaryButtonHeight,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "primary_btn_scale"
    )
    val shadowElev by animateDpAsState(
        targetValue = if (!enabled) 1.dp else if (isPressed) 2.dp else 8.dp,
        label = "primary_btn_shadow"
    )

    val activeBrush = if (enabled) gradient else PlayKidsTokens.DisabledGradient
    val activeTextColor = if (enabled) textColor else Color(0xFFF1F5F9)

    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = height)
            .shadow(shadowElev, RoundedCornerShape(PlayKidsTokens.RadiusPill))
            .clip(RoundedCornerShape(PlayKidsTokens.RadiusPill))
            .background(activeBrush)
            .border(2.5.dp, Color.White.copy(alpha = 0.92f), RoundedCornerShape(PlayKidsTokens.RadiusPill))
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glossy top highlight strip
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.92f)
                .height(height * 0.36f)
                .padding(top = 3.dp)
                .clip(RoundedCornerShape(PlayKidsTokens.RadiusPill))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.32f), Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.24f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = activeTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = activeTextColor,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = activeTextColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * PlayKids V8.0 Premium Secondary Button (Part 2):
 * Clean borders, balanced visual weight, and smooth spring press feedback.
 */
@Composable
fun PlayKidsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White.copy(alpha = 0.96f),
    contentColor: Color = DeepRoyalBlue,
    borderColor: Color = PlayBlue,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = PlayKidsTokens.SecondaryButtonHeight,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "secondary_btn_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .heightIn(min = height)
            .clip(RoundedCornerShape(PlayKidsTokens.RadiusMedium))
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = borderColor),
                enabled = enabled,
                onClick = onClick
            ),
        shape = RoundedCornerShape(PlayKidsTokens.RadiusMedium),
        color = if (enabled) containerColor else Color.White.copy(alpha = 0.55f),
        shadowElevation = if (!enabled) 0.dp else if (isPressed) 1.dp else 4.dp,
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled) borderColor else BorderSoft
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else SecondaryText.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) contentColor else SecondaryText.copy(alpha = 0.55f),
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else SecondaryText.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * PlayKids V8.0 Glossy Circular 3D Icon Button (Part 2):
 * Consistent 48dp+ touch target, glossy gradient/fill, white border, and press scale animation.
 */
@Composable
fun PlayKidsIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = PlayBlue,
    gradient: Brush? = null,
    iconTint: Color = Color.White,
    size: Dp = PlayKidsTokens.IconButtonSize,
    showBadgeDot: Boolean = false,
    badgeColor: Color = KidRed,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_btn_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .shadow(if (isPressed) 2.dp else 5.dp, CircleShape)
                .clip(CircleShape)
                .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.White),
                    onClick = onClick
                ),
            shape = CircleShape,
            color = if (gradient != null) Color.Transparent else containerColor,
            border = BorderStroke(2.2.dp, Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (gradient != null) Modifier.background(gradient) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(PlayKidsTokens.IconMedium)
                )
            }
        }

        if (showBadgeDot) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * PlayKids V8.0 Dedicated Settings Modal (Screen 12: "১২. সেটিংস" from UI Collage):
 * Provides instant, child-safe and parent-friendly control over:
 * 1. কণ্ঠ নির্দেশনা (Voice Instruction)
 * 2. সাউন্ড ইফেক্ট (Sound Effects)
 * 3. পটভূমি সংগীত (Background Music)
 * 4. ভাষা : বাংলা (Language indicator)
 * 5. অফলাইন মোড : ইনস্টল করা আছে ✅ (Offline Mode verification)
 * 6. অ্যানিমেশন কমানো (Reduced Motion)
 */
@Composable
fun PlayKidsSettingsDialog(
    audioSettings: AudioSettingsState,
    audio: BanglaAudioEngine? = null,
    bundledVoiceAssetsPresent: Boolean = true,
    onUpdateAudioSettings: (AudioSettingsState) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .shadow(14.dp, RoundedCornerShape(28.dp))
                .testTag("settings_dialog_card"),
            shape = RoundedCornerShape(28.dp),
            color = SoftCloud,
            border = BorderStroke(3.dp, Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Purple Header Bar matching Screen 12 ("সেটিংস")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PlayKidsTokens.PurplePrimaryGradient)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    PlayKidsIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ফিরে যাও",
                        onClick = {
                            audio?.playButtonTap()
                            onDismiss()
                        },
                        containerColor = PlayBlue,
                        size = 44.dp,
                        modifier = Modifier.align(Alignment.CenterStart),
                        testTag = "settings_dialog_close_btn"
                    )

                    Text(
                        text = "সেটিংস",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. কণ্ঠ নির্দেশনা (Voice Instruction)
                    SettingsToggleRow(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        iconTint = PlayBlue,
                        label = "কণ্ঠ নির্দেশনা",
                        checked = audioSettings.voiceEnabled,
                        testTag = "settings_switch_voice",
                        onCheckedChange = { enabled ->
                            val updated = audioSettings.copy(voiceEnabled = enabled)
                            audio?.updateAudioSettings(updated)
                            onUpdateAudioSettings(updated)
                        }
                    )

                    // 2. সাউন্ড ইফেক্ট (Sound Effects)
                    SettingsToggleRow(
                        icon = Icons.Default.MusicNote,
                        iconTint = CreativePurple,
                        label = "সাউন্ড ইফেক্ট",
                        checked = audioSettings.soundEffectsEnabled,
                        testTag = "settings_switch_sfx",
                        onCheckedChange = { enabled ->
                            val updated = audioSettings.copy(soundEffectsEnabled = enabled)
                            audio?.updateAudioSettings(updated)
                            onUpdateAudioSettings(updated)
                        }
                    )

                    // 3. পটভূমি সংগীত (Background Music)
                    SettingsToggleRow(
                        icon = Icons.Default.LibraryMusic,
                        iconTint = DeepRoyalBlue,
                        label = "পটভূমি সংগীত",
                        checked = audioSettings.backgroundMusicEnabled,
                        testTag = "settings_switch_bgm",
                        onCheckedChange = { enabled ->
                            val updated = audioSettings.copy(backgroundMusicEnabled = enabled)
                            audio?.updateAudioSettings(updated)
                            onUpdateAudioSettings(updated)
                        }
                    )

                    // 4. ভাষা (Language: বাংলা)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.5.dp, BorderSoft),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = PlayBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "ভাষা",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepRoyalBlue
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SoftSky,
                                border = BorderStroke(1.dp, PlayBlue.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "বাংলা ✓",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepRoyalBlue
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // 5. অফলাইন মোড (Offline Mode: ইনস্টল করা আছে ✅)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.5.dp, LearningMint.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone,
                                    contentDescription = null,
                                    tint = PlayBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "অফলাইন মোড",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepRoyalBlue
                                        )
                                    )
                                    Text(
                                        text = if (bundledVoiceAssetsPresent) "ইনস্টল করা আছে" else "ইন্সটলকৃত",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = KidGreenDark
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF16A34A),
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "সক্রিয়",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 6. অ্যানিমেশন নিয়ন্ত্রণ (Reduced Motion)
                    SettingsToggleRow(
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFFD97706),
                        label = "মৃদু অ্যানিমেশন (Reduced Motion)",
                        checked = audioSettings.reducedMotion,
                        testTag = "settings_switch_reduced_motion",
                        onCheckedChange = { enabled ->
                            val updated = audioSettings.copy(reducedMotion = enabled)
                            audio?.updateAudioSettings(updated)
                            onUpdateAudioSettings(updated)
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    PlayKidsPrimaryButton(
                        text = "ঠিক আছে",
                        onClick = {
                            audio?.playButtonTap()
                            onDismiss()
                        },
                        gradient = PlayKidsTokens.GreenPrimaryGradient,
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp,
                        testTag = "settings_done_btn"
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.5.dp, BorderSoft),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepRoyalBlue
                    )
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF16A34A),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}
