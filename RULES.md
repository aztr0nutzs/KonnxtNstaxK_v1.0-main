# Project Rules – Neon Connect & Sort

This doc is the law for this project. Break it and you’re adding bugs or tech debt.

## 1. Project Scope

- This is a **mobile Android game** built with:
  - **Kotlin**
  - **Jetpack Compose** UI
  - **Single-activity** architecture + navigation
- It has **two main modes**:
  1. Cyberpunk **Connect-4** variant
  2. **Ball Sort** style puzzle mode
- Style: **dark, neon, cyberpunk**, but still **readable and performant on real phones**.

No web, no desktop, no random experimental features unless explicitly approved and documented.

---

## 2. Architecture Rules

1. **Strict separation of concerns**
   - Game logic = **pure Kotlin**, no AndroidX/Compose imports.
   - UI = **Compose only**, zero game rules/algorithms baked into composables.
   - Persistence (settings, chips, etc.) lives in a **data layer** (e.g. DataStore wrapper), not in UI.

2. **Layers**
   - `game/` → pure logic (`ConnectFourGame`, `BallSortGame`, etc.).
   - `ui/` → composables, theming, navigation, screens.
   - `data/` → persistence, settings, simple repositories.
   - `domain/` (optional) → use-cases if complexity grows.

3. **No “god files”**
   - If a file hits ~400–500 lines with mixed responsibilities, split it.
   - Keep screens in **1 file per screen** unless there’s a very good reason.

---

## 3. Code Quality Rules

1. **No placeholder core logic**
   - Game rules **must be complete**.
   - No `TODO("Implement logic")` in gameplay, only in **non-core** parts (e.g. ads, billing, analytics).

2. **Build must stay green**
   - `./gradlew assembleDebug` must succeed at all times.
   - If tests exist, `./gradlew test` must pass. If you break tests, **fix them** in the same commit.

3. **Every feature gets tests where it matters**
   - Game logic: always unit-testable.
   - Critical logic (win detection, move validity, level completion) must have tests.

4. **No dead code**
   - Remove unused imports, dead helpers, commented-out experimental blocks.
   - If you need a scratchpad, use a separate file clearly marked as `EXPERIMENTAL_*.md` and don’t ship it.

---

## 4. UI / UX Rules (High-Level)

- Must be:
  - **Readable** on a 5–6" device.
  - **Touch-friendly** (>= 48dp targets).
  - **Consistent**: colors, typography, padding, animations.

- All buttons must:
  - Have **clear labels**, not “Button1”.
  - Use the **Neon** component set (e.g. `NeonButton`) unless you’re explicitly making something special.
  
- No “cool” animation that tanks FPS or readability. Style is secondary to **playability**.

See `UI_UX_GUIDELINES.md` for specifics.

---

## 5. Data & Secrets

- **No secrets in repo.**  
  - No real ad IDs, Play Billing IDs, API keys.
  - Use placeholders + `TODO` comments where integration goes.

- Any monetization code must:
  - Be clearly marked as **STUB / placeholder**.
  - Be isolated so it can be wired to real services later.

---

## 6. Git / Commit Rules

- Each commit should:
  - Do **one logical thing** (feature, refactor, bugfix).
  - Keep the project **buildable**.
  - Update tests & docs if behavior changed.

- Don’t commit:
  - Generated files.
  - Local config (keystores, local.properties, etc.).
  - Garbage experimental assets.

---

## 7. AI / Tooling Rules (Short)

- AI tools **must follow**:
  - Use **existing structure**. Don’t rewrite the project without cause.
  - Show **full files**, not half-snippets, when editing.
  - Never move game rules into UI.

Full details in `AI_AGENT_GUIDELINES.md`.

---

## 8. Non-Negotiables

- No broken builds.
- No half-implemented core features.
- No “temporary hack” that becomes permanent without being documented and tracked.

If something must be hacked around, document it in:
- `DEVELOPMENT_GUIDELINES.md` (behavior)
- and open a **tracked issue**.