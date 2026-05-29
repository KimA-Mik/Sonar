package ru.kima.sonar.data.finam.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class Plugin {
    @SerialName("instrument")
    INSTRUMENT
}

@Serializable
internal enum class Method {
    @SerialName("search")
    SEARCH
}

@Serializable
internal enum class Template {
    @SerialName("search")
    SEARCH
}

@Serializable
internal enum class Response {
    @SerialName("mixTeleport")
    MIX_TELEPORT
}

@Serializable
internal data class Parameters(
    val text: String
)

@Serializable
internal data class FinamRequest(
    val plugin: Plugin,
    val method: Method,
    val template: Template,
    val response: Response,
    val params: Parameters
) {
    companion object {
        fun securitySearchRequest(text: String) = FinamRequest(
            plugin = Plugin.INSTRUMENT,
            method = Method.SEARCH,
            template = Template.SEARCH,
            response = Response.MIX_TELEPORT,
            params = Parameters(text)
        )
    }
}
