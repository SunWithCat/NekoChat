package com.sunwithcat.nekochat.ui.chat

import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    dotSize: Dp = 10.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 6.dp
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
                            -10f at delayMillis + 200 using EaseOutCirc
                            0f at delayMillis + 400 using EaseInCirc
                            0f at 1200
                        },
                    repeatMode = RepeatMode.Restart
                ),
            label = "DotOffset"
        )
    }

    // 透明度动画
    @Composable
    fun animateAlpha(delayMillis: Int): State<Float> {
        return infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.4f at 0
                    1f at delayMillis + 200
                    0.4f at delayMillis + 400
                    0.4f at 1200
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "DotAlpha",
        )
    }

    val offset1 by animateDot(0)
    val alpha1 by animateAlpha(0)

    val offset2 by animateDot(200)
    val alpha2 by animateAlpha(200)

    val offset3 by animateDot(400)
    val alpha3 by animateAlpha(400)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Dot(offset1, alpha1, dotSize, dotColor)
        Spacer(modifier = Modifier.width(spaceBetween))
        Dot(offset2, alpha2, dotSize, dotColor)
        Spacer(modifier = Modifier.width(spaceBetween))
        Dot(offset3, alpha3, dotSize, dotColor)
    }
}

@Composable
private fun Dot(offset: Float, alpha: Float, size: Dp, color: Color) {
    Box(
        modifier =
            Modifier
                .size(size)
                .graphicsLayer {
                    translationY = offset
                    this.alpha = alpha
                }
                .background(color, CircleShape)
    )
}
