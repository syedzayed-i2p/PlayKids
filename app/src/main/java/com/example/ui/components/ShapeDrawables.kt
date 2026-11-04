package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.Kid3DShape
import com.example.data.KidShape
import com.example.ui.theme.PrimaryNavy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapeGraphic(
    shape: KidShape,
    color: Color,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 64.dp,
    isOutlineOnly: Boolean = false,
    isSilhouette: Boolean = false,
    rotationDegrees: Float = 0f,
    outlineColor: Color = Color(0xFF94A3B8),
    strokeWidth: Float = 6f
) {
    val activeFillColor = if (isSilhouette) PrimaryNavy.copy(alpha = 0.88f) else color
    val innerBorderColor = if (color == Color(0xFFFAFAFA) || color == Color.White) {
        Color(0xFF64748B)
    } else {
        Color.White.copy(alpha = 0.45f)
    }

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val pad = strokeWidth + 3f

            rotate(degrees = rotationDegrees, pivot = Offset(cx, cy)) {
                when (shape) {
                    KidShape.CIRCLE -> {
                        val radius = (minOf(w, h) / 2f) - pad
                        if (isOutlineOnly) {
                            drawCircle(outlineColor, radius, Offset(cx, cy), style = Stroke(width = strokeWidth))
                        } else {
                            drawCircle(activeFillColor, radius, Offset(cx, cy), style = Fill)
                            drawCircle(innerBorderColor, radius, Offset(cx, cy), style = Stroke(width = 3f))
                        }
                    }

                    KidShape.TRIANGLE -> {
                        val path = Path().apply {
                            moveTo(cx, pad)
                            lineTo(w - pad, h - pad)
                            lineTo(pad, h - pad)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.SQUARE -> {
                        val side = minOf(w, h) - 2 * pad
                        val topLeft = Offset(cx - side / 2f, cy - side / 2f)
                        val rectSize = Size(side, side)
                        if (isOutlineOnly) {
                            drawRect(outlineColor, topLeft, rectSize, style = Stroke(width = strokeWidth))
                        } else {
                            drawRect(activeFillColor, topLeft, rectSize, style = Fill)
                            drawRect(innerBorderColor, topLeft, rectSize, style = Stroke(width = 3f))
                        }
                    }

                    KidShape.STAR -> {
                        val path = createStarPath(cx, cy, minOf(w, h) / 2f - pad, 5)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.HEART -> {
                        val path = createHeartPath(w, h, pad)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.RECTANGLE -> {
                        val rw = w - 2 * pad
                        val rh = (h - 2 * pad) * 0.62f
                        val topLeft = Offset(pad, cy - rh / 2f)
                        val rectSize = Size(rw, rh)
                        if (isOutlineOnly) {
                            drawRect(outlineColor, topLeft, rectSize, style = Stroke(width = strokeWidth))
                        } else {
                            drawRect(activeFillColor, topLeft, rectSize, style = Fill)
                            drawRect(innerBorderColor, topLeft, rectSize, style = Stroke(width = 3f))
                        }
                    }

                    KidShape.OVAL -> {
                        val ow = w - 2 * pad
                        val oh = (h - 2 * pad) * 0.64f
                        val topLeft = Offset(pad, cy - oh / 2f)
                        val ovalSize = Size(ow, oh)
                        if (isOutlineOnly) {
                            drawOval(outlineColor, topLeft, ovalSize, style = Stroke(width = strokeWidth))
                        } else {
                            drawOval(activeFillColor, topLeft, ovalSize, style = Fill)
                            drawOval(innerBorderColor, topLeft, ovalSize, style = Stroke(width = 3f))
                        }
                    }

                    KidShape.DIAMOND -> {
                        val path = Path().apply {
                            moveTo(cx, pad)
                            lineTo(w - pad * 1.25f, cy)
                            lineTo(cx, h - pad)
                            lineTo(pad * 1.25f, cy)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.SEMICIRCLE -> {
                        val radius = (minOf(w, h) / 2f) - pad
                        val rect = Rect(cx - radius, cy - radius * 0.55f, cx + radius, cy + radius * 1.45f)
                        val path = Path().apply {
                            arcTo(rect, 180f, 180f, false)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.CRESCENT -> {
                        val r = (minOf(w, h) / 2f) - pad
                        val outerPath = Path().apply {
                            addOval(Rect(cx - r, cy - r, cx + r, cy + r))
                        }
                        val innerPath = Path().apply {
                            addOval(Rect(cx - r * 0.35f, cy - r * 0.9f, cx + r * 1.35f, cy + r * 0.9f))
                        }
                        val crescentPath = Path().apply {
                            op(outerPath, innerPath, PathOperation.Difference)
                        }
                        renderShapePath(crescentPath, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.PENTAGON -> {
                        val path = createRegularPolygonPath(cx, cy, minOf(w, h) / 2f - pad, 5)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.HEXAGON -> {
                        val path = createRegularPolygonPath(cx, cy, minOf(w, h) / 2f - pad, 6)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.OCTAGON -> {
                        val path = createRegularPolygonPath(cx, cy, minOf(w, h) / 2f - pad, 8, startAngleDeg = -67.5f)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.TRAPEZOID -> {
                        val topHalf = (w - 2 * pad) * 0.27f
                        val botHalf = (w - 2 * pad) * 0.48f
                        val vOff = (h - 2 * pad) * 0.36f
                        val path = Path().apply {
                            moveTo(cx - topHalf, cy - vOff)
                            lineTo(cx + topHalf, cy - vOff)
                            lineTo(cx + botHalf, cy + vOff)
                            lineTo(cx - botHalf, cy + vOff)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.PARALLELOGRAM -> {
                        val skew = (w - 2 * pad) * 0.22f
                        val vOff = (h - 2 * pad) * 0.34f
                        val path = Path().apply {
                            moveTo(pad + skew, cy - vOff)
                            lineTo(w - pad, cy - vOff)
                            lineTo(w - pad - skew, cy + vOff)
                            lineTo(pad, cy + vOff)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.CROSS -> {
                        val arm = (minOf(w, h) - 2 * pad) * 0.18f
                        val half = (minOf(w, h) / 2f) - pad
                        val path = Path().apply {
                            moveTo(cx - arm, cy - half)
                            lineTo(cx + arm, cy - half)
                            lineTo(cx + arm, cy - arm)
                            lineTo(cx + half, cy - arm)
                            lineTo(cx + half, cy + arm)
                            lineTo(cx + arm, cy + arm)
                            lineTo(cx + arm, cy + half)
                            lineTo(cx - arm, cy + half)
                            lineTo(cx - arm, cy + arm)
                            lineTo(cx - half, cy + arm)
                            lineTo(cx - half, cy - arm)
                            lineTo(cx - arm, cy - arm)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.ARROW -> {
                        val half = (minOf(w, h) / 2f) - pad
                        val shaftHalf = half * 0.38f
                        val path = Path().apply {
                            moveTo(cx - half, cy - shaftHalf)
                            lineTo(cx + half * 0.05f, cy - shaftHalf)
                            lineTo(cx + half * 0.05f, cy - half * 0.85f)
                            lineTo(cx + half, cy)
                            lineTo(cx + half * 0.05f, cy + half * 0.85f)
                            lineTo(cx + half * 0.05f, cy + shaftHalf)
                            lineTo(cx - half, cy + shaftHalf)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.KITE -> {
                        val halfW = (w / 2f - pad) * 0.78f
                        val topY = pad
                        val midY = cy - (h - 2 * pad) * 0.15f
                        val botY = h - pad
                        val path = Path().apply {
                            moveTo(cx, topY)
                            lineTo(cx + halfW, midY)
                            lineTo(cx, botY)
                            lineTo(cx - halfW, midY)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.HEPTAGON -> {
                        val path = createRegularPolygonPath(cx, cy, minOf(w, h) / 2f - pad, 7)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.DECAGON -> {
                        val path = createRegularPolygonPath(cx, cy, minOf(w, h) / 2f - pad, 10)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.RIGHT_TRIANGLE -> {
                        val path = Path().apply {
                            moveTo(pad, pad)
                            lineTo(pad, h - pad)
                            lineTo(w - pad, h - pad)
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.RING -> {
                        val outerR = (minOf(w, h) / 2f) - pad
                        val innerR = outerR * 0.48f
                        if (isOutlineOnly) {
                            drawCircle(color = outlineColor, radius = outerR, center = Offset(cx, cy), style = Stroke(width = strokeWidth))
                            drawCircle(color = outlineColor, radius = innerR, center = Offset(cx, cy), style = Stroke(width = strokeWidth * 0.8f))
                        } else {
                            val ringThickness = outerR - innerR
                            drawCircle(
                                color = activeFillColor,
                                radius = innerR + ringThickness / 2f,
                                center = Offset(cx, cy),
                                style = Stroke(width = ringThickness)
                            )
                            drawCircle(color = innerBorderColor, radius = outerR, center = Offset(cx, cy), style = Stroke(width = 3f))
                            drawCircle(color = innerBorderColor, radius = innerR, center = Offset(cx, cy), style = Stroke(width = 3f))
                        }
                    }

                    KidShape.FOUR_POINT_STAR -> {
                        val path = createStarPath(cx, cy, minOf(w, h) / 2f - pad / 2f, 4)
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.CLOUD_SHAPE -> {
                        val cw = w - 2 * pad
                        val ch = h - 2 * pad
                        val path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = pad + cw * 0.1f,
                                    top = cy - ch * 0.05f,
                                    right = w - pad - cw * 0.1f,
                                    bottom = h - pad - ch * 0.12f,
                                    cornerRadius = CornerRadius(ch * 0.25f, ch * 0.25f)
                                )
                            )
                            addOval(Rect(Offset(cx - cw * 0.22f, cy - ch * 0.12f), cw * 0.22f))
                            addOval(Rect(Offset(cx + cw * 0.02f, cy - ch * 0.18f), cw * 0.26f))
                            addOval(Rect(Offset(cx + cw * 0.24f, cy - ch * 0.06f), cw * 0.18f))
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }

                    KidShape.SHIELD -> {
                        val path = Path().apply {
                            moveTo(cx, pad)
                            lineTo(w - pad, pad + (h - 2 * pad) * 0.2f)
                            cubicTo(
                                w - pad, cy + (h - 2 * pad) * 0.25f,
                                cx + (w - 2 * pad) * 0.25f, h - pad * 1.2f,
                                cx, h - pad
                            )
                            cubicTo(
                                cx - (w - 2 * pad) * 0.25f, h - pad * 1.2f,
                                pad, cy + (h - 2 * pad) * 0.25f,
                                pad, pad + (h - 2 * pad) * 0.2f
                            )
                            close()
                        }
                        renderShapePath(path, isOutlineOnly, activeFillColor, outlineColor, innerBorderColor, strokeWidth)
                    }
                }
            }
        }
    }
}

/**
 * Isometric & Shaded 3D Solid Shape Renderer for the 8 3D Shapes Discovery Section
 */
@Composable
fun Shape3DGraphic(
    shape3D: Kid3DShape,
    modifier: Modifier = Modifier,
    primaryColor: Color = shape3D.primaryColor,
    sizeDp: Dp = 80.dp
) {
    val baseColor = primaryColor
    val lightFace = baseColor.copy(alpha = 0.95f)
    val midFace = Color(
        red = (baseColor.red * 0.82f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.82f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.82f).coerceIn(0f, 1f),
        alpha = 1f
    )
    val darkFace = Color(
        red = (baseColor.red * 0.65f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.65f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.65f).coerceIn(0f, 1f),
        alpha = 1f
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = minOf(w, h) * 0.42f

            when (shape3D) {
                Kid3DShape.SPHERE -> {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, lightFace, darkFace),
                            center = Offset(cx - r * 0.3f, cy - r * 0.3f),
                            radius = r * 1.35f
                        ),
                        radius = r,
                        center = Offset(cx, cy)
                    )
                    // Equator wireframe ellipse for 3D depth perception
                    drawOval(
                        color = Color.White.copy(alpha = 0.45f),
                        topLeft = Offset(cx - r, cy - r * 0.28f),
                        size = Size(r * 2f, r * 0.56f),
                        style = Stroke(width = 3f)
                    )
                }

                Kid3DShape.CUBE -> {
                    val dx = r * 0.85f
                    val dy = r * 0.48f
                    // Top diamond face
                    val topPath = Path().apply {
                        moveTo(cx, cy - r)
                        lineTo(cx + dx, cy - r + dy)
                        lineTo(cx, cy - r + 2 * dy)
                        lineTo(cx - dx, cy - r + dy)
                        close()
                    }
                    // Left face
                    val leftPath = Path().apply {
                        moveTo(cx - dx, cy - r + dy)
                        lineTo(cx, cy - r + 2 * dy)
                        lineTo(cx, cy + r)
                        lineTo(cx - dx, cy + r - dy)
                        close()
                    }
                    // Right face
                    val rightPath = Path().apply {
                        moveTo(cx, cy - r + 2 * dy)
                        lineTo(cx + dx, cy - r + dy)
                        lineTo(cx + dx, cy + r - dy)
                        lineTo(cx, cy + r)
                        close()
                    }
                    drawPath(topPath, lightFace)
                    drawPath(leftPath, midFace)
                    drawPath(rightPath, darkFace)
                    drawPath(topPath, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(leftPath, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(rightPath, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                }

                Kid3DShape.CUBOID -> {
                    val wHalf = r * 0.95f
                    val hHalf = r * 0.48f
                    val depthX = r * 0.35f
                    val depthY = r * 0.32f
                    // Front rectangular face
                    val frontRect = Path().apply {
                        moveTo(cx - wHalf, cy - hHalf + depthY)
                        lineTo(cx + wHalf - depthX, cy - hHalf + depthY)
                        lineTo(cx + wHalf - depthX, cy + hHalf + depthY)
                        lineTo(cx - wHalf, cy + hHalf + depthY)
                        close()
                    }
                    // Top face
                    val topFace = Path().apply {
                        moveTo(cx - wHalf, cy - hHalf + depthY)
                        lineTo(cx - wHalf + depthX, cy - hHalf)
                        lineTo(cx + wHalf, cy - hHalf)
                        lineTo(cx + wHalf - depthX, cy - hHalf + depthY)
                        close()
                    }
                    // Side face
                    val sideFace = Path().apply {
                        moveTo(cx + wHalf - depthX, cy - hHalf + depthY)
                        lineTo(cx + wHalf, cy - hHalf)
                        lineTo(cx + wHalf, cy + hHalf)
                        lineTo(cx + wHalf - depthX, cy + hHalf + depthY)
                        close()
                    }
                    drawPath(topFace, lightFace)
                    drawPath(frontRect, midFace)
                    drawPath(sideFace, darkFace)
                    drawPath(topFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(frontRect, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(sideFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                }

                Kid3DShape.CYLINDER -> {
                    val cw = r * 1.4f
                    val ch = r * 1.5f
                    val ry = r * 0.34f
                    val left = cx - cw / 2f
                    val top = cy - ch / 2f
                    drawRect(
                        brush = Brush.horizontalGradient(listOf(midFace, lightFace, darkFace)),
                        topLeft = Offset(left, top),
                        size = Size(cw, ch)
                    )
                    drawOval(darkFace, topLeft = Offset(left, top + ch - ry), size = Size(cw, ry * 2f))
                    drawOval(lightFace, topLeft = Offset(left, top - ry), size = Size(cw, ry * 2f))
                    drawOval(Color.White.copy(alpha = 0.7f), topLeft = Offset(left, top - ry), size = Size(cw, ry * 2f), style = Stroke(width = 3f))
                }

                Kid3DShape.CONE -> {
                    val cw = r * 1.6f
                    val ry = r * 0.34f
                    val baseY = cy + r * 0.65f
                    val apexY = cy - r * 0.9f
                    val conePath = Path().apply {
                        moveTo(cx, apexY)
                        lineTo(cx + cw / 2f, baseY)
                        lineTo(cx - cw / 2f, baseY)
                        close()
                    }
                    drawPath(conePath, brush = Brush.horizontalGradient(listOf(midFace, lightFace, darkFace)))
                    drawOval(darkFace, topLeft = Offset(cx - cw / 2f, baseY - ry), size = Size(cw, ry * 2f))
                    drawPath(conePath, Color.White.copy(alpha = 0.5f), style = Stroke(width = 3f))
                }

                Kid3DShape.PYRAMID -> {
                    val apex = Offset(cx, cy - r * 0.95f)
                    val leftPt = Offset(cx - r * 0.95f, cy + r * 0.55f)
                    val midPt = Offset(cx + r * 0.08f, cy + r * 0.92f)
                    val rightPt = Offset(cx + r * 0.95f, cy + r * 0.42f)

                    val leftFace = Path().apply {
                        moveTo(apex.x, apex.y)
                        lineTo(leftPt.x, leftPt.y)
                        lineTo(midPt.x, midPt.y)
                        close()
                    }
                    val rightFace = Path().apply {
                        moveTo(apex.x, apex.y)
                        lineTo(midPt.x, midPt.y)
                        lineTo(rightPt.x, rightPt.y)
                        close()
                    }
                    drawPath(leftFace, lightFace)
                    drawPath(rightFace, darkFace)
                    drawPath(leftFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(rightFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                }

                Kid3DShape.TRIANGULAR_PRISM -> {
                    val frontTop = Offset(cx - r * 0.35f, cy - r * 0.7f)
                    val frontLeft = Offset(cx - r * 0.95f, cy + r * 0.75f)
                    val frontRight = Offset(cx + r * 0.25f, cy + r * 0.75f)
                    val backTop = Offset(cx + r * 0.35f, cy - r * 0.95f)
                    val backRight = Offset(cx + r * 0.95f, cy + r * 0.45f)

                    val frontTri = Path().apply {
                        moveTo(frontTop.x, frontTop.y)
                        lineTo(frontLeft.x, frontLeft.y)
                        lineTo(frontRight.x, frontRight.y)
                        close()
                    }
                    val sideQuad = Path().apply {
                        moveTo(frontTop.x, frontTop.y)
                        lineTo(backTop.x, backTop.y)
                        lineTo(backRight.x, backRight.y)
                        lineTo(frontRight.x, frontRight.y)
                        close()
                    }
                    drawPath(sideQuad, darkFace)
                    drawPath(frontTri, lightFace)
                    drawPath(sideQuad, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(frontTri, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                }

                Kid3DShape.RECTANGULAR_PRISM -> {
                    val wHalf = r * 0.55f
                    val hHalf = r * 0.85f
                    val dx = r * 0.35f
                    val dy = r * 0.28f

                    val frontFace = Path().apply {
                        moveTo(cx - wHalf, cy - hHalf + dy)
                        lineTo(cx + wHalf - dx, cy - hHalf + dy)
                        lineTo(cx + wHalf - dx, cy + hHalf)
                        lineTo(cx - wHalf, cy + hHalf)
                        close()
                    }
                    val topFace = Path().apply {
                        moveTo(cx - wHalf, cy - hHalf + dy)
                        lineTo(cx - wHalf + dx, cy - hHalf)
                        lineTo(cx + wHalf, cy - hHalf)
                        lineTo(cx + wHalf - dx, cy - hHalf + dy)
                        close()
                    }
                    val rightFace = Path().apply {
                        moveTo(cx + wHalf - dx, cy - hHalf + dy)
                        lineTo(cx + wHalf, cy - hHalf)
                        lineTo(cx + wHalf, cy + hHalf - dy)
                        lineTo(cx + wHalf - dx, cy + hHalf)
                        close()
                    }
                    drawPath(topFace, lightFace)
                    drawPath(frontFace, midFace)
                    drawPath(rightFace, darkFace)
                    drawPath(topFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(frontFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                    drawPath(rightFace, Color.White.copy(alpha = 0.6f), style = Stroke(width = 3f))
                }
            }
        }
    }
}

private fun DrawScope.renderShapePath(
    path: Path,
    isOutlineOnly: Boolean,
    fillColor: Color,
    outlineColor: Color,
    innerBorderColor: Color,
    strokeWidth: Float
) {
    if (isOutlineOnly) {
        drawPath(path, color = outlineColor, style = Stroke(width = strokeWidth))
    } else {
        val topLight = Color(
            red = (fillColor.red + (1f - fillColor.red) * 0.28f).coerceIn(0f, 1f),
            green = (fillColor.green + (1f - fillColor.green) * 0.28f).coerceIn(0f, 1f),
            blue = (fillColor.blue + (1f - fillColor.blue) * 0.28f).coerceIn(0f, 1f),
            alpha = fillColor.alpha
        )
        val bottomShade = Color(
            red = (fillColor.red * 0.82f).coerceIn(0f, 1f),
            green = (fillColor.green * 0.82f).coerceIn(0f, 1f),
            blue = (fillColor.blue * 0.82f).coerceIn(0f, 1f),
            alpha = fillColor.alpha
        )
        drawPath(
            path = path,
            brush = Brush.verticalGradient(listOf(topLight, fillColor, bottomShade)),
            style = Fill
        )
        drawPath(path, color = innerBorderColor, style = Stroke(width = 3.5f))
    }
}

private fun createRegularPolygonPath(
    cx: Float,
    cy: Float,
    radius: Float,
    sides: Int,
    startAngleDeg: Float = -90f
): Path {
    val path = Path()
    val angleStep = (2.0 * PI / sides).toFloat()
    var angle = (startAngleDeg * PI / 180.0).toFloat()

    path.moveTo(cx + radius * cos(angle), cy + radius * sin(angle))
    for (i in 1 until sides) {
        angle += angleStep
        path.lineTo(cx + radius * cos(angle), cy + radius * sin(angle))
    }
    path.close()
    return path
}

private fun createStarPath(cx: Float, cy: Float, outerRadius: Float, points: Int): Path {
    val path = Path()
    val innerRadius = outerRadius * 0.45f
    val step = (PI / points).toFloat()
    var angle = (-PI / 2).toFloat()

    path.moveTo(cx + outerRadius * cos(angle), cy + outerRadius * sin(angle))

    for (i in 0 until points) {
        angle += step
        path.lineTo(cx + innerRadius * cos(angle), cy + innerRadius * sin(angle))
        angle += step
        path.lineTo(cx + outerRadius * cos(angle), cy + outerRadius * sin(angle))
    }
    path.close()
    return path
}

private fun createHeartPath(w: Float, h: Float, strokeWidth: Float): Path {
    val path = Path()
    val left = strokeWidth
    val top = strokeWidth
    val right = w - strokeWidth
    val bottom = h - strokeWidth
    val width = right - left
    val height = bottom - top

    path.moveTo(left + width / 2f, top + height / 4f)
    path.cubicTo(
        left + width / 2f, top,
        left, top,
        left, top + height / 3f
    )
    path.cubicTo(
        left, top + height * 0.6f,
        left + width * 0.35f, top + height * 0.8f,
        left + width / 2f, bottom
    )
    path.cubicTo(
        left + width * 0.65f, top + height * 0.8f,
        right, top + height * 0.6f,
        right, top + height / 3f
    )
    path.cubicTo(
        right, top,
        left + width / 2f, top,
        left + width / 2f, top + height / 4f
    )
    path.close()
    return path
}
