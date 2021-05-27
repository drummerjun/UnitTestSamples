package com.scribble.unittestsamples.users

interface UserCache {
    fun cacheUser(user: User?)
    fun getUser(userId: String?): User?
}