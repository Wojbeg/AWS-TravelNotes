package com.wojbeg.aws_travelnotes.presentation.components.logic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wojbeg.aws_travelnotes.common.ImageOperationsImpl
import com.wojbeg.aws_travelnotes.common.Resource
import java.io.File

abstract class ImageSelecting (
    val imageOperations: ImageOperationsImpl
): ViewModel() {

    var imageState by mutableStateOf(ImageState())
        private set

    fun handleImage(bitmap: Bitmap): String? {
        val file = imageOperations.createImageFile()
        file.deleteOnExit()

        val result = imageOperations.saveImageToFile(bitmap, file)

        when (result) {
            is Resource.Error -> {
                updateImageState(setImageNull = true)
            }
            is Resource.Loading -> {
                updateImageState(setImageNull = true)
            }
            is Resource.Success -> {
                setImage(file.absolutePath)
            }
        }

        return file.absolutePath
    }


    fun setImage(path: String) {
        val imgFile = File(path)
        if (imgFile.exists()) {

            val imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            updateImageState(imgBitmap, imgFile.absolutePath)
        }

    }

    fun updateImageState(imageBitmap: Bitmap? = null, filePath: String? = null, setImageNull: Boolean = false) {
        imageBitmap?.let { imageState = imageState.copy(image = it) }
        filePath?.let { imageState = imageState.copy(filePath = it) }
        if (setImageNull) imageState = imageState.copy(image = null, filePath = null)
    }

}