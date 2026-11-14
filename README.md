<div align="center">

<img src="desktop/icon.png" width="128" alt="V-Converter icon">

# V-Converter

**在 Psych Engine、V-Slice、Codename Engine 和 NightmareVision 之间互转 Friday Night Funkin' 模组，也能转到 FPS Plus。**

[![Build](https://github.com/YLuzew/V-Converter/actions/workflows/build.yml/badge.svg)](https://github.com/YLuzew/V-Converter/actions/workflows/build.yml)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Windows](https://img.shields.io/badge/Windows-0078D4?logo=windows&logoColor=white)
![Linux](https://img.shields.io/badge/Linux-FCC624?logo=linux&logoColor=black)
![macOS](https://img.shields.io/badge/macOS-000000?logo=apple&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?logo=ios&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Vibe coded](https://img.shields.io/badge/vibe%20coded-%E2%9C%A8-ff69b4)

</div>

> [!IMPORTANT]
> **简体中文汉化版。** 本仓库是 [BrenninhoTools/V-Converter](https://github.com/BrenninhoTools/V-Converter) 的汉化分支，由 [YLuzew](https://github.com/YLuzew) 和 [LS_Xenith](https://github.com/LS_Xenith)（玄虚）汉化。
> 汉化只改字符串字面量（界面文案），转换逻辑、生成器、文件格式一行未动 —— 用它转出来的模组和原版完全一样。
> 覆盖 Android / Windows / Linux / macOS / iOS 共用的那套 Compose 界面，桌面图标名也改成了「V-转换器」。漏翻、翻错的，提到[本仓库的 issue](https://github.com/YLuzew/V-Converter/issues)。

> [!NOTE]
> **这个 App 是 vibe coding 写出来的。** V-Converter 靠 vibe coding 完成：代码是按提示词让 AI（Claude）写的，然后拿引擎源码逐条核对、实测。转换结果都验证过，但别指望一点毛刺没有；坏了就[开个 issue](https://github.com/BrenninhoTools/V-Converter/issues)。

这是 V-Slice 转换器的重制版，用 Kotlin 写成的原生应用，支持 **Android**、**iOS**、**Windows**、**Linux** 和 **macOS**。它能把 **Psych Engine** 和 **Codename Engine** 的内容转到 **V-Slice**（现在的基础游戏），也能从 V-Slice 转回 Psych，在 Psych 和 Codename 之间双向转，在 [NightmareVision](https://github.com/NMVTeam/NightmareVision) 和 Psych / V-Slice / Codename 之间双向转，还能把其中任意一种转到 [FPS Plus](https://github.com/ThatRozebudDude/FPS-Plus-Public)。

## 目录

- [下载](#下载)
- [支持的转换](#支持的转换)
- [生成器](#生成器) · [谱面编辑器](#谱面编辑器) · [人物编辑器](#人物编辑器) · [背景编辑器](#背景编辑器) · [周目编辑器](#周目编辑器)
- [Psych → V-Slice](#psych--v-slice) · [V-Slice → Psych](#v-slice--psych) · [Codename → V-Slice](#codename--v-slice) · [Codename → Psych](#codename--psych) · [Psych → Codename](#psych--codename) · [NightmareVision](#nightmarevision) · [FPS Plus](#fps-plus)
- [多人编辑](#多人编辑) · [试玩](#试玩) · [在线服务器](#在线服务器)
- [汉化说明](#汉化说明) · [构建](#构建)

## 下载

最新版去 [Releases](https://github.com/YLuzew/V-Converter/releases) 下：Android 拿 `V Converter-Android.apk`，Windows 拿 `V Converter-Windows.exe`（安装包）。每个版本的更新日志在 [`docs/releases`](docs/releases)。正式发布由 [Release workflow](https://github.com/BrenninhoTools/V-Converter/actions/workflows/release.yml) 手动填版本号后触发。本仓库自己的构建产物直接放在 [Build workflow](https://github.com/YLuzew/V-Converter/actions/workflows/build.yml) 的运行记录里。

每次往 `main` 推代码，[GitHub Actions](https://github.com/YLuzew/V-Converter/actions/workflows/build.yml) 都会构建一次。打开最近一次成功的运行，在页面最下面拿 artifact：

| Artifact | 平台 | 内容 |
| --- | --- | --- |
| `V Converter-Android` | 📱 Android | `V Converter-Android.apk`，R8 优化过的已签名 APK |
| `V Converter-Windows` | 🪟 Windows | `V Converter-Windows.exe`，安装包 |
| `V Converter-Linux` | 🐧 Linux | Debian/Ubuntu 用 `V Converter-Linux.deb`；`V Converter-Linux.tar.gz` 是便携版，任何发行版解压就能跑（执行 `V-Converter/bin/V-Converter`） |
| `V Converter-MacOS` | 🍎 macOS | `V Converter-MacOS.dmg`（把应用拖进「应用程序」）和内含应用的 `V Converter-MacOS.zip`。没有做公证：第一次打开要右键选「打开」，或者去「系统设置 → 隐私与安全性」里放行 |
| `V Converter-IOS` | 📱 iOS / iPadOS | `V Converter-IOS.ipa`，未签名：用 AltStore、SideStore、Sideloadly 或 TrollStore 装（iOS 15 及以上） |

## 支持的转换

| 模块 | Psych → V-Slice | V-Slice → Psych | Codename → V-Slice | Codename → Psych | Psych → Codename |
| --- | :---: | :---: | :---: | :---: | :---: |
| 整个模组（文件夹 / .zip / .rar / .7z） | ✅ | ✅ | ✅ | ✅ | ✅ |
| 谱面 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 人物 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 背景 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 周目 ↔ 关卡 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 脚本 | ✅ | ✅ | ✅ | ✅ | ✅ |

| 方向 | 脚本 |
| --- | --- |
| Psych → V-Slice | Lua / HScript → `.hxc` |
| V-Slice → Psych | `.hxc` → Psych HScript |
| Codename → V-Slice | Codename HScript → `.hxc` |
| Codename → Psych | Codename HScript → Psych HScript |
| Psych → Codename | Lua / HScript → Codename HScript |
| Psych / V-Slice / Codename → NightmareVision | Lua / HScript → NightmareVision HScript |
| NightmareVision → Psych / V-Slice / Codename | NightmareVision HScript → Psych HScript，再走常规转换 |
| Psych / V-Slice / Codename / NightmareVision → FPS Plus | Lua / HScript → FPS Plus 的 `Script` 类（`.hxc`） |

Android 版分两大块，在顶部切换：**转换器**和**生成器**（从零做 V-Slice 内容）。转换器里先选源引擎（**Psych**、**Codename**、**V-Slice** 或 **NMV**；**FPS Plus** 只能作为目标），目标引擎在每个标签页顶部的桥上选；点 **To** 那张卡或中间的交换按钮可以调换方向。Windows、Linux、macOS 版在侧边栏选引擎，文件和模组可以直接拖进窗口。两端用的是同一个 V-Converter 图标（带四向箭头的麦克风，在 `desktop/icon.png`；Windows 和 macOS 的包用 `icon.ico` 和 `icon.icns`）和 `assets/images/cursor/cursor.png` 里的自定义光标（Android 上接鼠标或触控板时显示；触摸屏也显示，除非在设置里关掉「触摸光标」）。右上角有个 `FPS:` 计数器，菜单音乐会放 `assets/music/freakyMenu.ogg`（谱面编辑器里放歌时暂停），点标签页和按钮时放 `assets/sounds/scrollMenu.ogg`；计数器、音效、音乐和音量都在设置里调（右上角的齿轮）。

## 生成器

| 标签页 | 生成的内容 |
| --- | --- |
| 模组 | `_polymod_meta.json`、`_polymod_icon.png`，可选一个起始模块脚本 |
| 谱面 | [谱面编辑器](#谱面编辑器)：从名字起一首歌，或者打开任意谱面跟着音乐改 |
| 人物 | [人物编辑器](#人物编辑器)：打开任意人物或一张图集，修它的动画和偏移 |
| 背景 | [背景编辑器](#背景编辑器)：打开任意背景，或者从自己的图片开始，对着游戏摄像机摆版面 |
| 周目 | [周目编辑器](#周目编辑器)：打开任意周目，或者新建一个，排它的故事菜单、歌曲和解锁 |
| 脚本 | `.hxc` 类：模块、歌曲、背景、人物（Sparrow、Multi-Sparrow、Animate 图集、Multi-atlas、Packer）、歌曲事件（带谱面编辑器字段和游戏回调）、音符类型，回调自己勾 |

每个标签页都能把结果下成 `.zip`，或者直接写进模组文件夹。

## 谱面编辑器

Android 上是生成器里的**谱面**标签，Windows、Linux、macOS 上是侧边栏里的**谱面编辑器**。

- **新建或打开谱面**：填个名字、BPM 和难度就能起一首歌；也可以打开 V-Slice 的 `<song>-metadata.json` + `<song>-chart.json`、Psych 的谱面 `.json`，或者一首 Codename 的歌（`meta.json` + `charts/*.json`）。文件夹和 `.zip`/`.rar`/`.7z` 压缩包都行，谱面旁边有 `Inst`/`Voices` 音频的话会一起读进来。
- **音符网格**：V-Slice 那种棋盘格，扫弦线在最上面，音符、长条和接收器用基础游戏的 `assets/images/ui/NOTE_assets.png` 画，对手轨道在左、玩家轨道在右、事件轨道最后，事件用 `assets/images/ui/charter/event.png` 显示。点一条轨道就按网格吸附放一个音符（1/4 到 1/192），再点一下选中，第三次点删掉。选中的音符可以设长条长度和音符类型（`alt`、`hurt` 等）。拖动或滚轮可以滚动、缩放、按小节跳，工具栏能撤销重做，点或拖网格旁边的迷你地图可以在歌里跳。宽屏上网格夹在两侧面板中间，手机上那些面板是网格下面的标签页。
- **音乐**：放伴奏，人声混在里面（单个人声文件或玩家/对手分开都行），可选在音符经过扫弦线时放打击音。没有音频时，谱面照样按 BPM 走。
- **事件和 BPM**：`FocusCamera`、`PlayAnimation`、`ZoomCamera`、`ScrollSpeed`、`SetCameraBop` 各有自己的字段，也可以自己写事件配一个 JSON 值。BPM 和拍号变化放在时间轴上，网格会跟着变。
- **难度**：加空难度或复制难度、删难度、镜像或清空音符、单独设每个难度的滚动速度。歌曲信息（名字、作者、谱师、人物、背景、音符样式、偏移、变体）就地改。
- **导出**：V-Slice（`data/songs/<id>/` + `songs/<id>/` 音频）、Psych Engine（`data/<id>/` 谱面和 `events.json`、`songs/<id>/Inst.ogg` 和 `Voices*.ogg`）或 Codename Engine（`songs/<id>/meta.json`、`charts/` 和 `song/` 音频，扫弦线指向分开的人声），可以打成 `.zip`，也可以直接写进模组文件夹。

Windows、Linux、macOS 上：<kbd>Space</kbd> 播放，<kbd>Ctrl</kbd>+<kbd>Z</kbd> / <kbd>Ctrl</kbd>+<kbd>Y</kbd> 撤销重做，<kbd>Q</kbd> / <kbd>E</kbd> 缩短或拉长选中的长条，<kbd>Delete</kbd> 删除选中，<kbd>Page Up</kbd> / <kbd>Page Down</kbd> 跳一个小节。

## 人物编辑器

Android 上是生成器里的**人物**标签，Windows、Linux、macOS 上是侧边栏里的**人物编辑器**。

- **什么都能打开**：V-Slice 或 Psych 人物的 `.json`、Codename 人物的 `.xml`，连同它的图集 `.png` + `.xml`（文件夹和压缩包也行）。只给一张图集的话，会新建一个人物，从帧名猜动画（`idle`、`singLEFT` 等、miss、`danceLeft`/`danceRight`）。
- **动画预览**：按各自的帧率、循环和帧索引播放图集里的每个动画，可以逐帧看，画的位置和 V-Slice 摆人物一样（脚踩地板线，偏移跟着精灵缩放）。
- **偏移**：拖精灵或用方向按钮移动当前动画，后面垫着待机（或任意别的动画）的残影。撤销重做覆盖每一次改动。
- **背景和游戏摄像机**：人物站在 `assets/images/ui/character/stage.png` 的地板上（**背景**这个 chip 可以隐藏它）。摄像机标记画在 V-Slice 指的位置：人物中心 + 它的摄像机偏移 + 背景给它那一侧的默认偏移，不计全局偏移。虚线框是 1280×720 的摄像机在当前**预览缩放**下看到的范围。
- **人物设置**：侧别（玩家、对手、女友）、缩放、位置、摄像机偏移（预览上也能直接拖）、水平翻转、像素画、血条图标、唱歌时长、跳动频率和起始动画。
- **导出**：V-Slice（`data/characters/<id>.json` + `images/…`）、Psych Engine（`characters/<id>.json`，偏移和位置按选的侧别换算）或 Codename Engine（`data/characters/<id>.xml` + `images/characters/…`），一律带上图集。

Windows、Linux、macOS 上：<kbd>Space</kbd> 播放，方向键移动偏移（<kbd>Shift</kbd> 一次 10），<kbd>W</kbd> / <kbd>S</kbd> 切动画，<kbd>,</kbd> / <kbd>.</kbd> 逐帧，<kbd>G</kbd> 开关残影，滚轮缩放。

## 背景编辑器

Android 上是生成器里的**背景**标签，Windows、Linux、macOS 上是侧边栏里的**背景编辑器**。

- **什么都能打开**：V-Slice 或 Psych 的背景 `.json`（用 Lua 生成精灵的会连 `.lua` 一起读），或者 Codename 的背景 `.xml`，连同背景图片；直接打开整个模组文件夹或压缩包，会把背景用到的图片全找出来。新建背景从基础游戏的人物位置开始。
- **版面**：每个物件用它的图片画出来（动画 Sparrow 画第一帧），带上缩放、视差因子、透明度、角度和翻转；玩家、对手和女友按脚底位置画框，标出它们的摄像机点。虚线框是背景缩放下的游戏摄像机，视差会跟着你移动视图按滚动因子走。
- **编辑**：点一下选中，拖动移动，方向按钮微调，图层按 `zIndex` 排序并支持上移下移，复制和删除，纯色物件，从 `.png` 加图片（动画再加 `.xml`），背景名、缩放和资源库。撤销重做覆盖每一次改动。
- **导出**：V-Slice（`data/stages/<id>.json`）、Psych Engine（`stages/<id>.json`，Psych 的 JSON 装不下的部分另出一个 Lua 脚本）或 Codename Engine（`data/stages/<id>.xml`），一律把图片放进 `images/`。

Windows、Linux、macOS 上：方向键微调选中项（<kbd>Shift</kbd> 一次 10），<kbd>Page Up</kbd> / <kbd>Page Down</kbd> 改图层，<kbd>Ctrl</kbd>+<kbd>D</kbd> 复制，<kbd>Delete</kbd> 删物件，滚轮缩放。

## 周目编辑器

Android 上是生成器里的**周目**标签，Windows、Linux、macOS 上是侧边栏里的**周目编辑器**。

- **什么都能打开**：V-Slice 的关卡 `.json`、Psych 的周目 `.json`（连 `images/menucharacters/*.json`）或 Codename 的周目 `.xml`（连 `data/weeks/characters/*.xml`），连同标题和菜单人物图片。V-Slice 的歌曲元数据会把歌名和难度填上，V-Slice 的关卡脚本（`scripts/levels/`）会把解锁设置带回来。新建周目从基础游戏的 dad、bf、gf 开始。
- **故事菜单预览**：1280×720 的故事菜单，画得和 V-Slice 一样：背景色或背景图，菜单人物在各自偏移上播待机（或 `danceLeft`/`danceRight`）动画，屏幕四分之一一个位置，还有标题图、曲目列表、分数和周目名，以及难度（工具栏切换）。**确认**会播确认动画。
- **菜单人物**：对手、男友、女友三个位置各自可以不放、用基础游戏的或用自定义的；自定义的读 `.png` + `.xml`，从图集里挑它的待机和确认动画，带缩放、偏移（预览上也能拖）、像素画、水平翻转和跳动频率。
- **歌曲**：加、删、拖动排序，设 id、显示名、自由游玩图标和颜色。
- **解锁**：先通哪个周目、初始解锁、解锁前隐藏、从故事模式隐藏、从自由游玩隐藏歌曲，还有难度列表。
- **导出**：V-Slice（`data/levels/<id>.json`，解锁和自由游玩设置在 `scripts/levels/` 里另出一个关卡脚本）、Psych Engine（`weeks/<id>.json` 和 `images/menucharacters/*.json`）或 Codename Engine（`data/weeks/weeks/<id>.xml` 和 `data/weeks/characters/*.xml`），一律带上标题、背景和自定义菜单人物图片。撤销重做覆盖每一次改动。

Windows、Linux、macOS 上：点住菜单人物可以拖，方向键微调（<kbd>Shift</kbd> 一次 10），没选中时方向键改难度，<kbd>Enter</kbd> 播确认动画，<kbd>Space</kbd> 暂停动画。

---

## Psych → V-Slice

主力转换器。下面每一块在 App 里都有自己的标签页。

## 整个模组

**模组**标签一次性转完整个 Psych Engine 模组，从文件夹或者 `.zip`、`.rar`（RAR4 和 RAR5）、`.7z` 压缩包都行。压缩包里多套了几层文件夹也能自动找到模组根目录。那种以整个引擎构建形式发布的老模组也认：`assets/preload/`、`assets/shared/`、`assets/week*/`、`assets/base_game/` 和 `assets/songs/` 会当成 `mods/` 文件夹合并。

- `data/<song>/` 谱面（+ `events.json`）→ `data/songs/<song>/`
- `characters/`、`stages/`、`weeks/` → `data/characters/`、`data/stages/`、`data/levels/`
- `images/`、`songs/`、`music/`、`sounds/`、`fonts/`、`videos/`、`shaders/` 原样拷过去
- 分开的人声（`Voices-Player.ogg`、`Voices-Opponent.ogg` 或人物自己的 `vocals_file`）通过 `playerVocals` / `opponentVocals` 接上，所以音频文件名不用改
- 人物用在哪个位置，从谱面怎么用它们推出来
- 只有一个文本文件的占位文件夹（比如模板里的 `readme.txt`）自动跳过，两个方向都是
- `pack.json` / `pack.png` → `_polymod_meta.json` / `_polymod_icon.png`
- Lua 和 HScript 脚本 → 对应的 V-Slice 类：`data/<song>/` → `scripts/songs/`（`Song`），`stages/` → `scripts/stages/`（`Stage`），`custom_notetypes/` → `scripts/notekinds/`（`NoteKind`），`custom_events/` → `scripts/events/`（`SongEvent`），`scripts/` → `scripts/modules/`（`Module`）
- 谱面自定义的 `arrowSkin`（和 `splashSkin`）→ 用同一张图在 `data/notestyles/` 里做一个 V-Slice 音符样式；音符类型自己带 `texture` 的，会多一个音符样式和一个应用它的 `NoteKind` 脚本
- 像素背景（`isPixelStage` 或 `stageUI: "pixel"`）上的歌用 `pixel` 音符样式

其它标签页也都收压缩包，从里面挑自己要的文件。

## 谱面

选一个或多个 Psych Engine 谱面（`song-easy.json`、`song.json`、`song-hard.json` 等）连同它们的 `events.json`，几首歌一起选也行。App 写出一个 V-Slice 歌曲文件夹：

```
<song-id>/
├── <song-id>-metadata.json
├── <song-id>-chart.json
├── <song-id>-metadata-erect.json   （只有某个难度需要单独变体时才有）
└── <song-id>-chart-erect.json
```

把这个文件夹丢进你 V-Slice 模组的 `data/songs/`。

### 谱面标签页

所有方向共用同一个谱面标签页（Psych ↔ V-Slice、Codename → V-Slice、Codename ↔ Psych），Android、Windows、Linux 都一样：

1. **文件**，按歌分组。Psych 的难度名可以就地改（`song-insane.json` 这种命名挺方便）。
2. **预览**：整张谱面的迷你地图，画对手和玩家轨道（用到的还有女友轨道）、长条、摄像机焦点、BPM 和拍号变化、事件，可以开关缩放。转完能在源谱面和转出来的谱面之间切换，App 会核对每个音符、长条和 BPM 变化都过来了。
3. **选项**：歌名、作者、谱师、人物、背景、音符样式，以及每个难度的滚动速度（留空就保持谱面原来的），还有一个开关决定保留还是丢掉 Psych 独有的事件。只显示目标引擎有的选项。
4. **转换**并保存，和其它标签页一样。

### 转换内容

- 老格式（1.0 以前，`mustHitSection` 相对轨道）和 `psych_v1`（绝对轨道）都认
- 所有难度合成一张谱，各自带自己的滚动速度
- 自带 BPM、拍号、事件、人物或背景的难度不会拍平：它们变成 V-Slice 的变体（erect/nightmare 谱面还是 `erect`），整模组转换时会按 V-Slice 找的后缀复制 `Voices` 文件
- BPM 变化和拍号（3、6、7 拍之类的小节）从小节长度重建进 `timeChanges`
- 谱面的 `offset` 变成元数据的伴奏偏移（反方向也一样，V-Slice → Psych）
- `mustHitSection` / `gfSection` 的摄像机焦点 → `FocusCamera`
- Psych 事件：
  - `Hey!` → `PlayAnimation`（`hey`）
  - `Play Animation` → `PlayAnimation`
  - `Camera Follow Pos` → `FocusCamera`（指定位置或回到小节焦点）
  - `Change Scroll Speed` → `ScrollSpeed`（相对速度，时长换算成步数）
- 音符类型 → 音符种类（`Alt Animation` → `alt`，`Hurt Note` → `hurt`，`Hey!` → `hey`，`No Animation` → `noanim`，自定义类型 → slug 化 id），`altAnim` 小节 → 对手音符上的 `alt`
- 叠在一起的重复音符去掉
- 背景 id 映射到 V-Slice 的（`stage` → `mainStage`，`spooky` → `spookyMansion` 等）；`school`/`schoolEvil` 或者像素箭头皮肤 → `pixel` 音符样式

其它 Psych 事件会拿到一个 V-Slice 风格的 id（`Flash Camera` → `FlashCamera`，绝不和 `FocusCamera` 这类内置 id 撞），谱面和脚本里都按这个 id 应用，而转换后的脚本在 `onEvent` 里收到的还是原来的名字。`custom_events/*.lua`/`.hx` 变成 `scripts/events/<Name>Event.hxc`：一个 `SongEvent`（谱面编辑器里有 Value 1/Value 2 两个字段），装着转换后的代码，在 `handleEvent` 里跑。V-Slice 没有对应事件的 Psych 内置事件（`Add Camera Zoom`、`Screen Shake`、`Set Property`、`Play Sound`、`Set GF Speed`、`Alt Idle Animation`）也会在那边生成一个 `SongEvent` 脚本。还没法转的东西会逐条列在 App 的报告里。

## 保存位置

先选一次你的 V-Slice 模组文件夹（会记住）。转出来的文件直接进该去的地方：

```
<mod>/data/songs/<song-id>/<song-id>-metadata.json
<mod>/data/songs/<song-id>/<song-id>-chart.json
<mod>/data/characters/<id>.json
<mod>/data/stages/<id>.json
<mod>/data/levels/<id>.json
<mod>/scripts/songs/<Song>Song.hxc
<mod>/scripts/stages/<Stage>Stage.hxc
<mod>/scripts/notekinds/<Type>NoteKind.hxc
<mod>/scripts/events/<Name>Event.hxc
<mod>/scripts/modules/<Class>.hxc
```

## 人物

选 Psych 人物的 `.json`，连同它们的图集 `.xml`。有 XML 的话转换是像素级准的：V-Slice 把人物锚在脚底（待机帧底边中心）并缩放动画偏移，Psych 锚在第一张图集帧的左上角，所以偏移和摄像机会按真实帧尺寸重新换算。

- 动画、前缀、帧索引、帧率和循环
- Sparrow 和 multi-atlas（`multisparrow`）图集
- 缩放、翻转、像素（不抗锯齿）、血条图标、唱歌时长、跳动速度（`danceLeft`/`danceRight`）
- 摄像机位置併进 `cameraOffsets`
- 位置（玩家 / 对手 / 女友）决定偏移怎么换算
- `<anim>-loop` 动画（Psych 在 `<anim>` 播完后接着播）变成 V-Slice 的 `<anim>-hold`，效果一样（反方向 V-Slice → Psych 也认）
- **Animate 图集人物**（`images/<image>/Animation.json` + `spritemap1.json`）：Psych 的符号名（按前缀匹配）变成 V-Slice 要播的精确符号名（`animType: "symbol"`），偏移里算进了每个符号的边界（从 `Animation.json` 和 spritemap 读），所以图集落的位置就是 Psych 画的位置。Psych → Codename、V-Slice → Psych（帧标签动画变成主符号的帧索引，Psych 只能播这个）和 Codename → Psych 都一样
- **死亡人物**：玩家的死亡人物（`<id>-dead`，或者 Psych 默认的 `bf-dead`）会合并进本体，因为 V-Slice 把 `firstDeath`、`deathLoop`、`deathConfirm` 放在玩家人物里（从死亡图集来，形式是 `multisparrow` 或 `multianimateatlas`），放在 Psych 死亡画面画它们的位置
- V-Slice 人物的全局 `offsets` 按 V-Slice 的方式应用（跟着精灵缩放），两个方向都是
- 核对过：基础游戏里有图集的 44 个 V-Slice 人物转到 Psych 再转回来，偏移一模一样（死亡动画也在），Psych 0.6/0.7 的每个基础人物转到 V-Slice 再转回来也精确

不带的：血条颜色和每个人物的人声后缀（V-Slice 没有对应物）。

## 背景

对着 V-Slice 的 `Stage.hx` / `StageData.hx` 写的，所以 JSON 加载和绘制都跟 Psych 画的一样：

- 人物位置 → V-Slice 的脚底锚点、摄像机偏移（含 Psych 内置的 ±100/150 摄像机位移）和缩放
- `directory` 永远是真实存在的 V-Slice 资源库（`shared`、`week1` 等）：V-Slice 碰到不认识的会以 "Missing library" 停下，而模组图片本来就从 `images/` 加载
- Psych 1.0 背景编辑器的 `objects` → 物件（精灵、动画精灵、纯色方块），保留图层顺序、视差、缩放、透明度、角度、颜色、翻转和像素模式；只在低画质出现和隐藏的物件跳过
- 物件列表里没有的人物放到最上层，而不是消失；`hide_girlfriend` 变成隐形的女友
- 缩放过的动画精灵：V-Slice 会把动画偏移乘以缩放并保留判定框偏移，Psych 是直接替换。有图集 `.xml` 时偏移会重算，让每个动画都落在 Psych 的精确像素上（起始动画也一样）
- 没有动画的动画精灵显示图集第一帧，和 Psych 一样
- 会读背景 Lua：它创建的精灵变成物件，节拍跳动（`if curBeat % N == 0 then playAnim(...)`、danceLeft/danceRight 交替、`onStepHit` 里的跳动）变成 `danceEvery`，需要时把跳动动画改名成 `idle`。背景物件上的 Lua `addOffset`、`setBlendMode` 和按帧索引 `addAnimation`（Packer 图集）会併进去，那几行从转换后的脚本里去掉
- 女友的视差因子（0.95）保留

只在代码里搭出来的背景没有物件列表，它们的人物布局照样转，基础 Psych 背景指向对应的 V-Slice 背景（`stage` → `mainStage` 等），报告里列出要用物件重建的图片。

## 周目

- 歌曲（id 用 Psych 的歌曲路径格式）、故事名、背景（`menubackgrounds/menu_<name>` 或默认黄色）和标题图（`storymenu/<week>`）
- `images/menucharacters/*.json` 的菜单人物 → 关卡物件，放在 Psych 画的位置；基础的 `dad`/`bf`/`gf` 映射到 V-Slice 自己的物件
- 隐藏的故事周目 → `visible: false`
- 锁链和自由游玩隐藏 → 一个关卡脚本（`scripts/levels/<Week>Level.hxc`）：打完 `weekBefore` 才解锁，设了 `hiddenUntilUnlocked` 就锁着的时候一直藏着，锁着时歌曲不进自由游玩（和 Psych 一样），用 `hideFreeplay` 就永久藏起来

不带的：每首歌的自由游玩图标和颜色（V-Slice 显示对手的像素图标；报告里会列）。

## 脚本

**脚本**标签（模组标签也一样）把 Psych Engine 脚本转成 V-Slice 的脚本类（`.hxc`）。

- **Lua** 完整解析（Lua 5.1 / LuaJIT 语法）并转译成 HScript：局部/全局变量、闭包、可变参数、表（数组和映射）、`pairs`/`ipairs`、数字循环、`goto continue`、按 Lua 真假值处理的 `and`/`or`，还有 `string`、`table`、`math`、`os`、`bit` 库，包括 Lua 模式匹配（`find`、`match`、`gmatch`、`gsub`、`format`）
- **Psych HScript**（`.hx`）包进它的类里：import 会映射（`states.PlayState` → `funkin.play.PlayState` 等），`game.boyfriend`、`camFollow`、`defaultCamZoom`、`Conductor.songPosition`、`curBeat`、`ClientPrefs` 等改写成 V-Slice 的写法
- 每个脚本都变成所在位置的标准 V-Slice 类，按它的 id 命名（不加模组前缀、不自己造基类），对着 FunkinCrew/Funkin 的 `Song`、`Stage`、`NoteKind`、`SongEvent`、`Module` 写：

| Psych | V-Slice | 类 |
| --- | --- | --- |
| `data/<song>/*.lua`、`.hx` | `scripts/songs/<Song>Song.hxc` | `extends Song`，`super('<song>')` |
| `stages/<stage>.lua`、`.hx` | `scripts/stages/<Stage>Stage.hxc` | `extends Stage`，`super('<stage>')` |
| `custom_notetypes/<type>` | `scripts/notekinds/<Type>NoteKind.hxc` | `extends NoteKind`，`super('<kind>', '<type>'[, '<note style>'])` |
| `custom_events/<event>` | `scripts/events/<Event>Event.hxc` | `extends SongEvent`，代码在 `handleEvent` 里跑 |
| `scripts/*` | `scripts/modules/<name>.hxc` | `extends Module` |

- 只有类装不下的时候，脚本才退回成一个限域的 `Module`（`<Name>Module`，报告里会列）：同一首歌或背景的第二个脚本（V-Slice 一个 id 只允许一个 `Song`/`Stage` 类）、用了 `NoteKind` 根本收不到的回调的音符类型（`NoteKind` 只有 update、音符和 create/destroy 事件），或者基础游戏已经有脚本的 id（`bopeebo`、`phillyTrain` 等）。类名和基础游戏撞的，前面加模组名
- 成员名会覆盖类里继承的东西时改名（背景里的 Lua `update` 或 `color` 变成 `updateScript`/`colorScript`，这样 `FlxSpriteGroup.update` 还能用），`Song`/`Stage`/`SongEvent`/`NoteKind` 在 `onDestroy` 里清理（它们没有 `onStateChangeBegin`），音符类型的贴图变成它自己 `NoteKind` 的音符样式，而不是第二个类
- Psych 回调接到 V-Slice 事件上：`onCreate`、`onCreatePost`、`onUpdate`、`onBeatHit`、`onStepHit`、`onSectionHit`（按真实小节，所以拍号有效）、`goodNoteHit`、`opponentNoteHit`、`noteMiss`、`noteMissPress`、`onGhostTap`、`onKeyPress`/`onKeyRelease`（和它们的 `Pre` 版）、`onUpdateScore`、`onRecalculateRating`、`onEvent`、`onEventPushed`、`onMoveCamera`、`onSpawnNote`、`onCountdownTick`、`onStartCountdown`、`onEndSong`、`onSongStart`、`onPause`、`onResume`、`onGameOver`、`onGameOverStart`、`onGameOverConfirm`、`onDestroy`、`onTimerCompleted`、`onTweenCompleted`、`onSoundFinished`；返回 `Function_Stop` 会像 Psych 那样取消倒计时和歌曲结束
- 用 `runHaxeCode` 写的代码会就地转换（里面的函数、变量和传进去的变量），`runHaxeFunction` 调用那些函数；`_G.name`、`_G["name"]` 和 `_G[key]` 都能用，Psych 0.7 的 `FlxColor("#RRGGBB")` 也能用
- 输出是纯 V-Slice HScript：没有 Lua 残留（没有 `Function_*` 常量、没有 `psych("…")` 分发、没有 `g_`/`l_` 前缀）。脚本名尽量保留自己的名字（`onUpdate` 和类的方法撞时才变成 `onUpdateScript`），Psych 的调用变成真函数，比如 `makeSprite(...)`。不加运行时类或辅助类：每个类只为它用到的 Psych 函数带上对应的 V-Slice/Flixel 原生代码（Lua 精灵和文本、`getProperty`/`setProperty` 路径、缓动、计时器、声音、摄像机、血量、分数、扫弦音符、着色器、存档、共享变量）。同一个模组的脚本之间还是能拿到彼此的 Lua 精灵
- `precacheImage`、`precacheSound`、`precacheMusic` 和 `addCharacterToList` 通过 V-Slice 的 `FunkinMemory` 缓存（`cacheTexture`/`cacheSound`，人物图集从它的数据里读），所以资源在出现之前就准备好了
- 生成的代码碰到的每个类、字段和事件都对着 FunkinCrew/Funkin 源码核过

Animate 图集精灵（`makeFlxAnimateSprite`、`loadAnimateAtlas`、`addAnimationBySymbol(Indices)`）、所有着色器 getter/setter（int/bool 数组、`setShaderSampler2D`）、摄像机滚动和跟随点辅助、手柄（按键和摇杆）、`getPixelColor`、`loadMultipleFrames`、`createInstance`/`addInstance`、`callMethodFromClass`、`addToGroup`/`removeFromGroup`、`directoryFileList`、`loadSong` 和 `startVideo` 也都能转，V-Slice 和 Codename 都支持。

报告会逐行列出脚本里用到的、V-Slice 没有对应物的东西（自定义子状态、`addLuaScript`/`addHScript`、`saveFile`/`deleteFile`、对话、元表、`unspawnNotes` 那类取巧写法、`onNextDialogue` 或 `eventEarlyTrigger` 这种在 V-Slice 里根本不会跑的回调），那些部分可以手工收尾。

---

## V-Slice → Psych

| 输入 | 输出 |
| --- | --- |
| `<song>-metadata.json` + `<song>-chart.json` | `data/<song>/<song>[-difficulty].json`（psych_v1）：从小节长度重建小节（BPM 变化精确），摄像机焦点 → `mustHitSection`/`gfSection`，音符种类 → 音符类型，`PlayAnimation`/`ScrollSpeed`/`ZoomCamera`/`FocusCamera` → Psych 事件 |
| 变体（`-erect` 等） | 各自成一首 Psych 歌（`<song>-erect`），带自己的音频，放进只出现在自由游玩的周目 |
| `data/characters/<id>.json`（+ XML） | `characters/<id>.json`，位置和摄像机用图集精确换算；图片路径里的资源库前缀（`shared:`）去掉，多个 Sparrow 图集合并进 `image`，纹理图集人物保留主图集的动画（其它图集里的动画列在报告里） |
| `data/stages/<id>.json`（+ 图片的 `.xml`） | `stages/<id>.json`，带背景编辑器物件、图层顺序和人物标记，需要时另出 `stages/<id>.lua`：带 `danceEvery` 的物件跟着节拍跳（idle 或 danceLeft/danceRight），缩放过的动画物件按 V-Slice 的偏移算法并停在起始动画，没有起始动画的不动，Packer 动画、混合模式、人物视差/透明度/角度；隐形女友变成 `hide_girlfriend` |
| `data/levels/<id>.json` | `weeks/<id>.json`，自定义物件另出 `images/menucharacters/*.json` |
| `.hxc` 脚本 | Psych HScript：模块 → `scripts/`，歌曲 → `data/<song>/`，背景 → `stages/`，人物 → `characters/`，歌曲事件 → `custom_events/`（+ `.txt`），音符类型 → `custom_notetypes/`。属性（`get`/`set`）变成 getter/setter 调用，`FunkinSprite.create`/`createSparrow`、`Save.instance.hasBeatenSong`/`hasBeatenLevel`、`PlayStatePlaylist.isStoryMode`、`GameOverSubState`、`Constants` 和 `Preferences` 的值都做了映射，触感反馈去掉，`FunkinMemory` 缓存变成 Psych 自己的缓存（`Paths.image`/`Paths.sound`），`Paths` 调用里 V-Slice 的资源库名（`"week4"`、`"shared"`）去掉。背景脚本跑的顺序和 V-Slice 一样：`buildStage()` 在 `onCreate` 开头跑（Psych 已经按背景 JSON 把物件建好了），`addCharacter()` 对 boyfriend、dad、gf 各调一次，`fetchAssetPaths()` 预缓存它的资源，`onCountdownEnd` 在歌曲开始时跑，`getNamedProp`/`add`/`addProp`/`refresh()` 都能用，`zIndex` 靠几个小辅助函数按背景 JSON 的图层重排背景物件、人物和新物件。`funkin.util.Sequence` 变成 FlxTimer，`AdjustColorShader` 变成 Psych 的 `adjustColor` 运行时着色器，颜色算法一样。`VideoCutscene.play(Paths.videos(...))` 变成 `PlayState.instance.startVideo(...)`。输出里只留 Psych 1.0 真有的类：V-Slice 独有的类型变成 `Dynamic`，需要 V-Slice 独有类的语句（`StageProp`、`RuntimeRainShader`、`FlxTweenUtil`、别的脚本类等）以及用到它们产出的值的代码会被拿掉，逐行列在报告里。基础游戏的 114 个脚本都能转成 Psych 1.0 解析器吃得下的 HScript，没有一个类缺 |
| 带 `firstDeath`/`deathLoop`/`deathConfirm` 的玩家人物 | `characters/<id>-dead.json`（死亡动画）加 `characters/<id>.lua`，让人物当玩家时 Psych 用它 |
| 模组 | `pack.json`/`pack.png`，`Voices-<char>.ogg` → `Voices-Player.ogg`/`Voices-Opponent.ogg`，资源拷过去 |

往返（Psych → V-Slice → Psych）能保住音符、小节、事件、背景位置和屏幕上人物的摆位。

---

## Codename → V-Slice

| 输入 | 输出 |
| --- | --- |
| `songs/<song>/charts/<diff>.json` + `meta.json` + `events.json` | `<song>-metadata.json` + `<song>-chart.json`：玩家/对手扫弦线 → 0-3/4-7 轨道，音符类型 → 音符种类（`Alt Anim Note` → `alt` 并生成 `NoteKind`），`BPM Change`/`Time Signature Change` → 时间变化，`Camera Movement`/`Camera Position` → `FocusCamera`，`Camera Zoom` → `ZoomCamera`，`Scroll Speed Change` → `ScrollSpeed`，`Camera Modulo Change` → `SetCameraBop`，`Play Animation` → `PlayAnimation` |
| 变体（`charts/<variant>/`、`meta-<variant>.json`） | V-Slice 变体，带自己的 `Inst-<variant>` 音频 |
| 其它事件（`Camera Flash`、`Add Camera Zoom`、`Camera Bop`、`HScript Call`、自定义 `data/events/*.json`） | 带命名值留在谱面里，另在 `scripts/events/` 生成 `SongEvent`（内置的那些有可用实现） |
| `data/characters/<id>.xml`（+ 图集 XML） | `data/characters/<id>.json`：偏移乘以缩放（Codename 会缩放，V-Slice 不会），用在另一侧的人物按 Codename 运行时的方式镜像，`<id>-dead`/`gameOverChar` 的动画为死亡画面併进来 |
| `data/stages/<id>.xml` | `data/stages/<id>.json`：精灵、框、`high-memory` 内容、节拍精灵（`danceEvery`）、图层顺序和人物位置；缩放但没调 `updateHitbox` 的精灵移到 Codename 画它们的位置 |
| `data/weeks/weeks/*.xml` + `data/weeks/characters/*.xml` | 带菜单物件的 `data/levels/*.json` |
| `.hx` 脚本 | 同样的标准类：歌曲脚本 → `Song`，背景脚本 → `Stage`，音符类型脚本 → `NoteKind`（只用了音符、update 和 create/destroy 回调时），全局脚本 → `Module`；背景扩展、人物和事件变成按 Codename 运行方式限域的 `Module`，id 已经有类的脚本也一样。`create`/`update`/`beatHit`/`onPlayerHit`/`onEvent` 等变成模块事件，配 Codename 风格的事件对象；`boyfriend`、`camHUD`、`curBeat`、`Conductor.crochet`、按名字拿背景精灵等都映射到 V-Slice API |
| 模组 | 从 `data/config/modpack.ini` 生成 `_polymod_meta.json`，`song/Inst.ogg`/`Voices*.ogg` 移到 `songs/<song>/`，资源拷过去 |

---

## Codename → Psych

| 输入 | 输出 |
| --- | --- |
| `songs/<song>/charts/<diff>.json` + `meta.json` + `events.json` | `data/<song>/<song>-<diff>.json`（Psych 1.0 格式）：扫弦线 → 小节，由 `Camera Movement` 推出 `mustHitSection`/`gfSection`，女友扫弦线 → `GF Sing` 音符或隐藏的音符类型，音符类型改名（`Alt Anim Note` → `Alt Animation` 等），`BPM Change`/`Time Signature Change` → 小节 BPM 和拍数，`Add Camera Zoom`、`Play Animation`、`Scroll Speed Change` → Psych 事件 |
| 其它事件（`Camera Position`、`Camera Zoom`、`Camera Bop`、`Camera Flash`、`Camera Modulo Change`、`HScript Call`、自定义的） | `custom_events/<name>.hx` + `.txt`，内置的那些有可用的 Psych 实现，参数打包进两个值 |
| 变体 | 各自成 Psych 歌（`<song>-<variant>`），音频一起 |
| `data/characters/<id>.xml` | `characters/<id>.json`：偏移按缩放除回去，`holdTime` → `sing_duration`，按侧别设摄像机偏移；人物脚本一并转换 |
| `data/stages/<id>.xml` | `stages/<id>.json`，带物件、图层和人物位置，节拍跳动、混合模式、视差/缩放微调、`high-memory` 内容和背景扩展另出 `stages/<id>.hx` |
| `data/weeks/weeks/*.xml` + `data/weeks/characters/*.xml` | `weeks/*.json`、`weeks/weekList.txt`、`images/menucharacters/*.json`；不属于任何周目的歌放进一个隐藏故事的自由游玩周目 |
| `.hx` 脚本 | Psych HScript：`create`/`postCreate`/`update`/`beatHit`/`onPlayerHit`/`onDadHit`/`onEvent`/`onCountdown` 等变成 Psych 回调，配 Codename 风格的事件对象（`event.cancel()` 照样能用），`strumLines`、`stage.getSprite`、`FunkinSprite`、`CustomShader`、`Options`、`Conductor` 等都映射到 Psych；需要时另出一个小的 `scripts/CodenameCompat.hx` 重建 Codename 独有的摄像机设置 |
| 模组 | 从 `data/config/modpack.ini` 生成 `pack.json`，`song/Inst.ogg`/`Voices*.ogg` 移到 `songs/<song>/`，资源拷过去 |

---

## Psych → Codename

| 输入 | 输出 |
| --- | --- |
| `data/<song>/<song>-<diff>.json` + `events.json` | `songs/<song>/charts/<diff>.json` + `meta.json`：对手、玩家和（隐藏的）女友扫弦线，按人物分的人声（`Voices-Player`/`Voices-Opponent`），小节焦点 → `Camera Movement`（强制摄像机位置下保留），`Camera Follow Pos` → `Camera Position`，`Add Camera Zoom`、`Play Animation`、`Change Scroll Speed`、`Alt Idle Animation`、BPM 和拍号变化 |
| `Hey!`、`Set GF Speed`、`Screen Shake`、`Change Character`、`Set Property`、`Play Sound`、`Alt Idle Animation` | `data/events/<name>.hx` + `.json`，在 Codename 里重新实现 Psych 的内置事件 |
| 自定义 `arrowSkin`、自己带 `texture` 的音符类型 | 一个换音符和扫弦精灵的歌曲脚本，音符类型的贴图拷到 `images/game/notes/<type>`，也就是 Codename 找的位置 |
| `Hurt Note`、`Hey!`、`GF Sing` 音符类型 | `data/notes/<name>.hx`（CPU 会避开、命中掉血、跳过不算 miss；hey/欢呼动画；女友唱） |
| `characters/<id>.json` | `data/characters/<id>.xml`：偏移除以缩放，`sing_duration` → `holdTime`，按侧别设摄像机偏移，`images/characters/` 外面的图集拷进去 |
| `stages/<id>.json`（+ `stages/<id>.lua`） | `data/stages/<id>.xml`，带精灵、框、画质筛选（`low-memory`/`high-memory`）、人物位置和像素扩展；背景 Lua 里创建的精灵移进 XML，剩下的 Lua 变成 `data/stages/<id>.hx` |
| `weeks/*.json` + `images/menucharacters/*.json` | `data/weeks/weeks/*.xml`（带显示名的歌曲文件夹）、`data/weeks/weeks.txt`、`data/weeks/characters/*.xml`、`data/config/freeplaySonglist.txt` |
| Lua 和 HScript | Codename HScript：Psych 回调从 Codename 的回调桥接（`goodNoteHitPre` → `onPlayerHit`，`return Function_Stop` 照样取消），Lua 精灵变成共享的 `public var`，`getProperty`/`setProperty`/缓动/计时器/摄像机/文本/着色器/声音/存档都配了小号的 Psych 辅助函数，`runHaxeCode` 块就地转换，`mustHitSection` 读谱面的摄像机事件。歌曲脚本放 `songs/<song>/scripts/`，全局的放 `songs/`，自定义事件放 `data/events/`，音符类型放 `data/notes/` |
| 模组 | 从 `pack.json` 生成 `data/config/modpack.ini`，音频移到 `songs/<song>/song/`，`pack.png` → `icon.png`，资源拷过去 |

---

## NightmareVision

[NightmareVision](https://github.com/NMVTeam/NightmareVision) 是只跑 HScript 的 Psych Engine 分支。每个标签页都能 **NightmareVision → Psych / V-Slice / Codename** 和 **Psych / V-Slice / Codename → NightmareVision**。NightmareVision 的内容先当成 Psych 内容读（写也从 Psych 内容写），所以 V-Slice 和 Codename 方向用的还是 Psych 那套转换器。

| Psych | NightmareVision |
| --- | --- |
| `data/<song>/<song>-<diff>.json`（`psych_v1` 或老格式） | `songs/<song>/charts/<diff>.json`，带 `format: "nmv2"`、`keys` 和 `lanes`，另有 `songs/<song>/meta.json`；`events.json` 移到 `charts/`。nmv2 的轨道是绝对的（0 号轨道是玩家），所以 `psych_v1` 的音符数字不用改；转到 Psych 时多出来的轨道或按键上的音符丢掉 |
| `songs/<song>/Inst.ogg`、`Voices-Player`/`Voices-Opponent` | `songs/<song>/audio/Inst.ogg`、`Voices-player`/`Voices-opp` |
| `characters/<id>.json`（+ `.lua`/`.hx`） | `data/characters/<id>.json`（+ `.hx`）；`healthbar_colour` 和 NightmareVision 独有的字段都处理 |
| `stages/<id>.json`（+ `.lua`/`.hx`） | `data/stages/<id>/data.json`，带 `stageObjects`、`zIndex` 分层和 `dad`/`gf`/`bfZIndex`，以及 `data/stages/<id>/script.hx` |
| `weeks/*.json`、`images/menucharacters`、`images/storymenu`、`images/menubackgrounds` | `data/weeks/*.json`、`images/menus/story/characters`、`weeks`、`bgs` |
| `scripts/`、`custom_events/`、`custom_notetypes/`、`data/<song>/` 里的脚本 | `data/scripts/`、`data/events/`、`data/notetypes/`、`songs/<song>/` |
| `pack.json` + `pack.png` | `meta.json` + `images/pack.png` |
| Lua | NightmareVision HScript：`onCreate` 当成 `onLoad` 跑，`goodNoteHit`/`noteMiss`/`onSpawnNote`/`onEventPushed` 从 NightmareVision 的音符和事件对象桥接，脚本用到的每个 Psych 函数（精灵、文本、缓动、计时器、声音、摄像机、属性、分组、存档、着色器、按键等）都以小辅助函数的形式写进脚本。Lua 精灵住在 `game.variables` 里，所以 `getProperty` 从任何脚本都能找到；`setPropertyFromGroup('unspawnNotes', …)` 在音符生成时应用 |
| Psych HScript | `onCreate` → `onLoad`，`ClientPrefs.data.*` → `ClientPrefs.*`，`Conductor.crochet` → `crotchet`，Psych 的 import 映射到 `funkin.*`，NightmareVision 本身就有那个类的就丢掉 |

---

## FPS Plus

[FPS Plus](https://github.com/ThatRozebudDude/FPS-Plus-Public) 把人物、背景和脚本都做成 Polymod 类。每个标签页都能 **Psych / V-Slice / Codename / NightmareVision → FPS Plus**；V-Slice、Codename 和 NightmareVision 的内容先过 Psych 转换器。

| Psych | FPS Plus |
| --- | --- |
| `data/<song>/<song>-<diff>.json` | `data/songs/<song>/chart-<diff>.json`（`fpsplus_1`：音符带时间、方向、按步算的长度、tag 和侧别，还有 BPM 变化）、`events.json` 和 `meta.json`。小节焦点变成 `camFocusBf`/`camFocusDad`/`camFocusGf`，`Hey!`、`Play Animation`、`Set GF Speed`、`Add Camera Zoom`、`Change Character`、`Change Scroll Speed`、`Play Sound`、`Screen Shake` 和 `Camera Follow Pos` 变成 FPS Plus 事件，`Alt Animation`/`Hey!`/`No Animation` 音符用 FPS Plus 的音符类型 |
| 自定义事件和音符类型 | `data/events/PsychEvents.hxc`（每个事件调转换后脚本里的 `onEvent`）和 `data/notetypes/PsychNotes.hxc`（`Hurt Note` 掉血） |
| `characters/<id>.json` | `data/characters/<Id>.hxc`，`extends CharacterInfoBase`：精灵、图标、血量颜色、摄像机偏移、带动画偏移的动画（偏移除以缩放，FPS Plus 自己会乘）、缩放和长按时长；基础游戏人物用 FPS Plus 自己的类 |
| `stages/<id>.json`（+ `.lua`/`.hx`） | `data/stages/<Id>.hxc`，`extends BaseStage`：缩放、像素 UI、摄像机偏移、Psych 1.0 物件放在背景、中层或前景，人物放在 Psych 摆的位置，背景脚本併进类里；基础游戏背景用 FPS Plus 自己的 |
| `weeks/*.json`、`images/menucharacters`、`images/storymenu` | `data/weeks/*.json`、`images/menu/story/characters`（待机和确认动画改名）和 `images/menu/story/weeks`；歌曲用 `merge/data/freeplay/songList-bf.json` 加进自由游玩 |
| `images/icons/icon-<name>.png` | `images/ui/healthIcons/<name>.png`，带 FPS Plus 用的 3 帧（正常、掉血、回血） |
| Lua 和 HScript | `data/scripts` 里的 `Script` 类，在歌曲的 `scripts.json`（歌曲和人物脚本）或 `append/data/scripts/globalScripts.txt` 里登记。Psych 回调从类的 `create`、`update`、`beat`、`step`、`noteHit`、`noteMiss` 等跑，脚本用到的每个 Psych 函数都用 `ReflectUtil` 写进类里（不是 `Reflect`） |
| `pack.json` + `pack.png` | `meta.json`（带 `uid` 和 `api_version`）+ `icon.png` |

---

## 多人编辑

每个编辑器（谱面、人物、背景、周目）都有**多人编辑**卡片：两个人以上通过[在线服务器](#在线服务器)同时改同一个东西。

- **房间**：开一个房间，把 6 位房间码发出去；或者勾上**公开列出我的房间**，别人就能在**在线 → 房间**（Android）或**在线**页（Windows、Linux、macOS）按编辑器筛选找到。从列表加入会直接打开对应的编辑器。
- **实时改动**：谱面编辑器里音符和事件一个个同步，其它东西整张谱面发；其它编辑器在你操作时每秒几次发整份文档（一个人物、一个背景或一个周目）。撤销重做是本地的，跟别的改动一样同步。后来加入的人会收到当前状态。
- **共享文件**：你加载的图片、图集和歌曲音频会上传到房间，房间里所有人都会下载，所以大家的预览一致。
- **在线状态**：谁在房间里、每个人在歌里的位置（谱面编辑器），还有一个小聊天。

## 试玩

Android 上的**测试**和 Windows、Linux、macOS 侧边栏里的**试玩**会像游戏那样放歌，用来检查转出来的模组和你自己的项目。

- **能打开什么**：模组文件夹或压缩包（V-Slice、Psych 或 Codename；Psych 和 Codename 的模组会先过转换器转成 V-Slice，等于把转换器自己也测了）、任意转换的结果（输出下面的**试玩结果**）、转出来的模组（**试玩转换后的模组**），或者谱面编辑器里的谱面（**试玩这张谱子**）。
- **玩法**：音符按谱面速度滚动（下滚也行），有 Sick/Good/Bad/Shit 判定窗口、长条、miss、连击、分数、准确率、评级（PFC、GFC、FC 等）和血量。可以从歌里任意位置开始，或者打开**自动游玩**看谱面自己跑。手机上屏幕分成四条轨道点；电脑上用方向键或 D F J K 打音符。
- **背景和人物**：歌的背景带它的物件，玩家、对手和女友站在上面唱（和 miss）并跟着节拍跳，摄像机跟着 FocusCamera 事件走。
- **报告**：谱面里发现的问题（叠在一起的音符、长条撞进音符、缺 BPM、空难度、需要脚本的事件）、音频、人物（缺数据、图集或动画，前缀没有帧）、背景（物件没图或没帧）和转换本身，玩完还有时机提示。

## 在线服务器

`server/` 文件夹里是 V-Converter 的在线服务器，用 JavaScript 写（Node.js 22.13+、Express、`ws` 和内置 SQLite）。在设置（齿轮）里填了地址之后 App 就会连它：**服务器地址**和**发送错误报告**（默认关）。

| 功能 | App 里的位置 | 服务器 API |
| --- | --- | --- |
| 模组画廊 | Android 的**在线**块（模组 / 分享两个标签），Windows、Linux、macOS 侧边栏的**在线**：搜索、按引擎筛选、排序、下载，上传带标题、简介、作者、引擎和标签的 `.zip`。上传会返回一个删除密钥，那是以后删模组的唯一凭据 | `GET /api/mods`、`GET /api/mods/:id`、`GET /api/mods/:id/download`、`POST /api/mods`（multipart，字段 `file`）、`DELETE /api/mods/:id`（`X-Delete-Token`） |
| 更新和公告 | **新闻**标签：有新版 V-Converter 时提示（读最新 GitHub release，带对应平台的下载），并显示服务器公告 | `GET /api/version?platform=&current=`、`GET /api/announcements?platform=` |
| 错误报告 | 打开后，失败的转换和崩溃会上报消息、堆栈、App 版本和平台 | `POST /api/reports` |
| 多人编辑 | 每个编辑器里的**多人编辑**卡片（见[多人编辑](#多人编辑)）：私有或公开房间、实时改动、共享图片/图集/音频、在线状态和聊天 | `GET /api/rooms?kind=`（公开房间）、`POST /api/rooms`、`GET /api/rooms/:code`、`PUT`/`GET /api/rooms/:code/assets/<path>`、WebSocket `/ws?room=CODE&name=NAME` |

跑起来：

```
cd server
npm ci
ADMIN_TOKEN=change-me npm start
```

或者用 Docker（`docker build -t v-converter-server server` 和 `docker run -p 3000:3000 -v vconverter-data:/data -e ADMIN_TOKEN=change-me v-converter-server`）。上传的模组和 SQLite 数据库在 `DATA_DIR` 里。把它放到 HTTPS 后面（反向代理，或者 Render、Railway、Fly 这类平台），并在那边设 `TRUST_PROXY=true`，这样限流看到的是真实客户端地址。

| 变量 | 默认值 | 含义 |
| --- | --- | --- |
| `PORT` / `HOST` | `3000` / `0.0.0.0` | 监听地址 |
| `DATA_DIR` | `./data` | 上传的模组和数据库 |
| `PUBLIC_URL` | （请求的 host） | 下载链接用的基础 URL |
| `ADMIN_TOKEN` | （空） | 开启审核、公告和读取报告（`Authorization: Bearer <token>`） |
| `TRUST_PROXY` | `false` | 信任反向代理来的 `X-Forwarded-*` |
| `MAX_UPLOAD_MB` | `100` | 模组上传大小上限 |
| `UPLOADS_PER_HOUR` / `REPORTS_PER_HOUR` / `ROOMS_PER_HOUR` | `10` / `60` / `30` | 按地址的限流 |
| `GITHUB_REPO` / `GITHUB_TOKEN` | `BrenninhoTools/V-Converter` / （空） | 从哪儿读最新发布 |
| `LATEST_VERSION` | （空） | 连不上 GitHub 时报的版本 |
| `ROOM_IDLE_MINUTES` / `MAX_PEERS_PER_ROOM` / `MAX_MESSAGE_MB` | `120` / `16` / `8` | 实时编辑房间 |
| `ROOM_ASSETS_PER_HOUR` / `MAX_ROOM_ASSET_MB` / `MAX_ROOM_ASSETS_MB` | `600` / `40` / `200` | 房间里共享的文件 |

管理接口：`POST /api/announcements`（`{"title", "body", "level": "info"|"warning"|"update", "url", "platforms": ["android"], "expiresAt"}`）、`DELETE /api/announcements/:id`、`PATCH /api/mods/:id`（`{"hidden": true}`）、`DELETE /api/mods/:id`、`GET /api/reports` 和 `DELETE /api/reports/:id`。`npm test` 跑服务器测试，[Server workflow](https://github.com/BrenninhoTools/V-Converter/actions/workflows/server.yml) 在 `server/` 有改动时自动跑。

## 汉化说明

汉化按一条规矩做：**只动引号里的字**。脚本把 Kotlin 源码里每个双引号字符串取出来，查表命中就换成中文，写回去时重新转义 `\n`、`\"`、`$`。标识符、包名、JSON 键、事件 id、音符类型 id、Psych 默认动画名一律不碰 —— 这些会写进转换后的文件，翻了转换器自己的映射就断。

翻完跑三遍检查：① 剥掉字符串后代码区不许出现中文（防止把变量名一起换了）；② 每个文件的未转义双引号数要和原版一致；③ 含中文的行引号数必须是偶数（否则字符串跨行，Kotlin 编译不过）。

界面上刻意保留英文的：引擎名（Psych Engine / V-Slice / Codename Engine）、难度名（Easy / Normal / Hard）、事件 id、音符类型 id、动画名（`singLEFT` 之类）、Kotlin 与 Compose 的库异常。

字体换成了带中文字形的 `assets/fonts/funkin.ttf`（上游那份只有拉丁字形，中文会画不出来）。桌面图标名改成了「V-转换器」，英文系统下仍然是 V-Converter（`app/src/main/res/values-en`）。

### 自己编译

和上游一样，见[构建](#构建)。Android：

```
./gradlew assembleDebug assembleRelease
```

不想本地配环境就用 Actions：**Actions → Build → Run workflow**，跑完在那次运行页面最下面下 `V Converter-Android` 这个 artifact。

## 构建

Android（debug 和 release APK）：

```
./gradlew assembleDebug assembleRelease
```

Release APK 用 `keystore/` 里的密钥库签名（`v-converter.jks`，配置在 `keystore/keystore.properties`），所以每次 release 构建签名都一样，能直接覆盖安装上一版。加密钥库之前的构建用的是 debug 密钥：从那时候装过来的，要先卸载一次再装新的 release APK。密钥库在这个仓库里是公开的，谁都能拿它签 APK；只装这个仓库 Actions 跑出来的 APK。

Windows（安装包和便携版，在 Windows 上用 JDK 17 跑）：

```
./gradlew :desktop:packageMsi :desktop:packageExe :desktop:createDistributable
```

Linux（`.deb` 包和便携版，在 Linux 上用 JDK 17 跑）：

```
./gradlew :desktop:packageDeb :desktop:createDistributable
```

macOS（`.dmg` 和 `.app`，在 Mac 上用 JDK 17 跑）：

```
./gradlew :desktop:packageDmg :desktop:createDistributable
```

iOS（未签名的 `.ipa`，在 Mac 上用 Xcode、JDK 17 和 [XcodeGen](https://github.com/yonaskolb/XcodeGen) 跑）：

```
./gradlew :ios:linkReleaseFrameworkIosArm64
cd iosApp && xcodegen generate
xcodebuild -project VConverter.xcodeproj -target VConverter -configuration Release -sdk iphoneos CODE_SIGNING_ALLOWED=NO build
```

iOS 版是把桌面版那套界面用 Compose Multiplatform 重新构建的（iPhone 上侧边菜单，iPad 上完整侧边栏），所有转换器、编辑器、试玩和在线功能都原生跑。共享代码是纯 Kotlin，不用 JVM 独有的 API（自己的 JSON 库、ZIP 读写和小平台层），所以 Android、桌面和 iOS 跑的是同一套转换器。文件和压缩包用「文件」App 选；模组文件夹就地读写，`.zip` 结果通过「文件」的导出表保存。iOS 打不开 RAR 和 7z 压缩包（先解压，或者用 `.zip`）。

桌面版（Windows、Linux、macOS）是 [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) 构建，和 Android 版共用所有转换器。

### 致谢

**简体中文汉化**：[YLuzew](https://github.com/YLuzew)（发起与维护）、[LS_Xenith](https://github.com/LS_Xenith)（玄虚，汉化）

- 压缩包用 `java.util.zip`（ZIP）和 [7-Zip-JBinding-4Android](https://github.com/omicronapps/7-Zip-JBinding-4Android) / [7-Zip-JBinding](https://sevenzipjbind.sourceforge.net/)（Windows、Linux 和 Intel Mac 上解 RAR、RAR5、7z；Apple Silicon 的 Mac 上这些包得先解压）解
- 桌面版谱面编辑器用 [VorbisSPI](https://github.com/pdudits/soundlibs) 和 MP3SPI 播 `.ogg` 和 `.mp3`；iOS 上 `.ogg` 用 Sean Barrett 的 [stb_vorbis](https://github.com/nothings/stb)（公有领域）解码
- 界面用的是 Friday Night Funkin' 风格字体，在 `assets/fonts/funkin.ttf`

## 参考

- [FunkinCrew/Funkin](https://github.com/FunkinCrew/Funkin)
- [ShadowMario/FNF-PsychEngine](https://github.com/ShadowMario/FNF-PsychEngine)
- [CodenameCrew/CodenameEngine](https://github.com/CodenameCrew/CodenameEngine)
