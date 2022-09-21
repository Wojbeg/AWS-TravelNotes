package com.wojbeg.aws_travelnotes.presentation.sign_up

sealed class SignUpEvents {
    data class UpdateUsername(val value: String): SignUpEvents()
    data class UpdateEmail(val value: String): SignUpEvents()
    data class UpdatePassword(val value: String): SignUpEvents()
    data class UpdateRepeatPassword(val value: String): SignUpEvents()
    object SignUp: SignUpEvents()
}