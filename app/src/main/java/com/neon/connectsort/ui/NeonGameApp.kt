package com.neon.connectsort.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.neon.connectsort.core.data.userPreferencesDataStore
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.screens.*
import com.neon.connectsort.ui.screens.story.StoryHubScreen
import com.neon.connectsort.ui.screens.viewmodels.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NeonGameApp() {
    val context = LocalContext.current
    val repository = remember { AppPreferencesRepository(context.userPreferencesDataStore) }
    val viewModelFactory = remember(repository) { PreferencesViewModelFactory(repository) }
    val navController = rememberNavController()

    // ViewModels
    val lobbyViewModel: LobbyViewModel = viewModel(factory = viewModelFactory)
    val connectFourViewModel: ConnectFourViewModel = viewModel(factory = viewModelFactory)
    val ballSortViewModel: BallSortViewModel = viewModel(factory = viewModelFactory)
    val multiplierViewModel: MultiplierViewModel = viewModel(factory = viewModelFactory)
    val shopViewModel: ShopViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val characterChipsViewModel: CharacterChipsViewModel = viewModel(factory = viewModelFactory)
    val storyHubViewModel: StoryHubViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = AppDestination.Lobby.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(AppDestination.Lobby.route) {
            LobbyScreen(
                navController = navController,
                viewModel = lobbyViewModel
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
            ConnectFourScreen(
                navController = navController,
                viewModel = connectFourViewModel
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
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt(AppDestination.BallSort.levelArg) ?: 1
            BallSortScreen(
                navController = navController,
                viewModel = ballSortViewModel,
                initialLevel = level
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
