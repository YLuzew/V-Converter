package com.brenninhotools.vconverter.chart

import com.brenninhotools.vconverter.core.Report
import com.brenninhotools.vconverter.core.ReportBuilder
import com.brenninhotools.vconverter.core.fmt
import com.brenninhotools.vconverter.core.sortedMutableMap
import com.brenninhotools.vconverter.core.sortedMutableSet
import com.brenninhotools.vconverter.core.CaseInsensitiveOrder

class ConversionReport {
    val noteCounts = LinkedHashMap<String, Int>()
    val duplicateNotes = LinkedHashMap<String, Int>()
    val unsupportedEvents = sortedMutableMap<String, Int>(CaseInsensitiveOrder)
    val noteKinds = sortedMutableSet<String>()
    val warnings = mutableListOf<String>()
    var cameraEvents = 0
    var convertedEvents = 0
    var keptPsychEvents = 0
    var offsetApplied = 0.0
    val variations = LinkedHashMap<String, List<String>>()

    fun warn(message: String) {
        if (message !in warnings) warnings += message
    }

    fun skipEvent(name: String) {
        unsupportedEvents[name] = (unsupportedEvents[name] ?: 0) + 1
    }

    fun summary(): String = buildString {
        appendLine("Notes")
        noteCounts.forEach { (difficulty, count) ->
            val dupes = duplicateNotes[difficulty] ?: 0
            append("  • ").append(difficulty).append(": ").append(count)
            if (dupes > 0) append(" (").append(dupes).append(" stacked duplicates removed)")
            appendLine()
        }
        appendLine()
        appendLine("Events")
        appendLine("  • Camera focus events generated from sections: $cameraEvents")
        appendLine("  • Psych events converted: $convertedEvents")
        if (unsupportedEvents.isNotEmpty()) {
            appendLine("  • Skipped (no V-Slice equivalent yet):")
            unsupportedEvents.forEach { (name, count) -> appendLine("      - $name ×$count") }
        }
        if (noteKinds.isNotEmpty()) {
            appendLine()
            appendLine("Note kinds used")
            appendLine("  ${noteKinds.joinToString(", ")}")
            appendLine("  V-Slice only reacts to a note kind when a NoteKind script with that id exists in your mod.")
        }
        if (warnings.isNotEmpty()) {
            appendLine()
            appendLine("Warnings")
            warnings.forEach { appendLine("  • $it") }
        }
    }.trimEnd()

    fun toReport(): Report {
        val builder = ReportBuilder()
        noteCounts.forEach { (difficulty, count) -> builder.stat(difficulty.replaceFirstChar { it.titlecase() }, count) }
        builder.stat("Events", cameraEvents + convertedEvents)
        builder.section(
            "Events",
            listOf(
                "$cameraEvents camera focus events generated from sections",
                "$convertedEvents Psych events converted"
            )
        )
        builder.section(
            if (keptPsychEvents > 0) "保留 Psych 的原名（由脚本和生成的 SongEvents 处理）" else "跳过的事件（V-Slice 里还没有对应物）",
            unsupportedEvents.map { (name, count) -> "$name ×$count" }
        )
        builder.section(
            "去掉的重复项",
            duplicateNotes.map { (difficulty, count) -> "$difficulty: $count stacked notes" }
        )
        if (variations.isNotEmpty()) {
            builder.section("Variations", variations.map { (name, difficulties) -> "$name: ${difficulties.joinToString(", ")}" })
        }
        if (offsetApplied != 0.0) {
            builder.section("音频偏移", listOf("The Psych chart offset (${formatMs(offsetApplied)}) was written to the metadata's instrumental offset."))
        }
        if (noteKinds.isNotEmpty()) {
            builder.section(
                "Note kinds",
                listOf(
                    noteKinds.joinToString(", "),
                    "只有模组里存在对应 id 的 NoteKind 脚本时，V-Slice 才会响应这种音符类型。"
                )
            )
        }
        warnings.forEach(builder::warn)
        return builder.build()
    }

    companion object {
        fun formatMs(value: Double): String = fmt("%.0f ms", value)
    }
}
