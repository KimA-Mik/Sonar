package ru.kima.sonar.data.finam.model

sealed interface FinamRepositoryError {
    data class NotFound(val ticker: String) : FinamRepositoryError
    data object NoConnection : FinamRepositoryError
    data class Unknown(val e: Exception) : FinamRepositoryError
}