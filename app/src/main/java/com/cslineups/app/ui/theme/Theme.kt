package com.cslineups.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cslineups.app.model.Difficulty
import com.cslineups.app.model.UtilityType

val SmokeGray = Color(0xFF8E8E93)
val FlashYellow = Color(0xFFFFD400)
val MolotovOrange = Color(0xFFFF7A1A)
val HeGreen = Color(0xFF34C759)
val AccentBlue = Color(0xFF0A84FF)

/** 与 iOS 版 AppTheme 的四种道具配色一致。 */
val UtilityType.color: Color
    get() = when (this) {
        UtilityType.SMOKE -> SmokeGray
        UtilityType.FLASH -> FlashYellow
        UtilityType.MOLOTOV -> MolotovOrange
        UtilityType.HE -> HeGreen
    }

/** 难度用颜色区分：简单偏绿、中等偏黄、困难偏红。 */
val Difficulty.color: Color
    get() = when (this) {
        Difficulty.EASY -> Color(0xFF34C759)
        Difficulty.MEDIUM -> Color(0xFFFF9F0A)
        Difficulty.HARD -> Color(0xFFFF3B30)
    }

val LightBackground = Color(0xFFF4F5F7)
val DarkBackground = Color(0xFF101114)

private val DarkColors = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = Color(0xFFF2F3F5),
    surface = DarkBackground,
    onSurface = Color(0xFFF2F3F5),
    surfaceVariant = Color(0xFF1B1D22),
    onSurfaceVariant = Color(0xFF9AA0A8),
    outline = Color(0xFF2C2F36),
)

private val LightColors = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF11131A),
    surface = LightBackground,
    onSurface = Color(0xFF11131A),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF5C6169),
    outline = Color(0xFFD6D9DE),
)

@Composable
fun CsLineupsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

