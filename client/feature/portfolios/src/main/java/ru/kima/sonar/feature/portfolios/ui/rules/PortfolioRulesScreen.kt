package ru.kima.sonar.feature.portfolios.ui.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.feature.portfolios.ui.rules.state.RulesLoadingStatus

@Composable
internal fun PortfolioRulesScreen(
    portfolioId: Long
) {
    val viewModel: PortfolioRulesViewModel = koinViewModel { parametersOf(portfolioId) }
    val status by viewModel.status.collectAsState()
    val title by viewModel.title.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val rule by viewModel.rule.collectAsState()

    PortfolioRulesScreenContent(
        status = status,
        title = title,
        mode = mode,
        rule = rule
    )
}

@Composable
private fun PortfolioRulesScreenContent(
    status: RulesLoadingStatus,
    title: String,
    mode: RulesMode,
    rule: Rule
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(status.toString())
        Text(title)
        Text(mode.toString())
        Text(rule.toString())
    }
}