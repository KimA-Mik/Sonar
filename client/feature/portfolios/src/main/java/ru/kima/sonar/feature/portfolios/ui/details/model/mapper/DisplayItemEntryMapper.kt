package ru.kima.sonar.feature.portfolios.ui.details.model.mapper

import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolioEntry
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry

internal fun ListItemPortfolioEntry.toDisplayItemEntry() = DisplayItemEntry(
    id = id,
    uid = uid,
    name = name,
    price = price,
    lowPrice = lowPrice,
    highPrice = highPrice
)