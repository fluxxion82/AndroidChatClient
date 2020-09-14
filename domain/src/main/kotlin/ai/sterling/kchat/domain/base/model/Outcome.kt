package ai.sterling.kchat.domain.base.model

import ai.sterling.kchat.domain.exception.Failure

sealed class Outcome<out TResult : Any> {
    data class Success<out TResult : Any>(val value: TResult) : Outcome<TResult>()
    data class Error(val message: String, val cause: Failure) : Outcome<Nothing>()
}
