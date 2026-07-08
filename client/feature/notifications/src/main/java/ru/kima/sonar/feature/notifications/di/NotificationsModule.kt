package ru.kima.sonar.feature.notifications.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.feature.notifications.manager.SonarNotificationsManager
import ru.kima.sonar.feature.notifications.notifications.BoundPriceNotification
import ru.kima.sonar.feature.notifications.notifications.EventNotificationFormat
import ru.kima.sonar.feature.notifications.notifications.RulesNotification
import ru.kima.sonar.feature.notifications.notifications.UnboundPriceNotification
import ru.kima.sonar.feature.notifications.service.NotificationProviderUpdater

val notificationsModule = module {
    single<SonarNotificationsManager>()

    factoryOf(::BoundPriceNotification) {
        qualifier = named(BoundPriceNotification::class.java.simpleName)
    } bind EventNotificationFormat::class
    factoryOf(::UnboundPriceNotification) {
        qualifier = named(UnboundPriceNotification::class.java.simpleName)
    } bind EventNotificationFormat::class
    factoryOf(::RulesNotification) {
        qualifier = named(RulesNotification::class.java.simpleName)
    } bind EventNotificationFormat::class
    single<NotificationProviderUpdater>()
}