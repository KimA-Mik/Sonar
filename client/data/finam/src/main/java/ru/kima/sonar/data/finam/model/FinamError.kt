package ru.kima.sonar.data.finam.model

sealed interface FinamError {
    data class Unknown(val e: Exception) : FinamError
    data class RequestError(val code: String, val message: String) : FinamError
    data class RequestFailed(val code: Int) : FinamError
}