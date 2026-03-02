package ru.kima.sonar.feature.portfolios.ui.addentries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUiEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.EditableEntry
import ru.kima.sonar.feature.portfolios.ui.addentries.state.AddEntriesScreenState
import java.math.BigDecimal

@Composable
internal fun AddEntriesScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: AddEntriesViewModel = koinViewModel {
        parametersOf(portfolioId)
    }
    val numberFormat = LocalNumberFormat.current
    LaunchedEffect(numberFormat) {
        viewModel.setNumberFormatter(numberFormat)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()

    AddEntriesScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        uiEvent = uiEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddEntriesScreenContent(
    state: AddEntriesScreenState,
    onEvent: (AddEntriesUserEvent) -> Unit,
    uiEvent: SonarEvent<AddEntriesUiEvent>,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val snackbarHostState = LocalSnackbarHostState.current
    LaunchedEffect(uiEvent) {
        uiEvent.consume { event ->
            when (event) {
                AddEntriesUiEvent.OpenSelectSecuritiesDialog ->
                    navigator.navigate(PortfoliosGraph.List.Details.AddEntries.SelectSecuritiesDialog)

                AddEntriesUiEvent.PopBack -> navigator.goBack()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = {},
                navigateUp = { navigator.goBack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = !state.wasError && state.entries.isNotEmpty(),
                    enter = scaleIn() + slideInVertically { it / 2 },
                    exit = scaleOut() + slideOutVertically { it / 2 }
                ) {
                    SmallFloatingActionButton(
                        onClick = { onEvent(AddEntriesUserEvent.SaveChangesClicked) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(
                            painter = painterResource(CommonDrawables.save_24px),
                            contentDescription = null
                        )
                    }
                }

                FloatingActionButton(onClick = { onEvent(AddEntriesUserEvent.OpenSelectSecuritiesDialogClicked) }) {
                    Icon(
                        painterResource(CommonDrawables.add_24px),
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        AddEntriesScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
private fun AddEntriesScreenBody(
    state: AddEntriesScreenState,
    onEvent: (AddEntriesUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 40.dp)
    ) {
        items(state.entries, key = { it.uid }) {
            ListItem(
                entry = it,
                onEvent = onEvent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListItem(
    entry: EditableEntry,
    onEvent: (AddEntriesUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.ticker,
                style = MaterialTheme.typography.headlineLargeEmphasized
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = { onEvent(AddEntriesUserEvent.RemoveEntryClicked(entry.uid)) }) {
                Icon(
                    painter = painterResource(CommonDrawables.delete_24px),
                    contentDescription = null
                )
            }
            val rotation by animateFloatAsState(if (entry.expanded) 180f else 0f)
            IconButton(onClick = { onEvent(AddEntriesUserEvent.ExpandClicked(entry.uid)) }) {
                Icon(
                    painter = painterResource(CommonDrawables.arrow_drop_down_24px),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        }
        AnimatedVisibility(entry.expanded) {
            EditableEntryBody(
                entry = entry,
                onEvent = onEvent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EditableEntryBody(
    entry: EditableEntry,
    onEvent: (AddEntriesUserEvent) -> Unit,
    modifier: Modifier = Modifier
) = Card(modifier = modifier) {
    val nf = LocalNumberFormat.current
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Subtitle(
                text = "Last price",
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = stringResource(CommonStrings.price_format, nf.format(entry.price)),
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f, fill = false),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            PriceInputField(
                price = entry.lowPrice,
                wasError = entry.lowPriceError,
                onValueChange = { onEvent(AddEntriesUserEvent.UpdateLowPrice(entry.uid, it)) },
                modifier = Modifier.weight(1f, fill = false),
                label = { Text("Bid price") }
            )

            PriceInputField(
                price = entry.highPrice,
                wasError = entry.highPriceError,
                onValueChange = { onEvent(AddEntriesUserEvent.UpdateHighPrice(entry.uid, it)) },
                modifier = Modifier.weight(1f, fill = false),
                label = { Text("Sell price") }
            )
        }

// val        symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
//        val thousandsSeparator = symbols.groupingSeparator
//        val decimalSeparator = symbols.decimalSeparator


        OutlinedTextField(
            value = entry.note,
            onValueChange = { onEvent(AddEntriesUserEvent.NoteUpdated(entry.uid, it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note") },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(
                            CommonStrings.fraction,
                            entry.note.length,
                            NOTE_LENGTH
                        )
                    )
    }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Subtitle(
    text: String,
    modifier: Modifier = Modifier,
    maxLine: Int = 1
) = Text(
    text = text,
    modifier = modifier,
    maxLines = maxLine,
    style = MaterialTheme.typography.headlineSmallEmphasized
)

@Composable
fun PriceInputField(
    price: String,
    wasError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null
) = OutlinedTextField(
    value = price,
    onValueChange = onValueChange,
    modifier = modifier,
    label = label,
    suffix = { Text(stringResource(CommonStrings.rouble_sign)) },
    isError = wasError,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal
    ),
    maxLines = 1
)


@Preview
@Composable
private fun AddEntriesScreenPreview() = SonarPreview {
    val nf = LocalNumberFormat.current
    AddEntriesScreenContent(
        state = AddEntriesScreenState.default(
            entries = persistentListOf(
                EditableEntry(
                    uid = "ASD",
                    ticker = "SBER",
                    price = BigDecimal("123"),
                    lowPrice = nf.format(BigDecimal("120")),
                    lowPriceError = false,
                    highPrice = nf.format(BigDecimal("125")),
                    highPriceError = true,
                    expanded = true,
                    note = "There's a note"
                )
            )
        ),
        onEvent = {},
        uiEvent = SonarEvent()
    )
}