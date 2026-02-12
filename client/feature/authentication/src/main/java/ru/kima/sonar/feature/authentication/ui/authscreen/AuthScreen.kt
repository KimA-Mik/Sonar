package ru.kima.sonar.feature.authentication.ui.authscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.feature.authentication.R

@Composable
fun AuthScreen(modifier: Modifier = Modifier) {
    val viewModel: AuthScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState(AuthScreenState())
    val onEvent = { event: AuthScreenEvent -> viewModel.onEvent(event) }
    AuthScreenContent(
        state = state,
        onEvent = onEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreenContent(
    state: AuthScreenState,
    onEvent: (AuthScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) = Box(modifier = modifier) {
    AnimatedVisibility(
        state.isLoading,
        modifier = Modifier.align(Alignment.TopCenter),
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        LoadingIndicator(
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        )
    }

    IconButton(
        onClick = {},
        modifier = Modifier
            .align(Alignment.TopEnd)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Icon(
            painterResource(ru.kima.sonar.common.ui.R.drawable.settings_24px),
            contentDescription = stringResource(R.string.content_description_login_preferences),
        )
    }
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val m = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .widthIn(max = 200.dp)
        TextField(
            value = state.email,
            onValueChange = { onEvent(AuthScreenEvent.LoginChanged(it)) },
            modifier = m,
            enabled = !state.isLoading,
            label = { Text(stringResource(R.string.label_email)) },
            singleLine = true
        )

        var isPasswordVisile by remember { mutableStateOf(false) }
        TextField(
            value = state.password,
            onValueChange = { onEvent(AuthScreenEvent.PasswordChanged(it)) },
            modifier = m,
            enabled = !state.isLoading,
            label = { Text(stringResource(R.string.label_password)) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisile = !isPasswordVisile }) {
                    Icon(
                        painterResource(
                            if (isPasswordVisile) R.drawable.visibility_off_24px
                            else R.drawable.visibility_24px
                        ),
                        contentDescription = stringResource(
                            if (isPasswordVisile) R.string.content_description_hide_password
                            else R.string.content_description_show_password
                        )
                    )
                }
            },
            visualTransformation = if (isPasswordVisile) VisualTransformation.None
            else PasswordVisualTransformation(),
            singleLine = true
        )

        Button(
            onClick = { onEvent(AuthScreenEvent.LoginClicked) },
            modifier = m,
            enabled = !state.isLoading,
        ) {
            Text(stringResource(R.string.action_login))
        }
    }
}

@Preview(name = "LoginScreen preview light theme")
@Preview(
    name = "LoginScreen preview dark theme",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun AuthScreenPreview() = SonarPreview {
    var state by remember {
        mutableStateOf(
            AuthScreenState(
                email = "email@example.com",
                password = "password123"
            )
        )
    }

    AuthScreenContent(
        state = state,
        onEvent = { state = state.copy(isLoading = !state.isLoading) },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(name = "Loading LoginScreen preview light theme")
@Preview(
    name = "Loading LoginScreen preview dark theme",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun LoadingAuthScreenPreview() = SonarPreview {
    AuthScreenContent(
        state = AuthScreenState(
            email = "email@example.com",
            password = "password123",
            isLoading = true
        ),
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}