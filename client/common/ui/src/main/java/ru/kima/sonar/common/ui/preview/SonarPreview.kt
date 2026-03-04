package ru.kima.sonar.common.ui.preview

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.event.ResultEventBus
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.navigation.rememberNavigationState
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.isNightMode

@Serializable
internal object DummyNavKey : NavKey

@Composable
fun SonarPreview(
    darkTheme: Boolean = isNightMode(),
    isAmoled: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val navigationState = rememberNavigationState(DummyNavKey, setOf(DummyNavKey))
    CompositionLocalProvider(
        LocalNavigator provides remember { Navigator(navigationState) },
        LocalSnackbarHostState provides remember { SnackbarHostState() },
        LocalResultEventBus provides remember { ResultEventBus() }
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