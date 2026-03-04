package ru.kima.sonar.feature.portfolios.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.portfolios.ui.addentries.AddEntriesViewModel
import ru.kima.sonar.feature.portfolios.ui.details.EditEntryDialogViewModel
import ru.kima.sonar.feature.portfolios.ui.details.PortfolioDetailsViewModel
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListViewModel

fun portfoliosModule() = module {
    viewModel<PortfoliosListViewModel>()
    viewModel<PortfolioDetailsViewModel>()
    viewModel<EditEntryDialogViewModel>()
    viewModel<AddEntriesViewModel>()
}