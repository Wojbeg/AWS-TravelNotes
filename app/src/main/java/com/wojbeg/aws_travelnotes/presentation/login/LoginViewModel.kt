package com.wojbeg.aws_travelnotes.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthException
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.common.cleanText
import com.wojbeg.aws_travelnotes.domain.models.LoginData
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local.SetUsernameUserData_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.GetUsername_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.ResendVerificationCode_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.SignIn_UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    val signInUseCase: SignIn_UseCase,
    val getUserNameUseCase: GetUsername_UseCase,
    val resendVerificationCodeUseCase: ResendVerificationCode_UseCase,
    val setUsernameUseCase: SetUsernameUserData_UseCase
) : ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

    private val validationEventChannel = Channel<LoginUIEvents>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.UpdateLoginUsername -> {
                updateLoginState(username = event.value)
            }
            LoginEvents.Login -> {
                login()
            }
            is LoginEvents.UpdateLoginPassword -> {
                updateLoginState(password = event.value)
            }
        }
    }

    private fun updateLoginState(username: String? = null, password: String? = null, isLoading: Boolean = false, emailError: String? = null, passwordError: String? = null) {
        username?.let { loginState = loginState.copy(username = cleanText(it)) }
        password?.let { loginState = loginState.copy(password = cleanText(it)) }
        loginState = loginState.copy(isLoading = isLoading)
        emailError.let { loginState = loginState.copy(emailError = it) }
        passwordError.let { loginState = loginState.copy(passwordError = it) }
    }

    private fun login() {
        updateLoginState(isLoading = true)

        val loginData = LoginData(loginState.username, loginState.password)
        viewModelScope.launch {
            signInUseCase(loginData).collect { loginResult->
                when(loginResult) {

                    //Additional cases can be added and the checking system improved
                    //This one is just a simple example that provides user with a minimal product ready to go

                    is Resource.Error -> {
                        if (loginResult.data is AuthException.UserNotConfirmedException) {
                            handleResendVerificationCode()
                        } else {
                            updateLoginState(emailError = "Invalid Username Or Password", passwordError = "Invalid Username Or Password", isLoading = false)
                        }
                    }
                    is Resource.Loading -> {
                        updateLoginState(isLoading = true)
                    }
                    is Resource.Success -> {
                        setUsernameUseCase(getUserNameUseCase())

                        validationEventChannel.send(LoginUIEvents.LoginSuccess)
                        updateLoginState(isLoading = false)
                    }
                }
            }
        }

    }

    private fun handleResendVerificationCode() {
        viewModelScope.launch {

            resendVerificationCodeUseCase(loginState.username).collect { resendResult ->
                when (resendResult) {
                    is Resource.Error -> {
                        updateLoginState(emailError = resendResult.message)
                    }
                    is Resource.Loading -> {
                        updateLoginState(isLoading = true)
                    }
                    is Resource.Success -> {
                        validationEventChannel.send(LoginUIEvents.LoginError)
                    }
                }
            }
        }
    }

    sealed class LoginUIEvents {
        object LoginSuccess: LoginUIEvents()
        object LoginError: LoginUIEvents()
    }
}