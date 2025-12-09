package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StoryGameMode {
    CONNECT_FOUR,
    BALL_SORT,
    MULTIPLIER
}

data class StoryChapter(
    val id: String,
    val title: String,
    val description: String,
    val requiredMode: StoryGameMode,
    val requiredDifficulty: GameDifficulty,
    val requiredWins: Int = 1,
    val isLocked: Boolean = false,
    val isCompleted: Boolean = false,
    val unlocksWithChapterId: String? = null
)

class StoryHubViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {

    private val _chapters = MutableStateFlow(initialChapters())
    val chapters: StateFlow<List<StoryChapter>> = _chapters.asStateFlow()

    private val _activeChapterId = MutableStateFlow<String?>(null)
    val activeChapterId: StateFlow<String?> = _activeChapterId.asStateFlow()

    fun startChapter(chapter: StoryChapter) {
        _activeChapterId.value = chapter.id
        viewModelScope.launch {
            repository.setDifficulty(chapter.requiredDifficulty.level)
        }
    }

    fun markChapterCompleted(chapterId: String, success: Boolean) {
        if (!success) return

        _chapters.update { chapters ->
            chapters.map { chapter ->
                when {
                    chapter.id == chapterId -> chapter.copy(isCompleted = true)
                    chapter.unlocksWithChapterId == chapterId -> chapter.copy(isLocked = false)
                    else -> chapter
                }
            }
        }
    }

    private fun initialChapters(): List<StoryChapter> {
        return listOf(
            StoryChapter(
                id = "intro_calc",
                title = "Neon Origins",
                description = "Secure the lobby by beating Connect-4 on Easy.",
                requiredMode = StoryGameMode.CONNECT_FOUR,
                requiredDifficulty = GameDifficulty.EASY,
                unlocksWithChapterId = "ballsort_incursion"
            ),
            StoryChapter(
                id = "ballsort_incursion",
                title = "Liquid Archive",
                description = "Sort the shimmers in Ball Sort on Medium.",
                requiredMode = StoryGameMode.BALL_SORT,
                requiredDifficulty = GameDifficulty.MEDIUM,
                isLocked = true,
                unlocksWithChapterId = "multiplier_ramp"
            ),
            StoryChapter(
                id = "multiplier_ramp",
                title = "Streak Divergence",
                description = "Hit a 3x multiplier streak without losing.",
                requiredMode = StoryGameMode.MULTIPLIER,
                requiredDifficulty = GameDifficulty.MEDIUM,
                isLocked = true,
                unlocksWithChapterId = "connect_four_strike"
            ),
            StoryChapter(
                id = "connect_four_strike",
                title = "Syndicate Flank",
                description = "Win Connect-4 twice in a row on Hard.",
                requiredMode = StoryGameMode.CONNECT_FOUR,
                requiredDifficulty = GameDifficulty.HARD,
                requiredWins = 2,
                isLocked = true,
                unlocksWithChapterId = "ballsort_refraction"
            ),
            StoryChapter(
                id = "ballsort_refraction",
                title = "Prismatic Break",
                description = "Solve Ball Sort on Hard before the prisms stabilize.",
                requiredMode = StoryGameMode.BALL_SORT,
                requiredDifficulty = GameDifficulty.HARD,
                requiredWins = 2,
                isLocked = true
            )
        )
    }
}
