package ru.kima.sonar.server.feature.portfolios.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.flow.first
import ru.kima.sonar.common.serverapi.dto.portfolio.request.CreatePortfolioRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.request.UpdatePortfolioRequest
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.feature.portfolios.mappers.toDto
import java.math.BigDecimal

internal class PortfoliosController(
    private val userDataSource: UserDataSource,
    private val portfoliosDataSource: PortfolioDataSource,
    private val marketDataRepository: MarketDataRepository
) {
    suspend fun portfoliosRoute(call: RoutingCall) {
        val user = call.principal<User>() ?: run {
            call.respond(HttpStatusCode.InternalServerError)
            return
        }

        return when (val res = portfoliosDataSource.getPortfoliosByUserId(user.id)) {
            is SonarResult.Success -> call.respond(res.data.map { it.toDto() })
            is SonarResult.Error -> call.respond(HttpStatusCode.InternalServerError)
        }
    }

    suspend fun createPortfolio(call: RoutingCall) {
        val user = call.principal<User>() ?: run {
            call.respond(HttpStatusCode.InternalServerError)
            return
        }

        val request = try {
            call.receive<CreatePortfolioRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

        when (val res = portfoliosDataSource.insertPortfolio(
            Portfolio.default(
                userId = user.id,
                name = request.name
            )
        )) {
            is SonarResult.Success -> call.respond(res.data.toDto())
            is SonarResult.Error -> call.respond(HttpStatusCode.InternalServerError)
        }
    }

    suspend fun getPortfolio(call: RoutingCall, portfolioId: Long) {
        val user = call.principal<User>() ?: run {
            call.respond(HttpStatusCode.InternalServerError)
            return
        }

        when (val res = portfoliosDataSource.getPortfolioWithEntriesById(portfolioId)) {
            is SonarResult.Success -> {
                if (res.data.portfolio.userId != user.id) call.respond(HttpStatusCode.Forbidden)
                else call.respond(res.data.toDto(getCurrentPrices()))
            }

            is SonarResult.Error -> call.handleUserDataError(res.data)
        }
    }

    suspend fun updatePortfolio(call: RoutingCall, portfolioId: Long) {
        val user = call.principal<User>() ?: run {
            call.respond(HttpStatusCode.InternalServerError)
            return
        }

        val request = try {
            call.receive<UpdatePortfolioRequest>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }

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

        when (val res = portfoliosDataSource.updatePortfolio(
            oldPortfolio.copy(
                name = request.name
            )
        )) {
            is SonarResult.Success -> call.respond(res.data.toDto())
            is SonarResult.Error -> call.handleUserDataError(res.data)
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
}