package ru.kima.sonar.common.ui.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not presented")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal SnackbarHostState not presented")
}
