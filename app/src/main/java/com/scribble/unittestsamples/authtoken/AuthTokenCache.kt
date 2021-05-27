package com.scribble.unittestsamples.authtoken

interface AuthTokenCache {
    fun cacheAuthToken(authToken: String?)
    val authToken: String?
}
