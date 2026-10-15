package com.kaikunxp.pokeshowdown.ui.battle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.kaikunxp.pokeshowdown.data.model.ActiveAttackAnimation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun AttackAnimationOverlay(
    animation: ActiveAttackAnimation,
    isAttackerP1: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember(animation.id) { Animatable(0f) }

    LaunchedEffect(animation.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    val p = progress.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Player (P1) is at bottom-left: 25% width, 70% height
        // Opponent (P2) is at top-right: 75% width, 30% height
        val p1Pos = Offset(x = width * 0.25f, y = height * 0.70f)
        val p2Pos = Offset(x = width * 0.75f, y = height * 0.30f)

        val srcPos = if (isAttackerP1) p1Pos else p2Pos
        val dstPos = if (isAttackerP1) p2Pos else p1Pos

        val dx = dstPos.x - srcPos.x
        val dy = dstPos.y - srcPos.y
        val dist = hypot(dx, dy)
        val nx = if (dist > 0) dx / dist else 0f
        val ny = if (dist > 0) dy / dist else 0f

        // Split animation: 0.0 to 0.7 is travel, 0.7 to 1.0 is impact
        val travelP = (p / 0.7f).coerceIn(0f, 1f)
        val impactP = if (p > 0.7f) (p - 0.7f) / 0.3f else 0f

        val curPos = Offset(
            x = srcPos.x + dx * travelP,
            y = srcPos.y + dy * travelP
        )

        val type = animation.moveType.lowercase()

        when {
            type == "electric" -> {
                if (travelP > 0 && travelP < 1f) {
                    val path = Path().apply {
                        moveTo(srcPos.x, srcPos.y)
                        val steps = 8
                        for (i in 1..steps) {
                            val stepRatio = (i.toFloat() / steps.toFloat()) * travelP
                            val stepX = srcPos.x + dx * stepRatio
                            val stepY = srcPos.y + dy * stepRatio
                            val offset = if (i % 2 == 0) 40f else -40f
                            val ox = -ny * offset
                            val oy = nx * offset
                            lineTo(stepX + ox, stepY + oy)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF00E5FF),
                        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                if (impactP > 0f) {
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 1f - impactP),
                        radius = impactP * 150f,
                        center = dstPos
                    )
                    for (i in 0 until 6) {
                        val angle = i * (PI / 3) + (impactP * PI)
                        val startDist = impactP * 50f
                        val endDist = impactP * 150f
                        drawLine(
                            color = Color.Yellow.copy(alpha = 1f - impactP),
                            start = Offset(dstPos.x + cos(angle).toFloat() * startDist, dstPos.y + sin(angle).toFloat() * startDist),
                            end = Offset(dstPos.x + cos(angle).toFloat() * endDist, dstPos.y + sin(angle).toFloat() * endDist),
                            strokeWidth = 8f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            type == "fire" -> {
                if (travelP < 1f) {
                    for (i in 0..8) {
                        val tp = (travelP - i * 0.05f).coerceIn(0f, 1f)
                        if (tp > 0) {
                            val tPos = Offset(srcPos.x + dx * tp, srcPos.y + dy * tp)
                            val r = 40f - i * 4f
                            val col = if (i < 3) Color.White else if (i < 6) Color(0xFFFFEB3B) else Color(0xFFFF5722)
                            drawCircle(
                                color = col.copy(alpha = 1f - i * 0.1f),
                                radius = r,
                                center = tPos
                            )
                        }
                    }
                }

                if (impactP > 0f) {
                    drawCircle(
                        color = Color(0xFFFF5722).copy(alpha = 1f - impactP),
                        radius = impactP * 200f,
                        center = dstPos
                    )
                    drawCircle(
                        color = Color(0xFFFFEB3B).copy(alpha = 1f - impactP),
                        radius = impactP * 120f,
                        center = dstPos
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = (1f - impactP * 2).coerceAtLeast(0f)),
                        radius = impactP * 80f,
                        center = dstPos
                    )
                }
            }

            type == "water" -> {
                if (travelP < 1f) {
                    for (i in 0..3) {
                        val angle = travelP * PI.toFloat() * 6f + i * (PI.toFloat() / 2f)
                        val rDist = 30f * sin(travelP * PI.toFloat() * 3f)
                        val wPos = Offset(curPos.x + cos(angle) * rDist, curPos.y + sin(angle) * rDist)
                        drawCircle(
                            color = Color(0xFF00B0FF),
                            radius = 24f,
                            center = wPos
                        )
                        drawCircle(
                            color = Color(0xFF80D8FF),
                            radius = 12f,
                            center = Offset(wPos.x - 6f, wPos.y - 6f)
                        )
                    }
                }

                if (impactP > 0f) {
                    drawCircle(
                        color = Color(0xFF00B0FF).copy(alpha = 1f - impactP),
                        radius = impactP * 180f,
                        center = dstPos,
                        style = Stroke(width = 25f * (1f - impactP))
                    )
                    for (i in 0 until 8) {
                        val angle = i * (PI / 4)
                        val d = impactP * 150f
                        drawCircle(
                            color = Color(0xFF80D8FF).copy(alpha = 1f - impactP),
                            radius = 15f * (1f - impactP),
                            center = Offset(dstPos.x + cos(angle).toFloat() * d, dstPos.y + sin(angle).toFloat() * d)
                        )
                    }
                }
            }

            type == "grass" -> {
                val leaf = Path().apply {
                    moveTo(0f, -15f)
                    quadraticTo(15f, -5f, 0f, 15f)
                    quadraticTo(-15f, -5f, 0f, -15f)
                    close()
                }

                if (travelP < 1f) {
                    for (i in 0..4) {
                        val tp = (travelP - i * 0.1f).coerceIn(0f, 1f)
                        if (tp > 0) {
                            val latOffset = sin(tp * PI.toFloat() * 4f + i) * 50f
                            val lPos = Offset(srcPos.x + dx * tp - ny * latOffset, srcPos.y + dy * tp + nx * latOffset)
                            withTransform({
                                translate(lPos.x, lPos.y)
                                rotate(tp * 1080f + i * 45f)
                                scale(1.5f, 1.5f)
                            }) {
                                drawPath(leaf, color = Color(0xFF4CAF50))
                                drawPath(leaf, color = Color(0xFF1B5E20), style = Stroke(width = 2f))
                            }
                        }
                    }
                }

                if (impactP > 0f) {
                    for (i in 0 until 6) {
                        val angle = i * (PI / 3)
                        val d = impactP * 140f
                        withTransform({
                            translate(dstPos.x + cos(angle).toFloat() * d, dstPos.y + sin(angle).toFloat() * d)
                            rotate(impactP * 360f + i * 60f)
                            scale(2f, 2f)
                        }) {
                            drawPath(leaf, color = Color(0xFF4CAF50).copy(alpha = 1f - impactP))
                        }
                    }
                    drawCircle(
                        color = Color(0xFF81C784).copy(alpha = 1f - impactP),
                        radius = impactP * 100f,
                        center = dstPos
                    )
                }
            }

            type == "psychic" || type == "ghost" || type == "dark" || type == "poison" -> {
                val darkColor = if (type == "poison") Color(0xFF9C27B0) else if (type == "ghost") Color(0xFF651FFF) else if (type == "dark") Color(0xFF212121) else Color(0xFFE91E63)
                val lightColor = if (type == "poison") Color(0xFFE1BEE7) else if (type == "ghost") Color(0xFFB388FF) else if (type == "dark") Color(0xFF757575) else Color(0xFFF48FB1)

                if (travelP < 1f) {
                    withTransform({
                        translate(curPos.x, curPos.y)
                        rotate(travelP * 1080f)
                    }) {
                        drawOval(darkColor.copy(alpha = 0.8f), topLeft = Offset(-60f, -25f), size = Size(120f, 50f))
                        drawOval(lightColor.copy(alpha = 0.8f), topLeft = Offset(-25f, -60f), size = Size(50f, 120f))
                        drawCircle(Color.Black, radius = 18f, center = Offset.Zero)
                    }
                }

                if (impactP > 0f) {
                    drawCircle(
                        color = darkColor.copy(alpha = 1f - impactP),
                        radius = impactP * 250f,
                        center = dstPos,
                        style = Stroke(width = 40f * (1f - impactP))
                    )
                    drawCircle(
                        color = lightColor.copy(alpha = 1f - impactP),
                        radius = impactP * 180f,
                        center = dstPos,
                        style = Stroke(width = 20f * (1f - impactP))
                    )
                }
            }

            else -> {
                // Physical/Default moves: Anime speed streaks + Slash marks on impact
                if (travelP < 1f) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = curPos,
                        end = Offset(curPos.x - nx * 120f, curPos.y - ny * 120f),
                        strokeWidth = 16f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.7f),
                        start = Offset(curPos.x - ny * 30f, curPos.y + nx * 30f),
                        end = Offset(curPos.x - nx * 100f - ny * 30f, curPos.y - ny * 100f + nx * 30f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.7f),
                        start = Offset(curPos.x + ny * 30f, curPos.y - nx * 30f),
                        end = Offset(curPos.x - nx * 100f + ny * 30f, curPos.y - ny * 100f - nx * 30f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }

                if (impactP > 0f) {
                    withTransform({
                        translate(dstPos.x, dstPos.y)
                        rotate(-30f)
                        scale(1f + impactP * 0.5f, 1f + impactP * 0.5f)
                    }) {
                        for (i in -1..1) {
                            val offset = i * 30f
                            drawLine(
                                color = Color.Red.copy(alpha = 1f - impactP),
                                start = Offset(offset, -80f),
                                end = Offset(offset, 80f),
                                strokeWidth = 16f * (1f - impactP),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    val star = Path().apply {
                        moveTo(0f, -100f * impactP); lineTo(25f * impactP, -25f * impactP)
                        lineTo(100f * impactP, 0f); lineTo(25f * impactP, 25f * impactP)
                        lineTo(0f, 100f * impactP); lineTo(-25f * impactP, 25f * impactP)
                        lineTo(-100f * impactP, 0f); lineTo(-25f * impactP, -25f * impactP)
                        close()
                    }

                    withTransform({
                        translate(dstPos.x, dstPos.y)
                        rotate(impactP * 90f)
                    }) {
                        drawPath(
                            path = star,
                            color = Color.White.copy(alpha = (1f - impactP * 2).coerceAtLeast(0f))
                        )
                    }
                }
            }
        }
    }
}
