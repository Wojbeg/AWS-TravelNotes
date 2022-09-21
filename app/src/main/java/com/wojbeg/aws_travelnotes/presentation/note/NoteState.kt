package com.wojbeg.aws_travelnotes.presentation.note

import android.graphics.Bitmap


data class NoteState(
    var title: String = "",
    var description: String = "",
    var imageName: String? = null,

    var isLoading: Boolean = false,
    var titleError: String? = null,
    var error: String? = null,

    var isRemovable: Boolean = false,
    var id: String? = null,
)