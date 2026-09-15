package com.cslineups.app.ui.map

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 基准值来自 docs/map-math-probe 在浏览器里的实测结果
 * （图片 1206×1214 放入 900×809 容器，实测适配矩形为 x48 y0 803×809）。
 */
class MapGeometryTest {

    private val imageWidth = 1206f
    private val imageHeight = 1214f

    @Test
    fun `宽容器里按高度适配并水平居中`() {
        val fitted = fittedRect(imageWidth, imageHeight, 900f, 809f)

        assertEquals(48.17f, fitted.left, 0.5f)
        assertEquals(0f, fitted.top, 0.5f)
        assertEquals(803.67f, fitted.width, 1f)
        assertEquals(809f, fitted.height, 0.5f)
    }

    @Test
    fun `高容器里按宽度适配并垂直居中`() {
        val fitted = fittedRect(imageWidth, imageHeight, 412f, 600f)

        assertEquals(0f, fitted.left, 0.5f)
        assertEquals(92.63f, fitted.top, 0.5f)
        assertEquals(412f, fitted.width, 0.5f)
        assertEquals(414.73f, fitted.height, 1f)
    }

    @Test
    fun `归一化坐标映射到适配矩形内`() {
        val fitted = fittedRect(imageWidth, imageHeight, 900f, 809f)

        // 左上角与右下角
        val topLeft = pointLocation(fitted, Offset(0f, 0f))
        assertEquals(48.17f, topLeft.x, 0.5f)
        assertEquals(0f, topLeft.y, 0.5f)

        val bottomRight = pointLocation(fitted, Offset(1f, 1f))
        assertEquals(851.84f, bottomRight.x, 0.5f)
        assertEquals(809f, bottomRight.y, 0.5f)

        // 数据里的 VIP 烟目标点
        val windowSmoke = pointLocation(fitted, Offset(0.46f, 0.36f))
        assertEquals(417.86f, windowSmoke.x, 0.5f)
        assertEquals(291.24f, windowSmoke.y, 0.5f)
    }

    @Test
    fun `一倍缩放时地图居中不偏移`() {
        val fitted = fittedRect(imageWidth, imageHeight, 900f, 809f)
        val clamped = clampOffset(Offset(120f, -80f), 1f, fitted, 900f, 809f)

        assertEquals(0f, clamped.x, 0.5f)
        assertEquals(0f, clamped.y, 0.5f)
    }

    @Test
    fun `两倍缩放时平移被限制在边界内`() {
        val fitted = fittedRect(imageWidth, imageHeight, 900f, 809f)
        val clamped = clampOffset(Offset.Zero, 2f, fitted, 900f, 809f)

        // 水平方向：2 倍后地图比容器宽，左边界被限制在 -96.34
        assertEquals(-96.34f, clamped.x, 0.5f)
        // 垂直方向：适配矩形本身就占满容器高度，零点即合法边界
        assertEquals(0f, clamped.y, 0.5f)
    }

    @Test
    fun `聚合阈值随缩放分级`() {
        assertEquals(92f, clusterDistance(1f), 0f)
        assertEquals(76f, clusterDistance(1.5f), 0f)
        assertEquals(52f, clusterDistance(2f), 0f)
        assertEquals(36f, clusterDistance(3f), 0f)
    }
}
