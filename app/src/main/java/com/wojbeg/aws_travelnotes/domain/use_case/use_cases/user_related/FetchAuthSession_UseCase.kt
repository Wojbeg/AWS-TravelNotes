package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related

import com.amplifyframework.auth.AuthSession
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FetchAuthSession_UseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<Resource<AuthSession>> = flow {
        emit(Resource.Loading())
        emit(repository.fetchAuthSession())
    }.flowOn(Dispatchers.IO)
}