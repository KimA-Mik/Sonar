package ru.kima.sonar.feature.notifications.di

import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.feature.notifications.notifications.BoundPriceNotification
import ru.kima.sonar.feature.notifications.notifications.UnboundPriceNotification

internal fun NotificationEvent.qualifier() = when (this) {
    is BoundPriceEvent -> BoundPriceNotification::class.java.simpleName
    is UnboundPriceEvent -> UnboundPriceNotification::class.java.simpleName
}
