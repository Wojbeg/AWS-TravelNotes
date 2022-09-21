package com.wojbeg.aws_travelnotes.presentation.verify

data class VerificationCodeState(
    var username: String = "",
    var code: String = "",
    var isLoading: Boolean = false,
    var error: String? = null
)

