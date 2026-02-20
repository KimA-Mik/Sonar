package ru.kima.sonar.feature.securities.ui.list.model.mappers

import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemFuture

fun ListItemFuture.toDisplay() = DisplayListItemFuture(
    uid = uid,
    name = name,
    ticker = ticker,
    price = price,
    expirationDate = expirationDate
)