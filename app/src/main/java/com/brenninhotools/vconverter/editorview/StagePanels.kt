package com.brenninhotools.vconverter.editorview

import com.brenninhotools.vconverter.overlay.soundClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brenninhotools.vconverter.editor.StageDocument
import com.brenninhotools.vconverter.editor.StageProp
import com.brenninhotools.vconverter.editor.StageSlot
import com.brenninhotools.vconverter.editor.StageSpot

@Composable
private fun StageCheck(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(label, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StageToolbar(state: StageEditorState, modifier: Modifier = Modifier) {
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EditorChip("撤销", false) { if (state.canUndo) state.undo() }
        EditorChip("重做", false) { if (state.canRedo) state.redo() }
        EditorChip("缩小", false) { state.zoom = (state.zoom / 1.25f).coerceAtLeast(0.05f) }
        EditorChip("放大", false) { state.zoom = (state.zoom * 1.25f).coerceAtMost(4f) }
        EditorChip("重置视图", false) { state.resetView() }
        EditorChip("视差", state.parallax) { state.parallax = !state.parallax }
        EditorChip("摄像机", state.showCamera) { state.showCamera = !state.showCamera }
    }
}

@Composable
fun StageInfoPanel(state: StageEditorState, modifier: Modifier = Modifier) {
    val doc = state.document
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EditorSectionTitle("背景")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("背景 ID", doc.id, Modifier.weight(1f)) { v ->
                val clean = v.trim().lowercase().replace(Regex("[^a-z0-9-]+"), "-").trim('-')
                if (clean.isNotEmpty()) state.edit { it.copy(id = clean) }
            }
            TextValueField("名字", doc.name, Modifier.weight(1f)) { v -> state.edit { it.copy(name = v) } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            NumberField("摄像机缩放", doc.cameraZoom, Modifier.weight(1f)) { v -> if (v > 0) state.edit { it.copy(cameraZoom = v) } }
            TextValueField("资源库", doc.directory, Modifier.weight(1f)) { v -> state.edit { it.copy(directory = v.trim()) } }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StageLayersPanel(state: StageEditorState, modifier: Modifier = Modifier, onAddImage: (() -> Unit)? = null) {
    val doc = state.document
    val scheme = MaterialTheme.colorScheme
    var color by remember { mutableStateOf("#000000") }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EditorSectionTitle("图层（最上层在前）")
        state.drawOrder().asReversed().forEach { item ->
            val active = item == state.selection
            val (title, detail) = when (item) {
                is StageSelection.Prop -> doc.props[item.index].let { it.name to "z ${it.zIndex} · ${it.assetPath}" }
                is StageSelection.Spot -> item.slot.label to "z ${doc.spot(item.slot).zIndex} · character"
            }
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(if (active) scheme.primary else if (item is StageSelection.Spot) scheme.secondaryContainer else scheme.surfaceVariant)
                    .soundClickable { state.focus(item) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = if (active) scheme.onPrimary else scheme.onSurface, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(detail, color = if (active) scheme.onPrimary.copy(alpha = 0.75f) else scheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (item is StageSelection.Prop && !doc.props[item.index].isSolid && state.imageFor(doc.props[item.index]) == null) {
                    Text("没有图片", color = if (active) scheme.onPrimary else scheme.error, fontSize = 11.sp)
                }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
            if (onAddImage != null) EditorChip("加图片", false, onAddImage)
            EditorChip("上移一层", false) { state.moveLayer(1) }
            EditorChip("下移一层", false) { state.moveLayer(-1) }
            EditorChip("复制一份", false) { state.duplicateSelected() }
            EditorChip("删除", false) { state.deleteSelected() }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("纯色（#RRGGBB）", color, Modifier.weight(1f)) { color = it }
            EditorChip("添加颜色", false) {
                val hex = color.trim().removePrefix("#")
                if (hex.matches(Regex("[0-9A-Fa-f]{6}"))) {
                    val bf = state.document.spot(StageSlot.BOYFRIEND)
                    state.addProp(StageProp("background", "#" + hex.uppercase(), bf.x - 1600, bf.y - 1300, 0, 3200.0, 1800.0), autoLayer = false)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StageSelectionPanel(state: StageEditorState, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        when (val sel = state.selection) {
            null -> {
                EditorSectionTitle("选择")
                Text(
                    "点中一个物件或人物就能选中它，然后拖动它移动。在别处拖动是移动视角，滚轮是缩放。虚线框是游戏摄像机在背景缩放下的范围。",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            is StageSelection.Prop -> state.document.props.getOrNull(sel.index)?.let { PropEditor(state, sel.index, it) }
            is StageSelection.Spot -> SpotEditor(state, sel.slot, state.document.spot(sel.slot))
        }
        if (state.selection != null) {
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
        }
    }
}

@Composable
private fun PropEditor(state: StageEditorState, index: Int, prop: StageProp) {
    fun update(change: (StageProp) -> StageProp) = state.edit { doc: StageDocument -> doc.replaceProp(index, change(doc.props[index])) }
    EditorSectionTitle("物件")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        TextValueField("名字", prop.name, Modifier.weight(1f)) { v ->
            val clean = v.trim()
            if (clean.isNotEmpty() && state.document.props.none { it !== prop && it.name == clean }) update { it.copy(name = clean) }
        }
        NumberField("图层（z）", prop.zIndex.toDouble(), Modifier.weight(0.6f)) { v -> update { it.copy(zIndex = v.toInt()) } }
    }
    TextValueField(if (prop.isSolid) "颜色（#RRGGBB）" else "图片（images/…）", prop.assetPath, Modifier.fillMaxWidth()) { v -> update { it.copy(assetPath = v.trim()) } }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("X", prop.x, Modifier.weight(1f)) { v -> update { it.copy(x = v) } }
        NumberField("Y", prop.y, Modifier.weight(1f)) { v -> update { it.copy(y = v) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField(if (prop.isSolid) "宽" else "缩放 X", prop.scaleX, Modifier.weight(1f)) { v -> update { it.copy(scaleX = v) } }
        NumberField(if (prop.isSolid) "高" else "缩放 Y", prop.scaleY, Modifier.weight(1f)) { v -> update { it.copy(scaleY = v) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("滚动 X", prop.scrollX, Modifier.weight(1f)) { v -> update { it.copy(scrollX = v) } }
        NumberField("滚动 Y", prop.scrollY, Modifier.weight(1f)) { v -> update { it.copy(scrollY = v) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("透明度", prop.alpha, Modifier.weight(1f)) { v -> update { it.copy(alpha = v.coerceIn(0.0, 1.0)) } }
        NumberField("角度", prop.angle, Modifier.weight(1f)) { v -> update { it.copy(angle = v) } }
    }
    if (!prop.isSolid) {
        val prefixes = state.imageFor(prop)?.atlas?.prefixes() ?: emptyList()
        if (prefixes.isNotEmpty()) {
            ChoiceField("动画", prop.prefix, listOf("" to "静态图片") + prefixes.map { it to it }, Modifier.fillMaxWidth()) { v -> update { it.copy(prefix = v) } }
        }
        Row {
            StageCheck("水平翻转", prop.flipX) { v -> update { it.copy(flipX = v) } }
            StageCheck("垂直翻转", prop.flipY) { v -> update { it.copy(flipY = v) } }
            StageCheck("像素", prop.isPixel) { v -> update { it.copy(isPixel = v) } }
        }
    }
}

@Composable
private fun SpotEditor(state: StageEditorState, slot: StageSlot, spot: StageSpot) {
    fun update(change: (StageSpot) -> StageSpot) = state.edit { it.withSpot(slot, change(it.spot(slot))) }
    EditorSectionTitle(slot.label)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("X（脚底）", spot.x, Modifier.weight(1f)) { v -> update { it.copy(x = v) } }
        NumberField("Y（脚底）", spot.y, Modifier.weight(1f)) { v -> update { it.copy(y = v) } }
        NumberField("图层（z）", spot.zIndex.toDouble(), Modifier.weight(0.7f)) { v -> update { it.copy(zIndex = v.toInt()) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("摄像机 X", spot.cameraX, Modifier.weight(1f)) { v -> update { it.copy(cameraX = v) } }
        NumberField("摄像机 Y", spot.cameraY, Modifier.weight(1f)) { v -> update { it.copy(cameraY = v) } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        NumberField("滚动", spot.scroll, Modifier.weight(1f)) { v -> update { it.copy(scroll = v) } }
        NumberField("缩放", spot.scale, Modifier.weight(1f)) { v -> if (v > 0) update { it.copy(scale = v) } }
        NumberField("透明度", spot.alpha, Modifier.weight(1f)) { v -> update { it.copy(alpha = v.coerceIn(0.0, 1.0)) } }
    }
}
