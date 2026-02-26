package ru.kima.sonar.server.data.user.model

sealed interface UserDataError {
    data object NotFound : UserDataError

    data class UnknownError(val error: Throwable) : UserDataError
}