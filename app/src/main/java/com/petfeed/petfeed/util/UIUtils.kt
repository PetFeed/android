package com.petfeed.petfeed.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import org.jetbrains.anko.displayMetrics

object UIUtils {
    fun makeDP(context: Context, dp: Float): Float = context.displayMetrics.density * dp

    fun ratioARGB(color: Int, ratio: Float): Int {
        val a = Color.alpha(color) * ratio
        val r = Color.red(color) * ratio
        val g = Color.green(color) * ratio
        val b = Color.blue(color) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    fun rotate(src: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degree)
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun exifOrientationToDegrees(exifOrientation: Int) = when (exifOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    fun getRealPathFromUri(context: Context, uri: Uri): String {
        var result = ""
        var index = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.apply {
            index = if (moveToFirst()) getColumnIndexOrThrow(MediaStore.Images.Media.DATA) else index
            result = getString(index)
        }
        return result
    }
}