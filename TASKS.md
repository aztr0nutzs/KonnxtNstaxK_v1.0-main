# TASKS.md – Neon Connect & Sort (Advanced Feature Wave)

**Project:** Neon Connect & Sort  
**Root:** `KonnxtNstaxK_v1.0-main/`  
**App module:** `app/`  
**Package:** `com.neon.connectsort`

> This TASKS.md assumes the **core stabilization phases (0–7)** from the previous TASKS file are **mostly complete and green**:
> - Project builds cleanly
> - Game modes are playable (Connect-4, Ball Sort, Multiplier)
> - Navigation, ViewModels, theming, persistence, and tests are in a decent state
>
> If they are NOT, finish those first.

This file defines the **next advancement wave**: story/campaign, character chips, progression, shop/economy, UX/audio polish, analytics, and release prep.

---

## Conventions

- **Status tags:**
    - `[TODO]` = not started
    - `[WIP]`  = in progress
    - `[DONE]` = completed and verified
- For each task:
    - Edit **only** the files listed, plus any **strictly necessary** support files.
    - Run the **Verification** commands.
    - When marking `[DONE]`, add a short **Notes** bullet.

---

## Phase 8 – Story Mode & Campaign Spine

Goal: Introduce a **Story Mode / Campaign flow** that ties together Connect-4, Ball Sort, and Multiplier with a narrative and progression.

### 8.1 Story Mode skeleton & navigation

**Status:** `[TODO]`  
**Goal:** A new “Story Mode” entry flows from Lobby → StoryHub → individual story chapters, even if content is initially simple.

**Files:**
- `app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt`
- `app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt`
- **New:** `app/src/main/java/com/neon/connectsort/ui/screens/story/StoryHubScreen.kt`
- **New:** `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/StoryHubViewModel.kt`

**Actions:**
1. Add a **StoryMode** route to `AppDestinations`.
2. Add navigation from Lobby:
    - New button in `LobbyScreen` to enter Story Mode.
3. Create `StoryHubScreen`:
    - Lists chapters (even if they’re placeholders to start).
    - Shows basic locked/unlocked state.
4. Create `StoryHubViewModel`:
    - Holds a list of chapters (id, title, short description, locked/cleared flags).
    - Later will be wired to persistence.

**Verification:**
```bash
./gradlew assembleDebug

Done when:

    Lobby has a “Story Mode” entry.

    Story Hub screen loads and displays at least a static list of chapters.

    Back navigation returns cleanly to Lobby.

Notes:

    Story content can be placeholder text in this step; focus is on structure and nav.

8.2 Chapter → game mode mapping

Status: [DONE]
Goal: Each story chapter maps to one or more game modes with specific rules/difficulty (e.g. “Win Ball Sort on Medium”, “Win Connect-4 twice”).

Files:

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/StoryHubViewModel.kt

    app/src/main/java/com/neon/connectsort/ui/screens/story/StoryHubScreen.kt

    app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

    Related game ViewModels (ConnectFour, BallSort, Multiplier)

Actions:

    Extend StoryHubViewModel with a model such as:

    data class StoryChapter(
        val id: String,
        val title: String,
        val description: String,
        val requiredMode: StoryGameMode, // e.g. CONNECT4, BALLSORT, MULTIPLIER
        val requiredDifficulty: GameDifficulty,
        val requiredWins: Int,
        val isLocked: Boolean,
        val isCompleted: Boolean
    )

    Implement a launch flow:

        Tapping a chapter starts the correct game mode at the required difficulty.

    Decide how to return:

        When game ends, game ViewModel informs StoryHub (e.g. via a result callback, shared repository, or nav argument) whether the requirement was met.

Verification:

./gradlew assembleDebug

Done when:

    Selecting a chapter launches the appropriate mode and difficulty.

    Returning from a game updates the chapter as completed or not (even if not persisted yet).

Notes:

    Chapters now observe each game’s success path (Ball Sort, Multiplier, ConnectFour) via `publishStoryResult` so returning from the mode unlocks the next entry.

    Actual persistence of chapter completion comes in Phase 10/Progression; this step wires the flow.

8.3 Basic narrative & chapter text

Status: [DONE]
Goal: Replace placeholder text with a basic but coherent neon-arcade narrative.

Files:

    app/src/main/java/com/neon/connectsort/ui/screens/story/StoryHubScreen.kt

    Optional: localized strings in app/src/main/res/values/strings.xml

Actions:

    For each chapter, define:

        Title

        Short intro blurb

        One-line goal description (“Win Ball Sort with no mistakes”, etc.)

    Move all user-visible story strings into strings.xml.

    Make the Story Hub visually distinct (e.g., different background gradient).

Verification:

./gradlew assembleDebug

Done when:

    The Story Hub feels like a real “timeline” or “campaign map” with at least 5–10 chapters defined.

Notes:

    Chapter copy now lives in `strings.xml`, the Hub has a dedicated hero banner/timeline, and goals are surfaced per entry so the map feels like a narrative campaign.

Phase 9 – Character Chips System & Abilities

Goal: Turn “character chips” into a real, gameplay-impacting system (abilities, unlocks, rarity).
9.1 Character chips domain model

Status: [DONE]
Goal: Establish a robust data model for chips: id, name, rarity, abilities, cost/unlock conditions.

Files:

    app/src/main/java/com/neon/connectsort/core/domain/CharacterChip.kt (new)

    app/src/main/java/com/neon/connectsort/core/data/ChipRepository.kt (new or existing)

    Any existing chip definition files (ChipDefinitions.kt, etc.)

Actions:

    Create a CharacterChip data model:

    data class CharacterChip(
        val id: String,
        val name: String,
        val rarity: ChipRarity,
        val description: String,
        val baseColor: Color,
        val ability: ChipAbility
    )

    Define supporting enums/sealed classes:

        ChipRarity (COMMON, RARE, EPIC, LEGENDARY)

        ChipAbility (sealed class representing effects like extra points, extra move, shield, etc.)

    Create ChipRepository:

        Provides a list of all chips.

        Later will handle unlock states via persistence.

Verification:

./gradlew assembleDebug

Done when:

    There is a central, strongly typed place where all chips and their abilities are defined.

    No more random scatter of chip definitions across the codebase.

Notes:

    Centralized the roster in `ChipRepository`, added a `CharacterChip`/`ChipAbility` domain model, and documented the ability semantics in `docs/DECISIONS.md`.

9.2 Chip selection & loadout UI

Status: [TODO]
Goal: Allow the player to select one or more chips as a “loadout” before entering games.

Files:

    app/src/main/java/com/neon/connectsort/ui/screens/CharacterChipsScreen.kt (or new)

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/CharacterChipsViewModel.kt

    app/src/main/java/com/neon/connectsort/ui/screens/LobbyScreen.kt

    app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt

Actions:

    Create/extend CharacterChipsViewModel:

        Exposes:

            All available chips (from ChipRepository).

            Selected loadout (e.g., up to 3 active chips).

    Implement CharacterChipsScreen:

        Grid/list of chips with rarity and description.

        Tap to select/deselect into a fixed-size loadout.

    Add an entry point from Lobby (e.g., “Chips & Loadout”).

Verification:

./gradlew assembleDebug

Done when:

    Player can open Chips screen, choose a loadout, and see it reflected in the UI.

    Loadout state survives navigation but not yet persisted across app restarts (persistence is later).

Notes:

    Keep UI simple but consistent with neon/holographic style.

9.3 Chip abilities affecting gameplay

Status: [TODO]
Goal: Selected chips should have real mechanical effects in at least one mode (start with Connect-4, then Ball Sort/Multiplier).

Files:

    app/src/main/java/com/neon/game/connectfour/ConnectFourGame.kt

    app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/ConnectFourViewModel.kt

    app/src/main/java/com/neon/connectsort/core/domain/CharacterChip.kt

    app/src/main/java/com/neon/connectsort/core/data/AppPreferencesRepository.kt (for storing loadout)

    app/src/test/java/.../ConnectFourGameTest.kt (extend)

Actions:

    Decide on 2–3 concrete abilities, e.g.:

        Extra opening move.

        Bonus points per win.

        One-time “undo” per game.

    Wire the active loadout into ConnectFourViewModel:

        Load from repository or inject via constructor.

        Apply abilities at appropriate points (start of game, when scoring, when undo is used).

    Update tests:

        Validate that abilities change results as expected (e.g., more score with chip, undo restores previous board).

Verification:

./gradlew :app:testDebugUnitTest --tests "*ConnectFour*"
./gradlew assembleDebug

Done when:

    At least one game mode visibly changes behavior based on equipped chips.

    Tests prove the ability mechanics.

Notes:

    Extend to Ball Sort/Multiplier later using the same pattern.

Phase 10 – Economy, Rewards & Shop

Goal: Introduce a virtual currency + rewards system and wire it into the existing Shop screen.
10.1 Currency & rewards model

Status: [TODO]
Goal: Define a simple in-game currency and reward sources.
