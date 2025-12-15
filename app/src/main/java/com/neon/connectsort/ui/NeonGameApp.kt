package com.neon.connectsort.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.connectsort.ui.components.GameWebBridge
import com.neon.connectsort.ui.components.HtmlAssetScreen
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
    val dataStoreScope = rememberCoroutineScope()
    val dataStore = remember(context) {
        PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { context.preferencesDataStoreFile("user_prefs.preferences_pb") }
        )
    }
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
    val coroutineScope = rememberCoroutineScope()

    // ViewModels
    val multiplierViewModel: MultiplierViewModel = viewModel(factory = viewModelFactory)
    val shopViewModel: ShopViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val characterChipsViewModel: CharacterChipsViewModel = viewModel(factory = viewModelFactory)
    val storyHubViewModel: StoryHubViewModel = viewModel(factory = viewModelFactory)
    val ballSortViewModel: BallSortViewModel = viewModel(factory = viewModelFactory)

    val webBridge = remember(navController, shopViewModel, ballSortViewModel, storyHubViewModel, characterChipsViewModel) {
        GameWebBridge(
            economyRepository = economyRepository,
            navControllerProvider = { navController },
            shopViewModel = shopViewModel,
            ballSortViewModel = ballSortViewModel,
            storyHubViewModel = storyHubViewModel,
            characterChipsViewModel = characterChipsViewModel,
            scope = coroutineScope
        )
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
                enableDomStorage = true,
                webAppInterface = webBridge
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
                    enableDomStorage = true,
                    webAppInterface = webBridge
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
                    enableDomStorage = true,
                    webAppInterface = webBridge
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
