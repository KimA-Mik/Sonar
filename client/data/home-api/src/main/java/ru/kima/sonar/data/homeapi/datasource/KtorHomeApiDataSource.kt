package ru.kima.sonar.data.homeapi.datasource

import android.os.Build
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.dto.auth.response.AuthorizationResult
import ru.kima.sonar.common.serverapi.dto.portfolio.request.AddPortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.CreatePortfolioRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.UpdatePortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.UpdatePortfolioRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolio
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolioEntry
import ru.kima.sonar.common.serverapi.dto.portfolio.response.PortfolioResponse
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.serverapi.routing.AuthRoute
import ru.kima.sonar.common.serverapi.routing.PortfoliosRoute
import ru.kima.sonar.common.serverapi.routing.SecurityRoute
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError
import ru.kima.sonar.data.homeapi.model.mapper.toNotificationProvider
import java.math.BigDecimal
import kotlin.time.Duration

private const val TAG = "KtorHomeApiDataSource"

internal class KtorHomeApiDataSource(
    private val localConfigDataSource: LocalConfigDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : HomeApiDataSource {
    var client = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.DEFAULT
        }
        install(Resources)
        install(ContentNegotiation) {
            json(Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    localConfigDataSource
                        .localConfig()
                        .map { it.apiAccessToken }
                        .firstOrNull()
                        ?.let { BearerTokens(it, null) }
                }
            }
        }
    }

    init {
        coroutineScope.launch {
            localConfigDataSource.localConfig()
                .map { it.apiUrl }
                .collect { url ->
                    val oldClient = client
                    client = client.config {
                        defaultRequest {
                            url(url)
                        }
                    }
                    oldClient.close()
                }
        }
    }

    override suspend fun login(
        login: String,
        password: String,
        localNotificationProvider: LocalNotificationProvider?,
        notificationProviderClientId: String?
    ): SonarResult<String, HomeApiError> {
        val notificationProvider = localNotificationProvider?.toNotificationProvider()
        val request = if (notificationProviderClientId == null) {
            AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                login,
                password,
                //TODO: maybe abstract it away
                Build.MODEL
            )
        } else when (notificationProvider) {
            NotificationProvider.FIREBASE ->
                AuthenticateClientRequest.FirebaseLoginRequest(
                    login,
                    password,
                    Build.MODEL,
                    notificationProviderClientId
                )

            NotificationProvider.HUAWEI_PUSH_KIT ->
                AuthenticateClientRequest.HuaweiPushKitLoginRequest(
                    login,
                    password,
                    Build.MODEL,
                    notificationProviderClientId
                )

            null -> AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                login,
                password,
                Build.MODEL,
            )
        }

        return when (val result = safeApiCall<AuthorizationResult>(logOutOnUnauthorized = false) {
            client.post(AuthRoute.Login()) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }) {
            is SonarResult.Error -> SonarResult.Error(result.data)
            is SonarResult.Success -> SonarResult.Success(result.data.token)
        }
    }

    private suspend inline fun <reified T> safeApiCall(
        logOutOnUnauthorized: Boolean = true,
        apiCall: suspend () -> HttpResponse
    ): SonarResult<T, HomeApiError> = try {
        val response = apiCall()
        when (response.status) {
            HttpStatusCode.OK -> SonarResult.Success(response.body<T>())
            HttpStatusCode.BadRequest -> SonarResult.Error(HomeApiError.BadRequest)
            HttpStatusCode.Forbidden -> SonarResult.Error(HomeApiError.Forbidden)
            HttpStatusCode.InternalServerError -> SonarResult.Error(HomeApiError.InternalServerError)
            HttpStatusCode.Unauthorized -> {
                if (logOutOnUnauthorized) localConfigDataSource.updateApiAccessToken(null)
                SonarResult.Error(HomeApiError.Unauthorized)
            }

            else -> SonarResult.Error(HomeApiError.UnknownApiError(response.status.value))
        }
    } catch (e: IOException) {
        Log.d(TAG, "IOException: $e")
        SonarResult.Error(HomeApiError.NetworkError)
    } catch (e: Exception) {
        Log.d(TAG, "Exception: $e")
        SonarResult.Error(HomeApiError.UnknownError(e))
    }

    override fun tradableShares(period: Duration): Flow<SonarResult<List<ListItemShare>, HomeApiError>> =
        flow {
            while (currentCoroutineContext().isActive) {
                val res = safeApiCall<List<ListItemShare>>(false) {
                    client.get(SecurityRoute.Shares())
                }

                emit(res)
                delay(period)
            }
        }

    override fun tradableFutures(period: Duration): Flow<SonarResult<List<ListItemFuture>, HomeApiError>> =
        flow {
            while (currentCoroutineContext().isActive) {
                val res = safeApiCall<List<ListItemFuture>>(false) {
                    client.get(SecurityRoute.Futures())
                }

                emit(res)
                delay(period)
            }
        }

    override suspend fun portfolios(): SonarResult<List<ListItemPortfolio>, HomeApiError> =
        safeApiCall<List<ListItemPortfolio>> { client.get(PortfoliosRoute()) }

    override suspend fun createPortfolio(name: String): SonarResult<Unit, HomeApiError> =
        safeApiCall<Unit> {
            client.post(PortfoliosRoute.CreatePortfolio()) {
                contentType(ContentType.Application.Json)
                setBody(CreatePortfolioRequest(name = name))
            }
        }

    override suspend fun getPortfolio(portfolioId: Long): SonarResult<PortfolioResponse, HomeApiError> =
        safeApiCall { client.get(PortfoliosRoute.Portfolio(id = portfolioId)) }

    override suspend fun updatePortfolio(
        portfolioId: Long,
        name: String
    ): SonarResult<Unit, HomeApiError> = safeApiCall {
        client.put(PortfoliosRoute.Portfolio.Update(PortfoliosRoute.Portfolio(id = portfolioId))) {
            contentType(ContentType.Application.Json)
            setBody(UpdatePortfolioRequest(name = name))
        }
    }

    override suspend fun deletePortfolio(portfolioId: Long): SonarResult<Unit, HomeApiError> =
        safeApiCall { client.delete(PortfoliosRoute.Portfolio.Delete(PortfoliosRoute.Portfolio(id = portfolioId))) }

    override suspend fun getPortfolioEntry(entryId: Long): SonarResult<ListItemPortfolioEntry, HomeApiError> =
        safeApiCall { client.get(PortfoliosRoute.Entry(id = entryId)) }

    override suspend fun addEntry(
        portfolioId: Long,
        name: String,
        targetDeviation: BigDecimal,
        securityUid: String,
        lowPrice: BigDecimal,
        highPrice: BigDecimal,
        note: String
    ): SonarResult<Unit, HomeApiError> = safeApiCall {
        client.post(PortfoliosRoute.Portfolio.AddEntry(PortfoliosRoute.Portfolio(id = portfolioId))) {
            contentType(ContentType.Application.Json)
            setBody(
                AddPortfolioEntryRequest(
                    name = name,
                    targetDeviation = targetDeviation,
                    securityUid = securityUid,
                    lowPrice = lowPrice,
                    highPrice = highPrice,
                    note = note
                )
            )
        }
    }

    override suspend fun updateEntry(
        entryId: Long,
        name: String,
        targetDeviation: BigDecimal,
        lowPrice: BigDecimal,
        highPrice: BigDecimal,
        note: String
    ): SonarResult<Unit, HomeApiError> = safeApiCall {
        client.put(PortfoliosRoute.Entry.Update(PortfoliosRoute.Entry(id = entryId))) {
            contentType(ContentType.Application.Json)
            setBody(
                UpdatePortfolioEntryRequest(
                    name = name,
                    targetDeviation = targetDeviation,
                    lowPrice = lowPrice,
                    highPrice = highPrice,
                    note = note
                )
            )
        }
    }

    override suspend fun deleteEntry(entryId: Long): SonarResult<Unit, HomeApiError> =
        safeApiCall { client.delete(PortfoliosRoute.Entry.Delete(PortfoliosRoute.Entry(id = entryId))) }
}