package com.cslineups.app.ui.screens

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cslineups.app.data.LineupRepository
import com.cslineups.app.i18n.stringsFor
import com.cslineups.app.model.Lang
import com.cslineups.app.ui.theme.CsLineupsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * 在电脑上把地图页真正渲染一遍，确认数据解析、筛选与点位定位都正常。
 *
 * 之前出现过「地图上一个点都没有」的问题：点位被换算到屏幕外几万像素处，
 * 界面看起来正常但什么都看不到。这个测试就是防止它再发生。
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class TacticalMapScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val map = LineupRepository.loadMirage(ApplicationProvider.getApplicationContext<Context>())

    private fun renderMap(developerMode: Boolean = false) {
        compose.setContent {
            CsLineupsTheme {
                TacticalMapScreen(
                    map = map,
                    lang = Lang.ZH_HANS,
                    strings = stringsFor(Lang.ZH_HANS),
                    developerMode = developerMode,
                    onOpenGroup = {},
                    onBack = {},
                )
            }
        }
    }

    @Test
    fun `默认筛选下地图上能看到全部三个点位`() {
        renderMap()

        compose.onAllNodesWithTag(MAP_MARKER_TAG).assertCountEquals(3)
    }

    @Test
    fun `每个点位都真的显示在屏幕上`() {
        renderMap()

        // assertIsDisplayed 会检查节点是否真的落在可视区内；
        // 坐标换算错位时节点报告的可见区域是 0×0，这里就会失败。
        repeat(3) { index ->
            compose.onAllNodesWithTag(MAP_MARKER_TAG)[index].assertIsDisplayed()
        }
    }

    @Test
    fun `开发者模式下列出目标点与站位点`() {
        renderMap(developerMode = true)

        // 3 个目标点 + 6 个站位点
        compose.onAllNodesWithTag(DEV_MARKER_TAG).assertCountEquals(9)
    }
}
