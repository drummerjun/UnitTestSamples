package com.scribble.unittestsamples

import com.scribble.unittestsamples.authtoken.AuthTokenCache
import com.scribble.unittestsamples.eventbus.EventBusPoster
import com.scribble.unittestsamples.eventbus.LoggedInEvent
import com.scribble.unittestsamples.networking.LoginHttpEndpointSync
import com.scribble.unittestsamples.networking.NetworkErrorException


class LoginUseCaseSync(
    loginHttpEndpointSync: LoginHttpEndpointSync,
    authTokenCache: AuthTokenCache,
    eventBusPoster: EventBusPoster
) {
    enum class UseCaseResult {
        SUCCESS, FAILURE, NETWORK_ERROR
    }

    private val mLoginHttpEndpointSync: LoginHttpEndpointSync = loginHttpEndpointSync
    private val mAuthTokenCache: AuthTokenCache = authTokenCache
    private val mEventBusPoster: EventBusPoster = eventBusPoster

    fun loginSync(username: String?, password: String?): UseCaseResult {
        val endpointEndpointResult = try {
            mLoginHttpEndpointSync.loginSync(username, password)
        } catch (e: NetworkErrorException) {
            return UseCaseResult.FAILURE
        }

        mEventBusPoster.postEvent(LoggedInEvent())

        return if (isSuccessfulEndpointResult(endpointEndpointResult)) {
            mAuthTokenCache.cacheAuthToken(endpointEndpointResult?.authToken)
            UseCaseResult.SUCCESS
        } else {
            UseCaseResult.FAILURE
        }
    }

    private fun isSuccessfulEndpointResult(endpointResult: LoginHttpEndpointSync.EndpointResult?): Boolean {
        return endpointResult?.status === LoginHttpEndpointSync.EndpointResultStatus.SUCCESS
    }

}