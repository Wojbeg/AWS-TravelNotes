package com.wojbeg.aws_travelnotes.presentation.home

import com.wojbeg.aws_travelnotes.domain.models.Note

data class HomeState (
    var notes: List<Note> = mutableListOf(),
    var isLoading: Boolean = false
)