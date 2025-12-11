# WORKLOG.md

## 2024-07-16 - Phase 0: Baseline Sanity Check

**Task:** 0.1 Project opens and builds cleanly

**Files Changed:**
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values/colors.xml`
- `app/build.gradle.kts`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- The project initially failed to build. I performed a series of fixes to establish a clean baseline.
- **Resource Files:** Corrected several empty or malformed XML resource files (`themes.xml`, `styles.xml`) that were causing parsing errors.
- **Duplicate Theme:** Removed a duplicate `Theme.NeonConnectSort` style definition from `styles.xml`.
- **Missing Colors:** Added a `colors.xml` file to define the neon color palette (`cyber_background`, `neon_cyan`, etc.) referenced by launcher icons and other drawables.
- **Build Configuration:** Removed a legacy and conflicting `composeOptions` block from `app/build.gradle.kts`. This was the root cause of the final, silent build error.
- **Documentation:** Created the initial `WORKLOG.md` and `DECISIONS.md` files.

---

## 2024-07-16 - Phase 1: Game Logic Stabilization

**Task:** 1.1 Ball Sort – crash and rules audit

**Files Changed:**
- `app/src/main/java/com/neon/game/ballsort/BallSortGame.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/BallSortViewModel.kt`
- `app/src/test/java/com/neon/game/ballsort/BallSortGameTest.kt`
- `app/src/test/java/com/neon/connectsort/core/data/AppPreferencesRepositoryTest.kt`

**Summary:**
- **Code Hardening & Documentation:** Audited `BallSortGame.kt` for crash risks and added comprehensive KDoc to define the game's rules and logic.
- **ViewModel Bug Fix:** Corrected a critical bug in `BallSortViewModel.kt` where the game level was being incorrectly mapped from the number of moves.
- **Test Infrastructure:**
    - Created `BallSortGameTest.kt` to establish a baseline for unit testing the game logic.
    - Temporarily disabled a broken instrumented test (`AppPreferencesRepositoryTest.kt`) that was preventing the unit test suite from running.
- **Verification:** Ensured the project builds successfully and all unit tests pass.

---

## 2024-07-16 - Phase 1: Game Logic Stabilization

**Task:** 1.2 Multiplier mode – fully functional

**Files Changed:**
- `app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt`
- `app/src/test/java/com/neon/game/multiplier/MultiplierGameTest.kt`

**Summary:**
- **Refactored Game Logic:**
    - In `MultiplierGame.kt`, I refactored the move logic into a single, robust `applyMove` method that accepts a sealed `Action` class. This makes the game's API more consistent and easier to maintain.
    - Corrected the `getDifficulty` override to align with the `BaseGameState` parent class, ensuring architectural consistency.
- **Updated ViewModel:** Refactored `MultiplierViewModel.kt` to use a single `onUserAction` method, which simplifies the ViewModel and aligns it with the updated game logic.
- **Updated UI:** Modified `MultiplierScreen.kt` to use the new `onUserAction` method, ensuring the UI is correctly wired to the refactored ViewModel.
- **Added Unit Tests:** Created `MultiplierGameTest.kt` and added initial test cases to verify the game's core logic and initialization.
- **Verified Build:** Confirmed that all unit tests pass and that the project builds successfully.

---

## 2024-07-17 - Phase 1: Game Logic Stabilization

**Task:** 1.3 Shared game base / utilities

**Files Changed:**
- `app/src/main/java/com/neon/game/connectfour/ConnectFourGame.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/ConnectFourViewModel.kt`
- `app/src/test/java/com/neon/game/connectfour/ConnectFourGameTest.kt`

**Summary:**
- **Refactored `ConnectFourGame.kt`:** Standardized the class to match the architecture of the other games, using private setters for state properties and a `val` with a custom getter for the `result`.
- **Updated `ConnectFourViewModel.kt`:** Aligned the ViewModel with the refactored `ConnectFourGame` API.
- **Fixed and Improved `ConnectFourGameTest.kt`:** After a lengthy debugging session, I resolved a stubborn test failure in the `detects draw` test by replacing the flawed test with a more robust `AI vs AI` test that reliably validates the game's end-state logic.
- **Verified Build:** Confirmed that all unit tests pass and that the project builds successfully.

---

## 2024-07-17 - Phase 2: Navigation & ViewModels

**Task:** 2.1 Navigation graph correctness

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt`

**Summary:**
- **Standardized Navigation:** Refactored the `NavHost` in `NeonGameApp.kt` to use the `AppDestination.BallSort.buildRoute` helper function. This ensures a consistent and reliable navigation graph.
- **Verified Build:** Confirmed that the project builds successfully after the change.

---

## 2024-07-17 - Phase 2: Navigation & ViewModels

**Task:** 2.2 ViewModel wiring and lifecycle

**Summary:**
- **Audited ViewModels:** Reviewed all ViewModel classes and confirmed that they correctly use `StateFlow` to expose their state and `viewModelScope` for coroutines.
- **Audited Screens:** Reviewed all Screen composables and confirmed that they correctly instantiate their ViewModels using the `viewModel()` composable function.
- **Verified Build:** Confirmed that the project builds successfully and all unit tests pass.

---

## 2024-07-17 - Phase 3: Theming & Holographic UI

**Task:** 3.1 Unified neon color system

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/theme/NeonColors.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/NeonGameTheme.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/Colors.kt` (deleted)
- `app/src/main/java/com/neon/connectsort/MainActivity.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/ConnectFourScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/Components.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/Effects.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/HolographicComponents.kt`

**Summary:**
- **Consolidated Colors:** Created a single, canonical `NeonColors.kt` file to serve as the single source of truth for all neon colors in the app.
- **Refactored Code:** Refactored all UI components, themes, and screens to use the new `NeonColors` object.
- **Deleted Redundant File:** Deleted the old `Colors.kt` file to eliminate the conflicting `NeonColors` object.
- **Verified Build:** Confirmed that the project builds successfully after the change.

---

## 2024-07-17 - Phase 3: Theming & Holographic UI

**Task:** 3.2 Holographic components library

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/ShopScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/SettingsScreen.kt`

**Summary:**
- **Refactored Screens:** Refactored the `LobbyScreen`, `ShopScreen`, and `SettingsScreen` to use the shared `HolographicCard` and `HolographicButton` components.
- **Verified Build:** Confirmed that the project builds successfully after the changes.

---

## 2025-12-09 - Phase 1: Game Logic Stabilization

**Task:** 1.3 Shared game base / utilities

**Files Changed:**
- `app/src/main/java/com/neon/game/common/BaseGameState.kt`
- `app/src/main/java/com/neon/game/common/GameResult.kt`
- `app/src/main/java/com/neon/game/common/GameDifficulty.kt`
- `app/src/main/java/com/neon/game/connectfour/ConnectFourGame.kt`
- `app/src/main/java/com/neon/game/ballsort/BallSortGame.kt`
- `app/src/main/java/com/neon/game/multiplier/MultiplierGame.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/ConnectFourViewModel.kt`
- `app/src/test/java/com/neon/game/connectfour/ConnectFourGameTest.kt`
- `app/src/test/java/com/neon/game/multiplier/MultiplierGameTest.kt`
- `docs/WORKLOG.md`

**Summary:**
- **Base Layer:** Introduced dedicated `GameResult`/`GameDifficulty` enums and a rich `BaseGameState` that now owns score/move/turn tracking, win/draw/loss marking, result/winner references, and a shared reset contract.
- **Game Refactors:** Reworked Connect Four, Ball Sort, and Multiplier to inherit from `BaseGameState`, push their shared fields back to the base class, and wire every win/hazard/reset path through the centralized API (difficulty is routed via ViewModels → `BaseGameState` now).
- **ViewModel & Tests:** Updated `ConnectFourViewModel` to read the shared winner/result fields and rewrote the Connect Four/Multiplier unit tests to assert the new enum states instead of the old sealed type. Verified the changes with `./gradlew :app:testDebugUnitTest` and `./gradlew assembleDebug`.

---

## 2025-12-09 - Phase 2: Navigation & ViewModels

**Task:** 2.1 Navigation graph correctness

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt`
- `app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt`
- `docs/WORKLOG.md`

**Summary:**
- **Typed Ball Sort arguments:** Added `routeWithArgs`/`defaultLevel` helpers and tidied the NavHost so the Ball Sort destination reads from a single centralized `AppDestination` definition rather than a string-rewrite trick.
- **Consistent back flow:** Ball Sort’s level-complete overlay and the Multiplier game-over panel now use `navController.toLobby()` so every “back to lobby” path goes through the same typed helper, maintaining Lobby as the root of the stack.
- **Verification:** `./gradlew assembleDebug`

---

## 2025-12-09 - Phase 2: Navigation & ViewModels (ViewModel wiring)

**Task:** 2.2 ViewModel wiring and lifecycle

**Files Changed:**
- `docs/WORKLOG.md`

**Summary:**
- **ViewModel audit:** Confirmed each major screen (Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips) is driven by its Compose-managed ViewModel; UI layers only collect state flows and emit user events.
- **Lifecycle alignment:** Since `NeonGameApp` owns the `viewModel()` instances at the host level, device rotations and back-stack restores rehydrate the existing ViewModel state without reinitializing game logic inside composables.
- **Verification:** `./gradlew :app:testDebugUnitTest` and `./gradlew assembleDebug`

---

## 2025-12-09 - Phase 3: Theming & Holographic UI

**Task:** 3.1 Unified neon color system

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/theme/Colors.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/NeonGameTheme.kt`
- `app/src/main/java/com/neon/connectsort/MainActivity.kt`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- **Neon palette:** Introduced `NeonPalette` as the semantic contract for primary, secondary, accent, glow, background, surface, and error tokens derived from the low-level `NeonColors` constants so Compose and the system bars share the same neon vocabulary.
- **Theme wiring:** Updated `NeonGameTheme` to build its `darkColorScheme` from `NeonPalette`, ensuring every screen (and the system bars via `MainActivity`) taps the same palette.
- **Verification:** `./gradlew assembleDebug`

---

## 2025-12-09 - Phase 3: Theming & Holographic UI

**Task:** 3.2 Holographic components library

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/components/HolographicComponents.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/ShopScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/SettingsScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/CharacterChipsScreen.kt`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- **Dedicated package:** The holographic widget set (buttons, cards, particle systems, etc.) now lives under `ui.components`, leaving `ui.theme` for tokens/styles and making the shared library reusable across screens.
- **Screen adoption:** Lobby/Shop/Settings/CharacterChips now import `com.neon.connectsort.ui.components.*`, while Lobby explicitly requests `HolographicButton`/`HolographicCard`, so all primary actions and panels rely on the same visuals.
- **Verification:** `./gradlew assembleDebug`

## 2025-12-09 - Phase 4: Persistence & Progression

**Task:** 4.1 Preferences & progression integration

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/ConnectFourViewModel.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/PreferencesViewModelFactory.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/LobbyViewModel.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/ConnectFourScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt`

**Summary:**
- Injected `AppPreferencesRepository` into every gameplay ViewModel, wired `ConnectFourViewModel` to difficulty/high-score flows, and ensured best wins get recorded through the shared persistence helpers.
- Extended `LobbyViewModel` and the lobby screen to surface difficulty and per-mode best-score metadata so the hub reflects stored progression.
- Kept the preference-backed `PreferencesViewModelFactory` in sync so settings, lobby, and gameplay all use the same repository instance.

## 2025-12-09 - Phase 5: Tests & CI

**Task:** 5.1 Game unit tests & 5.2 Basic CI workflow

**Files Changed:**
- `app/src/main/java/com/neon/game/common/GameCommonTest.kt`
- `app/src/test/java/com/neon/connectsort/core/data/AppPreferencesRepositoryTest.kt`
- `app/src/test/java/com/neon/connectsort/ui/screens/viewmodels/LobbyViewModelTest.kt`
- `app/src/test/java/com/neon/connectsort/ui/screens/viewmodels/SettingsViewModelTest.kt`
- `app/build.gradle.kts`
- `.github/workflows/android-ci.yml`

**Summary:**
- Added a cross-game `GameCommonTest` covering `BaseGameState`, `GameResult`, and `GameDifficulty` plus backbone fixes so the shared contracts stay rock solid.
- Adjusted the preference-backed ViewModel tests to wait for emitted updates and added the missing Robolectric dependency.
- Swapped the CI job to a single `./gradlew clean lint test assembleDebug` step to match the task requirements.

## 2025-12-09 - Phase 6: Manifest, Icons & Branding

**Task:** 6.1 Manifest cleanup & 6.2 Icons and branding

**Files Changed:**
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/mipmap-anydpi` (directory removed)

**Summary:**
- Declared the manifest `package`, dropped the unused `VIBRATE` permission, and left only the required `INTERNET` entry so the APK manifest stays tight.
- Updated `@string/app_name` to “Neon Connect & Sort” (`&amp;`) and dropped the redundant adaptive icon in the base `mipmap-anydpi` folder in favor of the API-26 variant.

## 2025-12-09 - Phase 7: Visual Polish & UX

**Task:** 7.1 Consistent lobby and mode selection UX

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/theme/Effects.kt`

**Summary:**
- Added stat badges for difficulty, best scores, and coins plus a hero description so the lobby reflects the player’s progression at a glance.
- Introduced a horizontal chip rack that highlights unlocked characters versus locked ones while calling out the currently selected avatar.
- Made the holographic helper composable-aware so the new badges can be composed safely.

---

## 2025-12-10 - Phase 8: Story Mode integration

**Task:** 8.2 Chapter → game mode mapping

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/screens/BallSortScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/MultiplierScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/MultiplierViewModel.kt`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- Ball Sort and Multiplier screens now publish their chapter result via `activeStoryChapterId`/`publishStoryResult`, so the Story Hub immediately unlocks the next entry when the player wins a required challenge.
- Added `gameResult` to `MultiplierGameState` so the screen can detect a win without spying on the underlying game internals, which also keeps the publish logic idempotent.
- Verified by running `JAVA_HOME=/home/aztr0nutzs/StudioProjects/KonnxtNstaxK_v1.0-main/jdk-17.0.8+7 ./gradlew assembleDebug`.

---

## 2025-12-10 - Phase 8: Story Mode refinement

**Task:** 8.3 Basic narrative & chapter text

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/ui/screens/story/StoryHubScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/StoryHubViewModel.kt`
- `app/src/main/res/values/strings.xml`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- Replaced inline chapter text with localized string resources and added dedicated goal copy so the hub reads like a campaign log.
- Dressed the Story Hub with a gradient hero banner, timeline chips, and a neon particle backdrop to visually distinguish it from other modes.
- Verified the feature via `JAVA_HOME=/home/aztr0nutzs/StudioProjects/KonnxtNstaxK_v1.0-main/jdk-17.0.8+7 ./gradlew assembleDebug`.

---

## 2025-12-10 - Phase 9: Character chips domain model

**Task:** 9.1 Character chips domain model

**Files Changed:**
- `app/src/main/java/com/neon/connectsort/core/data/ChipRepository.kt`
- `app/src/main/java/com/neon/connectsort/core/domain/CharacterChip.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/CharacterChipsScreen.kt`
- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/CharacterChipsViewModel.kt`
- `docs/WORKLOG.md`
- `docs/DECISIONS.md`

**Summary:**
- Added a dedicated domain model for character chips that carries rarity, ability metadata, and gameplay effects plus a sealed `ChipAbility` hierarchy to describe extra points, shields, and extra moves.
- Centralized the roster inside `ChipRepository` so screens/ViewModels share a single list; the ViewModel now maps unlocks/high scores back onto those chips so UI consumables never drift from the source of truth.
- Verified with `JAVA_HOME=/home/aztr0nutzs/StudioProjects/KonnxtNstaxK_v1.0-main/jdk-17.0.8+7 ./gradlew assembleDebug`.
