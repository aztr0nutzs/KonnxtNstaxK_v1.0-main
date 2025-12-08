# Development Guidelines – Neon Connect & Sort

These are the practical instructions for working on this repo.

---

## 1. Environment

- Android Studio (current stable)
- Kotlin + Compose
- Java 17 (or project-specified version)
- Build & test commands:
  - `./gradlew assembleDebug`
  - `./gradlew test`

If any tool version is required, document it here and keep it updated.

---

## 2. Adding or Changing Features

### 2.1. Always start by picking a layer

When you add or change a feature, explicitly decide what you’re touching:

- **Game rules** → `game/` package (pure Kotlin)
- **UI** → `ui/screens` + `ui/components` + `ui/theme`
- **Settings / persistence** → `data/` (DataStore / repo)
- **Navigation / flow** → `navigation/` or root composables

If you’re touching more than one layer, **state that in the PR/commit**.

---

### 2.2. Flow for new gameplay feature (connect-four example)

1. Define/modify rule in `ConnectFourGame` (pure Kotlin).
2. Add/adjust tests in `ConnectFourGameTest`.
3. Only **after that**, wire it into `ConnectFourScreen`.
4. If UI changes are visible to the player:
   - Make sure it respects `UI_UX_GUIDELINES.md`.
   - Test on **realistic phone dimensions** in preview or emulator.

Same pattern for Ball Sort via `BallSortGame` and `BallSortScreen`.

---

### 2.3. Flow for new UI-only feature

Example: new settings toggle, new lobby button.

1. If it needs persistence:
   - Add key & behavior to `data` layer (e.g. `PreferencesManager`).
2. Add UI elements in the correct screen file.
3. Hook UI state to data layer via `remember` + `collectAsState` (if using Flows).
4. Make sure UI:
   - Uses **Neon components**.
   - Matches spacing, colors, and fonts defined in theme.

---

## 3. Coding Style (Kotlin / Compose)

- Prefer:
  - `val` over `var`
  - Small functions with clear names
  - Top-level `@Composable` functions per screen + smaller sub-composables inside the same file

### 3.1. Naming

- Screens: `XxxScreen`
- Pure game classes: `ConnectFourGame`, `BallSortGame`
- Helpers: `XxxState`, `XxxUiModel`, `XxxRenderer` where appropriate

### 3.2. Composables

- Avoid massive monolithic composables.
- Break screens into:
  - `XxxScreen()` → top-level
  - `XxxHeader()`, `XxxBoard()`, `XxxControls()` etc.

- No direct business logic in composables:
  - UI calls methods on game/data classes.
  - Composables just react to state + emit events.

---

## 4. Testing Guidelines

### 4.1. Game logic tests

- Located in `app/src/test/java/...`.
- Every rule change must have tests.
- Focus on:
  - win detection
  - move validity
  - edge cases (full board, no moves, weird sequences)

### 4.2. UI tests (optional / future)

- If added, they should:
  - Verify important interactions (buttons, nav)
  - Avoid brittle tests that depend on exact pixel values.

---

## 5. Error Handling & Edge Cases

- Game logic:
  - Validate inputs (column index, tube index).
  - Fail gracefully (return `false` / `null` where needed).
  - Don’t crash on bad input from UI.

- UI:
  - Don’t trust user taps to be “nice”.
  - Ignore invalid operations with clear feedback (visual or at least no crash).

---

## 6. Assets & Resources

- Keep all future image/asset files organized (`res/drawable`, `res/raw`, etc.).
- Don’t dump large binaries in random folders.
- If late-stage polishing adds sound/music, document:
  - File location
  - Licensing
  - Usage in code

---

## 7. “Done” Definition

A feature is **done** when:

- Code compiles: `./gradlew assembleDebug`
- Tests pass: `./gradlew test` (if applicable)
- UI is usable on a small phone emulator
- Relevant doc is updated:
  - Rules change? → `RULES.md`
  - Dev flow change? → `DEVELOPMENT_GUIDELINES.md`
  - Visual or UX change? → `UI_UX_GUIDELINES.md`

No “it works on my machine” without builds/tests/docs.