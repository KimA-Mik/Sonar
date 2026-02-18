package ru.kima.sonar.common.ui.util

import android.icu.text.NumberFormat
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.navigation.NavController
import java.util.Locale

val LocalNavController = compositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not presented")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal SnackbarHostState not presented")
}

val LocalNumberFormat = compositionLocalWithComputedDefaultOf<NumberFormat> {
    NumberFormat.getInstance(Locale.getDefault())
}
