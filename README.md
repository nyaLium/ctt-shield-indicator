# CTT Shield Indicator（26.3 客户端 Mod）

[English](README_EN.md) · [更新日志](CHANGELOG.md) · [发布说明](docs/publishing.md) · [MIT License](LICENSE)

`CTT - Shield Enhancing` 数据包型 Mod 的**纯客户端重写版**：不再通过 `minecraft:item_model`
组件替换物品模型，而是由客户端自己在 HUD 上画耐久计量条，因此**服务端不需要安装任何数据包或 Mod**。

| 项目 | 值 |
| --- | --- |
| Minecraft | 26.3 |
| 加载器 | Fabric Loader 0.20.1+ / Fabric API 0.166.2+26.3 |
| 环境 | 纯客户端（`"environment": "client"`） |
| Java | 25 |

> 与原数据包目录（`CTT - Shield Enhancing v1.0 - 26.3 Mod/`）完全独立，可共存但功能重复，
> 实际使用时装其中一个即可。

## 功能

- **副手**物品的 `minecraft:custom_data` 中 `shield:1b` 或 `shieldAbility:1b` 为真时，在副手槽位
  **正上方**显示耐久计量条。
- **只画计量条**，不再重复绘制当前盾牌本身（槽位里已经有它了）。
- 只在副手显示；主手 / 快捷栏 / 背包中的同一物品不会触发。
- **耐久满时不显示**；`max_damage <= 0`（不可损坏）的物品不显示。
- 尺寸按**纹理的可见内容**算：`meter_N.png` 里真正有像素的区域是 `x 42..93 / y 4..131`，即 **52×128**，
  不是 136×136 的画布。可见宽度 = `20px（原版一格）× widthSlots`（默认 1.1 → 22，比槽位略宽），
  高度默认按 52:128 自动（≈54），可用 `heightSlots` 单独压低。
- 水平对齐的是**副手物品格的中心**（`center∓109`），不是副手外框中心（`center∓105.5`，
  外框 29 宽、物品 16 宽画在 `center∓117`，两者差 3.5 格单位）。

## 档位

与资源包 `assets/minecraft/items/shield_indicator/enable/*.json` 里的 `range_dispatch`
阈值对应（`RangeSelectItemModel` 取“阈值 ≤ 值”的最后一项）。因为不再绘制盾牌图标，
资源包里 `0 < r < 0.2` 那档“只有盾牌、没有计量条”的状态并入 `meter_3`：

| damage / max_damage | 显示 |
| --- | --- |
| 0（耐久满） | 不显示 |
| 0 &lt; r &lt; 0.4 | `meter_3`（3 格） |
| 0.4 ≤ r &lt; 0.6 | `meter_2`（2 格） |
| 0.6 ≤ r &lt; 0.8 | `meter_1`（1 格） |
| ≥ 0.8 | `meter_0`（0 格，提示快要碎） |

## 位置

- 副手槽按原版几何算：左手副手 `guiWidth/2-120` 起、右手副手 `guiWidth/2+91` 起，均为 29×24 的框
  → 指示器水平对齐槽位中心（`center∓105.5`）。
- actionbar 文本画在 `translate(0, guiHeight-68)` 之后、本地 `y=-4` 处 → 文本顶边 ≈ `guiHeight-72`。
- 指示器底端 = `guiHeight - 72 - gapAboveActionBar`（默认 2px），所以它悬在 actionbar 上方，
  既不会被 actionbar 文字盖住，也不遮挡文字。

![布局预览（蓝框区域为 20×20 的计量条，绿/橙为 meter_1 实际纹理）](docs/layout-preview.png)

## 从资源包里取的模型信息

| 资源包文件 | 读出的事实 | 在 Mod 里 |
| --- | --- | --- |
| `models/item/shields/_meter_/template.json` | 计量条是 **16×16 平面四边形**（`from [0,5,8] to [16,21,8]`，`uv` 铺满整张纹理），`gui` = `scale 4` / `translate [0,80,-80]` | 原样打包，供 `meterMode=model` 使用 |
| `textures/item/shields/meter_0..3.png` | **136×136**，不透明像素仅 `x 42..93 / y 4..131`（52×128） | 复制进 Mod，`texture` 模式按 20×20 绘制 → 约 7.6×18.8 的竖条 |
| `models/template/shield.json` 等盾牌模型 | 14 cube 的 3D 盾牌 | **已移除**（不再绘制盾牌图标） |

## 构建

需要 JDK 25。仓库带 Gradle wrapper（9.9.1）：

```bash
./gradlew build
```

产物：`build/libs/ctt_shield_indicator-1.0.0.jar`（约 40 KB）

> 26.x 起 Minecraft 客户端不再发布混淆映射，因此本项目使用 Loom 的 **no-remap** 模式
> （`id 'net.fabricmc.fabric-loom'`），不做 remap、也不需要 `mappings` 依赖。

## 安装

1. Fabric Loader 0.20.1+ 与 Fabric API 0.166.2+26.3（客户端）。
2. jar 放进 `.minecraft/mods`。
3. 物品侧仍需 `custom_data` 里的 `shield:1b` / `shieldAbility:1b`（由 CTT 本体数据决定）。

## 配置

首次启动生成 `config/ctt_shield_indicator.json`：

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

| 字段 | 说明 |
| --- | --- |
| `enabled` | 总开关 |
| `showMeter` | 关掉后整个指示器不出现 |
| `widthSlots` | **可见内容**宽度 = 槽位格 20px × 本值。`1.0` 与槽位同宽，`1.1` 略宽（默认），`1.5` 明显更宽 |
| `heightSlots` | **可见内容**高度；`0` = 按 52:128 自动（宽 1.1 → 高 ≈54.2）。想压扁给 `1.5` → 22×30 |
| `gapAboveActionBar` | 底端与 actionbar 文字顶部的间距（像素） |
| `meterMode` | `texture`（默认）按上面尺寸绘制 PNG；`model` 直接渲染资源包的 `meter_N` 模型 |

改完保存后重新进入世界/重开游戏生效。

## 验证记录（在 `runClient` 里真实启动过）

| 版本 | 结果 |
| --- | --- |
| 带盾牌图标版 | Mod 正常加载；因开发环境没装 CTT 本体资源包，出现 43 条 `Missing textures in model .../shield-*`（盾牌 `layer0` 指向 `minecraft:item/<name>`，本体提供） |
| 计量条纹理修复 | 模型版计量条曾缺 `minecraft:textures/item/shields/meter_N.png`，补上后 4 条告警归零 |
| **当前版（无图标、`size: 0` 自动 = 槽位 20）** | Mod 正常加载，**`Missing textures` 0 条**，无任何加载异常 |

- 客户端日志可见 `ctt_shield_indicator 1.0.0`，资源包重载列表包含 `ctt_shield_indicator`。
- 标题界面不渲染 HUD，自动化环境无法截图；位置/大小请在游戏里用 `size` / `gapAboveActionBar` 微调。

## 目录结构

```
src/main/java/com/namylium/cttshieldindicator/
    CttShieldIndicatorClient.java   入口，注册 HUD 元素
    ShieldIndicatorHud.java         判定 + 绘制
    ShieldIndicatorConfig.java      config/ctt_shield_indicator.json
src/main/resources/
    fabric.mod.json
    assets/ctt_shield_indicator/    item model 定义（meter）+ 计量条纹理
    assets/minecraft/               meter 模型与纹理（meterMode=model 用）
docs/                               布局示意图、发布说明
```

## 许可证

MIT，见 [LICENSE](LICENSE)。
