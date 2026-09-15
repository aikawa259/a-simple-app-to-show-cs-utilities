package com.cslineups.app.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cslineups.app.i18n.ZhStrings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.LineupItem
import com.cslineups.app.ui.theme.CsLineupsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class MapHomeScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val map = CsMap(
        id = "map-1",
        name = "我的地图",
        items = listOf(LineupItem(id = "i1", name = "B 点烟", targetArea = "B 包点")),
    )

    private fun renderHome(
        currentMap: CsMap? = map,
        maps: List<CsMap> = listOf(map),
        onCreateMap: (String) -> Unit = {},
    ) {
        compose.setContent {
            CsLineupsTheme {
                MapHomeScreen(
                    map = currentMap,
                    maps = maps,
                    strings = ZhStrings,
                    onSelectMap = {},
                    onCreateMap = onCreateMap,
                    onRenameMap = { _, _ -> },
                    onDeleteMap = {},
                    onOpenList = {},
                    onOpenSearch = {},
                    onOpenFavorites = {},
                    onOpenSettings = {},
                    onAddItem = { _, _ -> },
                )
            }
        }
    }

    @Test
    fun `地图页只有三个入口`() {
        renderHome()

        compose.onNodeWithText("道具列表").assertExists()
        compose.onNodeWithText("搜索").assertExists()
        compose.onNodeWithText("收藏").assertExists()
        // 旧的 2D 战术地图入口不应再出现
        compose.onAllNodesWithText("2D 战术地图").assertCountEquals(0)
    }

    @Test
    fun `地图页有底部加号用于添加道具`() {
        renderHome()
        compose.onNodeWithContentDescription("添加道具").assertExists()
    }

    // 说明：不在这里测"点开弹窗"的交互。Robolectric 下弹窗打开后 Compose
    // 无法回到空闲状态（会一直等到 60 秒超时），属于测试环境限制；
    // 新建地图 / 添加道具的逻辑由 MapRepositoryTest 覆盖，弹窗本身在真机验证。
    @Test
    fun `没有地图时显示创建引导`() {
        renderHome(currentMap = null, maps = emptyList())

        compose.onNodeWithText("还没有地图").assertExists()
    }
}
