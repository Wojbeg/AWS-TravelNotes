package com.wojbeg.aws_travelnotes.presentation.verify

sealed class VerificationEvents {
    data class CheckUsername(val value: String): VerificationEvents()
    data class UpdateVerificationCode(val code: String): VerificationEvents()
    object VerifyCode: VerificationEvents()
}