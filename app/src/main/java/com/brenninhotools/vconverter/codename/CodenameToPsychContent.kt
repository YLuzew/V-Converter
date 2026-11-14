package com.brenninhotools.vconverter.codename

import com.brenninhotools.vconverter.core.CharacterSlot
import com.brenninhotools.vconverter.core.Report
import com.brenninhotools.vconverter.core.ReportBuilder
import com.brenninhotools.vconverter.core.AnimateAtlas
import com.brenninhotools.vconverter.core.SparrowAtlas
import com.brenninhotools.vconverter.reverse.VSliceToPsychCharacter
import com.brenninhotools.vconverter.psych.PsychWriters
import com.brenninhotools.vconverter.script.hx.HxSource
import com.brenninhotools.vconverter.stage.PsychPoint
import com.brenninhotools.vconverter.stage.PsychStage
import com.brenninhotools.vconverter.week.PsychMenuCharacter
import com.brenninhotools.vconverter.week.PsychWeek
import com.brenninhotools.vconverter.week.PsychWeekSong

data class PsychFile(val path: String, val text: String)

data class PsychCopy(val from: String, val to: String)

data class CodenamePsychResult(
    val id: String,
    val files: List<PsychFile>,
    val report: Report,
    val copies: List<PsychCopy> = emptyList(),
    val hooks: PsychHooks = PsychHooks(),
    val extensions: List<String> = emptyList()
) {
    val mainText: String get() = files.firstOrNull()?.text ?: ""
}

object CodenameToPsychCharacter {
    fun convert(
        character: CodenameCharacter,
        slot: CharacterSlot,
        atlases: Map<String, SparrowAtlas>,
        atlas: AnimateAtlas? = null
    ): CodenamePsychResult {
        val animate = atlas != null
        val raw = CodenamePsychCharacter.build(character, slot, atlases, null)
        val missingSymbols = mutableListOf<String>()
        val built = if (atlas == null) raw else raw.copy(character = raw.character.copy(animations = raw.character.animations.map { anim ->
            val source = character.animations.firstOrNull { it.prefix == anim.prefix && it.indices == anim.indices } ?: character.animations.firstOrNull { it.prefix == anim.prefix }
            val label = if (source?.label == true) atlas.findLabel(anim.prefix) else null
            val symbol = if (label == null) atlas.symbols[anim.prefix] ?: atlas.findSymbol(anim.prefix) else null
            val scale = character.scale
            when {
                label != null -> anim.copy(
                    prefix = VSliceToPsychCharacter.exactSymbol(atlas, atlas.mainSymbol),
                    indices = if (anim.indices.isNotEmpty()) anim.indices.map { label.start + it } else (label.start until label.start + label.duration).toList(),
                    offsetX = anim.offsetX + scale * atlas.mainBounds.x,
                    offsetY = anim.offsetY + scale * atlas.mainBounds.y
                )
                symbol != null -> anim.copy(
                    prefix = VSliceToPsychCharacter.exactSymbol(atlas, symbol.name),
                    offsetX = anim.offsetX + scale * symbol.bounds.x,
                    offsetY = anim.offsetY + scale * symbol.bounds.y
                )
                else -> {
                    missingSymbols += "${anim.anim} (\"${anim.prefix}\")"
                    anim
                }
            }
        }))
        val report = ReportBuilder()
        report.stat("Animations", built.character.animations.size)
        report.stat("Side", slot.name.lowercase())
        built.notes.warnings.forEach(report::warn)
        built.notes.sections.forEach { report.section(it.title, it.lines) }
        if (character.extraSheets.isNotEmpty()) report.section("Spritesheets", listOf("额外的图集已合进 image 字段：${built.character.image}"))
        if (atlases.isEmpty() && !animate) report.warn("没有提供图集 XML，所以镜像偏移和居中摄像机的修正是近似的。")
        if (animate) report.section("Animate 图集", listOf("帧标签动画变成了主符号的序号（Psych 只播符号），偏移里已经算上每个符号的边界。"))
        if (missingSymbols.isNotEmpty()) report.warn("这些动画在 Animation.json 里既没有对应符号也没有对应帧标签：${missingSymbols.joinToString()}.")
        val gameOver = character.gameOverCharacter
        val notes = mutableListOf<String>()
        if (gameOver != null && gameOver != "bf-dead") notes += "死亡人物「$gameOver\": set \"gameOverChar\": \"$gameOver\" in the charts (V-Converter does this for the songs it converts)."
        character.interval?.let { if (it != 2) notes += "Codename 待机间隔 $it became danceEveryNumBeats in the script below; Psych reads it from the character's animations (idle every 2 beats, danceLeft/danceRight every beat)." }
        report.section("Notes", notes)
        val hooks = character.interval?.takeIf { it != 2 && character.animations.any { a -> a.name == "idle" } }?.let { interval ->
            PsychHooks(callbacks = mapOf("onCreatePost" to listOf("if (self != null) self.danceEveryNumBeats = $interval;")))
        } ?: PsychHooks()
        return CodenamePsychResult(character.id, listOf(PsychFile("characters/${character.id}.json", PsychWriters.character(built.character))), report.build(), hooks = hooks)
    }
}

object CodenameToPsychStage {
    private val groups = mapOf("bf" to ("boyfriendGroup" to "boyfriend"), "gf" to ("gfGroup" to "gf"), "dad" to ("dadGroup" to "dad"))

    fun isPixel(stage: CodenameStage): Boolean =
        stage.extensions.any { it.substringAfterLast('/').equals("pixel", ignoreCase = true) } || stage.id.lowercase().startsWith("school")

    fun convert(stage: CodenameStage): CodenamePsychResult {
        val report = ReportBuilder()
        val pixel = isPixel(stage)
        val spots = stage.spots
        fun spot(key: String) = spots.getValue(key)
        val psych = PsychStage(
            directory = "",
            defaultZoom = stage.zoom,
            isPixelStage = pixel,
            stageUI = if (pixel) "pixel" else "",
            boyfriend = PsychPoint(spot("bf").x, spot("bf").y),
            girlfriend = PsychPoint(spot("gf").x, spot("gf").y),
            opponent = PsychPoint(spot("dad").x, spot("dad").y),
            hideGirlfriend = false,
            cameraBoyfriend = PsychPoint(spot("bf").cameraX, spot("bf").cameraY),
            cameraOpponent = PsychPoint(spot("dad").cameraX, spot("dad").cameraY),
            cameraGirlfriend = PsychPoint(spot("gf").cameraX, spot("gf").cameraY),
            cameraSpeed = 1.0,
            preload = emptyList(),
            objects = stage.objects
        )
        val json = PsychWriters.stage(psych)

        val fields = mutableListOf<String>()
        val create = mutableListOf<String>()
        val post = mutableListOf<String>()
        val beat = mutableListOf<String>()
        val countdown = mutableListOf<String>()
        val functions = mutableListOf<String>()
        val notes = mutableListOf<String>()

        val defaults = mapOf("bf" to 1.0, "gf" to 0.95, "dad" to 1.0)
        spots.forEach { (key, s) ->
            val (group, character) = groups[key] ?: return@forEach
            val base = defaults.getValue(key)
            if (s.scroll.x != base || s.scroll.y != base) post += "game.$group.scrollFactor.set(${num(s.scroll.x)}, ${num(s.scroll.y)});"
            if (s.scale != 1.0) post += "if (game.$character != null) game.$character.scale.set(game.$character.scale.x * ${num(s.scale)}, game.$character.scale.y * ${num(s.scale)});"
            if (s.alpha != 1.0) post += "if (game.$character != null) game.$character.alpha = ${num(s.alpha)};"
            if (s.angle != 0.0) post += "if (game.$character != null) game.$character.angle = ${num(s.angle)};"
            if (s.flip != null && s.flip != (key == "bf")) post += "if (game.$character != null) game.$character.flipX = !game.$character.flipX;"
        }

        if (stage.beats.isNotEmpty()) {
            fields += "var cneBeatSprites:Array<Dynamic> = [" + stage.beats.joinToString(", ") { b ->
                "{name: ${HxSource.hxString(b.name)}, interval: ${b.interval}, offset: ${b.offset}, anims: [${b.anims.joinToString(", ") { HxSource.hxString(it) }}], counted: 0, skipCountdown: ${b.onCountdown}}"
            } + "];"
            functions += """
function cneStageDance(beat:Int, countdown:Bool):Void
{
  for (b in cneBeatSprites)
  {
    if (countdown && b.skipCountdown) continue;
    var spr:Dynamic = getVar(b.name);
    if (spr == null || !spr.exists) continue;
    var shifted:Int = beat + b.offset;
    if (shifted % b.interval != 0) continue;
    var list:Array<String> = b.anims;
    var anim:String = list[b.counted % list.length];
    b.counted++;
    if (anim == null || anim == "none") continue;
    if (Reflect.hasField(spr, "animOffsets")) spr.playAnim(anim, true);
    else spr.animation.play(anim, true);
  }
}
""".trim()
            beat += "cneStageDance(curBeat, false);"
            countdown += "cneStageDance(counter - 4, true);"
        }

        val imports = linkedSetOf<String>()
        stage.properties.forEach { p ->
            val path = p.path.split('.')
            if (path.any { !Regex("^[A-Za-z_][A-Za-z0-9_]*$").matches(it) }) {
                notes += "Property ${p.sprite}.${p.path} was skipped (not a plain field path)."
                return@forEach
            }
            val value = when (p.type) {
                "int", "float", "number", "f", "i" -> p.value.toDoubleOrNull()?.let { num(it) }
                "bool", "boolean", "b" -> (p.value.trim().lowercase() == "true").toString()
                "color" -> "FlxColor.fromString(${HxSource.hxString(p.value)})"
                else -> HxSource.hxString(p.value)
            } ?: return@forEach
            post += "if (getVar(${HxSource.hxString(p.sprite)}) != null) getVar(${HxSource.hxString(p.sprite)}).${p.path} = $value;"
        }
        stage.blends.forEach { (name, blend) ->
            imports += "psychlua.LuaUtils"
            post += "if (getVar(${HxSource.hxString(name)}) != null) getVar(${HxSource.hxString(name)}).blend = LuaUtils.blendModeFromString(${HxSource.hxString(blend)});"
        }
        stage.startCamera?.let { (x, y) ->
            if (x != null) post += "game.camFollow.x = ${num(x)};"
            if (y != null) post += "game.camFollow.y = ${num(y)};"
            post += "FlxG.camera.snapToTarget();"
            notes += "初始摄像机位置改成了 onCreatePost 里的 camFollow。"
        }
        val extensions = stage.extensions.filter { !it.substringAfterLast('/').equals("pixel", ignoreCase = true) }
        extensions.forEach { ext -> create += "game.startHScriptsNamed(${HxSource.hxString("stages/extensions/${ext.substringAfterLast('/')}.hx")});" }
        if (pixel) notes += "像素扩展改成了 stageUI \"pixel\"（Psych 自带的像素音符、倒计时和判定）。"
        if (stage.zoomFactors.isNotEmpty()) report.warn("带 zoomfactor 的精灵（${stage.zoomFactors.keys.joinToString()}) zoom with the camera in Psych; it has no zoom factor.")
        if (stage.extraCharacters.isNotEmpty()) report.warn("额外的人物位（${stage.extraCharacters.keys.joinToString()}) have no Psych stage equivalent; their notes cannot be sung in Psych.")
        stage.ratings?.let { notes += "判定位置不属于 Psych 的背景配置（在脚本里挪 game.comboGroup）。" }
        stage.warnings.filterNot { it.contains("zoomfactor") || it.contains("额外人物") }.forEach { report.warn(it.replace("V-Slice", "Psych")) }
        notes += stage.notes.map { it.replace("V-Slice 总是加载整个背景", "Psych 用低画质过滤保留它们") }.filterNot { it.contains("初始摄像机") }

        report.stat("Objects", stage.objects.count { it.type in setOf("sprite", "animatedSprite", "square") })
        report.stat("会跳舞的精灵", stage.beats.size)
        report.section("Notes", notes)
        val hooks = PsychHooks(
            fields = fields,
            callbacks = linkedMapOf<String, List<String>>().apply {
                if (create.isNotEmpty()) put("onCreate", create)
                if (post.isNotEmpty()) put("onCreatePost", post)
                if (beat.isNotEmpty()) put("onBeatHit", beat)
                if (countdown.isNotEmpty()) put("onCountdownTick", countdown)
            },
            functions = functions,
            imports = imports.toList()
        )
        return CodenamePsychResult(stage.id, listOf(PsychFile("stages/${stage.id}.json", json)), report.build(), hooks = hooks, extensions = extensions)
    }

    private fun num(value: Double): String = CodenameToPsychEvents.number(value)
}

data class PsychSongInfo(val icon: String, val color: List<Int>)

object CodenameToPsychWeek {
    private val defaultDifficulties = listOf("easy", "normal", "hard")

    fun convert(week: CodenameWeek, songInfo: (String) -> PsychSongInfo?, songName: (String) -> String = { it }): CodenamePsychResult {
        val report = ReportBuilder()
        val songs = week.songs.map { song ->
            val name = songName(song.name)
            val info = songInfo(song.name)
            PsychWeekSong(name, info?.icon ?: "face", info?.color ?: listOf(146, 113, 253))
        }
        val difficulties = if (week.difficulties.map { it.lowercase() } == defaultDifficulties) emptyList()
            else week.difficulties.map { it.replaceFirstChar { c -> c.uppercaseChar() } }
        val psych = PsychWeek(
            songs = songs,
            weekCharacters = week.characters.take(3).map { it ?: "" } + List((3 - week.characters.size).coerceAtLeast(0)) { "" },
            weekBackground = "",
            storyName = week.name,
            weekName = week.name,
            weekBefore = "",
            startUnlocked = true,
            hiddenUntilUnlocked = false,
            hideStoryMode = false,
            hideFreeplay = false,
            difficulties = difficulties
        )
        val copies = listOf(
            PsychCopy("images/menus/storymenu/weeks/${week.sprite}.png", "images/storymenu/${week.id}.png")
        )
        report.stat("Songs", songs.size)
        val notes = mutableListOf<String>()
        notes += "周目图片：images/menus/storymenu/weeks/${week.sprite}.png → images/storymenu/${week.id}.png"
        if (difficulties.isNotEmpty()) notes += "Difficulties: ${difficulties.joinToString()}"
        val hidden = week.songs.filter { it.hidden }
        if (hidden.isNotEmpty()) report.warn("Codename 里从自由游玩隐藏的歌曲（${hidden.joinToString { it.name }}) are listed in Psych's freeplay; Psych hides whole weeks only.")
        week.songs.mapNotNull { s -> s.variation?.let { "${s.name} plays its \"$it\" variant in Codename; the week now lists the \"${songName(s.name)}\" song." } }.forEach { notes += it }
        report.section("Notes", notes)
        return CodenamePsychResult(week.id, listOf(PsychFile("weeks/${week.id}.json", PsychWriters.week(psych))), report.build(), copies)
    }

    fun freeplayWeek(id: String, songs: List<PsychWeekSong>): PsychFile = PsychFile(
        "weeks/$id.json",
        PsychWriters.week(PsychWeek(songs, listOf("", "", ""), "", "", "Freeplay", "", true, false, true, false, emptyList()))
    )

    fun menuCharacter(menu: CodenameMenuCharacter, id: String): CodenamePsychResult {
        val report = ReportBuilder()
        val idle = menu.animations.firstOrNull { it.name == "idle" } ?: menu.animations.firstOrNull()
        val confirm = menu.animations.firstOrNull { it.name == "confirm" }
        val dx = (idle?.x ?: 0.0) * menu.scale
        val dy = (idle?.y ?: 0.0) * menu.scale
        val psych = PsychMenuCharacter(
            image = id,
            scale = menu.scale,
            positionX = menu.x + dx,
            positionY = menu.y + dy,
            idleAnim = idle?.prefix ?: "",
            confirmAnim = confirm?.prefix ?: "",
            flipX = menu.flipX,
            antialiasing = menu.antialiasing
        )
        if (idle?.indices?.isNotEmpty() == true || confirm?.indices?.isNotEmpty() == true) report.warn("菜单人物 $id uses frame indices; Psych menu characters play whole prefixes.")
        if (confirm != null && (confirm.x != idle?.x || confirm.y != idle?.y)) report.warn("菜单人物 $id moves its confirm animation; Psych uses one position for both animations.")
        val copies = listOf("png", "xml").map { ext -> PsychCopy("images/${menu.sprite}.$ext", "images/menucharacters/$id.$ext") }
        return CodenamePsychResult(id, listOf(PsychFile("images/menucharacters/$id.json", PsychWriters.menuCharacter(psych))), report.build(), copies)
    }
}
