package com.valterfrancisco.flip_backend_challenge.service

import com.valterfrancisco.flip_backend_challenge.model.Url
import com.valterfrancisco.flip_backend_challenge.repository.UrlRepository
import org.junit.jupiter.api.Assertions.*

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Optional

@ExtendWith(SpringExtension::class)
class FlipShortenerServiceTest {

    private lateinit var urlRepository: UrlRepository
    private lateinit var flipShortenerService: FlipShortenerService

    private val baseUrl = "http://localhost:8080/api/v1/shortener/"

    @BeforeEach
    fun setUp() {
        urlRepository = mockk()
        flipShortenerService = spyk(FlipShortenerService(urlRepository))
    }

    @Test
    fun `shortenUrl should return existing short URL when long URL already exists`() {
        val longUrl = "https://example.com"
        val shortUrlId = "abc123"
        val url = Url(shortUrlId = shortUrlId, longUrl = longUrl)

        every { urlRepository.findByLongUrl(longUrl) } returns Optional.of(url)

        val result = flipShortenerService.shortenUrl(longUrl)

        assertEquals("$baseUrl$shortUrlId", result)
        verify(exactly = 1) { urlRepository.findByLongUrl(longUrl) }
        verify(exactly = 0) { urlRepository.save(any()) }
    }

    @Test
    fun `shortenUrl should generate new short URL when long URL does not exist`() {
        val longUrl = "https://example.com"
        val shortUrlId = "abc123"
        val expectedUrl = Url(shortUrlId = shortUrlId, longUrl = longUrl)

        every { urlRepository.findByLongUrl(longUrl) } returns Optional.empty()
        every { urlRepository.findByShortUrlId(shortUrlId) } returns Optional.empty()

        // Mock the short URL ID generation
        every { flipShortenerService.generateShortUrlId(longUrl) } returns shortUrlId

        every { urlRepository.save(any()) } returns expectedUrl

        val result = flipShortenerService.shortenUrl(longUrl)

        assertEquals("$baseUrl$shortUrlId", result)
        verify(exactly = 1) { urlRepository.findByLongUrl(longUrl) }
        verify(exactly = 1) { urlRepository.save(any()) }
    }

    @Test
    fun `getOriginalUrl should return original URL for a valid short URL`() {
        val shortUrlId = "abc123"
        val longUrl = "https://example.com"
        val url = Url(shortUrlId = shortUrlId, longUrl = longUrl)

        every { urlRepository.findByShortUrlId(shortUrlId) } returns Optional.of(url)

        val result = flipShortenerService.getOriginalUrl(shortUrlId)

        assertTrue(result.isPresent)
        assertEquals(longUrl, result.get().longUrl)
        verify(exactly = 1) { urlRepository.findByShortUrlId(shortUrlId) }
    }

    @Test
    fun `getOriginalUrl should return empty Optional when short URL does not exist`() {
        val shortUrlId = "abc123"

        every { urlRepository.findByShortUrlId(shortUrlId) } returns Optional.empty()

        val result = flipShortenerService.getOriginalUrl(shortUrlId)

        assertTrue(result.isEmpty)
        verify(exactly = 1) { urlRepository.findByShortUrlId(shortUrlId) }
    }
}