package com.wojbeg.aws_travelnotes.domain.local

import android.graphics.Bitmap
import com.wojbeg.aws_travelnotes.data.local.UserData
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.presentation.components.logic.UserDataState
import kotlinx.coroutines.flow.StateFlow

interface UserData {

    fun clearUserData()
    fun getUserData(): StateFlow<UserDataState>

    fun addNotes(noteList: List<Note>)
    fun addNote(note: Note)
    fun updateNote(note: Note)
    fun deleteNote(id: String): Boolean
    fun deleteNoteAt(at: Int) : Note?
    fun resetNotes()
    fun addImageToNote(Id: String, image: Bitmap)
    fun setNoteList(noteList: List<Note>)

    fun setNewUsername(newUsername: String)
    fun setNewEmail(newEmail: String)
    fun setNewImage(profileBitmap: Bitmap? = null, profilePictureFile: String? = null)
}