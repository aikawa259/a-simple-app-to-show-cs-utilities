package com.cslineups.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cslineups.app.data.MapRepository
import com.cslineups.app.data.Prefs
import com.cslineups.app.data.PrefsState
import com.cslineups.app.i18n.Strings
import com.cslineups.app.model.Lang
import com.cslineups.app.ui.screens.AboutScreen
import com.cslineups.app.ui.screens.FavoritesScreen
import com.cslineups.app.ui.screens.LineupDetailScreen
import com.cslineups.app.ui.screens.LineupEditorScreen
import com.cslineups.app.ui.screens.MapHomeScreen
import com.cslineups.app.ui.screens.SearchScreen
import com.cslineups.app.ui.screens.SettingsScreen
import com.cslineups.app.ui.screens.UtilityListScreen
import kotlinx.coroutines.launch

private object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val ITEM = "item/{itemId}"
    const val EDIT = "edit/{itemId}"

    fun item(itemId: String) = "item/$itemId"
    fun edit(itemId: String) = "edit/$itemId"
}

@Composable
fun AppNav(
    prefs: Prefs,
    state: PrefsState,
    lang: Lang,
    strings: Strings,
    repository: MapRepository,
) {
    val maps by repository.maps.collectAsState()
    var currentMapId by rememberSaveable { mutableStateOf<String?>(null) }
    val currentMap = maps.firstOrNull { it.id == currentMapId } ?: maps.firstOrNull()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentMap?.id) {
        currentMapId = currentMap?.id
    }

    // 页面像从入口卡片那里"展开"出来：以卡片所在的偏上位置为原点做缩放和旋转
    val expandFrom = TransformOrigin(0.5f, 0.26f)
    val pageSpring = spring<Float>(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow,
    )

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = {
            scaleIn(
                animationSpec = pageSpring,
                initialScale = 0.74f,
                transformOrigin = expandFrom,
            ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing))
        },
        exitTransition = {
            scaleOut(
                animationSpec = tween(360, easing = FastOutSlowInEasing),
                targetScale = 0.88f,
                transformOrigin = expandFrom,
            ) + fadeOut(animationSpec = tween(260, delayMillis = 60))
        },
        popEnterTransition = {
            scaleIn(
                animationSpec = pageSpring,
                initialScale = 0.88f,
                transformOrigin = expandFrom,
            ) + fadeIn(animationSpec = tween(260, easing = FastOutSlowInEasing))
        },
        popExitTransition = {
            scaleOut(
                animationSpec = tween(320, easing = FastOutSlowInEasing),
                targetScale = 0.74f,
                transformOrigin = expandFrom,
            ) + fadeOut(animationSpec = tween(220))
        },
    ) {
        composable(Routes.HOME) {
            MapHomeScreen(
                map = currentMap,
                maps = maps,
                strings = strings,
                onSelectMap = { currentMapId = it },
                onCreateMap = { name ->
                    scope.launch {
                        val created = repository.createMap(name)
                        currentMapId = created.id
                    }
                },
                onRenameMap = { id, name -> scope.launch { repository.renameMap(id, name) } },
                onDeleteMap = { id ->
                    scope.launch {
                        repository.deleteMap(id)
                        if (currentMapId == id) currentMapId = null
                    }
                },
                onOpenList = { navController.navigate(Routes.LIST) },
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onAddItem = { name, target ->
                    currentMap?.let { map ->
                        scope.launch {
                            val item = repository.addItem(map.id, name, target)
                            navController.navigate(Routes.edit(item.id))
                        }
                    }
                },
            )
        }

        composable(Routes.LIST) {
            val map = currentMap ?: return@composable
            UtilityListScreen(
                map = map,
                strings = strings,
                favoriteIds = state.favoriteItems,
                onOpenItem = { navController.navigate(Routes.item(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SEARCH) {
            val map = currentMap ?: return@composable
            SearchScreen(
                map = map,
                strings = strings,
                favoriteIds = state.favoriteItems,
                onOpenItem = { navController.navigate(Routes.item(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FAVORITES) {
            val map = currentMap ?: return@composable
            FavoritesScreen(
                map = map,
                strings = strings,
                favoriteIds = state.favoriteItems,
                onOpenItem = { navController.navigate(Routes.item(it)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                strings = strings,
                state = state,
                onSetLanguage = { scope.launch { prefs.setLanguage(it) } },
                onSetThemeMode = { scope.launch { prefs.setThemeMode(it) } },
                onOpenAbout = { navController.navigate(Routes.ABOUT) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(strings = strings, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.ITEM,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
        ) { entry ->
            val map = currentMap ?: return@composable
            val itemId = entry.arguments?.getString("itemId")
            val item = map.items.firstOrNull { it.id == itemId }
            if (item == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }
            LineupDetailScreen(
                item = item,
                strings = strings,
                isFavorite = item.id in state.favoriteItems,
                onToggleFavorite = { scope.launch { prefs.toggleFavorite(item.id) } },
                onEdit = { navController.navigate(Routes.edit(item.id)) },
                onDelete = {
                    scope.launch { repository.deleteItem(map.id, item.id) }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
        ) { entry ->
            val map = currentMap ?: return@composable
            val itemId = entry.arguments?.getString("itemId")
            val item = map.items.firstOrNull { it.id == itemId }
            if (item == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }
            LineupEditorScreen(
                item = item,
                strings = strings,
                onUpdate = { updated -> scope.launch { repository.updateItem(map.id, updated) } },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
