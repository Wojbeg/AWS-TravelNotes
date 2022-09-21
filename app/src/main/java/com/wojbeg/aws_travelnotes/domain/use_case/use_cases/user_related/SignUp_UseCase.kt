package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related

import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.models.SignUpData
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignUp_UseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(signUpData: SignUpData): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        emit(repository.signUp(signUpData))
    }.flowOn(Dispatchers.IO)
}