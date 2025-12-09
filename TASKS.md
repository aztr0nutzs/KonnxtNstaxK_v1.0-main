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

**Status:** `[DONE]`  
**Goal:** Ensure the project compiles and runs before deeper refactors.

**Verification:**
```bash
./gradlew clean assembleDebug
```

---

## Phase 1 – Game Logic Stabilization

### 1.1 Ball Sort – crash and rules audit

**Status:** `[DONE]`
**Goal:** Ball Sort mode never crashes, follows clear rules, and is restartable.

**Verification:**
```bash
./gradlew :app:testDebugUnitTest --tests "*BallSort*"
./gradlew assembleDebug
```

### 1.2 Multiplier mode – fully functional

**Status:** `[DONE]`
**Goal:** Multiplier game is actually playable, with visible scoring and difficulty.

**Verification:**
```bash
./gradlew :app:testDebugUnitTest --tests "*Multiplier*"
./gradlew assembleDebug
```

### 1.3 Shared game base / utilities

**Status:** `[DONE]`
**Goal:** Reduce duplication across games and centralize common logic.

**Verification:**
```bash
./gradlew :app:testDebugUnitTest
./gradlew assembleDebug
```

---

## Phase 2 – Navigation & ViewModels

### 2.1 Navigation graph correctness

**Status:** `[DONE]`
**Goal:** All screens are reachable with consistent routes and back-stack behavior.

**Files:**

- `app/src/main/java/com/neon/connectsort/ui/NeonGameApp.kt`
- `app/src/main/java/com/neon/connectsort/navigation/AppDestinations.kt`

**Verification:**
```bash
./gradlew assembleDebug
```

### 2.2 ViewModel wiring and lifecycle

**Status:** `[TODO]`
**Goal:** Every screen with non-trivial state has a ViewModel, and state is not lost on rotation.

**Files:**

- `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/*.kt`
- Associated `*Screen.kt` files.

**Actions:**

- **Ensure each main screen has a ViewModel:**
    - Lobby, ConnectFour, BallSort, Multiplier, Shop, Settings, CharacterChips.
- **Instantiate ViewModels correctly:**
    - Use `hiltViewModel()` or `viewModel()` inside composables, not manual singletons.
- **Move business logic out of composables:**
    - Keep composables as “dumb views” that render `uiState` and trigger events.
- **Confirm state survives configuration changes (rotate emulator).**

**Verification:**
```bash
./gradlew :app:testDebugUnitTest
./gradlew assembleDebug
```

---

## Phase 3 – Theming & Holographic UI

### 3.1 Unified neon color system

**Status:** `[TODO]`
**Goal:** One canonical source of truth for neon colors with no type mismatches.

**Verification:**
```bash
./gradlew assembleDebug
```

### 3.2 Holographic components library

**Status:** `[TODO]`
**Goal:** Use shared UI components for glowing buttons, cards, panels.

**Verification:**
```bash
./gradlew assembleDebug
```

---

## Phase 4 – Persistence & Progression

### 4.1 Preferences & progression integration

**Status:** `[TODO]`
**Goal:** Difficulty, audio settings, and chip unlocks persist between sessions.

**Verification:**
```bash
./gradlew :app:testDebugUnitTest --tests "*Preferences*"
./gradlew assembleDebug
```

---

## Phase 5 – Tests & CI

### 5.1 Game unit tests

**Status:** `[TODO]`
**Goal:** Each game has solid test coverage for its core rules.

**Verification:**
```bash
./gradlew :app:testDebugUnitTest
```

### 5.2 Basic CI workflow

**Status:** `[TODO]`
**Goal:** CI ensures no broken commits land in main.

**Verification:**
- Push to GitHub and confirm the CI job runs and passes.

---

## Phase 6 – Manifest, Icons & Packaging

### 6.1 Manifest cleanup

**Status:** `[TODO]`
**Goal:** Manifest is clean, precise, and aligned with the app.

**Verification:**
```bash
./gradlew assembleDebug
```

### 6.2 Icons and branding

**Status:** `[TODO]`
**Goal:** The app has proper adaptive icons and a consistent name.

**Verification:**
```bash
./gradlew assembleDebug
```

---

## Phase 7 – Visual Polish & UX (Optional but Recommended)

### 7.1 Consistent lobby and mode selection UX

**Status:** `[TODO]`
**Goal:** Lobby feels like the central hub of a neon arcade, not just a menu.

**Verification:**
```bash
./gradlew assembleDebug
```
