package com.cslineups.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cslineups.app.data.rememberStoredBitmap
import com.cslineups.app.i18n.Strings

/**
 * 输入一段文字的玻璃弹窗，用于名称、位置、步骤、说明等。
 * 它是页面内的浮层（不是独立窗口），所以背后内容可以实时模糊。
 */
@Composable
fun TextEditDialog(
    title: String,
    label: String,
    initialValue: String,
    confirmText: String,
    cancelText: String,
    multiline: Boolean = false,
    requireNonBlank: Boolean = false,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    GlassOverlay(onDismiss = onDismiss) {
        GlassSectionLabel(title)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text(label) },
            singleLine = !multiline,
            minLines = if (multiline) 4 else 1,
            shape = SmallShape,
            colors = glassTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassTextButton(text = cancelText, onClick = onDismiss)
            Spacer(Modifier.width(10.dp))
            GlassPrimaryButton(
                text = confirmText,
                enabled = !requireNonBlank || value.isNotBlank(),
                onClick = { onConfirm(value.trim()) },
            )
        }
    }
}

/** 二次确认的玻璃弹窗，确认按钮用危险色。 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GlassOverlay(onDismiss = onDismiss) {
        GlassSectionLabel(title)
        Spacer(Modifier.height(10.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassTextButton(text = cancelText, onClick = onDismiss)
            Spacer(Modifier.width(10.dp))
            GlassPrimaryButton(
                text = confirmText,
                onClick = onConfirm,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        }
    }
}

/** 一张教学图的编辑槽：点为选图，右上角可移除。 */
@Composable
fun ImageSlot(
    label: String,
    imageName: String?,
    strings: Strings,
    onPick: () -> Unit,
    onClear: () -> Unit,
) {
    val bitmap = rememberStoredBitmap(imageName)

    Column(Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(SmallShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onPick),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        strings.chooseImage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (bitmap != null) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = strings.removeImage,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/** 全屏看图：左右翻页 + 双指缩放（这个保持独立窗口，因为要盖住整个屏幕）。 */
@Composable
fun ImageViewerDialog(
    images: List<Pair<String, String?>>,
    initialIndex: Int,
    strings: Strings,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                ZoomableStoredImage(images[page].second, strings.noImage)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = strings.close, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    images.getOrNull(pagerState.currentPage)?.first.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ZoomableStoredImage(imageName: String?, placeholder: String) {
    val bitmap = rememberStoredBitmap(imageName)
    if (bitmap == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(placeholder, color = Color.White)
        }
        return
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(imageName) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset = if (scale <= 1f) Offset.Zero else offset + pan
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
        )
    }
}
