package com.ryzingtitan.datalogapi.domain.tracks.services

import com.ryzingtitan.datalogapi.data.tracks.entities.TrackEntity
import com.ryzingtitan.datalogapi.data.tracks.repositories.TrackRepository
import com.ryzingtitan.datalogapi.domain.tracks.dtos.Track
import com.ryzingtitan.datalogapi.domain.tracks.exceptions.TrackAlreadyExistsException
import com.ryzingtitan.datalogapi.domain.tracks.exceptions.TrackDoesNotExistException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TrackService(
    private val trackRepository: TrackRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(TrackService::class.java)

    suspend fun create(track: Track): Int? {
        val existingTrack = trackRepository.findByName(track.name)

        if (existingTrack != null) {
            val message = "A track already exists named ${track.name}"
            logger.error(message)
            throw TrackAlreadyExistsException(message)
        }

        val trackId =
            trackRepository
                .save(
                    TrackEntity(
                        name = track.name,
                        longitude = track.longitude,
                        latitude = track.latitude,
                    ),
                ).id

        logger.info("Created track named ${track.name}")

        return trackId
    }

    suspend fun update(track: Track) {
        val existingTrack = trackRepository.findById(track.id!!)

        if (existingTrack == null) {
            val message = "A track named ${track.name} does not exist"
            logger.error(message)
            throw TrackDoesNotExistException(message)
        }

        trackRepository.save(
            TrackEntity(
                id = track.id,
                name = track.name,
                longitude = track.longitude,
                latitude = track.latitude,
            ),
        )

        logger.info("Updated track named ${track.name}")
    }

    fun getAll(): Flow<Track> {
        logger.info("Retrieving all tracks")

        return trackRepository.findAll().map { trackEntity ->
            Track(
                id = trackEntity.id,
                name = trackEntity.name,
                longitude = trackEntity.longitude,
                latitude = trackEntity.latitude,
            )
        }
    }

    suspend fun delete(trackId: Int) {
        trackRepository.deleteById(trackId)
        logger.info("Deleted track with id $trackId")
    }
}
