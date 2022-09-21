package com.wojbeg.aws_travelnotes.domain.use_case.use_cases.notes_related.remote

import com.amplifyframework.storage.StorageAccessLevel
import com.wojbeg.aws_travelnotes.common.Resource
import com.wojbeg.aws_travelnotes.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoreImage_UseCase  @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(filePath: String, key: String, accessLevel: StorageAccessLevel = StorageAccessLevel.PRIVATE): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        emit(repository.storeImage(filePath, key, accessLevel))
    }.flowOn(Dispatchers.IO)
}