package com.neon.connectsort.ui

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.UserPrefs
import com.neon.connectsort.core.data.userPreferencesDataStore
import com.neon.connectsort.navigation.AppDestination
import com.neon.connectsort.ui.screens.viewmodels.BallSortViewModel
import com.neon.connectsort.ui.screens.viewmodels.ConnectFourGameState
import com.neon.connectsort.ui.screens.viewmodels.ConnectFourViewModel
import com.neon.connectsort.ui.screens.viewmodels.PreferencesViewModelFactory
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private enum class UiScreen(val asset: String) {
    LOBBY("file:///android_asset/ui/Knxt4_lobby.html"),
    CONNECT_FOUR("file:///android_asset/ui/Knxt4_2.html"),
    BALL_SORT("file:///android_asset/ui/ball_sort.html");
}

private data class PendingMessage(val js: String)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebUiHost() {
    val context = LocalContext.current
    val repository = remember { AppPreferencesRepository(context.userPreferencesDataStore) }
    val viewModelFactory = remember(repository) { PreferencesViewModelFactory(repository) }
    val connectFourViewModel: ConnectFourViewModel = viewModel(factory = viewModelFactory)
    val ballSortViewModel: BallSortViewModel = viewModel(factory = viewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    val screen: MutableState<UiScreen> = remember { mutableStateOf(UiScreen.LOBBY) }
    val debugEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            webChromeClient = WebChromeClient()
        }
    }
    val bridge = remember { WebBridge(screen) }

    DisposableEffect(Unit) {
        webView.addJavascriptInterface(bridge, "AndroidBridge")
        onDispose {
            webView.removeJavascriptInterface("AndroidBridge")
            webView.destroy()
        }
    }

    AndroidView(
        factory = {
            webView.apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        bridge.flushQueue(webView)
                        bridge.injectBindings(webView)
                    }
                }
                loadUrl(screen.value.asset)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            if (it.url != screen.value.asset) {
                it.loadUrl(screen.value.asset)
            }
        }
    )

    LaunchedEffect(screen.value) {
        // Notify JS after navigation delay to allow bindings
        delay(150)
        bridge.queueJavascript("window.AndroidBridge && AndroidBridge.pageReady?.('${screen.value.name.lowercase()}')")
        bridge.flushQueue(webView)
    }

    LaunchedEffect(Unit) {
        connectFourViewModel.gameState.collectLatest { state ->
            val payload = buildConnectFourPayload(state)
            bridge.queueJavascript("window.renderConnectFour && renderConnectFour(${payload.toString()})")
            bridge.flushQueue(webView)
        }
    }

    LaunchedEffect(Unit) {
        ballSortViewModel.uiState.collectLatest { state ->
            val payload = buildBallSortPayload(state)
            bridge.queueJavascript("window.renderBallSort && renderBallSort(${payload.toString()})")
            bridge.flushQueue(webView)
        }
    }

    LaunchedEffect(Unit) {
        combine(
            repository.prefsFlow,
            connectFourViewModel.gameState,
            ballSortViewModel.gameState
        ) { prefs, connectState, ballState ->
            Triple(prefs, connectState, ballState)
        }.collectLatest { (prefs, connectState, ballState) ->
            val payload = buildLobbyPayload(prefs, connectState, ballState)
            bridge.queueJavascript("window.renderLobby && renderLobby(${payload.toString()})")
            bridge.flushQueue(webView)
        }
    }

    LaunchedEffect(Unit) {
        bridge.events.collectLatest { event ->
            when (event) {
                is WebUiEvent.Navigate -> {
                    when (event.destination) {
                        AppDestination.ConnectFour.route -> screen.value = UiScreen.CONNECT_FOUR
                        AppDestination.BallSort.route -> screen.value = UiScreen.BALL_SORT
                        else -> screen.value = UiScreen.LOBBY
                    }
                }
                is WebUiEvent.ConnectFourDrop -> connectFourViewModel.dropChip(event.column)
                WebUiEvent.ConnectFourReset -> connectFourViewModel.resetGame()
                WebUiEvent.ConnectFourHint -> connectFourViewModel.requestHint()
                is WebUiEvent.SetDifficulty -> {
                    when (event.targetGame) {
                        AppDestination.ConnectFour.route -> connectFourViewModel.setDifficulty(event.difficulty)
                        AppDestination.BallSort.route -> ballSortViewModel.setDifficulty(event.difficulty)
                        else -> {
                            connectFourViewModel.setDifficulty(event.difficulty)
                            ballSortViewModel.setDifficulty(event.difficulty)
                        }
                    }
                }
                WebUiEvent.BackToLobby -> screen.value = UiScreen.LOBBY
                is WebUiEvent.BallSortSelect -> ballSortViewModel.selectTube(event.index)
                WebUiEvent.BallSortReset -> ballSortViewModel.resetLevel()
                WebUiEvent.BallSortUndo -> ballSortViewModel.undoLastMove()
                WebUiEvent.BallSortHint -> ballSortViewModel.requestHint()
                is WebUiEvent.BallSortPause -> ballSortViewModel.setPaused(event.paused)
                is WebUiEvent.UpdateAudioSettings -> coroutineScope.launch {
                    event.sound?.let { repository.setSound(it) }
                    event.music?.let { repository.setMusic(it) }
                    event.volume?.let { repository.setVolume(it) }
                    event.animations?.let { repository.setAnimations(it) }
                }
                is WebUiEvent.ToggleDebug -> debugEnabled.value = event.enabled
            }
        }
    }

    LaunchedEffect(debugEnabled.value, screen.value) {
        bridge.queueJavascript("window.__setHitboxDebug && __setHitboxDebug(${debugEnabled.value})")
        bridge.flushQueue(webView)
    }
}

private fun buildConnectFourPayload(state: ConnectFourGameState): JSONObject {
    val board = JSONArray()
    state.board.forEach { row ->
        val rowArray = JSONArray()
        row.forEach { value ->
            rowArray.put(value ?: 0)
        }
        board.put(rowArray)
    }

    return JSONObject().apply {
        put("board", board)
        put("currentPlayer", state.currentPlayer)
        put("winner", state.winner)
        put("playerScore", state.playerScore)
        put("aiScore", state.aiScore)
        put("isDraw", state.isDraw)
        put("difficulty", state.difficulty.level)
        put("bestScore", state.bestScore)
        state.hintColumn?.let { put("hintColumn", it) } ?: put("hintColumn", JSONObject.NULL)
        state.lastDrop?.let { move ->
            put("lastDrop", JSONObject().apply {
                put("row", move.row)
                put("column", move.column)
                put("player", move.player)
            })
        }
        if (state.winningLine.isNotEmpty()) {
            val line = JSONArray()
            state.winningLine.forEach { pair ->
                line.put(JSONArray(listOf(pair.first, pair.second)))
            }
            put("winningLine", line)
        }
    }
}

private fun buildLobbyPayload(
    prefs: UserPrefs,
    connectState: ConnectFourGameState,
    ballState: com.neon.connectsort.ui.screens.viewmodels.BallSortGameState
): JSONObject {
    return JSONObject().apply {
        put("coins", prefs.coins)
        put("coinsFormatted", "% ,d".format(prefs.coins).replace(' ', ','))
        put("tickets", prefs.highScoreMultiplier)
        put("ticketsFormatted", "% ,d".format(prefs.highScoreMultiplier).replace(' ', ','))
        put("connectFourWins", connectState.playerScore)
        put("connectFourLosses", connectState.aiScore)
        put("connectFourBest", prefs.highScoreConnectFour)
        put("ballSortBest", prefs.highScoreBallSort.takeIf { it > 0 } ?: JSONObject.NULL)
        put("ballSortMoves", ballState.moves)
        put("difficulty", prefs.gameDifficulty)
    }
}

private fun buildBallSortPayload(state: BallSortViewModel.UiState): JSONObject {
    val tubes = JSONArray()
    state.game.tubes.forEach { tube ->
        val tubeArray = JSONArray()
        tube.forEach { color ->
            val argb = color.toArgb()
            tubeArray.put("#" + Integer.toHexString(argb).padStart(8, '0').takeLast(6))
        }
        tubes.put(tubeArray)
    }

    return JSONObject().apply {
        put("tubes", tubes)
        put("moves", state.game.moves)
        put("bestMoves", state.game.bestMoves ?: JSONObject.NULL)
        put("isCompleted", state.game.isLevelComplete)
        put("level", state.game.level)
        put("difficulty", state.game.difficulty.displayName)
        put("selectedTube", state.selectedTube ?: JSONObject.NULL)
        put("isPaused", state.isPaused)
        state.hint?.let { hint ->
            put("hint", JSONObject().apply {
                put("from", hint.fromTube)
                put("to", hint.toTube)
            })
        } ?: put("hint", JSONObject.NULL)
    }
}

private class WebBridge(private val screen: MutableState<UiScreen>) {
    private val _queue = mutableListOf<PendingMessage>()
    private val _events = MutableSharedFlow<WebUiEvent>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val events: SharedFlow<WebUiEvent> = _events

    @JavascriptInterface
    fun pageReady(screenName: String) {
        // Page ready notification arrives from JS; no-op for now
    }

    @JavascriptInterface
    fun emit(action: String, payload: String?) {
        when (action) {
            "startConnectFour" -> emitEvent(WebUiEvent.Navigate(AppDestination.ConnectFour.route))
            "startBallSort" -> emitEvent(WebUiEvent.Navigate(AppDestination.BallSort.route))
            "backToLobby" -> emitEvent(WebUiEvent.BackToLobby)
            "connectFourDrop" -> payload?.toIntOrNull()?.let { emitEvent(WebUiEvent.ConnectFourDrop(it)) }
            "connectFourReset" -> emitEvent(WebUiEvent.ConnectFourReset)
            "connectFourHint" -> emitEvent(WebUiEvent.ConnectFourHint)
            "ballSortSelect" -> payload?.toIntOrNull()?.let { emitEvent(WebUiEvent.BallSortSelect(it)) }
            "ballSortReset" -> emitEvent(WebUiEvent.BallSortReset)
            "ballSortUndo" -> emitEvent(WebUiEvent.BallSortUndo)
            "ballSortHint" -> emitEvent(WebUiEvent.BallSortHint)
            "ballSortPause" -> emitEvent(WebUiEvent.BallSortPause(payload?.toBooleanStrictOrNull() ?: false))
            "setDifficulty" -> payload?.let { raw ->
                runCatching { JSONObject(raw) }.getOrNull()?.let { json ->
                    val level = json.optInt("level", json.optInt("difficulty", 2))
                    val target = json.optString("target", "").ifBlank { null }
                    emitEvent(WebUiEvent.SetDifficulty(GameDifficulty.fromLevel(level), target))
                } ?: raw.toIntOrNull()?.let { level ->
                    emitEvent(WebUiEvent.SetDifficulty(GameDifficulty.fromLevel(level), null))
                }
            }
            "audioSettings" -> payload?.let { raw ->
                runCatching { JSONObject(raw) }.getOrNull()?.let { json ->
                    val sound = if (json.has("sound")) json.optBoolean("sound") else null
                    val music = if (json.has("music")) json.optBoolean("music") else null
                    val volume = if (json.has("volume")) json.optDouble("volume").toFloat() else null
                    val animations = if (json.has("animations")) json.optBoolean("animations") else null
                    emitEvent(WebUiEvent.UpdateAudioSettings(sound, music, volume, animations))
                }
            }
            "toggleDebug" -> emitEvent(WebUiEvent.ToggleDebug(payload?.toBooleanStrictOrNull() ?: true))
        }
    }

    fun queueJavascript(script: String) {
        _queue.add(PendingMessage("(function(){${script}})();"))
    }

    fun flushQueue(view: WebView) {
        if (_queue.isEmpty()) return
        _queue.forEach { msg ->
            view.post { view.evaluateJavascript(msg.js, null) }
        }
        _queue.clear()
    }

    fun injectBindings(view: WebView) {
        queueJavascript(COMMON_BINDINGS)
        val bindingScript = when (screen.value) {
            UiScreen.LOBBY -> LOBBY_BINDINGS
            UiScreen.CONNECT_FOUR -> CONNECT4_BINDINGS
            UiScreen.BALL_SORT -> BALLSORT_BINDINGS
        }
        queueJavascript(bindingScript)
        flushQueue(view)
    }

    private fun emitEvent(event: WebUiEvent) {
        scope.launch { _events.emit(event) }
    }
}

private sealed class WebUiEvent {
    data class Navigate(val destination: String) : WebUiEvent()
    data class ConnectFourDrop(val column: Int) : WebUiEvent()
    object ConnectFourReset : WebUiEvent()
    object ConnectFourHint : WebUiEvent()
    object BackToLobby : WebUiEvent()
    data class BallSortSelect(val index: Int) : WebUiEvent()
    object BallSortReset : WebUiEvent()
    object BallSortUndo : WebUiEvent()
    object BallSortHint : WebUiEvent()
    data class BallSortPause(val paused: Boolean) : WebUiEvent()
    data class SetDifficulty(val difficulty: GameDifficulty, val targetGame: String? = null) : WebUiEvent()
    data class UpdateAudioSettings(val sound: Boolean?, val music: Boolean?, val volume: Float?, val animations: Boolean?) : WebUiEvent()
    data class ToggleDebug(val enabled: Boolean) : WebUiEvent()
}

private const val COMMON_BINDINGS = """
    (function() {
        if (window.__commonBound) return;
        window.__commonBound = true;
        if (!document.getElementById('hitbox-debug-style')) {
            const style = document.createElement('style');
            style.id = 'hitbox-debug-style';
            style.textContent = `
                .debug-hitboxes button,
                .debug-hitboxes .mode-button,
                .debug-hitboxes .tube,
                .debug-hitboxes .cell,
                .debug-hitboxes .control-btn,
                .debug-hitboxes .powerup-btn,
                .debug-hitboxes .powerup-option,
                .debug-hitboxes [data-section] {
                    outline: 2px dashed #00ffaa !important;
                    outline-offset: 2px;
                }
            `;
            document.head.appendChild(style);
        }
        if (!window.__setHitboxDebug) {
            window.__setHitboxDebug = function(enabled) {
                document.documentElement.classList.toggle('debug-hitboxes', !!enabled);
            };
        }
        if (!document.getElementById('debugToggleButton')) {
            const btn = document.createElement('button');
            btn.id = 'debugToggleButton';
            btn.textContent = 'DEBUG';
            btn.style.cssText = 'position:fixed;z-index:9999;bottom:18px;right:18px;padding:10px 14px;background:rgba(0,0,0,0.6);color:#00ffaa;border:1px solid #00ffaa;border-radius:8px;font-family:monospace;letter-spacing:1px;';
            btn.addEventListener('click', () => {
                const enabled = !document.documentElement.classList.contains('debug-hitboxes');
                document.documentElement.classList.toggle('debug-hitboxes', enabled);
                if (window.AndroidBridge) {
                    AndroidBridge.emit('toggleDebug', String(enabled));
                }
            });
            document.body.appendChild(btn);
        }
    })();
"""

private const val LOBBY_BINDINGS = """
    (function() {
        if (window.__lobbyBound) return;
        window.__lobbyBound = true;

        const rewire = (selector, action, payload) => {
            document.querySelectorAll(selector).forEach(btn => {
                btn.onclick = (event) => {
                    event?.preventDefault?.();
                    AndroidBridge.emit(action, payload || null);
                    return false;
                };
            });
        };

        window.startQuickPlay = () => AndroidBridge.emit('startConnectFour', null);
        window.startRanked = () => AndroidBridge.emit('startConnectFour', null);
        window.showCustomMatches = () => AndroidBridge.emit('startBallSort', null);
        window.createMatch = () => AndroidBridge.emit('startBallSort', null);
        window.refreshMatches = () => AndroidBridge.emit('backToLobby', null);

        rewire('.mode-card.quick .mode-button', 'startConnectFour');
        rewire('.mode-card.ranked .mode-button', 'startConnectFour');
        rewire('.mode-card.custom .mode-button', 'startBallSort');

        document.querySelectorAll('[data-section]').forEach(item => {
            item.addEventListener('click', () => {
                document.querySelectorAll('[data-section]').forEach(s => s.classList.remove('active'));
                item.classList.add('active');
            });
        });

        window.renderLobby = function(state) {
            const credits = document.getElementById('creditsAmount');
            if (credits) credits.textContent = state.coinsFormatted || state.coins || '0';
            const tickets = document.getElementById('ticketsAmount');
            if (tickets) tickets.textContent = state.ticketsFormatted || state.tickets || '0';

            const matchList = document.getElementById('matchList');
            if (matchList && matchList.children.length === 0) {
                    const match = document.createElement('div');
                    match.className = 'match-item';
                    match.innerHTML = `
                        <div class="match-info">
                        <h4>Connect-4 Training</h4>
                        <p><span>Wins \${state.connectFourWins || 0}</span><span>Losses \${state.connectFourLosses || 0}</span></p>
                    </div>
                    <div class="match-status active">READY</div>
                `;
                matchList.appendChild(match);
            }
        };
    })();
"""

private const val CONNECT4_BINDINGS = """
    (function() {
        if (window.__connect4Bound) return;
        window.__connect4Bound = true;
        const grid = document.getElementById('gameGrid');
        const indicator = document.getElementById('columnIndicator');
        const victory = document.getElementById('victoryScreen');
        const startBtn = document.getElementById('startBtn');
        const resetBtn = document.getElementById('resetBtn');
        const rematchBtn = document.getElementById('rematchBtn');
        const continueBtn = document.getElementById('continueBtn');
        const hintBtn = document.getElementById('hintBtn');
        const aiDifficulty = document.getElementById('aiDifficulty');

        const ensureGrid = (rows, cols) => {
            if (!grid) return;
            if (grid.children.length === rows * cols) return;
            grid.innerHTML = '';
            for (let r = 0; r < rows; r++) {
                for (let c = 0; c < cols; c++) {
                    const cell = document.createElement('div');
                    cell.className = 'cell';
                    cell.dataset.row = r;
                    cell.dataset.col = c;
                    cell.addEventListener('click', () => AndroidBridge.emit('connectFourDrop', String(c)));
                    grid.appendChild(cell);
                }
            }
        };

        const bindDropHandlers = () => {
            grid?.querySelectorAll('.cell').forEach(cell => {
                const col = parseInt(cell.dataset.col || '0', 10);
                cell.onclick = () => AndroidBridge.emit('connectFourDrop', String(col));
            });
        };

        startBtn?.addEventListener('click', () => AndroidBridge.emit('connectFourReset', null));
        resetBtn?.addEventListener('click', () => AndroidBridge.emit('connectFourReset', null));
        rematchBtn?.addEventListener('click', () => AndroidBridge.emit('connectFourReset', null));
        continueBtn?.addEventListener('click', () => AndroidBridge.emit('backToLobby', null));
        hintBtn?.addEventListener('click', () => AndroidBridge.emit('connectFourHint', null));
        aiDifficulty?.addEventListener('change', (ev) => {
            const level = parseInt(ev.target.value || '2', 10);
            AndroidBridge.emit('setDifficulty', JSON.stringify({ target: 'connect4', level: level }));
        });

        window.renderConnectFour = function(state) {
            const rows = state.board?.length || 0;
            const cols = rows ? state.board[0].length : 0;
            ensureGrid(rows, cols);
            bindDropHandlers();

            const colors = { 1: 'cyan', 2: 'magenta' };
            for (let r = 0; r < rows; r++) {
                for (let c = 0; c < cols; c++) {
                    const val = state.board[r][c];
                    const cell = grid?.querySelector(`[data-row="\${r}"][data-col="\${c}"]`);
                    if (!cell) continue;
                    cell.innerHTML = '';
                    cell.classList.remove('winner');
                    if (val !== 0) {
                        const chip = document.createElement('div');
                        chip.className = 'chip ' + (colors[val] || 'cyan');
                        cell.appendChild(chip);
                    }
                }
            }

            if (state.winningLine) {
                state.winningLine.forEach(item => {
                    const row = item[0];
                    const col = item[1];
                    const cell = grid?.querySelector(`[data-row="\${row}"][data-col="\${col}"]`);
                    if (cell) cell.classList.add('winner');
                });
            }

            if (indicator) {
                if (state.hintColumn !== null && state.hintColumn !== undefined && cols > 0) {
                    indicator.style.display = 'block';
                    indicator.style.left = `\${((state.hintColumn + 0.5) / cols) * 100}%`;
                } else {
                    indicator.style.display = 'none';
                }
            }

            document.getElementById('player1Score')?.textContent = state.playerScore ?? 0;
            document.getElementById('player2Score')?.textContent = state.aiScore ?? 0;
            document.getElementById('moveCount')?.textContent = state.lastDrop ? String(state.lastDrop.column + 1) : '0';
            if (aiDifficulty && state.difficulty) {
                aiDifficulty.value = String(state.difficulty);
            }

            if (victory) {
                const isWin = state.winner && state.winner !== 0;
                victory.classList.toggle('active', !!isWin);
                const title = document.getElementById('victoryTitle');
                if (title && isWin) {
                    title.textContent = state.winner === 1 ? 'PLAYER WINS' : 'AI WINS';
                }
            }
        };
    })();
"""

private const val BALLSORT_BINDINGS = """
    (function(){
        if (window.__ballSortBound) return;
        window.__ballSortBound = true;
        const container = document.getElementById('tubesContainer');
        const startBtn = document.getElementById('startBtn');
        const resetBtn = document.getElementById('resetBtn');
        const undoBtn = document.getElementById('undoBtn');
        const hintBtn = document.getElementById('hintBtn');
        const modeLabel = document.getElementById('gameModeDisplay');

        startBtn?.addEventListener('click', () => {
            const paused = document.body.classList.toggle('ballsort-paused');
            AndroidBridge.emit('ballSortPause', String(paused));
        });
        resetBtn?.addEventListener('click', () => AndroidBridge.emit('ballSortReset', null));
        undoBtn?.addEventListener('click', () => AndroidBridge.emit('ballSortUndo', null));
        hintBtn?.addEventListener('click', () => AndroidBridge.emit('ballSortHint', null));

        document.querySelectorAll('.difficulty-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                document.querySelectorAll('.difficulty-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                const mode = btn.dataset.difficulty || 'medium';
                const level = mode === 'easy' ? 1 : mode === 'hard' ? 3 : 2;
                AndroidBridge.emit('setDifficulty', JSON.stringify({ target: 'ballsort', level: level }));
            });
        });

        const soundToggle = document.getElementById('soundToggle');
        const musicToggle = document.getElementById('musicToggle');
        const animToggle = document.getElementById('animToggle');
        soundToggle?.addEventListener('change', () => AndroidBridge.emit('audioSettings', JSON.stringify({ sound: soundToggle.checked })));
        musicToggle?.addEventListener('change', () => AndroidBridge.emit('audioSettings', JSON.stringify({ music: musicToggle.checked })));
        animToggle?.addEventListener('change', () => AndroidBridge.emit('audioSettings', JSON.stringify({ animations: animToggle.checked })));

        window.renderBallSort = function(state){
            if (!container) return;
            container.innerHTML='';
            const tubes = state.tubes || [];
            tubes.forEach((tube, index)=>{
                const tubeEl=document.createElement('div');
                tubeEl.className='tube';
                tubeEl.dataset.index=index;
                tubeEl.onclick=()=>AndroidBridge.emit('ballSortSelect', String(index));
                if (state.selectedTube === index) {
                    tubeEl.classList.add('selected');
                }
                if (state.hint && (state.hint.from === index || state.hint.to === index)) {
                    tubeEl.classList.add('hint');
                }
                tube.forEach((color, pos)=>{
                    const ball=document.createElement('div');
                    ball.className='ball';
                    ball.style.background=color;
                    ball.style.boxShadow=`0 0 20px \${color}`;
                    ball.style.bottom=`\${380 - (pos*90)}px`;
                    tubeEl.appendChild(ball);
                });
                container.appendChild(tubeEl);
            });
            const moves=document.getElementById('totalMoves') || document.getElementById('p1Moves');
            if (moves) moves.textContent=state.moves||0;
            const bestMoves = state.bestMoves === null || state.bestMoves === undefined ? '-' : state.bestMoves;
            document.getElementById('p2Moves')?.replaceChildren(document.createTextNode(bestMoves));
            const completedCount = tubes.filter(t=>t.length>0 && t.every((v,i,a)=>v===a[0])).length;
            const tubeTotal = tubes.length;
            document.getElementById('completedTubes')?.replaceChildren(document.createTextNode(`\${completedCount}/\${tubeTotal}`));
            if (modeLabel) modeLabel.textContent = (state.difficulty || 'MEDIUM').toString().toUpperCase();
            const victory=document.getElementById('victoryScreen');
            if (victory) victory.classList.toggle('active', !!state.isCompleted);
            document.body.classList.toggle('ballsort-paused', !!state.isPaused);
            if (startBtn) {
                const label = state.isPaused ? 'RESUME' : (state.moves > 0 ? 'PAUSE' : 'START');
                const icon = state.isPaused || state.moves === 0 ? 'play' : 'pause';
                startBtn.innerHTML = `<i class="fas fa-\${icon}"></i> \${label}`;
            }
        };
    })();
"""
