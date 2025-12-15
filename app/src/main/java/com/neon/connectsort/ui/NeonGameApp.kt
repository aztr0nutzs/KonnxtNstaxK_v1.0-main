package com.neon.connectsort.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.userPreferencesDataStore
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.connectsort.ui.components.HtmlAssetScreen
import com.neon.connectsort.ui.components.HtmlBridge
import com.neon.connectsort.ui.components.HtmlBridgeAction
import com.neon.connectsort.ui.screens.CharacterChipsScreen
import com.neon.connectsort.ui.screens.MultiplierScreen
import com.neon.connectsort.ui.screens.SettingsScreen
import com.neon.connectsort.ui.screens.ShopScreen
import com.neon.connectsort.ui.screens.story.StoryHubScreen
import com.neon.connectsort.ui.screens.viewmodels.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NeonGameApp() {
    val context = LocalContext.current
    val dataStore = remember { context.userPreferencesDataStore }
    val preferencesRepository = remember { AppPreferencesRepository(dataStore) }
    val economyRepository = remember { EconomyRepository(dataStore) }
    val analyticsTracker = remember { AnalyticsTracker(preferencesRepository.analyticsEnabledFlow()) }
    val audioManager = remember { AudioManager(context, preferencesRepository) }
    val viewModelFactory = remember(
        preferencesRepository,
        economyRepository,
        analyticsTracker,
        audioManager
    ) {
        PreferencesViewModelFactory(
            preferencesRepository,
            economyRepository,
            analyticsTracker,
            audioManager
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            analyticsTracker.dispose()
            audioManager.release()
        }
    }
    val navController = rememberNavController()
    val htmlBridge = remember(navController, analyticsTracker) {
        HtmlBridge { action ->
            val payload = when (action) {
                is HtmlBridgeAction.FindMatch -> action.destination
                is HtmlBridgeAction.PurchaseItem -> action.itemId
            }
            analyticsTracker.logEvent(
                "html_action",
                mapOf(
                    "action" to action::class.simpleName,
                    "payload" to payload
                )
            )
            when (action) {
                is HtmlBridgeAction.FindMatch -> {
                    when (action.destination?.lowercase()) {
                        "ball_sort" -> navController.navigate(AppDestination.BallSort.buildRoute(1))
                        "connect_four" -> navController.navigate(AppDestination.ConnectFour.route)
                        else -> navController.navigate(AppDestination.Lobby.route)
                    }
                }
                is HtmlBridgeAction.PurchaseItem -> {
                    navController.navigate(AppDestination.Shop.route)
                }
            }
        }
    }

    // ViewModels
    val multiplierViewModel: MultiplierViewModel = viewModel(factory = viewModelFactory)
    val shopViewModel: ShopViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val characterChipsViewModel: CharacterChipsViewModel = viewModel(factory = viewModelFactory)
    val storyHubViewModel: StoryHubViewModel = viewModel(factory = viewModelFactory)

    val selectedCharacter by characterChipsViewModel.selectedCharacter.collectAsState()

    LaunchedEffect(htmlBridge) {
        economyRepository.coinBalance.collect { htmlBridge.updateCoinBalance(it) }
    }

    LaunchedEffect(selectedCharacter) {
        htmlBridge.updatePlayerName(selectedCharacter?.name ?: "NEON")
    }

    NavHost(
        navController = navController,
        startDestination = AppDestination.Lobby.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(AppDestination.Lobby.route) {
            HtmlAssetScreen(
                assetPath = "ui/lobby.html",
                modifier = Modifier.fillMaxSize(),
                enableJavaScript = true,
                bridge = htmlBridge
            )
        }
        composable(
            route = AppDestination.StoryHub.route,
        ) {
            StoryHubScreen(
                navController = navController,
                viewModel = storyHubViewModel
            )
        }
        composable(
            route = AppDestination.ConnectFour.route,
        ) {
                HtmlAssetScreen(
                    assetPath = "ui/connect4.html",
                    modifier = Modifier.fillMaxSize(),
                    enableJavaScript = true,
                    bridge = htmlBridge
                )
            }
        composable(
            route = AppDestination.BallSort.routeWithArgs,
                arguments = listOf(
                    navArgument(AppDestination.BallSort.levelArg) {
                        type = NavType.IntType
                        defaultValue = AppDestination.BallSort.defaultLevel
                    }
                ),
            ) { _ ->
                HtmlAssetScreen(
                    assetPath = "ui/ball_sort.html",
                    modifier = Modifier.fillMaxSize(),
                    enableJavaScript = true,
                    bridge = htmlBridge
                )
            }
        composable(
            route = AppDestination.Multiplier.route,
        ) {
            MultiplierScreen(
                navController = navController,
                viewModel = multiplierViewModel
            )
        }
        composable(
            route = AppDestination.Shop.route,
        ) {
            ShopScreen(
                navController = navController,
                viewModel = shopViewModel
            )
        }
        composable(
            route = AppDestination.Settings.route,
        ) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
        composable(
            route = AppDestination.CharacterChips.route,
        ) {
            CharacterChipsScreen(
                navController = navController,
                viewModel = characterChipsViewModel
            )
        }
    }
}
