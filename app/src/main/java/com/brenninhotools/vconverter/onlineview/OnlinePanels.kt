package com.brenninhotools.vconverter.onlineview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brenninhotools.vconverter.editorview.EditorChip
import com.brenninhotools.vconverter.editorview.EditorSectionTitle
import com.brenninhotools.vconverter.editorview.TextValueField
import com.brenninhotools.vconverter.online.ModEngine
import com.brenninhotools.vconverter.online.ModEntry
import com.brenninhotools.vconverter.online.RoomInfo
import com.brenninhotools.vconverter.core.fmt

private val warning = Color(0xFFFFC94A)

private fun size(bytes: Long): String = when {
    bytes >= 1024 * 1024 -> fmt("%.1f MB", bytes / 1024.0 / 1024.0)
    bytes >= 1024 -> fmt("%.0f KB", bytes / 1024.0)
    else -> "$bytes B"
}

@Composable
fun NotConfigured(modifier: Modifier = Modifier) {
    Text(
        "还没设置服务器。打开选项（齿轮）填一个 V-Converter 服务器的地址，例如 https://my-server.example.com。服务器的代码在仓库的 server/ 文件夹里。",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
fun OnlineNewsPanel(state: OnlineState, onOpenLink: (String) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(state.configured) { if (state.configured && state.version == null) state.refreshNews() }
    val scheme = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditorSectionTitle("新闻")
        if (!state.configured) {
            NotConfigured()
            return@Column
        }
        val version = state.version
        when {
            version == null -> Unit
            version.updateAvailable -> Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(scheme.primary).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("新版本 ${version.latest} 出来了", color = scheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("你现在是 ${version.current}。", color = scheme.onPrimary, fontSize = 13.sp)
                if (version.notes.isNotBlank()) Text(version.notes.take(600), color = scheme.onPrimary, fontSize = 12.sp, maxLines = 12, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (version.url.isNotBlank()) EditorChip("下载", false) { onOpenLink(version.url) }
                    if (version.page.isNotBlank()) EditorChip("发布页", false) { onOpenLink(version.page) }
                }
            }
            version.latest != null -> Text("你已经是最新版本了（${version.current}）。", fontSize = 13.sp, color = scheme.onSurfaceVariant)
        }
        state.announcements.forEach { a ->
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(scheme.surfaceVariant).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(a.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (a.level == "warning") warning else scheme.onSurface)
                if (a.body.isNotBlank()) Text(a.body, fontSize = 13.sp)
                if (a.url.isNotBlank()) EditorChip("打开链接", false) { onOpenLink(a.url) }
            }
        }
        if (state.announcements.isEmpty() && version != null) Text("目前没有公告。", fontSize = 13.sp, color = scheme.onSurfaceVariant)
        state.newsError?.let { Text(it, fontSize = 13.sp, color = scheme.error) }
        EditorChip(if (state.newsLoading) "检查中…" else "重新检查", false) { state.refreshNews() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModGalleryPanel(state: OnlineState, onDownload: (ModEntry) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(state.configured) { if (state.configured && state.page == null) state.search() }
    val scheme = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditorSectionTitle("模组画廊")
        if (!state.configured) {
            NotConfigured()
            return@Column
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextValueField("搜索模组", state.query, Modifier.weight(1f)) { state.query = it }
            EditorChip(if (state.loading) "搜索中…" else "搜索", false) { state.search() }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            EditorChip("全部引擎", state.engine == null) { state.engine = null; state.search() }
            ModEngine.entries.forEach { e -> EditorChip(e.label, state.engine == e) { state.engine = e; state.search() } }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("new" to "最新", "downloads" to "下载最多", "title" to "A–Z").forEach { (key, label) ->
                EditorChip(label, state.sort == key) { state.sort = key; state.search() }
            }
        }
        state.error?.let { Text(it, fontSize = 13.sp, color = scheme.error) }
        state.message?.let { Text(it, fontSize = 13.sp, color = scheme.primary) }
        val page = state.page
        if (page != null) {
            Text("${page.total} 个模组", fontSize = 12.sp, color = scheme.onSurfaceVariant)
            page.mods.forEach { mod ->
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(scheme.surfaceVariant).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(mod.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOfNotNull(
                            mod.author.takeIf { it.isNotBlank() }?.let { "作者：$it" },
                            ModEngine.of(mod.engine).label,
                            size(mod.size),
                            "下载 ${mod.downloads} 次",
                            mod.createdAt.take(10)
                        ).joinToString(" · "),
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant
                    )
                    if (mod.description.isNotBlank()) Text(mod.description, fontSize = 13.sp, maxLines = 6, overflow = TextOverflow.Ellipsis)
                    if (mod.tags.isNotEmpty()) Text(mod.tags.joinToString("  ") { "#$it" }, fontSize = 12.sp, color = scheme.primary)
                    if (state.downloading == mod.id) {
                        LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                    } else {
                        EditorChip("下载", false) { onDownload(mod) }
                    }
                }
            }
            if (page.mods.isEmpty()) Text("没有找到模组。", fontSize = 13.sp, color = scheme.onSurfaceVariant)
            if (page.pages > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (state.pageNumber > 1) EditorChip("← 上一页", false) { state.search(state.pageNumber - 1) }
                    Text("第 ${state.pageNumber} / ${page.pages} 页", fontSize = 12.sp)
                    if (state.pageNumber < page.pages) EditorChip("下一页 →", false) { state.search(state.pageNumber + 1) }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModSharePanel(state: OnlineState, onPickFile: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditorSectionTitle("分享模组")
        if (!state.configured) {
            NotConfigured()
            return@Column
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(state.file?.let { "${it.first} · ${size(it.second.size.toLong())}" } ?: "没有选择文件", fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            EditorChip("选择 .zip", false, onPickFile)
        }
        TextValueField("标题", state.title, Modifier.fillMaxWidth()) { state.title = it }
        TextValueField("描述", state.description, Modifier.fillMaxWidth()) { state.description = it }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextValueField("作者", state.author, Modifier.weight(1f)) { state.author = it }
            TextValueField("标签（逗号分隔）", state.tags, Modifier.weight(1f)) { state.tags = it }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ModEngine.entries.forEach { e -> EditorChip(e.label, state.uploadEngine == e) { state.uploadEngine = e } }
        }
        EditorChip(if (state.uploading) "上传中…" else "上传", false) { state.upload() }
        state.uploadError?.let { Text(it, fontSize = 13.sp, color = scheme.error) }
        state.uploaded?.let { done ->
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(scheme.surfaceVariant).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("「${done.mod.title}」已经进画廊了。", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                SelectionContainer {
                    Text("模组 id：${done.mod.id}\n删除密钥：${done.deleteToken}\n链接：${done.mod.downloadUrl}", fontSize = 12.sp)
                }
                Text("保管好删除密钥：以后只能靠它才能删掉这个模组。", fontSize = 12.sp, color = scheme.onSurfaceVariant)
                EditorChip("删除它", false) { state.deleteUploaded() }
            }
        }
        Text("只接受 .zip 文件。请分享你自己做的、或获得授权分享的模组。", fontSize = 12.sp, color = scheme.onSurfaceVariant)
    }
}

private val roomKinds = listOf("chart" to "谱面编辑器", "character" to "人物编辑器", "stage" to "背景编辑器", "week" to "周目编辑器")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublicRoomsPanel(state: OnlineState, onJoin: (RoomInfo) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(state.configured) { if (state.configured && state.rooms == null) state.refreshRooms() }
    val scheme = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditorSectionTitle("公开房间")
        if (!state.configured) {
            NotConfigured()
            return@Column
        }
        Text("这里是别人在编辑器的多人编辑卡里勾了「公开列出我的房间」后开的房间。加入会打开对应的编辑器，并加载房间的工作内容和文件。", fontSize = 12.sp, color = scheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            EditorChip("全部编辑器", state.roomKind == null) { state.roomKind = null; state.refreshRooms() }
            roomKinds.forEach { (key, label) -> EditorChip(label, state.roomKind == key) { state.roomKind = key; state.refreshRooms() } }
            EditorChip(if (state.roomsLoading) "刷新中…" else "刷新", false) { state.refreshRooms() }
        }
        state.roomsError?.let { Text(it, fontSize = 13.sp, color = scheme.error) }
        val rooms = state.rooms
        if (rooms != null && rooms.isEmpty()) Text("目前没有公开房间。在任意编辑器的多人编辑卡里开一个。", fontSize = 13.sp, color = scheme.onSurfaceVariant)
        rooms?.forEach { room ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(scheme.surfaceVariant).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(room.title.ifBlank { room.code }, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOfNotNull(
                            roomKinds.firstOrNull { it.first == room.kind }?.second,
                            room.host.takeIf { it.isNotBlank() }?.let { "作者：$it" },
                            "${room.peers.size} 人正在编辑",
                            room.code
                        ).joinToString(" · "),
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant
                    )
                    if (room.peers.isNotEmpty()) Text(room.peers.joinToString { it.name }, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                EditorChip("加入", false) { onJoin(room) }
            }
        }
    }
}
