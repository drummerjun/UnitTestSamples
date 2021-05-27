package com.scribble.unittestsamples

import com.scribble.unittestsamples.networking.AddToCartHttpEndpointSync
import com.scribble.unittestsamples.networking.CartItemScheme
import com.scribble.unittestsamples.networking.NetworkErrorException

class AddToCartUseCaseSync(addToCartHttpEndpointSync: AddToCartHttpEndpointSync) {
    enum class UseCaseResult {
        SUCCESS, FAILURE, NETWORK_ERROR
    }

    private val mAddToCartHttpEndpointSync: AddToCartHttpEndpointSync = addToCartHttpEndpointSync
    fun addToCartSync(offerId: String?, amount: Int): UseCaseResult {
        val result = try {
            mAddToCartHttpEndpointSync.addToCartSync(CartItemScheme(offerId ?: "", amount))
        } catch (e: NetworkErrorException) {
            return UseCaseResult.NETWORK_ERROR
        }
        return when (result) {
            AddToCartHttpEndpointSync.EndpointResult.SUCCESS -> UseCaseResult.SUCCESS
            AddToCartHttpEndpointSync.EndpointResult.AUTH_ERROR,
            AddToCartHttpEndpointSync.EndpointResult.GENERAL_ERROR -> UseCaseResult.FAILURE
            else -> throw RuntimeException("invalid endpoint result: $result")
        }
    }
}