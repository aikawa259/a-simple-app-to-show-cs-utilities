package com.cslineups.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Lang
import com.cslineups.app.model.ThemeMode
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.PressableCard
import com.cslineups.app.ui.components.ScreenScaffold

@Composable
fun SettingsScreen(
    strings: Strings,
    state: PrefsState,
    onSetLanguage: (Lang) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit,
) {
    ScreenScaffold(title = strings.settings, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(PagePadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(strings.language, style = MaterialTheme.typography.labelLarge)
                    Lang.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetLanguage(option) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = state.language == option,
                                onClick = { onSetLanguage(option) },
                            )
                            Text(option.label(strings))
                        }
                    }
                }
            }

            ThemeCard(
                strings = strings,
                themeMode = state.themeMode,
                onSetThemeMode = onSetThemeMode,
            )

            PressableCard(onClick = onOpenAbout) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(strings.about)
                }
            }
        }
    }
}

private fun Lang.label(strings: Strings): String = when (this) {
    Lang.SYSTEM -> strings.followSystem
    Lang.ZH_HANS -> strings.simplifiedChinese
    Lang.EN -> strings.english
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeCard(
    strings: Strings,
    themeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit,
) {
    val options = listOf(strings.themeSystem, strings.themeLight, strings.themeDark)

    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(strings.theme, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = index == ThemeMode.entries.indexOf(themeMode),
                        onClick = { onSetThemeMode(ThemeMode.entries[index]) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size,
                        ),
                    ) {
                        Text(option)
                    }
                }
            }
        }
    }
}
