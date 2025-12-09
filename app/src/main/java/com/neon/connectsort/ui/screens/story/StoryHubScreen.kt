package com.neon.connectsort.ui.screens.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.navigation.STORY_RESULT_KEY
import com.neon.connectsort.navigation.StoryResult
import com.neon.connectsort.navigation.setActiveStoryChapter
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.ui.components.HolographicButton
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonText
import com.neon.connectsort.ui.screens.viewmodels.StoryChapter
import com.neon.connectsort.ui.screens.viewmodels.StoryGameMode
import com.neon.connectsort.ui.screens.viewmodels.StoryHubViewModel

@Composable
fun StoryHubScreen(
    navController: NavController,
    viewModel: StoryHubViewModel
) {
    val chapters by viewModel.chapters.collectAsState()
    val storyEntry = remember { navController.getBackStackEntry(AppDestination.StoryHub.route) }
    val storyResult by storyEntry.savedStateHandle
        .getStateFlow<StoryResult?>(STORY_RESULT_KEY, null)
        .collectAsState()

    LaunchedEffect(storyResult) {
        storyResult?.let { result ->
            viewModel.markChapterCompleted(result.chapterId, result.success)
            storyEntry.savedStateHandle[STORY_RESULT_KEY] = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "STORY MODE",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = NeonColors.hologramCyan
        )
        Text(
            text = "Follow the neon campaign through Connect-4, Ball Sort, and Multiplier challenges.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeonColors.textHologram
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chapters) { chapter ->
                StoryChapterCard(
                    chapter = chapter,
                    onStart = { startChapter(navController, viewModel, chapter) }
                )
            }
        }
    }
}

@Composable
private fun StoryChapterCard(
    chapter: StoryChapter,
    onStart: () -> Unit
) {
    val tint = when {
        chapter.isCompleted -> NeonColors.hologramGreen
        chapter.isLocked -> NeonColors.hologramRed
        else -> NeonColors.hologramPink
    }

    NeonCard(
        modifier = Modifier
            .fillMaxWidth(),
        neonColor = tint
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(NeonColors.depthDark),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonText(
                    text = chapter.title,
                    fontSize = 20,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.hologramCyan
                )
                Text(
                    text = chapter.requiredDifficulty.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonColors.hologramYellow
                )
            }

            Text(
                text = chapter.description,
                style = MaterialTheme.typography.bodyMedium,
                color = NeonColors.textHologram
            )

            HolographicButton(
                text = when {
                    chapter.isLocked -> "LOCKED"
                    chapter.isCompleted -> "REPLAY"
                    else -> "LAUNCH"
                },
                onClick = onStart,
                glowColor = tint,
                enabled = !chapter.isLocked,
                modifier = Modifier.align(Alignment.End)
            )

            Text(
                text = "Mode: ${chapter.requiredMode.name.replace('_', ' ')} · Wins needed: ${chapter.requiredWins}",
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.textHologram.copy(alpha = 0.7f)
            )
        }
    }
}

private fun startChapter(
    navController: NavController,
    viewModel: StoryHubViewModel,
    chapter: StoryChapter
) {
    navController.setActiveStoryChapter(chapter.id)
    viewModel.startChapter(chapter)
    when (chapter.requiredMode) {
        StoryGameMode.CONNECT_FOUR -> navController.toConnectFour()
        StoryGameMode.BALL_SORT -> navController.toBallSort()
        StoryGameMode.MULTIPLIER -> navController.toMultiplier()
    }
}
