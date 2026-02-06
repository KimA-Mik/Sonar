package ru.kima.sonar

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.kima.sonar.di.commonModule
import ru.kima.sonar.di.dataModule
import ru.kima.sonar.di.featureModule

class SonarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SonarApplication)
            modules(commonModule(), dataModule(), featureModule())
        }
    }
}