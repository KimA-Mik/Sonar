package ru.kima.sonar.feature.authentication.ui.authscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class AuthScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    val state = combine(
        savedStateHandle.getStateFlow(LOGIN_KEY, ""),
        savedStateHandle.getStateFlow(PASSWORD_KEY, ""),
        isLoading
    ) { login, password, loading ->
        AuthScreenState(
            email = login,
            password = password,
            isLoading = loading
        )
    }

    fun onEvent(event: AuthScreenEvent) {
        when (event) {
            is AuthScreenEvent.LoginChanged -> onLoginChange(event.login)
            is AuthScreenEvent.PasswordChanged -> onPasswordChange(event.password)
            AuthScreenEvent.LoginClicked -> onLoginClicked()
        }
    }

    private fun onLoginChange(login: String) {
        savedStateHandle[LOGIN_KEY] = login
    }

    private fun onPasswordChange(password: String) {
        savedStateHandle[PASSWORD_KEY] = password
    }

    private fun onLoginClicked() {
        // Handle login logic here
    }

    companion object {
        private const val LOGIN_KEY = "login"
        private const val PASSWORD_KEY = "password"
    }
}