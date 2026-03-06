package ru.kima.sonar.feature.portfolios.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.portfolios.ui.addentries.AddEntriesViewModel
import ru.kima.sonar.feature.portfolios.ui.details.EditEntryDialogViewModel
import ru.kima.sonar.feature.portfolios.ui.details.PortfolioDetailsViewModel
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListViewModel
import ru.kima.sonar.feature.portfolios.ui.list.dialog.RemovePortfolioDialogViewModel

fun portfoliosModule() = module {
    viewModel<PortfoliosListViewModel>()
    viewModel<RemovePortfolioDialogViewModel>()
    viewModel<PortfolioDetailsViewModel>()
    viewModel<EditEntryDialogViewModel>()
    viewModel<AddEntriesViewModel>()
}