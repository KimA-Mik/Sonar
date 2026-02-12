package ru.kima.sonar.common.ui.preview

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.common.ui.util.LocalNavController
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.isNightMode

@Composable
fun SonarPreview(
    darkTheme: Boolean = isNightMode(),
    isAmoled: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController(),
        LocalSnackbarHostState provides remember { SnackbarHostState() }
    ) {
        SonarTheme(
            darkTheme = darkTheme,
//        isAmoled = isAmoled,
            dynamicColor = dynamicColor,
        ) {
            Surface {
                content()
            }
        }
    }
}