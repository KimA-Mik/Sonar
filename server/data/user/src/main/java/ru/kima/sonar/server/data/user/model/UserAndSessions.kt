package ru.kima.sonar.server.data.user.model

data class UserAndSessions(
    val user: User,
    val sessions: List<Session>
)