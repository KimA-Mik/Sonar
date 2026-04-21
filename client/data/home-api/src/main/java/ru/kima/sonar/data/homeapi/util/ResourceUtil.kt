package ru.kima.sonar.data.homeapi.util

import android.content.res.Resources
import ru.kima.sonar.data.homeapi.error.HomeApiError

typealias HomeApiStrings = ru.kima.sonar.data.homeapi.R.string

fun Resources.getHomeApiErrorString(error: HomeApiError): String {
    return when (error) {
        HomeApiError.BadRequest -> getString(HomeApiStrings.error_bad_request)
        HomeApiError.Forbidden -> getString(HomeApiStrings.error_forbidden)
        HomeApiError.InternalServerError -> getString(HomeApiStrings.error_internal_server)
        HomeApiError.NetworkError -> getString(HomeApiStrings.error_network)
        HomeApiError.Unauthorized -> getString(HomeApiStrings.error_unauthorized)
        is HomeApiError.UnknownApiError -> getString(
            HomeApiStrings.error_unknown_api,
            error.code,
        )

        is HomeApiError.UnknownError -> getString(HomeApiStrings.error_unknown)
    }
}