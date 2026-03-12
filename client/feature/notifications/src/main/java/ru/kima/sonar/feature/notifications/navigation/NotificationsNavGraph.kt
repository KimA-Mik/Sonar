package ru.kima.sonar.feature.notifications.navigation

import android.os.Build
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.feature.notifications.ui.requestpermissiondialog.RequestNotificationPermissionDialog

fun EntryProviderScope<NavKey>.notificationsNavGraph() {
    entry<NotificationsGraph.RequestNotificationsPermissionDialog>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestNotificationPermissionDialog()
        } else {
            LocalNavigator.current.goBack()
        }
    }
}