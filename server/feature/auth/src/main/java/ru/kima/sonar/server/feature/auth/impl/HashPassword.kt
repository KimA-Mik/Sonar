package ru.kima.sonar.server.feature.auth.impl

fun hashPassword(userId: Long, password: String): String {
    val idStr = userId.toString()
    val p = "$idStr:$password:$idStr"
    return p.hashCode().toString()
}