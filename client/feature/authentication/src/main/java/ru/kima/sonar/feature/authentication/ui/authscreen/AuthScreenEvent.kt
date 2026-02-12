package ru.kima.sonar.feature.authentication.ui.authscreen

sealed interface AuthScreenEvent {
    data class LoginChanged(val login: String) : AuthScreenEvent
    data class PasswordChanged(val password: String) : AuthScreenEvent
    data object LoginClicked : AuthScreenEvent
}