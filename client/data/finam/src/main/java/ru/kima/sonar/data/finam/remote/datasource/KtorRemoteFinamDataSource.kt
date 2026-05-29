package ru.kima.sonar.data.finam.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.finam.model.FinamError
import ru.kima.sonar.data.finam.remote.dto.FinamRequest
import ru.kima.sonar.data.finam.remote.dto.FinamResponse

private const val TAG = "KtorRemoteFinamDataSource"

internal class KtorRemoteFinamDataSource : RemoteFinamDataSource {
    private val client = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.DEFAULT
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun findTicker(ticker: String): SonarResult<String, FinamError> = try {
        val request = client.post(FINAM_PLUGIN_URL) {
            headers {
                append("Host", "www.finam.ru")
                append(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0"
                )
                append("Accept", "application/json, text/javascript, */*; q=0.01")
                append("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                append("Accept-Encoding", "gzip, deflate, br, zstd")
                append("Content-Type", "application/json; charset=utf-8")
                append("X-Requested-With", "XMLHttpRequest")
                append("Origin", "https://www.finam.ru")
                append("Sec-GPC", "1")
                append("Referer", "https://www.finam.ru/")
                append("Sec-Fetch-Dest", "empty")
                append("Sec-Fetch-Mode", "cors")
                append("Sec-Fetch-Site", "same-origin")
                append("Priority", "u=0")
            }
            setBody(FinamRequest.securitySearchRequest(ticker))
        }

        if (request.status.isSuccess()) {
            val response = request.body<FinamResponse>()
            if (response.error.message.isNotEmpty() || response.error.code.isNotEmpty()) {
                return SonarResult.Error(
                    FinamError.RequestError(response.error.code, response.error.message)
                )
            }
            return SonarResult.Success(response.html)
        } else {
            SonarResult.Error(FinamError.RequestFailed(request.status.value))
        }
    } catch (e: Exception) {
        SonarResult.Error(FinamError.Unknown(e))
    }

    companion object {
        private const val FINAM_PLUGIN_URL = "https://www.finam.ru/plugin/"
    }
}