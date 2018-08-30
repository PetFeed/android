package com.petfeed.petfeed.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.support.media.ExifInterface
import android.text.TextUtils


object ImageUtils {

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

    fun getPathOfAllImages(context: Context): ArrayList<String> {
        val result = ArrayList<String>()
        val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaColumns.DATA)

        context.contentResolver.query(uri, projection, null, null, MediaColumns.DATE_ADDED + " desc")?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaColumns.DATA)

            while (it.moveToNext()) {
                val absolutePathOfImage = it.getString(columnIndex)
                if (!TextUtils.isEmpty(absolutePathOfImage)) {
                    result.add(absolutePathOfImage)
                }
            }
            it.close()
        }
        return result
    }
}