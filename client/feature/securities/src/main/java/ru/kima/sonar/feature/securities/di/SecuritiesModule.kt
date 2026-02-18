package ru.kima.sonar.feature.securities.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.securities.ui.list.SecuritiesListViewModel

fun securitiesModule() = module {
    viewModel<SecuritiesListViewModel>()
}