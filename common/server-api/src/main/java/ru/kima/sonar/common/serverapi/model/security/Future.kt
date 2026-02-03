package ru.kima.sonar.common.serverapi.model.security

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

//@Serializable
//@SerialName("Future")
data class Future @OptIn(ExperimentalTime::class) constructor(
    override val uid: String = "",
    override val ticker: String = "",
    override val name: String = "",
    override val lot: Int = 0,
    val expirationDate: Instant
) : Security()
