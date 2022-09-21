package com.wojbeg.aws_travelnotes.domain.models

import android.graphics.Bitmap
import com.amplifyframework.datastore.generated.model.NoteData

data class Note(val id: String, val name: String, val description: String, var imageName: String? =null) {
    override fun toString(): String {
        return name
    }

    val data : NoteData
        get() = NoteData.builder()
            .name(this.name)
            .description(this.description)
            .image(this.imageName)
            .id(this.id)
            .build()

    var image: Bitmap? = null

    companion object {
        fun from(noteData : NoteData) : Note {
            val result = Note(noteData.id, noteData.name, noteData.description, noteData.image)
            return result
        }
    }
}
