# 发布说明（GitHub / Modrinth / CurseForge）

## 一、GitHub：需要上传哪些文件

仓库里**只保留源码与构建脚本**，构建产物和本地缓存不进仓库（`.gitignore` 已覆盖）。

### 必须提交（共 39 个文件，约 156 KB）

```
.gitattributes                 换行策略（gradlew 用 LF、.bat/.ps1 用 CRLF、png/jar 视为二进制）
.gitignore                     排除 build/ .gradle/ run/
LICENSE                        MIT，构建时会打进 jar
README.md                      英文主说明（默认，面向国际用户 / 平台）
README.zh-CN.md                中文说明
CHANGELOG.md                   版本记录
build.gradle                   构建脚本
settings.gradle
gradle.properties              版本号集中在这里
gradlew / gradlew.bat          Gradle Wrapper 启动脚本
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
docs/layout-preview.png        文档用布局示意图
docs/icon-512.png              平台用 512x512 图标
docs/publishing.md             本文件
.github/workflows/build.yml    CI
src/main/java/com/nyalium/cttshieldindicator/*.java        3 个类
src/main/resources/fabric.mod.json
src/main/resources/assets/**   4 个 item model 定义 / 5 个模型 / 9 张纹理
```

### 不要提交（已被忽略）

| 路径 | 原因 |
| --- | --- |
| `build/` | Gradle 构建产物（jar 在 Releases 上传，不进仓库） |
| `.gradle/` | 本地 Gradle 缓存（本机约 88 MB） |
| `run/` | `runClient` 产生的游戏目录（assets 缓存、日志、存档） |

### 可选

- `.github/workflows/build.yml`：推送到 main 时自动 `./gradlew build` 并上传 artifact。
- 仓库设置里加 Topics：`minecraft` `fabric` `minecraft-mod` `hud` `client-side`。

### 首次推送

仓库已经 `git init` 并提交好了，只差远程地址：

```bash
cd "CTT - Shield Enhancing v1.0 - 26.3 Client Mod"
git remote add origin https://github.com/nyaLium/ctt-shield-indicator.git
git push -u origin main
```

> 本仓库的提交身份已设为 `nyaLium <nyaLium@users.noreply.github.com>`。
> 换成自己的邮箱：`git config user.email "<你的邮箱>"`，再
> `git rebase --root --exec "git commit --amend --no-edit --reset-author"`。

`fabric.mod.json` 里已经写好了 `contact`：

```json
"contact": {
  "homepage": "https://github.com/nyaLium/ctt-shield-indicator",
  "sources": "https://github.com/nyaLium/ctt-shield-indicator",
  "issues": "https://github.com/nyaLium/ctt-shield-indicator/issues"
}
```

> 如果仓库名不叫 `ctt-shield-indicator`，改这三行（以及两个 README 顶部链接）再 `./gradlew build` 一次。

## 二、要发布的文件

只上传**一个文件**：`build/libs/ctt_shield_indicator-1.0.0.jar`（40 KB 左右）。

- Modrinth / CurseForge 都**不要**上传 `-sources.jar`。
- 平台会自动生成下载文件名，无需改名。
- GitHub 上请走 **Releases**（打 tag `v1.0.0`，附上同一个 jar + `CHANGELOG.md` 内容），
  不要把 jar 提交进仓库。

## 三、平台元数据（照抄即可）

| 字段 | 值 |
| --- | --- |
| 名称 | `CTT Shield Indicator` |
| 摘要（≤256 字符） | `Client-side shield durability gauge above the offhand slot. Reads custom_data (shield / shieldAbility) and damage; no server-side datapack needed.` |
| 中文摘要 | `纯客户端：副手物品带有 shield/shieldAbility 时，在副手槽上方显示盾牌耐久计量条，无需服务端。` |
| 版本号 | `1.0.0` |
| 支持的 MC 版本 | `26.3`（只写这一个） |
| 加载器 | `Fabric` |
| 环境 | **Client only**（`fabric.mod.json` 里是 `"environment": "client"`） |
| 许可证 | `MIT` |
| 依赖 | `Fabric API`（必需）、`Fabric Loader >= 0.20.1`、`Java 25` |
| 分类 / Tags | `Utility`、`HUD`、`Client-side`、`Equipment`（Modrinth）；`Cosmetic` / `HUD`（CurseForge） |
| 图标 | 用 `docs/icon-512.png`（512×512，已按原图放大） |
| 源码链接 | GitHub 仓库地址 |
| Issue 链接 | `.../issues` |

### 平台描述正文（中文，可直接粘）

```markdown
## 简介

纯客户端 HUD Mod：当**副手**物品的 `minecraft:custom_data` 含有 `shield:1b` 或 `shieldAbility:1b` 时，
在副手槽正上方显示一个耐久计量条。**不需要服务端安装任何东西**。

这是 `CTT Shield Enhancing` 数据包的客户端重写版——不再通过 `minecraft:item_model` 替换物品模型，
而是客户端自己读取组件与耐久并绘制，所以单人、多人、任何服务器都能用。

## 特性

- 只在副手显示；主手 / 快捷栏 / 背包不触发
- 耐久满时不显示；耐久越少格子越少（3 → 0）
- 位置固定在 actionbar 文字上方，不遮挡也不被遮挡
- 尺寸以纹理可见内容（52×128）为基准，宽度 = 一格 × `widthSlots`（默认 1.1）
- 完全可配置：`config/ctt_shield_indicator.json`

## 前置

- Minecraft 26.3
- Fabric Loader 0.20.1+
- Fabric API 0.166.2+26.3
- Java 25

## 说明

- 纯客户端，`environment: client`，服务端无需安装
- 与 `CTT` 本体资源包配合使用（物品贴图由本体提供）
- MIT 许可证
```

### Platform description (English)

```markdown
## What it does

A **client-side** Fabric mod for Minecraft 26.3. When the offhand item's `minecraft:custom_data`
contains `shield:1b` or `shieldAbility:1b`, a durability gauge is drawn above the offhand slot.
**Nothing is required on the server.**

This is the client-mod rewrite of the `CTT Shield Enhancing` datapack: instead of swapping
`minecraft:item_model` components, the client reads the component and the damage value directly,
so it works in singleplayer, on servers, everywhere.

## Features

- Offhand only — main hand / hotbar / inventory never trigger it
- Hidden at full durability; fewer cells as durability drops (3 → 0)
- Anchored just above the actionbar text, never overlapping it
- Sized from the texture's visible content (52×128): width = one slot × `widthSlots` (default 1.1)
- Fully configurable via `config/ctt_shield_indicator.json`

## Requirements

- Minecraft 26.3
- Fabric Loader 0.20.1+
- Fabric API 0.166.2+26.3
- Java 25

## Notes

- Client-only (`environment: client`), no server install needed
- Meant to be used together with the CTT resource pack (item textures come from it)
- MIT license
```

## 四、发布前检查清单

- [ ] `gradle.properties` 里的 `mod_version` 已更新
- [ ] `CHANGELOG.md` 已补本次版本条目
- [ ] `./gradlew clean build` 通过
- [ ] jar 内包含 `LICENSE_ctt_shield_indicator`（`LICENSE` 文件存在后会自动打入）
- [ ] 已在真实客户端里验证过（进世界看指示器位置/大小）
- [ ] GitHub Release 打了 tag `v1.0.0` 并附 jar
- [ ] 平台里把 Game version 勾成 `26.3`、Loader 勾成 `Fabric`、Environment 选 `Client`
- [ ] 依赖里声明 Fabric API（否则用户会遇到启动报错）
