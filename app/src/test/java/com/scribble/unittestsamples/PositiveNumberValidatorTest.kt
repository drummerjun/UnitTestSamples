package com.scribble.unittestsamples

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PositiveNumberValidatorTest {
    lateinit var SUT: PositiveNumberValidator

    @Before
    fun setup() {
        SUT = PositiveNumberValidator()
    }

    @Test
    fun test1() {
        val result = SUT.isPositive(-1)
        Assert.assertEquals(result, false)
    }

    @Test
    fun test2() {
        val result = SUT.isPositive(0)
        Assert.assertEquals(result, false)
    }

    @Test
    fun test3() {
        val result = SUT.isPositive(1)
        Assert.assertEquals(result, true)
    }
}