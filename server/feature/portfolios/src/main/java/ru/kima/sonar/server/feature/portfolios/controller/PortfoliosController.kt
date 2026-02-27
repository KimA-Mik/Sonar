package ru.kima.sonar.server.feature.portfolios.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.flow.first
import ru.kima.sonar.common.serverapi.dto.portfolio.request.AddPortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.CreatePortfolioRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.UpdatePortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.UpdatePortfolioRequest
import ru.kima.sonar.common.serverapi.util.TITLE_LENGTH
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.feature.portfolios.mappers.toDto
import java.math.BigDecimal

internal class PortfoliosController(
    private val userDataSource: UserDataSource,
    private val portfoliosDataSource: PortfolioDataSource,
    private val marketDataRepository: MarketDataRepository
) {
    suspend fun portfoliosRoute(call: RoutingCall) {
        val user = call.getUserOrISE { return }
        return when (val res = portfoliosDataSource.getPortfoliosByUserId(user.id)) {
            is SonarResult.Success -> call.respond(res.data.map { it.toDto() })
            is SonarResult.Error -> call.respond(HttpStatusCode.InternalServerError)
        }
    }

    suspend fun createPortfolio(call: RoutingCall) {
        val user = call.getUserOrISE { return }
        val request = try {
            call.receive<CreatePortfolioRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        var name = request.name.trim()
        if (name.length > TITLE_LENGTH) name = name.take(TITLE_LENGTH)
        when (val res = portfoliosDataSource.insertPortfolio(
            Portfolio.default(
                userId = user.id,
                name = name
            )
        )) {
            is SonarResult.Success -> call.respond(res.data.toDto())
            is SonarResult.Error -> call.respond(HttpStatusCode.InternalServerError)
        }
    }

    suspend fun getPortfolio(call: RoutingCall, portfolioId: Long) {
        val user = call.getUserOrISE { return }
        when (val res = portfoliosDataSource.getPortfolioWithEntriesById(portfolioId)) {
            is SonarResult.Success -> {
                if (res.data.portfolio.userId != user.id) call.respond(HttpStatusCode.Forbidden)
                else call.respond(res.data.toDto(getCurrentPrices()))
            }

            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun updatePortfolio(call: RoutingCall, portfolioId: Long) {
        val user = call.getUserOrISE { return }
        val oldPortfolio = when (val res = portfoliosDataSource.getPortfolioById(portfolioId)) {
            is SonarResult.Success -> res.data
            is SonarResult.Error -> {
                call.handleUserDataError(res.data)
                return
            }
        }

        if (oldPortfolio.userId != user.id) {
            call.respond(HttpStatusCode.Forbidden)
            return
        }

        val request = try {
            call.receive<UpdatePortfolioRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        when (val res = portfoliosDataSource.updatePortfolio(
            oldPortfolio.copy(
                name = request.name
            )
        )) {
            is SonarResult.Success -> call.respond(res.data.toDto())
            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun deletePortfolio(call: RoutingCall, portfolioId: Long) {
        val user = call.getUserOrISE { return }
        call.returnIfNotOwn(user.id, portfolioId) { return }

        when (val res = portfoliosDataSource.deletePortfolioById(portfolioId)) {
            is SonarResult.Success -> call.respond(HttpStatusCode.OK)
            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun addEntry(call: RoutingCall, portfolioId: Long) {
        val user = call.getUserOrISE { return }
        call.returnIfNotOwn(user.id, portfolioId) { return }

        val request = try {
            call.receive<AddPortfolioEntryRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        when (val res = portfoliosDataSource.insertPortfolioEntry(
            PortfolioEntry.default(
                portfolioId = portfolioId,
                securityUid = request.securityUid,
                name = request.name,
                lowPrice = request.lowPrice,
                highPrice = request.highPrice,
                note = request.note
            )
        )) {
            is SonarResult.Success -> call.respond(HttpStatusCode.OK)
            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun updateEntry(call: RoutingCall, entryId: Long) {
        val user = call.getUserOrISE { return }
        val oldEntry = when (val res = portfoliosDataSource.getEntryById(entryId)) {
            is SonarResult.Success -> res.data
            is SonarResult.Error -> {
                call.handleUserDataError(res.data)
                return
            }
        }
        call.returnIfNotOwn(user.id, oldEntry.portfolioId) { return }

        val request = try {
            call.receive<UpdatePortfolioEntryRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        when (val res = portfoliosDataSource.updatePortfolioEntry(
            oldEntry.copy(
                name = request.name,
                lowPrice = request.lowPrice,
                highPrice = request.highPrice,
                note = request.note
            )
        )) {
            is SonarResult.Success -> call.respond(HttpStatusCode.OK)
            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun deleteEntry(call: RoutingCall, entryId: Long) {
        val user = call.getUserOrISE { return }
        val entry = when (val res = portfoliosDataSource.getEntryById(entryId)) {
            is SonarResult.Success -> res.data
            is SonarResult.Error -> {
                call.handleUserDataError(res.data)
                return
            }
        }
        call.returnIfNotOwn(user.id, entry.portfolioId) { return }

        when (val res = portfoliosDataSource.deletePortfolioEntry(entryId)) {
            is SonarResult.Success -> call.respond(HttpStatusCode.OK)
            is SonarResult.Error -> when (res.data) {
                UserDataError.NotFound -> call.respond(HttpStatusCode.Forbidden)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }

    private suspend fun getCurrentPrices(): Map<String, BigDecimal> {
        val shares = marketDataRepository.tradableShares().first()
        val futures = marketDataRepository.tradableFutures().first()
        return buildMap(shares.size + futures.size) {
            shares.forEach { put(it.ticker, it.price) }
            futures.forEach { put(it.ticker, it.price) }
        }
    }

    private suspend fun RoutingCall.handleUserDataError(error: UserDataError) {
        when (error) {
            UserDataError.NotFound -> respond(HttpStatusCode.Forbidden)
            else -> respond(HttpStatusCode.InternalServerError)
        }
    }

    private suspend inline fun RoutingCall.getUserOrISE(actionReturn: () -> Nothing): User {
        val user = principal<User>()
        return if (user != null) {
            user
        } else {
            respond(HttpStatusCode.InternalServerError)
            actionReturn()
        }
    }

    private suspend inline fun RoutingCall.returnIfNotOwn(
        userId: Long,
        portfolioId: Long,
        onInvalid: () -> Nothing
    ) {
        val oldPortfolio = when (val res = portfoliosDataSource.getPortfolioById(portfolioId)) {
            is SonarResult.Success -> res.data
            is SonarResult.Error -> {
                handleUserDataError(res.data)
                onInvalid()
            }
        }

        if (oldPortfolio.userId != userId) {
            respond(HttpStatusCode.Forbidden)
            onInvalid()
        }
    }
}