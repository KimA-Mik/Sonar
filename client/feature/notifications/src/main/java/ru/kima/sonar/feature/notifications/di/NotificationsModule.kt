package ru.kima.sonar.feature.notifications.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.feature.notifications.manager.SonarNotificationsManager

val notificationsModule = module {
    single<SonarNotificationsManager>()
}