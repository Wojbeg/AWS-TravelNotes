package com.wojbeg.aws_travelnotes.presentation.profile

data class ProfileState (
    var username: String = "",
    var email: String = "",
    var isLoading: Boolean = false,
)