package com.lq.joy.data

sealed class BaseResult<out R> {
    data class Success<out T>(val data: T) : BaseResult<T>()
    data class Error(val exception: Exception) : BaseResult<Nothing>()
}

fun <T> BaseResult<T>.successOr(fallback: T): T {
    return (this as? BaseResult.Success<T>)?.data ?: fallback
}