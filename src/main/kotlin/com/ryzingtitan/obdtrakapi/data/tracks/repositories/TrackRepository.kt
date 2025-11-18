package com.ryzingtitan.obdtrakapi.data.tracks.repositories

import com.ryzingtitan.obdtrakapi.data.tracks.entities.TrackEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface TrackRepository : CoroutineCrudRepository<TrackEntity, UUID> {
    suspend fun findByName(name: String): TrackEntity?
}
