package com.cslineups.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.cslineups.app.data.MapRepository
import com.cslineups.app.data.Prefs
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.stringsFor
import com.cslineups.app.ui.AppNav
import com.cslineups.app.ui.theme.CsLineupsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = Prefs(applicationContext)
        val repository = MapRepository(applicationContext)

        setContent {
            val state by prefs.state.collectAsState(initial = PrefsState())
            val configuration = LocalConfiguration.current
            val systemIsChinese = remember(configuration) {
                val locales = configuration.locales
                (0 until locales.size()).any { locales.get(it).language == "zh" }
            }
            val lang = state.language.resolved(systemIsChinese)

            LaunchedEffect(Unit) {
                repository.load(useChinese = systemIsChinese)
            }

            CsLineupsTheme {
                AppNav(
                    prefs = prefs,
                    state = state,
                    lang = lang,
                    strings = stringsFor(lang),
                    repository = repository,
                )
            }
        }
    }
}
