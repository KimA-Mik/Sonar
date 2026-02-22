package ru.kima.sonar.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.kima.sonar.feature.authentication.ui.authscreen.AuthScreen

fun EntryProviderScope<NavKey>.authNavGraph() {
    entry<AuthGraph.Login> { AuthScreen() }
}