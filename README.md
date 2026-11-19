# CTT Shield Indicator (Minecraft 26.3 client mod)

[![build](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml/badge.svg)](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**English** · [中文](README.zh-CN.md) · [Changelog](CHANGELOG.md) · [Publishing](docs/publishing.md) · [Source](https://github.com/nyaLium/ctt-shield-indicator) · [Releases](https://github.com/nyaLium/ctt-shield-indicator/releases) · [Issues](https://github.com/nyaLium/ctt-shield-indicator/issues)

| Requirement | Version |
| --- | --- |
| Minecraft | 26.3 |
| Loader | Fabric Loader 0.20.1+ / Fabric API 0.166.2+26.3 |
| Mod Menu (optional) | 21.0.0+ — only needed for the in-game settings screen |
| Environment | Client only (`"environment": "client"`) |
| Java | 25 |

## Features

- When the **offhand** item's `minecraft:custom_data` contains `shield:1b` or `shieldAbility:1b`,
  a shield cooldown bar is drawn **directly above** the offhand slot.
- Offhand only — the same item in the main hand / hotbar / inventory never triggers it.
- **Hidden at full durability.**
- **Your choice of position** — anchor the gauge above the offhand slot (default) or at the **centre of
  the screen**, then nudge it left or right by up to 160 px.
- All settings can be changed **in-game through Mod Menu** (Mods → CTT Shield Indicator → Configure),
  or by editing the config file.

| damage / max_damage | Shown |
| --- | --- |
| 0 (full durability) | nothing |
| 0 &lt; r &lt; 0.4 | `meter_3` (3 cells) |
| 0.4 ≤ r &lt; 0.6 | `meter_2` (2 cells) |
| 0.6 ≤ r &lt; 0.8 | `meter_1` (1 cell) |
| ≥ 0.8 | `meter_0` (0 cells) |

## Placement

- Horizontal anchor is the offhand **item cell**: vanilla draws the 16×16 item at
  `guiWidth/2-117` (left) / `guiWidth/2+101` (right), so the centres are `center∓109`.
  (The 29-wide frame sprite starts at `center-120`; its centre `center∓105.5` would be 3.5 px off.)
- With `position: "center"` the horizontal anchor becomes the screen centre (`guiWidth/2`). Either
  anchor is then shifted by `horizontalOffset` and clamped so the whole gauge stays on screen.
- The actionbar text is drawn after `translate(0, guiHeight-68)` at local `y=-4`,
  so its top edge is ≈ `guiHeight-72`.
- The gauge bottom sits at `guiHeight - 72 - gapAboveActionBar` (default 2 px), so it floats just
  above the actionbar — never covered by the text, never covering it.

![Layout preview — dashed red: visible content 22×54.2; dashed blue: the 20×20 slot cell](docs/layout-preview.png)

## Building

Requires JDK 25. A Gradle wrapper (9.9.1) is included:

```bash
./gradlew build
```

## Installation

1. Fabric Loader 0.20.1+ and Fabric API 0.166.2+26.3 (client side).
2. Drop the jar into `.minecraft/mods`.

## Configuration

Generated on first launch at `config/ctt_shield_indicator.json`.
With **Mod Menu** installed everything below is also editable in-game
(Mods → CTT Shield Indicator → Configure), including a "Reset to defaults" button.

```json
{
  "enabled": true,
  "position": "offhand",
  "horizontalOffset": 0,
  "scale": 1.1,
  "gapAboveActionBar": 2
}
```

| Key | Meaning |
| --- | --- |
| `enabled` | Master switch. When false nothing is drawn. |
| `position` | `"offhand"` anchors to the offhand slot (default); `"center"` anchors to the screen centre. |
| `horizontalOffset` | Pixels to shift from that anchor: negative = left, positive = right, range ±160. The gauge is pulled back onto the screen if it would leave it. |
| `scale` | Gauge scale: visible content width = 20 px (one slot cell) × this value. `1.0` = one slot wide, `1.1` slightly wider (default). Height always follows the texture's 52:128 ratio. |
| `gapAboveActionBar` | Pixels between the gauge bottom and the top of the actionbar text. |

In-game (Mod Menu), `position` is a cycle button and the numbers are sliders, so you can tune them
while watching the result.

Changes take effect after re-entering the world or restarting the game.

## Project layout

```
src/main/java/com/nyalium/cttshieldindicator/
    CttShieldIndicatorClient.java   entrypoint, registers the HUD element
    ShieldIndicatorHud.java         conditions + drawing
    ShieldIndicatorConfig.java      config/ctt_shield_indicator.json (load/save/reset)
    ModMenuIntegration.java         Mod Menu entrypoint (optional dependency)
    ShieldIndicatorConfigScreen.java  in-game settings screen
src/main/resources/
    fabric.mod.json
    assets/ctt_shield_indicator/    gauge textures, icon, en_us/zh_cn lang
docs/                               layout preview, publishing guide
```

## Links

- Source / Issues — https://github.com/nyaLium/ctt-shield-indicator
- Releases (download the jar) — https://github.com/nyaLium/ctt-shield-indicator/releases
- Changelog — [CHANGELOG.md](CHANGELOG.md)
- Publishing notes — [docs/publishing.md](docs/publishing.md)

## License

MIT — see [LICENSE](LICENSE).
