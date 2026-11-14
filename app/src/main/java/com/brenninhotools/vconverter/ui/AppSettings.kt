package com.brenninhotools.vconverter.ui

import com.brenninhotools.vconverter.overlay.soundClickable
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
import com.brenninhotools.vconverter.R
import com.brenninhotools.vconverter.overlay.OverlayIcons
import com.brenninhotools.vconverter.ui.components.ChunkyButton
import com.brenninhotools.vconverter.ui.components.SectionTitle
import com.brenninhotools.vconverter.ui.components.ToggleRow
import com.brenninhotools.vconverter.ui.theme.Funkin
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
        com.brenninhotools.vconverter.online.Online.serverUrl = serverUrl
        prefs.edit().putString(KEY_SERVER, serverUrl).apply()
    }

    var errorReports by mutableStateOf(prefs.getBoolean(KEY_REPORTS, false))
        private set

    fun updateErrorReports(value: Boolean) {
        errorReports = value
        com.brenninhotools.vconverter.online.Online.reportsEnabled = value
        prefs.edit().putBoolean(KEY_REPORTS, value).apply()
    }

    init {
        com.brenninhotools.vconverter.online.Online.serverUrl = serverUrl
        com.brenninhotools.vconverter.online.Online.reportsEnabled = errorReports
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
    Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(22.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Funkin.Surface)
                .border(2.dp, Funkin.Outline, shape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle("选项", Funkin.Down)
            ToggleRow(
                label = "FPS 计数",
                checked = settings.fpsCounter,
                onChange = settings::updateFpsCounter,
                accent = Funkin.Down,
                hint = "在右上角显示「FPS:」。"
            )
            ToggleRow(
                label = "触摸光标",
                checked = settings.touchCursor,
                onChange = settings::updateTouchCursor,
                accent = Funkin.Down,
                hint = "在你点屏幕的地方显示光标。"
            )
            ToggleRow(
                label = "界面音效",
                checked = settings.uiSounds,
                onChange = settings::updateUiSounds,
                accent = Funkin.Down,
                hint = "点标签和按钮时播放 scrollMenu。"
            )
            ToggleRow(
                label = "菜单音乐",
                checked = settings.menuMusic,
                onChange = settings::updateMenuMusic,
                accent = Funkin.Down,
                hint = "干活时循环播放 freakyMenu，谱面编辑器放歌时自动暂停。"
            )
            if (settings.menuMusic) {
                Text("Music volume: ${(settings.musicVolume * 100).roundToInt()}%", style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
                Slider(
                    value = settings.musicVolume,
                    onValueChange = settings::updateMusicVolume,
                    colors = SliderDefaults.colors(thumbColor = Funkin.Down, activeTrackColor = Funkin.Down)
                )
            }
            com.brenninhotools.vconverter.ui.components.FunkinTextField(
                value = settings.serverUrl,
                onValueChange = settings::updateServerUrl,
                label = "在线服务器地址",
                accent = Funkin.Down,
                placeholder = "https://my-server.example.com"
            )
            ToggleRow(
                label = "发送错误报告",
                checked = settings.errorReports,
                onChange = settings::updateErrorReports,
                accent = Funkin.Down,
                hint = "把转换错误和崩溃（消息、应用版本、平台）发到在线服务器。"
            )
            Text("选项保存在这台设备上。", style = MaterialTheme.typography.bodySmall, color = Funkin.Muted)
            ChunkyButton(text = "完成", accent = Funkin.Down, onClick = onDismiss, icon = R.drawable.ic_check)
        }
    }
}
