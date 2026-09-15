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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Lang
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold

@Composable
fun SettingsScreen(
    strings: Strings,
    state: PrefsState,
    onSetLanguage: (Lang) -> Unit,
    onSetDeveloperMode: (Boolean) -> Unit,
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
            SettingsCard {
                Text(strings.language, style = MaterialTheme.typography.labelLarge)
                Lang.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetLanguage(option) }
                            .padding(vertical = 4.dp),
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

            SettingsCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.developerMode, Modifier.weight(1f))
                    Switch(
                        checked = state.developerMode,
                        onCheckedChange = onSetDeveloperMode,
                    )
                }
            }

            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenAbout),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { content() }
    }
}
