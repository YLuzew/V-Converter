package com.vconvertercrew.vconverter.codename

class PsychCodenameAdapters(
    private val defined: Set<String>,
    private val lua: Boolean,
    private val hooks: Map<String, List<String>> = emptyMap()
) {
    val helpers = linkedSetOf<String>()
    val fields = mutableListOf<String>()
    val warnings = mutableListOf<String>()

    companion object {
        val collisions = mapOf(
            "onStartCountdown" to "psychStartCountdown",
            "onEvent" to "psychEvent",
            "onGameOver" to "psychGameOver"
        )

        val callbacks = setOf(
            "onCreate", "onCreatePost", "onUpdate", "onUpdatePost", "onBeatHit", "onStepHit", "onSectionHit", "onSongStart", "onEndSong",
            "onStartCountdown", "onCountdownStarted", "onCountdownTick", "goodNoteHit", "goodNoteHitPre", "opponentNoteHit", "opponentNoteHitPre",
            "noteMiss", "noteMissPress", "onEvent", "onEventPushed", "onPause", "onResume", "onGameOver", "onDestroy", "onMoveCamera", "onSpawnNote",
            "onRecalculateRating", "onTimerCompleted", "onTweenCompleted", "onSoundFinished"
        )

        val unsupported = mapOf(
            "onKeyPress" to "Codename has no key press hook; check controls in update().",
            "onKeyPressPre" to "Codename has no key press hook; check controls in update().",
            "onKeyRelease" to "Codename has no key release hook; check controls in update().",
            "onKeyReleasePre" to "Codename has no key release hook; check controls in update().",
            "onGhostTap" to "Codename 通过 onPlayerMiss（event.ghostMiss）报告空按，noteMissPress 已经在用这个了。",
            "onUpdateScore" to "Codename 没有分数文本钩子。",
            "preUpdateScore" to "Codename 没有分数文本钩子。",
            "onGameOverStart" to "Codename 的死亡脚本是用 GameOverSubstate.script 设的。",
            "onGameOverConfirm" to "Codename 的死亡脚本是用 GameOverSubstate.script 设的。",
            "onNextDialogue" to "Codename 的对话是用 dialogue XML 文件写脚本的。",
            "onSkipDialogue" to "Codename 的对话是用 dialogue XML 文件写脚本的。",
            "eventEarlyTrigger" to "Codename 的事件总是按自己的时间跑。",
            "onCustomSubstateCreate" to "Psych 的自定义子状态在 Codename 里没有对应物（用 ModSubState）。",
            "onCustomSubstateCreatePost" to "Psych 的自定义子状态在 Codename 里没有对应物（用 ModSubState）。",
            "onCustomSubstateUpdate" to "Psych 的自定义子状态在 Codename 里没有对应物（用 ModSubState）。",
            "onCustomSubstateUpdatePost" to "Psych 的自定义子状态在 Codename 里没有对应物（用 ModSubState）。",
            "onCustomSubstateDestroy" to "Psych 的自定义子状态在 Codename 里没有对应物（用 ModSubState）。"
        )
    }

    fun name(psych: String): String = collisions[psych]?.takeIf { psych in defined } ?: psych

    private fun has(name: String) = name in defined

    private fun call(name: String, vararg args: String) = "${name(name)}(${args.joinToString(", ")});"

    private fun noteArgs(): Array<String> {
        if (!lua) return arrayOf("event.note")
        helpers += "noteType"
        return arrayOf("event.note", "event.direction", "psychNoteType(event.noteType)", "event.note.isSustainNote")
    }

    fun write(): String {
        val out = StringBuilder()
        fun fn(signature: String, lines: List<String>) {
            if (lines.isEmpty()) return
            out.append('\n').append("function ").append(signature).append("\n{\n")
            lines.forEach { out.append("  ").append(it).append('\n') }
            out.append("}\n")
        }
        fun hook(name: String) = hooks[name] ?: emptyList()

        defined.filter { it in unsupported }.forEach { warnings += "$it() never runs in Codename: ${unsupported.getValue(it)}" }

        fn("create()", hook("create") + listOfNotNull(if (has("onCreate")) call("onCreate") else null))
        val post = mutableListOf<String>()
        post += hook("postCreate")
        if (has("onEventPushed")) {
            helpers += "eventArgs"
            post += "for (e in PlayState.SONG.events)"
            post += "{"
            post += "  var args:Array<Dynamic> = psychEventArgs(e);"
            post += "  if (args != null) ${call("onEventPushed", "args[0]", "args[1]", "args[2]", "e.time")}"
            post += "}"
        }
        if (has("onCreatePost")) post += call("onCreatePost")
        fn("postCreate()", post)
        fn("update(elapsed:Float)", hook("update") + listOfNotNull(if (has("onUpdate")) call("onUpdate", "elapsed") else null))
        fn("postUpdate(elapsed:Float)", hook("postUpdate") + listOfNotNull(if (has("onUpdatePost")) call("onUpdatePost", "elapsed") else null))
        fn("beatHit(curBeat:Int)", hook("beatHit") + listOfNotNull(if (has("onBeatHit")) call("onBeatHit") else null))
        fn("stepHit(curStep:Int)", hook("stepHit") + listOfNotNull(if (has("onStepHit")) call("onStepHit") else null))
        fn("measureHit(curMeasure:Int)", hook("measureHit") + listOfNotNull(if (has("onSectionHit")) call("onSectionHit") else null))
        if (has("onEndSong")) fn("onSongEnd(event)", listOf("if (${name("onEndSong")}() == true) event.cancel();"))
        if (has("onStartCountdown")) fn("onStartCountdown(event)", listOf("if (${name("onStartCountdown")}() == true) event.cancel();"))
        if (has("onCountdownStarted")) fn("onPostStartCountdown(event)", listOf(call("onCountdownStarted")))
        if (has("onCountdownTick")) fn("onCountdown(event)", listOf(if (lua) call("onCountdownTick", "event.swagCounter") else call("onCountdownTick", "event.swagCounter", "event.swagCounter")))
        if (has("goodNoteHitPre")) fn("onPlayerHit(event)", listOf("if (${name("goodNoteHitPre")}(${noteArgs().joinToString(", ")}) == true) event.cancel();"))
        if (has("opponentNoteHitPre")) fn("onDadHit(event)", listOf("if (${name("opponentNoteHitPre")}(${noteArgs().joinToString(", ")}) == true) event.cancel();"))
        if (has("goodNoteHit") || has("opponentNoteHit")) {
            val lines = mutableListOf<String>()
            if (has("goodNoteHit")) lines += "if (event.player) " + call("goodNoteHit", *noteArgs())
            if (has("opponentNoteHit")) lines += (if (has("goodNoteHit")) "else " else "if (!event.player) ") + call("opponentNoteHit", *noteArgs())
            fn("onPostNoteHit(event)", lines)
        }
        if (has("noteMiss") || has("noteMissPress")) {
            val lines = mutableListOf<String>()
            if (has("noteMiss")) lines += "if (event.note != null) " + call("noteMiss", *noteArgs())
            if (has("noteMissPress")) lines += (if (has("noteMiss")) "else " else "if (event.note == null) ") + call("noteMissPress", "event.direction")
            fn("onPostPlayerMiss(event)", lines)
        }
        if (has("onEvent")) {
            helpers += "eventArgs"
            fn("onEvent(event)", listOf(
                "var args:Array<Dynamic> = psychEventArgs(event.event);",
                "if (args != null) ${call("onEvent", "args[0]", "args[1]", "args[2]", "event.event.time")}"
            ))
        }
        if (has("onPause")) fn("onGamePause(event)", listOf("if (${name("onPause")}() == true) event.cancel();"))
        if (has("onResume")) fn("onSubstateClose(event)", listOf(call("onResume")))
        if (has("onGameOver")) fn("onGameOver(event)", listOf("if (${name("onGameOver")}() == true) event.cancel();"))
        fn("destroy()", hook("destroy") + listOfNotNull(if (has("onDestroy")) call("onDestroy") else null))
        if (has("onMoveCamera")) {
            fields += "var psychLastFocus:String = null;"
            fn("onCameraMove(event)", listOf(
                "var lines:Array<Dynamic> = PlayState.instance.strumLines.members;",
                "var focus:String = event.strumLine == lines[1] ? \"boyfriend\" : (lines.length > 2 && event.strumLine == lines[2] ? \"gf\" : \"dad\");",
                "if (focus == psychLastFocus) return;",
                "psychLastFocus = focus;",
                call("onMoveCamera", "focus")
            ))
        }
        if (has("onSpawnNote")) {
            val args = if (lua) {
                helpers += "noteType"
                arrayOf("event.note", "event.note.noteData", "psychNoteType(event.note.noteType)", "event.note.isSustainNote", "event.note.strumTime")
            } else arrayOf("event.note")
            fn("onPostNoteCreation(event)", listOf(call("onSpawnNote", *args)))
        }
        if (has("onRecalculateRating")) fn("onRatingUpdate(event)", listOf("if (${name("onRecalculateRating")}() == true) event.cancel();"))
        hooks.filterKeys { it !in setOf("create", "postCreate", "update", "postUpdate", "beatHit", "stepHit", "measureHit", "destroy") }.forEach { (name, lines) ->
            fn("$name(event)", lines)
        }
        return out.toString()
    }
}
