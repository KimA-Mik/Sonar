package ru.kima.sonar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.messaging
import ru.kima.sonar.common.ui.theme.SonarTheme
import ru.kima.sonar.ui.setEdgeToEdgeConfig


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        val splashScreen = installSplashScreen()
        initFirebase()
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

    private fun initFirebase() {
        val availability = GoogleApiAvailability.getInstance()
        val res = availability.isGooglePlayServicesAvailable(this)
        if (res != ConnectionResult.SUCCESS) return

        Firebase.messaging.isAutoInitEnabled = true
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
    }
}
