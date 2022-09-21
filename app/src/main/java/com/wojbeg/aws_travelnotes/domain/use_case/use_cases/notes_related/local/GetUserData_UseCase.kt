package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.local

import android.graphics.Bitmap
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import com.wojbeg.aws_travelnotes.presentation.components.logic.UserDataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserData_UseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): StateFlow<UserDataState> = repository.getUserData()
}