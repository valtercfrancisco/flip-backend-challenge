package com.valterfrancisco.flip_backend_challenge.controller

import com.ninjasquad.springmockk.MockkBean
import com.valterfrancisco.flip_backend_challenge.model.Url
import com.valterfrancisco.flip_backend_challenge.service.FlipShortenerService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(FlipShortenerController::class)
class FlipShortenerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var flipShortenerService: FlipShortenerService

    @Test
    fun `shortenUrl should return shortened URL when successful`() {
        // Given
        val longUrl = "https://example.com"
        val shortUrl = "http://localhost:8080/api/v1/shortener/abc123"

        every { flipShortenerService.shortenUrl(longUrl) } returns shortUrl

        // When & Then
        mockMvc.perform(post("/api/v1/shortener/shorten")
            .param("longUrl", longUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk)
            .andExpect(content().string(shortUrl))
    }

    @Test
    fun `getOriginalUrl should return 302 and redirect to original URL when short URL exists`() {
        // Given
        val shortUrlId = "abc123"
        val originalUrl = "https://example.com"
        val url = Url(shortUrlId = shortUrlId, longUrl = originalUrl)

        every { flipShortenerService.getOriginalUrl(shortUrlId) } returns Optional.of(url)

        // When & Then
        mockMvc.perform(get("/api/v1/shortener/{shortUrlId}", shortUrlId))
            .andExpect(status().isFound)
            .andExpect(header().string("Location", originalUrl)) // Asserting the redirect header
    }

    @Test
    fun `shortenUrl should return bad request when invalid URL is provided`() {
        // Given
        val invalidUrl = "invalid_url"

        // When & Then
        mockMvc.perform(post("/api/v1/shortener/shorten")
            .param("longUrl", invalidUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid URL format."))
    }

    @Test
    fun `shortenUrl should return internal server error when service throws exception`() {
        // Given
        val longUrl = "https://example.com"
        every { flipShortenerService.shortenUrl(longUrl) } throws RuntimeException("Service error")

        // When & Then
        mockMvc.perform(post("/api/v1/shortener/shorten")
            .param("longUrl", longUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string("An error occurred while processing the request."))
    }

    @Test
    fun `getOriginalUrl should return 404 when short URL does not exist`() {
        // Given
        val shortUrlId = "nonexistent"

        every { flipShortenerService.getOriginalUrl(shortUrlId) } returns Optional.empty()

        // When & Then
        mockMvc.perform(get("/api/v1/shortener/{shortUrlId}", shortUrlId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getOriginalUrl should return internal server error when service throws exception`() {
        // Given
        val shortUrlId = "abc123"
        every { flipShortenerService.getOriginalUrl(shortUrlId) } throws RuntimeException("Service error")

        // When & Then
        mockMvc.perform(get("/api/v1/shortener/{shortUrlId}", shortUrlId))
            .andExpect(status().isInternalServerError)
    }

    @Test
    fun `deleteUrl should return 204 when URL is deleted successfully`() {
        // Given
        val shortUrlId = "abc123"

        every { flipShortenerService.deleteUrl(shortUrlId) } returns true

        // When & Then
        mockMvc.perform(delete("/api/v1/shortener/{shortUrlId}", shortUrlId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUrl should return 404 when URL does not exist`() {
        // Given
        val shortUrlId = "nonexistent"

        every { flipShortenerService.deleteUrl(shortUrlId) } returns false

        // When & Then
        mockMvc.perform(delete("/api/v1/shortener/{shortUrlId}", shortUrlId))
            .andExpect(status().isNotFound)
    }
}