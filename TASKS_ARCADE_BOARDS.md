# Arcade Boards, Lobby & Animations

## Status

✅ Connect 4 and Ball Sort boards now render through the provided PNG cabinets, layered with animated slime/holographic pulses that stay in sync with the existing controls and ViewModel feedback.

✅ The cyberpunk lobby previews those same cabinets through a holographic shimmer, highlights the selected mode in real time, and keeps multiplayer/settings/shop/leaderboard actions grouped together.

## Completed work

- **Connect 4**
  - Replaced the placeholder vector sketch with `connect4_board.png` and layered `connect4_slime.png` on top; the slime overlay shifts with a pulsing animation and brightens whenever a turn, win, or draw ramps the board intensity.
  - Retained the drop indicators, particle field, and neon pulse ring, wiring their colors/intensities to the ViewModel so the machine lights up for each move.

- **Ball Sort**
  - Swapped in `ballsort_board.png` for the tabletop cabinet, and introduced `HolographicPulseFrame` to emit a glowing field whenever hints fire or a level completes.
  - The holographic tube grid, info row, and control buttons keep the gameplay controls fully interactive while the new pulse effect reacts to user action.

- **Lobby**
  - `GameModePreview` now wraps the descriptive text inside `LobbyPreviewHologram`, which layers glimpses of the Connect 4 and Ball Sort cabinets plus animated scanlines to reinforce the neon environment.
  - The lobby cards, multiplayer panel, and footer still echo the cyberpunk grid, but now everything feels grounded on the actual machine visuals and their stateful highlights.

- **Effects & shared utilities**
  - Added helper effects (slime wave animation, holographic pulses, lobby preview shimmer) so the new PNG assets can be reused with responsive glow/slide mechanics without duplicating canvas work.

## Assets

- `app/src/main/res/drawable-nodpi/connect4_board.png`
- `app/src/main/res/drawable-nodpi/connect4_slime.png`
- `app/src/main/res/drawable-nodpi/ballsort_board.png`

## Next steps

1. Hook the new cabinet art into any platform haptics/SFX if required and confirm the touch targets for the neon buttons remain stable across device densities.
2. Expand the leaderboard/multiplayer mock data once the backend lobby system is ready and ensure transitions remain smooth when switching modes.
3. Monitor performance on lower-end hardware and consider trimming particle counts or offering a “lite” visual profile if the new effects start to lag.
