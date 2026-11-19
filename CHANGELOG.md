# Changelog

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)，格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。

## [1.3.0] - 2026-11-19

两个位置各自记住自己的参数，并新增透明度。

### 新增

- **透明度**：0%–100%，默认 100%；只影响指示器本身，不影响其它界面元素。
- **两个位置的参数互不干扰**：切换位置会载入该位置自己的一套「缩放比例 + 水平偏移」，
  来回切换不会丢掉另一边的调整。

### 变更

- 屏幕中心模式的出厂值改为 `scale 0.85`、`horizontalOffset +25`（略小，并向右让开准星）；
  副手槽模式仍是 `1.10 / 0`。
- 屏幕中心模式下隐藏「与 actionbar 的间距」一行——该模式用不到它。

### 配置文件

```json
{
  "enabled": true,
  "position": "offhand",
  "opacity": 1.0,
  "gapAboveActionBar": 2,
  "offhand": {
    "scale": 1.1,
    "offset": 0
  },
  "center": {
    "scale": 0.85,
    "offset": 25
  }
}
```

旧配置里的 `scale` / `horizontalOffset` 会自动迁移到 `offhand`；
`gapAboveActionBar` 保持原位置不变。

## [1.2.0] - 2026-11-19

新增指示器的位置选项，并支持左右平移。

### 新增

- **位置**：可在「副手槽上方」（默认，1.1.0 的行为）和「屏幕中心」之间切换。「屏幕中心」指整个界面的
  正中（水平、垂直都居中），不是 actionbar 上方；这种模式下「与 actionbar 的间距」不再生效。
- **水平偏移**：在所选锚点的基础上左右平移，范围 ±160 像素，步进 1 像素；
  绘制时会自动把指示器限制在屏幕内（四周各留 2 像素边距），不会移出画面。
- 以上两项在 Mod Menu 设置界面里分别是循环按钮和滑块，拖动即时生效。

### 变更

- 设置界面顺序：启用 → 位置 → 缩放比例 → 水平偏移 → 与 actionbar 的间距 → 恢复默认 / 完成。
- 配置文件新增 `position`（`"offhand"` / `"center"`）与 `horizontalOffset`（整数像素）两项；
  旧配置缺这两个字段时按默认值处理（跟随副手、不偏移），无需手动迁移。

## [1.1.0] - 2026-11-19

加入游戏内设置界面，并精简可配置项。

### 新增

- **Mod Menu 集成**：游戏内「模组 → CTT Shield Indicator → 配置」即可调整设置，
  带「恢复默认」按钮，关闭界面自动写回配置文件。Mod Menu 是可选依赖，未安装时照常运行。
- 设置界面的中英文文本（`assets/ctt_shield_indicator/lang/{en_us,zh_cn}.json`）。

### 变更

- 设置项精简：
  - 移除「显示计量条」（`showMeter`）——`enabled` 已能完全替代；
  - 移除「高度」（`heightSlots`）——高度始终按纹理 52:128 自动计算，避免误操作；
  - 移除「绘制方式」（`meterMode`）——只保留纹理绘制；
  - `widthSlots` 重命名为 **`scale`（缩放比例）**。
- 设置界面的数值项改为**滑块**：缩放比例 0.60–2.50（步进 0.05）、间距 0–20 px（步进 1）。
- 删除模型模式所需的资源（4 个 item model 定义、5 个 meter 模型、4 张 `minecraft:` 命名空间纹理），
  jar 从 46.7 KB 降到 36.3 KB。

### 配置文件

```json
{
  "enabled": true,
  "scale": 1.1,
  "gapAboveActionBar": 2
}
```

旧配置里的 `showMeter` / `heightSlots` / `meterMode` / `widthSlots` 会被忽略；
`widthSlots` 不会自动迁移到 `scale`，旧值会回到默认 1.1。

## [1.0.0] - 2026-11-19

首个发布版本，适配 Minecraft 26.3 / Fabric。

### 新增

- 纯客户端 HUD 指示器：副手物品的 `minecraft:custom_data` 命中 `shield:1b` 或 `shieldAbility:1b` 时，
  在副手槽正上方显示耐久计量条。
- 计量条档位与 `CTT Shield Enhancing` 数据包的 `range_dispatch` 阈值一致：
  耐久损失 ≥80% → `meter_0`，≥60% → `meter_1`，≥40% → `meter_2`，其余 → `meter_3`。
- 耐久满时不显示；仅在副手显示，主手 / 快捷栏 / 背包不触发。
- 位置固定在 actionbar 文字上方（可配置间距），不遮挡也不被遮挡。
- 尺寸以“纹理可见内容 52×128”为基准：宽度 = 槽位格 20px × 比例，高度按比例或单独指定。
- 水平对齐副手**物品格**中心（`center∓109`），不是 29 宽的外框中心。
- `config/ctt_shield_indicator.json` 配置。
- 无需服务端安装任何数据包或 Mod。

### 技术说明

- 26.x 起 Minecraft 客户端不再混淆，因此使用 Loom 的 no-remap 模式（`net.fabricmc.fabric-loom`），
  不需要 `mappings` 依赖。
- 挂载点使用 Fabric API 的 `HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, ...)`，
  不使用 mixin。
