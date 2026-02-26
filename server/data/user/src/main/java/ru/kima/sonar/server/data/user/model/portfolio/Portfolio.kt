package ru.kima.sonar.server.data.user.model.portfolio

data class Portfolio(
    val id: Long,
    val userId: Long,
    val name: String,
) {
    companion object {
        fun default(
            id: Long = 0,
            userId: Long,
            name: String
        ) = Portfolio(
            id = id,
            userId = userId,
            name = name
        )
    }
}