package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
import com.cslineups.app.ui.components.UtilityDot
import com.cslineups.app.ui.difficultyLabel
import com.cslineups.app.ui.label

@Composable
fun SearchScreen(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    onOpenGroup: (String) -> Unit,
    onOpenVariant: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query, map) { searchLineups(map, query) }

    ScreenScaffold(title = strings.search, onBack = onBack) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(strings.searchPrompt) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth().padding(PagePadding),
            )

            when {
                query.isBlank() -> Text(
                    strings.searchHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = PagePadding),
                )

                results.isEmpty() -> EmptyState(Icons.Filled.Search, strings.searchNoResults)

                else -> LazyColumn(
                    contentPadding = PaddingValues(PagePadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(results, key = { it.key }) { result ->
                        SearchRow(result, lang, strings) {
                            when (result) {
                                is SearchResult.GroupResult -> onOpenGroup(result.group.id)
                                is SearchResult.VariantResult ->
                                    onOpenVariant(result.group.id, result.variant.id)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchRow(
    result: SearchResult,
    lang: Lang,
    strings: Strings,
    onClick: () -> Unit,
) {
    val group = result.group
    val title = when (result) {
        is SearchResult.GroupResult -> group.targetName.value(lang)
        is SearchResult.VariantResult -> result.variant.name.value(lang)
    }
    val kind = when (result) {
        is SearchResult.GroupResult -> strings.searchResultGroup
        is SearchResult.VariantResult -> strings.searchResultVariant
    }
    val subtitle = when (result) {
        is SearchResult.GroupResult -> listOf(
            group.type.label(strings),
            group.category.label(strings),
            group.side,
        ).joinToString(" · ")

        is SearchResult.VariantResult -> listOf(
            group.targetName.value(lang),
            result.variant.startArea.value(lang),
            result.variant.targetArea.value(lang),
            result.variant.difficulty.difficultyLabel(strings),
        ).joinToString(" · ")
    }

    Card(
        onClick = onClick,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UtilityDot(group.type, size = 28.dp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        kind,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private sealed interface SearchResult {
    val key: String
    val group: LineupGroup

    data class GroupResult(override val group: LineupGroup) : SearchResult {
        override val key: String = group.id
    }

    data class VariantResult(
        override val group: LineupGroup,
        val variant: LineupVariant,
    ) : SearchResult {
        override val key: String = "${group.id}-${variant.id}"
    }
}

/** 与 iOS 版一致：忽略大小写与空格，中英文字段一起匹配。 */
private fun searchLineups(map: CsMap, query: String): List<SearchResult> {
    val normalizedQuery = query.trim().lowercase()
    if (normalizedQuery.isEmpty()) return emptyList()
    val compactQuery = normalizedQuery.filterNot { it.isWhitespace() }

    fun matches(terms: List<String>): Boolean = terms.any { term ->
        val normalizedTerm = term.trim().lowercase()
        normalizedTerm.contains(normalizedQuery) ||
            normalizedTerm.filterNot { it.isWhitespace() }.contains(compactQuery)
    }

    val results = mutableListOf<SearchResult>()
    map.lineupGroups.forEach { group ->
        val groupTerms = listOf(
            group.id,
            group.mapId,
            group.targetName.en,
            group.targetName.zhHans,
            group.type.name,
            group.side,
        ) + group.type.searchTerms + group.category.searchTerms

        if (matches(groupTerms)) results += SearchResult.GroupResult(group)

        group.variants.forEach { variant ->
            val variantTerms = listOf(
                variant.id,
                variant.name.en,
                variant.name.zhHans,
                variant.spawnRequirement.en,
                variant.spawnRequirement.zhHans,
                variant.startArea.en,
                variant.startArea.zhHans,
                variant.targetArea.en,
                variant.targetArea.zhHans,
                variant.throwMethod.en,
                variant.throwMethod.zhHans,
                variant.description.en,
                variant.description.zhHans,
                variant.difficulty,
            )
            if (matches(variantTerms)) results += SearchResult.VariantResult(group, variant)
        }
    }
    return results
}

private val UtilityType.searchTerms: List<String>
    get() = when (this) {
        UtilityType.SMOKE -> listOf("Smoke", "烟")
        UtilityType.FLASH -> listOf("Flash", "闪")
        UtilityType.MOLOTOV -> listOf("Molotov", "火")
        UtilityType.HE -> listOf("HE", "Grenade", "雷")
    }

private val LineupCategory.searchTerms: List<String>
    get() = when (this) {
        LineupCategory.A_SITE -> listOf("A Site", "A 包点")
        LineupCategory.B_SITE -> listOf("B Site", "B 包点")
        LineupCategory.MID -> listOf("Mid", "中路")
        LineupCategory.T_SIDE -> listOf("T Side", "T 方")
        LineupCategory.CT_SIDE -> listOf("CT Side", "CT 方")
    }

