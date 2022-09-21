package com.wojbeg.aws_travelnotes.presentation.verify

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.common.cleanText
import com.wojbeg.aws_travelnotes.data.remote.AmplifyServiceImpl
import com.wojbeg.aws_travelnotes.domain.models.LoginData
import com.wojbeg.aws_travelnotes.domain.models.SignUpData
import com.wojbeg.aws_travelnotes.domain.models.VerificationData
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.VerifyCode_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateEmail
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidatePassword
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateRepeatPassword
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateUsername
import com.wojbeg.aws_travelnotes.presentation.login.LoginState
import com.wojbeg.aws_travelnotes.presentation.sign_up.SignUpState
import com.wojbeg.aws_travelnotes.presentation.verify.VerificationCodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor (
    val verifyCodeUseCase: VerifyCode_UseCase
) : ViewModel() {

    var verificationCodeState by mutableStateOf(VerificationCodeState())
        private set

    private val validationEventChannel = Channel<AuthUIEvents>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: VerificationEvents) {
        when(event) {
            is VerificationEvents.CheckUsername -> {
                checkUsername(event.value)
            }
            is VerificationEvents.UpdateVerificationCode -> {
                updateVerificationCodeState(code = event.code)
            }
            VerificationEvents.VerifyCode -> {
                verifyCode()
            }
        }
    }

    private fun checkUsername(username: String) {
        updateVerificationCodeState(username = username)
    }

    private fun updateVerificationCodeState(code: String? = null, username: String? = null, isLoading: Boolean = false, error: String? = null) {
        code?.let { verificationCodeState = verificationCodeState.copy(code = cleanText(it)) }
        username?.let { verificationCodeState = verificationCodeState.copy(username = cleanText(it)) }
        verificationCodeState = verificationCodeState.copy(isLoading = isLoading, error = error)
    }

    private fun verifyCode() {
        updateVerificationCodeState(isLoading = true)

        val verificationData = VerificationData(verificationCodeState.username, verificationCodeState.code)

        viewModelScope.launch {

            verifyCodeUseCase(verificationData).collect { verifyResult ->
                when(verifyResult) {
                    is Resource.Error -> {
                        updateVerificationCodeState(isLoading = false, error = verifyResult.message)
                    }
                    is Resource.Loading -> {
                        updateVerificationCodeState(isLoading = true)
                    }
                    is Resource.Success -> {
                        updateVerificationCodeState(isLoading = false)
                        validationEventChannel.send(AuthUIEvents.ValidationSuccess)
                    }
                }
            }
        }
    }

    sealed class AuthUIEvents {
        object ValidationSuccess: AuthUIEvents()
    }
}