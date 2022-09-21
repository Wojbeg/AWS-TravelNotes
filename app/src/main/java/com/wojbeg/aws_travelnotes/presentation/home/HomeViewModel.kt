package com.wojbeg.aws_travelnotes.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.common.TAG
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.domain.models.UserAttribute
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.*
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.GetNotes_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote.RetrieveImage_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.GetUserInfo_UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    val queryNotes_UseCase: GetNotes_UseCase,
    val getUserInfoUseCase: GetUserInfo_UseCase,
    val retrieveImageUseCase: RetrieveImage_UseCase,
    val setNewProfileLocalImageUseCase: SetNewProfileImageUserData_UseCase,
    val setNewUserNameUseCase: SetUsernameUserData_UseCase,
    val setNewEmail_UseCase: SetEmailUserData_UseCase,
    val getUserDataUseCase: GetUserData_UseCase,
    val setNoteListUseCase: SetNoteListUserData_UseCase
): ViewModel() {

    val userData = getUserDataUseCase()

    var homeState by mutableStateOf(HomeState())
        private set

    init {
        downloadNotes()
        getUsersAttributes()
    }

    private fun updateState(notes: List<Note>? = null, isLodaing: Boolean? = null) {
        notes?.let { homeState = homeState.copy(notes = notes) }
        isLodaing?.let { homeState = homeState.copy(isLoading = isLodaing) }
    }

    private fun downloadNotes() {
        updateState(isLodaing = true)

        viewModelScope.launch {
            queryNotes_UseCase().collect { result ->
                when(result) {
                    is Resource.Error -> {
                        updateState(isLodaing = false)
                    }
                    is Resource.Loading -> {
                        updateState(isLodaing = true)
                    }
                    is Resource.Success -> {
                        updateState(result.data, false)
                        result.data?.let {
                            setNoteListUseCase(it)
                            getNotesImages(it)
                        }
                    }
                }
            }
        }
    }

    private fun getNotesImages(notes: List<Note>) {

        viewModelScope.launch {
            notes.forEach { note ->
                note.imageName?.let {
                    if (note.image == null) {

                        retrieveImageUseCase(it).collect { retrieveImageResult ->
                            when (retrieveImageResult) {
                                is Resource.Error -> {}
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    note.image = retrieveImageResult.data?.image
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    private fun getUsersAttributes() {

        viewModelScope.launch(Dispatchers.Default) {

            getUserInfoUseCase(
                listOf(
                    UserAttribute.PICTURE,
                    UserAttribute.EMAIL,
                    UserAttribute.EMAIL_VERIFIED,
                    UserAttribute.NAME,
                    UserAttribute.NICKNAME
                )
            ).collect {
                when(it) {
                    is Resource.Error -> {
                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        it.data?.let {
                            handleProfileInfo(it)
                        }
                    }
                }
            }
        }

    }

    private fun handleProfileInfo(info: Map<UserAttribute, String>) {
        info.forEach { (userAttribute, value) ->
            when(userAttribute) {
                UserAttribute.PICTURE -> {
                    Log.i(TAG, "PIC: $value")
                    profileImageRetrieved(value)
                }
                UserAttribute.EMAIL -> {
                    Log.i(TAG, "Email: $value")
                    setNewEmail_UseCase(value)
                }
                UserAttribute.EMAIL_VERIFIED -> {
                    Log.i(TAG, "Email verified: $value")
                }
                UserAttribute.NAME -> {
                    Log.i(TAG, "Name: $value")
                    setNewUserNameUseCase(value)
                }
                UserAttribute.PROFILE -> {
                    Log.i(TAG, "Profile: $value")
                }
                UserAttribute.NICKNAME -> {
                    Log.i(TAG, "Nickname: $value")
                }
                else -> {}
            }
        }
    }

    private fun profileImageRetrieved(imageUrl: String) {
        viewModelScope.launch {

            retrieveImageUseCase(imageUrl).collect {
                when (it) {
                    is Resource.Error -> {
                        Log.e(TAG, "Nickname: $it")
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        it.data?.let {
                            setNewProfileLocalImageUseCase(it.image, it.filePath)
                        }
                    }
                }
            }
        }
    }
}