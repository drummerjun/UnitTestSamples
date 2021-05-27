package com.scribble.unittestsamples

import com.scribble.unittestsamples.authtoken.AuthTokenCache
import com.scribble.unittestsamples.eventbus.EventBusPoster
import com.scribble.unittestsamples.eventbus.LoggedInEvent
import com.scribble.unittestsamples.networking.LoginHttpEndpointSync
import com.scribble.unittestsamples.networking.NetworkErrorException
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

class LoginUseCaseSyncTest {
    lateinit var mAuthTokenCacheMock: AuthTokenCache
    lateinit var mEventBusPosterMock: EventBusPoster
    private lateinit var mLoginHttpEndpointSyncMock: LoginHttpEndpointSync
    private lateinit var SUT: LoginUseCaseSync

    @Before
    @Throws(Exception::class)
    fun setup() {
        mLoginHttpEndpointSyncMock = mock(LoginHttpEndpointSync::class.java)
        mAuthTokenCacheMock = mock(AuthTokenCache::class.java)
        mEventBusPosterMock = mock(EventBusPoster::class.java)
        SUT = LoginUseCaseSync(mLoginHttpEndpointSyncMock, mAuthTokenCacheMock, mEventBusPosterMock)
        success()
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_usernameAndPasswordPassedToEndpoint() {
        val ac: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mLoginHttpEndpointSyncMock, times(1)).loginSync(ac.capture(), ac.capture())
        val captures: List<String> = ac.allValues
        Assert.assertEquals(captures[0], USERNAME)
        Assert.assertEquals(captures[1], PASSWORD)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_authTokenCached() {
        val ac: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mAuthTokenCacheMock).cacheAuthToken(ac.capture())
        Assert.assertEquals(ac.value, AUTH_TOKEN)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_authTokenNotCached() {
        generalError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mAuthTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_authTokenNotCached() {
        authError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mAuthTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_authTokenNotCached() {
        serverError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mAuthTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_loggedInEventPosted() {
        val ac: ArgumentCaptor<Any> = ArgumentCaptor.forClass(Any::class.java)
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mEventBusPosterMock).postEvent(ac.capture())
        Assert.assertThat(ac.value, CoreMatchers.instanceOf(LoggedInEvent::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_noInteractionWithEventBusPoster() {
        generalError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_noInteractionWithEventBusPoster() {
        authError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_noInteractionWithEventBusPoster() {
        serverError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(mEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_successReturned() {
        val result = SUT.loginSync(USERNAME, PASSWORD)
        Assert.assertEquals(result, LoginUseCaseSync.UseCaseResult.SUCCESS)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_failureReturned() {
        serverError()
        val result = SUT.loginSync(USERNAME, PASSWORD)
        Assert.assertEquals(result, LoginUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_failureReturned() {
        authError()
        val result = SUT.loginSync(USERNAME, PASSWORD)
        Assert.assertEquals(result, LoginUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_failureReturned() {
        generalError()
        val result = SUT.loginSync(USERNAME, PASSWORD)
        Assert.assertEquals(result, LoginUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_networkError_networkErrorReturned() {
        networkError()
        val result = SUT.loginSync(USERNAME, PASSWORD)
        Assert.assertEquals(result, LoginUseCaseSync.UseCaseResult.NETWORK_ERROR)
    }

    @Throws(Exception::class)
    private fun networkError() {
        doThrow(NetworkErrorException())
            .`when`(mLoginHttpEndpointSyncMock)
            .loginSync(any(String::class.java), any(String::class.java))
    }

    @Throws(NetworkErrorException::class)
    private fun success() {
        `when`(
            mLoginHttpEndpointSyncMock.loginSync(any(String::class.java), any(String::class.java))
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SUCCESS, AUTH_TOKEN)
        )
    }

    @Throws(Exception::class)
    private fun generalError() {
        `when`(
            mLoginHttpEndpointSyncMock.loginSync(
                any(String::class.java),
                any(String::class.java)
            )
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                ""
            )
        )
    }

    @Throws(Exception::class)
    private fun authError() {
        `when`(
            mLoginHttpEndpointSyncMock.loginSync(
                any(String::class.java),
                any(String::class.java)
            )
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                ""
            )
        )
    }

    @Throws(Exception::class)
    private fun serverError() {
        `when`(
            mLoginHttpEndpointSyncMock.loginSync(
                any(String::class.java),
                any(String::class.java)
            )
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                ""
            )
        )
    }

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val AUTH_TOKEN = "authToken"
    }
}