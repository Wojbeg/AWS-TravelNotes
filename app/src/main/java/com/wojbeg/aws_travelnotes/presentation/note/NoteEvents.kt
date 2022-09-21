package com.wojbeg.aws_travelnotes.presentation.note

import android.graphics.Bitmap

sealed class NoteEvents {
    data class HandleImage(val image: Bitmap): NoteEvents()
    object Remove: NoteEvents()
    data class UpdateTitle(val value: String): NoteEvents()
    data class UpdateDescription(val value: String): NoteEvents()
    object SaveNote: NoteEvents()
    data class CheckId(val id: String?): NoteEvents()
}