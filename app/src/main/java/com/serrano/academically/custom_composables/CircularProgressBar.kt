package com.serrano.academically.custom_composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
    val thickness = radius / 3f
    val number = 50
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2f + thickness * 2f)
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            val sizeSubtract = sqrt(radius.toPx() * radius.toPx() + radius.toPx() * radius.toPx()) - radius.toPx()
            val circlesRadius = radius.toPx() - sizeSubtract
            val circlesThickness = circlesRadius / 3f
            val thicknessAndRadius = circlesRadius + circlesThickness

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        color.copy(alpha = 0.30f),
                        Color.Gray.copy(alpha = 0.30f)
                    )
                ),
                radius = ((size.minDimension / 2.0f) - (thicknessAndRadius / 2f)) + (circlesThickness / 2f)
            )
            drawArc(
                color = color.copy(alpha = 0.75f),
                startAngle = -90f,
                sweepAngle = 360 * curPercentage.value,
                useCenter = false,
                style = Stroke(
                    width = circlesThickness,
                    cap = StrokeCap.Round
                ),
                topLeft = Offset(thicknessAndRadius / 2, thicknessAndRadius / 2),
                size = Size(size.width - thicknessAndRadius, size.height - thicknessAndRadius)
            )
            val percent = (curPercentage.value * number).toInt()
            for (i in 0..<number) {
                val angle = Math.toRadians((i * 360.0 / number) - 135)
                val start = calculateRotationTranslation(
                    circlesRadius, angle
                ) + center
                val end = calculateRotationTranslation(
                    circlesRadius + circlesThickness, angle
                ) + center
                drawLine(
                    color = if (i < percent) color else Color.Gray,
                    start = start,
                    end = end,
                    strokeWidth = circlesThickness / 3f
                )
            }
        }
        Text(
            text = "${(curPercentage.value * 100).toInt()}%",
            style = MaterialTheme.typography.displayMedium,
            color = Color.Gray
        )
    }
}

fun calculateRotationTranslation(x: Float, angleRadians: Double): Offset {
    return Offset(
        (x * cos(angleRadians) - x * sin(angleRadians)).toFloat(),
        (x * sin(angleRadians) + x * cos(angleRadians)).toFloat()
    )
}