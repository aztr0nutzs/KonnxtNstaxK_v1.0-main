package com.neon.connectsort.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.screens.*
import com.neon.connectsort.ui.screens.viewmodels.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NeonGameApp() {
    val navController = rememberAnimatedNavController()

    // ViewModels
    val lobbyViewModel: LobbyViewModel = viewModel()
    val connectFourViewModel: ConnectFourViewModel = viewModel()
    val ballSortViewModel: BallSortViewModel = viewModel()
    val multiplierViewModel: MultiplierViewModel = viewModel()
    val shopViewModel: ShopViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val characterChipsViewModel: CharacterChipsViewModel = viewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = AppDestination.Lobby.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(AppDestination.Lobby.route) {
            LobbyScreen(
                navController = navController,
                viewModel = lobbyViewModel
            )
        }
        composable(
            route = AppDestination.ConnectFour.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) }
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
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) }
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
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) }
        ) {
            MultiplierScreen(
                navController = navController,
                viewModel = multiplierViewModel
            )
        }
        composable(
            route = AppDestination.Shop.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            ShopScreen(
                navController = navController,
                viewModel = shopViewModel
            )
        }
        composable(
            route = AppDestination.Settings.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
        composable(
            route = AppDestination.CharacterChips.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            CharacterChipsScreen(
                navController = navController,
                viewModel = characterChipsViewModel
            )
        }
    }
}
