package com.cslineups.app.ui

import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.LineupItem

/**
 * 搜索：忽略大小写与空格，名称 / 位置 / 步骤 / 说明 / 类型 / 分类 / 阵营 / 难度 一起匹配。
 */
fun searchItems(items: List<LineupItem>, query: String, strings: Strings): List<LineupItem> {
    val normalizedQuery = query.trim().lowercase()
    if (normalizedQuery.isEmpty()) return emptyList()
    val compactQuery = normalizedQuery.filterNot { it.isWhitespace() }

    return items.filter { item ->
        val terms = listOf(
            item.name,
            item.startArea,
            item.targetArea,
            item.throwSteps,
            item.notes,
            item.side,
            item.type.label(strings),
            item.type.name,
            item.category.label(strings),
            item.difficulty.label(strings),
            item.difficulty.raw,
        )

        terms.any { term ->
            val normalizedTerm = term.trim().lowercase()
            normalizedTerm.contains(normalizedQuery) ||
                normalizedTerm.filterNot { it.isWhitespace() }.contains(compactQuery)
        }
    }
}
