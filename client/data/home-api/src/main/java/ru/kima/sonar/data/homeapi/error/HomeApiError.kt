package ru.kima.sonar.data.homeapi.error

sealed interface HomeApiError {
    data object NetworkError : HomeApiError
    data object Unauthorized : HomeApiError
    data object BadRequest : HomeApiError
    data object Forbidden : HomeApiError
    data object InternalServerError : HomeApiError
    data class UnknownApiError(val code: Int) : HomeApiError
    data class UnknownError(val exception: Exception) : HomeApiError
}