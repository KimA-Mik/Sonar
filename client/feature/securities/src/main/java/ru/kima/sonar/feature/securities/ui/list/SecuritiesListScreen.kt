package ru.kima.sonar.feature.securities.ui.list

import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.feature.securities.R
import java.math.BigDecimal
import java.util.Locale
import kotlin.time.Clock

@Composable
fun SecuritiesLstScreen() {
    val viewModel: SecuritiesListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SecuritiesListScreenContent(
        state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecuritiesListScreenContent(
    state: SecuritiesListState
) {
//    DisposableEffect(Unit) {
//        onDispose {
//
//        }
//    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_title_securities_list)) }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.shares, key = { it.uid }) {
                    ListItemShare(
                        ticker = it.ticker,
                        name = it.name,
                        price = it.price,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

val nf = NumberFormat.getInstance(Locale.of("ru", "RU"))

@Composable
fun ListItemShare(
    ticker: String,
    name: String,
    price: BigDecimal,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        Text(ticker, style = MaterialTheme.typography.headlineSmall)
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }

    PriceFormat(price)
}

@Composable
fun PriceFormat(price: BigDecimal, modifier: Modifier = Modifier) {
    Text(
        "${nf.format(price)} RUB",
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview
@Composable
private fun SecuritiesListScreenPreview() = SonarPreview {
    SecuritiesListScreenContent(
        SecuritiesListState(
            shares = listOf(
                ListItemShare(
                    uid = "1",
                    ticker = "AAPL",
                    name = "Apple Inc.",
                    price = BigDecimal("150.0"),
                    priceTimestamp = Clock.System.now()
                ),
                ListItemShare(
                    uid = "2",
                    ticker = "GOOGL",
                    name = "Alphabet Inc.",
                    price = BigDecimal("2800.0"),
                    priceTimestamp = Clock.System.now()
                )
            )
        )
    )
}