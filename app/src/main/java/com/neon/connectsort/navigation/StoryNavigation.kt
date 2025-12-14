package com.neon.connectsort.navigation

import android.os.Parcelable
import androidx.navigation.NavController
import kotlinx.parcelize.Parcelize

object StoryNavigation {
    const val STORY_RESULT_KEY = "storyResult"
    const val ACTIVE_CHAPTER_KEY = "activeStoryChapterId"

    @Parcelize
    data class StoryResult(val chapterId: String, val success: Boolean) : Parcelable
}

fun NavController.setActiveStoryChapter(chapterId: String) {
    currentBackStackEntry?.savedStateHandle?.set(StoryNavigation.ACTIVE_CHAPTER_KEY, chapterId)
}

fun NavController.publishStoryResult(success: Boolean) {
    val previousEntry = previousBackStackEntry ?: return
    previousEntry.savedStateHandle.get<String>(StoryNavigation.ACTIVE_CHAPTER_KEY)?.let { chapterId ->
        previousEntry.savedStateHandle[StoryNavigation.STORY_RESULT_KEY] =
            StoryNavigation.StoryResult(chapterId, success)
    }
}

fun NavController.activeStoryChapterId(): String? {
    return previousBackStackEntry?.savedStateHandle?.get<String>(StoryNavigation.ACTIVE_CHAPTER_KEY)
}
