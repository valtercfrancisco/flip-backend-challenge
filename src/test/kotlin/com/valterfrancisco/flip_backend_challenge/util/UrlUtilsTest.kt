package com.valterfrancisco.flip_backend_challenge.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UrlValidationUtilTest {

    @Test
    fun `should return true for valid HTTP URL`() {
        val url = "http://example.com"
        val result = isValidUrl(url)
        assertTrue(result, "Expected URL to be valid")
    }

    @Test
    fun `should return true for valid HTTPS URL`() {
        val url = "https://example.com"
        val result = isValidUrl(url)
        assertTrue(result, "Expected URL to be valid")
    }

    @Test
    fun `should return false for URL with no scheme`() {
        val url = "example.com"
        val result = isValidUrl(url)
        assertFalse(result, "Expected URL to be invalid due to missing scheme")
    }

    @Test
    fun `should return false for invalid scheme`() {
        val url = "ftp://example.com"
        val result = isValidUrl(url)
        assertFalse(result, "Expected URL to be invalid due to unsupported scheme")
    }

    @Test
    fun `should return false for invalid URL format`() {
        val url = "http://"
        val result = isValidUrl(url)
        assertFalse(result, "Expected URL to be invalid due to incorrect format")
    }

    @Test
    fun `should return false for malformed URL`() {
        val url = "http://example..com"
        val result = isValidUrl(url)
        assertFalse(result, "Expected URL to be invalid due to being malformed")
    }

    @Test
    fun `should return false for empty URL`() {
        val url = ""
        val result = isValidUrl(url)
        assertFalse(result, "Expected URL to be invalid due to being empty")
    }
}