# CTT Shield Indicator (Minecraft 26.3 client mod)

**English** · [中文](README.zh-CN.md) · [Changelog](CHANGELOG.md) · [Publishing](docs/publishing.md) · [MIT License](LICENSE)

| Requirement | Version |
| --- | --- |
| Minecraft | 26.3 |
| Loader | Fabric Loader 0.20.1+ / Fabric API 0.166.2+26.3 |
| Environment | Client only (`"environment": "client"`) |
| Java | 25 |

## Features

- When the **offhand** item's `minecraft:custom_data` contains `shield:1b` or `shieldAbility:1b`,
  a shield cooldown bar is drawn **directly above** the offhand slot.
- Offhand only — the same item in the main hand / hotbar / inventory never triggers it.
- **Hidden at full durability.**

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

Generated on first launch at `config/ctt_shield_indicator.json`:

```json
{
  "enabled": true,
  "showMeter": true,
  "widthSlots": 1.1,
  "heightSlots": 0.0,
  "gapAboveActionBar": 2,
  "meterMode": "texture"
}
```

| Key | Meaning |
| --- | --- |
| `enabled` | Master switch. |
| `showMeter` | When false the whole indicator disappears. |
| `widthSlots` | **Visible content** width = 20 px (one slot cell) × this value. `1.0` = one slot wide, `1.1` slightly wider (default), `1.5` wider still. |
| `heightSlots` | **Visible content** height; `0` = follow the 52:128 ratio (1.1 wide → ≈54.2 tall). Set `1.5` to squash it to 22×30. |
| `gapAboveActionBar` | Pixels between the gauge bottom and the top of the actionbar text. |
| `meterMode` | `texture` (default) draws the PNG at the size above; `model` renders the resource pack's `meter_N` model instead. |

Changes take effect after re-entering the world or restarting the game.

## Project layout

```
src/main/java/com/namylium/cttshieldindicator/
    CttShieldIndicatorClient.java   entrypoint, registers the HUD element
    ShieldIndicatorHud.java         conditions + drawing
    ShieldIndicatorConfig.java      config/ctt_shield_indicator.json
src/main/resources/
    fabric.mod.json
    assets/ctt_shield_indicator/    item model definitions (meter) + gauge textures
    assets/minecraft/               meter models and textures (used by meterMode=model)
docs/                               layout preview, publishing guide
```

## License

MIT — see [LICENSE](LICENSE).
