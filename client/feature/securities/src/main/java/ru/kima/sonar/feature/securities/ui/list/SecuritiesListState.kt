package ru.kima.sonar.feature.securities.ui.list

import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare

data class SecuritiesListState(
    val shares: List<ListItemShare>
)