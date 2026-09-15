package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.ItemCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold

@Composable
fun FavoritesScreen(
    map: CsMap,
    strings: Strings,
    favoriteIds: Set<String>,
    onOpenItem: (String) -> Unit,
    onBack: () -> Unit,
) {
    val favorites = map.items.filter { it.id in favoriteIds }

    ScreenScaffold(title = strings.favorites, onBack = onBack) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState(
                    icon = Icons.Filled.Star,
                    title = strings.emptyFavorites,
                    message = strings.emptyFavoritesMessage,
                )
            }
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(favorites, key = { it.id }) { item ->
                ItemCard(
                    item = item,
                    strings = strings,
                    isFavorite = true,
                    onClick = { onOpenItem(item.id) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}
