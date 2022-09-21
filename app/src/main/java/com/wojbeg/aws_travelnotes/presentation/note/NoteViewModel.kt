package com.wojbeg.aws_travelnotes.presentation.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.ImageOperationsImpl
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.*
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.CreateNote_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.DeleteNote_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.UpdateNote_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateTitle
import com.wojbeg.aws_travelnotes.presentation.components.logic.ImageSelecting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor (
    val createNoteUseCase: CreateNote_UseCase,
    val updateNoteUseCase: UpdateNote_UseCase,
    val deleteNoteUseCase: DeleteNote_UseCase,
    getUserDataUseCase: GetUserData_UseCase,
    val addNoteUserData_UseCase: AddNoteUserData_UseCase,
    val updateNoteUserData_UseCase: UpdateNoteUserData_UseCase,
    val deleteNoteUserData_UseCase: DelteNoteUserData_UseCase,
    private val validateTitle: ValidateTitle,
    imageOperations: ImageOperationsImpl
) : ImageSelecting(imageOperations) {

    var noteState by mutableStateOf(NoteState())
        private set

    private val uiEventChannel = Channel<NoteUIEvents>()
    val noteUIEvents = uiEventChannel.receiveAsFlow()

    val userData = getUserDataUseCase()

    fun onEvent(events: NoteEvents) {
        when(events) {
            is NoteEvents.HandleImage -> {
                handleImage(events.image)
            }
            NoteEvents.Remove -> {
                remove()
            }
            NoteEvents.SaveNote -> {
                saveNote()
            }
            is NoteEvents.UpdateDescription -> {
                updateNoteState(description = events.value)
            }
            is NoteEvents.UpdateTitle -> {
                updateNoteState(title = events.value)
            }
            is NoteEvents.CheckId -> {
                checkId(events.id)
            }
        }
    }

    private fun updateNoteState(title: String? = null, description: String? = null, isLoading: Boolean? = null, titleError: String? = null, error: String? = null) {
        title?.let { noteState = noteState.copy(title = title) }
        description?.let { noteState = noteState.copy(description = description) }
        isLoading?.let { noteState = noteState.copy(isLoading = isLoading) }
        noteState = noteState.copy(titleError = titleError, error = error)
    }

    private fun checkId(id: String? = null) {
        updateNoteState(isLoading = true)

        id?.let {
            val note = userData.value.notes.firstOrNull { note ->
                note.id == id
            }

            if (note != null) {
                setId(id)
                updateNoteState(title = note.name, description = note.description)
                updateImageState(imageBitmap = note.image)
                setRemovable(true)
            } else {
                viewModelScope.launch {
                    uiEventChannel.send(NoteUIEvents.NotFound)
                }
            }
        }
        updateNoteState(isLoading = false)
    }

    private fun saveNote() {
        updateNoteState(isLoading = true)

        val titleResult = validateTitle.execute(noteState.title)

        val hasErrors = listOf(
            titleResult,
        ).any { !it.successful }

        if (hasErrors) {
            updateNoteState(
                titleError = titleResult.errorMessage,
                isLoading = false
            )
        }

        if (!hasErrors) {

            val id = noteState.id ?: UUID.randomUUID().toString()
            if (imageState.image != null && imageState.filePath!=null) {
                noteState.imageName = id
            }

            val noteToSaveOrUpdate = Note(
                id,
                noteState.title,
                noteState.description,
                noteState.imageName,
            )

            if (imageState.image != null) {
                noteToSaveOrUpdate.image = imageState.image
            }

            viewModelScope.launch {
                val result = if (noteState.id == null ) {
                    createNoteUseCase(noteToSaveOrUpdate, imageState.filePath)
                } else {
                    updateNoteUseCase(noteToSaveOrUpdate, imageState.filePath)
                }

                result.collect { createUpdateResult ->
                    when(createUpdateResult) {
                        is Resource.Error -> {
                            updateNoteState(
                                error = createUpdateResult.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            updateNoteState(
                                isLoading = true
                            )
                        }
                        is Resource.Success -> {
                            updateNoteState(
                                isLoading = false
                            )

                            if (noteState.id == null ) {
                                addNoteUserData_UseCase(noteToSaveOrUpdate)
                            } else {
                                updateNoteUserData_UseCase(noteToSaveOrUpdate)
                            }

                            viewModelScope.launch {
                                uiEventChannel.send(NoteUIEvents.Success)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setRemovable(value: Boolean) {
        noteState = noteState.copy(isRemovable = value)
    }

    private fun setId(id: String?) {
        noteState = noteState.copy(id=id)
    }

    private fun remove() {

        val note: Note = Note(noteState.id!!, noteState.title,noteState.description, noteState.imageName)

        viewModelScope.launch {

            deleteNoteUseCase(note).collect { result ->
                when(result) {
                    is Resource.Error -> {
                    }
                    is Resource.Loading -> {
                        updateNoteState(isLoading = true)
                    }
                    is Resource.Success -> {
                        updateNoteState(isLoading = false)
                        deleteNoteUserData_UseCase(note.id)
                        uiEventChannel.send(NoteUIEvents.Success)
                    }
                }
            }
        }
    }

    sealed class NoteUIEvents {
        object Success: NoteUIEvents()
        object NotFound: NoteUIEvents()
    }

}

