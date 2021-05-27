package com.scribble.unittestsamples

import com.scribble.unittestsamples.networking.AddToCartHttpEndpointSync
import com.scribble.unittestsamples.networking.CartItemScheme
import com.scribble.unittestsamples.networking.NetworkErrorException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddToCartUseCaseSyncTest {
    @Mock var mAddToCartHttpEndpointSyncMock: AddToCartHttpEndpointSync = mock()
    lateinit var SUT: AddToCartUseCaseSync

    @Before
    @Throws(Exception::class)
    fun setup() {
        SUT = AddToCartUseCaseSync(mAddToCartHttpEndpointSyncMock)
        success()
    }

    @Test
    @Throws(Exception::class)
    fun addToCartSync_correctParametersPassedToEndpoint() {
        // Arrange
        val ac: ArgumentCaptor<CartItemScheme> = ArgumentCaptor.forClass(CartItemScheme::class.java)
        // Act
        SUT.addToCartSync(OFFER_ID, AMOUNT)
        // Assert
        verify {
            mAddToCartHttpEndpointSyncMock.addToCartSync(ac.capture())
        }
        Assert.assertEquals(ac.value.offerId, OFFER_ID)
        Assert.assertEquals(ac.value.amount, AMOUNT)
    }

    @Test
    @Throws(Exception::class)
    fun addToCartSync_success_successReturned() {
        val result: AddToCartUseCaseSync.UseCaseResult = SUT.addToCartSync(OFFER_ID, AMOUNT)
        Assert.assertEquals(result, AddToCartUseCaseSync.UseCaseResult.SUCCESS)
    }

    @Test
    @Throws(Exception::class)
    fun addToCartSync_authError_failureReturned() {
        // Arrange
        authError()
        // Act
        val result: AddToCartUseCaseSync.UseCaseResult = SUT.addToCartSync(OFFER_ID, AMOUNT)
        // Assert
        Assert.assertEquals(result, AddToCartUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(Exception::class)
    fun addToCartSync_generalError_failureReturned() {
        // Arrange
        generalError()
        // Act
        val result: AddToCartUseCaseSync.UseCaseResult = SUT.addToCartSync(OFFER_ID, AMOUNT)
        // Assert
        Assert.assertEquals(result, AddToCartUseCaseSync.UseCaseResult.FAILURE)
    }

    @Test
    @Throws(Exception::class)
    fun addToCartSync_networkError_networkErrorReturned() {
        // Arrange
        networkError()
        // Act
        val result: AddToCartUseCaseSync.UseCaseResult = SUT.addToCartSync(OFFER_ID, AMOUNT)
        // Assert
        Assert.assertEquals(result, AddToCartUseCaseSync.UseCaseResult.NETWORK_ERROR)
    }

    // region helper methods -----------------------------------------------------------------------
    @Throws(NetworkErrorException::class)
    private fun success() {
        whenever(mAddToCartHttpEndpointSyncMock.addToCartSync(
                ArgumentMatchers.any(CartItemScheme::class.java)
            )
        ).thenReturn(AddToCartHttpEndpointSync.EndpointResult.SUCCESS)
    }

    @Throws(NetworkErrorException::class)
    private fun authError() {
        whenever(
            mAddToCartHttpEndpointSyncMock.addToCartSync(
                ArgumentMatchers.any(CartItemScheme::class.java)
            )
        ).thenReturn(AddToCartHttpEndpointSync.EndpointResult.AUTH_ERROR)
    }

    @Throws(NetworkErrorException::class)
    private fun generalError() {
        whenever(
            mAddToCartHttpEndpointSyncMock.addToCartSync(
                ArgumentMatchers.any(CartItemScheme::class.java)
            )
        ).thenReturn(AddToCartHttpEndpointSync.EndpointResult.GENERAL_ERROR)
    }

    @Throws(NetworkErrorException::class)
    private fun networkError() {
        whenever(
            mAddToCartHttpEndpointSyncMock.addToCartSync(
                ArgumentMatchers.any(CartItemScheme::class.java)
            )
        ).thenThrow(NetworkErrorException())
    }

    companion object {
        const val OFFER_ID = "offerId"
        const val AMOUNT = 4
    }
}