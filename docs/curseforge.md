# CurseForge 上传步骤

CurseForge 专用的逐步操作单。通用元数据见 [publishing.md](publishing.md)；
本文件第 2 步要粘的正文已经单独写好：[curseforge-description.md](curseforge-description.md)（整份复制）。

## 0. 准备清单

| 项 | 位置 | 说明 |
| --- | --- | --- |
| 上传文件 | `build/libs/ctt_shield_indicator-1.1.0.jar` | 36.3 KB，**只传这一个**；不要传 `-sources.jar` |
| 项目头像 | `docs/icon-512.png` | 512×512，CurseForge 要求 ≥400×400 |
| 截图 | `docs/layout-preview.png` | 854×480，用作 Gallery 第一张 |
| 描述正文 | [curseforge-description.md](curseforge-description.md) | 英文在前、中文在后，两边用户都能看 |
| 更新日志 | `CHANGELOG.md` 的 `[1.1.0]` 段 | 粘到上传页的 Changelog 字段 |
| 前置完成 | GitHub Release `v1.1.0` | 项目设置里要填 Source / Issues，先有 Release 更可信 |

## 1. 新建项目

1. 打开 <https://authors.curseforge.com/#/projects>（作者后台），点 **Create Project**。
2. 游戏选 **Minecraft** → 类型选 **Mods**。
3. 基本信息按下表填：

| 字段 | 值 |
| --- | --- |
| Project name | `CTT Shield Indicator` |
| Summary | `Client-side shield durability gauge above the offhand slot. Reads custom_data (shield / shieldAbility); nothing to install on the server.` |
| Primary category | `Utility & QoL` |
| Secondary category | `Armor, Tools, and Weapons`（该分类不存在就留空） |
| License | `MIT License`（CurseForge 内置选项，语义与仓库里的 `LICENSE` 一致） |
| Project avatar | `docs/icon-512.png` |
| Comments / 其他开关 | 保持默认 |

> 摘要字段上限约 255 字符，建议实际控制在 150 字符内，列表页只显示一行。
> 项目 slug 会由名称生成，正常是 `ctt-shield-indicator`，第 6 步以实际地址为准。

## 2. 填 Description

把 [curseforge-description.md](curseforge-description.md) 的**全部内容**粘进 Description 编辑器
（Markdown：支持标题、列表、表格、图片、链接，不支持 HTML）。

粘完检查三处：

- 图片能显示 —— 用的是 `raw.githubusercontent.com` 绝对地址，**仓库必须是 public**；
- 链接全是绝对地址，没有 `./docs/...` 这种 GitHub 相对路径（粘到 CurseForge 后会 404）；
- 围栏代码块（三个反引号）里的 JSON 缩进没被编辑器吃掉。

> 如果编辑器停留在旧版 **BBCode** 模式（看到 `[b]`/`[list]` 按钮），切到 Markdown 标签页再粘；
> 只能 BBCode 时把标题写成 `[size=5][b]标题[/b][/size]`、列表写成 `[list][*]项[/list]`。

## 3. 加 Gallery 截图

项目页 → **Gallery** → 上传：

| 图片 | Title | Description |
| --- | --- | --- |
| `docs/layout-preview.png` | `Gauge position above the offhand slot` | `Dashed red: visible texture content (22 × 54.2 px). Dashed blue: the 20 × 20 px slot cell.` |

## 4. 上传文件

项目页 → **Files** → **Upload File**：

| 字段 | 值 |
| --- | --- |
| File | `ctt_shield_indicator-1.1.0.jar` |
| Display name | `ctt_shield_indicator-1.1.0`（留空也行，会取文件名） |
| Release type | `Release` |
| Game version | `26.3` ——**只勾这一个** |
| Mod loader | `Fabric` ——不要勾 Forge / NeoForge / Quilt |
| Environment / Side | 有这个选项就选 `Client`；多数流程里没有，忽略即可 |
| Changelog | 粘 `CHANGELOG.md` 中 `## [1.1.0]` 到 `## [1.0.0]` 之前的内容 |

### 依赖关系（最容易漏）

同一个上传页的 **Add Dependency / Relations** 里加两条：

| 项目 | 关系 | 理由 |
| --- | --- | --- |
| `Fabric API` | **Required** | 没有它启动阶段直接崩 |
| `Mod Menu` | **Optional** | 只影响游戏内设置界面，标成 Required 会拦住不需要它的用户 |

`Fabric Loader` 在 CurseForge 上不是独立项目，不用填（版本要求写在描述和 `fabric.mod.json` 里）。
搜不到 `Mod Menu` 时先打开 <https://www.curseforge.com/minecraft/mc-mods/modmenu> 一次，再回上传页搜索。

## 5. 提交

CurseForge 的 Mod 项目通常**上传即发布**，不排队。若提示 `Under review`，等邮件通知
（一般几分钟到几小时），期间文件已经可以下载。

## 6. 发布后

1. 记下项目地址，形如 `https://www.curseforge.com/minecraft/mc-mods/ctt-shield-indicator`；
   文件地址形如 `.../ctt-shield-indicator/files/<fileId>`。
2. 把这两个链接补进 `README.md` 与 `README.zh-CN.md` 的 `## Links` 段。
3. 想同时上 Modrinth，把同一个 jar 再传一次；两边版本互不影响。

## 7. 常见问题

| 现象 | 原因与处理 |
| --- | --- |
| Game version 列表里没有 `26.3` | CurseForge 的版本列表是人工维护的（26.3 是 2026-09-15 的正式版，Modrinth 已收录，CurseForge 一般也会跟上）。真没有就到 <https://www.curseforge.com/minecraft/game-versions> 用 **Request a game version**，或邮件说明「Minecraft 26.3 已发布，请添加」。**不要**将就勾别的版本：用户会被误导，按版本筛选也搜不到。 |
| 上传报 `File already exists` | 同版本 jar 传过了。删掉旧文件再传，或提高 `mod_version` 重新 build。 |
| 用户报 `NoClassDefFoundError: com/terraformersmc/...` | 装了本 Mod 但没装 Mod Menu 的极端情况。确认 `fabric.mod.json` 里 Mod Menu 仍是 `suggests` 而不是 `depends`，且 `ModMenuIntegration` 只由 Mod Menu 的 entrypoint 调起。 |
| 描述里的图片打不开 | 仓库改 public，或改用 Gallery 里 CurseForge CDN 的图片地址。 |
| 页面提示缺少依赖 / 用户说启动不了 | 依赖漏了 Fabric API，回 Files 页编辑该文件补上 Required 关系。 |
| 手机端 / 盗版启动器找不到 | CurseForge 只保证官方启动器与 CurseForge App 的可见性；其他启动器按 slug 搜。 |
