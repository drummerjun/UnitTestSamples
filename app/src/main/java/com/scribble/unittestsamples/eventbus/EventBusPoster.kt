package com.scribble.unittestsamples.eventbus

interface EventBusPoster {
    fun postEvent(event: Any?)
}