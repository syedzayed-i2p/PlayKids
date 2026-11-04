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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrandConfig
import com.example.data.PictureCardItem
import com.example.data.PlayKidsLogoVariant
import com.example.data.Screen
import com.example.ui.theme.*

enum class PlayoraWorldTheme {
    DEFAULT_SKY_HILLS,
    EMOJI_DISCOVERY_WORLD,
    MAGICAL_SHAPE_GARDEN,
    RAINBOW_DISCOVERY_WORLD,
    CREATIVE_ART_STUDIO
}

/**
 * Full-bleed illustrated PlayKids scenic background with sky gradients,
 * floating clouds, rainbow arcs, floating garden shapes, rolling green hills, and meadow flowers.
 */
@Composable
fun PlayoraAnimatedSceneBackground(
    worldTheme: PlayoraWorldTheme = PlayoraWorldTheme.DEFAULT_SKY_HILLS,
    theme: PlayoraWorldTheme = worldTheme,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val activeTheme = if (theme != PlayoraWorldTheme.DEFAULT_SKY_HILLS) theme else worldTheme
    val infiniteTransition = rememberInfiniteTransition(label = "playkids_scene_anim")
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_drift"
    )
    val floatY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shape_float"
    )

    val activeCloudDrift = if (reducedMotion) 0f else cloudDrift
    val activeFloatY = if (reducedMotion) 0f else floatY

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val skyColors = when (activeTheme) {
                PlayoraWorldTheme.DEFAULT_SKY_HILLS -> listOf(
                    Color(0xFF5AB2FF),
                    Color(0xFF9BE0FF),
                    Color(0xFFDDF3FF)
                )
                PlayoraWorldTheme.EMOJI_DISCOVERY_WORLD -> listOf(
                    Color(0xFF4FA8FF),
                    Color(0xFF92DAFF),
                    Color(0xFFE4F7FF)
                )
                PlayoraWorldTheme.MAGICAL_SHAPE_GARDEN -> listOf(
                    Color(0xFF46B5E8),
                    Color(0xFF98E8D8),
                    Color(0xFFE2FBE8)
                )
                PlayoraWorldTheme.RAINBOW_DISCOVERY_WORLD -> listOf(
                    Color(0xFF66A6FF),
                    Color(0xFFA8DCFF),
                    Color(0xFFF3E8FF)
                )
                PlayoraWorldTheme.CREATIVE_ART_STUDIO -> listOf(
                    Color(0xFF58B4FF),
                    Color(0xFFA5E3FF),
                    Color(0xFFFFF5E4)
                )
            }

            drawRect(
                brush = Brush.verticalGradient(skyColors),
                size = size
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66FFF9C4),
                        Color(0x22FFF59D),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.18f, h * 0.12f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.18f, h * 0.12f)
            )

            if (activeTheme == PlayoraWorldTheme.RAINBOW_DISCOVERY_WORLD ||
                activeTheme == PlayoraWorldTheme.DEFAULT_SKY_HILLS
            ) {
                val rainbowColors = listOf(
                    Color(0x55FF5252),
                    Color(0x55FFB74D),
                    Color(0x55FFF176),
                    Color(0x5581C784),
                    Color(0x5564B5F6),
                    Color(0x55BA68C8)
                )
                val arcCenter = Offset(w * 0.5f, h * 0.42f)
                val baseRadius = w * 0.62f
                rainbowColors.forEachIndexed { idx, col ->
                    val r = baseRadius - idx * 14f
                    drawArc(
                        color = col,
                        startAngle = 190f,
                        sweepAngle = 160f,
                        useCenter = false,
                        topLeft = Offset(arcCenter.x - r, arcCenter.y - r),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )
                }
            }

            if (activeTheme == PlayoraWorldTheme.MAGICAL_SHAPE_GARDEN) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.28f),
                    radius = 28f,
                    center = Offset(w * 0.14f, h * 0.22f + activeFloatY)
                )
                drawRoundRect(
                    color = StarYellow.copy(alpha = 0.26f),
                    topLeft = Offset(w * 0.80f, h * 0.18f - activeFloatY),
                    size = Size(52f, 52f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                val triPath = Path().apply {
                    moveTo(w * 0.22f, h * 0.36f + activeFloatY)
                    lineTo(w * 0.28f, h * 0.43f + activeFloatY)
                    lineTo(w * 0.16f, h * 0.43f + activeFloatY)
                    close()
                }
                drawPath(triPath, color = LearningMint.copy(alpha = 0.30f))
            }

            drawFluffyCloud(
                cx = w * 0.18f + activeCloudDrift,
                cy = h * 0.13f,
                scale = 1.0f
            )
            drawFluffyCloud(
                cx = w * 0.82f - activeCloudDrift * 0.8f,
                cy = h * 0.16f,
                scale = 0.9f
            )
            drawFluffyCloud(
                cx = w * 0.52f + activeCloudDrift * 0.5f,
                cy = h * 0.26f,
                scale = 0.75f
            )

            val backHillPath = Path().apply {
                moveTo(0f, h * 0.76f)
                cubicTo(
                    w * 0.28f, h * 0.68f,
                    w * 0.62f, h * 0.80f,
                    w, h * 0.71f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = backHillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF8CE64E), Color(0xFF4DB825)),
                    startY = h * 0.68f,
                    endY = h
                )
            )

            val frontHillPath = Path().apply {
                moveTo(0f, h * 0.83f)
                cubicTo(
                    w * 0.35f, h * 0.77f,
                    w * 0.70f, h * 0.87f,
                    w, h * 0.80f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = frontHillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF68D836), Color(0xFF2E9E1B)),
                    startY = h * 0.77f,
                    endY = h
                )
            )

            if (activeTheme == PlayoraWorldTheme.CREATIVE_ART_STUDIO) {
                val tableTopY = h * 0.88f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF6D399), Color(0xFFE0AC69)),
                        startY = tableTopY,
                        endY = h
                    ),
                    topLeft = Offset(0f, tableTopY),
                    size = Size(w, h - tableTopY)
                )
                drawLine(
                    color = Color(0xFFC68B45),
                    start = Offset(0f, tableTopY),
                    end = Offset(w, tableTopY),
                    strokeWidth = 6f
                )
            } else {
                drawMeadowFlower(Offset(w * 0.10f, h * 0.88f), Color(0xFFFF80AB))
                drawMeadowFlower(Offset(w * 0.88f, h * 0.86f), Color(0xFFFFD54F))
                drawMeadowFlower(Offset(w * 0.24f, h * 0.93f), Color(0xFFFFFFFF))
                drawMeadowFlower(Offset(w * 0.76f, h * 0.92f), Color(0xFFFF8A80))
            }
        }

        content()
    }
}

private fun DrawScope.drawFluffyCloud(cx: Float, cy: Float, scale: Float) {
    val r = 34f * scale
    drawCircle(Color(0x18243B80), radius = r * 1.05f, center = Offset(cx, cy + 6f))
    drawCircle(Color(0x18243B80), radius = r * 0.82f, center = Offset(cx - r * 0.85f, cy + 10f))
    drawCircle(Color(0x18243B80), radius = r * 0.82f, center = Offset(cx + r * 0.85f, cy + 10f))
    drawCircle(Color.White.copy(alpha = 0.92f), radius = r * 0.82f, center = Offset(cx - r * 0.85f, cy + 4f))
    drawCircle(Color.White.copy(alpha = 0.92f), radius = r * 0.82f, center = Offset(cx + r * 0.85f, cy + 4f))
    drawCircle(Color.White.copy(alpha = 0.96f), radius = r * 1.08f, center = Offset(cx, cy - 4f))
}

private fun DrawScope.drawMeadowFlower(center: Offset, petalColor: Color) {
    val petalR = 5.5f
    for (i in 0 until 5) {
        val angle = (i * 72.0 * Math.PI / 180.0)
        val px = center.x + (petalR * 1.3f * kotlin.math.cos(angle)).toFloat()
        val py = center.y + (petalR * 1.3f * kotlin.math.sin(angle)).toFloat()
        drawCircle(petalColor, radius = petalR, center = Offset(px, py))
    }
    drawCircle(StarYellow, radius = 4.5f, center = center)
}

/**
 * PlayKids Brand Logo System (Version 6.0)
 * Centralizes BrandConfig.APPROVED_BRAND_NAME ("PlayKids") and supports all 8 required variants:
 * 1. Primary Horizontal Logo
 * 2. Compact Logo
 * 3. Android App Icon
 * 4. Transparent PNG / Badge
 * 5. Scalable Vector Logo
 * 6. Light-Background Version
 * 7. Dark-Background Version
 * 8. Monochrome Version
 */
@Composable
fun PlayKidsBrandLogo(
    variant: PlayKidsLogoVariant = PlayKidsLogoVariant.PRIMARY_HORIZONTAL,
    showTaglineRibbon: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDarkBg = variant == PlayKidsLogoVariant.DARK_BACKGROUND
    val isMonochrome = variant == PlayKidsLogoVariant.MONOCHROME
    val isCompact = variant == PlayKidsLogoVariant.COMPACT
    val isTransparent = variant == PlayKidsLogoVariant.TRANSPARENT_PNG
    val isIconOnly = variant == PlayKidsLogoVariant.ANDROID_APP_ICON || variant == PlayKidsLogoVariant.SCALABLE_VECTOR

    val playColor = when {
        isMonochrome -> DeepRoyalBlue
        isDarkBg -> Color(0xFF93C5FD)
        else -> PlayBlue
    }
    val kidsColor = when {
        isMonochrome -> DeepRoyalBlue
        isDarkBg -> StarYellow
        else -> CreativePurple
    }

    val containerBg = when (variant) {
        PlayKidsLogoVariant.DARK_BACKGROUND -> DeepRoyalBlue
        PlayKidsLogoVariant.LIGHT_BACKGROUND -> Color.White
        PlayKidsLogoVariant.TRANSPARENT_PNG -> Color.Transparent
        PlayKidsLogoVariant.MONOCHROME -> Color(0xFFF8FAFC)
        else -> Color.White.copy(alpha = 0.96f)
    }

    if (isIconOnly) {
        Surface(
            modifier = modifier.size(if (variant == PlayKidsLogoVariant.ANDROID_APP_ICON) 68.dp else 64.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (variant == PlayKidsLogoVariant.ANDROID_APP_ICON) DeepRoyalBlue else Color.White,
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                SmilingStarMascot(
                    size = 52,
                    showBook = true,
                    reducedMotion = true
                )
            }
        }
        return
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(if (isCompact) 20.dp else 24.dp),
            color = containerBg,
            shadowElevation = if (isTransparent) 0.dp else if (isCompact) 3.dp else 6.dp,
            border = if (isTransparent) null else androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = if (isMonochrome) DeepRoyalBlue else Color.White
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (isCompact) 12.dp else 18.dp,
                    vertical = if (isCompact) 4.dp else 7.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Integrated Learning Star + Open Book + Subtle Play Symbol Emblem
                SmilingStarMascot(
                    size = if (isCompact) 32 else 42,
                    showBook = true,
                    isMonochrome = isMonochrome,
                    monochromeTint = if (isDarkBg) Color.White else DeepRoyalBlue,
                    reducedMotion = true
                )

                Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 10.dp))

                // Clean, Modern Balanced Wordmark using BrandConfig.APPROVED_BRAND_NAME ("PlayKids")
                val brandName = BrandConfig.APPROVED_BRAND_NAME
                val splitIdx = BrandConfig.BRAND_PRIMARY_PART.length.coerceAtMost(brandName.length)
                val firstPart = brandName.substring(0, splitIdx)
                val secondPart = brandName.substring(splitIdx)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = firstPart,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isCompact) 22.sp else 30.sp,
                            color = playColor
                        )
                    )
                    Text(
                        text = secondPart,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isCompact) 22.sp else 30.sp,
                            color = kidsColor
                        )
                    )
                }

                Spacer(modifier = Modifier.width(if (isCompact) 5.dp else 8.dp))

                // Subtle Integrated Play Badge (▶)
                Surface(
                    shape = CircleShape,
                    color = if (isMonochrome) DeepRoyalBlue else if (isDarkBg) StarYellow else PlayBlue,
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isDarkBg && !isMonochrome) DeepRoyalBlue else Color.White,
                            modifier = Modifier.size(if (isCompact) 14.dp else 16.dp)
                        )
                    }
                }
            }
        }

        if (showTaglineRibbon) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isMonochrome) DeepRoyalBlue else StarYellow,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isMonochrome) DeepRoyalBlue else Color.White
                )
            ) {
                Text(
                    text = BrandConfig.BRAND_TAGLINE,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isMonochrome) Color.White else DeepRoyalBlue,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun PlayoraBrandLogo(
    showTaglineRibbon: Boolean = false,
    showRibbon: Boolean = showTaglineRibbon,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    PlayKidsBrandLogo(
        variant = if (compact) PlayKidsLogoVariant.COMPACT else PlayKidsLogoVariant.PRIMARY_HORIZONTAL,
        showTaglineRibbon = showTaglineRibbon || showRibbon,
        modifier = modifier
    )
}

/**
 * PlayKids 4-Tab Bottom Navigation Bar:
 * Home | Progress | Practice | Parents
 */
@Composable
fun PlayoraBottomNavBar(
    currentScreen: Screen,
    onSelectTab: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        color = Color.White.copy(alpha = 0.96f),
        border = androidx.compose.foundation.BorderStroke(2.dp, PlayBlue.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple(Screen.HOME, "হোম", Icons.Default.Home),
                Triple(Screen.MY_PROGRESS, "অগ্রগতি", Icons.Default.BarChart),
                Triple(Screen.PHYSICAL_PRACTICE, "অনুশীলন", Icons.Default.SportsEsports),
                Triple(Screen.PARENT_DASHBOARD, "অভিভাবক", Icons.Default.People)
            )

            navItems.forEach { (screen, label, icon) ->
                val isSelected = currentScreen == screen
                val tabColor = when (screen) {
                    Screen.HOME -> CreativePurple
                    Screen.MY_PROGRESS -> PlayBlue
                    Screen.PHYSICAL_PRACTICE -> PlayBlue
                    else -> PlayBlue
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) tabColor.copy(alpha = 0.14f) else Color.Transparent)
                        .testTag("bottom_nav_${screen.name.lowercase()}")
                        .clickable { onSelectTab(screen) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) tabColor else SoftSky),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) Color.White else DeepRoyalBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) tabColor else DeepRoyalBlue,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Custom Framed Illustrated Picture Badge for Emoji & Picture Matching Cards.
 */
@Composable
fun PlayoraIllustratedPictureBadge(
    item: PictureCardItem,
    sizeDp: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = minOf(w, h) * 0.46f

            drawCircle(
                color = Color(0x18243B80),
                radius = r,
                center = Offset(cx, cy + 4f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, item.badgeColor),
                    center = Offset(cx - r * 0.25f, cy - r * 0.25f),
                    radius = r * 1.2f
                ),
                radius = r,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = PlayBlue.copy(alpha = 0.28f),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )

            when (item.id) {
                "panda" -> {
                    val headR = r * 0.58f
                    drawCircle(Color(0xFF263238), radius = headR * 0.38f, center = Offset(cx - headR * 0.7f, cy - headR * 0.65f))
                    drawCircle(Color(0xFF263238), radius = headR * 0.38f, center = Offset(cx + headR * 0.7f, cy - headR * 0.65f))
                    drawCircle(Color.White, radius = headR, center = Offset(cx, cy + 2f))
                    drawCircle(Color(0xFF263238), radius = headR, center = Offset(cx, cy + 2f), style = Stroke(width = 2.5f))
                    drawOval(Color(0xFF263238), topLeft = Offset(cx - headR * 0.65f, cy - headR * 0.25f), size = Size(headR * 0.5f, headR * 0.6f))
                    drawOval(Color(0xFF263238), topLeft = Offset(cx + headR * 0.15f, cy - headR * 0.25f), size = Size(headR * 0.5f, headR * 0.6f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx - headR * 0.38f, cy - headR * 0.02f))
                    drawCircle(Color.White, radius = 3.5f, center = Offset(cx + headR * 0.38f, cy - headR * 0.02f))
                    drawCircle(Color(0xFF263238), radius = 4f, center = Offset(cx, cy + headR * 0.25f))
                    drawCircle(JoyCoral.copy(alpha = 0.6f), radius = 4.5f, center = Offset(cx - headR * 0.55f, cy + headR * 0.32f))
                    drawCircle(JoyCoral.copy(alpha = 0.6f), radius = 4.5f, center = Offset(cx + headR * 0.55f, cy + headR * 0.32f))
                }
            }
        }

        if (item.id != "panda") {
            Text(
                text = item.symbol,
                fontSize = (sizeDp.value * 0.56f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlayoraIllustratedPictureBadge(
    symbol: String,
    badgeColor: Color = SoftSunshine,
    sizeDp: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val itemId = if (symbol == "🐼") "panda" else symbol
    PlayoraIllustratedPictureBadge(
        item = PictureCardItem(
            id = itemId,
            symbol = symbol,
            nameBn = "",
            category = com.example.data.PictureCategory.CORE_EMOJI,
            badgeColor = badgeColor
        ),
        sizeDp = sizeDp,
        modifier = modifier
    )
}
