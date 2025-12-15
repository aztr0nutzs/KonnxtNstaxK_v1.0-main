package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.StringRes
import com.neon.connectsort.R
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

enum class StoryGameMode {
    CONNECT_FOUR,
    BALL_SORT,
    MULTIPLIER
}

data class StoryChapter(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val goalRes: Int,
    val requiredMode: StoryGameMode,
    val requiredDifficulty: GameDifficulty,
    val requiredWins: Int = 1,
    val isLocked: Boolean = false,
    val isCompleted: Boolean = false,
    val unlocksWithChapterId: String? = null
)

class StoryHubViewModel(
    private val repository: AppPreferencesRepository,
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {

    private val baseChapters = initialChapters()
    private val _chapters = MutableStateFlow(baseChapters)
    val chapters: StateFlow<List<StoryChapter>> = _chapters.asStateFlow()

    private val _activeChapterId = MutableStateFlow<String?>(null)
    val activeChapterId: StateFlow<String?> = _activeChapterId.asStateFlow()

    init {
        viewModelScope.launch {
            economy.storyProgressFlow.collect { progress ->
                _chapters.value = baseChapters.map { chapter ->
                    val unlocked = chapter.id in progress.unlockedChapters || !chapter.isLocked
                    chapter.copy(
                        isLocked = !unlocked,
                        isCompleted = chapter.id in progress.completedChapters
                    )
                }
                _activeChapterId.value = progress.activeChapter
            }
        }
    }

    fun startChapter(chapter: StoryChapter) {
        _activeChapterId.value = chapter.id
        viewModelScope.launch {
            repository.setDifficulty(chapter.requiredDifficulty.level)
            economy.setActiveStoryChapter(chapter.id)
            analyticsTracker.logEvent("story_chapter_started", mapOf("chapter" to chapter.id))
            audioManager.playSample(AudioManager.Sample.SELECT)
        }
    }

    fun markChapterCompleted(chapterId: String, success: Boolean) {
        if (!success) return

        viewModelScope.launch {
            economy.markChapterCompleted(chapterId)
            baseChapters.firstOrNull { it.unlocksWithChapterId == chapterId }?.let { next ->
                economy.unlockStoryChapter(next.id)
            }
            analyticsTracker.logEvent("story_chapter_completed", mapOf("chapter" to chapterId))
            audioManager.playSample(AudioManager.Sample.VICTORY)
        }
    }

    private fun initialChapters(): List<StoryChapter> {
        return listOf(
            StoryChapter(
                id = "intro_calc",
                titleRes = R.string.chapter_neon_origins_title,
                descriptionRes = R.string.chapter_neon_origins_description,
                goalRes = R.string.chapter_neon_origins_goal,
                requiredMode = StoryGameMode.CONNECT_FOUR,
                requiredDifficulty = GameDifficulty.EASY,
                unlocksWithChapterId = "ballsort_incursion"
            ),
            StoryChapter(
                id = "ballsort_incursion",
                titleRes = R.string.chapter_liquid_archive_title,
                descriptionRes = R.string.chapter_liquid_archive_description,
                goalRes = R.string.chapter_liquid_archive_goal,
                requiredMode = StoryGameMode.BALL_SORT,
                requiredDifficulty = GameDifficulty.MEDIUM,
                isLocked = true,
                unlocksWithChapterId = "multiplier_ramp"
            ),
            StoryChapter(
                id = "multiplier_ramp",
                titleRes = R.string.chapter_streak_divergence_title,
                descriptionRes = R.string.chapter_streak_divergence_description,
                goalRes = R.string.chapter_streak_divergence_goal,
                requiredMode = StoryGameMode.MULTIPLIER,
                requiredDifficulty = GameDifficulty.MEDIUM,
                isLocked = true,
                unlocksWithChapterId = "connect_four_strike"
            ),
            StoryChapter(
                id = "connect_four_strike",
                titleRes = R.string.chapter_syndicate_flank_title,
                descriptionRes = R.string.chapter_syndicate_flank_description,
                goalRes = R.string.chapter_syndicate_flank_goal,
                requiredMode = StoryGameMode.CONNECT_FOUR,
                requiredDifficulty = GameDifficulty.HARD,
                requiredWins = 2,
                isLocked = true,
                unlocksWithChapterId = "ballsort_refraction"
            ),
            StoryChapter(
                id = "ballsort_refraction",
                titleRes = R.string.chapter_prismatic_break_title,
                descriptionRes = R.string.chapter_prismatic_break_description,
                goalRes = R.string.chapter_prismatic_break_goal,
                requiredMode = StoryGameMode.BALL_SORT,
                requiredDifficulty = GameDifficulty.HARD,
                requiredWins = 2,
                isLocked = true
            )
        )
    }
}
