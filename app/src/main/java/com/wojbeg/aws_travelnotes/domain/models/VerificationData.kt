package com.wojbeg.aws_travelnotes.domain.models

data class VerificationData (
    val username: String,
    val verificationCode: String,
)