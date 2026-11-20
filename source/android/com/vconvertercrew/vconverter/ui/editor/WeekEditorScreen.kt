package com.vconvertercrew.vconverter.ui.editor

import androidx.compose.foundation.layout.fillMaxSize
import com.vconvertercrew.vconverter.editorview.*
import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.vconvertercrew.vconverter.R
import com.vconvertercrew.vconverter.editor.ExportTarget
import com.vconvertercrew.vconverter.editor.SpriteAtlas
import com.vconvertercrew.vconverter.editor.StageAsset
import com.vconvertercrew.vconverter.editor.WeekDocument
import com.vconvertercrew.vconverter.editor.WeekExport
import com.vconvertercrew.vconverter.editor.WeekLoader
import com.vconvertercrew.vconverter.editor.WeekProp
import com.vconvertercrew.vconverter.editor.WeekSlot
import com.vconvertercrew.vconverter.editorview.StageImage
import com.vconvertercrew.vconverter.editorview.WeekEditorState
import com.vconvertercrew.vconverter.editorview.WeekInfoPanel
import com.vconvertercrew.vconverter.editorview.WeekPreview
import com.vconvertercrew.vconverter.editorview.WeekPropsPanel
import com.vconvertercrew.vconverter.editorview.WeekSongsPanel
import com.vconvertercrew.vconverter.editorview.WeekToolbar
import com.vconvertercrew.vconverter.editorview.weekSummary
import com.vconvertercrew.vconverter.ui.BaseConverterViewModel
import com.vconvertercrew.vconverter.ui.ConvertSection
import com.vconvertercrew.vconverter.ui.StatusMessage
import com.vconvertercrew.vconverter.ui.components.ChoiceChips
import com.vconvertercrew.vconverter.ui.components.DropZone
import com.vconvertercrew.vconverter.ui.components.FunkinCard
import com.vconvertercrew.vconverter.ui.components.OutlineButton
import com.vconvertercrew.vconverter.ui.components.SectionTitle
import com.vconvertercrew.vconverter.ui.theme.Funkin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class WeekImageRole { PROP, TITLE, BACKGROUND }

class WeekEditorViewModel(application: Application) : BaseConverterViewModel(application) {
    val editor = WeekEditorState(WeekDocument("new-week", "新建周目"))
    val live = com.vconvertercrew.vconverter.editorview.LiveSession(com.vconvertercrew.vconverter.editorview.WeekLiveAdapter(editor) { asset -> decode(asset) }) { block -> android.os.Handler(android.os.Looper.getMainLooper()).post(block) }
    var target by mutableStateOf(ExportTarget.VSLICE)
        private set
    override val vsliceOutput: Boolean get() = target == ExportTarget.VSLICE
    var source by mutableStateOf("新建一个周目")
        private set
    private var exportedRevision = -1

    override fun downloadName(): String = "${editor.document.id}-${target.name.lowercase()}.zip"

    fun changed(revision: Int) {
        if (revision != exportedRevision && output != null) invalidate()
    }

    fun selectTarget(value: ExportTarget) {
        if (value != target) {
            target = value
            invalidate()
        }
    }

    private fun decode(asset: StageAsset): StageImage? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(asset.png, 0, asset.png.size, bounds)
        var sample = 1
        while (bounds.outWidth / sample > 4096 || bounds.outHeight / sample > 4096 || (bounds.outWidth.toLong() / sample) * (bounds.outHeight / sample) > 16_000_000L) sample *= 2
        val bitmap = BitmapFactory.decodeByteArray(asset.png, 0, asset.png.size, BitmapFactory.Options().apply { inSampleSize = sample }) ?: return null
        StageImage(bitmap.asImageBitmap(), bounds.outWidth, bounds.outHeight, asset.xml?.let { SpriteAtlas.parse(it) }, bitmap.width.toFloat() / bounds.outWidth.coerceAtLeast(1))
    }.getOrNull()

    private suspend fun readAll(uris: List<Uri>): List<Pair<String, ByteArray>> =
        load(uris, { it.extension in setOf("json", "xml", "png", "hxc") }) { file -> file.path to file.open().use { it.readBytes() } }.items

    fun open(uris: List<Uri>) {
        if (uris.isEmpty()) return
        runTask {
            try {
                val files = readAll(uris)
                val loaded = withContext(Dispatchers.Default) { WeekLoader.load(files) }
                val decoded = withContext(Dispatchers.Default) {
                    loaded.assets.mapNotNull { (key, asset) -> decode(asset)?.let { key to it } }.toMap()
                }
                editor.load(loaded.document, loaded.assets, decoded)
                source = "${loaded.source} · ${loaded.document.id}"
                invalidate()
                status = StatusMessage.Info("Opened ${loaded.document.id} from ${loaded.source}: ${weekSummary(loaded.document)}, ${decoded.size} image(s).")
            } catch (e: Exception) {
                status = StatusMessage.Error(e.message ?: "打开周目失败。")
            }
        }
    }

    fun addImage(role: WeekImageRole, slot: WeekSlot?, uris: List<Uri>) {
        if (uris.isEmpty()) return
        runTask {
            try {
                val files = readAll(uris)
                val png = files.firstOrNull { it.first.endsWith(".png", true) } ?: throw IllegalArgumentException("挑一张 .png（要动画人物就再带上它的 .xml）。")
                val xml = files.firstOrNull { it.first.endsWith(".xml", true) }?.second?.toString(Charsets.UTF_8)?.takeIf { SpriteAtlas.parse(it).frames.isNotEmpty() }
                val stem = png.first.substringAfterLast('/').substringBeforeLast('.')
                val doc = editor.document
                val path = when (role) {
                    WeekImageRole.TITLE -> doc.title
                    WeekImageRole.BACKGROUND -> "menubackgrounds/menu_${doc.id}"
                    WeekImageRole.PROP -> doc.prop(slot ?: WeekSlot.DAD)?.takeIf { !it.isBase }?.assetPath ?: "storymenu/props/$stem"
                }
                val asset = StageAsset(png.second, if (role == WeekImageRole.PROP) xml else null)
                val image = withContext(Dispatchers.Default) { decode(asset) }
                editor.addAsset(path, asset, image)
                when (role) {
                    WeekImageRole.TITLE -> editor.edit { it.copy(titleAsset = path) }
                    WeekImageRole.BACKGROUND -> editor.edit { it.copy(background = path) }
                    WeekImageRole.PROP -> {
                        val target = slot ?: WeekSlot.DAD
                        val prefixes = image?.atlas?.prefixes() ?: emptyList()
                        val old = editor.document.prop(target)
                        val idle = prefixes.firstOrNull { it.contains("idle", true) } ?: prefixes.firstOrNull() ?: "idle"
                        val confirm = prefixes.firstOrNull { it.contains("confirm", true) } ?: ""
                        val base = WeekProp(path, offsetX = old?.offsetX ?: 0.0, offsetY = old?.offsetY ?: 0.0).withPrefix("idle", idle).withPrefix("confirm", confirm)
                        editor.edit { it.withProp(target, base) }
                        editor.selected = target
                    }
                }
                invalidate()
                status = null
            } catch (e: Exception) {
                status = StatusMessage.Error(e.message ?: "加载不了这张图片。")
            }
        }
    }

    fun createNew() {
        editor.load(WeekDocument("new-week", "新建周目"), emptyMap(), emptyMap())
        source = "新建一个周目"
        invalidate()
        status = StatusMessage.Info("已新建周目，菜单人物用的是本体默认值。加上歌曲和标题图片就能用。")
    }

    fun export() {
        runTask {
            try {
                val document = editor.document
                val assets = editor.assets
                val chosen = target
                output = withContext(Dispatchers.Default) { WeekExport.build(document, chosen, assets) }
                exportedRevision = editor.revision
                download = null
                status = null
            } catch (e: Exception) {
                output = null
                status = StatusMessage.Error(e.message ?: "导出周目失败。")
            }
        }
    }
}

@Composable
fun WeekEditorScreen(viewModel: WeekEditorViewModel, modFolderName: String?, onSave: () -> Unit, onExit: () -> Unit = {}) {
    val accent = Funkin.Right
    val busy = viewModel.busy
    val editor = viewModel.editor
    var pending by remember { mutableStateOf<Pair<WeekImageRole, WeekSlot?>>(WeekImageRole.PROP to null) }
    val opener = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.open(it) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.addImage(pending.first, pending.second, it) }
    fun pick(role: WeekImageRole, slot: WeekSlot? = null) {
        pending = role to slot
        imagePicker.launch(arrayOf("image/png", "text/xml", "application/xml", "*/*"))
    }

    LaunchedEffect(editor.revision) { viewModel.changed(editor.revision) }

    val windows = rememberEditorWindows("songs")
    val menus = listOf(
        EditorMenu(
            "File",
            listOf(
                EditorMenuItem.Action("新建周目", enabled = !busy) { viewModel.createNew() },
                EditorMenuItem.Action("Open Week…", enabled = !busy) { opener.launch(arrayOf("*/*")) },
                EditorMenuItem.Action("Title Image…", enabled = !busy) { pick(WeekImageRole.TITLE) },
                EditorMenuItem.Action("Background Image…", enabled = !busy) { pick(WeekImageRole.BACKGROUND) },
                EditorMenuItem.Separator,
                EditorMenuItem.Action("导出 ${viewModel.target.label}", enabled = !busy && editor.document.songs.isNotEmpty()) {
                    viewModel.export()
                    windows.show("export")
                },
                EditorMenuItem.Action("Export & Save…") { windows.show("export") },
                EditorMenuItem.Separator,
                EditorMenuItem.Action("Exit Editor") { onExit() }
            )
        ),
        WeekMenus.edit(editor),
        WeekMenus.view(editor),
        EditorMenu(
            "Help",
            listOf(
                EditorMenuItem.Action("Controls…") { windows.show("help") },
                EditorMenuItem.Action("Week Info…") { windows.show("info") }
            )
        )
    )
    val panels = listOf(
        EditorWindow("info", "周目", 400.dp) {
            Text(viewModel.source, color = Funkin.Muted)
            DropZone(
                title = "打开周目",
                hint = "选择 V-Slice 关卡 .json、Psych 周目 .json（连同 images/menucharacters/*.json）或 Codename 周目 .xml（连同 data/weeks/characters/*.xml），再加上标题图和菜单人物图。歌曲元数据文件会补上歌名。压缩包（.zip/.rar/.7z）也可以。",
                accent = accent,
                icon = R.drawable.ic_tab_week,
                enabled = !busy,
                onClick = { opener.launch(arrayOf("*/*")) }
            )
        },
        EditorWindow("songs", "歌曲") { WeekSongsPanel(editor) },
        EditorWindow("properties", "Week Data") {
            WeekInfoPanel(editor)
            OutlineButton("标题图片（.png）", accent, onClick = { pick(WeekImageRole.TITLE) }, enabled = !busy)
            OutlineButton("背景图片（.png）", accent, onClick = { pick(WeekImageRole.BACKGROUND) }, enabled = !busy)
        },
        EditorWindow("props", "Menu Characters", 400.dp) { WeekPropsPanel(editor, onLoadImage = { slot -> pick(WeekImageRole.PROP, slot) }) },
        EditorWindow("live", "多人编辑") { com.vconvertercrew.vconverter.editorview.LiveSessionPanel(viewModel.live) },
        EditorWindow("export", "导出", 420.dp) {
            SectionTitle("导出格式", accent, 1)
            ChoiceChips(
                options = ExportTarget.entries,
                label = { it.label },
                isSelected = { it == viewModel.target },
                onClick = viewModel::selectTarget,
                accent = accent,
                enabled = !busy
            )
            Text(
                when (viewModel.target) {
                    ExportTarget.VSLICE -> "data/levels/<id>.json（锁住或从自由游玩隐藏时还有 scripts/levels/），以及对应的图片。"
                    ExportTarget.PSYCH -> "weeks/<id>.json, images/menucharacters/*.json and the images."
                    ExportTarget.CODENAME -> "data/weeks/weeks/<id>.xml, data/weeks/characters/*.xml and the images."
                },
                color = Funkin.Muted
            )
            ConvertSection(
                viewModel = viewModel,
                accent = accent,
                step = 2,
                canConvert = editor.document.songs.isNotEmpty(),
                onConvert = viewModel::export,
                onSave = onSave,
                modFolderName = modFolderName,
                title = "导出",
                buttonText = "导出 ${viewModel.target.label}",
                buttonIcon = R.drawable.ic_magic
            )
        },
        EditorWindow("help", "Controls") { EditorHelpText("Tap a menu character in the preview and drag it to move it. Use the menus at the top: File opens, creates and exports, Edit undoes and changes the selection, View changes what you see, and Window shows or hides these boxes. Drag a box by its title to move it.") }
    )
    FunkinEditorShell(title = "周目编辑器", accent = accent, menus = menus, windows = windows, panels = panels, modifier = Modifier.fillMaxSize()) {
        WeekEditorStage(editor)
    }
}
