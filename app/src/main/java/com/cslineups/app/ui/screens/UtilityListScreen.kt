package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Info
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
import com.cslineups.app.model.LineupCategory
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.EmptyState
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.UtilityBadge
import com.cslineups.app.ui.components.UtilityDot
import com.cslineups.app.ui.label

@Composable
fun UtilityListScreen(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    onOpenGroup: (String) -> Unit,
    onBack: () -> Unit,
) {
    ScreenScaffold(title = strings.utilityList, onBack = onBack) { padding ->
        if (map.lineupGroups.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState(Icons.Filled.Info, strings.emptyUtilities, strings.emptyUtilitiesMessage)
            }
            return@ScreenScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LineupCategory.entries.forEach { category ->
                val groups = map.lineupGroups.filter { it.category == category }
                if (groups.isEmpty()) return@forEach

                item(key = "header-${category.name}") {
                    SectionTitle(category.label(strings))
                }
                items(groups, key = { it.id }) { group ->
                    GroupRow(group, lang, strings) { onOpenGroup(group.id) }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UtilityDot(group.type)
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(group.targetName.value(lang), style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    UtilityBadge(group.type.label(strings))
                    UtilityBadge(group.category.label(strings))
                }
                Text(
                    strings.variantCount(group.variants.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

