package ru.kima.sonar.feature.securities.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.kima.sonar.feature.securities.ui.list.SecuritiesLstScreen

fun EntryProviderScope<NavKey>.securitiesNavGraph() {
    entry<SecuritiesGraph.SecuritiesList> { SecuritiesLstScreen() }
}