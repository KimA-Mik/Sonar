package ru.kima.sonar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.inject
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.ui.setEdgeToEdgeConfig


class MainActivity : ComponentActivity() {
    private val localConfig by inject<LocalConfigDataSource>()
    private val tokenFlow = localConfig.localConfig().map { it.apiAccessToken }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        setContent {
            SonarTheme {
                Surface {
                    val token by tokenFlow.collectAsStateWithLifecycle(null)
                    ApplicationScreen(authorised = token != null)
                }
            }
        }
    }
}
