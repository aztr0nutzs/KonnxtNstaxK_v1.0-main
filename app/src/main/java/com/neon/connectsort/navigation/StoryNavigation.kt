package com.neon.connectsort.navigation

import androidx.navigation.NavController

const val STORY_RESULT_KEY = "storyResult"
const val ACTIVE_CHAPTER_KEY = "activeStoryChapterId"

data class StoryResult(
    val chapterId: String,
    val success: Boolean
)

fun NavController.setActiveStoryChapter(chapterId: String) {
    currentBackStackEntry?.savedStateHandle?.set(ACTIVE_CHAPTER_KEY, chapterId)
}

fun NavController.publishStoryResult(success: Boolean) {
    val previousEntry = previousBackStackEntry ?: return
    previousEntry.savedStateHandle.get<String>(ACTIVE_CHAPTER_KEY)?.let { chapterId ->
        previousEntry.savedStateHandle[STORY_RESULT_KEY] = StoryResult(chapterId, success)
    }
}

fun NavController.activeStoryChapterId(): String? {
    return previousBackStackEntry?.savedStateHandle?.get<String>(ACTIVE_CHAPTER_KEY)
}
