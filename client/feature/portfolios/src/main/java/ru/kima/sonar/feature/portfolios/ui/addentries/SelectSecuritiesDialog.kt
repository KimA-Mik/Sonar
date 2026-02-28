package ru.kima.sonar.feature.portfolios.ui.addentries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.components.BasicDialog
import ru.kima.sonar.common.ui.components.ConditionalPullToRefreshBox
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.clearFocusOnSoftKeyboardHide
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.addentries.event.SelectSecuritiesDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.state.SelectSecuritiesDialogState
import java.math.BigDecimal


@Composable
internal fun SelectSecuritiesDialog(modifier: Modifier = Modifier) {
    val viewModel: AddEntriesViewModel = koinViewModel()
    val state by viewModel.selectDialogState.collectAsStateWithLifecycle()

    SelectSecuritiesDialogContent(
        state = state,
        onEvent = viewModel::onSelectDialogEvent,
        modifier = modifier
    )
}

@Composable
private fun SelectSecuritiesDialogContent(
    state: SelectSecuritiesDialogState,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) = BasicDialog(
    modifier = modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onEvent(SelectSecuritiesDialogUserEvent.QueryUpdated(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_ticker)) },
            singleLine = true
        )

        ConditionalPullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(SelectSecuritiesDialogUserEvent.RefreshRequest) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(state.entries, key = { it.uid }) {
                    AddableEntry(
                        it,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AddableEntry(
    security: AddableSecurity,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = security.ticker,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = security.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
            Checkbox(
                checked = security.selected,
                onCheckedChange = { onEvent(SelectSecuritiesDialogUserEvent.EntryChecked(security.uid)) }
            )

            Price(security.price)
        }
    }
}

@Composable
private fun Price(
    price: BigDecimal,
    modifier: Modifier = Modifier
) {
    val numberFormat = LocalNumberFormat.current
    Text(
        text = stringResource(
            ru.kima.sonar.common.ui.R.string.price_format,
            numberFormat.format(price)
        ),
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium
    )
}

@Preview
@Composable
private fun SelectSecuritiesDialogPreview() = SonarPreview {
    SelectSecuritiesDialogContent(
        state = SelectSecuritiesDialogState.default(
            entries = persistentListOf(
                AddableSecurity(
                    uid = "1",
                    ticker = "SBER",
                    name = "Сбербанк",
                    price = BigDecimal("123.456"),
                    selected = false,
                    basicAsset = "SBER"
                ),
                AddableSecurity(
                    uid = "",
                    ticker = "MOEX",
                    name = "Индекс мосбиржи с очень длинным названием",
                    price = BigDecimal("3456.789"),
                    selected = true,
                    basicAsset = "MOEX"
                )
            )
        ),
        onEvent = {}
    )
}