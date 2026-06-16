package ru.kima.sonar.server.lifecycle

import io.ktor.server.application.Application
import kotlinx.coroutines.Job
import org.koin.ktor.ext.inject
import ru.kima.sonar.server.common.util.di.CommonQualifiers

fun Application.shutdownHook() {
    val rootJob by inject<Job>(CommonQualifiers.ROOT_JOB)
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down application...")
        rootJob.cancel()
    })
}