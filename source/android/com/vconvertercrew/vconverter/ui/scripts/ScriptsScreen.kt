package com.vconvertercrew.vconverter.ui.scripts

import com.vconvertercrew.vconverter.overlay.soundClickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vconvertercrew.vconverter.R
import com.vconvertercrew.vconverter.script.ScriptScope
import com.vconvertercrew.vconverter.ui.ConvertSection
import com.vconvertercrew.vconverter.ui.components.DropZone
import com.vconvertercrew.vconverter.ui.components.FileItem
import com.vconvertercrew.vconverter.ui.components.FunkinCard
import com.vconvertercrew.vconverter.ui.components.FunkinTextField
import com.vconvertercrew.vconverter.ui.components.SectionTitle
import com.vconvertercrew.vconverter.ui.components.Tag
import com.vconvertercrew.vconverter.ui.theme.Funkin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScriptsScreen(viewModel: ScriptsViewModel, modFolderName: String?, onSave: () -> Unit) {
    val accent = Funkin.Lavender
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.add(it) }
    val busy = viewModel.busy

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FunkinCard {
            SectionTitle("脚本", accent, 1)
            DropZone(
                title = "添加 Psych 脚本",
                hint = "选择 .lua 或 .hx 文件，或者包含模组 data、stages、scripts、custom_events 和 custom_notetypes 文件夹的压缩包。每个脚本会变成一个 V-Slice 模块。",
                accent = accent,
                icon = R.drawable.ic_tab_script,
                enabled = !busy,
                onClick = { picker.launch(arrayOf("*/*")) }
            )
            if (viewModel.scripts.isNotEmpty()) {
                FunkinTextField(viewModel.modName, { viewModel.modName = it }, "模组名（会用在类名里）", accent, enabled = !busy)
            }
            viewModel.scripts.forEachIndexed { index, item ->
                FileItem(
                    title = item.fileName,
                    accent = accent,
                    enabled = !busy,
                    onRemove = { viewModel.remove(index) },
                    tags = {
                        Tag(if (item.isLua) "Lua" else "HScript", if (item.isLua) Funkin.Down else Funkin.Left)
                        Tag("${item.lineCount} lines", Funkin.Muted)
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("作用于", style = MaterialTheme.typography.labelSmall, color = Funkin.Muted)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ScriptScope.entries.forEach { scope ->
                                ScopeChip(scope.label, scope == item.scope, accent, enabled = !busy) { viewModel.setScope(index, scope) }
                            }
                        }
                        if (item.scope != ScriptScope.GLOBAL) {
                            FunkinTextField(item.target, { viewModel.setTarget(index, it) }, "${item.scope.label} name", accent, enabled = !busy)
                        }
                    }
                }
            }
        }

        ConvertSection(
            viewModel = viewModel,
            accent = accent,
            step = 2,
            canConvert = viewModel.scripts.isNotEmpty(),
            onConvert = viewModel::convert,
            onSave = onSave,
            modFolderName = modFolderName
        )
    }
}

@Composable
private fun ScopeChip(text: String, selected: Boolean, accent: Color, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) Funkin.Ink else Funkin.Text,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) accent else Funkin.Surface)
            .border(1.5.dp, if (selected) Funkin.Ink else Funkin.Outline, shape)
            .soundClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
