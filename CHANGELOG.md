# Changelog

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)，格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。

## [1.0.0] - 2026-11-19

首个发布版本，适配 Minecraft 26.3 / Fabric。

### 新增

- 纯客户端 HUD 指示器：副手物品的 `minecraft:custom_data` 命中 `shield:1b` 或 `shieldAbility:1b` 时，
  在副手槽正上方显示耐久计量条。
- 计量条档位与 `CTT Shield Enhancing` 数据包的 `range_dispatch` 阈值一致：
  耐久损失 ≥80% → `meter_0`，≥60% → `meter_1`，≥40% → `meter_2`，其余 → `meter_3`。
- 耐久满时不显示；仅在副手显示，主手 / 快捷栏 / 背包不触发。
- 位置固定在 actionbar 文字上方（可配置间距），不遮挡也不被遮挡。
- 尺寸以“纹理可见内容 52×128”为基准：宽度 = 槽位格 20px × `widthSlots`（默认 1.1），
  高度默认按比例，可用 `heightSlots` 单独压低。
- 水平对齐副手**物品格**中心（`center∓109`），不是 29 宽的外框中心。
- `config/ctt_shield_indicator.json` 配置：`enabled` / `showMeter` / `widthSlots` / `heightSlots` /
  `gapAboveActionBar` / `meterMode`（`texture` 或 `model`）。
- 无需服务端安装任何数据包或 Mod。

### 技术说明

- 26.x 起 Minecraft 客户端不再混淆，因此使用 Loom 的 no-remap 模式（`net.fabricmc.fabric-loom`），
  不需要 `mappings` 依赖。
- 挂载点使用 Fabric API 的 `HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, ...)`，
  不使用 mixin。
