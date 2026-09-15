package com.cslineups.app

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.toArgb
import com.cslineups.app.data.MapRepository
import com.cslineups.app.data.Prefs
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.stringsFor
import com.cslineups.app.ui.AppNav
import com.cslineups.app.ui.theme.CsLineupsTheme
import com.cslineups.app.ui.theme.DarkBackground
import com.cslineups.app.ui.theme.LightBackground

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
            val darkTheme = state.themeMode.isDark(isSystemInDarkTheme())
            val windowColor = if (darkTheme) DarkBackground else LightBackground

            LaunchedEffect(Unit) {
                repository.load(useChinese = systemIsChinese)
            }

            // 窗口底色跟着所选外观走，避免启动或切换页面时闪出另一种颜色
            LaunchedEffect(windowColor) {
                window.setBackgroundDrawable(ColorDrawable(windowColor.toArgb()))
            }

            CsLineupsTheme(darkTheme = darkTheme) {
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
