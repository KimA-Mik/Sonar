package ru.kima.sonar.server.data.market.marketdata.util

import com.google.protobuf.Timestamp
import kotlin.time.Instant

fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder()
    .setSeconds(epochSeconds)
    .setNanos(nanosecondsOfSecond)
    .build()

fun Timestamp.toInstant() = Instant.fromEpochSeconds(
    epochSeconds = seconds,
    nanosecondAdjustment = nanos
)