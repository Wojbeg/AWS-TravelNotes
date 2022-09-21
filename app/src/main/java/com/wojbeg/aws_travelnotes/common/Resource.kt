package com.wojbeg.aws_travelnotes.common

sealed class Resource<T>(val data: T? = null, val message: String? = null, val code: Int = -1) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, code: Int = -1) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}