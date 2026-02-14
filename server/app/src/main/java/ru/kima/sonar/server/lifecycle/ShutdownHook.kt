package ru.kima.sonar.server.lifecycle

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource

fun Application.shutdownHook() {
    val tinkoffDataSource by inject<TinkoffDataSource>()
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down application...")
        tinkoffDataSource.shutdown()
    })
}