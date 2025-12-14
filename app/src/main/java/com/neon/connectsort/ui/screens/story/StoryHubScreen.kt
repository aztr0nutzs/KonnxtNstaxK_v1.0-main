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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.navigation.StoryNavigation
import com.neon.connectsort.navigation.setActiveStoryChapter
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.ui.components.HolographicButton
import com.neon.connectsort.ui.components.NeonParticleField
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
    val storyEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(AppDestination.StoryHub.route)
    }
    val storyResult by storyEntry.savedStateHandle
        .getStateFlow<StoryNavigation.StoryResult?>(StoryNavigation.STORY_RESULT_KEY, null)
        .collectAsState()

    LaunchedEffect(storyResult) {
        storyResult?.let { result ->
            viewModel.markChapterCompleted(result.chapterId, result.success)
            storyEntry.savedStateHandle[StoryNavigation.STORY_RESULT_KEY] = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeonColors.depthVoid,
                        NeonColors.depthMidnight,
                        NeonColors.depthOcean
                    )
                )
            )
    ) {
        NeonParticleField(modifier = Modifier.matchParentSize(), intensity = 0.65f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            StoryHubHero()

            Spacer(modifier = Modifier.height(12.dp))

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
}

@Composable
private fun StoryHubHero() {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        neonColor = NeonColors.hologramBlue.copy(alpha = 0.25f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(id = com.neon.connectsort.R.string.story_mode_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = NeonColors.hologramCyan
            )
            Text(
                text = stringResource(id = com.neon.connectsort.R.string.story_mode_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = NeonColors.textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            StoryHubTimeline()
        }
    }
}

@Composable
private fun StoryHubTimeline() {
    val nodes = listOf(
        stringResource(id = com.neon.connectsort.R.string.story_timeline_node_basecamp),
        stringResource(id = com.neon.connectsort.R.string.story_timeline_node_network),
        stringResource(id = com.neon.connectsort.R.string.story_timeline_node_ascent)
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        nodes.forEachIndexed { index, label ->
            NeonCard(
                modifier = Modifier.weight(1f),
                neonColor = if (index == 0) NeonColors.hologramPink else NeonColors.hologramPurple.copy(alpha = 0.35f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonColors.textPrimary,
                    modifier = Modifier.padding(8.dp)
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
                    text = stringResource(id = chapter.titleRes),
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
                text = stringResource(id = chapter.descriptionRes),
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
                text = stringResource(
                    id = com.neon.connectsort.R.string.story_chapter_requirement,
                    chapter.requiredMode.name.replace('_', ' '),
                    chapter.requiredWins
                ),
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.textHologram.copy(alpha = 0.7f)
            )
            Text(
                text = stringResource(id = chapter.goalRes),
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
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
