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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Lang
import com.cslineups.app.model.LineupGroup
import com.cslineups.app.model.LineupVariant
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.InfoRow
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import com.cslineups.app.ui.components.SectionTitle
import com.cslineups.app.ui.components.UtilityBadge
import com.cslineups.app.ui.components.UtilityDot
import com.cslineups.app.ui.label
import com.cslineups.app.ui.difficultyLabel

@Composable
fun GroupDetailScreen(
    group: LineupGroup,
    lang: Lang,
    strings: Strings,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenVariant: (String) -> Unit,
    onBack: () -> Unit,
) {
    ScreenScaffold(
        title = group.targetName.value(lang),
        onBack = onBack,
        actions = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                    tint = if (isFavorite) Color(0xFFFFD400) else Color(0xFF8E8E93),
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(PagePadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { SectionTitle(strings.overview) }
            item {
                Card(
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow(strings.name, group.targetName.value(lang))
                        InfoRow(strings.type, group.type.label(strings))
                        InfoRow(strings.side, group.side)
                        InfoRow(strings.category, group.category.label(strings))
                        InfoRow(strings.lineupVariants, strings.variantCount(group.variants.size))
                    }
                }
            }

            item { SectionTitle(strings.lineupVariants) }
            items(group.variants, key = { it.id }) { variant ->
                VariantCard(group, variant, lang, strings) { onOpenVariant(variant.id) }
            }
        }
    }
}

@Composable
private fun VariantCard(
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UtilityDot(group.type, size = 30.dp)
                Spacer(Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(variant.name.value(lang), style = MaterialTheme.typography.titleMedium)
                    UtilityBadge(variant.difficulty.difficultyLabel(strings))
                }
            }
            LabelledText(strings.spawnRequirement, variant.spawnRequirement.value(lang))
            LabelledText(strings.lineupSteps, variant.throwMethod.value(lang))
        }
    }
}

@Composable
internal fun LabelledText(title: String, value: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
