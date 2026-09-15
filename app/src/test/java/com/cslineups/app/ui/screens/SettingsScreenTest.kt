package com.cslineups.app.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.ZhStrings
import com.cslineups.app.ui.theme.CsLineupsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class SettingsScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private fun renderSettings() {
        compose.setContent {
            CsLineupsTheme {
                SettingsScreen(
                    strings = ZhStrings,
                    state = PrefsState(),
                    onSetLanguage = {},
                    onSetThemeMode = {},
                    onOpenAbout = {},
                    onBack = {},
                )
            }
        }
    }

    @Test
    fun `设置页有深色模式的三档选择`() {
        renderSettings()

        compose.onNodeWithText("深色模式").assertExists()
        compose.onNodeWithText("浅色").assertExists()
        compose.onNodeWithText("深色").assertExists()
        // 「跟随系统」在语言和外观里各有一个
        compose.onAllNodesWithText("跟随系统").assertCountEquals(2)
    }

    @Test
    fun `设置页保留语言与关于入口`() {
        renderSettings()

        compose.onNodeWithText("语言").assertExists()
        compose.onNodeWithText("关于").assertExists()
    }
}
