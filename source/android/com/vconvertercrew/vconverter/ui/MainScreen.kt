package com.vconvertercrew.vconverter.ui

import com.vconvertercrew.vconverter.overlay.soundClickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vconvertercrew.vconverter.R
import com.vconvertercrew.vconverter.overlay.FpsCounter
import com.vconvertercrew.vconverter.ui.characters.CharactersScreen
import com.vconvertercrew.vconverter.ui.characters.CharactersViewModel
import com.vconvertercrew.vconverter.ui.charts.ChartsScreen
import com.vconvertercrew.vconverter.ui.charts.CodenameChartsViewModel
import com.vconvertercrew.vconverter.ui.charts.CodenameToPsychChartsViewModel
import com.vconvertercrew.vconverter.ui.charts.PsychToCodenameChartsViewModel
import com.vconvertercrew.vconverter.ui.charts.PsychToVSliceChartsViewModel
import com.vconvertercrew.vconverter.ui.charts.ReverseChartsViewModel
import com.vconvertercrew.vconverter.ui.components.AppLogo
import com.vconvertercrew.vconverter.ui.scripts.ScriptsScreen
import com.vconvertercrew.vconverter.ui.scripts.ScriptsViewModel
import com.vconvertercrew.vconverter.ui.stages.StagesScreen
import com.vconvertercrew.vconverter.ui.stages.StagesViewModel
import com.vconvertercrew.vconverter.ui.mod.ModScreen
import com.vconvertercrew.vconverter.ui.mod.ModViewModel
import com.vconvertercrew.vconverter.ui.weeks.WeeksScreen
import com.vconvertercrew.vconverter.ui.weeks.WeeksViewModel
import com.vconvertercrew.vconverter.ui.theme.Funkin
import com.vconvertercrew.vconverter.ui.editor.CharacterEditorScreen
import com.vconvertercrew.vconverter.ui.editor.CharacterEditorViewModel
import com.vconvertercrew.vconverter.ui.generator.ModGenScreen
import com.vconvertercrew.vconverter.ui.generator.ModGenViewModel
import com.vconvertercrew.vconverter.ui.editor.ChartEditorScreen
import com.vconvertercrew.vconverter.ui.editor.ChartEditorViewModel
import com.vconvertercrew.vconverter.ui.generator.ScriptGenScreen
import com.vconvertercrew.vconverter.ui.generator.ScriptGenViewModel
import com.vconvertercrew.vconverter.ui.editor.StageEditorScreen
import com.vconvertercrew.vconverter.ui.editor.StageEditorViewModel
import com.vconvertercrew.vconverter.ui.editor.WeekEditorScreen
import com.vconvertercrew.vconverter.ui.editor.WeekEditorViewModel
import com.vconvertercrew.vconverter.ui.online.OnlineModsScreen
import com.vconvertercrew.vconverter.ui.online.OnlineNewsScreen
import com.vconvertercrew.vconverter.ui.online.OnlineRoomsScreen
import com.vconvertercrew.vconverter.ui.online.OnlineShareScreen
import com.vconvertercrew.vconverter.ui.online.OnlineViewModel
import com.vconvertercrew.vconverter.ui.playtest.LocalPlayTest
import com.vconvertercrew.vconverter.ui.playtest.PlayTestLauncher
import com.vconvertercrew.vconverter.ui.playtest.PlayTestPlayScreen
import com.vconvertercrew.vconverter.ui.playtest.PlayTestReportScreen
import com.vconvertercrew.vconverter.ui.playtest.PlayTestViewModel
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedVisibility
import com.vconvertercrew.vconverter.ui.codename.CodenameCharactersViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameModScreen
import com.vconvertercrew.vconverter.ui.codename.CodenameModViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameScriptsViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameStagesViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameWeeksViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameToPsychModViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameToPsychCharactersViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameToPsychStagesViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameToPsychWeeksViewModel
import com.vconvertercrew.vconverter.ui.codename.CodenameToPsychScriptsViewModel
import com.vconvertercrew.vconverter.ui.codename.PsychToCodenameCharactersViewModel
import com.vconvertercrew.vconverter.ui.codename.PsychToCodenameStagesViewModel
import com.vconvertercrew.vconverter.ui.codename.PsychToCodenameWeeksViewModel
import com.vconvertercrew.vconverter.ui.codename.PsychToCodenameScriptsViewModel
import com.vconvertercrew.vconverter.ui.mod.PsychToCodenameModViewModel
import com.vconvertercrew.vconverter.ui.reverse.ReverseCharactersViewModel
import com.vconvertercrew.vconverter.ui.reverse.ReverseModScreen
import com.vconvertercrew.vconverter.ui.reverse.ReverseModViewModel
import com.vconvertercrew.vconverter.ui.reverse.ReverseScreen
import com.vconvertercrew.vconverter.ui.reverse.ReverseScriptsViewModel
import com.vconvertercrew.vconverter.ui.reverse.ReverseStagesViewModel
import com.vconvertercrew.vconverter.ui.reverse.ReverseWeeksViewModel
import com.vconvertercrew.vconverter.overlay.MainMenuEntry
import com.vconvertercrew.vconverter.overlay.MainMenuArt
import com.vconvertercrew.vconverter.overlay.FunkinMainMenu
import com.vconvertercrew.vconverter.overlay.FunkinOptionsMenu
import com.vconvertercrew.vconverter.overlay.MenuArt
import com.vconvertercrew.vconverter.overlay.MenuOption
import com.vconvertercrew.vconverter.overlay.DiscordBadge
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.displayCutoutPadding

interface AppTab {
    val label: String
    @get:DrawableRes val icon: Int
    val accent: Color
    val title: String
    val description: String
}

enum class Section(val label: String, @DrawableRes val icon: Int, val accent: Color) {
    CONVERTER("转换器", R.drawable.ic_swap, Funkin.Down),
    GENERATOR("生成器", R.drawable.ic_magic, Funkin.Warning),
    ONLINE("在线", R.drawable.ic_share, Funkin.Pink),
    PLAYTEST("测试", R.drawable.ic_tab_chart, Funkin.Up)
}

enum class PlayTab(
    override val label: String,
    @DrawableRes override val icon: Int,
    override val accent: Color,
    override val title: String,
    override val description: String
) : AppTab {
    PLAY("播放", R.drawable.ic_tab_chart, Funkin.Up, "试玩", "随便播一首模组的歌、转换结果或谱面编辑器里的谱子，带上它的背景和人物，看看哪里坏了。"),
    REPORT("报告", R.drawable.ic_warning, Funkin.Warning, "试玩报告", "在加载的这首歌的谱子、人物、背景、音频和转换结果里发现的问题。")
}

enum class OnlineTab(
    override val label: String,
    @DrawableRes override val icon: Int,
    override val accent: Color,
    override val title: String,
    override val description: String
) : AppTab {
    MODS("模组", R.drawable.ic_download, Funkin.Pink, "模组画廊", "在 V-Converter 服务器上找别人分享的模组并下载。"),
    SHARE("分享", R.drawable.ic_share, Funkin.Up, "分享模组", "把模组 .zip 上传到服务器画廊，让别人能下载。"),
    ROOMS("房间", R.drawable.ic_tab_chart, Funkin.Left, "公开房间", "加入别人公开列出的多人编辑房间，一起编辑。"),
    NEWS("新闻", R.drawable.ic_warning, Funkin.Down, "新闻", "V-Converter 的新版本和服务器公告。")
}

enum class Tab(
    override val label: String,
    @DrawableRes override val icon: Int,
    override val accent: Color,
    override val title: String,
    override val description: String,
    val reverseDescription: String,
    val codenameDescription: String
) : AppTab {
    MOD("模组", R.drawable.ic_tab_mod, Funkin.Pink, "整个模组", "从一个文件夹或 .zip / .rar / .7z 压缩包转换整个 Psych 模组。", "从一个文件夹或 .zip / .rar / .7z 压缩包转换整个 V-Slice 模组。", "从一个文件夹或 .zip / .rar / .7z 压缩包转换整个 Codename 模组。"),
    CHARTS("谱子", R.drawable.ic_tab_chart, Funkin.Down, "谱子", "把 Psych 谱面和 events.json 合成一首 V-Slice 歌曲。", "把 V-Slice 的元数据 + 谱面转成 Psych 的各难度谱面。", "把 Codename 的扫弦线谱面、meta.json 和事件转成 V-Slice 歌曲。"),
    CHARACTERS("人物", R.drawable.ic_tab_character, Funkin.Left, "人物", "人物 JSON + 图集 XML，偏移精确到像素。", "把 V-Slice 人物转回 Psych，有 XML 时精确。", "人物 XML + 图集 XML，偏移已缩放，死亡动画合并。"),
    STAGES("背景", R.drawable.ic_tab_stage, Funkin.Up, "背景", "背景布局、摄像机和编辑器物件。", "把 V-Slice 背景转成 Psych 背景编辑器文件。", "把背景 XML 的精灵、方框和人物位置转成 V-Slice 物件。"),
    WEEKS("周目", R.drawable.ic_tab_week, Funkin.Right, "周目", "把故事周目转成带菜单物件的 V-Slice 关卡。", "把 V-Slice 关卡转成 Psych 周目和菜单人物。", "把周目 XML 和菜单人物转成 V-Slice 关卡。"),
    SCRIPTS("脚本", R.drawable.ic_tab_script, Funkin.Lavender, "脚本", "把 Lua 和 HScript 转成 V-Slice 模块。", ".hxc 脚本转成 Psych HScript（.hx）。", "把 Codename HScript（.hx）转成 V-Slice 模块和歌曲事件。");

    fun describe(direction: Direction): String = when (direction) {
        Direction.PSYCH_TO_VSLICE -> description
        Direction.VSLICE_TO_PSYCH -> reverseDescription
        Direction.CODENAME_TO_VSLICE -> codenameDescription
        Direction.CODENAME_TO_PSYCH -> when (this) {
            MOD -> "把整个 Codename 模组转成 Psych 模组：谱子、脚本、事件和音频。"
            CHARTS -> "把 Codename strumline 谱子和事件转成 Psych 难度谱子。"
            CHARACTERS -> "把人物 XML 转成 Psych JSON，偏移未缩放。"
            STAGES -> "把背景 XML 转成 Psych 背景 JSON，外加一个背景脚本。"
            WEEKS -> "把周目 XML 转成 Psych 周目、weekList.txt 和菜单人物。"
            SCRIPTS -> "把 Codename HScript 转成 Psych HScript（.hx），回调已对上。"
        }
        Direction.PSYCH_TO_CODENAME -> when (this) {
            MOD -> "把整个 Psych 模组转成 Codename 模组：歌曲、Lua/HScript、事件和音频。"
            CHARTS -> "把 Psych 谱子和 events.json 转成 Codename 的 strumline 与 meta.json。"
            CHARACTERS -> "把人物 JSON 转成 Codename XML，偏移已缩放。"
            STAGES -> "把背景 JSON（以及背景 Lua 精灵）转成 Codename 背景 XML。"
            WEEKS -> "把 Psych 周目转成周目 XML、weeks.txt 和菜单人物。"
            SCRIPTS -> "把 Lua 和 HScript 转成带 Psych 辅助的 Codename HScript。"
        }
        else -> com.vconvertercrew.vconverter.ui.nmv.nmvDescription(direction, this)
    }
}

enum class Engine(val label: String, val title: String, val detail: String, val color: Color) {
    PSYCH("PSYCH", "Psych Engine", "Lua · HScript · JSON", Funkin.Left),
    CODENAME("CODENAME", "Codename Engine", "XML · HScript", Funkin.Orange),
    VSLICE("V-SLICE", "V-Slice", "Polymod · .hxc", Funkin.Down),
    NMV("NMV", "NightmareVision", "HScript · JSON", Funkin.Purple),
    FPSPLUS("FPS PLUS", "FPS Plus", "Polymod · .hxc", Funkin.Right);

    val key: String get() = when (this) {
        PSYCH -> "psych"
        CODENAME -> "codename"
        VSLICE -> "vslice"
        NMV -> "nmv"
        FPSPLUS -> "fpsplus"
    }

    val canBeSource: Boolean get() = this != FPSPLUS
}

enum class Direction(val from: Engine, val to: Engine) {
    PSYCH_TO_VSLICE(Engine.PSYCH, Engine.VSLICE),
    VSLICE_TO_PSYCH(Engine.VSLICE, Engine.PSYCH),
    CODENAME_TO_VSLICE(Engine.CODENAME, Engine.VSLICE),
    CODENAME_TO_PSYCH(Engine.CODENAME, Engine.PSYCH),
    PSYCH_TO_CODENAME(Engine.PSYCH, Engine.CODENAME),
    NMV_TO_PSYCH(Engine.NMV, Engine.PSYCH),
    NMV_TO_VSLICE(Engine.NMV, Engine.VSLICE),
    NMV_TO_CODENAME(Engine.NMV, Engine.CODENAME),
    PSYCH_TO_NMV(Engine.PSYCH, Engine.NMV),
    VSLICE_TO_NMV(Engine.VSLICE, Engine.NMV),
    CODENAME_TO_NMV(Engine.CODENAME, Engine.NMV),
    PSYCH_TO_FPSPLUS(Engine.PSYCH, Engine.FPSPLUS),
    VSLICE_TO_FPSPLUS(Engine.VSLICE, Engine.FPSPLUS),
    CODENAME_TO_FPSPLUS(Engine.CODENAME, Engine.FPSPLUS),
    NMV_TO_FPSPLUS(Engine.NMV, Engine.FPSPLUS);

    val label: String get() = "${from.title} to ${to.title}"
    val canSwap: Boolean get() = entries.any { it.from == to && it.to == from }
    fun swapped(): Direction = entries.firstOrNull { it.from == to && it.to == from } ?: this
    fun nextTarget(): Direction {
        val options = entries.filter { it.from == from }
        return options[(options.indexOf(this) + 1) % options.size]
    }
    fun retarget(engine: Engine): Direction = entries.firstOrNull { it.from == from && it.to == engine } ?: this

    companion object {
        fun from(engine: Engine): Direction = when (engine) {
            Engine.PSYCH -> PSYCH_TO_VSLICE
            Engine.VSLICE -> VSLICE_TO_PSYCH
            Engine.CODENAME -> CODENAME_TO_VSLICE
            Engine.NMV -> NMV_TO_PSYCH
            Engine.FPSPLUS -> PSYCH_TO_FPSPLUS
        }

        fun targets(engine: Engine): List<Engine> = entries.filter { it.from == engine }.map { it.to }
    }
}

enum class GenTab(
    override val label: String,
    @DrawableRes override val icon: Int,
    override val accent: Color,
    override val title: String,
    override val description: String
) : AppTab {
    MOD("Mod", R.drawable.ic_tab_mod, Funkin.Pink, "New mod", "A ready V-Slice mod folder with metadata and icon."),
    SONG("谱子", R.drawable.ic_tab_chart, Funkin.Down, "谱面编辑器", "Start a song or open any chart, then place notes, events and BPM changes to the music."),
    CHARACTER("Char", R.drawable.ic_tab_character, Funkin.Left, "人物编辑器", "Open any character or a spritesheet, then preview the animations and fix offsets, camera and scale."),
    STAGE("背景", R.drawable.ic_tab_stage, Funkin.Up, "背景编辑器", "Open any stage or start from your images, then place props and characters with the game camera in view."),
    WEEK("周目", R.drawable.ic_tab_week, Funkin.Right, "周目编辑器", "Open any week or start a new one, then lay out the story menu, its songs and how it unlocks."),
    XML("XML", R.drawable.ic_tab_character, Funkin.Lavender, "XML Editor", "Turn .png frames into a spritesheet with its Sparrow .xml, or make the .xml for one image that already holds every frame."),
    SCRIPT("脚本", R.drawable.ic_tab_script, Funkin.Lavender, "New script", "Ready-to-edit .hxc classes for V-Slice.")
}

private const val MENU_NONE = ""
private const val MENU_MAIN = "main"
private const val MENU_TITLE = "title"
private const val MENU_CONVERTER = "converter"
private const val MENU_GENERATOR = "generator"

@Composable
fun MainScreen() {
    val modFolder: ModFolderViewModel = viewModel()
    val charts: PsychToVSliceChartsViewModel = viewModel()
    val characters: CharactersViewModel = viewModel()
    val stages: StagesViewModel = viewModel()
    val weeks: WeeksViewModel = viewModel()
    val mod: ModViewModel = viewModel()
    val scripts: ScriptsViewModel = viewModel()
    val genMod: ModGenViewModel = viewModel()
    val genSong: ChartEditorViewModel = viewModel()
    val genCharacter: CharacterEditorViewModel = viewModel()
    val genStage: StageEditorViewModel = viewModel()
    val genWeek: WeekEditorViewModel = viewModel()
    val genScript: ScriptGenViewModel = viewModel()
    val online: OnlineViewModel = viewModel()
    val playtest: PlayTestViewModel = viewModel()
    val revMod: ReverseModViewModel = viewModel()
    val revCharts: ReverseChartsViewModel = viewModel()
    val revCharacters: ReverseCharactersViewModel = viewModel()
    val revStages: ReverseStagesViewModel = viewModel()
    val revWeeks: ReverseWeeksViewModel = viewModel()
    val revScripts: ReverseScriptsViewModel = viewModel()
    val cnMod: CodenameModViewModel = viewModel()
    val cnCharts: CodenameChartsViewModel = viewModel()
    val cnCharacters: CodenameCharactersViewModel = viewModel()
    val cnStages: CodenameStagesViewModel = viewModel()
    val cnWeeks: CodenameWeeksViewModel = viewModel()
    val cnScripts: CodenameScriptsViewModel = viewModel()
    val c2pMod: CodenameToPsychModViewModel = viewModel()
    val c2pCharts: CodenameToPsychChartsViewModel = viewModel()
    val c2pCharacters: CodenameToPsychCharactersViewModel = viewModel()
    val c2pStages: CodenameToPsychStagesViewModel = viewModel()
    val c2pWeeks: CodenameToPsychWeeksViewModel = viewModel()
    val c2pScripts: CodenameToPsychScriptsViewModel = viewModel()
    val p2cMod: PsychToCodenameModViewModel = viewModel()
    val p2cCharts: PsychToCodenameChartsViewModel = viewModel()
    val p2cCharacters: PsychToCodenameCharactersViewModel = viewModel()
    val p2cStages: PsychToCodenameStagesViewModel = viewModel()
    val p2cWeeks: PsychToCodenameWeeksViewModel = viewModel()
    val p2cScripts: PsychToCodenameScriptsViewModel = viewModel()
    val nmv = com.vconvertercrew.vconverter.ui.nmv.rememberNmvModels()
    remember {
        listOf(charts, characters, stages, weeks, mod, scripts, cnMod, cnCharts, cnCharacters, cnStages, cnWeeks, cnScripts, genMod, genScript)
            .forEach { it.vsliceTarget = true }
        Tab.entries.forEach { tab -> runCatching { nmv.forTab(Direction.NMV_TO_VSLICE, tab).vsliceTarget = true } }
        true
    }
    var direction by rememberSaveable { mutableStateOf(Direction.PSYCH_TO_VSLICE) }

    var section by rememberSaveable { mutableStateOf(Section.CONVERTER) }
    var converterTab by rememberSaveable { mutableStateOf(Tab.MOD) }
    var generatorTab by rememberSaveable { mutableStateOf(GenTab.MOD) }
    var onlineTab by rememberSaveable { mutableStateOf(OnlineTab.MODS) }
    var playTab by rememberSaveable { mutableStateOf(PlayTab.PLAY) }
    var pendingSave by remember { mutableStateOf<AppTab?>(null) }
    val tab: AppTab = when (section) {
        Section.CONVERTER -> converterTab
        Section.GENERATOR -> generatorTab
        Section.ONLINE -> onlineTab
        Section.PLAYTEST -> playTab
    }

    fun converterFor(target: AppTab): BaseConverterViewModel = if (direction in com.vconvertercrew.vconverter.ui.nmv.nmvDirections && target is Tab) nmv.forTab(direction, target)
    else if (direction == Direction.VSLICE_TO_PSYCH && target is Tab) when (target) {
        Tab.MOD -> revMod
        Tab.CHARTS -> revCharts
        Tab.CHARACTERS -> revCharacters
        Tab.STAGES -> revStages
        Tab.WEEKS -> revWeeks
        Tab.SCRIPTS -> revScripts
    } else if (direction == Direction.CODENAME_TO_VSLICE && target is Tab) when (target) {
        Tab.MOD -> cnMod
        Tab.CHARTS -> cnCharts
        Tab.CHARACTERS -> cnCharacters
        Tab.STAGES -> cnStages
        Tab.WEEKS -> cnWeeks
        Tab.SCRIPTS -> cnScripts
    } else if (direction == Direction.CODENAME_TO_PSYCH && target is Tab) when (target) {
        Tab.MOD -> c2pMod
        Tab.CHARTS -> c2pCharts
        Tab.CHARACTERS -> c2pCharacters
        Tab.STAGES -> c2pStages
        Tab.WEEKS -> c2pWeeks
        Tab.SCRIPTS -> c2pScripts
    } else if (direction == Direction.PSYCH_TO_CODENAME && target is Tab) when (target) {
        Tab.MOD -> p2cMod
        Tab.CHARTS -> p2cCharts
        Tab.CHARACTERS -> p2cCharacters
        Tab.STAGES -> p2cStages
        Tab.WEEKS -> p2cWeeks
        Tab.SCRIPTS -> p2cScripts
    } else when (target) {
        Tab.MOD -> mod
        Tab.CHARTS -> charts
        Tab.CHARACTERS -> characters
        Tab.STAGES -> stages
        Tab.WEEKS -> weeks
        Tab.SCRIPTS -> scripts
        GenTab.MOD -> genMod
        GenTab.SONG -> genSong
        GenTab.CHARACTER -> genCharacter
        GenTab.STAGE -> genStage
        GenTab.WEEK -> genWeek
        GenTab.SCRIPT -> genScript
        else -> mod
    }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        val target = pendingSave
        pendingSave = null
        if (uri != null) {
            modFolder.select(uri)
            if (target != null) converterFor(target).save(uri)
        }
    }

    val save: () -> Unit = {
        val folder = modFolder.folder
        if (folder != null) {
            converterFor(tab).save(folder)
        } else {
            pendingSave = tab
            folderPicker.launch(null)
        }
    }
    val folderName = modFolder.folder?.let { modFolder.folderName }

    val playLauncher = remember(playtest) {
        object : PlayTestLauncher {
            override fun files(files: List<Pair<String, ByteArray>>, label: String) {
                playtest.state.open(files, label)
                section = Section.PLAYTEST
                playTab = PlayTab.PLAY
            }

            override fun chart(document: com.vconvertercrew.vconverter.editor.ChartDocument, audio: List<com.vconvertercrew.vconverter.editor.EditorAudio>) {
                genSong.pause()
                playtest.state.useChart(document, audio, "谱面编辑器 · ${document.meta.songName.ifBlank { document.id }}")
                section = Section.PLAYTEST
                playTab = PlayTab.PLAY
            }
        }
    }

    val joinRoom: (com.vconvertercrew.vconverter.online.RoomInfo) -> Unit = { room ->
        section = Section.GENERATOR
        val live = when (room.kind) {
            "character" -> genCharacter.live.also { generatorTab = GenTab.CHARACTER }
            "stage" -> genStage.live.also { generatorTab = GenTab.STAGE }
            "week" -> genWeek.live.also { generatorTab = GenTab.WEEK }
            else -> genSong.live.also { generatorTab = GenTab.SONG }
        }
        live.join(room.code)
    }

    val cursor = rememberAppCursor()
    val settings = rememberAppSettings()
    var optionsOpen by rememberSaveable { mutableStateOf(false) }
    var menuScreen by rememberSaveable { mutableStateOf(MENU_TITLE) }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val compact = configuration.screenHeightDp < 600
    val menuContext = androidx.compose.ui.platform.LocalContext.current
    val menuEntries = remember { listOf(MainMenuEntry("converter", "converter"), MainMenuEntry("generator", "generator"), MainMenuEntry("online", "online"), MainMenuEntry("options", "options", flicker = false)) }
    val menuArt = remember {
        val assets = menuContext.assets
        MainMenuArt.load(
            menuEntries.map { it.id },
            image = { path -> runCatching { assets.open(path).use { android.graphics.BitmapFactory.decodeStream(it) }.asImageBitmap() }.getOrNull() },
            text = { path -> runCatching { assets.open(path).use { it.readBytes().decodeToString() } }.getOrNull() }
        )
    }
    val listArt = remember {
        val assets = menuContext.assets
        MenuArt.load(
            image = { path -> runCatching { assets.open(path).use { android.graphics.BitmapFactory.decodeStream(it) }.asImageBitmap() }.getOrNull() },
            text = { path -> runCatching { assets.open(path).use { it.readBytes().decodeToString() } }.getOrNull() }
        )
    }
    fun listFor(target: Section): String = when (target) {
        Section.CONVERTER -> MENU_CONVERTER
        Section.GENERATOR -> MENU_GENERATOR
        else -> MENU_MAIN
    }
    val converterOptions = Direction.entries.map { option ->
        MenuOption.Submenu("${option.from.label} to ${option.to.label}", option.label, option.label) {
            Tab.entries.map { target ->
                MenuOption.Action(target.title, target.describe(option)) {
                    com.vconvertercrew.vconverter.overlay.FunkinTransition.go {
                        direction = option
                        converterTab = target
                        section = Section.CONVERTER
                        menuScreen = MENU_NONE
                    }
                }
            }
        }
    }
    val generatorOptions = GenTab.entries.map { target ->
        MenuOption.Action(target.title, target.description) {
            com.vconvertercrew.vconverter.overlay.FunkinTransition.go {
                generatorTab = target
                section = Section.GENERATOR
                menuScreen = MENU_NONE
            }
        }
    }
    val appVersion = remember { runCatching { menuContext.packageManager.getPackageInfo(menuContext.packageName, 0).versionName }.getOrNull() ?: "" }
    val navTabs: List<AppTab> = when (section) {
        Section.CONVERTER, Section.GENERATOR -> emptyList()
        Section.ONLINE -> OnlineTab.entries
        Section.PLAYTEST -> PlayTab.entries
    }
    val onTab: (AppTab) -> Unit = { selected ->
        when (selected) {
            is Tab -> converterTab = selected
            is GenTab -> generatorTab = selected
            is OnlineTab -> onlineTab = selected
            is PlayTab -> playTab = selected
        }
    }
    androidx.activity.compose.BackHandler(enabled = menuScreen == MENU_NONE && !optionsOpen) { menuScreen = listFor(section) }
    val touchCursor = remember { TouchCursorState() }
    val cursorImage = rememberCursorImage()

    androidx.compose.runtime.CompositionLocalProvider(LocalPlayTest provides playLauncher) {
    Box(Modifier.fillMaxSize().then(if (settings.touchCursor) Modifier.touchCursor(touchCursor) else Modifier)) {
        if (menuScreen == MENU_TITLE) {
            val titleArt = remember {
                val assets = menuContext.assets
                com.vconvertercrew.vconverter.overlay.TitleArt.load(
                    image = { path -> runCatching { assets.open(path).use { android.graphics.BitmapFactory.decodeStream(it) }.asImageBitmap() }.getOrNull() },
                    text = { path -> runCatching { assets.open(path).use { it.readBytes().decodeToString() } }.getOrNull() }
                )
            }
            com.vconvertercrew.vconverter.overlay.FunkinTitle(titleArt, onFinish = { menuScreen = MENU_MAIN })
        } else if (menuScreen == MENU_MAIN) {
            FunkinMainMenu(
                entries = menuEntries,
                art = menuArt,
                version = "V-Converter v$appVersion",
                onSelect = { entry ->
                    when (entry.id) {
                        "converter" -> menuScreen = MENU_CONVERTER
                        "generator" -> menuScreen = MENU_GENERATOR
                        "online" -> {
                            section = Section.ONLINE
                            menuScreen = MENU_NONE
                        }
                        else -> optionsOpen = true
                    }
                },
                overlay = { DiscordBadge(Modifier.align(Alignment.TopEnd).displayCutoutPadding().padding(16.dp)) }
            )
        } else if (menuScreen == MENU_CONVERTER || menuScreen == MENU_GENERATOR) {
            androidx.compose.runtime.key(menuScreen) {
                var listBack by remember { mutableStateOf(0) }
                androidx.activity.compose.BackHandler(enabled = !optionsOpen) { listBack++ }
                FunkinOptionsMenu(
                    title = if (menuScreen == MENU_CONVERTER) "Converter" else "Generator",
                    options = if (menuScreen == MENU_CONVERTER) converterOptions else generatorOptions,
                    art = listArt,
                    onClose = { menuScreen = MENU_MAIN },
                    closeRequests = listBack,
                    overlay = { DiscordBadge(Modifier.align(Alignment.TopEnd).displayCutoutPadding().padding(16.dp)) }
                )
            }
        } else if (section == Section.ONLINE) {
            var onlineBack by remember { mutableStateOf(0) }
            androidx.activity.compose.BackHandler(enabled = !optionsOpen) { onlineBack++ }
            com.vconvertercrew.vconverter.ui.online.OnlineMenuScreen(
                online,
                onJoin = joinRoom,
                onBack = { menuScreen = MENU_MAIN },
                backRequests = onlineBack,
                overlay = { DiscordBadge(Modifier.align(Alignment.BottomStart).displayCutoutPadding().padding(16.dp)) }
            )
        } else if (section == Section.GENERATOR && generatorTab == GenTab.XML) {
            val xmlEditor: com.vconvertercrew.vconverter.ui.editor.XmlEditorViewModel = viewModel()
            Box(Modifier.fillMaxSize()) {
                com.vconvertercrew.vconverter.ui.editor.XmlEditorScreen(xmlEditor) { com.vconvertercrew.vconverter.overlay.FunkinTransition.go { menuScreen = MENU_GENERATOR } }
            }
        } else if (section == Section.GENERATOR && generatorTab == GenTab.SONG) {
            Box(Modifier.fillMaxSize()) {
                ChartEditorScreen(genSong, folderName, save) { com.vconvertercrew.vconverter.overlay.FunkinTransition.go { menuScreen = MENU_GENERATOR } }
            }
        } else Scaffold(
            modifier = Modifier.appCursor(cursor),
            containerColor = Funkin.Background,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                Header(
                    compact = compact,
                    onLogo = { menuScreen = MENU_MAIN },
                    folderName = folderName,
                    onPickFolder = { folderPicker.launch(null) },
                    onOptions = { optionsOpen = true },
                    section = section,
                    onSection = {
                        section = it
                        if (it == Section.CONVERTER || it == Section.GENERATOR) menuScreen = listFor(it)
                    },
                    direction = direction
                )
            },
        ) { padding ->
            Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize().padding(padding)) {
            if (navTabs.isNotEmpty()) FunkinNavRail(navTabs, tab, onTab)
            AnimatedContent(
                targetState = tab to (if (section == Section.CONVERTER) direction else Direction.PSYCH_TO_VSLICE),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .imePadding(),
                label = "tab"
            ) { (current, currentDirection) ->
                val editorTab = current == GenTab.SONG || current == GenTab.CHARACTER || current == GenTab.STAGE || current == GenTab.WEEK
                if (editorTab) Box(Modifier.fillMaxSize().padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp)) {
                    val exit = { menuScreen = MENU_GENERATOR }
                    when (current) {
                        GenTab.SONG -> ChartEditorScreen(genSong, folderName, save, exit)
                        GenTab.CHARACTER -> CharacterEditorScreen(genCharacter, folderName, save, exit)
                        GenTab.STAGE -> StageEditorScreen(genStage, folderName, save, exit)
                        GenTab.WEEK -> WeekEditorScreen(genWeek, folderName, save, exit)
                        else -> Unit
                    }
                } else Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                ) {
                    if (current is Tab) {
                        EngineBridge(currentDirection) { direction = it }
                        Spacer(Modifier.size(12.dp))
                    }
                    TabHero(current, currentDirection)
                    Spacer(Modifier.size(16.dp))
                    if (currentDirection in com.vconvertercrew.vconverter.ui.nmv.nmvDirections && current is Tab) {
                        com.vconvertercrew.vconverter.ui.nmv.NmvTabScreen(nmv, currentDirection, current, folderName, save)
                    } else if (currentDirection == Direction.CODENAME_TO_VSLICE && current is Tab) when (current) {
                        Tab.MOD -> CodenameModScreen(cnMod, folderName, save)
                        Tab.CHARTS -> ChartsScreen(cnCharts, folderName, save)
                        Tab.CHARACTERS -> ReverseScreen(cnCharacters, Funkin.Left, "人物", "添加 Codename 人物", "选择 data/characters/*.xml 和对应的精灵图集 .xml（images/characters/…），位置才准确。加上 <名字>-dead.xml 可以把死亡动画合并进来。", R.drawable.ic_tab_character, folderName, save, buttonText = "转换为 V-Slice")
                        Tab.STAGES -> ReverseScreen(cnStages, Funkin.Up, "背景", "添加 Codename 背景", "选择 data/stages/*.xml。把背景图片（.png 和精灵图集 .xml）也加上，缩放过的精灵才能保持准确位置。", R.drawable.ic_tab_stage, folderName, save, buttonText = "转换为 V-Slice")
                        Tab.WEEKS -> ReverseScreen(cnWeeks, Funkin.Right, "周目", "添加 Codename 周目", "选择 data/weeks/weeks/*.xml，再选 data/weeks/characters/*.xml 作为菜单人物。", R.drawable.ic_tab_week, folderName, save, buttonText = "转换为 V-Slice")
                        Tab.SCRIPTS -> ReverseScreen(cnScripts, Funkin.Lavender, "脚本", "添加 Codename 脚本", "选择 .hx 脚本。给背景脚本补上背景 XML，给自定义事件补上 data/events/*.json。压缩包会保留文件夹结构，歌曲、背景、事件和音符脚本都能落到正确的作用域。", R.drawable.ic_tab_script, folderName, save, buttonText = "转换为 V-Slice")
                    } else if (currentDirection == Direction.CODENAME_TO_PSYCH && current is Tab) when (current) {
                        Tab.MOD -> CodenameModScreen(
                            c2pMod, folderName, save,
                            intro = "把整个 Codename Engine 模组转成 Psych：歌曲与变体、人物、背景（连同背景脚本）、周目、HScript 脚本、事件、音符类型、图片与音频。",
                            infoNote = "写入 pack.json（来源是 data/config/modpack.ini）。icon.png 会变成 pack.png。",
                            convertNote = "把转换好的模组下载成 .zip（解压到 Psych 的 mods 文件夹），或者直接写进一个空的模组文件夹。"
                        )
                        Tab.CHARTS -> ChartsScreen(c2pCharts, folderName, save)
                        Tab.CHARACTERS -> ReverseScreen(c2pCharacters, Funkin.Left, "人物", "添加 Codename 人物", "选择 data/characters/*.xml 和对应的精灵图集 .xml（images/characters/…）；Animate 图集则用 Animation.json + spritemap*.json，这样偏移才准确。", R.drawable.ic_tab_character, folderName, save)
                        Tab.STAGES -> ReverseScreen(c2pStages, Funkin.Up, "背景", "添加 Codename 背景", "选择 data/stages/*.xml。精灵会变成背景物件；跟拍动作、混合模式和额外设置会写进背景脚本。", R.drawable.ic_tab_stage, folderName, save)
                        Tab.WEEKS -> ReverseScreen(c2pWeeks, Funkin.Right, "周目", "添加 Codename 周目", "选择 data/weeks/weeks/*.xml、data/weeks/characters/*.xml，再选歌曲的 meta.json 来带出自由游玩图标和颜色。", R.drawable.ic_tab_week, folderName, save)
                        Tab.SCRIPTS -> ReverseScreen(c2pScripts, Funkin.Lavender, "脚本", "添加 Codename 脚本", "选择 .hx 脚本。给背景脚本补上背景 XML，给自定义事件补上 data/events/*.json。压缩包会保留文件夹结构，歌曲、背景、事件和音符脚本会落到对应的 Psych 文件夹。", R.drawable.ic_tab_script, folderName, save)
                    } else if (currentDirection == Direction.PSYCH_TO_CODENAME && current is Tab) when (current) {
                        Tab.MOD -> ModScreen(
                            p2cMod, folderName, save,
                            intro = "把整个 Psych Engine 模组转成 Codename：歌曲、人物、背景、周目、Lua/HScript 脚本、自定义事件、音符类型、图片与音频。",
                            infoNote = "写入 data/config/modpack.ini。pack.png 会变成 icon.png。",
                            convertNote = "把转换好的模组下载成 .zip（解压到 Codename 的 mods 文件夹），或者直接写进一个空的模组文件夹。"
                        )
                        Tab.CHARTS -> ChartsScreen(p2cCharts, folderName, save)
                        Tab.CHARACTERS -> ReverseScreen(p2cCharacters, Funkin.Left, "人物", "添加 Psych 人物", "选择 characters/*.json。偏移会按 Codename 缩放，站位决定摄像机偏移。Animate 图集人物再补上 Animation.json + spritemap*.json。", R.drawable.ic_tab_character, folderName, save, buttonText = "转换为 Codename")
                        Tab.STAGES -> ReverseScreen(p2cStages, Funkin.Up, "背景", "添加 Psych 背景", "选择 stages/*.json；如果背景是在 Lua 里生成精灵的，再选 stages/<背景>.lua：那些精灵会被搬进背景 XML。", R.drawable.ic_tab_stage, folderName, save, buttonText = "转换为 Codename")
                        Tab.WEEKS -> ReverseScreen(p2cWeeks, Funkin.Right, "周目", "添加 Psych 周目", "选择 weeks/*.json 和 images/menucharacters/*.json。", R.drawable.ic_tab_week, folderName, save, buttonText = "转换为 Codename")
                        Tab.SCRIPTS -> ReverseScreen(p2cScripts, Funkin.Lavender, "脚本", "添加 Lua / HScript", "挑 .lua 和 .hx 脚本。压缩包会保留目录结构，所以 scripts/、data/<song>/、stages/、custom_events/ 和 custom_notetypes/ 会落进对应的 Codename 目录。背景脚本再加 stages/*.json。", R.drawable.ic_tab_script, folderName, save, buttonText = "转换为 Codename")
                    } else if (currentDirection == Direction.VSLICE_TO_PSYCH) when (current) {
                        Tab.MOD -> ReverseModScreen(revMod, folderName, save)
                        Tab.CHARTS -> ChartsScreen(revCharts, folderName, save)
                        Tab.CHARACTERS -> ReverseScreen(revCharacters, Funkin.Left, "人物", "添加 V-Slice 人物", "选择 data/characters/*.json 和对应的精灵图集 .xml（images/…）；Animate 图集用 Animation.json + spritemap*.json，位置才准确。压缩包也可以。", R.drawable.ic_tab_character, folderName, save)
                        Tab.STAGES -> ReverseScreen(revStages, Funkin.Up, "背景", "添加 V-Slice 背景", "选择 data/stages/*.json。物件、图层、滚动和动画会变成 Psych 的背景物件。", R.drawable.ic_tab_stage, folderName, save)
                        Tab.WEEKS -> ReverseScreen(revWeeks, Funkin.Right, "关卡", "添加 V-Slice 关卡", "选择 data/levels/*.json，再选歌曲的 -metadata.json 带出名称、图标和难度。", R.drawable.ic_tab_week, folderName, save)
                        Tab.SCRIPTS -> ReverseScreen(revScripts, Funkin.Lavender, "脚本", "添加 .hxc 脚本", "选择 V-Slice 的 .hxc 脚本。模块、歌曲、背景、人物、歌曲事件和音符类型会变成 Psych 的 HScript 文件，放进对应文件夹。", R.drawable.ic_tab_script, folderName, save)
                        else -> Unit
                    } else when (current) {
                        Tab.MOD -> ModScreen(mod, folderName, save)
                        Tab.CHARTS -> ChartsScreen(charts, folderName, save)
                        Tab.CHARACTERS -> CharactersScreen(characters, folderName, save)
                        Tab.STAGES -> StagesScreen(stages, folderName, save)
                        Tab.WEEKS -> WeeksScreen(weeks, folderName, save)
                        Tab.SCRIPTS -> ScriptsScreen(scripts, folderName, save)
                        GenTab.MOD -> ModGenScreen(genMod, folderName, save)
                        GenTab.SCRIPT -> ScriptGenScreen(genScript, folderName, save)
                        OnlineTab.MODS -> OnlineModsScreen(online)
                        OnlineTab.SHARE -> OnlineShareScreen(online)
                        OnlineTab.NEWS -> OnlineNewsScreen(online)
                        OnlineTab.ROOMS -> OnlineRoomsScreen(online, joinRoom)
                        PlayTab.PLAY -> PlayTestPlayScreen(playtest)
                        PlayTab.REPORT -> PlayTestReportScreen(playtest)
                        else -> Unit
                    }
                    Spacer(Modifier.size(24.dp))
                }
            }
            }
            if (settings.fpsCounter) {
                FpsCounter(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = padding.calculateTopPadding() + 4.dp, end = 12.dp)
                )
            }
            }
        }
        com.vconvertercrew.vconverter.overlay.FunkinTransitionOverlay()
        if (settings.touchCursor) TouchCursorOverlay(touchCursor, cursorImage, Modifier.fillMaxSize())
    }
    }
    val activity = androidx.compose.ui.platform.LocalContext.current as? com.vconvertercrew.vconverter.MainActivity
    if (optionsOpen) OptionsDialog(settings) {
        optionsOpen = false
        activity?.requestLocalNetwork()
    }
    MenuMusic(settings.menuMusic, settings.musicVolume, suppressed = genSong.editor.playing)
    UiSounds(settings.uiSounds)
}

@Composable
private fun Header(
    compact: Boolean,
    onLogo: () -> Unit,
    folderName: String?,
    onPickFolder: () -> Unit,
    onOptions: () -> Unit,
    section: Section,
    onSection: (Section) -> Unit,
    direction: Direction
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Funkin.Background)
            .statusBarsPadding()
            .displayCutoutPadding()
            .padding(start = 16.dp, end = 16.dp, top = if (compact) 4.dp else 8.dp, bottom = if (compact) 6.dp else 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(RoundedCornerShape(12.dp)).soundClickable(onClick = onLogo)) { AppLogo(40) }
            Spacer(Modifier.width(10.dp))
            Column(modifier = if (compact) Modifier.widthIn(max = 170.dp) else Modifier.weight(1f)) {
                Text("V-转换器", style = MaterialTheme.typography.titleLarge, color = Funkin.Text, maxLines = 1)
                AnimatedContent(targetState = section to direction, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "subtitle") { (current, currentDirection) ->
                    if (current == Section.GENERATOR) {
                        Text("新建 V-Slice 内容", style = MaterialTheme.typography.labelSmall, color = Funkin.Warning, maxLines = 1)
                    } else if (current == Section.ONLINE) {
                        Text("模组、更新与新闻", style = MaterialTheme.typography.labelSmall, color = Funkin.Pink, maxLines = 1)
                    } else if (current == Section.PLAYTEST) {
                        Text("试玩并检查歌曲", style = MaterialTheme.typography.labelSmall, color = Funkin.Up, maxLines = 1)
                    } else {
                        Text(
                            currentDirection.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Funkin.Muted,
                            maxLines = 1
                        )
                    }
                }
            }
            if (compact) {
                Spacer(Modifier.width(10.dp))
                SectionSwitch(section, onSection, Modifier.weight(1f))
            }
            Spacer(Modifier.width(8.dp))
            FolderChip(folderName, onPickFolder)
            Spacer(Modifier.width(8.dp))
            OptionsButton(onOptions)
        }
        if (!compact) SectionSwitch(section, onSection)
    }
}

@Composable
private fun FolderChip(folderName: String?, onClick: () -> Unit) {
    val picked = folderName != null
    val shape = RoundedCornerShape(50)
    val tint = if (picked) Funkin.Up else Funkin.Muted
    Row(
        modifier = Modifier
            .widthIn(max = 130.dp)
            .clip(shape)
            .background(if (picked) Funkin.Up.copy(alpha = 0.12f) else Funkin.Surface)
            .border(1.5.dp, if (picked) Funkin.Up.copy(alpha = 0.6f) else Funkin.Outline, shape)
            .soundClickable(onClick = onClick)
            .padding(start = 10.dp, end = 12.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.ic_folder), contentDescription = "模组文件夹", tint = tint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Column {
            Text("模组文件夹", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Funkin.Muted, maxLines = 1)
            Text(
                folderName ?: "选择",
                style = MaterialTheme.typography.labelMedium,
                color = if (picked) Funkin.Text else Funkin.Down,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TabHero(tab: AppTab, direction: Direction) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(tab.accent.copy(alpha = 0.26f), Funkin.Surface, Funkin.Surface)))
            .border(1.5.dp, tab.accent.copy(alpha = 0.4f), shape)
    ) {
        Icon(
            painterResource(tab.icon),
            contentDescription = null,
            tint = tab.accent.copy(alpha = 0.10f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 18.dp)
                .size(110.dp)
                .rotate(-12f)
        )
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(tab.accent)
                    .border(3.dp, Funkin.Ink, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(tab.icon), contentDescription = null, tint = Funkin.Ink, modifier = Modifier.size(27.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tab.title.uppercase(), style = MaterialTheme.typography.titleLarge, color = Funkin.Text)
                Spacer(Modifier.height(2.dp))
                Text(if (tab is Tab) tab.describe(direction) else tab.description, style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
            }
        }
    }
}

@Composable
private fun FunkinNavRail(tabs: List<AppTab>, selected: AppTab, onSelect: (AppTab) -> Unit) {
    Row(Modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(84.dp)
                .background(Funkin.Surface)
                .navigationBarsPadding()
                .displayCutoutPadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEach { item ->
                val active = item == selected
                val fill by animateColorAsState(if (active) item.accent else Color.Transparent, label = "railFill")
                val tint by animateColorAsState(if (active) Funkin.Ink else Funkin.Muted, label = "railTint")
                val shape = RoundedCornerShape(12.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .soundClickable { onSelect(item) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 30.dp)
                            .clip(shape)
                            .background(fill)
                            .then(if (active) Modifier.border(2.5.dp, Funkin.Ink, shape) else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(item.icon), contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) item.accent else Funkin.Muted,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        Box(
            Modifier
                .fillMaxHeight()
                .width(3.dp)
                .background(Brush.verticalGradient(tabs.map { it.accent }))
        )
    }
}

@Composable
private fun SectionSwitch(selected: Section, onSelect: (Section) -> Unit, modifier: Modifier = Modifier.fillMaxWidth()) {
    val outer = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .clip(outer)
            .background(Funkin.Ink)
            .border(2.dp, Funkin.Outline, outer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Section.entries.forEach { item ->
            val active = item == selected
            val fill by animateColorAsState(if (active) item.accent else Color.Transparent, label = "section")
            val content by animateColorAsState(if (active) Funkin.Ink else Funkin.Muted, label = "sectionText")
            val shape = RoundedCornerShape(14.dp)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(shape)
                    .background(fill)
                    .soundClickable { onSelect(item) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (active) {
                    Icon(painterResource(item.icon), contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                }
                Text(item.label.uppercase(), style = MaterialTheme.typography.labelLarge, fontSize = 12.sp, color = content, maxLines = 1, softWrap = false)
            }
        }
    }
}

@Composable
private fun EngineBridge(direction: Direction, onChange: (Direction) -> Unit) {
    val turn by animateFloatAsState(if (direction.from.ordinal > direction.to.ordinal) 180f else 0f, animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f), label = "swap")
    val from = direction.from
    val to = direction.to
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EngineCard("从", from, Modifier.weight(1f)) {
                    val engines = Engine.entries.filter { it.canBeSource }
                    onChange(Direction.from(engines[(engines.indexOf(from) + 1) % engines.size]))
                }
                EngineCard("到", to, Modifier.weight(1f)) { onChange(direction.nextTarget()) }
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Funkin.Ink)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(from.color, to.color)))
                    .border(2.5.dp, Funkin.Ink, CircleShape)
                    .soundClickable(enabled = direction.canSwap) { onChange(direction.swapped()) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(if (direction.canSwap) R.drawable.ic_swap else R.drawable.ic_arrow_forward),
                    contentDescription = if (direction.canSwap) "交换方向" else null,
                    tint = Funkin.Ink,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (direction.canSwap) turn else 0f)
                )
            }
        }
        SourcePicker("源", direction.from, Engine.entries.filter { it.canBeSource }) { onChange(if (it == direction.from) direction else Direction.from(it)) }
        SourcePicker("目标", direction.to, Direction.targets(direction.from)) { onChange(direction.retarget(it)) }
    }
}

@Composable
private fun SourcePicker(caption: String, selected: Engine, enabled: List<Engine>, onSelect: (Engine) -> Unit) {
    val outer = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(outer)
            .background(Funkin.Surface)
            .border(1.5.dp, Funkin.Outline, outer)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(caption, style = MaterialTheme.typography.labelSmall, color = Funkin.Muted, modifier = Modifier.width(56.dp).padding(start = 8.dp))
        Engine.entries.forEach { engine ->
            val active = engine == selected
            val usable = engine in enabled
            val fill by animateColorAsState(if (active) engine.color else Color.Transparent, label = "source")
            val content by animateColorAsState(if (active) Funkin.Ink else if (usable) Funkin.Muted else Funkin.Muted.copy(alpha = 0.35f), label = "sourceText")
            val shape = RoundedCornerShape(11.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(shape)
                    .background(fill)
                    .soundClickable(enabled = usable) { onSelect(engine) },
                contentAlignment = Alignment.Center
            ) {
                Text(engine.label, style = MaterialTheme.typography.labelMedium, color = content, maxLines = 1)
            }
        }
    }
}

@Composable
private fun EngineCard(role: String, engine: Engine, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val color by animateColorAsState(engine.color, label = "engine")
    Column(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.16f), Funkin.Surface)))
            .border(1.5.dp, color.copy(alpha = 0.55f), shape)
            .soundClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = if (role == "从") Alignment.Start else Alignment.End
    ) {
        Text(role.uppercase(), style = MaterialTheme.typography.labelSmall, color = Funkin.Muted)
        AnimatedContent(
            targetState = engine,
            transitionSpec = {
                val direction = if (role == "从") -1 else 1
                (slideInVertically { it * direction } + fadeIn()) togetherWith (slideOutVertically { -it * direction } + fadeOut())
            },
            label = "engineName"
        ) { current ->
            Column(horizontalAlignment = if (role == "从") Alignment.Start else Alignment.End) {
                Text(current.label, style = MaterialTheme.typography.titleLarge, color = current.color, maxLines = 1)
                Text(current.detail, style = MaterialTheme.typography.labelSmall, color = Funkin.Muted, maxLines = 1)
            }
        }
    }
}
