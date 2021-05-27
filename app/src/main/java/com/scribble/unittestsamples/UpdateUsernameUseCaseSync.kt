package com.scribble.unittestsamples

import com.scribble.unittestsamples.eventbus.EventBusPoster
import com.scribble.unittestsamples.eventbus.UserDetailChangedEvent
import com.scribble.unittestsamples.networking.NetworkErrorException
import com.scribble.unittestsamples.networking.UpdateUsernameHttpEndpointSync
import com.scribble.unittestsamples.users.User
import com.scribble.unittestsamples.users.UserCache

class UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSync: UpdateUsernameHttpEndpointSync,
                                usersCache: UserCache, eventBusPoster: EventBusPoster)
{
    enum class UseCaseResult {
        SUCCESS, FAILURE, NETWORK_ERROR
    }

    private val mUpdateUsernameHttpEndpointSync: UpdateUsernameHttpEndpointSync = updateUsernameHttpEndpointSync
    private val mUsersCache: UserCache = usersCache
    private val mEventBusPoster: EventBusPoster = eventBusPoster

    fun updateUsernameSync(userId: String?, username: String?): UseCaseResult {
        var endpointResult: UpdateUsernameHttpEndpointSync.EndpointResult? = null
        try {
            endpointResult = mUpdateUsernameHttpEndpointSync.updateUsername(userId, username)
        } catch (e: NetworkErrorException) {
            // the bug here is "swallowed" exception instead of return
        }
        return if (isSuccessfulEndpointResult(endpointResult)) {
            // the bug here is reversed arguments
            val user = User(endpointResult?.username, endpointResult?.userId)
            mEventBusPoster.postEvent(UserDetailChangedEvent(User(userId, username)))
            mUsersCache.cacheUser(user)
            UseCaseResult.SUCCESS
        } else {
            UseCaseResult.FAILURE
        }
    }

    private fun isSuccessfulEndpointResult(endpointResult: UpdateUsernameHttpEndpointSync.EndpointResult?): Boolean {
        // the bug here is the wrong definition of successful response
        return (endpointResult?.status === UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS
                || endpointResult?.status === UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR)
    }

}