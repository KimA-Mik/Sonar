package ru.kima.sonar.feature.authentication.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object AuthGraph {
    @Serializable
    object Login : NavKey
}