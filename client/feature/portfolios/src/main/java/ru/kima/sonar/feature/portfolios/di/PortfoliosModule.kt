package ru.kima.sonar.feature.portfolios.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListViewModel

fun portfoliosModule() = module {
    viewModel<PortfoliosListViewModel>()
}