package com.wojbeg.aws_travelnotes.presentation.profile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.ImageOperationsImpl
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.data.local.UserData
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.ClearUserData_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.GetUserData_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.SetNewProfileImageUserData_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.LogOut_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.StoreProfilePicture_UseCase
import com.wojbeg.aws_travelnotes.presentation.components.logic.ImageSelecting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (
    val logoutUseCase: LogOut_UseCase,
    val storeProfilePictureUseCase: StoreProfilePicture_UseCase,
    val getUserDataUseCase: GetUserData_UseCase,
    val clearUserData_UseCase: ClearUserData_UseCase,
    val setNewProfileLocalImageUseCase: SetNewProfileImageUserData_UseCase,
    imageOperations: ImageOperationsImpl
) : ImageSelecting(imageOperations) {


    private val validationEventChannel = Channel<ProfileUIEvents>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    var profileState by mutableStateOf(ProfileState())
        private set

    val userData = getUserDataUseCase()

    fun onEvent(event: ProfileEvents) {
        when(event) {
            ProfileEvents.LogOut -> {
                logOut()
            }
            is ProfileEvents.UpdateProfile -> {
                updateProfile(event.image)
            }
        }
    }

    private fun updateProfileState(username: String? = null, email: String? = null, isLoading: Boolean = false) {
        username?.let { profileState = profileState.copy(username = it) }
        email?.let { profileState = profileState.copy(email = it) }
        profileState = profileState.copy(isLoading = isLoading)
    }

    private fun logOut() {
        viewModelScope.launch {
            logoutUseCase().collect { result ->
                when (result) {
                    is Resource.Error -> {
                        updateProfileState(isLoading = false)
                    }
                    is Resource.Loading -> {
                        updateProfileState(isLoading = true)
                    }
                    is Resource.Success -> {
                        updateProfileState(isLoading = true)
//                        userData.clearUserData()
                        clearUserData_UseCase()
                        validationEventChannel.send(ProfileUIEvents.Logout)
                    }
                }
            }
        }
    }

    private fun updateProfile(bitmap: Bitmap) {
        updateProfileState(isLoading = true)
        val path = handleImage(bitmap)
        if (path != null) {
            handleProfileImageSave(path, bitmap)
        } else {
            updateProfileState(isLoading = false)
        }
    }

    private fun handleProfileImageSave(path: String, bitmap: Bitmap) {
        viewModelScope.launch {

            storeProfilePictureUseCase(path).collect { result ->
                when(result) {
                    is Resource.Error -> {
                        updateProfileState(isLoading = false)
                    }
                    is Resource.Loading -> {
                        updateProfileState(isLoading = true)
                    }
                    is Resource.Success -> {
                        setNewProfileLocalImageUseCase(bitmap, path)
                        updateProfileState(isLoading = false)
                    }
                }
            }
        }
    }

    sealed class ProfileUIEvents {
        object Logout: ProfileUIEvents()
    }
}