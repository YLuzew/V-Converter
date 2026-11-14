package com.brenninhotools.vconverter.generator

import com.brenninhotools.vconverter.core.ReportBuilder
import com.brenninhotools.vconverter.mod.ModConverter
import com.brenninhotools.vconverter.json.JSONArray
import com.brenninhotools.vconverter.json.JSONObject

data class ModSpec(
    val title: String,
    val description: String = "",
    val author: String = "",
    val version: String = "1.0.0",
    val homepage: String = "",
    val license: String = "",
    val icon: PickedAsset? = null,
    val starterModule: Boolean = true
)

object ModGenerator {
    fun generate(spec: ModSpec): Generated {
        require(spec.title.isNotBlank()) { "给模组起个标题。" }
        require(spec.version.matches(Regex("\\d+\\.\\d+\\.\\d+"))) { "版本号要写成 1.0.0 这样。" }
        val contributors = JSONArray()
        spec.author.split(',').map { it.trim() }.filter { it.isNotEmpty() }.forEach {
            contributors.put(JSONObject().put("name", it))
        }
        val meta = JSONObject()
            .put("title", spec.title.trim())
            .put("description", spec.description.trim())
            .put("contributors", contributors)
            .put("homepage", spec.homepage.trim())
            .put("api_version", ModConverter.API_VERSION)
            .put("mod_version", spec.version)
            .put("license", spec.license.trim())
        val files = mutableListOf(GenFile("_polymod_meta.json", meta.toString(2)))
        val report = ReportBuilder()
        spec.icon?.let {
            require(it.extension == "png") { "模组图标必须是 .png 文件。" }
            files += GenFile("_polymod_icon.png", bytes = it.bytes)
        }
        if (spec.starterModule) {
            val name = Ids.className(spec.title) + "Module"
            files += GenFile(
                "scripts/modules/$name.hxc",
                ScriptGenerator.generate(
                    ScriptSpec(ScriptKind.MODULE, name, Ids.of(spec.title).ifEmpty { "mod" }, setOf(Callback.CREATE, Callback.UPDATE, Callback.BEAT_HIT))
                ).files.single().text
            )
        }
        report.stat("Files", files.size)
        report.section(
            "下一步",
            listOf(
                "解压或者保存到 V-Slice 的 mods 文件夹里。",
                "歌曲用 Song 生成器加，人物用 Character，背景用 Stage，周目用 Level。",
                "Images go in images/, audio in songs/<song>/, data in data/, scripts in scripts/."
            )
        )
        return Generated(files, report.build())
    }
}
