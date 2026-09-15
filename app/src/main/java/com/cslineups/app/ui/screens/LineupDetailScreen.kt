package com.cslineups.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cslineups.app.data.rememberStoredBitmap
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.LineupItem
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.ConfirmDialog
import com.cslineups.app.ui.components.GlowIconButton
import com.cslineups.app.ui.components.glassBackdropBlur
import com.cslineups.app.ui.components.ImageViewerDialog
import com.cslineups.app.ui.components.InfoRow
import com.cslineups.app.ui.components.ListCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.SmallShape
import com.cslineups.app.ui.label

private val FavoriteYellow = Color(0xFFFFD400)

@Composable
fun LineupDetailScreen(
    item: LineupItem,
    strings: Strings,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
) {
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }

    val images = listOf(
        strings.positionImage to item.positionImage,
        strings.aimImage to item.aimImage,
        strings.resultImage to item.resultImage,
    )

    Box(Modifier.fillMaxSize()) {
    ScreenScaffold(
        title = item.name,
        onBack = onBack,
        actions = {
            GlowIconButton(
                icon = Icons.Filled.Star,
                contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                onClick = onToggleFavorite,
                tint = if (isFavorite) FavoriteYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                glowColor = if (isFavorite) FavoriteYellow else MaterialTheme.colorScheme.primary,
            )
            GlowIconButton(
                icon = Icons.Filled.Edit,
                contentDescription = strings.editItem,
                onClick = onEdit,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        modifier = Modifier.glassBackdropBlur(confirmDelete),
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item { SectionTitle(strings.overview) }
            item {
                ListCard {
                    InfoRow(strings.name, item.name, strings.emptyValue)
                    InfoRow(strings.type, item.type.label(strings))
                    InfoRow(strings.side, item.side)
                    InfoRow(strings.category, item.category.label(strings))
                    InfoRow(strings.difficulty, item.difficulty.label(strings))
                }
            }

            item { SectionTitle(strings.startArea) }
            item {
                ListCard {
                    InfoRow(strings.startArea, item.startArea, strings.emptyValue)
                    InfoRow(strings.targetArea, item.targetArea, strings.emptyValue)
                }
            }

            item { SectionTitle(strings.throwSteps) }
            item {
                ListCard {
                    Text(
                        text = item.throwSteps.ifBlank { strings.emptyValue },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.throwSteps.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.5f,
                    )
                }
            }

            item { SectionTitle(strings.teachingImages) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    images.forEachIndexed { index, (label, name) ->
                        DetailImageCard(
                            label = label,
                            imageName = name,
                            placeholder = strings.noImage,
                            onClick = { if (name != null) viewerIndex = index else onEdit() },
                        )
                    }
                }
            }

            item { SectionTitle(strings.notes) }
            item {
                ListCard {
                    Text(
                        text = item.notes.ifBlank { strings.emptyValue },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.notes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { confirmDelete = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(strings.delete, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    viewerIndex?.let { index ->
        ImageViewerDialog(
            images = images,
            initialIndex = index,
            strings = strings,
            onDismiss = { viewerIndex = null },
        )
    }

    if (confirmDelete) {
        ConfirmDialog(
            title = strings.deleteConfirmTitle,
            message = strings.deleteItemConfirm,
            confirmText = strings.delete,
            cancelText = strings.cancel,
            onConfirm = {
                confirmDelete = false
                onDelete()
            },
            onDismiss = { confirmDelete = false },
        )
    }
    }
}

@Composable
private fun DetailImageCard(
    label: String,
    imageName: String?,
    placeholder: String,
    onClick: () -> Unit,
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
                .clickable(onClick = onClick),
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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 60.dp),
                )
            }
        }
    }
}
