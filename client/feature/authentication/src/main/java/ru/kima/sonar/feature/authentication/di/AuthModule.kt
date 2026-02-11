package ru.kima.sonar.feature.authentication.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.authentication.ui.authscreen.AuthScreenViewModel
import ru.kima.sonar.feature.authentication.uscase.LogInUseCase

fun authModule() = module {
    viewModel<AuthScreenViewModel>()

    single<LogInUseCase>()
}