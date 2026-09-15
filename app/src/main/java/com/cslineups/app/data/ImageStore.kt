package com.cslineups.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * 用户从相册选的教学图会复制进应用私有目录，卸载前一直可用，
 * 也不需要任何存储权限。
 */
object ImageStore {

    private const val MAX_SIZE = 1600
    private const val DIR = "images"

    private fun dir(context: Context) = File(context.filesDir, DIR).apply { mkdirs() }

    fun file(context: Context, name: String?): File? {
        if (name.isNullOrBlank()) return null
        val f = File(dir(context), name)
        return if (f.exists()) f else null
    }

    /** 把选中的图片复制进应用目录，返回保存后的文件名。 */
    suspend fun import(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val name = "${UUID.randomUUID()}.jpg"
            val target = File(dir(context), name)
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "无法读取所选图片" }
                target.outputStream().use { output -> input.copyTo(output) }
            }
            name
        }.getOrNull()
    }

    suspend fun delete(context: Context, name: String?) = withContext(Dispatchers.IO) {
        file(context, name)?.delete()
    }

    suspend fun loadBitmap(context: Context, name: String?): ImageBitmap? = withContext(Dispatchers.IO) {
        val f = file(context, name) ?: return@withContext null
        runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(f.absolutePath, bounds)

            var sample = 1
            while (bounds.outWidth / (sample * 2) >= MAX_SIZE || bounds.outHeight / (sample * 2) >= MAX_SIZE) {
                sample *= 2
            }

            val options = BitmapFactory.Options().apply {
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(f.absolutePath, options)?.asImageBitmap()
        }.getOrNull()
    }
}

/** 在界面里按文件名异步加载图片。 */
@Composable
fun rememberStoredBitmap(name: String?): ImageBitmap? {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = name) {
        value = ImageStore.loadBitmap(context, name)
    }
    return bitmap
}
