package com.brenninhotools.vconverter.ui.nmv

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.brenninhotools.vconverter.R
import com.brenninhotools.vconverter.overlay.ModScannerPanel
import com.brenninhotools.vconverter.overlay.ScannerColors
import com.brenninhotools.vconverter.core.Report
import com.brenninhotools.vconverter.core.ReportStat
import com.brenninhotools.vconverter.ui.components.SaveActions
import com.brenninhotools.vconverter.ui.components.DropZone
import com.brenninhotools.vconverter.ui.components.FunkinCard
import com.brenninhotools.vconverter.ui.components.FunkinTextField
import com.brenninhotools.vconverter.ui.components.ReportView
import com.brenninhotools.vconverter.ui.components.SectionTitle
import com.brenninhotools.vconverter.ui.components.StatusBanner
import com.brenninhotools.vconverter.ui.components.Tag
import com.brenninhotools.vconverter.ui.theme.Funkin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NmvModScreen(
    viewModel: NmvModViewModel,
    modFolderName: String?,
    onConvert: () -> Unit,
    intro: String,
    infoNote: String,
    convertNote: String
) {
    val accent = Funkin.Purple
    val busy = viewModel.busy
    val progress by viewModel.progress.collectAsState()
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) viewModel.openFolder(uri)
    }
    val archivePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.openArchive(uri)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FunkinCard {
            SectionTitle("NightmareVision 模组", accent, 1)
            Text(
                intro,
                style = MaterialTheme.typography.bodySmall,
                color = Funkin.Muted
            )
            DropZone(
                title = "导入 .zip / .rar / .7z",
                hint = "模组可以在压缩包的子文件夹里，会自动找到。",
                accent = accent,
                icon = R.drawable.ic_archive,
                enabled = !busy,
                onClick = { archivePicker.launch(arrayOf("*/*")) }
            )
            DropZone(
                title = "打开模组文件夹",
                hint = "选 NightmareVision 的 content 目录里那个模组文件夹。",
                accent = Funkin.Lavender,
                icon = R.drawable.ic_folder,
                enabled = !busy,
                onClick = { folderPicker.launch(null) }
            )
            val loaded = viewModel.mod
            if (loaded != null && progress == null) {
                val scan = loaded.scan
                Text(loaded.sourceName, style = MaterialTheme.typography.titleSmall, color = Funkin.Text)
                if (scan.root.isNotEmpty()) {
                    Text("Mod root: ${scan.root}", style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
                }
                ReportView(
                    Report(
                        stats = listOf(
                            ReportStat("歌曲", scan.songs.size.toString()),
                            ReportStat("人物", scan.characters.size.toString()),
                            ReportStat("背景", scan.stages.size.toString()),
                            ReportStat("周目", scan.weeks.size.toString()),
                            ReportStat("资源", scan.assets.toString()),
                            ReportStat("脚本", scan.scripts.toString())
                        )
                    ),
                    accent
                )
                if (scan.songs.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scan.songs.take(24).forEach { song ->
                            Tag(song, Funkin.Down)
                        }
                        if (scan.songs.size > 24) Tag("+${scan.songs.size - 24} more", Funkin.Muted)
                    }
                }
            }
        }

        if (viewModel.mod != null) {
            FunkinCard {
                SectionTitle("模组信息", accent, 2)
                FunkinTextField(viewModel.title, { viewModel.title = it }, "标题", accent, enabled = !busy)
                FunkinTextField(viewModel.description, { viewModel.description = it }, "描述", accent, enabled = !busy)
                FunkinTextField(viewModel.author, { viewModel.author = it }, "作者", accent, enabled = !busy)
                Text(
                    infoNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = Funkin.Muted
                )
            }
        }

        FunkinCard(accent = if (viewModel.result != null) accent else null) {
            SectionTitle("转换", accent, 3)
            Text(
                convertNote,
                style = MaterialTheme.typography.bodySmall,
                color = Funkin.Muted
            )
            SaveActions(
                viewModel = viewModel,
                accent = accent,
                modFolderName = modFolderName,
                onSave = onConvert,
                saveLabel = if (modFolderName != null) "Convert into $modFolderName" else "转换到模组文件夹"
            )
            progress?.let { current ->
                ModScannerPanel(
                    progress = current,
                    colors = ScannerColors(accent, Funkin.Background, Funkin.SurfaceHigh, Funkin.Text, Funkin.Muted),
                    onCancel = if (viewModel.cancellable) viewModel::cancelConversion else null,
                    cancelling = viewModel.cancelling
                )
            }
            StatusBanner(viewModel.status, viewModel::dismissStatus)
            viewModel.result?.let { ReportView(it, accent) }
        }
    }
}
