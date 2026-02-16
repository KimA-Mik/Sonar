package ru.kima.sonar.feature.securities.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.feature.securities.R

@Composable
fun SecuritiesLstScreen() {
    SecuritiesListScreenContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecuritiesListScreenContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_title_securities_list)) }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.top_bar_title_securities_list))
        }
    }
}

@Preview
@Composable
private fun SecuritiesListScreenPreview() = SonarPreview {
    SecuritiesListScreenContent()
}