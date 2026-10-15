package com.kaikunxp.pokeshowdown.ui.battle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

private data class ArenaPalette(
    val skyTop: Color,
    val skyBottom: Color,
    val mountainBg: Color,
    val mountainFg: Color,
    val groundTop: Color,
    val groundBottom: Color,
    val dirtColor: Color,
    val p2Grass: Color,
    val p1Grass: Color,
    val tuftColor: Color
)

@Composable
fun DynamicBattleArenaBackground(
    turn: Int,
    modifier: Modifier = Modifier
) {
    val targetPalette = when {
        turn <= 4 -> ArenaPalette(
            skyTop = Color(0xFF4FC3F7), skyBottom = Color(0xFFE1F5FE),
            mountainBg = Color(0xFF81C784), mountainFg = Color(0xFF4CAF50),
            groundTop = Color(0xFFA5D6A7), groundBottom = Color(0xFF388E3C),
            dirtColor = Color(0xFF795548),
            p2Grass = Color(0xFFAED581), p1Grass = Color(0xFF81C784),
            tuftColor = Color(0xFF2E7D32)
        )
        turn <= 8 -> ArenaPalette(
            skyTop = Color(0xFFFF8A65), skyBottom = Color(0xFFF8BBD0),
            mountainBg = Color(0xFFBCAAA4), mountainFg = Color(0xFF8D6E63),
            groundTop = Color(0xFFE6EE9C), groundBottom = Color(0xFF9E9D24),
            dirtColor = Color(0xFF4E342E),
            p2Grass = Color(0xFFDCE775), p1Grass = Color(0xFFC0CA33),
            tuftColor = Color(0xFF827717)
        )
        else -> ArenaPalette(
            skyTop = Color(0xFF1A237E), skyBottom = Color(0xFF4A148C),
            mountainBg = Color(0xFF3949AB), mountainFg = Color(0xFF283593),
            groundTop = Color(0xFF546E7A), groundBottom = Color(0xFF263238),
            dirtColor = Color(0xFF212121),
            p2Grass = Color(0xFF455A64), p1Grass = Color(0xFF37474F),
            tuftColor = Color(0xFF1C313A)
        )
    }

    val animSpec = tween<Color>(durationMillis = 1200)
    val skyTop by animateColorAsState(targetPalette.skyTop, animSpec, label = "skyTop")
    val skyBottom by animateColorAsState(targetPalette.skyBottom, animSpec, label = "skyBottom")
    val mountainBg by animateColorAsState(targetPalette.mountainBg, animSpec, label = "mBg")
    val mountainFg by animateColorAsState(targetPalette.mountainFg, animSpec, label = "mFg")
    val groundTop by animateColorAsState(targetPalette.groundTop, animSpec, label = "gTop")
    val groundBottom by animateColorAsState(targetPalette.groundBottom, animSpec, label = "gBot")
    val dirtColor by animateColorAsState(targetPalette.dirtColor, animSpec, label = "dirt")
    val p2Grass by animateColorAsState(targetPalette.p2Grass, animSpec, label = "p2g")
    val p1Grass by animateColorAsState(targetPalette.p1Grass, animSpec, label = "p1g")
    val tuftColor by animateColorAsState(targetPalette.tuftColor, animSpec, label = "tuft")

    // Drifting Clouds Infinite Animation
    val infiniteTransition = rememberInfiniteTransition(label = "cloudMotion")
    val cloudOffsetRatio by infiniteTransition.animateFloat(
        initialValue = -0.2f, // Start off-screen left
        targetValue = 1.2f,  // End off-screen right
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudOffset"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val skyHeight = height * 0.52f
        val groundHeight = height * 0.48f

        // 1. Draw Sky Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(skyTop, skyBottom),
                startY = 0f,
                endY = skyHeight
            ),
            size = Size(width, skyHeight)
        )

        // 2. Draw Clean Drifting Clouds (Multi-layer pixel/bubble style)
        val cloudColor = Color.White.copy(alpha = if (turn > 8) 0.15f else 0.45f)
        
        // Background slow cloud
        val cloud1X = ((cloudOffsetRatio * 0.5f) % 1.4f - 0.2f) * width
        drawCloudShape(Offset(cloud1X, skyHeight * 0.15f), size = 120f, color = cloudColor.copy(alpha = cloudColor.alpha * 0.7f))
        
        // Midground medium cloud
        val cloud2X = (cloudOffsetRatio % 1.4f - 0.2f) * width
        drawCloudShape(Offset(cloud2X, skyHeight * 0.35f), size = 160f, color = cloudColor)

        // Foreground fast cloud
        val cloud3X = ((cloudOffsetRatio * 1.5f + 0.5f) % 1.4f - 0.2f) * width
        drawCloudShape(Offset(cloud3X, skyHeight * 0.10f), size = 100f, color = cloudColor)

        // 3. Draw Background Mountains
        val mBgPath = Path().apply {
            moveTo(0f, skyHeight)
            lineTo(width * 0.15f, skyHeight - height * 0.12f)
            lineTo(width * 0.35f, skyHeight - height * 0.04f)
            lineTo(width * 0.60f, skyHeight - height * 0.18f)
            lineTo(width * 0.85f, skyHeight - height * 0.06f)
            lineTo(width, skyHeight - height * 0.15f)
            lineTo(width, skyHeight)
            close()
        }
        drawPath(mBgPath, color = mountainBg.copy(alpha = 0.9f))

        // 4. Draw Foreground Mountains / Treeline
        val mFgPath = Path().apply {
            moveTo(0f, skyHeight)
            lineTo(width * 0.22f, skyHeight - height * 0.07f)
            lineTo(width * 0.45f, skyHeight - height * 0.14f)
            lineTo(width * 0.70f, skyHeight - height * 0.05f)
            lineTo(width * 0.90f, skyHeight - height * 0.10f)
            lineTo(width, skyHeight - height * 0.03f)
            lineTo(width, skyHeight)
            close()
        }
        drawPath(mFgPath, color = mountainFg)

        // 5. Draw Horizon Line Glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.2f)),
                startY = skyHeight - 20f,
                endY = skyHeight
            ),
            topLeft = Offset(0f, skyHeight - 20f),
            size = Size(width, 20f)
        )

        // 6. Draw Battle Arena Ground Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(groundTop, groundBottom),
                startY = skyHeight,
                endY = height
            ),
            topLeft = Offset(0f, skyHeight),
            size = Size(width, groundHeight)
        )

        // 7. Draw 3D Perspective Lines on Ground
        val numLines = 6
        for (i in 1..numLines) {
            val prog = i.toFloat() / numLines
            val yOffset = groundHeight * (prog * prog) // Quadratic scaling for faux-3D depth
            drawLine(
                color = Color.Black.copy(alpha = 0.06f),
                start = Offset(0f, skyHeight + yOffset),
                end = Offset(width, skyHeight + yOffset),
                strokeWidth = 2f + (2f * prog)
            )
        }

        // 8. Draw Dense Retro Grass Tufts
        val tuftPositions = listOf(
            Offset(width * 0.05f, skyHeight + 15f), Offset(width * 0.15f, skyHeight + 35f),
            Offset(width * 0.28f, skyHeight + 20f), Offset(width * 0.38f, skyHeight + 45f),
            Offset(width * 0.52f, skyHeight + 15f), Offset(width * 0.65f, skyHeight + 30f),
            Offset(width * 0.78f, skyHeight + 18f), Offset(width * 0.90f, skyHeight + 40f),
            Offset(width * 0.08f, skyHeight + 70f), Offset(width * 0.22f, skyHeight + 85f),
            Offset(width * 0.35f, skyHeight + 65f), Offset(width * 0.46f, skyHeight + 90f),
            Offset(width * 0.58f, skyHeight + 75f), Offset(width * 0.70f, skyHeight + 95f),
            Offset(width * 0.82f, skyHeight + 68f), Offset(width * 0.95f, skyHeight + 85f),
            Offset(width * 0.12f, skyHeight + 115f), Offset(width * 0.42f, skyHeight + 120f),
            Offset(width * 0.64f, skyHeight + 110f), Offset(width * 0.86f, skyHeight + 125f)
        )
        tuftPositions.forEach { center -> drawRetroGrassTuft(center, tuftColor) }

        // 9. Draw Opponent Platform (P2 - Top Right) [DRAWN FIRST]
        val p2Center = Offset(width * 0.75f, height * 0.55f)
        val p2Width = width * 0.45f
        val p2Height = height * 0.15f
        
        drawOval(color = dirtColor, topLeft = Offset(p2Center.x - p2Width / 2, p2Center.y - p2Height / 2 + 10f), size = Size(p2Width, p2Height))
        drawOval(color = p2Grass, topLeft = Offset(p2Center.x - p2Width / 2, p2Center.y - p2Height / 2), size = Size(p2Width, p2Height))
        drawOval(color = p2Grass.copy(alpha = 0.5f), topLeft = Offset(p2Center.x - p2Width * 0.4f, p2Center.y - p2Height * 0.4f), size = Size(p2Width * 0.8f, p2Height * 0.8f), style = Stroke(width = 3f))

        // 10. Draw Player Platform (P1 - Bottom Left) [DRAWN SECOND]
        val p1Center = Offset(width * 0.25f, height * 0.82f)
        val p1Width = width * 0.65f
        val p1Height = height * 0.22f

        drawOval(color = dirtColor, topLeft = Offset(p1Center.x - p1Width / 2, p1Center.y - p1Height / 2 + 15f), size = Size(p1Width, p1Height))
        drawOval(color = p1Grass, topLeft = Offset(p1Center.x - p1Width / 2, p1Center.y - p1Height / 2), size = Size(p1Width, p1Height))
        drawOval(color = p1Grass.copy(alpha = 0.5f), topLeft = Offset(p1Center.x - p1Width * 0.4f, p1Center.y - p1Height * 0.4f), size = Size(p1Width * 0.8f, p1Height * 0.8f), style = Stroke(width = 5f))
    }
}

private fun DrawScope.drawCloudShape(center: Offset, size: Float, color: Color) {
    val h = size * 0.4f
    drawOval(color, topLeft = Offset(center.x - size/2, center.y - h/2), size = Size(size, h))
    drawOval(color, topLeft = Offset(center.x - size*0.3f, center.y - h*0.8f), size = Size(size*0.4f, size*0.4f))
    drawOval(color, topLeft = Offset(center.x, center.y - h*0.9f), size = Size(size*0.3f, size*0.3f))
}

private fun DrawScope.drawRetroGrassTuft(center: Offset, bladeColor: Color) {
    val strokeWidth = 5f
    drawCircle(color = bladeColor.copy(alpha = 0.3f), radius = 8f, center = Offset(center.x, center.y + 2f))
    drawLine(color = bladeColor, start = center, end = Offset(center.x - 8f, center.y - 14f), strokeWidth = strokeWidth)
    drawLine(color = bladeColor, start = center, end = Offset(center.x, center.y - 18f), strokeWidth = strokeWidth)
    drawLine(color = bladeColor, start = center, end = Offset(center.x + 8f, center.y - 14f), strokeWidth = strokeWidth)
}
