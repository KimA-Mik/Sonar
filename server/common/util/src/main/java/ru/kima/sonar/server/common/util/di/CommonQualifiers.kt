package ru.kima.sonar.server.common.util.di

import org.koin.core.qualifier.named

object CommonQualifiers {
    val ROOT_JOB = named("rootJob")
    val DEFAULT_SCOPE = named("defaultScope")
    val IO_SCOPE = named("ioScope")
}