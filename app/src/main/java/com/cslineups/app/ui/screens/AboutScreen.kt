package com.cslineups.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cslineups.app.i18n.Strings
import com.cslineups.app.ui.components.CardShape
import com.cslineups.app.ui.components.InfoRow
import com.cslineups.app.ui.components.PagePadding
import com.cslineups.app.ui.components.ScreenScaffold
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(strings: Strings, onBack: () -> Unit) {
    val context = LocalContext.current
    val version = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "-"

    ScreenScaffold(title = strings.about, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(PagePadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoCard {
                Text(strings.appName, style = MaterialTheme.typography.headlineSmall)
                Text(
                    strings.appIntro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                InfoRow(strings.name, strings.appName)
                InfoRow(strings.version, version)
            }

            InfoCard {
                Text(strings.unofficialNotice, style = MaterialTheme.typography.titleMedium)
                Text(
                    strings.unofficialNoticeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            InfoCard {
                Text(strings.acknowledgements, style = MaterialTheme.typography.titleMedium)
                Text(
                    strings.acknowledgementsText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InfoCard(content: @Composable () -> Unit) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { content() }
    }
}
