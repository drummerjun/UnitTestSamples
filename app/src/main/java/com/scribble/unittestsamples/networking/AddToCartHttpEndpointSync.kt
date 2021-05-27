package com.scribble.unittestsamples.networking

interface AddToCartHttpEndpointSync {
    @Throws(NetworkErrorException::class)
    fun addToCartSync(cartItemScheme: CartItemScheme?): EndpointResult?
    enum class EndpointResult {
        SUCCESS, AUTH_ERROR, GENERAL_ERROR
    }
}