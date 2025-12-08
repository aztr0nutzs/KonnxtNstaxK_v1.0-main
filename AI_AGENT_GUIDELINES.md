# AI Agent Collaboration Guidelines – Neon Connect & Sort

This file tells AI tools (Codex, ChatGPT, etc.) how they are allowed to touch this repo.

---

## 1. General Behavior

- Primary goal: **produce working, production-grade code**, not essays.
- Always:
  - Respect the existing architecture.
  - Keep **build green**.
  - Use **Compose** for UI and **pure Kotlin** for game logic.

---

## 2. File Editing Rules

1. When editing a file:
   - Show the **entire updated file**, not fragments.
   - Preserve imports and package declarations unless they’re wrong.

2. When creating new files:
   - Place them in a sensible package:
     - `game/` for pure logic
     - `ui/screens` for screens
     - `ui/components` for shared UI
     - `ui/theme` for theming
     - `data/` for persistence

3. Do not:
   - Randomly rename packages.
   - Introduce new frameworks or architectural patterns without being asked.

---

## 3. Code vs. Prose

- Responses must be **code-first**:
  - Use fenced code blocks with filenames indicated as comments:
    - `// FILE: app/src/main/java/.../SomeFile.kt`
- Explanations should be:
  - Short (1–3 lines).
  - Focused on **what changed** and **how to build/test**.

- No long design essays, especially when the user asks for implementation.

---

## 4. Game Logic Constraints

- Game rules belong in:
  - `ConnectFourGame`, `BallSortGame`, or other `game/*` classes.

- Absolutely no:
  - Win detection inside composables.
  - Tube move validation inside UI code.

- When changing rules:
  - Update tests in `app/src/test/java/...` accordingly.
  - Never leave core logic as TODO.

---

## 5. UI / UX Constraints

- Must respect `UI_UX_GUIDELINES.md`.
- Use:
  - `NeonGameTheme` for theming.
  - `NeonButton` / `NeonCard` where appropriate.
- Always keep:
  - Buttons large and labeled.
  - Screens navigable from the lobby.

If user says “add feature X to Connect-4 screen”:
- Don’t redesign the entire game.
- Insert changes into the **existing screen structure**.

---

## 6. Testing & Build

Whenever you introduce changes that affect behavior:

- Ensure the answer includes:
  - What tests to run: `./gradlew test`
  - Build command: `./gradlew assembleDebug`

If tests or build might fail due to a new dependency:
- Explicitly state what needs to be installed or configured.

---

## 7. Safety & Scope

- No real ad IDs, billing IDs, or secrets in code.
- Monetization logic must be stubbed with clear TODO comments.
- Stick to **this project’s scope**:
  - Android game
  - Kotlin + Compose
  - Cyberpunk Connect-4 + Ball Sort

If the user asks for something that conflicts with `RULES.md`:
- Call it out and either:
  - Adapt the request to fit the rules, or
  - Explicitly mark it as a one-off exception (with comments in the code).

---

## 8. When Unsure

If requirements are ambiguous:
- Prefer:
  - Minimal, clean implementation that’s easy to extend.
  - Well-named functions and clear comments over clever hacks.

Never silently make big architectural changes.  
If a major refactor is truly required, **describe it briefly** and implement it step-by-step, with full files in each step.