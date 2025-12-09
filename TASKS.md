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
    - **Edit only the files listed**, plus any strictly necessary supporting files.
    - **Run the verification commands** at the end of the task.
    - If you add new files, **append them** to this document under the relevant task.
    - Add a short **Notes** bullet when marking `[DONE]`.

---

## Phase 0 – Baseline Sanity Check

### 0.1 Project opens and builds cleanly

**Status:** `[DONE]`  
**Goal:** Ensure the project compiles and runs before deeper refactors.

**Files to look at:**
- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/neon/connectsort/MainActivity.kt`

**Actions:**
1. Open project in Android Studio.
2. Confirm Kotlin & Gradle versions are compatible with the installed Android Studio.
3. Fix any obvious red errors (mismatched package names, missing resources).
4. Ensure `applicationId` in `app/build.gradle.kts` matches manifest package `com.neon.connectsort`.

**Verification:**
```bash
./gradlew clean assembleDebug

Done when:

    assembleDebug completes without errors.

Notes:

    Already completed. Do not regress build sanity.

Phase 1 – Game Logic Stabilization
1.1 Ball Sort – crash and rules audit

Status: [DONE]
Goal: Ball Sort mode never crashes, follows clear rules, and is restartable.

Files:

    app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

    app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/BallSortViewModel.kt

    app/src/test/java/com/neon/game/ballsort/BallSortGameTest.kt

Actions (completed):

    Audit BallSortGame for unsafe index operations and fix them.

    Add a clear reset() function that regenerates a valid puzzle state.

    Ensure BallSortScreen uses the ViewModel and never manipulates game logic directly.

    Ensure BallSortViewModel exposes immutable UI state and mediates actions.

Verification:

./gradlew :app:testDebugUnitTest --tests "*BallSort*"
./gradlew assembleDebug

Done when:

    Ball Sort game runs repeatedly without crashes.

    Restart always yields a consistent playable state.

Notes:

    Completed in initial refactor. Do not introduce crashes or new rules without tests.

1.2 Multiplier mode – fully functional

Status: [DONE]
Goal: Multiplier game is playable, with visible scoring and difficulty.

Files:

    app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt

    app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt

    app/src/test/java/com/neon/game/multiplier/MultiplierGameTest.kt

Actions (completed):

    Implement core Multiplier state model (score, multiplier, streaks, lives, etc.).

    Wire from MultiplierGame → MultiplierViewModel → MultiplierScreen.

    Show score/multiplier/difficulty in UI and support restart.

    Add tests for scoring, difficulty, and reset logic.

Verification:

./gradlew :app:testDebugUnitTest --tests "*Multiplier*"
./gradlew assembleDebug

Done when:

    Multiplier mode can be launched, played, and restarted from the UI.

    Unit tests cover core logic and pass.

Notes:

    Completed in initial refactor. Keep behavior stable, extend only with tests.

1.3 Shared game base / utilities

Status: [DONE]
Goal: Reduce duplication across games and centralize common logic (score, moves, difficulty, game result).

Files:

    app/src/main/java/com/neon/game/connectfour/ConnectFourGame.kt

    app/src/main/java/com/neon/game/ballsort/BallSortGame.kt

    app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt

    New or existing: app/src/main/java/com/neon/game/common/BaseGameState.kt

    New or existing: app/src/main/java/com/neon/game/common/GameResult.kt

    New or existing: app/src/main/java/com/neon/game/common/GameDifficulty.kt

    Tests under app/src/test/java/com/neon/game/

Actions:

    Identify repeated concepts in all three games:

        Score, moves, turn count.

        Win/lose/in-progress state.

        Difficulty level or scaling.

    Create or refine common types in game/common/:

        BaseGameState holding common fields and reset() contract.

        GameResult enum (e.g., IN_PROGRESS, WIN, LOSE).

        GameDifficulty enum or sealed class with mapping from easy/normal/hard.

    Refactor games to use shared types:

        ConnectFourGame, BallSortGame, MultiplierGame either:

            Extend BaseGameState, or

            Contain a BaseGameState instance and delegate fields.

        Each game updates gameResult, score, moves, isGameOver via the shared API.

    Update tests:

        Make sure tests compile and validate:

            Win conditions.

            Reset behavior.

            Difficulty effects, if applicable.

Verification:

./gradlew :app:testDebugUnitTest
./gradlew assembleDebug

Done when:

    No duplicated “score/moves/difficulty/game result” structures in game files.

    All games compile, run, and tests pass using the shared common types.

Notes:

    Added `BaseGameState`, `GameResult`, and `GameDifficulty` to centralize score/move/difficulty/result tracking, and rewired all three game implementations plus ConnectFourViewModel to rely on the shared state and lifecycle helpers (winner/result updates, resets, difficulty propagation).

Phase 2 – Navigation & ViewModels
2.1 Navigation graph correctness

Status: [DONE]
Goal: All screens are reachable with consistent routes and back-stack behavior.

Files:

    app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt

    app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt

    All ui/screens/*Screen.kt

Actions:

    Confirm AppDestinations contains routes for:

        Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips (or equivalent).

    Ensure NeonGameApp:

        Sets up a single NavHost or AnimatedNavHost with those routes.

    Standardize navigation:

        All navigation uses constants from AppDestinations (no magic strings).

        Back navigation pops correctly to Lobby or the appropriate previous screen.

Verification:

./gradlew assembleDebug

Done when:

    You can navigate Lobby → any mode → Settings/Shop → back without crashes or dead ends.

    No unreachable screens or orphan routes.

Notes:

    Added a `routeWithArgs`/`defaultLevel` declaration for `AppDestination.BallSort` and rewired the NavHost to consume it; level-complete and game-over overlays now call `navController.toLobby()` to keep Lobby as the root. Verified via `./gradlew assembleDebug`.
    If using AnimatedNavHost, keep transitions simple and stable.

2.2 ViewModel wiring and lifecycle

Status: [DONE]
Goal: Every stateful screen uses a ViewModel correctly; state survives rotation where appropriate.

Files:

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/*.kt

    Associated app/src/main/java/com/neon/connectsort/ui/screens/*Screen.kt

Actions:

    Ensure each main screen has a ViewModel:

        Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips.

    Instantiate ViewModels properly in composables:

        Use viewModel() / hiltViewModel() instead of manual creation.

    Move business logic out of composables:

        Screens read uiState from ViewModels and call ViewModel event methods.

    Verify configuration changes:

        Rotate the emulator and confirm expected state is preserved or reset by design.

Verification:

./gradlew :app:testDebugUnitTest
./gradlew assembleDebug

Done when:

    No composable directly manipulates game classes or repositories.

    No crashes or unexpected resets on rotation.

Notes:

    Audited every UI screen to confirm it pulls its ViewModel from `NeonGameApp` and only reads `uiState`/dispatches events; documented the decision in `docs/DECISIONS.md`. Verified via `./gradlew :app:testDebugUnitTest` and `./gradlew assembleDebug`.
    Document any ViewModel scoping or lifecycle decisions in docs/DECISIONS.md.

Phase 3 – Theming & Holographic UI
3.1 Unified neon color system

Status: [DONE]
Goal: One canonical source of truth for neon colors with no type mismatches or ad-hoc palettes.

Files:

    app/src/main/java/com/neon/connectsort/ui/theme/Colors.kt

    app/src/main/java/com/neon/connectsort/ui/theme/NeonGameTheme.kt

    app/src/main/java/com/neon/connectsort/ui/theme/Effects.kt

    app/src/main/java/com/neon/connectsort/MainActivity.kt

Actions:

    Create or finalize a NeonColors object:

        Define primary, secondary, accent, glow, background, error, etc.

    Fix any usage of .value on Color that causes type issues:

        Only convert to Int where system APIs require it (e.g., system bars).

    Ensure NeonGameTheme:

        Wraps the entire NeonGameApp call chain.

        Uses a consistent palette across all screens.

    Clean up themes.xml/styles.xml if needed so the XML theme matches the Compose theme.

Verification:

./gradlew assembleDebug

Done when:

    No color type crashes (ULong vs Int).

    Visual style is consistently neon/cyberpunk across screens.

Notes:

    Added `NeonPalette` tokens in `Colors.kt`, rewired `NeonGameTheme` to consume them, and reused `NeonPalette.background` inside `MainActivity` so the system bars match the neon palette while documentation records the final palette.

3.2 Holographic components library

Status: [DONE]
Goal: Use shared components for neon/holographic buttons, cards, and panels instead of reimplementing per screen.

Files:

    app/src/main/java/com/neon/connectsort/ui/components/HolographicComponents.kt (or create it)

    All ui/screens/*Screen.kt

Actions:

    Define reusable composables:

        NeonButton, NeonCard, NeonPanel, etc., using gradients and glow from Effects.kt.

    Apply these components to:

        Lobby primary actions.

        Mode selection buttons.

        Major panels (scores, settings sections, shop items).

    Remove duplicated ad-hoc neon styles in screens and replace with shared components.

Verification:

./gradlew assembleDebug

Done when:

    Most screens rely on shared holographic components for primary UI elements.

    Styling changes can be made in one place and propagate app-wide.

Notes:

    Moved the holographic widgets into `ui.components.HolographicComponents`, and updated Lobby/Shop/Settings/CharacterChips to import the shared buttons/cards so styling changes flow from a single library (see docs/DECISIONS.md).

Phase 4 – Persistence & Progression
4.1 Preferences & progression integration

Status: [TODO]
Goal: Difficulty, audio settings, and chip unlocks persist between sessions and drive gameplay.

Files:

    app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/SettingsViewModel.kt

    Any ViewModels consuming difficulty or unlock data (Lobby, game ViewModels, etc.)

Actions:

    Ensure AppPreferencesRepository uses DataStore or SharedPreferences reliably.

    Implement methods for:

        Difficulty: getDifficultyFlow(), setDifficulty(level)

        Unlocks: getUnlockedChipsFlow(), unlockChip(id)

        Audio: getAudioSettingsFlow(), setAudioSettings(...)

    Wire SettingsViewModel:

        To read these flows and expose combined SettingsUiState.

        To write changes back when user updates controls.

    Wire game ViewModels (and Lobby if needed):

        To observe difficulty and best scores.

        To update repository when new best scores or unlocks are earned.

Verification:

./gradlew :app:testDebugUnitTest --tests "*Preferences*"
./gradlew assembleDebug

Done when:

    Difficulty and audio changes persist across app restarts.

    Unlocks remain unlocked; Lobby or other screens reflect progression.

Notes:

    Document key names and data model shape in docs/DECISIONS.md.

Phase 5 – Tests & CI
5.1 Game unit tests

Status: [TODO]
Goal: Each game has solid test coverage for core rules and shared base behavior.

Files:

    app/src/test/java/.../ConnectFourGameTest.kt

    app/src/test/java/.../BallSortGameTest.kt

    app/src/test/java/.../MultiplierGameTest.kt

    Any tests for game/common/ types.

Actions:

    Ensure tests cover:

        Win/lose/in-progress detection.

        Reset behavior.

        Difficulty effects (where applicable).

    Add tests for shared base types:

        BaseGameState (if non-trivial).

        GameDifficulty mapping.

        GameResult behavior if there’s logic around it.

Verification:

./gradlew :app:testDebugUnitTest

Done when:

    All tests pass.

    You can refactor internal game logic with confidence.

Notes:

    Keep tests pure-JVM; no Android framework dependencies.

5.2 Basic CI workflow

Status: [TODO]
Goal: CI ensures no broken commits land in main.

Files:

    New or existing: .github/workflows/android-ci.yml

Actions:

    Add or refine CI workflow:

        Checkout repo.

        Use Gradle build action.

        Run:

            ./gradlew clean lint test assembleDebug

    Keep YAML minimal and robust; avoid secret dependencies.

Verification:

    Push to GitHub and confirm the CI job runs and passes.

Done when:

    Every push/PR runs CI, and failing builds are visible in GitHub.

Notes:

    Document CI expectations (which branches, which jobs) in docs/DECISIONS.md if needed.

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

        ACCESS_NETWORK_STATE (if you use it)

    Remove unused permissions and legacy attributes.

Verification:

./gradlew assembleDebug

Done when:

    No manifest-related build warnings.

    Theme and package are consistent.

Notes:

    Any new permissions must be justified in docs/DECISIONS.md.

6.2 Icons and branding

Status: [TODO]
Goal: The app has proper adaptive icons and a consistent name.

Files:

    app/src/main/res/mipmap-*/*

    app/src/main/res/values/strings.xml

Actions:

    Define app name in strings.xml (e.g., "Neon Connect & Sort").

    Configure adaptive icons:

        ic_launcher.xml referencing foreground/background drawables.

    Ensure manifest android:icon and android:label use the new resources.

Verification:

./gradlew assembleDebug

Done when:

    App installs with correct name and icon on device/emulator.

Notes:

    Keep brand assets consistent across debug/release builds.

Phase 7 – Visual Polish & UX (Optional but Recommended)
7.1 Consistent lobby and mode selection UX

Status: [TODO]
Goal: Lobby feels like the central hub of a neon arcade, not just a static menu.

Files:

    app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

    Shared holographic components.

Actions:

    Use holographic components for all lobby actions.

    Show basic player stats:

        Best scores per mode.

        Current difficulty.

        Possibly last-played mode.

    Highlight locked/unlocked chips or modes visually (if progression is wired).

Verification:

./gradlew assembleDebug

Done when:

    Lobby clearly presents options and progress.

    Visual style is consistent with the rest of the neon/holographic UI.

Notes:

    Treat this as polish once core stability, persistence, and tests are solid.


That gives you:

- A **follow-up prompt** that tells Gemini exactly what to do next, in order.
- An **updated TASKS.md** that matches that strategy and stays compatible with what you’ve already fed it.

From here, the workflow is: drop in the new `TASKS.md`, feed Gemini the follow-up prompt, and let it chew through 1.3 → 2.x → 3.x in a controlled way instead of flailing across the codebase.
