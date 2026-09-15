package com.cslineups.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Lang
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.model.LineupVariant
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.InfoRow
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.UtilityBadge
import com.cslineups.app.ui.components.rememberDrawableId
import com.cslineups.app.ui.difficultyLabel
import com.cslineups.app.ui.label

private data class TeachingImage(
    val title: String,
    val icon: ImageVector,
    val resourceName: String,
)

@Composable
fun VariantDetailScreen(
    group: LineupGroup,
    variant: LineupVariant,
    lang: Lang,
    strings: Strings,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
) {
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    val images = listOf(
        TeachingImage(strings.startPosition, Icons.Filled.Person, variant.positionImageName),
        TeachingImage(strings.aimPoint, Icons.Filled.Place, variant.aimImageName),
        TeachingImage(strings.resultImage, Icons.Filled.CheckCircle, variant.resultImageName),
    )

    ScreenScaffold(
        title = variant.name.value(lang),
        onBack = onBack,
        actions = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                    tint = if (isFavorite) Color(0xFFFFD400) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { SectionTitle(strings.overview) }
            item {
                InfoCard {
                    InfoRow(strings.name, variant.name.value(lang))
                    InfoRow(strings.type, group.type.label(strings))
                    InfoRow(strings.side, group.side)
                    InfoRow(strings.category, group.category.label(strings))
                    InfoRow(strings.difficulty, variant.difficulty.difficultyLabel(strings))
                }
            }

            item { SectionTitle(strings.position) }
            item {
                InfoCard {
                    InfoRow(strings.startArea, variant.startArea.value(lang))
                    InfoRow(strings.targetArea, variant.targetArea.value(lang))
                    InfoRow(strings.spawnRequirement, variant.spawnRequirement.value(lang))
                }
            }

            item { SectionTitle(strings.lineupSteps) }
            item {
                InfoCard {
                    Text(variant.throwMethod.value(lang), style = MaterialTheme.typography.bodyMedium)
                }
            }

            item { SectionTitle(strings.teachingImages) }
            items(images.size) { index ->
                TeachingImageCard(
                    item = images[index],
                    placeholder = strings.placeholder,
                    onClick = { previewIndex = index },
                )
            }

            item { SectionTitle(strings.notes) }
            item {
                InfoCard {
                    Text(variant.description.value(lang), style = MaterialTheme.typography.bodyMedium)
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    previewIndex?.let { initial ->
        ImagePreviewDialog(
            images = images,
            initialIndex = initial,
            strings = strings,
            onDismiss = { previewIndex = null },
        )
    }
}

@Composable
private fun InfoCard(content: @Composable () -> Unit) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun TeachingImageCard(
    item: TeachingImage,
    placeholder: String,
    onClick: () -> Unit,
) {
    val drawableId = rememberDrawableId(item.resourceName)
    Card(
        onClick = onClick,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(item.icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(item.title, style = MaterialTheme.typography.titleSmall)
            }
            if (drawableId != 0) {
                androidx.compose.foundation.Image(
                    painter = painterResource(drawableId),
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImagePreviewDialog(
    images: List<TeachingImage>,
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
                ZoomableImage(images[page], strings.placeholder)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = strings.close,
                        tint = Color.White,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    images.getOrNull(pagerState.currentPage)?.title.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun ZoomableImage(item: TeachingImage, placeholder: String) {
    val drawableId = rememberDrawableId(item.resourceName)
    if (drawableId == 0) {
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
            .pointerInput(drawableId) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset = if (scale <= 1f) Offset.Zero else offset + pan
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(drawableId),
            contentDescription = item.title,
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

