package com.scribble.unittestsamples.eventbus

import com.scribble.unittestsamples.users.User

class UserDetailChangedEvent(user: User) {
    private val mUser: User = user
    val user: User
        get() = mUser
}
