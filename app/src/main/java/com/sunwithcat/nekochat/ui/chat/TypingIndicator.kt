package com.sunwithcat.nekochat.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TypingIndicator(
        modifier: Modifier = Modifier,
        dotSize: Dp = 12.dp,
        dotColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        spaceBetween: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TypingIndicator")

    @Composable
    fun animateDot(delayMillis: Int): State<Float> {
        return infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0f,
                animationSpec =
                        infiniteRepeatable(
                                animation =
                                        keyframes {
                                            durationMillis = 1200
                                            0f at 0
                                            0f at delayMillis
                                            -10f at delayMillis + 300 using FastOutSlowInEasing
                                            0f at delayMillis + 600
                                            0f at 1200
                                        },
                                repeatMode = RepeatMode.Restart
                        ),
                label = "DotAnimation"
        )
    }

    val offset1 by animateDot(0)
    val offset2 by animateDot(200)
    val offset3 by animateDot(400)

    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
    ) {
        Dot(offset1, dotSize, dotColor)
        Spacer(modifier = Modifier.width(spaceBetween))
        Dot(offset2, dotSize, dotColor)
        Spacer(modifier = Modifier.width(spaceBetween))
        Dot(offset3, dotSize, dotColor)
    }
}

@Composable
private fun Dot(offset: Float, size: Dp, color: Color) {
    Box(
            modifier =
                    Modifier.size(size)
                            .graphicsLayer { translationY = offset }
                            .background(color, CircleShape)
    )
}
