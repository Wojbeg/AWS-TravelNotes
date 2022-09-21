package com.wojbeg.aws_travelnotes.common

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageOperationsImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(Constants.TIME_FORMAT, Locale.ENGLISH).format(Date())
        val storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun saveImageToFile(bitmap: Bitmap, file: File): Resource<Unit> {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                Constants.IMAGE_COMPRESS_QUALITY, outputStream)
            outputStream.close()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unexpected error occurred")
        }
    }
}