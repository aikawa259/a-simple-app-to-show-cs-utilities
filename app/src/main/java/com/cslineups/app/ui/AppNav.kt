package com.cslineups.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cslineups.app.data.LineupRepository
import com.cslineups.app.data.Prefs
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.CsMap
import com.cslineups.app.model.Lang
import com.cslineups.app.ui.screens.AboutScreen
import com.cslineups.app.ui.screens.FavoritesScreen
import com.cslineups.app.ui.screens.GroupDetailScreen
import com.cslineups.app.ui.screens.MapHomeScreen
import com.cslineups.app.ui.screens.SearchScreen
import com.cslineups.app.ui.screens.SettingsScreen
import com.cslineups.app.ui.screens.TacticalMapScreen
import com.cslineups.app.ui.screens.UtilityListScreen
import com.cslineups.app.ui.screens.VariantDetailScreen
import kotlinx.coroutines.launch

private object Routes {
    const val HOME = "home"
    const val MAP = "map"
    const val LIST = "list"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val GROUP = "group/{groupId}"
    const val VARIANT = "variant/{groupId}/{variantId}"

    fun group(groupId: String) = "group/$groupId"
    fun variant(groupId: String, variantId: String) = "variant/$groupId/$variantId"
}

@Composable
fun AppNav(prefs: Prefs, state: PrefsState, lang: Lang, strings: Strings) {
    val context = LocalContext.current
    val map = remember { LineupRepository.loadMirage(context) }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            MapHomeScreen(
                map = map,
                lang = lang,
                strings = strings,
                onOpenMap = { navController.navigate(Routes.MAP) },
                onOpenList = { navController.navigate(Routes.LIST) },
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.MAP) {
            TacticalMapScreen(
                map = map,
                lang = lang,
                strings = strings,
                developerMode = state.developerMode,
                onOpenGroup = { navController.navigate(Routes.group(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.LIST) {
            UtilityListScreen(
                map = map,
                lang = lang,
                strings = strings,
                onOpenGroup = { navController.navigate(Routes.group(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                map = map,
                lang = lang,
                strings = strings,
                onOpenGroup = { navController.navigate(Routes.group(it)) },
                onOpenVariant = { groupId, variantId ->
                    navController.navigate(Routes.variant(groupId, variantId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                map = map,
                lang = lang,
                strings = strings,
                favoriteGroupIds = state.favoriteGroups,
                favoriteVariantIds = state.favoriteVariants,
                onOpenGroup = { navController.navigate(Routes.group(it)) },
                onOpenVariant = { groupId, variantId ->
                    navController.navigate(Routes.variant(groupId, variantId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                strings = strings,
                state = state,
                onSetLanguage = { scope.launch { prefs.setLanguage(it) } },
                onSetDeveloperMode = { scope.launch { prefs.setDeveloperMode(it) } },
                onOpenAbout = { navController.navigate(Routes.ABOUT) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(strings = strings, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.GROUP,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType }),
        ) { entry ->
            val group = map.findGroup(entry.arguments?.getString("groupId")) ?: return@composable
            GroupDetailScreen(
                group = group,
                lang = lang,
                strings = strings,
                isFavorite = group.id in state.favoriteGroups,
                onToggleFavorite = { scope.launch { prefs.toggleFavoriteGroup(group.id) } },
                onOpenVariant = { navController.navigate(Routes.variant(group.id, it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.VARIANT,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("variantId") { type = NavType.StringType },
            ),
        ) { entry ->
            val groupId = entry.arguments?.getString("groupId")
            val variantId = entry.arguments?.getString("variantId")
            val group = map.findGroup(groupId) ?: return@composable
            val variant = group.variants.firstOrNull { it.id == variantId } ?: return@composable
            VariantDetailScreen(
                group = group,
                variant = variant,
                lang = lang,
                strings = strings,
                isFavorite = variant.id in state.favoriteVariants,
                onToggleFavorite = { scope.launch { prefs.toggleFavoriteVariant(variant.id) } },
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private fun CsMap.findGroup(groupId: String?) = lineupGroups.firstOrNull { it.id == groupId }

