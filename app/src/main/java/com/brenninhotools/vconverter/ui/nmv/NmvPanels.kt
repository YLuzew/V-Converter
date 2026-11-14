package com.brenninhotools.vconverter.ui.nmv

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brenninhotools.vconverter.R
import com.brenninhotools.vconverter.convert.NmvConverters
import com.brenninhotools.vconverter.convert.NmvTab
import com.brenninhotools.vconverter.ui.BaseConverterViewModel
import com.brenninhotools.vconverter.ui.Direction
import com.brenninhotools.vconverter.ui.Engine
import com.brenninhotools.vconverter.ui.Tab
import com.brenninhotools.vconverter.ui.codename.CodenameModScreen
import com.brenninhotools.vconverter.ui.mod.ModScreen
import com.brenninhotools.vconverter.ui.reverse.ReverseModScreen
import com.brenninhotools.vconverter.ui.reverse.ReverseScreen
import com.brenninhotools.vconverter.ui.theme.Funkin

class NmvModels(
    val toPsych: NmvToPsychModViewModel,
    val toVSlice: NmvToVSliceModViewModel,
    val toCodename: NmvToCodenameModViewModel,
    val fromPsych: PsychToNmvModViewModel,
    val fromVSlice: VSliceToNmvModViewModel,
    val fromCodename: CodenameToNmvModViewModel,
    val nmvToFps: NmvToFpsModViewModel,
    val psychToFps: PsychToFpsModViewModel,
    val vsliceToFps: VSliceToFpsModViewModel,
    val codenameToFps: CodenameToFpsModViewModel,
    val files: Map<Pair<Direction, Tab>, NmvFileViewModel>
) {
    fun mod(direction: Direction): BaseConverterViewModel = when (direction) {
        Direction.NMV_TO_VSLICE -> toVSlice
        Direction.NMV_TO_CODENAME -> toCodename
        Direction.PSYCH_TO_NMV -> fromPsych
        Direction.VSLICE_TO_NMV -> fromVSlice
        Direction.CODENAME_TO_NMV -> fromCodename
        Direction.NMV_TO_FPSPLUS -> nmvToFps
        Direction.PSYCH_TO_FPSPLUS -> psychToFps
        Direction.VSLICE_TO_FPSPLUS -> vsliceToFps
        Direction.CODENAME_TO_FPSPLUS -> codenameToFps
        else -> toPsych
    }

    fun forTab(direction: Direction, tab: Tab): BaseConverterViewModel = if (tab == Tab.MOD) mod(direction) else files.getValue(direction to tab)
}

val nmvDirections = listOf(
    Direction.NMV_TO_PSYCH, Direction.NMV_TO_VSLICE, Direction.NMV_TO_CODENAME,
    Direction.PSYCH_TO_NMV, Direction.VSLICE_TO_NMV, Direction.CODENAME_TO_NMV,
    Direction.PSYCH_TO_FPSPLUS, Direction.VSLICE_TO_FPSPLUS, Direction.CODENAME_TO_FPSPLUS, Direction.NMV_TO_FPSPLUS
)

private fun nmvTab(tab: Tab): NmvTab = when (tab) {
    Tab.CHARTS -> NmvTab.CHARTS
    Tab.CHARACTERS -> NmvTab.CHARACTERS
    Tab.STAGES -> NmvTab.STAGES
    Tab.WEEKS -> NmvTab.WEEKS
    else -> NmvTab.SCRIPTS
}

@Composable
fun rememberNmvModels(): NmvModels {
    val app = LocalContext.current.applicationContext as Application
    val files = LinkedHashMap<Pair<Direction, Tab>, NmvFileViewModel>()
    nmvDirections.forEach { direction ->
        Tab.entries.filter { it != Tab.MOD }.forEach { tab ->
            files[direction to tab] = viewModel<NmvFileViewModel>(
                key = "nmv-${direction.name}-${tab.name}",
                factory = viewModelFactory { initializer { NmvFileViewModel(app, NmvConverters.create(direction.from.key, direction.to.key, nmvTab(tab))) } }
            )
        }
    }
    return NmvModels(
        viewModel<NmvToPsychModViewModel>(),
        viewModel<NmvToVSliceModViewModel>(),
        viewModel<NmvToCodenameModViewModel>(),
        viewModel<PsychToNmvModViewModel>(),
        viewModel<VSliceToNmvModViewModel>(),
        viewModel<CodenameToNmvModViewModel>(),
        viewModel<NmvToFpsModViewModel>(),
        viewModel<PsychToFpsModViewModel>(),
        viewModel<VSliceToFpsModViewModel>(),
        viewModel<CodenameToFpsModViewModel>(),
        files
    )
}

fun nmvDescription(direction: Direction, tab: Tab): String {
    val target = direction.to.title
    if (direction.to == Engine.FPSPLUS) return when (tab) {
        Tab.MOD -> "A full ${direction.from.title} mod into an FPS Plus mod: charts, .hxc characters and stages, weeks, Freeplay and scripts."
        Tab.CHARTS -> "${direction.from.title} charts into fpsplus_1 charts with events.json and meta.json."
        Tab.CHARACTERS -> "${direction.from.title} characters into CharacterInfoBase classes (.hxc)."
        Tab.STAGES -> "${direction.from.title} stages into BaseStage classes (.hxc) with their scripts."
        Tab.WEEKS -> "${direction.from.title} weeks into FPS Plus weeks and story menu characters."
        Tab.SCRIPTS -> "${direction.from.title} scripts (Lua too) into FPS Plus Script classes (.hxc)."
    }
    return if (direction.from == Engine.NMV) when (tab) {
        Tab.MOD -> "A full NightmareVision mod into a $target mod: charts, HScript, characters, stages, weeks and audio."
        Tab.CHARTS -> "songs/<song>/charts (nmv2) into $target charts."
        Tab.CHARACTERS -> "NightmareVision character JSON into $target characters."
        Tab.STAGES -> "data/stages/<stage>/data.json and its stageObjects into $target stages."
        Tab.WEEKS -> "data/weeks and story menu characters into $target weeks."
        Tab.SCRIPTS -> "NightmareVision HScript into $target scripts."
    } else when (tab) {
        Tab.MOD -> "A full ${direction.from.title} mod into a NightmareVision mod, with Lua turned into HScript."
        Tab.CHARTS -> "${direction.from.title} charts into nmv2 charts with meta.json."
        Tab.CHARACTERS -> "${direction.from.title} characters into data/characters JSON."
        Tab.STAGES -> "${direction.from.title} stages into data/stages/<stage>/data.json with stageObjects."
        Tab.WEEKS -> "${direction.from.title} weeks into data/weeks and story menu characters."
        Tab.SCRIPTS -> "${direction.from.title} scripts (Lua too) into NightmareVision HScript."
    }
}

private fun hint(direction: Direction, tab: Tab): String = when (direction.from) {
    Engine.NMV -> when (tab) {
        Tab.CHARTS -> "挑 songs/<song>/charts/<难度>.json 和 events.json。保留 songs/<song>/charts 的文件夹结构（或者用压缩包），才能认出是哪首歌。"
        Tab.CHARACTERS -> "挑 data/characters/*.json。"
        Tab.STAGES -> "挑 data/stages/<背景>/data.json 和 script.hx（或者带背景文件夹的压缩包）。"
        Tab.WEEKS -> "挑 data/weeks/*.json 和 images/menus/story/characters/*.json。"
        else -> "挑 .hx 脚本。压缩包会保留目录，所以 songs/<song>/、data/scripts/、data/stages/、data/events/ 和 data/notetypes/ 各自的作用域不会丢。"
    }
    Engine.FPSPLUS -> ""
    Engine.PSYCH -> when (tab) {
        Tab.CHARTS -> "挑 data/<song>/<song>-<难度>.json 和 events.json。"
        Tab.CHARACTERS -> "挑 characters/*.json（连同它们的 .lua/.hx 脚本）。"
        Tab.STAGES -> "挑 stages/*.json 和 stages/<背景>.lua 或 .hx。"
        Tab.WEEKS -> "选择 weeks/*.json 和 images/menucharacters/*.json。"
        else -> "挑 .lua 和 .hx 脚本。压缩包会保留目录，所以 scripts/、data/<song>/、stages/、custom_events/ 和 custom_notetypes/ 各自的作用域不会丢。"
    }
    Engine.CODENAME -> when (tab) {
        Tab.CHARTS -> "挑 songs/<song>/charts/*.json，连同 meta.json 和 events.json。"
        Tab.CHARACTERS -> "挑 data/characters/*.xml 和对应的图集 .xml。"
        Tab.STAGES -> "挑 data/stages/*.xml。"
        Tab.WEEKS -> "挑 data/weeks/weeks/*.xml 和 data/weeks/characters/*.xml。"
        else -> "挑 .hx 脚本，再补上背景 XML 和 data/events/*.json 作为上下文。"
    }
    Engine.VSLICE -> when (tab) {
        Tab.CHARTS -> "挑 <song>-metadata.json 和 <song>-chart.json（连同变体）。"
        Tab.CHARACTERS -> "挑 data/characters/*.json 和对应的图集 .xml。"
        Tab.STAGES -> "挑 data/stages/*.json。"
        Tab.WEEKS -> "挑 data/levels/*.json，再加上歌曲的 -metadata.json。"
        else -> "挑 .hxc 脚本。"
    }
}

@Composable
fun NmvTabScreen(models: NmvModels, direction: Direction, tab: Tab, folderName: String?, save: () -> Unit) {
    val button = "Convert to " + direction.to.title
    when (tab) {
        Tab.MOD -> when (direction) {
            Direction.PSYCH_TO_NMV -> ModScreen(
                models.fromPsych, folderName, save,
                intro = "把整个 Psych Engine 模组转成 NightmareVision：谱面、人物、背景、周目、Lua 脚本（转成 HScript）、HScript、事件、音符类型、图片和音频。",
                infoNote = "写进 meta.json。pack.png 会变成 images/pack.png。",
                convertNote = "把转换好的模组下载成 .zip（解压到 NightmareVision 的 content 文件夹），或者直接写进一个空的模组文件夹。"
            )
            Direction.CODENAME_TO_NMV -> CodenameModScreen(
                models.fromCodename, folderName, save,
                intro = "把整个 Codename Engine 模组转成 NightmareVision：歌曲、人物、背景、周目、HScript 脚本、事件、音符类型、图片和音频。",
                infoNote = "写进 meta.json（来源是 data/config/modpack.ini）。icon.png 会变成 images/pack.png。",
                convertNote = "把转换好的模组下载成 .zip（解压到 NightmareVision 的 content 文件夹），或者直接写进一个空的模组文件夹。"
            )
            Direction.PSYCH_TO_FPSPLUS -> ModScreen(
                models.psychToFps, folderName, save,
                intro = "把整个 Psych Engine 模组转成 FPS Plus：谱面、人物和背景转成 .hxc 类，周目、自由游玩、Lua 和 HScript 脚本、事件、音符类型、图标、图片和音频。",
                infoNote = "写进 meta.json，带上 FPS Plus 需要的 uid 和 api_version。pack.png 会变成 icon.png。",
                convertNote = "把转换好的模组下载成 .zip（解压到 FPS Plus 的 mods 文件夹），或者直接写进一个空的模组文件夹。"
            )
            Direction.CODENAME_TO_FPSPLUS -> CodenameModScreen(
                models.codenameToFps, folderName, save,
                intro = "把整个 Codename Engine 模组转成 FPS Plus：歌曲、人物和背景转成 .hxc 类，周目、脚本、事件、图片和音频。",
                infoNote = "写进 meta.json（来源是 data/config/modpack.ini），带上 FPS Plus 需要的 uid 和 api_version。",
                convertNote = "把转换好的模组下载成 .zip（解压到 FPS Plus 的 mods 文件夹），或者直接写进一个空的模组文件夹。"
            )
            Direction.VSLICE_TO_FPSPLUS -> ReverseModScreen(
                models.vsliceToFps, folderName, save,
                intro = "把整个 V-Slice 模组转成 FPS Plus：歌曲、人物和背景转成 .hxc 类，关卡转成周目，脚本、图片和音频。",
                convertNote = "把转换好的模组下载成 .zip（解压到 FPS Plus 的 mods 文件夹），或者直接写进一个空的模组文件夹。"
            )
            Direction.VSLICE_TO_NMV -> ReverseModScreen(
                models.fromVSlice, folderName, save,
                intro = "把整个 V-Slice 模组转成 NightmareVision：歌曲（变体各自成歌）、人物、背景、关卡、.hxc 脚本、图片和音频。",
                convertNote = "把转换好的模组下载成 .zip（解压到 NightmareVision 的 content 文件夹），或者直接写进一个空的模组文件夹。"
            )
            else -> {
                val target = direction.to.title
                val note = when (direction.to) {
                    Engine.FPSPLUS -> "写进 meta.json，带上 FPS Plus 需要的 uid 和 api_version。"
                    Engine.VSLICE -> "写进 _polymod_meta.json（来源是 meta.json）。"
                    Engine.CODENAME -> "写进 data/config/modpack.ini（来源是 meta.json）。"
                    else -> "写进 pack.json（来源是 meta.json）。meta.json 里的图标会变成 pack.png。"
                }
                NmvModScreen(
                    models.mod(direction) as NmvModViewModel, folderName, save,
                    intro = "Convert a whole NightmareVision mod to $target: nmv2 charts, characters, stages with stageObjects, weeks, HScript, events, note types, images and audio.",
                    infoNote = note,
                    convertNote = "把转换好的模组下载成 .zip，或者直接写进一个空的模组文件夹。"
                )
            }
        }
        Tab.CHARTS -> ReverseScreen(models.files.getValue(direction to tab), Funkin.Down, "谱子", "加谱面", hint(direction, tab), R.drawable.ic_tab_chart, folderName, save, buttonText = button)
        Tab.CHARACTERS -> ReverseScreen(models.files.getValue(direction to tab), Funkin.Left, "人物", "加人物", hint(direction, tab), R.drawable.ic_tab_character, folderName, save, buttonText = button)
        Tab.STAGES -> ReverseScreen(models.files.getValue(direction to tab), Funkin.Up, "背景", "加背景", hint(direction, tab), R.drawable.ic_tab_stage, folderName, save, buttonText = button)
        Tab.WEEKS -> ReverseScreen(models.files.getValue(direction to tab), Funkin.Right, "周目", "加周目", hint(direction, tab), R.drawable.ic_tab_week, folderName, save, buttonText = button)
        Tab.SCRIPTS -> ReverseScreen(models.files.getValue(direction to tab), Funkin.Lavender, "脚本", "加脚本", hint(direction, tab), R.drawable.ic_tab_script, folderName, save, buttonText = button)
    }
}
