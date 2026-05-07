package ru.kima.sonar.feature.portfolios.ui.components.editentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
import ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.clearFocusOnSoftKeyboardHide
import ru.kima.sonar.feature.portfolios.R

@Composable
internal fun EditEntry2Content(
    components: ImmutableList<EditEntryComponent>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    onDeleteEntry: ((uid: String) -> Unit)? = null,
    onStopLossPriceChange: (String, String) -> Unit,
    onStopLossNoteChange: (String, String) -> Unit,
    onDeleteStopLoss: (String) -> Unit,
    onTakeProfitPriceChange: (String, String) -> Unit,
    onTakeProfitNoteChange: (String, String) -> Unit,
    onDeleteTakeProfit: (String) -> Unit,
    onAddStopLoss: (uid: String) -> Unit,
    onAddTakeProfit: (uid: String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = components,
            key = { it.key },
            span = {
                when (it) {
                    is EditEntryComponent.Title -> GridItemSpan(2)
                    else -> GridItemSpan(1)
                }
            },
            contentType = { it::class }
        ) { component ->
            when (component) {
                is EditEntryComponent.Title -> EditEntryTitle(
                    title = component,
                    onDeleteEntry = onDeleteEntry
                )

                is EditEntryComponent.StopLoss -> EditEntryStopLoss(
                    stopLoss = component,
                    onPriceChange = onStopLossPriceChange,
                    onNoteChange = onStopLossNoteChange,
                    onDelete = onDeleteStopLoss
                )

                is EditEntryComponent.TakeProfit -> EditEntryTakeProfit(
                    takeProfit = component,
                    onPriceChange = onTakeProfitPriceChange,
                    onNoteChange = onTakeProfitNoteChange,
                    onDelete = onDeleteTakeProfit
                )

                is EditEntryComponent.AddStopLoss -> AddButton(
                    onClick = { onAddStopLoss(component.uid) }
                )

                is EditEntryComponent.AddTakeProfit -> AddButton(
                    onClick = { onAddTakeProfit(component.uid) }
                )

                is EditEntryComponent.Padding -> {}
            }
        }
    }
}

@Composable
private fun EditEntryTitle(
    title: EditEntryComponent.Title,
    modifier: Modifier = Modifier,
    onDeleteEntry: ((String) -> Unit)?,
) {
    val nf = LocalNumberFormat.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Subtitle(
            text = title.title,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = stringResource(CommonStrings.price_format, nf.format(title.price)),
            modifier = Modifier
                .alignByBaseline()
                .weight(1f, fill = false),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLargeEmphasized
        )

        onDeleteEntry?.let {
            IconButton(onClick = { it.invoke(title.uid) }) {
                Icon(
                    painter = painterResource(CommonDrawables.delete_24px),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun EditEntryStopLoss(
    stopLoss: EditEntryComponent.StopLoss,
    modifier: Modifier = Modifier,
    onPriceChange: (String, String) -> Unit,
    onNoteChange: (String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeleteTitle(
            index = stopLoss.index,
            onDeleteClick = { onDelete(stopLoss.key) },
            modifier = Modifier.fillMaxWidth()
        )
        PriceInputField(
            price = stopLoss.price,
            wasError = false,
            onValueChange = { onPriceChange(stopLoss.key, it) },
            label = { Text("Stop Loss") }
        )
        OutlinedTextField(
            value = stopLoss.note,
            onValueChange = { onNoteChange(stopLoss.key, it) },
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_note)) },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(
                            CommonStrings.fraction,
                            stopLoss.note.length,
                            NOTE_LENGTH
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun EditEntryTakeProfit(
    takeProfit: EditEntryComponent.TakeProfit,
    modifier: Modifier = Modifier,
    onPriceChange: (String, String) -> Unit,
    onNoteChange: (String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeleteTitle(
            index = takeProfit.index,
            onDeleteClick = { onDelete(takeProfit.key) },
            modifier = Modifier.fillMaxWidth()
        )
        PriceInputField(
            price = takeProfit.price,
            wasError = false,
            onValueChange = { onPriceChange(takeProfit.key, it) },
            label = { Text("Take Profit") }
        )
        OutlinedTextField(
            value = takeProfit.note,
            onValueChange = { onNoteChange(takeProfit.key, it) },
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_note)) },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(
                            CommonStrings.fraction,
                            takeProfit.note.length,
                            NOTE_LENGTH
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painterResource(CommonDrawables.add_24px),
            contentDescription = null
        )
    }
}

@Composable
private fun PriceInputField(
    price: String,
    wasError: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null
) = OutlinedTextField(
    value = price,
    onValueChange = onValueChange,
    modifier = modifier.clearFocusOnSoftKeyboardHide(),
    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
    label = label,
    suffix = { Text(stringResource(CommonStrings.rouble_sign)) },
    isError = wasError,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Decimal
    ),
    maxLines = 1
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Subtitle(
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
private fun DeleteTitle(
    index: Int,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "№${index}",
            style = MaterialTheme.typography.titleLargeEmphasized
        )

        IconButton(onClick = onDeleteClick) {
            Icon(
                painterResource(CommonDrawables.delete_24px),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun EditEntryPreview() = SonarPreview {
    val components = persistentListOf(
        EditEntryComponent.Title(
            key = "Title",
            uid = "",
            title = "SBER",
            price = 1337.toBigDecimal(),
            targetDeviation = "123",
            id = 123
        ),
        EditEntryComponent.StopLoss(
            key = "stopLoss1",
            uid = "",
            index = 1,
            price = "123",
            note = "A note"
        ),
        EditEntryComponent.TakeProfit(
            key = "takeProfit1",
            uid = "",
            index = 1,
            price = "123",
            note = "Another note"
        ),
        EditEntryComponent.AddStopLoss("0"),
        EditEntryComponent.TakeProfit(
            key = "takeProfit2",
            uid = "",
            index = 2,
            price = "456",
            note = "Yet another note"
        ),
        EditEntryComponent.Padding(
            key = "Padding 1",
            uid = ""
        ),
        EditEntryComponent.AddTakeProfit("0"),
    )
    EditEntry2Content(
        components = components,
        modifier = Modifier.padding(8.dp),
        onDeleteEntry = {},
        onStopLossPriceChange = { _, _ -> },
        onStopLossNoteChange = { _, _ -> },
        onDeleteStopLoss = {},
        onTakeProfitPriceChange = { _, _ -> },
        onTakeProfitNoteChange = { _, _ -> },
        onDeleteTakeProfit = {},
        onAddStopLoss = {},
        onAddTakeProfit = {}
    )
}

@Preview
@Composable
private fun MapperPreview() = SonarPreview {
    val components = listOf(
        PortfolioEntry(
            id = 0,
            uid = "0",
            name = "0",
            targetDeviation = 0.toBigDecimal(),
            price = 0.toBigDecimal(),
            lowPrice = 0.toBigDecimal(),
            highPrice = 0.toBigDecimal(),
            note = "Note",
            stopLosses = listOf(
                StopLoss(
                    id = 0,
                    entryId = 0,
                    price = 0.toBigDecimal(),
                    note = "Note"
                ),
            ),
            takeProfits = listOf(
                TakeProfit(
                    id = 0,
                    entryId = 0,
                    price = 0.toBigDecimal(),
                    note = "Note"
                ),
                TakeProfit(
                    id = 1,
                    entryId = 0,
                    price = 1.toBigDecimal(),
                    note = "Note"
                )
            )
        ),
        PortfolioEntry(
            id = 1,
            uid = "1",
            name = "1",
            targetDeviation = 1.toBigDecimal(),
            price = 1.toBigDecimal(),
            lowPrice = 1.toBigDecimal(),
            highPrice = 1.toBigDecimal(),
            note = "Note",
            stopLosses = emptyList(),
            takeProfits = listOf(
                TakeProfit(
                    id = 1,
                    entryId = 1,
                    price = 1.toBigDecimal(),
                    note = "Note"
                )
            )
        )
    ).toComponents()

    EditEntry2Content(
        components = components,
        modifier = Modifier.padding(8.dp),
        onDeleteEntry = {},
        onStopLossPriceChange = { _, _ -> },
        onStopLossNoteChange = { _, _ -> },
        onDeleteStopLoss = {},
        onTakeProfitPriceChange = { _, _ -> },
        onTakeProfitNoteChange = { _, _ -> },
        onDeleteTakeProfit = {},
        onAddStopLoss = {},
        onAddTakeProfit = {}
    )
}