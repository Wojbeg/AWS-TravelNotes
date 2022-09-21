package com.wojbeg.aws_travelnotes.presentation.sign_up

data class SignUpState(
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var repeatPassword: String = "",
    var isLoading: Boolean = false,
    var usernameError: String? = null,
    var emailError: String? = null,
    var passwordError: String? = null,
    var repeatPasswordError: String? = null,
    var otherError: String? = null
)