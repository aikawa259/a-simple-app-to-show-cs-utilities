package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.Lang
import com.cslineups.app.ui.components.FeatureCard
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold

@Composable
fun MapHomeScreen(
    map: CsMap,
    lang: Lang,
    strings: Strings,
    onOpenMap: () -> Unit,
    onOpenList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    ScreenScaffold(
        title = map.name.value(lang),
        actions = {
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = strings.settings)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(PagePadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FeatureCard(
                icon = Icons.Filled.Place,
                title = strings.tacticalMap,
                subtitle = strings.featureMapSubtitle,
                iconColor = Color(0xFF0A84FF),
                onClick = onOpenMap,
            )
            FeatureCard(
                icon = Icons.AutoMirrored.Filled.List,
                title = strings.utilityList,
                subtitle = strings.featureListSubtitle,
                iconColor = Color(0xFF34C759),
                onClick = onOpenList,
            )
            FeatureCard(
                icon = Icons.Filled.Search,
                title = strings.search,
                subtitle = strings.featureSearchSubtitle,
                iconColor = Color(0xFFFF9F0A),
                onClick = onOpenSearch,
            )
            FeatureCard(
                icon = Icons.Filled.Star,
                title = strings.favorites,
                subtitle = strings.featureFavoritesSubtitle,
                iconColor = Color(0xFFFFD400),
                onClick = onOpenFavorites,
            )
        }
    }
}

