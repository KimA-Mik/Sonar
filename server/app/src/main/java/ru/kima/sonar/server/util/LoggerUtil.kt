package ru.kima.sonar.server.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory


fun setLogbackLevel(level: Level) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    context.loggerList.forEach { it.setLevel(level) }
}

fun setLogbackLevel(name: String, level: Level) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    val logger = context.getLogger(name)
    if (logger != null) {
        logger.setLevel(level)
    }
}
