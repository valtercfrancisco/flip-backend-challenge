package com.valterfrancisco.flip_backend_challenge.service

import com.valterfrancisco.flip_backend_challenge.model.Url
import com.valterfrancisco.flip_backend_challenge.repository.UrlRepository
import com.valterfrancisco.flip_backend_challenge.util.hash
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class FlipShortenerService(private val urlRepository: UrlRepository) {

    private val baseUrl = "http://localhost:8080/api/v1/shortener/"

    fun shortenUrl(longUrl: String): String {
        val existingUrl = urlRepository.findByLongUrl(longUrl)
        // Check and resolve collisions
        if (existingUrl.isPresent) {
            return "$baseUrl${existingUrl.get().shortUrlId}"
        }

        val shortUrlId = generateShortUrlId(longUrl)
        val url = Url(shortUrlId = shortUrlId, longUrl = longUrl)
        urlRepository.save(url)

        return "$baseUrl$shortUrlId"
    }

    @Transactional
    fun deleteUrl(shortUrlId: String): Boolean {
        if (urlRepository.existsByShortUrlId(shortUrlId)) {
            val deletedCount = urlRepository.deleteByShortUrlId(shortUrlId)
            return deletedCount > 0
        } else {
            println("URL with shortUrlId: $shortUrlId does not exist.")
            return false
        }
    }

    fun getOriginalUrl(shortUrlId: String): Optional<Url> {
        return urlRepository.findByShortUrlId(shortUrlId)
    }

    fun generateShortUrlId(longUrl: String): String {
        // Generate a short URL ID and check for collisions
        var shortUrlId: String
        do {
            shortUrlId = longUrl.hash()
        } while (urlRepository.findByShortUrlId(shortUrlId).isPresent)
        return shortUrlId
    }
}