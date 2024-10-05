package com.valterfrancisco.flip_backend_challenge.model

import jakarta.persistence.*
import jakarta.persistence.GenerationType.*

@Entity
@Table(name = "urls")
data class Url(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val shortUrlId: String,

    @Column(nullable = false)
    val longUrl: String
)