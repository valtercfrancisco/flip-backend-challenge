package com.valterfrancisco.flip_backend_challenge.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
class EncodeUtilsKtTest {

    @Test
    fun `hash should return MD5 hash of string with default truncation`() {
        // Given
        val inputString = "https://hash.com"

        // When
        val result = inputString.hash()

        // Then
        assertEquals("ad1d12", result)
    }

    @Test
    fun `hash should return MD5 hash of string with custom truncation length`() {
        // Given
        val inputString = "https://hash.com"
        val truncateLength = 8

        // When
        val result = inputString.hash(truncate = truncateLength)

        // Then
        assertEquals("ad1d12de", result)
    }

    @Test
    fun `hash should handle an empty string`() {
        // Given
        val inputString = ""

        // When
        val result = inputString.hash()

        // Then
        assertEquals("d41d8c", result)
    }

    @Test
    fun `hash should return correct result for small input strings`() {
        // Given
        val inputString = "abc"

        // When
        val result = inputString.hash()

        // Then
        assertEquals("900150", result)
    }
}