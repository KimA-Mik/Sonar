package ru.kima.sonar.feature.notifications.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object NotificationsGraph {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Serializable
    data object RequestNotificationsPermissionDialog : NavKey
}