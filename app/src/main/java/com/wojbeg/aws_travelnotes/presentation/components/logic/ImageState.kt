package com.wojbeg.aws_travelnotes.presentation.components.logic

import android.graphics.Bitmap

data class ImageState (
    var image: Bitmap? = null,
    var filePath: String? = null
)