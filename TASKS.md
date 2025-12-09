# TASKS.md – Neon ConnectSort Refactor & Stabilization Plan

Project: **KonnxtNstaxK_v1.0-main-main**  
Package: `com.neon.connectsort`  
Module: `app`

This file defines a **systematic, multi-phase task list** for stabilizing, polishing, and expanding the Neon ConnectSort project.

Each task is written so an AI coding agent or a human dev can pick it up and complete it **end-to-end** without guessing.

---

## 0. How To Use This File

- Each task has a checkbox: `- [ ]` → not done, `- [x]` → done.
- When an AI agent picks up a task, it should:
  1. Mark it as `IN_PROGRESS` in a comment.
  2. Edit the specified files.
  3. Run the listed commands.
  4. Update this file: mark the checkbox as `[x]` and add a short note under **Notes**.

Example update pattern:

```markdown
- [x] P1.1 – Unify BallSort game state ⇨ **Completed by Codex**, 2025-12-08
  - Notes: Fixed move validation, added solved state, updated ViewModel to use StateFlow.
PHASE 1 – Game Logic Stability (CRITICAL)

Focus: Make Ball Sort + Multiplier 100% stable and correctly synced with the UI.

P1.1 – Unify Ball Sort game state with UI

Status: - [ ]
Files:

app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/BallSortViewModel.kt

app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt

Tasks:

Ensure BallSortGame:

Uses BaseGameState fields consistently (score, moves, turnCount, isGameOver, result).

Correctly sets result = GameResult.WIN when solved and GameResult.IN_PROGRESS otherwise.

Update BallSortViewModel to:

Expose a StateFlow<BallSortUiState> (or equivalent) that includes tubes, selectedTube, moves, score, and gameResult.

Call emit() / update { ... } after every move and reset.

Update BallSortScreen to:

React to gameResult and show win/lose banners.

Properly call viewModel.resetLevel() or equivalent on reset button.

Verification:

Run: ./gradlew :app:testDebugUnitTest --tests "*BallSortGameTest*"

Manually:

Launch Ball Sort.

Play through until solved.

Confirm UI updates instantly and reset works without crashes.

P1.2 – Harden Ball Sort move validation

Status: - [ ]
Files:

app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

Tasks:

Add safe guards to:

Reject moves where from or to are out of bounds.

Reject moves when from == to.

Reject moves when source tube is empty or destination tube is full.

Replace any raw index access with validated helper functions, e.g.:

Ensure invalid moves do not mutate game state or crash.

Verification:

Extend BallSortGameTest with invalid move cases:

Out of range indices.

Moves from empty tube.

Moves to full tube.

Run: ./gradlew :app:testDebugUnitTest --tests "*BallSortGameTest*"

P1.3 – Synchronize Multiplier game state & UI

Status: - [ ]
Files:

app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt

app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt

Tasks:

Ensure MultiplierGame:

Correctly uses BaseGameState fields for score, moves, isGameOver, result.

Sets result = GameResult.LOSE when lives are exhausted, and GameResult.WIN when player cashes out successfully.

Update MultiplierViewModel to:

Expose a single StateFlow<MultiplierUiState> including board, lives, multiplier, streak, lastEvent, and result.

Reset all relevant fields on reset() and emit new state.

Update MultiplierScreen:

Show clear “Game Over” and “You Won” states based on result.

Disable interactions when isGameOver == true.

Verification:

Run: ./gradlew :app:testDebugUnitTest --tests "*MultiplierGameTest*"

Manual: play multiple rounds, including losing all lives and cashing out.

PHASE 2 – Unified Theming & Neon Effects (HIGH)

Focus: Make the game look consistently neon/holographic across all screens.

P2.1 – Clean up theme resources and remove dead file

Status: - [ ]
Files:

app/src/main/res/values/themes.xml

app/src/main/res/values/styles.xml

app/src/main/java/com/neon/connectsort/ui/theme/NeonGameTheme.kt

Tasks:

Either:

Remove empty themes.xml completely, or

Populate it with valid theme definitions and reference them from styles.xml.

Ensure AndroidManifest.xml uses @style/Theme.NeonConnectSort as the app theme.

Align Compose NeonGameTheme with XML theme:

Colors and typography should match.

Verification:

Project builds with no theme-related warnings.

App launches with consistent status bar/nav bar colors.

P2.2 – Standardize neon color palette & glow

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/theme/Colors.kt

app/src/main/java/com/neon/connectsort/ui/theme/Effects.kt

Tasks:

Consolidate all neon colors into a single object NeonColors.

Replace any .value / ULong conversions with Color(0xFFxxxxxx.toInt()) style or Color(0xFFxxxxxx).

Add reusable modifiers:
fun Modifier.holoButton(): Modifier
fun Modifier.holoCard(): Modifier

Use HolographicGradients for button and card backgrounds.

Verification:

All screens compile with no color-type issues.

Visual check: Lobby, game modes, and settings all share the same neon look.

P2.3 – Apply holographic components across screens

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/ShopScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/SettingsScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/ConnectFourScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt

Tasks:

Replace plain Button containers with Modifier.holoButton().

Wrap major content sections (e.g. score panels, difficulty display) in holoCard().

Ensure typography is consistent using definitions from Typography.kt.

Verification:

App runs and all major screens show holographic borders and neon gradients.

No mismatched padding / text sizes between screens.

PHASE 3 – Data Persistence & Difficulty (HIGH)

Focus: Make difficulty and high scores persistent and actually used by the games.

P3.1 – Extend AppPreferencesRepository for difficulty and scores

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt

Tasks:

Add functions:
suspend fun saveDifficulty(level: Int)
fun observeDifficulty(): Flow<Int>

suspend fun saveBestScore(mode: String, score: Int)
fun observeBestScore(mode: String): Flow<Int>

Implement using the existing underlying DataStore / preferences mechanism.

Verification:

Add or update AppPreferencesRepositoryTest to cover these new functions.

Run: ./gradlew :app:testDebugUnitTest --tests "*AppPreferencesRepositoryTest*"

P3.2 – Wire difficulty to Settings and game ViewModels

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/SettingsViewModel.kt

app/src/main/java/com/neon/connectsort/ui/screens/SettingsScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/BallSortViewModel.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt

Tasks:

In SettingsViewModel:

Read difficulty from AppPreferencesRepository.observeDifficulty() and expose it as StateFlow<Int>.

Provide a setDifficulty(level: Int) function that calls saveDifficulty(level).

In game ViewModels:

On init, subscribe to difficulty and apply to BallSortGame / MultiplierGame (using GameDifficulty.fromLevel(level)).

In SettingsScreen:

Make the difficulty slider or selector reflect the current difficulty from the ViewModel.

Verification:

Change difficulty in Settings.

Start Ball Sort / Multiplier, confirm different behavior (e.g., more/less complexity).

Restart app → difficulty persists.

P3.3 – Persist and display best scores

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/LobbyViewModel.kt

app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt

Tasks:

In LobbyViewModel:

Observe best scores for each mode via repository ("connect4", "ballsort", "multiplier").

Expose these as part of a LobbyUiState.

In LobbyScreen:

Display best scores in game cards.

In game ViewModels:

When a game is won with a better score, update via saveBestScore.

Verification:

Win each game mode with some score.

Verify Lobby shows updated best scores.

Restart app → scores persist.

PHASE 4 – Navigation & Transitions (MODERATE)

Focus: Smooth, animated navigation between screens.

P4.1 – Reintroduce AnimatedNavHost with Accompanist

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt

app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt

build.gradle.kts (app module, if dependency missing)

Tasks:

Ensure accompanist-navigation-animation dependency is present.

Replace NavHost with AnimatedNavHost.

Add enter/exit transitions:

Lobby → Game screens: slide in from right, fade in.

Game → Lobby: slide out to left.

Lobby ↔ Settings/Shop: fade transitions.

Verification:

App compiles and runs.

No nav-related crashes.

Transitions play correctly when switching screens.

PHASE 5 – UI / Layout Consistency (MODERATE)

Focus: Make Lobby, Shop, Settings feel like one cohesive neon suite.

P5.1 – Normalize paddings, fonts, and layout grid

Status: - [ ]
Files:

app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/ShopScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/SettingsScreen.kt

Tasks:

Standardize main content padding to 16.dp.

Use heading typography for titles, body for descriptions, consistent sizes for buttons.

Use a consistent grid/column layout (e.g. Column with verticalArrangement = spacedBy(12.dp)).

Verification:

Visual check: all three screens have the same spacing and typography system.

No cramped or misaligned elements.

PHASE 6 – Testing & CI (LOW)

Focus: Confidence and regression protection.

P6.1 – Extend unit tests for games and preferences

Status: - [ ]
Files:

app/src/test/java/... (existing tests)

build.gradle.kts (if any test plugins needed)

Tasks:

Extend existing tests to cover:

Ball Sort invalid move handling.

Multiplier lose/win conditions.

Difficulty persistence and retrieval.

Ensure tests are deterministic (no randomness without a fixed seed).

Verification:

Run: ./gradlew :app:testDebugUnitTest.

All tests pass.

P6.2 – Ensure CI runs full build + tests

Status: - [ ]
Files:

.github/workflows/android-ci.yml

Tasks:

Verify workflow runs:

./gradlew clean lint test assembleDebug

Optionally add coverage reporting:

Apply jacoco or kover and upload reports as CI artifacts.

Verification:

Push to a branch → GitHub Actions workflow passes.

PHASE 7 – Manifest & Resource Cleanup (LOW)

Focus: Polish and remove landmines.

P7.1 – Clean manifest & adaptive icons

Status: - [ ]
Files:

app/src/main/AndroidManifest.xml

app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml

app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml

Tasks:

Remove any duplicate permissions (e.g., multiple INTERNET entries).

Confirm app theme and splash theme are correct and consistent.

Ensure launcher icons are valid and load correctly.

Verification:

Build and install APK.

Check launcher icon and app name on device.

Confirm there are no manifest-related warnings in the build output.

FINAL VERIFICATION

Once all tasks above have been checked off:

Run full build:
./gradlew clean assembleDebug
Run all tests:
./gradlew lint test

Manually verify:

All three game modes:

Launch, play, win/lose, reset, and reflect difficulty properly.

Lobby:

Shows difficulty and best scores per mode.

Settings:

Difficulty + sound/vibration persist across app restarts.

Visuals:

All screens share consistent neon/holographic feel and smooth navigation animations.

When all of the above are true, this phase of the project can be considered stable and ready for the next feature wave (story mode, online play, lobby chat, etc.).

Next natural move after this is you start assigning specific blocks (e.g., “PHASE 1 – P1.1 + P1.2”) to Codex/Gemini with a short wrapper prompt like:

> “Open `docs/TASKS.md`, focus on P1.1 and P1.2 only. Do not touch anything else yet…”

That keeps the AI from free-styling and forces it to chew through the project in a sane order.
