package com.wojbeg.aws_travelnotes.presentation.login

data class LoginState(
    var username: String = "",
    var password: String = "",
    var emailError: String? = null,
    var passwordError: String? = null,
    var isLoading: Boolean = false
)
