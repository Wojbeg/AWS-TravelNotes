package com.wojbeg.aws_travelnotes.presentation.login

sealed class LoginEvents {
    data class UpdateLoginUsername(val value: String): LoginEvents()
    data class UpdateLoginPassword(val value: String): LoginEvents()
    object Login: LoginEvents()
}