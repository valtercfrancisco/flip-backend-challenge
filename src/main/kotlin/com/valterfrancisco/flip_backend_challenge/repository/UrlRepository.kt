package com.valterfrancisco.flip_backend_challenge.repository

import com.valterfrancisco.flip_backend_challenge.model.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UrlRepository : JpaRepository<Url, Long> {
    fun findByShortUrlId(shortUrlId: String): Optional<Url>
    fun findByLongUrl(longUrl: String): Optional<Url>
}