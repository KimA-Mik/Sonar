package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun PortfolioDetailsScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
//    val viewModel: PortfolioDetailsViewModel = koinViewModel {
//        parametersOf(portfolioId)
//    }

    PortfolioDetailsScreenContent(
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun PortfolioDetailsScreenContent(
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text("Portfolio details")
    }
}