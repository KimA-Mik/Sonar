package ru.kima.sonar.feature.portfolios.ui.addentries.model

import ru.kima.sonar.feature.portfolios.R

internal enum class AddEntriesTabs(val titleId: Int) {
    Selector(R.string.tab_header_selector),
    Bulk(R.string.tab_header_bulk)
}