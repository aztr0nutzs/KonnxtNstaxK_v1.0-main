package com.neon.connectsort.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import com.neon.connectsort.ui.screens.*
import com.neon.connectsort.ui.screens.viewmodels.*
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toCharacterChips
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toLobby
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.navigation.toSettings
import com.neon.connectsort.navigation.toShop

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NeonGameApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
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
        modifier = Modifier.fillMaxSize()
    ) {
        // Lobby Screen
        composable(
            route = AppDestination.Lobby.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            LobbyScreen(
                navController = navController,
                viewModel = lobbyViewModel
            )
        }
        
        // Connect-4 Screen
        composable(
            route = AppDestination.ConnectFour.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            ConnectFourScreen(
                navController = navController,
                viewModel = connectFourViewModel
            )
        }
        
        // Ball Sort Screen
        composable(
            route = "${AppDestination.BallSort.route}?${AppDestination.BallSort.levelArg}={${AppDestination.BallSort.levelArg}}",
            arguments = listOf(
                navArgument(AppDestination.BallSort.levelArg) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            ),
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt(AppDestination.BallSort.levelArg) ?: 1
            BallSortScreen(
                navController = navController,
                viewModel = ballSortViewModel,
                initialLevel = level
            )
        }
        
        // Multiplier Screen
        composable(
            route = AppDestination.Multiplier.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            MultiplierScreen(
                navController = navController,
                viewModel = multiplierViewModel
            )
        }
        
        // Shop Screen
        composable(
            route = AppDestination.Shop.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            ShopScreen(
                navController = navController,
                viewModel = shopViewModel
            )
        }
        
        // Settings Screen
        composable(
            route = AppDestination.Settings.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
        
        // Character Chips Screen
        composable(
            route = AppDestination.CharacterChips.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            CharacterChipsScreen(
                navController = navController,
                viewModel = characterChipsViewModel
            )
        }
    }
}
