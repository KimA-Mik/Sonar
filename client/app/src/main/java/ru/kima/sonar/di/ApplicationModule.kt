package ru.kima.sonar.di

import org.koin.dsl.module

val applicationModule = module {
    includes(dataModule)
    includes(featureModule)
}