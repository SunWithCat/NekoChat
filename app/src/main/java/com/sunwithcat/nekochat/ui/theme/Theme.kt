package com.sunwithcat.nekochat.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 定义深色模式映射
private val DarkColorScheme = darkColorScheme(
    primary = TgDarkPrimary,
    onPrimary = Color.White,
    background = TgDarkBackground, // 全局背景变深蓝灰
    surface = TgDarkSurface,       // 卡片和顶栏变稍浅的深蓝灰
    onBackground = Color.White,
    onSurface = Color.White,
    primaryContainer = TgDarkSurface, // 关键：容器颜色也设为深色
    onPrimaryContainer = Color.White
)

// 定义浅色模式映射
private val LightColorScheme = lightColorScheme(
    primary = TgBlue,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    primaryContainer = Color(0xFFF1F1F1), // 浅色模式下的容器背景（类似灰色）
    onPrimaryContainer = Color.Black
)

@Composable
fun NekoChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}