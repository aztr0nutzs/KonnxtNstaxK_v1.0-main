package com.neon.connectsort.ui.components

import android.webkit.JavascriptInterface
import androidx.navigation.NavController
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toLobby
import com.neon.connectsort.navigation.toShop
import com.neon.connectsort.ui.screens.viewmodels.BallSortViewModel
import com.neon.connectsort.ui.screens.viewmodels.CharacterChipsViewModel
import com.neon.connectsort.ui.screens.viewmodels.ShopViewModel
import com.neon.connectsort.ui.screens.viewmodels.StoryHubViewModel
import com.neon.game.common.PowerUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Bidirectional JS bridge exposed to WebViews as `Android`.
 *
 * UI (HTML/JS) is authoritative for visuals; ViewModels remain the source of truth for state.
 */
class GameWebBridge(
    economyRepository: EconomyRepository,
    private val navControllerProvider: () -> NavController,
    private val shopViewModel: ShopViewModel,
    private val ballSortViewModel: BallSortViewModel,
    private val storyHubViewModel: StoryHubViewModel,
    characterChipsViewModel: CharacterChipsViewModel,
    private val scope: CoroutineScope
) {
    private val cachedCoins = AtomicInteger(0)
    private val cachedName = AtomicReference("NEON")
    private val cachedChapter = AtomicReference<String?>(null)

    init {
        scope.launch {
            economyRepository.coinBalance.collectLatest { cachedCoins.set(it) }
        }
        scope.launch {
            characterChipsViewModel.selectedCharacter.collectLatest { character ->
                cachedName.set(character?.name ?: "NEON")
            }
        }
        scope.launch {
            storyHubViewModel.activeChapterId.collectLatest { chapterId ->
                cachedChapter.set(chapterId)
            }
        }
    }

    @JavascriptInterface
    fun getCoins(): Int = cachedCoins.get()

    @JavascriptInterface
    fun getSelectedCharacter(): String = cachedName.get()

    @JavascriptInterface
    fun getActiveChapter(): String? = cachedChapter.get()

    @JavascriptInterface
    fun startConnectFour() {
        scope.launch(Dispatchers.Main) { navControllerProvider().toConnectFour() }
    }

    @JavascriptInterface
    fun startBallSort(level: Int) {
        scope.launch(Dispatchers.Main) { navControllerProvider().toBallSort(level) }
    }

    @JavascriptInterface
    fun startLobby() {
        scope.launch(Dispatchers.Main) { navControllerProvider().toLobby() }
    }

    @JavascriptInterface
    fun openShop() {
        scope.launch(Dispatchers.Main) { navControllerProvider().toShop() }
    }

    @JavascriptInterface
    fun purchaseItem(itemId: String) {
        if (itemId.isBlank()) return
        scope.launch(Dispatchers.Main) { shopViewModel.purchaseItem(itemId) }
    }

    @JavascriptInterface
    fun usePowerUp(powerUpId: String) {
        val parsed = runCatching { PowerUp.valueOf(powerUpId.uppercase()) }.getOrNull() ?: return
        scope.launch(Dispatchers.Main) { ballSortViewModel.usePowerUp(parsed) }
    }

    @JavascriptInterface
    fun publishStoryResult(chapterId: String, success: Boolean) {
        if (chapterId.isBlank()) return
        scope.launch(Dispatchers.Main) { storyHubViewModel.markChapterCompleted(chapterId, success) }
    }
}
