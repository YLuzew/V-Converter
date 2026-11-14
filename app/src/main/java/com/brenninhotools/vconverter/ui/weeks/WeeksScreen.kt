package com.brenninhotools.vconverter.ui.weeks

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.brenninhotools.vconverter.R
import com.brenninhotools.vconverter.ui.ConvertSection
import com.brenninhotools.vconverter.ui.components.DropZone
import com.brenninhotools.vconverter.ui.components.FileItem
import com.brenninhotools.vconverter.ui.components.FunkinCard
import com.brenninhotools.vconverter.ui.components.FunkinTextField
import com.brenninhotools.vconverter.ui.components.SectionTitle
import com.brenninhotools.vconverter.ui.components.Tag
import com.brenninhotools.vconverter.ui.theme.Funkin

@Composable
fun WeeksScreen(viewModel: WeeksViewModel, modFolderName: String?, onSave: () -> Unit) {
    val accent = Funkin.Right
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.add(it) }
    val busy = viewModel.busy

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FunkinCard {
            SectionTitle("Weeks", accent, 1)
            DropZone(
                title = "添加周目与菜单人物",
                hint = "选择 weeks 文件夹里的周目 .json，和 images/menucharacters 里的菜单人物 .json，或者包含它们的 .zip/.rar/.7z。",
                accent = accent,
                icon = R.drawable.ic_tab_week,
                enabled = !busy,
                onClick = { picker.launch(arrayOf("*/*")) }
            )
            if (viewModel.menuCharacters.isNotEmpty()) {
                Text(
                    "菜单人物：" + viewModel.menuCharacters.keys.joinToString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Funkin.Muted
                )
            }
            viewModel.weeks.forEachIndexed { index, item ->
                val missing = viewModel.missingMenuCharacters(item)
                FileItem(
                    title = item.fileName,
                    accent = accent,
                    enabled = !busy,
                    onRemove = { viewModel.remove(index) },
                    tags = {
                        Tag("${item.week.songs.size} songs", accent)
                        item.week.storyName.takeIf { it.isNotBlank() }?.let { Tag(it, Funkin.Muted) }
                        if (missing.isEmpty()) Tag("Menu chars ready", Funkin.Up) else Tag("Missing ${missing.size} menu char(s)", Funkin.Warning)
                        if (item.week.hideStoryMode) Tag("Hidden", Funkin.Muted)
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FunkinTextField(item.id, { viewModel.setId(index, it) }, "Level id", accent, enabled = !busy)
                        Text(
                            item.week.songs.joinToString(" · ") { it.name },
                            style = MaterialTheme.typography.bodySmall,
                            color = Funkin.Muted
                        )
                        if (missing.isNotEmpty()) {
                            Text(
                                "Needs: " + missing.joinToString { "menucharacters/$it.json" },
                                style = MaterialTheme.typography.bodySmall,
                                color = Funkin.Warning
                            )
                        }
                    }
                }
            }
        }

        ConvertSection(
            viewModel = viewModel,
            accent = accent,
            step = 2,
            canConvert = viewModel.weeks.isNotEmpty(),
            onConvert = viewModel::convert,
            onSave = onSave,
            modFolderName = modFolderName
        )
    }
}
