package com.cslineups.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cslineups.app.model.Lang
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

data class PrefsState(
    val language: Lang = Lang.SYSTEM,
    val favoriteItems: Set<String> = emptySet(),
)

/** 对应 iOS 版的 UserDefaults：语言、开发者模式、收藏。 */
class Prefs(private val context: Context) {

    private val languageKey = stringPreferencesKey("language")
    private val favoriteItemsKey = stringSetPreferencesKey("favorite_item_ids")

    val state: Flow<PrefsState> = context.settingsDataStore.data.map { stored ->
        PrefsState(
            language = stored[languageKey]
                ?.let { raw -> Lang.entries.firstOrNull { it.name == raw } }
                ?: Lang.SYSTEM,
            favoriteItems = stored[favoriteItemsKey] ?: emptySet(),
        )
    }

    suspend fun setLanguage(language: Lang) {
        context.settingsDataStore.edit { it[languageKey] = language.name }
    }

    suspend fun toggleFavorite(itemId: String) {
        context.settingsDataStore.edit { stored ->
            stored[favoriteItemsKey] = stored[favoriteItemsKey].orEmpty().toggle(itemId)
        }
    }

    private fun Set<String>.toggle(id: String): Set<String> =
        if (contains(id)) this - id else this + id
}

