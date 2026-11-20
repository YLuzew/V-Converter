package com.vconvertercrew.vconverter.ui

import com.vconvertercrew.vconverter.overlay.soundClickable
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.asImageBitmap
import com.vconvertercrew.vconverter.overlay.FunkinOptionsMenu
import com.vconvertercrew.vconverter.overlay.MenuOption
import com.vconvertercrew.vconverter.R
import com.vconvertercrew.vconverter.overlay.OverlayIcons
import com.vconvertercrew.vconverter.ui.components.ChunkyButton
import com.vconvertercrew.vconverter.ui.components.SectionTitle
import com.vconvertercrew.vconverter.ui.components.ToggleRow
import com.vconvertercrew.vconverter.ui.theme.Funkin
import kotlin.math.roundToInt

class AppSettings(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var fpsCounter by mutableStateOf(prefs.getBoolean(KEY_FPS, true))
        private set

    var touchCursor by mutableStateOf(prefs.getBoolean(KEY_TOUCH_CURSOR, true))
        private set

    fun updateTouchCursor(value: Boolean) {
        touchCursor = value
        prefs.edit().putBoolean(KEY_TOUCH_CURSOR, value).apply()
    }

    var menuMusic by mutableStateOf(prefs.getBoolean(KEY_MUSIC, true))
        private set

    fun updateMenuMusic(value: Boolean) {
        menuMusic = value
        prefs.edit().putBoolean(KEY_MUSIC, value).apply()
    }

    var uiSounds by mutableStateOf(prefs.getBoolean(KEY_UI_SOUNDS, true))
        private set

    fun updateUiSounds(value: Boolean) {
        uiSounds = value
        prefs.edit().putBoolean(KEY_UI_SOUNDS, value).apply()
    }

    var musicVolume by mutableStateOf(prefs.getFloat(KEY_VOLUME, 0.5f))
        private set

    fun updateMusicVolume(value: Float) {
        musicVolume = value.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_VOLUME, musicVolume).apply()
    }

    var serverUrl by mutableStateOf(prefs.getString(KEY_SERVER, "") ?: "")
        private set

    fun updateServerUrl(value: String) {
        serverUrl = value.trim()
        com.vconvertercrew.vconverter.online.Online.serverUrl = serverUrl
        prefs.edit().putString(KEY_SERVER, serverUrl).apply()
    }

    var errorReports by mutableStateOf(prefs.getBoolean(KEY_REPORTS, false))
        private set

    fun updateErrorReports(value: Boolean) {
        errorReports = value
        com.vconvertercrew.vconverter.online.Online.reportsEnabled = value
        prefs.edit().putBoolean(KEY_REPORTS, value).apply()
    }

    var vslicePreview by mutableStateOf(prefs.getBoolean(KEY_VSLICE_PREVIEW, false))
        private set

    fun updateVSlicePreview(value: Boolean) {
        vslicePreview = value
        com.vconvertercrew.vconverter.vslice.VSlicePreview.enabled = value
        prefs.edit().putBoolean(KEY_VSLICE_PREVIEW, value).apply()
    }

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    init {
        com.vconvertercrew.vconverter.overlay.TouchOptions.load { prefs.getString(it, null) }
        com.vconvertercrew.vconverter.vslice.VSlicePreview.enabled = vslicePreview
        com.vconvertercrew.vconverter.online.Online.serverUrl = serverUrl
        com.vconvertercrew.vconverter.online.Online.reportsEnabled = errorReports
    }

    fun updateFpsCounter(value: Boolean) {
        fpsCounter = value
        prefs.edit().putBoolean(KEY_FPS, value).apply()
    }

    private companion object {
        const val KEY_FPS = "fps_counter"
        const val KEY_TOUCH_CURSOR = "touch_cursor"
        const val KEY_MUSIC = "menu_music"
        const val KEY_UI_SOUNDS = "ui_sounds"
        const val KEY_VOLUME = "music_volume"
        const val KEY_SERVER = "server_url"
        const val KEY_REPORTS = "error_reports"
        const val KEY_VSLICE_PREVIEW = "vslice_preview"
    }
}

@Composable
fun rememberAppSettings(): AppSettings {
    val context = LocalContext.current.applicationContext
    return remember(context) { AppSettings(context) }
}

@Composable
fun OptionsButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape)
            .background(Funkin.Surface)
            .border(1.5.dp, Funkin.Outline, shape)
            .soundClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(OverlayIcons.Gear, contentDescription = "选项", tint = Funkin.Text, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun OptionsDialog(settings: AppSettings, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val art = remember {
        val assets = context.assets
        com.vconvertercrew.vconverter.overlay.MenuArt.load(
            image = { path -> runCatching { assets.open(path).use { android.graphics.BitmapFactory.decodeStream(it) }.asImageBitmap() }.getOrNull() },
            text = { path -> runCatching { assets.open(path).use { it.readBytes().decodeToString() } }.getOrNull() }
        )
    }
    var backPresses by remember { mutableStateOf(0) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        androidx.activity.compose.BackHandler { backPresses++ }
        val links = androidx.compose.ui.platform.LocalUriHandler.current
        val preferences = listOf(
            MenuOption.Toggle("FPS 计数", "在右上角显示「FPS:」。", { settings.fpsCounter }, settings::updateFpsCounter),
            MenuOption.Toggle("触摸光标", "在你点屏幕的地方显示光标。", { settings.touchCursor }, settings::updateTouchCursor),
            MenuOption.Toggle("界面音效", "点标签和按钮时播放 scrollMenu。", { settings.uiSounds }, settings::updateUiSounds),
            MenuOption.Toggle("菜单音乐", "干活时循环播放 freakyMenu，谱面编辑器放歌时自动暂停。", { settings.menuMusic }, settings::updateMenuMusic),
            MenuOption.Stepper("音乐音量", "左右（或点它）改菜单音乐的音量。", { "${(settings.musicVolume * 100).roundToInt()}%" }) { step ->
                val next = ((settings.musicVolume * 10).roundToInt() + step).let { if (it > 10) 0 else if (it < 0) 10 else it }
                settings.updateMusicVolume(next / 10f)
            },
            MenuOption.Toggle("V-Slice 预览", "V-Slice 的输出用 Public PlayTest 的目录结构（gameplay/songs、gameplay/characters、gameplay/stages、ui/story-mode）。关掉就保持 0.7 的结构。", { settings.vslicePreview }, settings::updateVSlicePreview),
            MenuOption.TextEntry("在线服务器地址", "留空就自动连 V-Converter 的服务器，也可以填你自己的服务器地址。再点一次可以修改。", { settings.serverUrl }, settings::updateServerUrl, "自动"),
            MenuOption.Toggle("发送错误报告", "把转换错误和崩溃（消息、应用版本、平台）发到在线服务器。", { settings.errorReports }, settings::updateErrorReports)
        )
        val options = listOf(
            MenuOption.Submenu("偏好设置", "FPS 计数、触摸光标、音效、音乐、V-Slice 目录结构、在线服务器和错误报告。") { preferences },
            com.vconvertercrew.vconverter.overlay.TouchOptions.submenu(settings::saveString),
            com.vconvertercrew.vconverter.overlay.discordOption { links.openUri(it) }
        )
        FunkinOptionsMenu(
            "选项", options, art, onClose = onDismiss, closeRequests = backPresses,
            overlay = {
                com.vconvertercrew.vconverter.overlay.DiscordBadge(
                    Modifier.align(Alignment.TopEnd).padding(16.dp)
                )
            }
        )
    }
}
