package ru.kima.sonar.data.homeapi.model.mapper

import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider

fun LocalNotificationProvider.toNotificationProvider(): NotificationProvider = when (this) {
    LocalNotificationProvider.FIREBASE -> NotificationProvider.FIREBASE
    LocalNotificationProvider.HUAWEI_PUSH_KIT -> NotificationProvider.HUAWEI_PUSH_KIT
}
