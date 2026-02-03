package ru.kima.sonar.common.ui.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.common.ui.util.isNightMode

@Composable
fun SonarPreview(
    darkTheme: Boolean = isNightMode(),
    isAmoled: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
//    CompositionLocalProvider(
//        LocalNavController provides rememberNavController()
//    ) {
    SonarTheme(
        darkTheme = darkTheme,
//        isAmoled = isAmoled,
        dynamicColor = dynamicColor,
    ) {
        Surface {
            content()
        }
    }
//    }
}