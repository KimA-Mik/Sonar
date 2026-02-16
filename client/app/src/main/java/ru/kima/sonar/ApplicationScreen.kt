package ru.kima.sonar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import ru.kima.sonar.common.ui.util.LocalNavController
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.ui.navigation.SonarNavHost

@Composable
fun ApplicationScreen(authorised: Boolean) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        SonarNavHost(navController, authorised)
    }
}