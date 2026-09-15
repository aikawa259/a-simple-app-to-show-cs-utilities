package com.cslineups.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.ui.components.AddIconButton
import com.cslineups.app.ui.components.ConfirmDialog
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.GlassOverlay
import com.cslineups.app.ui.components.GlassPrimaryButton
import com.cslineups.app.ui.components.GlassSectionLabel
import com.cslineups.app.ui.components.GlassTextButton
import com.cslineups.app.ui.components.GlowFab
import com.cslineups.app.ui.components.GlowIconButton
import com.cslineups.app.ui.components.EntryCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.PressableCard
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SmallShape
import com.cslineups.app.ui.components.TextEditDialog
import com.cslineups.app.ui.components.UtilityBadge
import com.cslineups.app.ui.components.glassTextFieldColors
import com.cslineups.app.ui.components.glassBackdropBlur
import com.cslineups.app.ui.components.pressScale
import kotlinx.coroutines.delay

/**
 * 地图首页：左上角加号新建地图，标题点开地图列表，
 * 主体是道具列表 / 搜索 / 收藏三个入口，右下角加号添加道具。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapHomeScreen(
    map: CsMap?,
    maps: List<CsMap>,
    strings: Strings,
    onSelectMap: (String) -> Unit,
    onCreateMap: (String) -> Unit,
    onRenameMap: (String, String) -> Unit,
    onDeleteMap: (String) -> Unit,
    onOpenList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
    onAddItem: (String, String) -> Unit,
) {
    var showMapList by remember { mutableStateOf(false) }
    var showCreateMap by remember { mutableStateOf(false) }
    var renamingMap by remember { mutableStateOf<CsMap?>(null) }
    var deletingMap by remember { mutableStateOf<CsMap?>(null) }
    var showAddItem by remember { mutableStateOf(false) }

    // 任一弹层打开时把后面的内容模糊掉，形成"液态玻璃"的层次
    val overlayOpen = showCreateMap || renamingMap != null || deletingMap != null || showAddItem

    Box(Modifier.fillMaxSize()) {
    ScreenScaffold(
        title = map?.name.orEmpty(),
        navigationIcon = {
            AddIconButton(contentDescription = strings.newMap) { showCreateMap = true }
        },
        titleContent = {
            if (map != null) {
                MapTitle(
                    name = map.name,
                    isExample = map.isExample,
                    exampleLabel = strings.exampleMap,
                    onClick = { showMapList = true },
                )
            } else {
                Text(strings.appName, fontWeight = FontWeight.SemiBold)
            }
        },
        actions = {
            GlowIconButton(
                icon = Icons.Filled.Settings,
                contentDescription = strings.settings,
                onClick = onOpenSettings,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        floatingActionButton = {
            if (map != null) {
                GlowFab(
                    icon = Icons.Filled.Add,
                    contentDescription = strings.addItem,
                    onClick = { showAddItem = true },
                )
            }
        },
        // 首页是开屏看到的第一个页面，不做旋转/甩字动效，免得盖住系统启动动画
        entranceAnimation = false,
        modifier = Modifier.glassBackdropBlur(overlayOpen),
    ) { padding ->
        if (map == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyState(
                        icon = Icons.Filled.Add,
                        title = strings.emptyMaps,
                        message = strings.emptyMapsMessage,
                    )
                    TextButton(onClick = { showCreateMap = true }) { Text(strings.newMap) }
                }
            }
            return@ScreenScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(PagePadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StaggeredAppear(index = 0) {
                EntryCard(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = strings.utilityList,
                    subtitle = strings.listSubtitle,
                    iconColor = Color(0xFF34C759),
                    onClick = onOpenList,
                )
            }
            StaggeredAppear(index = 1) {
                EntryCard(
                    icon = Icons.Filled.Search,
                    title = strings.search,
                    subtitle = strings.searchSubtitle,
                    iconColor = Color(0xFFFF9F0A),
                    onClick = onOpenSearch,
                )
            }
            StaggeredAppear(index = 2) {
                EntryCard(
                    icon = Icons.Filled.Star,
                    title = strings.favorites,
                    subtitle = strings.favoritesSubtitle,
                    iconColor = Color(0xFFFFD400),
                    onClick = onOpenFavorites,
                )
            }

            Text(
                strings.itemCount(map.items.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }

    if (showCreateMap) {
        TextEditDialog(
            title = strings.newMap,
            label = strings.mapName,
            initialValue = "",
            confirmText = strings.create,
            cancelText = strings.cancel,
            requireNonBlank = true,
            onConfirm = { name ->
                onCreateMap(name)
                showCreateMap = false
            },
            onDismiss = { showCreateMap = false },
        )
    }

    if (showMapList) {
        ModalBottomSheet(onDismissRequest = { showMapList = false }) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    strings.maps,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = PagePadding, bottom = 8.dp),
                )
                LazyColumn {
                    items(maps, key = { it.id }) { item ->
                        MapRow(
                            map = item,
                            selected = item.id == map?.id,
                            exampleLabel = strings.exampleMap,
                            itemCount = strings.itemCount(item.items.size),
                            onSelect = {
                                onSelectMap(item.id)
                                showMapList = false
                            },
                            onRename = {
                                renamingMap = item
                                showMapList = false
                            },
                            onDelete = {
                                deletingMap = item
                                showMapList = false
                            },
                        )
                    }
                }
                TextButton(
                    onClick = {
                        showMapList = false
                        showCreateMap = true
                    },
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.newMap)
                }
            }
        }
    }

    renamingMap?.let { target ->
        TextEditDialog(
            title = strings.renameMap,
            label = strings.mapName,
            initialValue = target.name,
            confirmText = strings.save,
            cancelText = strings.cancel,
            requireNonBlank = true,
            onConfirm = { name ->
                onRenameMap(target.id, name)
                renamingMap = null
            },
            onDismiss = { renamingMap = null },
        )
    }

    deletingMap?.let { target ->
        ConfirmDialog(
            title = strings.deleteConfirmTitle,
            message = strings.deleteMapConfirm,
            confirmText = strings.delete,
            cancelText = strings.cancel,
            onConfirm = {
                onDeleteMap(target.id)
                deletingMap = null
            },
            onDismiss = { deletingMap = null },
        )
    }

    if (showAddItem) {
        var itemName by remember { mutableStateOf("") }
        var targetPoint by remember { mutableStateOf("") }

        GlassOverlay(onDismiss = { showAddItem = false }) {
            GlassSectionLabel(strings.addItem)
            Spacer(Modifier.height(4.dp))
            Text(
                strings.autosaveHint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text(strings.itemName) },
                singleLine = true,
                shape = SmallShape,
                colors = glassTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = targetPoint,
                onValueChange = { targetPoint = it },
                label = { Text(strings.targetPoint) },
                singleLine = true,
                shape = SmallShape,
                colors = glassTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassTextButton(
                    text = strings.cancel,
                    onClick = { showAddItem = false },
                )
                Spacer(Modifier.width(10.dp))
                GlassPrimaryButton(
                    text = strings.createAndEdit,
                    enabled = itemName.isNotBlank(),
                    onClick = {
                        onAddItem(itemName.trim(), targetPoint.trim())
                        showAddItem = false
                    },
                )
            }
        }
    }
    }
}

/** 标题：地图名 + 小箭头，点开地图列表。 */
@Composable
private fun MapTitle(
    name: String,
    isExample: Boolean,
    exampleLabel: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .pressScale(interaction, pressedScale = 0.94f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
        if (isExample) {
            Spacer(Modifier.width(6.dp))
            UtilityBadge(exampleLabel, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MapRow(
    map: CsMap,
    selected: Boolean,
    exampleLabel: String,
    itemCount: String,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    PressableCard(
        onClick = onSelect,
        modifier = Modifier.padding(horizontal = PagePadding, vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        shape = SmallShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = map.name.take(1).uppercase(),
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(map.name, style = MaterialTheme.typography.titleSmall)
                    if (map.isExample) {
                        Spacer(Modifier.width(6.dp))
                        UtilityBadge(exampleLabel, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text(
                    itemCount,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            GlowIconButton(
                icon = Icons.Filled.Edit,
                contentDescription = null,
                onClick = onRename,
            )
            GlowIconButton(
                icon = Icons.Filled.Delete,
                contentDescription = null,
                onClick = onDelete,
                glowColor = MaterialTheme.colorScheme.error,
            )
        }
    }
}
