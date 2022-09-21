package com.wojbeg.aws_travelnotes.common

import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

suspend fun <T> mutate(request: GraphQLRequest<T>): GraphQLResponse<T> =
    suspendCancellableCoroutine { continuation ->
        val apiCall = Amplify.API.mutate(
            request,
            {   response ->
                continuation.resume(response)
            },
            {   exception ->
                continuation.resumeWithException(exception)
            }
        )
        continuation.invokeOnCancellation { apiCall?.cancel() }
    }
