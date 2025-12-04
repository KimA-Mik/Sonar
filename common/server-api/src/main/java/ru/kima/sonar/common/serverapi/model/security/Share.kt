package ru.kima.sonar.common.serverapi.model.security

//@Serializable
//@SerialName("Share")
data class Share(
    override val uid: String = "",
    override val ticker: String = "",
    override val name: String = "",
    override val lot: Int = 0
) : Security()
