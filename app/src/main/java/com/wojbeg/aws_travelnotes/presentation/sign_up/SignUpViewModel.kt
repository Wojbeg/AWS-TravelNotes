package com.wojbeg.aws_travelnotes.presentation.sign_up

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.common.cleanText
import com.wojbeg.aws_travelnotes.domain.models.LoginData
import com.wojbeg.aws_travelnotes.domain.models.SignUpData
import com.wojbeg.aws_travelnotes.domain.models.VerificationData
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.SignUp_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateEmail
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidatePassword
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateRepeatPassword
import com.wojbeg.aws_travelnotes.domain.use_case.validation.ValidateUsername
import com.wojbeg.aws_travelnotes.presentation.login.LoginState
import com.wojbeg.aws_travelnotes.presentation.verify.VerificationCodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor (
    val signUpUseCase: SignUp_UseCase,
    private val validateUsername: ValidateUsername,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatPassword ,
) : ViewModel() {

    var signUpState by mutableStateOf(SignUpState())
        private set

    private val validationEventChannel = Channel<SignUpUIEvents>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: SignUpEvents){
        when(event) {
            SignUpEvents.SignUp -> {
                signUp()
            }
            is SignUpEvents.UpdateEmail -> {
                updateSignUpState(email = event.value)
            }
            is SignUpEvents.UpdatePassword -> {
                updateSignUpState(password = event.value)
            }
            is SignUpEvents.UpdateRepeatPassword -> {
                updateSignUpState(repeatPassword = event.value)
            }
            is SignUpEvents.UpdateUsername -> {
                updateSignUpState(username = event.value)
            }
        }
    }

    private fun updateSignUpState(username: String? = null, email: String? = null, password: String? = null, repeatPassword: String? = null,
                          isLoading: Boolean = false, usernameError: String? = null, emailError: String? = null, passwordError: String? = null,
                          repeatPasswordError: String? = null, otherError: String? = null) {
        username?.let {
            signUpState = signUpState.copy(username = cleanText(it))
        }
        email?.let { signUpState = signUpState.copy(email = cleanText(it)) }
        password?.let { signUpState = signUpState.copy(password = cleanText(it)) }
        repeatPassword?.let { signUpState = signUpState.copy(repeatPassword = cleanText(it)) }
        signUpState = signUpState.copy(isLoading = isLoading, usernameError = usernameError, emailError = emailError, passwordError = passwordError, repeatPasswordError = repeatPasswordError, otherError = otherError)
    }

    private fun signUp() {
        updateSignUpState(isLoading = true)

        val usernameResult = validateUsername.execute(signUpState.username)
        val emailResult = validateEmail.execute(signUpState.email)
        val passwordResult = validatePassword.execute(signUpState.password)
        val repeatPassword = validateRepeatedPassword.execute(signUpState.password, signUpState.repeatPassword)

        val hasErrors = listOf(
            usernameResult,
            emailResult,
            passwordResult,
            repeatPassword,
        ).any { !it.successful }

        if (hasErrors) {
            updateSignUpState(isLoading = false,
                emailError = emailResult.errorMessage,
                usernameError = usernameResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                repeatPasswordError = repeatPassword.errorMessage
            )
        }

        if (!hasErrors) {
            val signUpData = SignUpData(signUpState.username, signUpState.email, signUpState.password)

            viewModelScope.launch {

                signUpUseCase(signUpData).collect { signUpResult ->
                    when(signUpResult) {
                        is Resource.Error -> {
                            updateSignUpState(isLoading = false, otherError = signUpResult.message)
                        }
                        is Resource.Loading -> {
                            updateSignUpState(isLoading = true)
                        }
                        is Resource.Success -> {
                            updateSignUpState(isLoading = false)
                            validationEventChannel.send(SignUpUIEvents.RegisterSuccess)
                        }
                    }
                }
            }
        }
    }

    sealed class SignUpUIEvents {
        object RegisterSuccess: SignUpUIEvents()
    }
}