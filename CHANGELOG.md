# Changelog

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)，格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)。

> 本文件对应 **Minecraft 1.21.4** 版本。功能、界面与配置格式和 26.3 版本完全一致，
> 差异只在编译目标与 HUD 挂载方式（见下方「技术说明」）。

## [1.2.0] - 2026-11-19

1.21.4 的首个版本，功能对齐 26.3 版的 1.2.0。

### 新增

- **位置**：可在「副手槽上方」（默认）和「屏幕中心」之间切换。「屏幕中心」指整个界面的
  正中（水平、垂直都居中），不是 actionbar 上方。
- **水平偏移**：在所选锚点的基础上左右平移，范围 ±160 像素，步进 1 像素；
  绘制时会自动把指示器限制在屏幕内（四周各留 2 像素边距），不会移出画面。
- **两个位置互不干扰**：每种位置各自保存一套「缩放比例 + 水平偏移」，来回切换不会丢掉另一边的调整。
- **透明度**：10%–100%，默认 100%，只影响指示器本身。
- 纯客户端 HUD 指示器：副手物品的 `minecraft:custom_data` 命中 `shield:1b` 或 `shieldAbility:1b` 时，
  在副手槽正上方显示耐久计量条；耐久满时不显示，主手 / 快捷栏 / 背包不触发。
- **Mod Menu 集成**：游戏内「模组 → CTT Shield Indicator → 配置」即可调整设置，
  带「恢复默认」按钮，关闭界面自动写回配置文件。Mod Menu 是可选依赖，未安装时照常运行。

### 变更

- 出厂值：副手槽模式 `scale 1.10 / offset 0`，屏幕中心模式 `scale 0.85 / offset +25`
  （略小，并向右让开准星）。
- 设置界面顺序：启用 → 位置 → 缩放比例 → 水平偏移 → 透明度 → 与 actionbar 的间距 → 恢复默认 / 完成；
  屏幕中心模式下「与 actionbar 的间距」置灰不可用。
- 计量条档位与 `CTT Shield Enhancing` 数据包的 `range_dispatch` 阈值一致：
  耐久损失 ≥80% → `meter_0`，≥60% → `meter_1`，≥40% → `meter_2`，其余 → `meter_3`。

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

旧配置里的 `scale` / `horizontalOffset`（1.1.0 的扁平格式）会自动迁移到 `offhand` 那套；
`position` / `opacity` / `offhand` / `center` 缺失时按默认值处理。

### 技术说明

- 1.21.4 仍使用混淆映射，走标准 Loom 重映射流程：`fabric-loom` 1.19.4 + Yarn `1.21.4+build.8`，
  依赖用 `modImplementation`。26.3 那边则是 no-remap（`minecraft` 客户端不混淆），两边构建脚本不通用。
- HUD 挂载用 Fabric API 的 `HudRenderCallback`（1.21.4 还没有 `HudElementRegistry`），不使用 mixin。
- 透明度通过 `DrawContext#drawTexture` 的 ARGB 顶点色实现；26.3 用的是 `GuiGraphicsExtractor#blit` 的 color 参数。
- 编译与运行需要 **Java 21**（26.3 版本需要 Java 25）。
