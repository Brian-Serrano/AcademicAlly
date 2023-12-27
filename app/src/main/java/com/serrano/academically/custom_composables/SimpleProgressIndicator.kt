package com.serrano.academically.custom_composables

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SimpleProgressIndicatorWithAnim(
    modifier: Modifier = Modifier,
    progress: Float = 0.7f,
    progressBarColor: Color = Color.Red,
    cornerRadius: Dp = 0.dp,
    trackColor: Color = Color(0XFFFBE8E8),
    thumbRadius: Dp = 0.dp,
    thumbColor: Color = Color.White,
    thumbOffset: Dp = thumbRadius,
    animationSpec: AnimationSpec<Float> = SimpleProgressIndicatorDefaults.SimpleProgressAnimationSpec,
) {
    val mProgress: Float by animateFloatAsState(
        targetValue = progress,
        animationSpec = animationSpec,
        label = ""
    )
    SimpleProgressIndicator(
        modifier,
        mProgress,
        progressBarColor,
        cornerRadius,
        trackColor,
        thumbRadius,
        thumbColor,
        thumbOffset
    )
}

@Composable
fun SimpleProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0.7f,
    progressBarColor: Color = Color.Red,
    cornerRadius: Dp = 0.dp,
    trackColor: Color = Color(0XFFFBE8E8),
    thumbRadius: Dp = 0.dp,
    thumbColor: Color = Color.White,
    thumbOffset: Dp = thumbRadius
) {
    Canvas(modifier.progressSemantics(progress)) {
        val progressWidth = size.width * progress
        drawLinearIndicatorBackground(trackColor, cornerRadius)
        drawLinearIndicator(progress, progressBarColor, cornerRadius)
        val thumbCenter = progressWidth - thumbOffset.toPx()
        if (thumbCenter > 0) {
            drawThumb(
                thumbRadius,
                thumbColor,
                Offset(progressWidth - thumbOffset.toPx(), size.height / 2f)
            )
        }

    }
}

private fun DrawScope.drawLinearIndicatorBackground(
    color: Color,
    cornerRadius: Dp
) {
    drawLinearIndicator(1f, color, cornerRadius)
}

private fun DrawScope.drawLinearIndicator(
    widthFraction: Float,
    color: Color,
    cornerRadius: Dp,
) {
    drawRoundRect(
        color = color,
        size = drawContext.size.copy(width = size.width * widthFraction),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )
}

private fun DrawScope.drawThumb(radius: Dp, color: Color, center: Offset) {
    drawCircle(
        color = color,
        radius = radius.toPx(),
        center = center
    )
}

object SimpleProgressIndicatorDefaults {
    val SimpleProgressAnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = null,
    )
}