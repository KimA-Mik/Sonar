package ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper

import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity

internal fun ListItemShare.toAddableSecurity(
    selected: Boolean = false
) = AddableSecurity(
    uid = uid,
    ticker = ticker,
    securityType = SecurityType.SHARE,
    name = name,
    price = price,
    selected = selected,
    basicAsset = ticker
)