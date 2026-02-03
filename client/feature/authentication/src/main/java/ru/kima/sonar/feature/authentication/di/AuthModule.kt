package ru.kima.sonar.feature.authentication.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import ru.kima.sonar.feature.authentication.ui.authscreen.AuthScreenViewModel

fun authModule() = module {
    viewModel<AuthScreenViewModel>()
}