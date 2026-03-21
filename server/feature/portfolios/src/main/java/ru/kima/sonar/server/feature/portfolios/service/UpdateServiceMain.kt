package ru.kima.sonar.server.feature.portfolios.service

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

fun Application.runUpdateService() {
    val updateService: UpdateService by inject()
    updateService.run()
}