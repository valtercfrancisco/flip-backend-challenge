package com.valterfrancisco.flip_backend_challenge.util

import java.net.URI
import java.net.URISyntaxException

fun isValidUrl(url: String): Boolean {
    return try {
        val uri = URI(url)
        if (uri.scheme != null && (uri.scheme == "http" || uri.scheme == "https")) {
            // Additional check for malformed URL criteria
            val host = uri.host
            return !(host == null || host.isEmpty() || host.contains("..") || host.startsWith(".") || host.endsWith("."))
        }
        false
    } catch (e: URISyntaxException) {
        false
    }
}