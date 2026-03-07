package ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper

import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity

internal fun ListItemShare.toAddableSecurity(
    selected: Boolean = false
) = AddableSecurity(
    uid = uid,
    ticker = ticker,
    name = name,
    price = price,
    selected = selected,
    basicAsset = ticker
)