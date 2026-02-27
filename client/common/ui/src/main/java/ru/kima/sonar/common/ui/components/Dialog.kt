package ru.kima.sonar.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.kima.sonar.common.ui.util.ProvideContentColorTextStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialog(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    showDivider: Boolean = false,
    actions: @Composable (() -> Unit)? = null,
    body: @Composable () -> Unit,
) = Surface(
    modifier = modifier.widthIn(280.dp, 560.dp),
    shape = RoundedCornerShape(28.dp),
    color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Column(modifier = Modifier.padding(24.dp)) {
        icon?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
                    it()
                }
            }
        }

        title?.let {
            Box(
                modifier = Modifier
                    .align(if (icon != null) Alignment.CenterHorizontally else Alignment.Start)
                    .padding(bottom = 16.dp)
            ) {
                ProvideContentColorTextStyle(
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.typography.headlineSmall
                ) {
                    it()
                }
            }
        }

        if (showDivider) {
            HorizontalDivider()
        }

        ProvideContentColorTextStyle(
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.typography.bodyMedium
        ) {
            body()
        }

        actions?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp)
            ) {
                ProvideContentColorTextStyle(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.typography.labelLarge
                ) {
                    it()
                }
            }
        }
    }
}

@Composable
fun SonarAlertDialog(
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    showDivider: Boolean = false,
) = BasicDialog(
    modifier = modifier,
    icon = icon,
    title = title,
    showDivider = showDivider,
    actions = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dismissButton?.invoke()
            confirmButton()
        }
    },
    body = { text?.invoke() }
)
