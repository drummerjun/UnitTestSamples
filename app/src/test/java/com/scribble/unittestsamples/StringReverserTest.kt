package com.scribble.unittestsamples

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class StringReverserTest {
    lateinit var SUT: StringReverser

    @Before
    @Throws(Exception::class)
    fun setup() {
        SUT = StringReverser()
    }

    @Test
    @Throws(Exception::class)
    fun reverse_emptyString_emptyStringReturned() {
        val result = SUT.reverse("")
        Assert.assertEquals(result, "")
    }

    @Test
    @Throws(Exception::class)
    fun reverse_singleCharacter_sameStringReturned() {
        val result = SUT.reverse("a")
        Assert.assertEquals(result, "a")
    }

    @Test
    @Throws(Exception::class)
    fun reverse_longString_reversedStringReturned() {
        val result = SUT.reverse("Vasiliy Zukanov")
        Assert.assertEquals(result, "vonakuZ yilisaV")
    }
}