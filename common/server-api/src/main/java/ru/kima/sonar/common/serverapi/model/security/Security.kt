package ru.kima.sonar.common.serverapi.model.security

//@Serializable
sealed class Security {
    abstract val uid: String
    abstract val ticker: String
    abstract val name: String
    abstract val lot: Int
}
