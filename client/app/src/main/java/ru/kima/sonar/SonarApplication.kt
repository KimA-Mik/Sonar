package ru.kima.sonar

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.kima.sonar.common.ui.event.ResultEventBus
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.di.applicationModule

class SonarApplication : Application() {
    private lateinit var job: Job
    private lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SonarApplication)
            modules(applicationModule)

        }

        job = Job()
        applicationScope = CoroutineScope(Dispatchers.Default + job)
        initialize()
    }

    private fun initialize() {
        val lc by inject<LocalConfigDataSource>()
        applicationScope.launch(Dispatchers.IO) {
            lc.localConfig()
                .map { it.apiAccessToken }
                .distinctUntilChanged()
                .collect {
                    _loggedIn.value = it != null
                    initialized = true
                }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        resultEventBus.channelMap.clear()
    }

    companion object {
        var initialized = false
            private set
        private val _loggedIn = MutableStateFlow(false)
        val loggedIn = _loggedIn.asStateFlow()
        val resultEventBus = ResultEventBus()
    }
}