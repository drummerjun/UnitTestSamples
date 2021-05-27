package com.scribble.unittestsamples

import com.scribble.unittestsamples.eventbus.EventBusPoster
import com.scribble.unittestsamples.eventbus.UserDetailChangedEvent
import com.scribble.unittestsamples.networking.NetworkErrorException
import com.scribble.unittestsamples.networking.UpdateUsernameHttpEndpointSync
import com.scribble.unittestsamples.users.User
import com.scribble.unittestsamples.users.UserCache
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.OngoingStubbing

@RunWith(MockitoJUnitRunner::class)
class UpdateUsernameUseCaseSyncTest {
    @Mock val httpEndpointSync: UpdateUsernameHttpEndpointSync = mock()
    @Mock val userCache: UserCache = mock()
    @Mock val eventBusPoster: EventBusPoster = mock()
    lateinit var SUT: UpdateUsernameUseCaseSync

    @Before
    @Throws(Exception::class)
    fun setup() {
        SUT = UpdateUsernameUseCaseSync(httpEndpointSync, userCache, eventBusPoster)
        whenever(httpEndpointSync.updateUsername(any(String::class.java), any(String::class.java)))
            .thenReturn(
                UpdateUsernameHttpEndpointSync.EndpointResult(
                    UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USERID, USERNAME
                )
            )
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_success_userIdAndUsernamePassedToEndpoint() {
        val ac = ArgumentCaptor.forClass(String::class.java)
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(httpEndpointSync, times(1)).updateUsername(ac.capture(), ac.capture())
        val captures = ac.allValues
        Assert.assertEquals(captures[0], USERID)
        Assert.assertEquals(captures[1], USERNAME)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_success_userCached() {
        val ac: ArgumentCaptor<User> = ArgumentCaptor.forClass(User::class.java)
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(userCache).cacheUser(ac.capture())
        val cachedUser: User = ac.value
        Assert.assertEquals(cachedUser.userId, USERID)
        Assert.assertEquals(cachedUser.username, USERNAME)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_generalError_userNotCached() {
        generalError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(userCache)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_authError_userNotCached() {
        authError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(userCache)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_serverError_userNotCached() {
        serverError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(userCache)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_success_loggedInEventPosted() {
        val ac = ArgumentCaptor.forClass(Any::class.java)
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(eventBusPoster).postEvent(ac.capture())
        Assert.assertThat(ac.value, CoreMatchers.instanceOf(UserDetailChangedEvent::class.java))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_generalError_noInteractionWithEventBusPoster() {
        generalError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(eventBusPoster)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_authError_noInteractionWithEventBusPoster() {
        authError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(eventBusPoster)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_serverError_noInteractionWithEventBusPoster() {
        serverError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(eventBusPoster)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_success_successReturned() {
        val result: UpdateUsernameUseCaseSync.UseCaseResult = SUT.updateUsernameSync(USERID, USERNAME)
        Assert.assertEquals(result, UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_serverError_failureReturned() {
        serverError()
        val result: UpdateUsernameUseCaseSync.UseCaseResult = SUT.updateUsernameSync(USERID, USERNAME)
        Assert.assertEquals(result, UpdateUsernameUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_authError_failureReturned() {
        authError()
        val result: UpdateUsernameUseCaseSync.UseCaseResult = SUT.updateUsernameSync(USERID, USERNAME)
        Assert.assertEquals(result, UpdateUsernameUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_generalError_failureReturned() {
        generalError()
        val result: UpdateUsernameUseCaseSync.UseCaseResult = SUT.updateUsernameSync(USERID, USERNAME)
        Assert.assertEquals(result, UpdateUsernameUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun updateUsername_networkError_networkErrorReturned() {
        networkError()
        val result: UpdateUsernameUseCaseSync.UseCaseResult = SUT.updateUsernameSync(USERID, USERNAME)
        Assert.assertEquals(result, UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR)
    }

    @Throws(java.lang.Exception::class)
    private fun networkError() {
        doThrow(NetworkErrorException())
            .`when`(httpEndpointSync)
            .updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    @Throws(Exception::class)
    private fun generalError() {
        whenever(
            httpEndpointSync.updateUsername(any(String::class.java), any(String::class.java))
        ).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""
            )
        )
    }

    @Throws(Exception::class)
    private fun authError() {
        whenever(
            httpEndpointSync.updateUsername(any(String::class.java), any(String::class.java))
        ).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""
            )
        )
    }

    @Throws(Exception::class)
    private fun serverError() {
        whenever(
            httpEndpointSync.updateUsername(any(String::class.java), any(String::class.java))
        ).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""
            )
        )
    }

    companion object {
        const val USERNAME = "username"
        const val USERID = "userid"
    }
}

inline fun <reified T> mock(): T = mock(T::class.java)

fun <T> whenever(methodCall: T): OngoingStubbing<T> = `when`(methodCall)
