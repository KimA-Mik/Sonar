package ru.kima.sonar.feature.portfolios.ui.rules

import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.components.SonarDropdownMenu
import ru.kima.sonar.common.ui.components.SonarDropdownMenuItem
import ru.kima.sonar.common.ui.components.SonarExposedDropdownMenu
import ru.kima.sonar.common.ui.event.ResultEffect
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.data.homeapi.model.rules.RuleType
import ru.kima.sonar.data.homeapi.util.getHomeApiErrorString
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph.List.Details.Rules.ClearGroupDialog
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph.List.Details.Rules.DeleteRuleDialog
import ru.kima.sonar.feature.portfolios.ui.rules.components.rules.RulesList
import ru.kima.sonar.feature.portfolios.ui.rules.components.rules.rememberRulesMenu
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenBusEvent
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenUiEvent
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
    val canSave by viewModel.canSave.collectAsState()

    val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

    PortfolioRulesScreenBody(
        status = status,
        title = title,
        mode = mode,
        onEvent = viewModel::onEvent,
        onCallbackEvent = viewModel::onCallbackEvent,
        uiEvent = uiEvents,
        rules = rules,
        canSave = canSave
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PortfolioRulesScreenBody(
    status: RulesLoadingStatus,
    title: String,
    mode: RulesMode,
    rules: ImmutableList<DisplayRule>,
    canSave: Boolean,
    onEvent: (RulesScreenUserEvent) -> Unit,
    onCallbackEvent: (RulesScreenBusEvent) -> Unit,
    uiEvent: SonarEvent<RulesScreenUiEvent>,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val resources = LocalResources.current
    val snackbarHostState = LocalSnackbarHostState.current
    LaunchedEffect(uiEvent) {
        consumeEvent(
            uiEvent = uiEvent,
            navigator = navigator,
            coroutineScope = coroutineScope,
            resources = resources,
            snackbarHostState = snackbarHostState
        )
    }

    ResultEffect<RulesScreenBusEvent> {
        onCallbackEvent(it)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sb = remember { scrollBehavior }
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = { Text(title) },
                navigateUp = { navigator.goBack() },
                scrollBehavior = sb
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = canSave,
                enter = scaleIn() + slideInHorizontally { it / 2 },
                exit = scaleOut() + slideOutHorizontally { it / 2 }
            ) {
                FloatingActionButton(onClick = { onEvent(RulesScreenUserEvent.Save) }) {
                    Icon(
                        painterResource(CommonDrawables.save_24px),
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = status,
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(sb.nestedScrollConnection)
        ) { state ->
            when (state) {
                is RulesLoadingStatus.Error -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text(stringResource(R.string.label_error_loading_rules))
                    Button(onClick = { onEvent(RulesScreenUserEvent.ReloadRules) }) {
                        Text(stringResource(CommonStrings.action_retry))
                    }
                }

                RulesLoadingStatus.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }

                RulesLoadingStatus.Success -> PortfolioRulesScreenContent(
                    rules = rules,
                    mode = mode,
                    onEvent = onEvent,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}


private fun consumeEvent(
    uiEvent: SonarEvent<RulesScreenUiEvent>,
    navigator: Navigator,
    coroutineScope: CoroutineScope,
    resources: Resources,
    snackbarHostState: SnackbarHostState
) {
    uiEvent.consume { event ->
        when (event) {
            is RulesScreenUiEvent.ShowClearGroupDialog -> navigator.navigate(
                ClearGroupDialog(event.key)
            )

            is RulesScreenUiEvent.ShowDeleteRuleDialog -> navigator.navigate(
                DeleteRuleDialog(
                    event.key,
                    event.ruleType
                )
            )

            is RulesScreenUiEvent.ErrorSaving -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = resources.getHomeApiErrorString(event.error),
                    withDismissAction = true
                )
            }

            RulesScreenUiEvent.SuccessfullySaved -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = resources.getString(R.string.snackbar_message_rules_saved_successfully),
                    withDismissAction = true
                )
            }
        }
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
            contentPadding = PaddingValues(bottom = 72.dp)
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
        onEvent = {},
        onCallbackEvent = {},
        uiEvent = SonarEvent(),
        canSave = true
    )
}