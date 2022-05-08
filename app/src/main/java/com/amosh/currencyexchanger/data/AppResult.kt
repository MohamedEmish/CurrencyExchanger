package com.amosh.currencyexchanger.data

sealed class AppResult<out T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(
        data: T?,
    ) :
        AppResult<T>(data)

    class Error<T>(
        message: String? = null,
    ) : AppResult<T>(message = message)

    class Loading<T> : AppResult<T>()
}