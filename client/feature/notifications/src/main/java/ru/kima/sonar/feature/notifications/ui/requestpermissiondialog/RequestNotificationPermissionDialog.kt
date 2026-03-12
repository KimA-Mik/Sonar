package ru.kima.sonar.feature.notifications.ui.requestpermissiondialog

import android.Manifest
import android.content.res.Resources
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.feature.notifications.R

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
internal fun RequestNotificationPermissionDialog(
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val resources = LocalResources.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        snackbarHostState.showPermissionSnackbar(coroutineScope, granted, resources)
        if (granted) {
            navigator.goBack()
        }
    }
    SonarAlertDialog(
        confirmButton = {
            TextButton(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                Text(stringResource(CommonStrings.action_grant))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = {
                snackbarHostState.showPermissionSnackbar(coroutineScope, false, resources)
                navigator.goBack()
            }) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.title_grant_notifications_permission)) },
        text = { Text(stringResource(R.string.body_grant_notifications_permission)) }
    )
}

fun SnackbarHostState.showPermissionSnackbar(
    scope: CoroutineScope,
    isGranted: Boolean,
    resources: Resources
) {
    val message = if (isGranted) {
        resources.getString(R.string.snackbar_permission_granted)
    } else {
        resources.getString(R.string.snackbar_permission_denied)
    }
    scope.launch {
        showSnackbar(message, withDismissAction = true)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun RequestNotificationPermissionDialogPreview() = SonarPreview {
    RequestNotificationPermissionDialog(
        modifier = Modifier.padding(16.dp)
    )
}