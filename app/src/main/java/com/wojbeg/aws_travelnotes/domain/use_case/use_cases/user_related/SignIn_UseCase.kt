package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related

import com.amplifyframework.auth.AuthException
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.LoginData
import com.wojbeg.aws_travelnotes.domain.models.Note
import com.wojbeg.aws_travelnotes.domain.remote.AmplifyService
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignIn_UseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(loginData: LoginData): Flow<Resource<AuthException?>> = flow {
        emit(Resource.Loading())
        emit(repository.login(loginData))
    }.flowOn(Dispatchers.IO)
}