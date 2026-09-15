package com.cslineups.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 按下时轻微发光：外圈几层渐隐的光晕 + 内部一层很淡的填色。
 * 用描边而不是模糊，任何 Android 版本都能画出来。
 */
@Composable
fun Modifier.pressGlow(
    interactionSource: MutableInteractionSource,
    cornerRadius: Dp,
    glowColor: Color = MaterialTheme.colorScheme.primary,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val glow by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressGlow",
    )

    return this.drawBehind {
        if (glow <= 0.01f) return@drawBehind

        val radius = minOf(cornerRadius.toPx(), size.minDimension / 2f)
        val corners = CornerRadius(radius, radius)

        drawRoundRect(color = glowColor.copy(alpha = 0.12f * glow), cornerRadius = corners)
        for (ring in 0..2) {
            drawRoundRect(
                color = glowColor.copy(alpha = 0.22f * glow / (ring + 1)),
                cornerRadius = corners,
                style = Stroke(width = (2 + ring * 3).dp.toPx()),
            )
        }
    }
}

/** 液态玻璃卡片：半透明底 + 顶部高光 + 亮边框。 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val tint = if (isDark) Color(0xFF31353E) else Color.White
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(CardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tint.copy(alpha = if (isDark) 0.80f else 0.86f),
                        tint.copy(alpha = if (isDark) 0.62f else 0.66f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (isDark) 0.16f else 0.65f),
                shape = CardShape,
            )
            // 吞掉卡片上的点击，避免穿透到底部遮罩导致误关闭
            .clickable(interactionSource = interaction, indication = null) {}
            .padding(20.dp),
        content = content,
    )
}

/** 悬浮玻璃层：后面内容模糊 + 半透明遮罩 + 弹簧放大的玻璃卡片。 */
@Composable
fun GlassOverlay(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.86f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "glassScale",
    )
    val fade by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "glassFade",
    )

    val scrimInteraction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f * fade))
            .clickable(
                interactionSource = scrimInteraction,
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = fade
                },
            content = content,
        )
    }
}

/** 玻璃里的输入框配色：透明底、淡边框。 */
@Composable
fun glassTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

/** 玻璃里的主按钮：实色底 + 按压缩放 + 发光。 */
@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(PillShape)
            .pressGlow(
                interactionSource = interaction,
                cornerRadius = PillRadius,
                glowColor = MaterialTheme.colorScheme.primary,
            )
            .pressScale(interaction, pressedScale = 0.94f)
            .background(
                color = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                shape = PillShape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/** 玻璃里的次要按钮。 */
@Composable
fun GlassTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(PillShape)
            .pressGlow(
                interactionSource = interaction,
                cornerRadius = PillRadius,
                glowColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            .pressScale(interaction, pressedScale = 0.94f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** 右下角的加号：圆形 + 按下发光。 */
@Composable
fun GlowFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .pressGlow(
                interactionSource = interaction,
                cornerRadius = PillRadius,
                glowColor = MaterialTheme.colorScheme.primary,
            )
            .pressScale(interaction, pressedScale = 0.9f)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(26.dp),
        )
    }
}

/** 工具栏等处的图标按钮：圆形按下发光 + 轻微缩放。 */
@Composable
fun GlowIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    glowColor: Color = MaterialTheme.colorScheme.primary,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .pressGlow(
                interactionSource = interaction,
                cornerRadius = PillRadius,
                glowColor = glowColor,
            )
            .pressScale(interaction, pressedScale = 0.88f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** 玻璃弹窗里的一行小标题。 */
@Composable
fun GlassSectionLabel(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(0.dp))
    }
}
