package com.cslineups.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cslineups.app.R
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.Lang
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.model.LineupVariant
import com.cslineups.app.model.UtilityType
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.UtilityBadge
import com.cslineups.app.ui.components.copyToClipboard
import com.cslineups.app.ui.label
import com.cslineups.app.ui.map.clampOffset
import com.cslineups.app.ui.map.clusterPoints
import com.cslineups.app.ui.map.fittedRect
import com.cslineups.app.ui.map.markerTopLeft
import com.cslineups.app.ui.map.pointLocation
import com.cslineups.app.ui.theme.color
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f

/** 地图点位标记的测试标识。 */
internal const val MAP_MARKER_TAG = "map-marker"

/** 开发者模式下可拖动点位的测试标识。 */
internal const val DEV_MARKER_TAG = "dev-marker"

@Composable
fun TacticalMapScreen(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    developerMode: Boolean,
    onOpenGroup: (String) -> Unit,
    onBack: () -> Unit,
) {
    var areaFilter by remember { mutableStateOf(MapAreaFilter.FEATURED) }
    var typeFilter by remember { mutableStateOf<UtilityType?>(null) }
    var clusterSelection by remember { mutableStateOf<List<LineupGroup>?>(null) }
    var lastEdited by remember { mutableStateOf<EditedPoint?>(null) }
    var status by remember { mutableStateOf<String?>(null) }
    var showTargets by remember { mutableStateOf(true) }
    var showStarts by remember { mutableStateOf(true) }
    var showLines by remember { mutableStateOf(true) }
    val edits = remember { mutableStateMapOf<String, Offset>() }

    val context = LocalContext.current
    val filteredGroups = map.lineupGroups.filter { areaFilter.matches(it) }
        .filter { typeFilter == null || it.type == typeFilter }

    ScreenScaffold(title = map.name.value(lang), onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = PagePadding),
        ) {
            ChipRow(
                title = strings.areaFilter,
                labels = MapAreaFilter.entries.map { it.label(strings) },
                selectedIndex = MapAreaFilter.entries.indexOf(areaFilter),
            ) { areaFilter = MapAreaFilter.entries[it] }

            ChipRow(
                title = strings.utilityTypeFilter,
                labels = typeOptions(strings),
                selectedIndex = typeOptions(strings).indexOfFirst { label -> label == selectedTypeLabel(typeFilter, strings) },
            ) { index ->
                typeFilter = if (index == 0) null else UtilityType.entries[index - 1]
            }

            MapCanvas(
                map = map,
                lang = lang,
                strings = strings,
                groups = filteredGroups,
                developerMode = developerMode,
                showTargets = showTargets,
                showStarts = showStarts,
                showLines = showLines,
                edits = edits,
                onOpenGroup = onOpenGroup,
                onSelectCluster = { clusterSelection = it },
                onEdited = { lastEdited = it; status = null },
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp),
            )

            Text(
                text = if (developerMode) strings.developerHint else strings.tapHint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )

            if (developerMode) {
                DeveloperPanel(
                    strings = strings,
                    lastEdited = lastEdited,
                    status = status,
                    showTargets = showTargets,
                    showStarts = showStarts,
                    showLines = showLines,
                    onShowTargets = { showTargets = it },
                    onShowStarts = { showStarts = it },
                    onShowLines = { showLines = it },
                    onCopyCoordinates = {
                        lastEdited?.let {
                            copyToClipboard(context, it.text)
                            status = strings.coordinatesCopied
                        }
                    },
                    onCopyJson = {
                        copyToClipboard(context, buildJson(map, edits))
                        status = strings.jsonCopied
                    },
                )
            }
        }
    }

    clusterSelection?.let { cluster ->
        ClusterSheet(
            cluster = cluster,
            lang = lang,
            strings = strings,
            onDismiss = { clusterSelection = null },
            onOpenGroup = {
                clusterSelection = null
                onOpenGroup(it)
            },
        )
    }
}

private fun typeOptions(strings: Strings): List<String> =
    listOf(strings.filterAll) + UtilityType.entries.map { it.label(strings) }

private fun selectedTypeLabel(type: UtilityType?, strings: Strings): String =
    type?.label(strings) ?: strings.filterAll

@Composable
private fun ChipRow(
    title: String,
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(Modifier.padding(top = 6.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape,
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                )
            }
        }
    }
}

@Composable
private fun MapCanvas(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    groups: List<LineupGroup>,
    developerMode: Boolean,
    showTargets: Boolean,
    showStarts: Boolean,
    showLines: Boolean,
    edits: MutableMap<String, Offset>,
    onOpenGroup: (String) -> Unit,
    onSelectCluster: (List<LineupGroup>) -> Unit,
    onEdited: (EditedPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val density = LocalDensity.current
        val painter = painterResource(R.drawable.mirage_map)
        val imageSize = painter.intrinsicSize
        val containerWidth = with(density) { maxWidth.toPx() }
        val containerHeight = with(density) { maxHeight.toPx() }

        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val fitted = remember(containerWidth, containerHeight, imageSize) {
            fittedRect(imageSize.width, imageSize.height, containerWidth, containerHeight)
        }

        val applyTransform: (Offset, Float, Offset) -> Unit = { pan, zoom, centroid ->
            val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
            val actualZoom = newScale / scale
            val proposed = centroid - (centroid - offset) * actualZoom + pan
            scale = newScale
            offset = clampOffset(proposed, newScale, fitted, containerWidth, containerHeight)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .mapTransformGestures(developerMode, applyTransform),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                        transformOrigin = TransformOrigin(0f, 0f)
                    },
            ) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = map.name.value(lang),
                    modifier = Modifier
                        .offset { IntOffset(fitted.left.roundToInt(), fitted.top.roundToInt()) }
                        .size(
                            width = with(density) { fitted.width.toDp() },
                            height = with(density) { fitted.height.toDp() },
                        ),
                )

                if (groups.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Info,
                        title = strings.emptyUtilities,
                        message = strings.emptyUtilitiesMessage,
                    )
                } else if (developerMode) {
                    if (showLines) {
                        Canvas(Modifier.fillMaxSize()) {
                            groups.forEach { group ->
                                group.variants.forEach { variant ->
                                    val start = pointLocation(fitted, startCoordinate(variant, edits))
                                    val target = pointLocation(fitted, targetCoordinate(group, edits))
                                    drawLine(
                                        color = group.type.color.copy(alpha = 0.75f),
                                        start = start,
                                        end = target,
                                        strokeWidth = 2f,
                                    )
                                }
                            }
                        }
                    }

                    if (showTargets) {
                        groups.forEach { group ->
                            val coordinate = targetCoordinate(group, edits)
                            DevMarker(
                                group = group,
                                label = "${strings.targetPoint} ${formatCoordinate(coordinate)}",
                                normalized = coordinate,
                                fitted = fitted,
                                scale = scale,
                                dragKey = group.id,
                                onDrag = { next ->
                                    edits[group.id] = next
                                    onEdited(
                                        EditedPoint(
                                            groupId = group.id,
                                            variantId = null,
                                            displayName = group.targetName.value(lang),
                                            kind = PointKind.GROUP_TARGET,
                                            coordinate = next,
                                        ),
                                    )
                                },
                            )
                        }
                    }

                    if (showStarts) {
                        groups.forEach { group ->
                            group.variants.forEach { variant ->
                                val coordinate = startCoordinate(variant, edits)
                                DevMarker(
                                    group = group,
                                    label = "${strings.startPoint} ${formatCoordinate(coordinate)}",
                                    normalized = coordinate,
                                    fitted = fitted,
                                    scale = scale,
                                    dragKey = variant.id,
                                    onDrag = { next ->
                                        edits[variant.id] = next
                                        onEdited(
                                            EditedPoint(
                                                groupId = group.id,
                                                variantId = variant.id,
                                                displayName = variant.name.value(lang),
                                                kind = PointKind.VARIANT_START,
                                                coordinate = next,
                                            ),
                                        )
                                    },
                                )
                            }
                        }
                    }
                } else {
                    val touchSizePx = with(density) { 44.dp.toPx() }
                    val points = groups.map { targetCoordinate(it, edits) }

                    clusterPoints(fitted, points, scale).forEach { cluster ->
                        val clusterGroups = cluster.indices.map { groups[it] }
                        val topLeft = markerTopLeft(pointLocation(fitted, cluster.center), touchSizePx)
                        val markerModifier = Modifier
                            .offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
                            .size(44.dp)
                            .testTag(MAP_MARKER_TAG)

                        if (clusterGroups.size > 1) {
                            Box(
                                modifier = markerModifier.clickable { onSelectCluster(clusterGroups) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        clusterGroups.size.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        } else {
                            val group = clusterGroups.first()
                            Box(
                                modifier = markerModifier.clickable { onOpenGroup(group.id) },
                                contentAlignment = Alignment.Center,
                            ) {
                                com.cslineups.app.ui.components.UtilityDot(group.type, size = 30.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevMarker(
    group: LineupGroup,
    label: String,
    normalized: Offset,
    fitted: Rect,
    scale: Float,
    dragKey: String,
    onDrag: (Offset) -> Unit,
) {
    val current by rememberUpdatedState(normalized)
    val position = pointLocation(fitted, normalized)
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (position.x - 34f).roundToInt(),
                    (position.y - 24f).roundToInt(),
                )
            }
            .size(width = 68.dp, height = 48.dp)
            .testTag(DEV_MARKER_TAG)
            .pointerInput(dragKey, fitted, scale) {
                var base = Offset.Zero
                var total = Offset.Zero
                detectDragGestures(
                    onDragStart = {
                        base = current
                        total = Offset.Zero
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        total += amount
                        val dx = total.x / (fitted.width * scale)
                        val dy = total.y / (fitted.height * scale)
                        onDrag(
                            Offset(
                                (base.x + dx).coerceIn(0f, 1f),
                                (base.y + dy).coerceIn(0f, 1f),
                            ),
                        )
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            com.cslineups.app.ui.components.UtilityDot(group.type, size = 26.dp)
            Text(
                label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f), CardShape)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClusterSheet(
    cluster: List<LineupGroup>,
    lang: Lang,
    strings: Strings,
    onDismiss: () -> Unit,
    onOpenGroup: (String) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(PagePadding)) {
            Text(strings.clusteredUtilities, style = MaterialTheme.typography.titleMedium)
            cluster.forEach { group ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenGroup(group.id) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.cslineups.app.ui.components.UtilityDot(group.type, size = 30.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(group.targetName.value(lang), style = MaterialTheme.typography.titleSmall)
                        Text(
                            "${group.type.label(strings)} · ${strings.variantCount(group.variants.size)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeveloperPanel(
    strings: Strings,
    lastEdited: EditedPoint?,
    status: String?,
    showTargets: Boolean,
    showStarts: Boolean,
    showLines: Boolean,
    onShowTargets: (Boolean) -> Unit,
    onShowStarts: (Boolean) -> Unit,
    onShowLines: (Boolean) -> Unit,
    onCopyCoordinates: () -> Unit,
    onCopyJson: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, CardShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ToggleRow(strings.targetPoints, showTargets, onShowTargets)
        ToggleRow(strings.variantStartPoints, showStarts, onShowStarts)
        ToggleRow(strings.lineConnections, showLines, onShowLines)

        Text(
            if (status == null) strings.liveCoordinates else strings.lastEdited,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (lastEdited == null) {
            Text(strings.noEditedCoordinate, style = MaterialTheme.typography.bodySmall)
        } else {
            Text(
                "${lastEdited.displayName} · ${lastEdited.kind.label(strings)}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(lastEdited.text, style = MaterialTheme.typography.bodySmall, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
        if (status != null) {
            Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCopyCoordinates) { Text(strings.copyCoordinates) }
            Button(onClick = onCopyJson) { Text(strings.copyJson) }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

/* ------------------------------------------------------------------
   坐标换算与聚合（与 iOS 版算法保持一致）
   ------------------------------------------------------------------ */

private enum class PointKind(val xField: String, val yField: String) {
    GROUP_TARGET("targetMapX", "targetMapY"),
    VARIANT_START("startMapX", "startMapY");

    fun label(strings: Strings): String = when (this) {
        GROUP_TARGET -> strings.targetPoint
        VARIANT_START -> strings.startPoint
    }
}

private data class EditedPoint(
    val groupId: String,
    val variantId: String?,
    val displayName: String,
    val kind: PointKind,
    val coordinate: Offset,
) {
    val text: String
        get() = "%s: %.3f, %s: %.3f".format(
            kind.xField,
            coordinate.x,
            kind.yField,
            coordinate.y,
        )
}

private fun targetCoordinate(group: LineupGroup, edits: Map<String, Offset>): Offset =
    edits[group.id] ?: Offset(group.targetMapX.toFloat(), group.targetMapY.toFloat())

private fun startCoordinate(variant: LineupVariant, edits: Map<String, Offset>): Offset =
    edits[variant.id] ?: Offset(variant.startMapX.toFloat(), variant.startMapY.toFloat())

private fun formatCoordinate(coordinate: Offset): String =
    "%.3f, %.3f".format(coordinate.x, coordinate.y)

/** 与 iOS 版一致，导出可直接粘回数据文件的 JSON。 */
private fun buildJson(map: CsMap, edits: Map<String, Offset>): String {
    val groups = JSONArray()
    map.lineupGroups.forEach { group ->
        val target = targetCoordinate(group, edits)
        val variants = JSONArray()
        group.variants.forEach { variant ->
            val start = startCoordinate(variant, edits)
            variants.put(
                JSONObject()
                    .put("id", variant.id)
                    .put("startMapX", rounded(start.x))
                    .put("startMapY", rounded(start.y))
                    .put("targetMapX", rounded(target.x))
                    .put("targetMapY", rounded(target.y)),
            )
        }
        groups.put(
            JSONObject()
                .put("id", group.id)
                .put("targetMapX", rounded(target.x))
                .put("targetMapY", rounded(target.y))
                .put("variants", variants),
        )
    }
    return groups.toString(2)
}

private fun rounded(value: Float): Double = (value * 1000f).roundToInt() / 1000.0

private enum class MapAreaFilter {
    FEATURED,
    A_SITE,
    B_SITE,
    MID,
    T_SIDE,
    CT_SIDE;

    fun label(strings: Strings): String = when (this) {
        FEATURED -> strings.filterFeatured
        A_SITE -> strings.categoryASite
        B_SITE -> strings.categoryBSite
        MID -> strings.categoryMid
        T_SIDE -> strings.categoryTSide
        CT_SIDE -> strings.categoryCTSide
    }

    fun matches(group: LineupGroup): Boolean = when (this) {
        FEATURED -> group.isFeatured
        A_SITE -> group.category == LineupCategory.A_SITE
        B_SITE -> group.category == LineupCategory.B_SITE
        MID -> group.category == LineupCategory.MID
        T_SIDE -> group.category == LineupCategory.T_SIDE
        CT_SIDE -> group.category == LineupCategory.CT_SIDE
    }
}

/**
 * 平移与缩放。非开发者模式单指即可平移；开发者模式需要双指，
 * 这样单指才能用来拖动点位（与 iOS 版 minimumNumberOfTouches 的处理一致）。
 */
private fun Modifier.mapTransformGestures(
    developerMode: Boolean,
    onTransform: (pan: Offset, zoom: Float, centroid: Offset) -> Unit,
): Modifier = pointerInput(developerMode) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val pressedCount = event.changes.count { it.pressed }
            if (!developerMode || pressedCount >= 2) {
                val zoom = event.calculateZoom()
                val pan = event.calculatePan()
                if (zoom != 1f || pan != Offset.Zero) {
                    onTransform(pan, zoom, event.calculateCentroid())
                    event.changes.forEach { if (it.positionChanged()) it.consume() }
                }
            }
        } while (event.changes.any { it.pressed })
    }
}
