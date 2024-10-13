package com.valterfrancisco.flip_backend_challenge.controller

import com.valterfrancisco.flip_backend_challenge.service.FlipShortenerService
import com.valterfrancisco.flip_backend_challenge.util.isValidUrl
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/shortener")
class FlipShortenerController(private val urlShortenerService: FlipShortenerService) {

    @PostMapping("/shorten")
    fun shortenUrl(@RequestParam longUrl: String): ResponseEntity<String> {
        return try {
            if (!isValidUrl(longUrl)) {
                return ResponseEntity.badRequest().body("Invalid URL format.")
            }
            val shortUrl = urlShortenerService.shortenUrl(longUrl)
            ResponseEntity.ok(shortUrl)
        } catch (e: Exception) {
            ResponseEntity.status(INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.")
        }
    }

    @GetMapping("/{shortUrlId}")
    fun getOriginalUrl(@PathVariable shortUrlId: String): ResponseEntity<Void> {
        return try {
            val url = urlShortenerService.getOriginalUrl(shortUrlId)
            if (url.isPresent) {
                ResponseEntity.status(302).location(URI.create(url.get().longUrl)).build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(INTERNAL_SERVER_ERROR).build()
        }
    }
}