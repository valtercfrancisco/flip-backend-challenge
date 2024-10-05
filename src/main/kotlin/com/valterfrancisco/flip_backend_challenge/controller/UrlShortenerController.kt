package com.valterfrancisco.flip_backend_challenge.controller

import com.valterfrancisco.flip_backend_challenge.service.FlipShortenerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/shortener")
class FlipShortenerController(private val urlShortenerService: FlipShortenerService) {

    @PostMapping("/shorten")
    fun shortenUrl(@RequestParam longUrl: String): ResponseEntity<String> {
        val shortUrl = urlShortenerService.shortenUrl(longUrl)
        return ResponseEntity.ok(shortUrl)
    }

    @GetMapping("/{shortUrlId}")
    fun getOriginalUrl(@PathVariable shortUrlId: String): ResponseEntity<Void> {
        val url = urlShortenerService.getOriginalUrl(shortUrlId)
        return if (url.isPresent) {
            ResponseEntity.status(302).location(URI.create(url.get().longUrl)).build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}