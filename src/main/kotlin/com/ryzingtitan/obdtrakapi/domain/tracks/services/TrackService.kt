package com.ryzingtitan.obdtrakapi.domain.tracks.services

import com.ryzingtitan.obdtrakapi.data.tracks.entities.TrackEntity
import com.ryzingtitan.obdtrakapi.data.tracks.repositories.TrackRepository
import com.ryzingtitan.obdtrakapi.domain.tracks.dtos.Track
import com.ryzingtitan.obdtrakapi.domain.tracks.dtos.TrackRequest
import com.ryzingtitan.obdtrakapi.domain.tracks.exceptions.TrackAlreadyExistsException
import com.ryzingtitan.obdtrakapi.domain.tracks.exceptions.TrackDoesNotExistException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TrackService(
    private val trackRepository: TrackRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(TrackService::class.java)

    suspend fun create(trackRequest: TrackRequest): Track {
        val existingTrack = trackRepository.findByName(trackRequest.name)

        if (existingTrack != null) {
            val message = "A track already exists named ${trackRequest.name}"
            logger.error(message)
            throw TrackAlreadyExistsException(message)
        }

        val trackEntity =
            trackRepository
                .save(
                    TrackEntity(
                        name = trackRequest.name,
                        longitude = trackRequest.longitude,
                        latitude = trackRequest.latitude,
                    ),
                )

        logger.info("Created track named ${trackRequest.name}")

        return Track(
            id = trackEntity.id!!,
            name = trackEntity.name,
            latitude = trackEntity.latitude,
            longitude = trackEntity.longitude,
        )
    }

    suspend fun update(
        trackId: UUID,
        trackRequest: TrackRequest,
    ): Track {
        val existingTrack = trackRepository.findById(trackId)

        if (existingTrack == null) {
            val message = "A track named ${trackRequest.name} does not exist"
            logger.error(message)
            throw TrackDoesNotExistException(message)
        }

        val updatedTrackEntity =
            trackRepository.save(
                TrackEntity(
                    id = trackId,
                    name = trackRequest.name,
                    longitude = trackRequest.longitude,
                    latitude = trackRequest.latitude,
                ),
            )

        logger.info("Updated track named ${trackRequest.name}")

        return Track(
            id = updatedTrackEntity.id!!,
            name = updatedTrackEntity.name,
            latitude = updatedTrackEntity.latitude,
            longitude = updatedTrackEntity.longitude,
        )
    }

    fun getAll(): Flow<Track> {
        logger.info("Retrieving all tracks")

        return trackRepository.findAll().map { trackEntity ->
            Track(
                id = trackEntity.id!!,
                name = trackEntity.name,
                longitude = trackEntity.longitude,
                latitude = trackEntity.latitude,
            )
        }
    }

    suspend fun delete(trackId: UUID) {
        trackRepository.deleteById(trackId)
        logger.info("Deleted track with id $trackId")
    }
}
