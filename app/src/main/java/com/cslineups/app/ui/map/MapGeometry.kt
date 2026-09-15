package com.cslineups.app.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

/**
 * 地图坐标换算。算法与 iOS 版 TacticalMapView 保持一致：
 * 先把图片按宽高比居中放进容器，再把 0–1 的归一化坐标映射到该矩形内。
 */
fun fittedRect(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float,
): Rect {
    if (imageWidth <= 0f || imageHeight <= 0f || containerWidth <= 0f || containerHeight <= 0f) {
        return Rect(Offset.Zero, Size.Zero)
    }

    val imageRatio = imageWidth / imageHeight
    val containerRatio = containerWidth / containerHeight
    val fittedWidth: Float
    val fittedHeight: Float
    if (imageRatio > containerRatio) {
        fittedWidth = containerWidth
        fittedHeight = containerWidth / imageRatio
    } else {
        fittedHeight = containerHeight
        fittedWidth = containerHeight * imageRatio
    }

    return Rect(
        offset = Offset(
            x = (containerWidth - fittedWidth) / 2f,
            y = (containerHeight - fittedHeight) / 2f,
        ),
        size = Size(fittedWidth, fittedHeight),
    )
}

fun pointLocation(fitted: Rect, normalized: Offset): Offset = Offset(
    x = fitted.left + fitted.width * normalized.x,
    y = fitted.top + fitted.height * normalized.y,
)

/** 标记的左上角：以点位为中心对齐。 */
fun markerTopLeft(position: Offset, markerSizePx: Float): Offset = Offset(
    x = position.x - markerSizePx / 2f,
    y = position.y - markerSizePx / 2f,
)

/** 平移边界：放大后不允许把地图拖出可视区；缩回 1 倍时重新居中。 */
fun clampOffset(
    offset: Offset,
    scale: Float,
    fitted: Rect,
    containerWidth: Float,
    containerHeight: Float,
): Offset {
    val scaledWidth = fitted.width * scale
    val scaledHeight = fitted.height * scale

    val minX = containerWidth - (fitted.left + fitted.width) * scale
    val maxX = -fitted.left * scale
    val minY = containerHeight - (fitted.top + fitted.height) * scale
    val maxY = -fitted.top * scale

    val x = if (minX > maxX) {
        (containerWidth - scaledWidth) / 2f - fitted.left * scale
    } else {
        offset.x.coerceIn(minX, maxX)
    }
    val y = if (minY > maxY) {
        (containerHeight - scaledHeight) / 2f - fitted.top * scale
    } else {
        offset.y.coerceIn(minY, maxY)
    }
    return Offset(x, y)
}

/** 点位聚合阈值，按缩放级别取值（与 iOS 版一致）。 */
fun clusterDistance(scale: Float): Float = when {
    scale < 1.5f -> 92f
    scale < 2f -> 76f
    scale < 3f -> 52f
    else -> 36f
}

/**
 * 一组需要合并显示的点位。
 *
 * [indices] 指向传入的点位列表，[center] 始终是**归一化坐标**（0–1）：
 * 渲染时再用 [pointLocation] 换成像素，避免像素与归一化坐标混用。
 */
data class PointCluster(val indices: List<Int>, val center: Offset)

fun clusterPoints(fitted: Rect, points: List<Offset>, scale: Float): List<PointCluster> {
    val limit = clusterDistance(scale)
    val clusters = mutableListOf<PointCluster>()

    points.forEachIndexed { index, point ->
        val pointPx = pointLocation(fitted, point)
        val found = clusters.indexOfFirst { cluster ->
            (pointLocation(fitted, cluster.center) - pointPx).getDistance() * maxOf(scale, 1f) <= limit
        }

        if (found >= 0) {
            val existing = clusters[found]
            val count = existing.indices.size
            clusters[found] = PointCluster(
                indices = existing.indices + index,
                center = Offset(
                    x = (existing.center.x * count + point.x) / (count + 1),
                    y = (existing.center.y * count + point.y) / (count + 1),
                ),
            )
        } else {
            clusters += PointCluster(indices = listOf(index), center = point)
        }
    }
    return clusters
}
