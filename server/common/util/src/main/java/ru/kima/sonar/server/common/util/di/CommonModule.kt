package ru.kima.sonar.server.common.util.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.bind
import org.koin.dsl.module

fun commonModule() = module {
    single(CommonQualifiers.ROOT_JOB) { SupervisorJob() } bind Job::class
    single(CommonQualifiers.DEFAULT_SCOPE) {
        val job: Job = get(CommonQualifiers.ROOT_JOB)
        CoroutineScope(job + Dispatchers.Default)
    }
    single(CommonQualifiers.IO_SCOPE) {
        val job: Job = get(CommonQualifiers.ROOT_JOB)
        CoroutineScope(job + Dispatchers.IO)
    }
}