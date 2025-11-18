package com.ryzingtitan.obdtrakapi.data.tracks.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("tracks")
data class TrackEntity(
    @Id
    val id: UUID? = null,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)
