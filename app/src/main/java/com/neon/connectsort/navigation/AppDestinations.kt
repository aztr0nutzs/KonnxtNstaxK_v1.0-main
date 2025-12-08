package com.neon.connectsort.navigation

import androidx.navigation.NavController

/**
 * Centralized route definitions for the app.
 */
sealed class AppDestination(val route: String) {
    object Lobby : AppDestination("lobby")
    object ConnectFour : AppDestination("connect4")
    object BallSort : AppDestination("ballsort") {
        const val levelArg = "level"
        fun buildRoute(level: Int) = "$route?$levelArg=$level"
    }
    object Multiplier : AppDestination("multiplier")
    object Shop : AppDestination("shop")
    object Settings : AppDestination("settings")
    object CharacterChips : AppDestination("characterchips")
}

// Typed navigation helpers
fun NavController.toLobby() {
    navigate(AppDestination.Lobby.route) {
        popUpTo(AppDestination.Lobby.route) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavController.toConnectFour() {
    navigate(AppDestination.ConnectFour.route) { launchSingleTop = true }
}

fun NavController.toBallSort(level: Int = 1) {
    navigate(AppDestination.BallSort.buildRoute(level)) { launchSingleTop = true }
}

fun NavController.toMultiplier() {
    navigate(AppDestination.Multiplier.route) { launchSingleTop = true }
}

fun NavController.toShop() {
    navigate(AppDestination.Shop.route) { launchSingleTop = true }
}

fun NavController.toSettings() {
    navigate(AppDestination.Settings.route) { launchSingleTop = true }
}

fun NavController.toCharacterChips() {
    navigate(AppDestination.CharacterChips.route) { launchSingleTop = true }
}
