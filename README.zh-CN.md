# CTT Shield Indicator（26.3 客户端 Mod）

[![build](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml/badge.svg)](https://github.com/nyaLium/ctt-shield-indicator/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

[English](README.md) · **中文** · [更新日志](CHANGELOG.md) · [发布说明](docs/publishing.md) · [源码](https://github.com/nyaLium/ctt-shield-indicator) · [下载](https://github.com/nyaLium/ctt-shield-indicator/releases) · [Issues](https://github.com/nyaLium/ctt-shield-indicator/issues)

| 项目 | 值 |
| --- | --- |
| Minecraft | 26.3 |
| 加载器 | Fabric Loader 0.20.1+ / Fabric API 0.166.2+26.3 |
| Mod Menu（可选） | 21.0.0+，只有游戏内设置界面需要 |
| 环境 | 纯客户端（`"environment": "client"`） |
| Java | 25 |

## 功能

- **副手**物品的 `minecraft:custom_data` 中 `shield:1b` 或 `shieldAbility:1b` 为真时，在副手槽位
  **正上方**显示盾牌冷却条。
- 只在副手显示；主手 / 快捷栏 / 背包中的同一物品不会触发。
- **耐久满时不显示**
- 所有设置都可以**在游戏内通过 Mod Menu 修改**（模组 → CTT Shield Indicator → 配置），也可以直接改配置文件。

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

首次启动生成 `config/ctt_shield_indicator.json`。装了 **Mod Menu** 的话，下面这些都能在游戏内改
（模组 → CTT Shield Indicator → 配置），界面上还有「恢复默认」按钮。

```json
{
  "enabled": true,
  "scale": 1.1,
  "gapAboveActionBar": 2
}
```

| 字段 | 说明 |
| --- | --- |
| `enabled` | 总开关，关掉后什么都不画 |
| `scale` | 缩放比例：可见内容宽度 = 一格 20px × 本值。`1.0` 与槽位同宽，`1.1` 略宽（默认）；高度始终按纹理 52:128 自动 |
| `gapAboveActionBar` | 底端与 actionbar 文字顶部的间距（像素） |

游戏内（Mod Menu）这两个数值都是滑块，可以边看效果边拖。

改完保存后重新进入世界/重开游戏生效。

## 目录结构

```
src/main/java/com/nyalium/cttshieldindicator/
    CttShieldIndicatorClient.java   入口，注册 HUD 元素
    ShieldIndicatorHud.java         判定 + 绘制
    ShieldIndicatorConfig.java      config/ctt_shield_indicator.json（读/写/恢复默认）
    ModMenuIntegration.java         Mod Menu 入口点（可选依赖）
    ShieldIndicatorConfigScreen.java  游戏内设置界面
src/main/resources/
    fabric.mod.json
    assets/ctt_shield_indicator/    计量条纹理、图标、en_us/zh_cn 语言文件
docs/                               布局示意图、发布说明（含 CurseForge 上架步骤）
```

## 相关链接

- 源码 / 问题反馈 — https://github.com/nyaLium/ctt-shield-indicator
- 下载（Releases） — https://github.com/nyaLium/ctt-shield-indicator/releases
- 更新日志 — [CHANGELOG.md](CHANGELOG.md)
- 发布说明 — [docs/publishing.md](docs/publishing.md)
- CurseForge 上架 — [docs/curseforge.md](docs/curseforge.md)

## 许可证

MIT，见 [LICENSE](LICENSE)。
