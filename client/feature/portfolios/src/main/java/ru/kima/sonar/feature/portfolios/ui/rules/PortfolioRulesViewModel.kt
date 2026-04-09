package ru.kima.sonar.feature.portfolios.ui.rules

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.model.portfolio.RuleEditPortfolio
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.rules.state.RulesLoadingStatus

@Stable
internal class PortfolioRulesViewModel(
    private val portfolioId: Long,
    private val savedStateHandle: SavedStateHandle,
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private val _status = MutableStateFlow<RulesLoadingStatus>(RulesLoadingStatus.Loading)
    val status = _status.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _mode = MutableStateFlow(RulesMode.RULES_DISABLED)
    val mode = _mode.asStateFlow()

    private val _rule = MutableStateFlow<Rule>(GroupRule(0, emptyList()))
    val rule = _rule.asStateFlow()

    init {
        loadRules()
    }

    private fun loadRules() {
        viewModelScope.launch {
            when (val res = homeApiDataSource.getPortfolioRule(portfolioId)) {
                is SonarResult.Error<*, *> -> _status.value =
                    RulesLoadingStatus.Error(res.data.toString())

                is SonarResult.Success<RuleEditPortfolio, *> -> {
                    _status.value = RulesLoadingStatus.Success
                    _title.value = res.data.name
                    _mode.value = res.data.rule.mode
                    _rule.value = res.data.rule.rule
                }
            }
        }
    }
}