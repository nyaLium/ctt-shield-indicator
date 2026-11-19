# CTT Shield Indicator（26.3 客户端 Mod）

[![build](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml/badge.svg)](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

[English](README.md) · **中文** · [更新日志](CHANGELOG.md) · [发布说明](docs/publishing.md) · [源码](https://github.com/nyaLium/ctt-shield-indicator) · [下载](https://github.com/nyaLium/ctt-shield-indicator/releases) · [Issues](https://github.com/nyaLium/ctt-shield-indicator/issues)

| 项目 | 值 |
| --- | --- |
| Minecraft | 26.3 |
| 加载器 | Fabric Loader 0.20.1+ / Fabric API 0.166.2+26.3 |
| 环境 | 纯客户端（`"environment": "client"`） |
| Java | 25 |

## 功能

- **副手**物品的 `minecraft:custom_data` 中 `shield:1b` 或 `shieldAbility:1b` 为真时，在副手槽位
  **正上方**显示盾牌冷却条。
- 只在副手显示；主手 / 快捷栏 / 背包中的同一物品不会触发。
- **耐久满时不显示**

| damage / max_damage | 显示 |
| --- | --- |
| 0（耐久满） | 不显示 |
| 0 &lt; r &lt; 0.4 | `meter_3`（3 格） |
| 0.4 ≤ r &lt; 0.6 | `meter_2`（2 格） |
| 0.6 ≤ r &lt; 0.8 | `meter_1`（1 格） |
| ≥ 0.8 | `meter_0`（0 格） |

## 位置

- 水平方向对齐的是副手**物品格**：原版把 16×16 的物品画在 `guiWidth/2-117`（左）/ `guiWidth/2+101`（右），
  所以物品格中心是 `center∓109`。（29 宽的外框 sprite 从 `center-120` 开始，它的中心 `center∓105.5`
  会偏 3.5px。）
- actionbar 文本画在 `translate(0, guiHeight-68)` 之后、本地 `y=-4` 处 → 文本顶边 ≈ `guiHeight-72`。
- 指示器底端 = `guiHeight - 72 - gapAboveActionBar`（默认 2px），所以它悬在 actionbar 上方，
  既不会被 actionbar 文字盖住，也不遮挡文字。

![布局预览（红虚线为可见内容 22×54.2，蓝虚线为 20×20 的槽位格）](docs/layout-preview.png)

## 构建

需要 JDK 25。仓库带 Gradle wrapper（9.9.1）：

```bash
./gradlew build
```

## 安装

1. Fabric Loader 0.20.1+ 与 Fabric API 0.166.2+26.3（客户端）。
2. jar 放进 `.minecraft/mods`。

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
| `widthSlots` | **可见内容**宽度 = 槽位格 20px × 本值。`1.0` 与槽位同宽，`1.1` 略宽（默认），`1.5` 更宽 |
| `heightSlots` | **可见内容**高度；`0` = 按 52:128 自动（宽 1.1 → 高 ≈54.2）。想压扁给 `1.5` → 22×30 |
| `gapAboveActionBar` | 底端与 actionbar 文字顶部的间距（像素） |
| `meterMode` | `texture`（默认）按上面尺寸绘制 PNG；`model` 直接渲染资源包的 `meter_N` 模型 |

改完保存后重新进入世界/重开游戏生效。

## 目录结构

```
src/main/java/com/nyalium/cttshieldindicator/
    CttShieldIndicatorClient.java   入口，注册 HUD 元素
    ShieldIndicatorHud.java         判定 + 绘制
    ShieldIndicatorConfig.java      config/ctt_shield_indicator.json
src/main/resources/
    fabric.mod.json
    assets/ctt_shield_indicator/    item model 定义（meter）+ 计量条纹理
    assets/minecraft/               meter 模型与纹理（meterMode=model 用）
docs/                               布局示意图、发布说明
```

## 相关链接

- 源码 / 问题反馈 — https://github.com/nyaLium/ctt-shield-indicator
- 下载（Releases） — https://github.com/nyaLium/ctt-shield-indicator/releases
- 更新日志 — [CHANGELOG.md](CHANGELOG.md)
- 发布说明 — [docs/publishing.md](docs/publishing.md)

## 许可证

MIT，见 [LICENSE](LICENSE)。
