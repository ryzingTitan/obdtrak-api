package com.ryzingtitan.obdtrakapi.domain.sessions.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.obdtrakapi.data.cars.entities.CarEntity
import com.ryzingtitan.obdtrakapi.data.cars.repositories.CarRepository
import com.ryzingtitan.obdtrakapi.data.records.entities.RecordEntity
import com.ryzingtitan.obdtrakapi.data.records.repositories.RecordRepository
import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import com.ryzingtitan.obdtrakapi.data.sessions.repositories.SessionRepository
import com.ryzingtitan.obdtrakapi.data.tracks.entities.TrackEntity
import com.ryzingtitan.obdtrakapi.data.tracks.repositories.TrackRepository
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUploadMetadata
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.Session
import com.ryzingtitan.obdtrakapi.domain.sessions.exceptions.SessionAlreadyExistsException
import com.ryzingtitan.obdtrakapi.domain.sessions.exceptions.SessionDoesNotExistException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import java.time.Instant
import java.util.UUID

class SessionServiceTests {
    @Nested
    inner class GetAllByUser {
        @Test
        fun `returns all sessions for user`() =
            runTest {
                whenever(mockSessionRepository.findAllByUserEmail(USER_EMAIL))
                    .thenReturn(flowOf(firstSessionEntity, secondSessionEntity))
                whenever(mockTrackRepository.findById(trackId)).thenReturn(trackEntity)
                whenever(mockCarRepository.findById(carId)).thenReturn(carEntity)

                val sessions = sessionService.getAllByUser(USER_EMAIL)

                assertEquals(listOf(firstSession, secondSession), sessions.toList())
            }
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a new session`() =
            runTest {
                whenever(mockFileParsingService.parse(any<FileUpload>())).thenReturn(listOf(recordEntity))
                whenever(
                    mockSessionRepository.findByUserEmailAndStartTimeAndEndTime(
                        USER_EMAIL,
                        recordEntity.timestamp,
                        recordEntity.timestamp,
                    ),
                ).thenReturn(null)
                whenever(mockSessionRepository.save(firstSessionEntity.copy(id = null))).thenReturn(firstSessionEntity)

                val updatedRecordEntity = recordEntity.copy(sessionId = firstSessionEntity.id)
                whenever(mockRecordRepository.saveAll(listOf(updatedRecordEntity)))
                    .thenReturn(flowOf(updatedRecordEntity))

                val sessionId = sessionService.create(FileUpload(flowOf(dataBuffer), fileUploadMetadata))

                assertEquals(firstSessionEntity.id, sessionId)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Session created for user $USER_EMAIL and timestamp $timestamp - $timestamp",
                    appender.list[0].message,
                )

                verify(mockFileParsingService, times(1)).parse(any<FileUpload>())
                verify(mockSessionRepository, times(1)).findByUserEmailAndStartTimeAndEndTime(
                    USER_EMAIL,
                    recordEntity.timestamp,
                    recordEntity.timestamp,
                )
                verify(mockSessionRepository, times(1)).save(firstSessionEntity.copy(id = null))
                verify(mockRecordRepository, times(1)).saveAll(listOf(recordEntity.copy(sessionId = sessionId)))
            }

        @Test
        fun `does not create duplicate sessions for a user`() =
            runTest {
                whenever(mockFileParsingService.parse(any<FileUpload>())).thenReturn(listOf(recordEntity))
                whenever(
                    mockSessionRepository.findByUserEmailAndStartTimeAndEndTime(
                        USER_EMAIL,
                        recordEntity.timestamp,
                        recordEntity.timestamp,
                    ),
                ).thenReturn(firstSessionEntity)

                val exception =
                    assertThrows<SessionAlreadyExistsException> {
                        sessionService.create(FileUpload(flowOf(dataBuffer), fileUploadMetadata))
                    }

                assertEquals(
                    "A session already exists for user $USER_EMAIL and timestamp $timestamp - $timestamp",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "A session already exists for user $USER_EMAIL and timestamp $timestamp - $timestamp",
                    appender.list[0].message,
                )

                verify(mockFileParsingService, times(1)).parse(any<FileUpload>())
                verify(mockSessionRepository, times(1)).findByUserEmailAndStartTimeAndEndTime(
                    USER_EMAIL,
                    recordEntity.timestamp,
                    recordEntity.timestamp,
                )
                verify(mockSessionRepository, never()).save(any())
                verify(mockRecordRepository, never()).saveAll(any<List<RecordEntity>>())
            }
    }

    @Nested
    inner class Update {
        @Test
        fun `updates an existing session`() =
            runTest {
                val currentSessionId = UUID.randomUUID()
                val trackId = UUID.randomUUID()
                val carId = UUID.randomUUID()

                whenever(mockSessionRepository.findById(currentSessionId))
                    .thenReturn(firstSessionEntity)
                whenever(mockRecordRepository.deleteAllBySessionId(currentSessionId))
                    .thenReturn(flowOf(recordEntity.copy(sessionId = currentSessionId)))
                whenever(mockFileParsingService.parse(any<FileUpload>()))
                    .thenReturn(listOf(recordEntity.copy(sessionId = currentSessionId)))
                whenever(mockRecordRepository.saveAll(listOf(recordEntity.copy(sessionId = currentSessionId))))
                    .thenReturn(flowOf(recordEntity.copy(sessionId = currentSessionId)))

                sessionService.update(
                    FileUpload(
                        flowOf(dataBuffer),
                        fileUploadMetadata.copy(trackId = trackId, carId = carId),
                    ),
                    currentSessionId,
                )

                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Session $currentSessionId updated", appender.list[0].message)

                verify(mockSessionRepository, times(1)).findById(currentSessionId)
                verify(mockRecordRepository, times(1)).deleteAllBySessionId(currentSessionId)
                verify(mockFileParsingService, times(1)).parse(any<FileUpload>())
                verify(mockRecordRepository, times(1))
                    .saveAll(listOf(recordEntity.copy(sessionId = currentSessionId)))
                verify(mockSessionRepository, times(1)).save(firstSessionEntity.copy(trackId = trackId, carId = carId))
            }

        @Test
        fun `does not update a session that does not exist`() =
            runTest {
                val currentSessionId = UUID.randomUUID()

                whenever(mockSessionRepository.findById(currentSessionId)).thenReturn(null)

                val exception =
                    assertThrows<SessionDoesNotExistException> {
                        sessionService
                            .update(
                                FileUpload(
                                    flowOf(dataBuffer),
                                    fileUploadMetadata,
                                ),
                                currentSessionId,
                            )
                    }

                assertEquals("Session id $currentSessionId does not exist", exception.message)
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals("Session id $currentSessionId does not exist", appender.list[0].message)

                verify(mockSessionRepository, times(1)).findById(currentSessionId)
                verify(mockRecordRepository, never()).deleteAllBySessionId(any())
                verify(mockFileParsingService, never()).parse(any())
                verify(mockRecordRepository, never()).saveAll(any<List<RecordEntity>>())
                verify(mockSessionRepository, never()).save(any())
            }
    }

    @BeforeEach
    fun setup() {
        sessionService =
            SessionService(
                mockSessionRepository,
                mockTrackRepository,
                mockCarRepository,
                mockFileParsingService,
                mockRecordRepository,
            )

        logger = LoggerFactory.getLogger(SessionService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var sessionService: SessionService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockTrackRepository = mock<TrackRepository>()
    private val mockCarRepository = mock<CarRepository>()
    private val mockSessionRepository = mock<SessionRepository>()
    private val mockFileParsingService = mock<FileParsingService>()
    private val mockRecordRepository = mock<RecordRepository>()

    private val timestamp = Instant.now()
    private val dataBufferFactory = DefaultDataBufferFactory()
    private val dataBuffer = dataBufferFactory.wrap("header row 1\ndata row 1\n".toByteArray())
    private val trackId = UUID.randomUUID()
    private val carId = UUID.randomUUID()

    private val firstSessionEntity =
        SessionEntity(
            id = UUID.randomUUID(),
            userEmail = USER_EMAIL,
            userFirstName = USER_FIRST_NAME,
            userLastName = USER_LAST_NAME,
            startTime = timestamp,
            endTime = timestamp,
            trackId = trackId,
            carId = carId,
        )

    private val secondSessionEntity =
        SessionEntity(
            id = UUID.randomUUID(),
            userEmail = USER_EMAIL,
            userFirstName = USER_FIRST_NAME,
            userLastName = USER_LAST_NAME,
            startTime = timestamp,
            endTime = timestamp,
            trackId = trackId,
            carId = carId,
        )

    private val trackEntity =
        TrackEntity(
            id = trackId,
            name = TRACK_NAME,
            latitude = TRACK_LATITUDE,
            longitude = TRACK_LONGITUDE,
        )

    private val carEntity =
        CarEntity(
            id = carId,
            yearManufactured = CAR_YEAR,
            make = CAR_MAKE,
            model = CAR_MODEL,
        )

    private val firstSession =
        Session(
            id = firstSessionEntity.id!!,
            startTime = firstSessionEntity.startTime,
            endTime = firstSessionEntity.endTime,
            trackName = TRACK_NAME,
            trackLatitude = TRACK_LATITUDE,
            trackLongitude = TRACK_LONGITUDE,
            carYear = CAR_YEAR,
            carMake = CAR_MAKE,
            carModel = CAR_MODEL,
        )

    private val secondSession =
        Session(
            id = secondSessionEntity.id!!,
            startTime = secondSessionEntity.startTime,
            endTime = secondSessionEntity.endTime,
            trackName = TRACK_NAME,
            trackLatitude = TRACK_LATITUDE,
            trackLongitude = TRACK_LONGITUDE,
            carYear = CAR_YEAR,
            carMake = CAR_MAKE,
            carModel = CAR_MODEL,
        )

    private val fileUploadMetadata =
        FileUploadMetadata(
            fileName = "testFile.txt",
            trackId = trackId,
            carId = carId,
            userEmail = USER_EMAIL,
            userFirstName = USER_FIRST_NAME,
            userLastName = USER_LAST_NAME,
        )

    private val recordEntity =
        RecordEntity(
            sessionId = UUID.randomUUID(),
            timestamp = timestamp,
            longitude = -86.14162,
            latitude = 42.406800000000004,
            altitude = 188.4f,
            intakeAirTemperature = 138,
            boostPressure = 16.5f,
            coolantTemperature = 155,
            engineRpm = 3500,
            speed = 79,
            throttlePosition = 83.2f,
            airFuelRatio = 17.5f,
        )

    companion object SessionServiceTestConstants {
        const val USER_EMAIL = "test@test.com"
        const val USER_FIRST_NAME = "test"
        const val USER_LAST_NAME = "tester"
        const val TRACK_NAME = "Test Track"
        const val TRACK_LATITUDE = 12.0
        const val TRACK_LONGITUDE = 14.0
        const val CAR_YEAR = 2001
        const val CAR_MAKE = "Volkswagen"
        const val CAR_MODEL = "Jetta"
    }
}
