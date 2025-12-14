package com.neon.connectsort.navigation

import androidx.navigation.NavController

private const val MULTIPLAYER_CONNECT_FOUR_KEY = "multiplayer_connect_four_local"
private const val MULTIPLAYER_BALL_SORT_KEY = "multiplayer_ball_sort_competitive"

fun NavController.requestConnectFourLocalMatch() {
    currentBackStackEntry?.savedStateHandle?.set(MULTIPLAYER_CONNECT_FOUR_KEY, true)
}

fun NavController.consumeConnectFourLocalMatchRequest(): Boolean {
    val previous = previousBackStackEntry ?: return false
    val requested = previous.savedStateHandle.get<Boolean>(MULTIPLAYER_CONNECT_FOUR_KEY) ?: false
    previous.savedStateHandle[MULTIPLAYER_CONNECT_FOUR_KEY] = null
    return requested
}

fun NavController.requestBallSortCompetitiveMatch() {
    currentBackStackEntry?.savedStateHandle?.set(MULTIPLAYER_BALL_SORT_KEY, true)
}

fun NavController.consumeBallSortCompetitiveMatchRequest(): Boolean {
    val previous = previousBackStackEntry ?: return false
    val requested = previous.savedStateHandle.get<Boolean>(MULTIPLAYER_BALL_SORT_KEY) ?: false
    previous.savedStateHandle[MULTIPLAYER_BALL_SORT_KEY] = null
    return requested
}
