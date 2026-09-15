package com.cslineups.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * 通过资源名解析图片。数据文件引用的教学图当前在项目里并不存在，
 * 解析不到时返回 0，由调用方显示占位，行为与 iOS 版一致。
 */
fun drawableId(context: Context, name: String): Int =
    if (name.isEmpty()) 0 else context.resources.getIdentifier(name, "drawable", context.packageName)

@Composable
fun rememberDrawableId(name: String): Int {
    val context = LocalContext.current
    return remember(name) { drawableId(context, name) }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("cslineups", text))
}

