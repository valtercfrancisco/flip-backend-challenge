package com.valterfrancisco.flip_backend_challenge.util

import java.security.MessageDigest

fun String.hash(truncate: Int = 6): String {
    // Hash the string using MD5
    val hashBytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(Charsets.UTF_8))
    val hashString = StringBuilder().apply {
        hashBytes.forEach { byte -> append(String.format("%02x", byte)) }
    }.toString()
    return hashString.take(truncate)
}