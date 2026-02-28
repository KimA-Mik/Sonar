package ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper

import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity

internal fun ListItemFuture.toAddableSecurity(
    selected: Boolean = false
) = AddableSecurity(
    uid = uid,
    ticker = ticker,
    name = name,
    price = price,
    selected = selected,
    basicAsset = basicAsset
)