# TASKS.md – KonnxtNstaxK_v1.0-main

**Project:** Neon Connect & Sort  
**Root:** `KonnxtNstaxK_v1.0-main/`  
**App module:** `app/`  
**Package:** `com.neon.connectsort`  

This file defines a **sequential, production-focused task list** for stabilizing and expanding the project.  
Tasks are grouped by phase and written so an AI agent or human dev can execute them reliably.

---

## Conventions

- **Status tags:**
  - `[TODO]` = not started
  - `[WIP]`  = in progress
  - `[DONE]` = completed and verified
- For each task:
  - **Edit only the files listed.**
  - **Run the verification commands** at the end of the task.
  - If you add new files, **append them** to this document under the relevant task.

---

## Phase 0 – Baseline Sanity Check

### 0.1 Project opens and builds cleanly

**Status:** `[TODO]`  
**Goal:** Ensure the project compiles and runs before deeper refactors.

**Files to look at:**
- `settings.gradle.kts`
- `build.gradle.kts` (root + `app/`)
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/neon/connectsort/MainActivity.kt`

**Actions:**
1. Open project in Android Studio.
2. Confirm Kotlin & Gradle versions are compatible with your installed Android Studio.
3. Fix any obvious red errors (mismatched package names, missing resources).
4. Ensure `applicationId` in `app/build.gradle.kts` matches manifest package `com.neon.connectsort`.

**Verification:**
```bash
./gradlew clean assembleDebug
Done when:

assembleDebug completes without errors.

Phase 1 – Game Logic Stabilization
1.1 Ball Sort – crash and rules audit

Status: [TODO]
Goal: Ball Sort mode never crashes, follows clear rules, and is restartable.

Files:

app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt

(If present) app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/BallSortViewModel.kt

Actions:

Audit BallSortGame:

Identify all operations that index into collections or stacks.

Add checks so no invalid index or empty-pop can occur.

Add a clear reset() function that regenerates a valid initial puzzle state.

Define and document rules in code comments:

Max stack height.

Allowed moves.

Win condition.

Ensure BallSortScreen:

Calls reset() from restart UI actions.

Never directly mutates game state without going through ViewModel/logic methods.

If there is a BallSortViewModel:

Expose immutable UI state (e.g., StateFlow or MutableState).

Provide functions like onTubeSelected(fromIndex, toIndex) that call BallSortGame.

Verification:
./gradlew :app:testDebugUnitTest --tests "*BallSort*"
./gradlew assembleDebug


Done when:

Ball Sort game runs repeatedly without crashes.

Restart always yields a consistent playable state.

1.2 Multiplier mode – fully functional

Status: [TODO]
Goal: Multiplier game is actually playable, with visible scoring and difficulty.

Files:

app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt

app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt

app/src/test/java/.../MultiplierGameTest.kt (or create it)

Actions:

In MultiplierGame.kt:

Define core state model (current value, multiplier, streaks, etc.).

Implement methods: start(difficulty), applyMove(...), reset(), isGameOver().

In MultiplierViewModel:

Wrap MultiplierGame and expose UI state and intents:

uiState, onUserAction(...), onRestart().

Make sure difficulty is configurable (easy/normal/hard).

In MultiplierScreen:

Render score, current multiplier, and any timers/turns left.

Wire buttons/controls to ViewModel actions, not directly to MultiplierGame.

Create/extend MultiplierGameTest.kt:

Test scoring logic, edge cases, and reset behavior.

Verification:

./gradlew :app:testDebugUnitTest --tests "*Multiplier*"
./gradlew assembleDebug


Done when:

Multiplier mode can be launched, played, and restarted from the UI.

Unit tests cover core logic and pass.

1.3 Shared game base / utilities

Status: [TODO]
Goal: Reduce duplication across games and centralize common logic.

Files:

app/src/main/java/com/neon/game/connectfour/ConnectFourGame.kt

app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt

New file: app/src/main/java/com/neon/game/common/BaseGameState.kt (or similar)

Actions:

Identify repeated concepts:

Score, moves, turn count, difficulty.

Create BaseGameState or a small set of common types in game/common/:

e.g., GameDifficulty, GameResult, shared reset() / isGameOver().

Refactor each game to use the shared types without breaking semantics.

Verification:
./gradlew :app:testDebugUnitTest
./gradlew assembleDebug


Done when:

No copy-paste of basic game structures across game files.

All games still work and tests pass.

Phase 2 – Navigation & ViewModels
2.1 Navigation graph correctness

Status: [TODO]
Goal: All screens are reachable with consistent routes and back-stack behavior.

Files:

app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt

app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt

All ui/screens/*Screen.kt

Actions:

Confirm AppDestinations contains routes for:

Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips, etc.

Ensure NeonGameApp:

Sets up a single NavHost or AnimatedNavHost with those routes.

Standardize navigation:

All navigation calls use route constants from AppDestinations.

Back navigation pops correctly to Lobby or previous screen.

Verification:

./gradlew assembleDebug


Done when:

You can move between Lobby → all modes → Settings → Shop → back without crashes or dead ends.

2.2 ViewModel wiring and lifecycle

Status: [TODO]
Goal: Every screen with non-trivial state has a ViewModel, and state is not lost on rotation.

Files:

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/*.kt

Associated *Screen.kt files.

Actions:

Ensure each main screen has a ViewModel:

Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips.

Instantiate ViewModels correctly:

Use hiltViewModel() or viewModel() inside composables, not manual singletons.

Move business logic out of composables:

Keep composables as “dumb views” that render uiState and trigger events.

Confirm state survives configuration changes (rotate emulator).

Verification:
./gradlew :app:testDebugUnitTest
./gradlew assembleDebug

Done when:

Rotating the device doesn’t reset games or settings unexpectedly.

No composable directly mutates underlying game logic classes.

Phase 3 – Theming & Holographic UI
3.1 Unified neon color system

Status: [TODO]
Goal: One canonical source of truth for neon colors with no type mismatches.

Files:

app/src/main/java/com/neon/connectsort/ui/theme/Colors.kt

app/src/main/java/com/neon/connectsort/ui/theme/NeonGameTheme.kt

app/src/main/java/com/neon/connectsort/ui/components/HolographicComponents.kt

app/src/main/java/com/neon/connectsort/MainActivity.kt

Actions:

Create or finalize NeonColors:

Define primary/secondary/accent, glow, background, surface, error, etc.

Fix any usage of .value on Color:

Convert safely: Color(neonColor.value.toInt()) only if needed for system bars.

Apply neon colors consistently:

Buttons, chips, game boards, and backgrounds all use NeonColors.

Ensure NeonGameTheme wraps the entire NeonGameApp and uses Material3 theme APIs.

Verification:
./gradlew assembleDebug

Done when:

There are no ULong vs Int color crashes.

Visual style across screens is clearly consistent and neon/cyberpunk themed.

3.2 Holographic components library

Status: [TODO]
Goal: Use shared UI components for glowing buttons, cards, panels.

Files:

app/src/main/java/com/neon/connectsort/ui/components/HolographicComponents.kt

All ui/screens/*Screen.kt

Actions:

Define reusable components:

NeonButton, NeonCard, NeonPanel, etc.

Refactor screens to use these instead of inline Button/Card definitions.

Keep API generic:

Accept modifier, onClick, enabled, content lambdas for flexibility.

Verification:
./gradlew assembleDebug

Done when:

Screens mostly use the shared holographic components.

No screen defines its own completely custom “neon” button unless truly unique.

Phase 4 – Persistence & Progression
4.1 Preferences & progression integration

Status: [TODO]
Goal: Difficulty, audio settings, and chip unlocks persist between sessions.

Files:

app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt

app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/SettingsViewModel.kt

Any ViewModel consuming difficulty or chip unlock data.

Actions:

Ensure AppPreferencesRepository uses DataStore or SharedPreferences reliably.

Implement methods:

getDifficultyFlow(), setDifficulty(...)

getUnlockedChipsFlow(), unlockChip(id: String)

getAudioSettingsFlow(), setAudioSettings(...)

Wire Settings screen to these flows:

UI sliders/toggles update repository.

Repository updates propagate back to game ViewModels.

Verification:
./gradlew :app:testDebugUnitTest --tests "*Preferences*"
./gradlew assembleDebug

Done when:

Changing difficulty or volume persists after app restart.

Chip unlocks remain unlocked across sessions.

Phase 5 – Tests & CI
5.1 Game unit tests

Status: [TODO]
Goal: Each game has solid test coverage for its core rules.

Files:

app/src/test/java/.../ConnectFourGameTest.kt

app/src/test/java/.../BallSortGameTest.kt

app/src/test/java/.../MultiplierGameTest.kt

Actions:

Write or complete tests for:

Connect-4 win detection, invalid moves.

Ball Sort valid & invalid moves, solved state.

Multiplier scoring, streaks, and reset logic.

Ensure tests don’t touch Android framework (pure JVM).

Verification:

./gradlew :app:testDebugUnitTest

Done when:

All tests pass.

You can safely refactor logic without fear of silent breakage.

5.2 Basic CI workflow

Status: [TODO]
Goal: CI ensures no broken commits land in main.

Files:

New: .github/workflows/android-ci.yml

Actions:

Add a CI workflow that:

Checks out repo.

Uses Gradle build action.

Runs: ./gradlew clean lint test assembleDebug.

Keep the YAML simple and robust; no secrets required.

Verification:

Push to GitHub and confirm the CI job runs and passes.

Done when:

Every push/PR runs CI, and failing builds are visible in GitHub.

Phase 6 – Manifest, Icons & Packaging
6.1 Manifest cleanup

Status: [TODO]
Goal: Manifest is clean, precise, and aligned with the app.

Files:

app/src/main/AndroidManifest.xml

Actions:

Confirm:

package="com.neon.connectsort"

android:theme="@style/Theme.NeonConnectSort"

Add only necessary permissions:

INTERNET

ACCESS_NETWORK_STATE (if used for online features)

Remove unused permissions and legacy attributes.

Verification:
./gradlew assembleDebug

Done when:

No manifest-related build warnings.

Theme and package are consistent.

6.2 Icons and branding

Status: [TODO]
Goal: The app has proper adaptive icons and a consistent name.

Files:

app/src/main/res/mipmap-*/*

app/src/main/res/values/strings.xml

Actions:

Define app name in strings.xml (e.g., "Neon Connect & Sort").

Set adaptive icons:

ic_launcher.xml referencing foreground/background drawables.

Update any references in manifest to use the new icons and app name.

Verification:
./gradlew assembleDebug

Done when:

App installs with correct name and icon on device/emulator.

Phase 7 – Visual Polish & UX (Optional but Recommended)
7.1 Consistent lobby and mode selection UX

Status: [TODO]
Goal: Lobby feels like the central hub of a neon arcade, not just a menu.

Files:

app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

Shared holographic components.

Actions:

Use holographic components for all lobby actions.

Show basic player stats (win counts, last mode played, difficulty).

Highlight locked/unlocked chips or modes visually.

Verification:
./gradlew assembleDebug

Done when:

Lobby conveys progress and options clearly and looks visually consistent with the rest of the game.

End of TASKS.md
