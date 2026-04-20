package ru.kima.sonar.feature.portfolios.ui.rules

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.model.portfolio.RuleEditPortfolio
import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.data.homeapi.model.rules.RuleType
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesAction
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenUserEvent
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.mapper.toFlatDisplayRuleList
import ru.kima.sonar.feature.portfolios.ui.rules.state.RulesLoadingStatus
import java.math.BigDecimal

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

    private val _rules = MutableStateFlow<ImmutableList<DisplayRule>>(persistentListOf())
    val rules = _rules.asStateFlow()

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
                    _rules.value = res.data.rule.rule.toFlatDisplayRuleList().toImmutableList()
                }
            }
        }
    }

    fun onEvent(event: RulesScreenUserEvent) {
        when (event) {
            is RulesScreenUserEvent.SetMode -> onSetMode(event.mode)
            is RulesScreenUserEvent.SetRootRule -> setRootRule(event.ruleType)
            is RulesScreenUserEvent.RuleAction -> onRuleAction(event.action)
        }
    }

    private fun onSetMode(mode: RulesMode) {
        _mode.value = mode
    }

    private fun setRootRule(ruleType: RuleType) {
        var oldLow: Float? = null
        var oldHigh: Float? = null
        var oldThreshold = 2

        val oldRoot = _rules.value.firstOrNull()
        if (oldRoot is DisplayRule.Indicator) {
            oldLow = oldRoot.low
            oldHigh = oldRoot.high
            oldThreshold = oldRoot.threshold
        }

        val newRoot = when (ruleType) {
            RuleType.RSI -> DisplayRule.Indicator.Rsi(
                key = 1,
                depth = 0,
                low = oldLow ?: dummyRsi.defaultLowThreshold,
                high = oldHigh ?: dummyRsi.defaultHighThreshold,
                threshold = oldThreshold,
                parent = null
            )

            RuleType.SRSI -> DisplayRule.Indicator.Srsi(
                key = 1,
                depth = 0,
                low = oldLow ?: dummySrsi.defaultLowThreshold,
                high = oldHigh ?: dummySrsi.defaultHighThreshold,
                threshold = oldThreshold,
                parent = null
            )

            RuleType.MFI -> DisplayRule.Indicator.Mfi(
                key = 1,
                depth = 0,
                low = oldLow ?: dummyMfi.defaultLowThreshold,
                high = oldHigh ?: dummyMfi.defaultHighThreshold,
                threshold = oldThreshold,
                parent = null
            )

            RuleType.BB -> DisplayRule.Indicator.Bb(
                key = 1,
                depth = 0,
                low = oldLow ?: dummyBb.defaultLowThreshold,
                high = oldHigh ?: dummyBb.defaultHighThreshold,
                threshold = oldThreshold,
                parent = null
            )

            RuleType.GROUP -> DisplayRule.Group(
                key = 1,
                threshold = 1,
                depth = 0,
                parent = null
            )
        }

        _rules.value = persistentListOf(newRoot)
    }

    private fun onRuleAction(action: RulesAction) {
        when (action) {
            is RulesAction.AddRule -> {
                val (index, parent) = findIndexOrReturn<DisplayRule.Group>(action.key) { return }
                val list = _rules.value.toMutableList()
                val key = list.maxOf { it.key } + 1
                val newRule = when (action.ruleType) {
                    RuleType.RSI -> DisplayRule.Indicator.Rsi(
                        key = key,
                        depth = parent.depth + 1,
                        low = dummyRsi.defaultLowThreshold,
                        high = dummyRsi.defaultHighThreshold,
                        threshold = 2,
                        parent = parent
                    )

                    RuleType.SRSI -> DisplayRule.Indicator.Srsi(
                        key = key,
                        depth = parent.depth + 1,
                        low = dummySrsi.defaultLowThreshold,
                        high = dummySrsi.defaultHighThreshold,
                        threshold = 2,
                        parent = parent
                    )

                    RuleType.MFI -> DisplayRule.Indicator.Mfi(
                        key = key,
                        depth = parent.depth + 1,
                        low = dummyMfi.defaultLowThreshold,
                        high = dummyMfi.defaultHighThreshold,
                        threshold = 2,
                        parent = parent
                    )

                    RuleType.BB -> DisplayRule.Indicator.Bb(
                        key = key,
                        depth = parent.depth + 1,
                        low = dummyBb.defaultLowThreshold,
                        high = dummyBb.defaultHighThreshold,
                        threshold = 2,
                        parent = parent
                    )

                    RuleType.GROUP -> DisplayRule.Group(
                        key = key,
                        threshold = 1,
                        depth = parent.depth + 1,
                        parent = parent
                    )
                }

                var position = list.indexOfLast { (it.parent as DisplayRule?)?.key == action.key }
                if (position < 0) position = index
                list.add(position + 1, newRule)
                _rules.value = list.toImmutableList()
            }

            is RulesAction.ClearGroup -> {}
            is RulesAction.DeleteRule -> {}
            is RulesAction.UpdateGroupRuleTruthThreshold -> {
                val (index, old) = findIndexOrReturn<DisplayRule.Group>(action.key) { return }
                val list = _rules.value.toMutableList()
                list[index] = old.copy(
                    threshold = action.truthThreshold,
                )
                _rules.value = list.toImmutableList()
            }

            is RulesAction.UpdateBbRuleAction -> {
                val (index, old) = findIndexOrReturn<DisplayRule.Indicator.Bb>(action.key) { return }
                val list = _rules.value.toMutableList()
                list[index] = old.copy(
                    threshold = action.requiredCount,
                    low = action.lowThreshold,
                    high = action.highThreshold
                )
                _rules.value = list.toImmutableList()
            }

            is RulesAction.UpdateMfiRuleAction -> {
                val (index, old) = findIndexOrReturn<DisplayRule.Indicator.Mfi>(action.key) { return }
                val list = _rules.value.toMutableList()
                list[index] = old.copy(
                    threshold = action.requiredCount,
                    low = action.lowThreshold,
                    high = action.highThreshold
                )
                _rules.value = list.toImmutableList()
            }

            is RulesAction.UpdateRsiRuleAction -> {
                val (index, old) = findIndexOrReturn<DisplayRule.Indicator.Rsi>(action.key) { return }
                val list = _rules.value.toMutableList()
                list[index] = old.copy(
                    threshold = action.requiredCount,
                    low = action.lowThreshold,
                    high = action.highThreshold
                )
                _rules.value = list.toImmutableList()
            }

            is RulesAction.UpdateSrsiRuleAction -> {
                val (index, old) = findIndexOrReturn<DisplayRule.Indicator.Srsi>(action.key) { return }
                val list = _rules.value.toMutableList()
                list[index] = old.copy(
                    threshold = action.requiredCount,
                    low = action.lowThreshold,
                    high = action.highThreshold
                )
                _rules.value = list.toImmutableList()
            }
        }
    }

    private inline fun <reified T> findIndexOrReturn(
        key: Long,
        block: () -> Nothing
    ): Pair<Int, T> {
        val list = _rules.value
        val index = list.indexOfFirst { it.key == key }
        if (index < 0) block()
        if (list[index] !is T) block()
        return index to (list[index] as T)
    }

    private val dummyRsi = RsiRule(0, BigDecimal(0), BigDecimal(0))
    private val dummySrsi = SrsiRule(0, BigDecimal(0), BigDecimal(0))
    private val dummyMfi = MfiRule(0, BigDecimal(0), BigDecimal(0))
    private val dummyBb = BbRule(0, BigDecimal(0), BigDecimal(0))

}
