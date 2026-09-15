package com.cslineups.app.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeModeTest {

    @Test
    fun `跟随系统时跟系统的深浅色一致`() {
        assertFalse(ThemeMode.SYSTEM.isDark(systemInDark = false))
        assertTrue(ThemeMode.SYSTEM.isDark(systemInDark = true))
    }

    @Test
    fun `强制浅色与强制深色不受系统影响`() {
        assertFalse(ThemeMode.LIGHT.isDark(systemInDark = true))
        assertTrue(ThemeMode.DARK.isDark(systemInDark = false))
    }
}
