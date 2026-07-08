package ru.kima.sonar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.ui.setEdgeToEdgeConfig


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !SonarApplication.initialized }
        setContent {
            SonarTheme {
                Surface {
                    val authorised by SonarApplication.loggedIn.collectAsState()
                    ApplicationScreen(authorised = authorised)
                }
            }
        }
    }
}
