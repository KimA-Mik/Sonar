package ru.kima.sonar.common.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


sealed interface SonarResult<out S, out E> {
    data class Success<out S, E>(val data: S) : SonarResult<S, E>
    data class Error<S, out E>(val data: E) : SonarResult<S, E>
}

inline fun <S, E> SonarResult<S, E>.valueOr(alternative: (E) -> S): S {
    return when (this) {
        is SonarResult.Error -> alternative(data)
        is SonarResult.Success -> data
    }
}

@OptIn(ExperimentalContracts::class)
fun <S, E> SonarResult<S, E>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is SonarResult.Success<S, E>)
        returns(false) implies (this@isSuccess is SonarResult.Error<S, E>)
    }
    return this is SonarResult.Success<S, E>
}

@OptIn(ExperimentalContracts::class)
fun <S, E> SonarResult<S, E>.isError(): Boolean {
    contract {
        returns(true) implies (this@isError is SonarResult.Error<S, E>)
        returns(false) implies (this@isError is SonarResult.Success<S, E>)
    }
    return this is SonarResult.Error<S, E>
}

fun <S, E> SonarResult<S, E>.getOrNull(): S? {
    return when (this) {
        is SonarResult.Error -> null
        is SonarResult.Success -> data
    }
}

inline fun <R> sonarRunCatching(block: () -> R): SonarResult<R, Exception> {
    return try {
        SonarResult.Success(block())
    } catch (e: Exception) {
        SonarResult.Error(e)
    }
}

inline fun <T, R> T.sonarRunCaching(block: T.() -> R): SonarResult<R, Exception> {
    return try {
        SonarResult.Success(block())
    } catch (e: Exception) {
        SonarResult.Error(e)
    }
}