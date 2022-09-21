package com.wojbeg.aws_travelnotes.data.local

import android.graphics.Bitmap
import com.wojbeg.aws_travelnotes.domain.local.UserData
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.presentation.components.logic.UserDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserData : UserData {

    private val _data: MutableStateFlow<UserDataState> = MutableStateFlow(UserDataState())
    val data: StateFlow<UserDataState> = _data.asStateFlow()

    override fun getUserData(): StateFlow<UserDataState> = data

    private fun updateData(username: String? = null, email: String? = null,
                           notes: MutableList<Note>? = null, profilePictureFile: String? = null,
                           profileBitmap: Bitmap? = null) {
        username?.let { _data.value = _data.value.copy(username = it) }
        email?.let { _data.value = _data.value.copy(email = it) }
        notes?.let { setNoteList(it) }
        profilePictureFile?.let { _data.value = _data.value.copy(profilePictureFile = it) }
        profileBitmap?.let { _data.value = _data.value.copy(profileBitmap = it) }

    }

    override fun addNotes(noteList: List<Note>) {
        val newNotes = (_data.value.notes.toList() + noteList).distinctBy { it.id }

        _data.value.apply {
            notes.clear()
            notes.addAll(newNotes)
        }
    }

    override fun addNote(note: Note) {
        _data.value.notes.add(note)
    }

    override fun updateNote(note: Note) {

        val index = _data.value.notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            _data.value.notes[index] = note
        }
    }

    override fun setNoteList(noteList: List<Note>) {

        _data.value.apply {
            notes.clear()
            notes.addAll(noteList)
        }
    }

    override fun deleteNote(id: String): Boolean {
        val noteToDelete = _data.value.notes.find { it.id == id }

        return _data.value.notes.remove(noteToDelete)
    }

    override fun deleteNoteAt(at: Int) : Note? {
        return _data.value.notes.removeAt(at)
    }

    override fun resetNotes() {
        _data.value.notes.clear()
    }

    override fun addImageToNote(Id: String, image: Bitmap) {
        val index = _data.value.notes.indexOfFirst { it.id == Id }
        if (index != -1) {
            _data.value.notes[index].image = image
        }
    }

    override fun setNewUsername(newUsername: String) {
        updateData(username = newUsername)
    }

    override fun clearUserData() {
        _data.value = UserDataState()
    }

    override fun setNewImage(profileBitmap: Bitmap?, profilePictureFile: String?) {
        updateData(profileBitmap = profileBitmap, profilePictureFile = profilePictureFile)
    }

    override fun setNewEmail(newEmail: String) {
        updateData(email = newEmail)
    }
}
