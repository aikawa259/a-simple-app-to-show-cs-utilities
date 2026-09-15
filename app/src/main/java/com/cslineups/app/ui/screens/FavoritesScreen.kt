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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.Lang
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.model.LineupVariant
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.UtilityDot

@Composable
fun FavoritesScreen(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    favoriteGroupIds: Set<String>,
    favoriteVariantIds: Set<String>,
    onOpenGroup: (String) -> Unit,
    onOpenVariant: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    val groups = map.lineupGroups.filter { it.id in favoriteGroupIds }
    val variants = map.lineupGroups.flatMap { group ->
        group.variants.filter { it.id in favoriteVariantIds }.map { group to it }
    }

    ScreenScaffold(title = strings.favorites, onBack = onBack) { padding ->
        if (groups.isEmpty() && variants.isEmpty()) {
            EmptyState(Icons.Filled.Star, strings.emptyFavorites, strings.emptyFavoritesMessage)
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (groups.isNotEmpty()) {
                item(key = "favorite-groups") { SectionTitle(strings.favoriteGroups) }
                items(groups, key = { "g-${it.id}" }) { group ->
                    FavoriteGroupRow(group, lang, strings) { onOpenGroup(group.id) }
                }
            }
            if (variants.isNotEmpty()) {
                item(key = "favorite-variants") { SectionTitle(strings.favoriteVariants) }
                items(variants, key = { "v-${it.second.id}" }) { (group, variant) ->
                    FavoriteVariantRow(group, variant, lang, strings) {
                        onOpenVariant(group.id, variant.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteGroupRow(
    group: LineupGroup,
    lang: Lang,
    strings: Strings,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            UtilityDot(group.type)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(group.targetName.value(lang), style = MaterialTheme.typography.titleMedium)
                Text(
                    strings.groupSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    strings.variantCount(group.variants.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FavoriteVariantRow(
    group: LineupGroup,
    variant: LineupVariant,
    lang: Lang,
    strings: Strings,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            UtilityDot(group.type)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(variant.name.value(lang), style = MaterialTheme.typography.titleMedium)
                Text(
                    group.targetName.value(lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    variant.startArea.value(lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
