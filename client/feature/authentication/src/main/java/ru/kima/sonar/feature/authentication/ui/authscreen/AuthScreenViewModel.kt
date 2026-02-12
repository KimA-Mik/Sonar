package ru.kima.sonar.feature.authentication.ui.authscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.kima.sonar.feature.authentication.uscase.LogInUseCase

class AuthScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val logIn: LogInUseCase
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

    private fun onLoginClicked() = viewModelScope.launch {
        isLoading.value = true
        val login = savedStateHandle.get<String>(LOGIN_KEY) ?: ""
        val password = savedStateHandle.get<String>(PASSWORD_KEY) ?: ""
        logIn(login, password)
        isLoading.value = false
    }

    companion object {
        private const val LOGIN_KEY = "login"
        private const val PASSWORD_KEY = "password"
    }
}