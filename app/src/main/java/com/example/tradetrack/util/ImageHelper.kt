package com.example.tradetrack.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageHelper {

    private const val TRADE_IMAGES_DIR = "trade_images"

    /** Save bitmap to app internal storage and return the file path. */
    fun saveTradeImage(context: Context, bitmap: Bitmap): String? {
        val dir = File(context.filesDir, TRADE_IMAGES_DIR)
        if (!dir.exists()) dir.mkdirs()
        val name = "trade_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        val file = File(dir, name)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Copy image from URI to app storage and return path. */
    fun saveImageFromUri(context: Context, uri: Uri): String? {
        val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return null
        return saveTradeImage(context, bitmap)
    }

    /** Load bitmap from path; returns null if file missing or invalid. */
    fun loadBitmap(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null
        val file = File(path)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(path)
    }

    /** Delete image file by path. */
    fun deleteImage(path: String?) {
        if (path.isNullOrBlank()) return
        File(path).takeIf { it.exists() }?.delete()
    }
}
