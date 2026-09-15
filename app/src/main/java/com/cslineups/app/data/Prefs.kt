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
    val developerMode: Boolean = false,
    val favoriteGroups: Set<String> = emptySet(),
    val favoriteVariants: Set<String> = emptySet(),
)

/** 对应 iOS 版的 UserDefaults：语言、开发者模式、收藏。 */
class Prefs(private val context: Context) {

    private val languageKey = stringPreferencesKey("language")
    private val developerModeKey = booleanPreferencesKey("developer_mode")
    private val favoriteGroupsKey = stringSetPreferencesKey("favorite_group_ids")
    private val favoriteVariantsKey = stringSetPreferencesKey("favorite_variant_ids")

    val state: Flow<PrefsState> = context.settingsDataStore.data.map { stored ->
        PrefsState(
            language = stored[languageKey]
                ?.let { raw -> Lang.entries.firstOrNull { it.name == raw } }
                ?: Lang.SYSTEM,
            developerMode = stored[developerModeKey] ?: false,
            favoriteGroups = stored[favoriteGroupsKey] ?: emptySet(),
            favoriteVariants = stored[favoriteVariantsKey] ?: emptySet(),
        )
    }

    suspend fun setLanguage(language: Lang) {
        context.settingsDataStore.edit { it[languageKey] = language.name }
    }

    suspend fun setDeveloperMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[developerModeKey] = enabled }
    }

    suspend fun toggleFavoriteGroup(groupId: String) {
        context.settingsDataStore.edit { stored ->
            stored[favoriteGroupsKey] = stored[favoriteGroupsKey].orEmpty().toggle(groupId)
        }
    }

    suspend fun toggleFavoriteVariant(variantId: String) {
        context.settingsDataStore.edit { stored ->
            stored[favoriteVariantsKey] = stored[favoriteVariantsKey].orEmpty().toggle(variantId)
        }
    }

    private fun Set<String>.toggle(id: String): Set<String> =
        if (contains(id)) this - id else this + id
}

