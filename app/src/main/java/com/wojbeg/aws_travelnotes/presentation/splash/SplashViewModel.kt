package com.wojbeg.aws_travelnotes.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.data.local.UserData
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.FetchAuthSession_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.LogOut_UseCase
import com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related.SignIn_UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val fetchAuthSessionUseCase: FetchAuthSession_UseCase,
    val logoutUseCase: LogOut_UseCase
): ViewModel() {

    init {
        checkAuthentication()
    }

    private val splashEventChannel = Channel<SplashUIEvents>()
    val splashEvents = splashEventChannel.receiveAsFlow()

    private fun checkAuthentication() {

        viewModelScope.launch {
            fetchAuthSessionUseCase().collect {
                when(it) {
                    is Resource.Error -> {
                        logOut()
                    }
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        it.data?.let { authSession ->
                            if (authSession.isSignedIn) {
                                splashEventChannel.send(SplashUIEvents.UserLoggedIn)
                            } else {
                                splashEventChannel.send(SplashUIEvents.UserNotFound)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun logOut() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    sealed class SplashUIEvents {
        object UserLoggedIn: SplashUIEvents()
        object UserNotFound: SplashUIEvents()
    }

}
