package com.serrano.academically.custom_composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressBar(
    percentage: Float,
    radius: Dp,
    color: Color,
    animationPlayed: Boolean
) {
    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = ""
    )
    val thickness = radius / 5f
    val number = 50
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f + thickness * 2f)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        color.copy(alpha = 0.45f),
                        Color.Gray.copy(alpha = 0.15f)
                    )
                ),
                radius = (size.minDimension / 2.0f) + (thickness.toPx() * 2f)
            )
            drawArc(
                color = color.copy(alpha = 0.50f),
                startAngle = -90f,
                sweepAngle = 360 * curPercentage.value,
                useCenter = false,
                style = Stroke(
                    width = thickness.toPx(),
                    cap = StrokeCap.Round
                )
            )
            val percent = (curPercentage.value * number).toInt()
            for (i in 0..<number) {
                val angle = Math.toRadians((i * 360.0 / number) - 135)
                val start = calculateRotationTranslation(
                    radius.toPx(), angle
                ) + center
                val end = calculateRotationTranslation(
                    radius.toPx() - thickness.toPx(), angle
                ) + center
                drawLine(
                    color = if (i < percent) color else Color.Gray,
                    start = start,
                    end = end,
                    strokeWidth = thickness.toPx() / 2.5f
                )
            }
        }
        Text(
            text = "${(curPercentage.value * 100).toInt()}%",
            color = Color.Black,
            style = MaterialTheme.typography.displayMedium
        )
    }
}

fun calculateRotationTranslation(x: Float, angleRadians: Double): Offset {
    return Offset(
        (x * cos(angleRadians) - x * sin(angleRadians)).toFloat(),
        (x * sin(angleRadians) + x * cos(angleRadians)).toFloat()
    )
}