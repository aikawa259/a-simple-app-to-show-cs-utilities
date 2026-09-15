package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.ItemCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.searchItems

@Composable
fun SearchScreen(
    map: CsMap,
    strings: Strings,
    favoriteIds: Set<String>,
    onOpenItem: (String) -> Unit,
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val results = remember(query, map.items, strings) { searchItems(map.items, query, strings) }

    ScreenScaffold(title = strings.search, onBack = onBack) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(strings.searchPrompt) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                shape = com.cslineups.app.ui.components.SmallShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PagePadding, vertical = 8.dp),
            )

            when {
                query.isBlank() -> Box(Modifier.fillMaxSize()) {
                    EmptyState(
                        icon = Icons.Filled.Search,
                        title = strings.emptySearchHint,
                    )
                }

                results.isEmpty() -> Box(Modifier.fillMaxSize()) {
                    EmptyState(
                        icon = Icons.Filled.Search,
                        title = strings.emptySearch,
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(PagePadding),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(results, key = { it.id }) { item ->
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
}
