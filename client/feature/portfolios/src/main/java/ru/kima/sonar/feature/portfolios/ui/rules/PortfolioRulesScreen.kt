package ru.kima.sonar.feature.portfolios.ui.rules

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.components.SonarDropdownMenu
import ru.kima.sonar.common.ui.components.SonarDropdownMenuItem
import ru.kima.sonar.common.ui.components.SonarExposedDropdownMenu
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.data.homeapi.model.rules.RuleType
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.rules.components.rules.RulesList
import ru.kima.sonar.feature.portfolios.ui.rules.components.rules.rememberRulesMenu
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenUserEvent
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.mapper.toFlatDisplayRuleList
import ru.kima.sonar.feature.portfolios.ui.rules.state.RulesLoadingStatus
import java.math.BigDecimal

@Composable
internal fun PortfolioRulesScreen(
    portfolioId: Long
) {
    val viewModel: PortfolioRulesViewModel = koinViewModel { parametersOf(portfolioId) }
    val status by viewModel.status.collectAsState()
    val title by viewModel.title.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val rules by viewModel.rules.collectAsState()

    PortfolioRulesScreenBody(
        status = status,
        title = title,
        mode = mode,
        onEvent = viewModel::onEvent,
        rules = rules
    )
}

@Composable
private fun PortfolioRulesScreenBody(
    status: RulesLoadingStatus,
    title: String,
    mode: RulesMode,
    rules: ImmutableList<DisplayRule>,
    onEvent: (RulesScreenUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = { Text(title) },
                navigateUp = { navigator.goBack() }
            )
        }
    ) { paddingValues ->
        PortfolioRulesScreenContent(
            rules = rules,
            mode = mode,
            onEvent = onEvent,
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun PortfolioRulesScreenContent(
    rules: ImmutableList<DisplayRule>,
    mode: RulesMode,
    onEvent: (RulesScreenUserEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModeSelector(
            mode = mode,
            onSelect = { onEvent(RulesScreenUserEvent.SetMode(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        RootRoleSelector(
            rootRule = rules.firstOrNull(),
            onSelect = { onEvent(RulesScreenUserEvent.SetRootRule(it)) },
            enabled = mode != RulesMode.RULES_DISABLED,
            modifier = Modifier.fillMaxWidth()
        )

        RulesList(
            rules = rules,
            onAction = { onEvent(RulesScreenUserEvent.RuleAction(it)) },
            enabled = mode != RulesMode.RULES_DISABLED,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeSelector(
    mode: RulesMode,
    onSelect: (RulesMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        val state = rememberTextFieldState()
        val resource = LocalResources.current
        LaunchedEffect(mode) {
            val id = when (mode) {
                RulesMode.RULES_DISABLED -> R.string.menu_label_rules_disabled
                RulesMode.LIMIT_SECURITIES -> R.string.menu_label_rules_limit
                RulesMode.RULES_NOTIFICATIONS -> R.string.menu_label_rules_notifications
                RulesMode.RULES_AND_SECURITIES -> R.string.menu_label_rules_notifications_and_securities
            }
            state.setTextAndPlaceCursorAtEnd(resource.getString(id))
        }
        TextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            label = { Text(stringResource(R.string.label_rules_mode)) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painterResource(CommonDrawables.arrow_drop_down_24px),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotation
                        }
                    )
                }
            }
        )

        val items = remember(onSelect) {
            persistentListOf(
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.menu_label_rules_disabled,
                    onClick = {
                        expanded = false
                        onSelect(RulesMode.RULES_DISABLED)
                    }
                ),
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.menu_label_rules_limit,
                    onClick = {
                        expanded = false
                        onSelect(RulesMode.LIMIT_SECURITIES)
                    }
                )
            )
        }

        SonarExposedDropdownMenu(
            expanded = expanded,
            items = items,
            checkedIndex = -1,
            onDismissRequest = { expanded = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootRoleSelector(
    rootRule: DisplayRule?,
    onSelect: (RuleType) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
) {
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(enabled) { if (!enabled) expanded = false }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    val state = rememberTextFieldState()
    val resources = LocalResources.current
    LaunchedEffect(rootRule) {
        val id = when (rootRule) {
            is DisplayRule.Group -> R.string.menu_label_rule_type_group
            is DisplayRule.Indicator.Bb -> R.string.rule_title_bb
            is DisplayRule.Indicator.Mfi -> R.string.rule_title_mfi
            is DisplayRule.Indicator.Rsi -> R.string.rule_title_rsi
            is DisplayRule.Indicator.Srsi -> R.string.rule_title_srsi
            null -> null
        }
        id?.let {
            state.setTextAndPlaceCursorAtEnd(resources.getString(id))
        }
    }

    TextField(
        state = state,
        modifier = Modifier
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = true,
        label = { Text(stringResource(R.string.label_root_rule)) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
        lineLimits = TextFieldLineLimits.SingleLine,
        trailingIcon = {
            IconButton(
                onClick = { expanded = true },
                enabled = enabled
            ) {
                Icon(
                    painterResource(CommonDrawables.arrow_drop_down_24px),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        }
    )

    val menuItems = rememberRulesMenu {
        onSelect(it)
        expanded = false
    }

    SonarDropdownMenu(
        expanded = expanded,
        items = menuItems,
        onDismissRequest = { expanded = false }
    )
}

@Preview
@Composable
private fun PortfolioRulesScreenPreview() = SonarPreview {
    PortfolioRulesScreenBody(
        status = RulesLoadingStatus.Success,
        title = "Title",
        mode = RulesMode.LIMIT_SECURITIES,
        rules = GroupRule(
            1,
            listOf(
                GroupRule(
                    2,
                    listOf(
                        RsiRule(
                            requiredCount = 2,
                            lowThreshold = BigDecimal(30),
                            highThreshold = BigDecimal(70)
                        )
                    )
                ),
                GroupRule(
                    3,
                    listOf()
                )
            )
        ).toFlatDisplayRuleList().toImmutableList(),
        onEvent = {}
    )
}