package com.wzbx.cinetrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 主色：胶片暖黄。 */
private val Amber = Color(0xFFF5C451)
private val Ink = Color(0xFF0F1115)

private val DarkColors = darkColorScheme(
    primary = Amber,
    onPrimary = Ink,
    background = Ink,
    onBackground = Color(0xFFE8E6E1),
    surface = Color(0xFF171A1F),
    onSurface = Color(0xFFE8E6E1),
    surfaceVariant = Color(0xFF23272E),
    onSurfaceVariant = Color(0xFFB4B8BF)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF8A5A00),
    onPrimary = Color.White,
    background = Color(0xFFFAF8F4),
    onBackground = Color(0xFF1B1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1B1C1E),
    surfaceVariant = Color(0xFFEFEBE3),
    onSurfaceVariant = Color(0xFF4A4D52)
)

/**
 * 主题入口：跟随系统深浅色。
 *
 * 这里没有用 Material You 动态取色，因为 App 的视觉基调（深色影院感）比
 * “跟随壁纸变色”更重要；答辩时这也是一个可以讲的取舍。
 */
@Composable
fun CineTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
