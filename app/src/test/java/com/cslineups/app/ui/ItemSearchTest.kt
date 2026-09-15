package com.cslineups.app.ui

import com.cslineups.app.i18n.ZhStrings
import com.cslineups.app.model.Difficulty
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupItem
import com.cslineups.app.model.UtilityType
import org.junit.Assert.assertEquals
import org.junit.Test

class ItemSearchTest {

    private val items = listOf(
        LineupItem(
            id = "1",
            name = "VIP 烟",
            type = UtilityType.SMOKE,
            category = LineupCategory.MID,
            startArea = "匪家",
            targetArea = "VIP",
            throwSteps = "跳投",
        ),
        LineupItem(
            id = "2",
            name = "A 点火",
            type = UtilityType.MOLOTOV,
            category = LineupCategory.A_SITE,
            difficulty = Difficulty.HARD,
            startArea = "A Ramp",
            targetArea = "A 大坑",
        ),
    )

    @Test
    fun `按名称搜索`() {
        assertEquals(listOf("1"), searchItems(items, "VIP", ZhStrings).map { it.id })
    }

    @Test
    fun `按位置搜索`() {
        assertEquals(listOf("2"), searchItems(items, "大坑", ZhStrings).map { it.id })
    }

    @Test
    fun `按类型与难度搜索`() {
        assertEquals(listOf("2"), searchItems(items, "火", ZhStrings).map { it.id })
        assertEquals(listOf("2"), searchItems(items, "困难", ZhStrings).map { it.id })
    }

    @Test
    fun `忽略大小写与空格`() {
        assertEquals(listOf("2"), searchItems(items, "a ramp", ZhStrings).map { it.id })
        assertEquals(listOf("2"), searchItems(items, "aramp", ZhStrings).map { it.id })
    }

    @Test
    fun `空查询返回空结果`() {
        assertEquals(emptyList<String>(), searchItems(items, "   ", ZhStrings).map { it.id })
    }
}
