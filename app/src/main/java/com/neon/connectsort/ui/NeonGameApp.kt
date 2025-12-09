package com.neon.connectsort.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.screens.*
import com.neon.connectsort.ui.screens.viewmodels.*

@Composable
fun NeonGameApp() {
    val navController = rememberNavController()

    // ViewModels
    val lobbyViewModel: LobbyViewModel = viewModel()
    val connectFourViewModel: ConnectFourViewModel = viewModel()
    val ballSortViewModel: BallSortViewModel = viewModel()
    val multiplierViewModel: MultiplierViewModel = viewModel()
    val shopViewModel: ShopViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val characterChipsViewModel: CharacterChipsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Lobby.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppDestination.Lobby.route) {
            LobbyScreen(
                navController = navController,
                viewModel = lobbyViewModel
            )
        }
        composable(AppDestination.ConnectFour.route) {
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
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt(AppDestination.BallSort.levelArg) ?: 1
            BallSortScreen(
                navController = navController,
                viewModel = ballSortViewModel,
                initialLevel = level
            )
        }
        composable(AppDestination.Multiplier.route) {
            MultiplierScreen(
                navController = navController,
                viewModel = multiplierViewModel
            )
        }
        composable(AppDestination.Shop.route) {
            ShopScreen(
                navController = navController,
                viewModel = shopViewModel
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
        composable(AppDestination.CharacterChips.route) {
            CharacterChipsScreen(
                navController = navController,
                viewModel = characterChipsViewModel
            )
        }
    }
}
