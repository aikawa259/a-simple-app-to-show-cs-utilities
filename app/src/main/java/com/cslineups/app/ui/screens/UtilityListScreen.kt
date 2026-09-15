package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.ItemCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.label

@Composable
fun UtilityListScreen(
    map: CsMap,
    strings: Strings,
    favoriteIds: Set<String>,
    onOpenItem: (String) -> Unit,
    onBack: () -> Unit,
) {
    ScreenScaffold(title = strings.utilityList, onBack = onBack) { padding ->
        if (map.items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState(Icons.Filled.Info, strings.emptyItems, strings.emptyItemsMessage)
            }
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LineupCategory.entries.forEach { category ->
                val items = map.items.filter { it.category == category }
                if (items.isEmpty()) return@forEach

                item(key = "header-${category.name}") {
                    SectionTitle(category.label(strings))
                }
                items(items, key = { it.id }) { item ->
                    ItemCard(
                        item = item,
                        strings = strings,
                        isFavorite = item.id in favoriteIds,
                        onClick = { onOpenItem(item.id) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}
