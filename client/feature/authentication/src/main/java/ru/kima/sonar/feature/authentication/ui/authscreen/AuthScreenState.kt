package ru.kima.sonar.feature.authentication.ui.authscreen

import androidx.compose.runtime.Immutable

@Immutable
data class AuthScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)
