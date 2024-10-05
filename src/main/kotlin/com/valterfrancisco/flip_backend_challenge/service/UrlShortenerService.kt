package com.valterfrancisco.flip_backend_challenge.service

import com.valterfrancisco.flip_backend_challenge.model.Url
import com.valterfrancisco.flip_backend_challenge.repository.UrlRepository
import com.valterfrancisco.flip_backend_challenge.util.hash
import org.springframework.stereotype.Service
import java.util.*

@Service
class FlipShortenerService(private val urlRepository: UrlRepository) {

    private val baseUrl = "http://localhost:8080/api/v1/shortener/"

    fun shortenUrl(longUrl: String): String {
        val existingUrl = urlRepository.findByLongUrl(longUrl)
        if (existingUrl.isPresent) {
            return "$baseUrl${existingUrl.get().shortUrlId}"
        }

        val shortUrlId = generateShortUrlId(longUrl)
        val url = Url(shortUrlId = shortUrlId, longUrl = longUrl)
        urlRepository.save(url)

        return "$baseUrl$shortUrlId"
    }

    fun getOriginalUrl(shortUrlId: String): Optional<Url> {
        return urlRepository.findByShortUrlId(shortUrlId)
    }

    private fun generateShortUrlId(longUrl: String): String {
        // Generate a short URL ID and check for collisions
        var shortUrlId: String
        do {
            shortUrlId = longUrl.hash()
        } while (urlRepository.findByShortUrlId(shortUrlId).isPresent)
        return shortUrlId
    }
}