package ru.kima.sonar.feature.portfolios.ui.components.editentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
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
                is EditEntryComponent.Title -> EditEntryTitle(component)
                is EditEntryComponent.StopLoss -> EditEntryStopLoss(component)
                is EditEntryComponent.TakeProfit -> EditEntryTakeProfit(component)
                is EditEntryComponent.AddStopLoss -> AddButton(onClick = {})
                is EditEntryComponent.AddTakeProfit -> AddButton(onClick = {})
                is EditEntryComponent.Padding -> {}
            }
        }
    }
}

@Composable
private fun EditEntryTitle(
    title: EditEntryComponent.Title,
    modifier: Modifier = Modifier
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
    }
}

@Composable
private fun EditEntryStopLoss(
    stopLoss: EditEntryComponent.StopLoss,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeleteTitle(
            index = stopLoss.index,
            onDeleteClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        PriceInputField(
            price = stopLoss.price,
            wasError = false,
            onValueChange = {},
            label = { Text("Stop Loss") }
        )
        OutlinedTextField(
            value = stopLoss.note,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_note)) },
            maxLines = 1,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeleteTitle(
            index = takeProfit.index,
            onDeleteClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        PriceInputField(
            price = takeProfit.price,
            wasError = false,
            onValueChange = {},
            label = { Text("Take Profit") }
        )
        OutlinedTextField(
            value = takeProfit.note,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_note)) },
            maxLines = 1,
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
            title = "SBER",
            price = 1337.toBigDecimal(),
            targetDeviation = "123"
        ),
        EditEntryComponent.StopLoss(
            key = "stopLoss1",
            index = 1,
            price = "123",
            note = "A note"
        ),
        EditEntryComponent.TakeProfit(
            key = "takeProfit1",
            index = 1,
            price = "123",
            note = "Another note"
        ),
        EditEntryComponent.AddStopLoss("addStopLoss1"),
        EditEntryComponent.TakeProfit(
            key = "takeProfit2",
            index = 2,
            price = "456",
            note = "Yet another note"
        ),
        EditEntryComponent.Padding(key = "Padding 1"),
        EditEntryComponent.AddTakeProfit("addTakeProfit"),
    )
    EditEntry2Content(
        components = components,
        modifier = Modifier.padding(8.dp)
    )
}