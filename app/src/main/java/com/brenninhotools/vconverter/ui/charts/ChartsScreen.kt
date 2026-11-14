package com.brenninhotools.vconverter.ui.charts

import com.brenninhotools.vconverter.overlay.soundClickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brenninhotools.vconverter.R
import com.brenninhotools.vconverter.chart.preview.ChartEngine
import com.brenninhotools.vconverter.chart.preview.ChartField
import com.brenninhotools.vconverter.chart.preview.ChartPreview
import com.brenninhotools.vconverter.chartview.ChartStrip
import com.brenninhotools.vconverter.chartview.ChartStripColors
import com.brenninhotools.vconverter.chartview.formatBpm
import com.brenninhotools.vconverter.chartview.formatDuration
import com.brenninhotools.vconverter.ui.ConvertSection
import com.brenninhotools.vconverter.ui.components.Chip
import com.brenninhotools.vconverter.ui.components.DropZone
import com.brenninhotools.vconverter.ui.components.FieldLabel
import com.brenninhotools.vconverter.ui.components.FileItem
import com.brenninhotools.vconverter.ui.components.FunkinCard
import com.brenninhotools.vconverter.ui.components.FunkinTextField
import com.brenninhotools.vconverter.ui.components.NumberField
import com.brenninhotools.vconverter.ui.components.OutlineButton
import com.brenninhotools.vconverter.ui.components.SectionTitle
import com.brenninhotools.vconverter.ui.components.Tag
import com.brenninhotools.vconverter.ui.components.ToggleRow
import com.brenninhotools.vconverter.ui.theme.Funkin

val FunkinStripColors = ChartStripColors(
    background = Funkin.Ink,
    lane = Funkin.SurfaceHigh.copy(alpha = 0.55f),
    grid = Funkin.Outline,
    text = Funkin.Text,
    muted = Funkin.Muted,
    left = Funkin.Left,
    down = Funkin.Down,
    up = Funkin.Up,
    right = Funkin.Right,
    opponent = Funkin.Orange,
    player = Funkin.Down,
    girlfriend = Funkin.Pink,
    event = Funkin.Warning
)

private fun dropTitle(engine: ChartEngine): String = when (engine) {
    ChartEngine.PSYCH -> "添加 Psych 谱子"
    ChartEngine.VSLICE -> "添加 V-Slice 歌曲"
    ChartEngine.CODENAME -> "添加 Codename 谱子"
}

private fun dropHint(source: ChartEngine, target: ChartEngine): String = when (source) {
    ChartEngine.PSYCH -> "选一首歌的全部难度（data/<歌曲>/<歌曲>-<难度>.json）和它的 events.json。多首歌、文件夹和 .zip/.rar/.7z 也都可以。"
    ChartEngine.VSLICE -> "选择 <歌曲>-metadata.json 和 <歌曲>-chart.json，以及它们的 -erect 变体文件。变体会变成独立的 Psych 歌曲。"
    ChartEngine.CODENAME -> "选择 songs/<歌曲>/charts/*.json，连同 meta.json 和 events.json（变体在 charts/<变体>/ 里）。" +
        if (target == ChartEngine.PSYCH) " Add data/events/*.json for custom event params." else ""
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartsScreen(viewModel: ChartsViewModel, modFolderName: String?, onSave: () -> Unit) {
    val accent = Funkin.Down
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.add(it) }
    val busy = viewModel.busy
    val hasCharts = viewModel.files.any { it.kind == "chart" }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FunkinCard {
            SectionTitle("Charts", accent, 1)
            DropZone(
                title = dropTitle(viewModel.source),
                hint = dropHint(viewModel.source, viewModel.target),
                accent = accent,
                icon = R.drawable.ic_tab_chart,
                enabled = !busy,
                onClick = { picker.launch(arrayOf("*/*")) }
            )
            val groups = viewModel.files.withIndex().groupBy { viewModel.songOf(it.value) }
            groups.forEach { (song, entries) ->
                if (song.isNotEmpty()) SongHeader(song.ifEmpty { "Other files" }, entries.count { it.value.kind == "chart" }, accent)
                entries.forEach { (index, item) ->
                    val difficulty = viewModel.difficultyOf(item)
                    val preview = viewModel.before.firstOrNull { difficulty != null && it.difficulty == difficulty && viewModel.songOf(item).let { s -> s.isEmpty() || it.song == s } }
                    FileItem(
                        title = item.name,
                        accent = kindColor(item.kind, accent),
                        enabled = !busy,
                        onRemove = { viewModel.remove(index) },
                        tags = {
                            Tag(kindLabel(item.kind), kindColor(item.kind, accent))
                            if (difficulty != null) Tag(difficulty, accent) else if (item.label.isNotBlank()) Tag(item.label, Funkin.Muted)
                            if (preview != null) {
                                Tag("${preview.notes.size} notes", Funkin.Muted)
                                Tag("${formatBpm(preview.bpmRange.first)} BPM", Funkin.Muted)
                                Tag("Speed ${formatBpm(preview.speed)}", Funkin.Muted)
                            }
                        }
                    ) {
                        if (viewModel.canRenameDifficulty && difficulty != null) {
                            var text by remember(item.key) { mutableStateOf(difficulty) }
                            FunkinTextField(
                                value = text,
                                onValueChange = {
                                    text = it
                                    if (it.isNotBlank()) viewModel.setDifficulty(index, it)
                                },
                                label = "Difficulty",
                                accent = accent,
                                enabled = !busy
                            )
                        }
                    }
                }
            }
            if (viewModel.files.isNotEmpty()) {
                OutlineButton("Clear all", Funkin.Muted, viewModel::clear, enabled = !busy, icon = R.drawable.ic_close)
            }
        }

        AnimatedVisibility(visible = viewModel.before.isNotEmpty(), enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            PreviewCard(viewModel, accent)
        }

        AnimatedVisibility(visible = hasCharts, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            OptionsCard(viewModel, accent, busy)
        }

        ConvertSection(
            viewModel = viewModel,
            accent = accent,
            step = if (hasCharts) 4 else 2,
            canConvert = viewModel.canConvert,
            onConvert = viewModel::convert,
            onSave = onSave,
            modFolderName = modFolderName,
            buttonText = "转换为 \${viewModel.target.title}"
        )
    }
}

@Composable
private fun SongHeader(song: String, charts: Int, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Icon(painterResource(R.drawable.ic_folder), contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(song, style = MaterialTheme.typography.titleSmall, color = Funkin.Text, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (charts > 0) Text("$charts chart${if (charts == 1) "" else "s"}", style = MaterialTheme.typography.labelSmall, color = Funkin.Muted)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreviewCard(viewModel: ChartsViewModel, accent: Color) {
    FunkinCard {
        SectionTitle("Preview", accent, 2)
        if (viewModel.after.isNotEmpty()) {
            Segmented(
                options = listOf("${viewModel.source.title} (before)", "${viewModel.target.title} (after)"),
                selected = if (viewModel.showAfter) 1 else 0,
                accent = accent
            ) { viewModel.showAfter = it == 1 }
        }
        val before = viewModel.before
        if (before.size > 1) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val multipleSongs = before.map { it.song }.distinct().size > 1
                before.forEach { preview ->
                    Chip(
                        text = if (multipleSongs) "${preview.song} · ${preview.label}" else preview.label,
                        selected = viewModel.selectedBefore == preview,
                        accent = accent
                    ) { viewModel.select(preview) }
                }
            }
        }
        val current = viewModel.current ?: return@FunkinCard
        val other = if (viewModel.showAfter) viewModel.selectedBefore else viewModel.selectedAfter
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${current.song} · ${current.label}",
                style = MaterialTheme.typography.titleSmall,
                color = Funkin.Text,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Chip(if (viewModel.zoomed) "Fit" else "Zoom", viewModel.zoomed, accent) { viewModel.zoomed = !viewModel.zoomed }
        }
        ChartStrip(current, FunkinStripColors, pixelsPerSecond = if (viewModel.zoomed) 70f else null)
        Legend(current)
        StatsGrid(current, accent)
        if (viewModel.showAfter && other != null) CompareRow(other, current)
    }
}

@Composable
private fun Segmented(options: List<String>, selected: Int, accent: Color, onSelect: (Int) -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Funkin.Ink).border(1.5.dp, Funkin.Outline, shape).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, label ->
            val active = index == selected
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (active) accent else Color.Transparent)
                    .soundClickable { onSelect(index) }.padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = if (active) Funkin.Ink else Funkin.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Legend(preview: ChartPreview) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LegendDot(FunkinStripColors.opponent, "Opponent${if (preview.opponent.isNotBlank()) " · ${preview.opponent}" else ""}")
        LegendDot(FunkinStripColors.player, "Player${if (preview.player.isNotBlank()) " · ${preview.player}" else ""}")
        if (preview.otherNotes > 0 || preview.focus.any { it.side == 2 }) LegendDot(FunkinStripColors.girlfriend, "Girlfriend")
        LegendDot(FunkinStripColors.event, "Events")
    }
}

@Composable
private fun LegendDot(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(5.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = Funkin.Muted, maxLines = 1)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsGrid(preview: ChartPreview, accent: Color) {
    val (low, high) = preview.bpmRange
    val stats = listOf(
        "玩家音符" to preview.playerNotes.toString(),
        "对手音符" to preview.opponentNotes.toString(),
        "Holds" to preview.holdNotes.toString(),
        "Peak NPS" to formatBpm(preview.peakNps),
        "Length" to formatDuration(preview.length),
        "BPM" to if (low == high) formatBpm(low) else "${formatBpm(low)}–${formatBpm(high)}",
        "Time sig." to preview.signatures.joinToString(" · ") { "$it/4" },
        "Events" to preview.events.size.toString(),
        "Speed" to formatBpm(preview.speed)
    ) + listOfNotNull(
        if (preview.otherNotes > 0) "女友音符" to preview.otherNotes.toString() else null,
        if (preview.duplicates > 0) "叠在一起的重复音符" to preview.duplicates.toString() else null,
        if (preview.offset != 0.0) "Offset" to "${formatBpm(preview.offset)} ms" else null,
        if (preview.stage.isNotBlank()) "Stage" to preview.stage else null
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.forEach { (label, value) ->
            Column(
                Modifier.clip(RoundedCornerShape(12.dp)).background(Funkin.SurfaceHigh).padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(value, style = MaterialTheme.typography.titleSmall, color = accent, maxLines = 1)
                Text(label, style = MaterialTheme.typography.labelSmall, color = Funkin.Muted, maxLines = 1)
            }
        }
    }
}

@Composable
private fun CompareRow(before: ChartPreview, after: ChartPreview) {
    val checks = listOf(
        "玩家音符" to (before.playerNotes to after.playerNotes),
        "对手音符" to (before.opponentNotes to after.opponentNotes),
        "Holds" to (before.holdNotes to after.holdNotes),
        "BPM changes" to (before.timing.size to after.timing.size)
    )
    val differences = checks.filter { it.second.first != it.second.second }
    val shape = RoundedCornerShape(14.dp)
    val color = if (differences.isEmpty()) Funkin.Up else Funkin.Warning
    Column(
        Modifier.fillMaxWidth().clip(shape).background(color.copy(alpha = 0.10f)).border(1.5.dp, color.copy(alpha = 0.5f), shape).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(if (differences.isEmpty()) R.drawable.ic_check else R.drawable.ic_warning), contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                if (differences.isEmpty()) "所有音符、长条和 BPM 变化都完整过来了。" else "转换后有些数量变了：",
                style = MaterialTheme.typography.bodySmall,
                color = Funkin.Text
            )
        }
        differences.forEach { (label, pair) ->
            Text("• $label: ${pair.first} → ${pair.second}", style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
        }
    }
}

@Composable
private fun OptionsCard(viewModel: ChartsViewModel, accent: Color, busy: Boolean) {
    val fields = viewModel.fields
    val tweaks = viewModel.tweaks
    val sample = viewModel.before.firstOrNull()
    FunkinCard {
        SectionTitle("选项", accent, 3)
        Text("留空就保持谱子原来的设置。", style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
        if (ChartField.SONG_NAME in fields) {
            FunkinTextField(tweaks.songName, { v -> viewModel.updateTweaks { it.copy(songName = v) } }, "歌曲名", accent, enabled = !busy)
        }
        if (ChartField.ARTIST in fields) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FunkinTextField(tweaks.artist, { v -> viewModel.updateTweaks { it.copy(artist = v) } }, "Artist", accent, Modifier.weight(1f), enabled = !busy)
                FunkinTextField(tweaks.charter, { v -> viewModel.updateTweaks { it.copy(charter = v) } }, "Charter", accent, Modifier.weight(1f), enabled = !busy)
            }
        }
        if (ChartField.CHARACTERS in fields) {
            FieldLabel("Characters")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FunkinTextField(tweaks.player, { v -> viewModel.updateTweaks { it.copy(player = v.trim()) } }, "Player", accent, Modifier.weight(1f), enabled = !busy, placeholder = sample?.player)
                FunkinTextField(tweaks.opponent, { v -> viewModel.updateTweaks { it.copy(opponent = v.trim()) } }, "Opponent", accent, Modifier.weight(1f), enabled = !busy, placeholder = sample?.opponent)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FunkinTextField(tweaks.girlfriend, { v -> viewModel.updateTweaks { it.copy(girlfriend = v.trim()) } }, "Girlfriend", accent, Modifier.weight(1f), enabled = !busy, placeholder = sample?.girlfriend)
                if (ChartField.STAGE in fields) {
                    FunkinTextField(tweaks.stage, { v -> viewModel.updateTweaks { it.copy(stage = v.trim()) } }, "Stage", accent, Modifier.weight(1f), enabled = !busy, placeholder = sample?.stage)
                }
            }
        }
        if (ChartField.NOTE_STYLE in fields) {
            FieldLabel("Note style")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("" to "Auto", "funkin" to "Funkin", "pixel" to "Pixel").forEach { (value, label) ->
                    Chip(label, tweaks.noteStyle == value, accent, !busy) { viewModel.updateTweaks { it.copy(noteStyle = value) } }
                }
            }
        }
        if (ChartField.SPEED in fields && viewModel.difficulties.isNotEmpty()) {
            FieldLabel("滚动速度")
            viewModel.difficulties.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { difficulty ->
                        val detected = viewModel.before.firstOrNull { it.difficulty == difficulty }?.speed
                        var text by remember(difficulty, tweaks.speeds.containsKey(difficulty)) { mutableStateOf(tweaks.speeds[difficulty]?.let(::formatBpm) ?: "") }
                        NumberField(
                            value = text,
                            onValueChange = {
                                text = it
                                viewModel.setSpeed(difficulty, it)
                            },
                            label = difficulty.replaceFirstChar { c -> c.uppercase() } + (detected?.let { " (${formatBpm(it)})" } ?: ""),
                            accent = accent,
                            modifier = Modifier.weight(1f),
                            enabled = !busy
                        )
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        if (ChartField.KEEP_EVENTS in fields) {
            ToggleRow(
                label = "保留仅 Psych 的事件",
                checked = tweaks.keepUnknownEvents,
                onChange = { value -> viewModel.updateTweaks { it.copy(keepUnknownEvents = value) } },
                accent = accent,
                hint = "没有 V-Slice 对应物的事件会以自己的 Psych 名字留在谱子里，内置的那些会配 SongEvent 脚本。",
                enabled = !busy
            )
        }
        if (!tweaks.isEmpty) {
            OutlineButton("重置选项", Funkin.Muted, viewModel::resetTweaks, enabled = !busy)
        }
    }
}

private fun kindLabel(kind: String): String = when (kind) {
    "metadata" -> "Metadata"
    "chart" -> "Chart"
    "events" -> "Events"
    "definition" -> "事件参数"
    else -> kind
}

private fun kindColor(kind: String, accent: Color): Color = when (kind) {
    "metadata", "events", "definition" -> Funkin.Warning
    else -> accent
}
