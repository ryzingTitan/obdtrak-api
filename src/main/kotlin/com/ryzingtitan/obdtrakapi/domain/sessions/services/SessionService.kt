package com.ryzingtitan.obdtrakapi.domain.sessions.services

import com.ryzingtitan.obdtrakapi.data.cars.repositories.CarRepository
import com.ryzingtitan.obdtrakapi.data.datalogs.repositories.DatalogRepository
import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import com.ryzingtitan.obdtrakapi.data.sessions.repositories.SessionRepository
import com.ryzingtitan.obdtrakapi.data.tracks.repositories.TrackRepository
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.Session
import com.ryzingtitan.obdtrakapi.domain.sessions.exceptions.SessionAlreadyExistsException
import com.ryzingtitan.obdtrakapi.domain.sessions.exceptions.SessionDoesNotExistException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.collections.map

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    private val trackRepository: TrackRepository,
    private val carRepository: CarRepository,
    private val fileParsingService: FileParsingService,
    private val datalogRepository: DatalogRepository,
) {
    suspend fun getAllByUser(userEmail: String): Flow<Session> =
        sessionRepository.findAllByUserEmail(userEmail).map { sessionEntity ->
            val trackEntity = trackRepository.findById(sessionEntity.trackId)!!
            val carEntity = carRepository.findById(sessionEntity.carId)!!

            Session(
                id = sessionEntity.id!!,
                startTime = sessionEntity.startTime,
                endTime = sessionEntity.endTime,
                trackName = trackEntity.name,
                trackLatitude = trackEntity.latitude,
                trackLongitude = trackEntity.longitude,
                carYear = carEntity.yearManufactured,
                carMake = carEntity.make,
                carModel = carEntity.model,
            )
        }

    suspend fun create(fileUpload: FileUpload): UUID {
        val datalogs = fileParsingService.parse(fileUpload)

        val firstDatalogTimestamp = datalogs.minBy { it.timestamp }.timestamp
        val lastDatalogTimestamp = datalogs.maxBy { it.timestamp }.timestamp
        val existingSession =
            sessionRepository.findByUserEmailAndStartTimeAndEndTime(
                fileUpload.metadata.userEmail,
                firstDatalogTimestamp,
                lastDatalogTimestamp,
            )

        if (existingSession != null) {
            val message =
                "A session already exists for user ${fileUpload.metadata.userEmail} " +
                    "and timestamp $firstDatalogTimestamp - $lastDatalogTimestamp"
            logger.error(message)
            throw SessionAlreadyExistsException(message)
        }

        val sessionId =
            sessionRepository
                .save(
                    SessionEntity(
                        userEmail = fileUpload.metadata.userEmail,
                        userFirstName = fileUpload.metadata.userFirstName,
                        userLastName = fileUpload.metadata.userLastName,
                        startTime = firstDatalogTimestamp,
                        endTime = lastDatalogTimestamp,
                        trackId = fileUpload.metadata.trackId,
                        carId = fileUpload.metadata.carId,
                    ),
                ).id!!

        datalogRepository.saveAll(datalogs.map { it.copy(sessionId = sessionId) }).collect()

        logger.info(
            "Session created for user ${fileUpload.metadata.userEmail} " +
                "and timestamp $firstDatalogTimestamp - $lastDatalogTimestamp",
        )
        return sessionId
    }

    suspend fun update(
        fileUpload: FileUpload,
        sessionId: UUID,
    ) {
        val existingSession = sessionRepository.findById(sessionId)

        if (existingSession == null) {
            val message = "Session id $sessionId does not exist"
            logger.error(message)
            throw SessionDoesNotExistException(message)
        }

        datalogRepository.deleteAllBySessionId(sessionId).collect()

        val newDatalogs = fileParsingService.parse(fileUpload)

        datalogRepository.saveAll(newDatalogs.map { it.copy(sessionId = sessionId) }).collect()
        sessionRepository.save(
            existingSession.copy(
                carId = fileUpload.metadata.carId,
                trackId = fileUpload.metadata.trackId,
            ),
        )
        logger.info("Session $sessionId updated")
    }

    private val logger: Logger = LoggerFactory.getLogger(SessionService::class.java)
}
