package ru.kima.sonar.feature.portfolios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.clearFocusOnSoftKeyboardHide
import ru.kima.sonar.feature.portfolios.R
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditEntryContent(
    price: BigDecimal,
    lowPrice: String,
    onLowPriceUpdate: (String) -> Unit,
    highPrice: String,
    onHighPriceUpdate: (String) -> Unit,
    note: String,
    onNoteUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
    lowPriceError: Boolean = false,
    highPriceError: Boolean = false
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    val nf = LocalNumberFormat.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Subtitle(
            text = stringResource(R.string.tittle_last_price),
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = stringResource(CommonStrings.price_format, nf.format(price)),
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
            price = lowPrice,
            wasError = lowPriceError,
            onValueChange = onLowPriceUpdate,
            modifier = Modifier.weight(1f, fill = false),
            label = { Text(stringResource(R.string.label_bid_price)) }
        )

        PriceInputField(
            price = highPrice,
            wasError = highPriceError,
            onValueChange = onHighPriceUpdate,
            modifier = Modifier.weight(1f, fill = false),
            label = { Text(stringResource(R.string.label_sell_price)) }
        )
    }

    OutlinedTextField(
        value = note,
        onValueChange = onNoteUpdate,
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
                        note.length,
                        NOTE_LENGTH
                    )
                )
            }
        }
    )
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
    modifier = modifier.clearFocusOnSoftKeyboardHide(),
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
private fun EditEntryContentPreview() = SonarPreview {
    EditEntryContent(
        price = BigDecimal("123.45"),
        lowPrice = "120.00",
        onLowPriceUpdate = {},
        highPrice = "130.00",
        onHighPriceUpdate = {},
        note = "This is a note about the entry.",
        onNoteUpdate = {}
    )
}
