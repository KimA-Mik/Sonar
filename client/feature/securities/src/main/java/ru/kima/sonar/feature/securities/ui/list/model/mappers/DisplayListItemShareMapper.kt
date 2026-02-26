package ru.kima.sonar.feature.securities.ui.list.model.mappers

import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemShare

fun ListItemShare.toDisplayListItemShare() = DisplayListItemShare(
    uid = uid,
    name = name,
    ticker = ticker,
    price = price
)