package ru.kima.sonar.feature.portfolios.ui.addentries

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.util.LocalNavigator

@Composable
internal fun AddEntriesScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    AddEntriesScreenContent(modifier = modifier.fillMaxSize())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEntriesScreenContent(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    Scaffold(
        modifier = modifier, topBar = {
            AppBar(
                titleContent = {},
                navigateUp = { navigator.goBack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Add entries screen")
        }
    }
}