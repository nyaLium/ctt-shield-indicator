# CTT Shield Indicator

**Your shield, at a glance — before it breaks, not after.**

Shields from the CTT pack wear down quietly. The durability number is buried in the tooltip, the item
still looks the same, and the moment you finally notice is usually the moment it shatters in your hand.

This mod puts a small gauge right above your offhand slot and lets it shrink as your shield takes
hits: three segments while it is healthy, two when it is scratched, one when it is close to breaking,
and nothing when it is about to give out. At full durability it stays completely invisible, so your
HUD looks exactly as vanilla as before.

![The gauge sitting just above the offhand slot](https://raw.githubusercontent.com/nyaLium/ctt-shield-indicator/main/docs/layout-preview.png)

## Why players like it

- **Only when it matters.** It reacts to the shield in your offhand and nothing else — the same item
  in your main hand, hotbar or inventory never triggers it.
- **Never in the way.** It floats just above the actionbar, so it does not cover chat or actionbar
  text, and it is not covered by them either.
- **Works on any server.** Nothing has to be installed on the server side. Singleplayer, your
  friends' world, a big public server — it simply works.
- **Looks like part of the game.** The gauge is sized to match a hotbar slot, not a giant overlay.
- **No setup.** Install it and play. The defaults are already what most people want.

## Requirements

- **Minecraft 26.3**
- **Fabric Loader 0.20.1+**
- **Fabric API**
- **Java 25**
- Optional: **Mod Menu** — only if you want to change the settings in game

## Installation

1. Install Fabric Loader for Minecraft 26.3.
2. Put **Fabric API** and this mod into your `mods` folder.
3. Start the game. Done.

This is a **client-side** mod — you do not need to install it on a server, and servers do not need
to know about it.

## Settings

Out of the box there is nothing to configure. If you want to fine-tune it, install **Mod Menu** and
open *Mods → CTT Shield Indicator → Configure*:

- **Enabled** — turn the gauge on or off entirely.
- **Scale** — how wide the gauge is, from 0.60× to 2.50× of a hotbar slot.
- **Gap above actionbar** — how far it floats above the actionbar text.
- **Reset to defaults** — back to the original look.

Settings are saved automatically when you close the screen. Without Mod Menu you can also edit
`config/ctt_shield_indicator.json` by hand.

## FAQ

**The gauge is not showing.**
Three usual reasons: the shield is still at full durability (it hides itself), the item is not in
your offhand, or it is not a shield that the CTT pack marks as one.

**Do I need the CTT pack for this?**
The gauge only appears for shields marked by the CTT pack. The pack also provides the shield
textures, so keep it installed.

**Does it work with other shield textures or retextures?**
Yes — the gauge does not care what the shield looks like, only what it is.

**Will it break other mods?**
It only draws one small element on the HUD and adds no mixins to the game, so it plays nicely with
HUD and UI mods. If something does look off, the *Enabled* switch turns it off instantly.

**Will it get me banned on servers?**
It is a pure client-side display mod: it reads information the client already has and draws a gauge.
It does not send anything extra to the server.

## Links

- Source, downloads and issue reports — https://github.com/nyaLium/ctt-shield-indicator
- Change log — https://github.com/nyaLium/ctt-shield-indicator/blob/main/CHANGELOG.md

## License

MIT. Free to use, modify and include in modpacks.

---

# 中文说明

**盾牌还剩多少，一眼就够——不用等到它碎在手里。**

CTT 资源包里的盾牌掉耐久掉得很安静：耐久数字藏在提示框里，贴图看不出变化，等你发现的时候，
它往往正好在你手里碎掉。

这个 Mod 会在副手槽正上方加一条小计量条，盾牌挨打时它就从三格缩到两格、一格，最后空掉。
满耐久的时候它完全不显示，界面和原版一模一样。

![副手槽上方的计量条](https://raw.githubusercontent.com/nyaLium/ctt-shield-indicator/main/docs/layout-preview.png)

## 好用在哪

- **该出现时才出现。** 只认副手槽里的盾牌；同一件东西放主手、快捷栏或背包里都不会触发。
- **不挡视线。** 浮在 actionbar 上方，不遮聊天栏和 actionbar 文字，也不会被它们盖住。
- **任何服务器都能用。** 服务端什么都不用装。单人、朋友的世界、公开服务器，装上就好使。
- **像原版的一部分。** 大小与快捷栏格子对齐，不是那种糊满屏幕的巨型 UI。
- **不用设置。** 装完直接玩，默认值就是大多数人想要的。

## 前置

- **Minecraft 26.3**
- **Fabric Loader 0.20.1+**
- **Fabric API**
- **Java 25**
- 可选：**Mod Menu** —— 只有想在游戏内改设置时才需要

## 安装

1. 为 Minecraft 26.3 安装 Fabric Loader。
2. 把 **Fabric API** 和本 Mod 一起放进 `mods` 文件夹。
3. 启动游戏，完事。

这是**纯客户端** Mod：服务端不用装，也不会有任何感知。

## 设置

默认不用调。想微调就装 **Mod Menu**，打开「模组 → CTT Shield Indicator → 配置」：

- **显示** —— 整个计量条的开 / 关。
- **缩放比例** —— 计量条宽度，0.60× 到 2.50×（以快捷栏一格为基准）。
- **与 actionbar 的间距** —— 距离 actionbar 文字多远。
- **恢复默认** —— 回到原始外观。

关闭界面自动保存。没装 Mod Menu 也可以直接改 `config/ctt_shield_indicator.json`。

## 常见问题

**计量条不显示？**
通常是这三种情况：盾牌还是满耐久（会主动隐藏）、物品不在副手、或者它不是 CTT 资源包认定的盾牌。

**需要装 CTT 资源包吗？**
计量条只对 CTT 资源包认定的盾牌生效，而且盾牌贴图也是资源包提供的，所以建议一直装着。

**换了别的盾牌贴图还能用吗？**
能。它只关心这件物品是不是盾牌，不关心它长什么样。

**会和其他 Mod 冲突吗？**
它只在 HUD 上画一个小元素，没有往游戏里塞 mixin，和 HUD、界面类 Mod 相处得很好。
真觉得不对劲，用「显示」开关一秒关掉。

**在服务器用会被封吗？**
纯客户端显示：读的是客户端本来就有的信息，只画一条计量条，不会往服务端多发任何数据。

## 相关链接

- 源码、下载、问题反馈 —— https://github.com/nyaLium/ctt-shield-indicator
- 更新日志 —— https://github.com/nyaLium/ctt-shield-indicator/blob/main/CHANGELOG.md

## 许可证

MIT，可自由使用、修改，也可放进整合包。
