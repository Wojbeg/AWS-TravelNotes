package com.wojbeg.aws_travelnotes.presentation.components.logic

import android.graphics.Bitmap
import com.wojbeg.aws_travelnotes.domain.models.Note

data class UserDataState (
    val username: String = "",
    val email: String = "",
    val notes: MutableList<Note> = mutableListOf(),
    val profilePictureFile: String? = null,
    val profileBitmap: Bitmap? = null,
)