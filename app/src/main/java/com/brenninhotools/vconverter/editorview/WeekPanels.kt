package com.brenninhotools.vconverter.editorview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brenninhotools.vconverter.editor.WeekDocument
import com.brenninhotools.vconverter.editor.WeekProp
import com.brenninhotools.vconverter.editor.WeekSlot
import com.brenninhotools.vconverter.overlay.soundClickable

@Composable
private fun WeekCheck(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(label, fontSize = 14.sp)
    }
}

private fun swatch(raw: String): Color {
    val value = raw.trim().removePrefix("#").toLongOrNull(16) ?: return Color.Gray
    return Color(0xFF000000 or (value and 0xFFFFFF))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekToolbar(state: WeekEditorState, modifier: Modifier = Modifier) {
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EditorChip("撤销", false) { if (state.canUndo) state.undo() }
        EditorChip("重做", false) { if (state.canRedo) state.redo() }
        EditorChip(if (state.playing) "暂停" else "播放", state.playing) { state.playing = !state.playing }
        EditorChip("确认", false) { state.confirm() }
        EditorChip("◀ 难度", false) { state.changeDifficulty(-1) }
        EditorChip("难度 ▶", false) { state.changeDifficulty(1) }
    }
}

@Composable
fun WeekInfoPanel(state: WeekEditorState, modifier: Modifier = Modifier) {
    val doc = state.document
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EditorSectionTitle("周目")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("周目 id", doc.id, Modifier.weight(1f)) { v ->
                val clean = v.trim().lowercase().replace(Regex("[^a-z0-9-]+"), "-").trim('-')
                if (clean.isNotEmpty()) state.edit { it.copy(id = clean) }
            }
            TextValueField("故事名", doc.name, Modifier.weight(1f)) { v -> state.edit { it.copy(name = v) } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("周目名（Psych 自由游玩）", doc.weekName, Modifier.weight(1f)) { v -> state.edit { it.copy(weekName = v) } }
            TextValueField("标题图（images/…）", doc.titleAsset.ifBlank { doc.title }, Modifier.weight(1f)) { v -> state.edit { it.copy(titleAsset = v.trim()) } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextValueField("背景（#RRGGBB 或 images/…）", doc.background, Modifier.weight(1f)) { v -> state.edit { it.copy(background = v.trim()) } }
            if (doc.backgroundIsColor) {
                Row(Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(swatch(doc.background))) {}
            }
        }
        TextValueField("难度（逗号分隔）", doc.difficulties.joinToString(", "), Modifier.fillMaxWidth()) { v ->
            val list = v.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.distinct()
            if (list.isNotEmpty()) state.edit { it.copy(difficulties = list) }
        }
        Row {
            WeekCheck("在故事模式中显示", doc.visible) { v -> state.edit { it.copy(visible = v) } }
            WeekCheck("从自由游玩中隐藏歌曲", doc.hideFreeplay) { v -> state.edit { it.copy(hideFreeplay = v) } }
        }
        EditorSectionTitle("解锁")
        TextValueField("前置周目（weekBefore）", doc.weekBefore, Modifier.fillMaxWidth()) { v -> state.edit { it.copy(weekBefore = v.trim()) } }
        Row {
            WeekCheck("初始解锁", doc.startUnlocked) { v -> state.edit { it.copy(startUnlocked = v) } }
            WeekCheck("解锁前隐藏", doc.hiddenUntilUnlocked) { v -> state.edit { it.copy(hiddenUntilUnlocked = v) } }
        }
        Text(
            if (doc.isLocked) "Locked until \"${doc.weekBefore}\" is beaten. V-Slice gets a level script for it (scripts/levels/), Psych keeps it in the week file; Codename has no week locking."
            else "关掉「初始解锁」再填一个周目名，就能把这个周目锁到那个周目通关为止。",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekSongsPanel(state: WeekEditorState, modifier: Modifier = Modifier) {
    val doc = state.document
    val scheme = MaterialTheme.colorScheme
    var draft by remember { mutableStateOf("") }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EditorSectionTitle("Songs (${doc.songs.size})")
        doc.songs.forEachIndexed { index, song ->
            val active = index == state.selectedSong
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(if (active) scheme.primary else scheme.surfaceVariant)
                    .soundClickable { state.selectedSong = if (active) -1 else index }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(swatch(song.color))) {}
                Column(Modifier.weight(1f)) {
                    Text("${index + 1}. ${song.displayName}", color = if (active) scheme.onPrimary else scheme.onSurface, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${song.id} · icon ${song.icon.ifBlank { "face" }}", color = if (active) scheme.onPrimary.copy(alpha = 0.75f) else scheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        if (doc.songs.isEmpty()) Text("还没有歌曲：把这一周的歌曲按播放顺序加到下面。", fontSize = 13.sp, color = scheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextValueField("歌曲名", draft, Modifier.weight(1f)) { draft = it }
            EditorChip("添加歌曲", false) {
                state.addSong(draft)
                draft = ""
            }
        }
        val index = state.selectedSong
        val song = doc.songs.getOrNull(index)
        if (song != null) {
            EditorSectionTitle(song.displayName)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextValueField("歌曲 ID（data/songs/…）", song.id, Modifier.weight(1f)) { v ->
                    val clean = v.trim().lowercase().replace(Regex("[^a-z0-9-]+"), "-").trim('-')
                    if (clean.isNotEmpty() && doc.songs.none { it !== song && it.id == clean }) state.edit { it.replaceSong(index, song.copy(id = clean)) }
                }
                TextValueField("显示名", song.name, Modifier.weight(1f)) { v -> state.edit { it.replaceSong(index, song.copy(name = v)) } }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextValueField("自由游玩图标", song.icon, Modifier.weight(1f)) { v -> state.edit { it.replaceSong(index, song.copy(icon = v.trim())) } }
                TextValueField("自由游玩颜色（#RRGGBB）", song.color, Modifier.weight(1f)) { v ->
                    val hex = v.trim().removePrefix("#")
                    if (hex.matches(Regex("[0-9A-Fa-f]{6}"))) state.edit { it.replaceSong(index, song.copy(color = "#" + hex.uppercase())) }
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                EditorChip("上移", false) { state.moveSong(index, -1) }
                EditorChip("下移", false) { state.moveSong(index, 1) }
                EditorChip("移除", false) { state.removeSong(index) }
            }
            Text("图标和颜色供 Psych 的自由游玩使用；V-Slice 读歌曲自己的元数据。", fontSize = 12.sp, color = scheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekPropsPanel(state: WeekEditorState, modifier: Modifier = Modifier, onLoadImage: ((WeekSlot) -> Unit)? = null) {
    val doc = state.document
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EditorSectionTitle("菜单人物")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            WeekSlot.entries.forEach { slot ->
                val prop = doc.prop(slot)
                EditorChip("${slot.label}: ${prop?.let { if (it.isBase) "base ${slot.key}" else it.assetPath.substringAfterLast('/') } ?: "none"}", state.selected == slot) {
                    state.selected = if (state.selected == slot) null else slot
                }
            }
        }
        val slot = state.selected
        if (slot == null) {
            Text(
                "点预览里（或上方）的人物来编辑，然后拖动它移动。每个站位依次向右挪四分之一屏宽，跟 V-Slice 的故事菜单一致。",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }
        val prop = doc.prop(slot)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            EditorChip("无", prop == null) { state.edit { it.withProp(slot, null) } }
            EditorChip("Base ${slot.key}", prop?.isBase == true) { state.edit { it.withProp(slot, WeekProp.base(slot)) } }
            EditorChip("自定义", prop != null && !prop.isBase) {
                if (prop == null || prop.isBase) {
                    val base = prop ?: WeekProp.base(slot)
                    state.edit { it.withProp(slot, WeekProp("storymenu/props/${it.id}-${slot.key}", offsetX = base.offsetX, offsetY = base.offsetY)) }
                }
            }
            if (onLoadImage != null) EditorChip("加载图片…", false) { onLoadImage(slot) }
        }
        if (prop == null) return@Column
        PropFields(state, slot, prop)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            EditorChip("← 1", false) { state.nudge(-1.0, 0.0) }
            EditorChip("→ 1", false) { state.nudge(1.0, 0.0) }
            EditorChip("↑ 1", false) { state.nudge(0.0, -1.0) }
            EditorChip("↓ 1", false) { state.nudge(0.0, 1.0) }
            EditorChip("← 10", false) { state.nudge(-10.0, 0.0) }
            EditorChip("→ 10", false) { state.nudge(10.0, 0.0) }
            EditorChip("↑ 10", false) { state.nudge(0.0, -10.0) }
            EditorChip("↓ 10", false) { state.nudge(0.0, 10.0) }
        }
        if (state.imageFor(prop.assetPath) == null) {
            Text(
                if (prop.isBase) "基础游戏的精灵没有打包进来，所以显示成方块；它不会被导出（游戏里本来就有）。"
                else "No image loaded for images/${prop.assetPath}.png yet: use \"Load image…\" with the .png and its .xml.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PropFields(state: WeekEditorState, slot: WeekSlot, prop: WeekProp) {
    fun update(change: (WeekProp) -> WeekProp) = state.updateProp(slot, change)
    TextValueField("图片（images/…）", prop.assetPath, Modifier.fillMaxWidth()) { v -> if (v.isNotBlank()) update { it.copy(assetPath = v.trim()) } }
    val prefixes = state.imageFor(prop.assetPath)?.atlas?.prefixes() ?: emptyList()
    val idle = prop.anim(prop.idleName)?.prefix ?: ""
    val confirm = prop.anim("confirm")?.prefix ?: ""
    if (prefixes.isNotEmpty()) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChoiceField("待机动画", idle, prefixes.map { it to it }, Modifier.weight(1f)) { v -> update { it.withPrefix(it.idleName, v) } }
            ChoiceField("确认动画", confirm, listOf("" to "无") + prefixes.map { it to it }, Modifier.weight(1f)) { v -> update { it.withPrefix("confirm", v) } }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("待机前缀", idle, Modifier.weight(1f)) { v -> update { it.withPrefix(it.idleName, v) } }
            TextValueField("确认前缀", confirm, Modifier.weight(1f)) { v -> update { it.withPrefix("confirm", v) } }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("偏移 X", prop.offsetX, Modifier.weight(1f)) { v -> update { it.copy(offsetX = v) } }
        NumberField("偏移 Y", prop.offsetY, Modifier.weight(1f)) { v -> update { it.copy(offsetY = v) } }
        NumberField("缩放", prop.scale, Modifier.weight(1f)) { v -> if (v > 0) update { it.copy(scale = v) } }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        NumberField("待机间隔（拍）", prop.danceEvery, Modifier.weight(1f)) { v -> if (v >= 0) update { it.copy(danceEvery = v) } }
        WeekCheck("像素", prop.isPixel) { v -> update { it.copy(isPixel = v) } }
        WeekCheck("水平翻转", prop.flipX) { v -> update { it.copy(flipX = v) } }
    }
    if (confirm.isNotEmpty()) {
        Text("点预览上方的「确认」来播放确认动画。", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun weekSummary(doc: WeekDocument): String =
    "${doc.songs.size} song(s) · ${WeekSlot.entries.count { doc.prop(it) != null }} menu character(s)" + if (doc.isLocked) " · locked" else ""
