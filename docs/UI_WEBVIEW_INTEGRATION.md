# Web UI Integration

## Architecture Choice
- The project now renders the game UI through a single `WebView` hosted in `WebUiHost`. 
- The attached HTML screens (`Knxt4_lobby.html`, `Knxt4_2.html`, `ball_sort.html`) are loaded directly from `app/src/main/assets/ui`.
- JavaScript interfaces expose a compact contract so HTML actions can call Kotlin game logic and Kotlin can push state back into the DOM.

## Event Flow
- **UI → Logic:** Elements call `AndroidBridge.emit(action, payload)` (bound automatically per screen). The bridge translates actions into `WebUiEvent` values that invoke ViewModel methods (e.g., `connectFourViewModel.dropChip`, `connectFourViewModel.requestHint`, `ballSortViewModel.selectTube`, `ballSortViewModel.undoLastMove`). Audio/settings toggles emit `audioSettings` events that persist through `AppPreferencesRepository`.
- **Logic → UI:** State flows from the ViewModels are serialized to JSON and delivered with `renderConnectFour(state)`, `renderBallSort(state)`, or `renderLobby(state)` JavaScript functions. These redraw the boards, scores, difficulty labels, and victory overlays inside the HTML.
- Navigation requests (`startConnectFour`, `startBallSort`, `backToLobby`) simply swap the loaded asset URL while preserving the active logic instances.

### Mapped actions
- **Lobby:** `startConnectFour`, `startBallSort`, and `backToLobby` drive navigation. Lobby also receives wallet/high-score state via `renderLobby` to replace the placeholder counters.
- **Connect-4 board:** `connectFourDrop(column)`, `connectFourReset`, `connectFourHint`, and `setDifficulty(target='connect4', level)` map to the Kotlin ViewModel and AI helper. The render payload includes the full board, last drop, winning line, hint column, and scores.
- **Ball Sort board:** `ballSortSelect(index)`, `ballSortReset`, `ballSortUndo`, `ballSortHint`, `ballSortPause(paused)`, and `setDifficulty(target='ballsort', level)` are bound to the HTML buttons/tubes. `audioSettings` listens to sound/music/animation toggles. Render payloads include tubes, selected/hint tubes, pause state, completion flag, and best-move data.

## Adding Future HTML Screens
1. Place the new HTML (and any assets) under `app/src/main/assets/ui/`.
2. Add a new `UiScreen` entry in `WebUiHost.kt` that points to the asset URL.
3. Extend the binding/injection section with:
   - An `emit` action name for UI interactions.
   - A render function (`renderYourScreen`) that accepts JSON from Kotlin.
4. Push state updates by collecting from the relevant ViewModel and queuing `renderYourScreen(...)` through the bridge.

## Debugging Notes
- The bridge queues JavaScript until a page finishes loading to avoid missing bindings.
- To trace UI actions, log from inside `WebBridge.emit` before emitting events.
- If the UI stops updating, ensure `renderConnectFour`/`renderBallSort` remain defined in the HTML after any manual edits.
- A floating **DEBUG** toggle is injected on every page; it applies `debug-hitboxes` outlines to every interactive element so you can visually confirm the tap targets map to the Kotlin logic.
