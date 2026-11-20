package com.vconvertercrew.vconverter.ui.stages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.vconvertercrew.vconverter.R
import com.vconvertercrew.vconverter.ui.ConvertSection
import com.vconvertercrew.vconverter.ui.components.DropZone
import com.vconvertercrew.vconverter.ui.components.FileItem
import com.vconvertercrew.vconverter.ui.components.FunkinCard
import com.vconvertercrew.vconverter.ui.components.FunkinTextField
import com.vconvertercrew.vconverter.ui.components.SectionTitle
import com.vconvertercrew.vconverter.ui.components.Tag
import com.vconvertercrew.vconverter.ui.theme.Funkin

@Composable
fun StagesScreen(viewModel: StagesViewModel, modFolderName: String?, onSave: () -> Unit) {
    val accent = Funkin.Up
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { viewModel.add(it) }
    val busy = viewModel.busy

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FunkinCard {
            SectionTitle("背景", accent, 1)
            DropZone(
                title = "添加 Psych 背景",
                hint = "从 stages 文件夹选背景 .json；如果背景是在脚本里生成精灵的，再选它的 .lua：那些精灵会变成 V-Slice 物件。把带动画精灵的 .xml（images/…）也加上，缩放动画的偏移才准确。压缩包（.zip/.rar/.7z）也可以。",
                accent = accent,
                icon = R.drawable.ic_tab_stage,
                enabled = !busy,
                onClick = { picker.launch(arrayOf("*/*")) }
            )
            viewModel.stages.forEachIndexed { index, item ->
                val objects = item.stage.objects
                FileItem(
                    title = item.fileName,
                    accent = accent,
                    enabled = !busy,
                    onRemove = { viewModel.remove(index) },
                    tags = {
                        if (item.lua != null) Tag("Lua 精灵", Funkin.Down)
                        if (!item.fromJson) Tag("没有 JSON", Funkin.Warning)
                        if (objects != null) {
                            Tag("${objects.count { it.type in setOf("square", "sprite", "animatedSprite") }} sprites", accent)
                        } else if (item.lua == null) {
                            Tag("脚本背景", Funkin.Warning)
                        }
                        Tag("Zoom ${item.stage.defaultZoom}", Funkin.Muted)
                        if (item.stage.isPixelStage) Tag("像素", Funkin.Left)
                        val sheets = viewModel.atlases.keys.map { it.substringAfterLast('/') }.toSet()
                        val animated = objects?.filter { it.type == "animatedSprite" }?.map { it.image.substringAfterLast('/').lowercase() }?.distinct() ?: emptyList()
                        if (animated.isNotEmpty()) Tag("${animated.count { it in sheets }}/${animated.size} spritesheets", if (animated.all { it in sheets }) Funkin.Up else Funkin.Muted)
                    }
                ) {
                    FunkinTextField(item.id, { viewModel.setId(index, it) }, "背景 ID", accent, enabled = !busy)
                }
            }
        }

        ConvertSection(
            viewModel = viewModel,
            accent = accent,
            step = 2,
            canConvert = viewModel.stages.isNotEmpty(),
            onConvert = viewModel::convert,
            onSave = onSave,
            modFolderName = modFolderName
        )
    }
}
