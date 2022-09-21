package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.user_related

import com.amplifyframework.storage.StorageAccessLevel
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoreProfilePicture_UseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(filePath: String): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        emit(repository.storeProfilePicture(filePath))
    }.flowOn(Dispatchers.IO)
}