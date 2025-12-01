package com.ryzingtitan.obdtrakapi.presentation.controllers

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.obdtrakapi.domain.records.dtos.Record
import com.ryzingtitan.obdtrakapi.domain.records.services.RecordService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.time.Instant
import java.util.UUID

class RecordControllerTests {
    @Nested
    inner class GetRecordsBySessionId {
        @Test
        fun `returns 'OK' status with session data that matches the request parameter`() =
            runTest {
                whenever(mockRecordService.getAllBySessionId(sessionId))
                    .thenReturn(flowOf(firstRecord, secondRecord))

                webTestClient
                    .get()
                    .uri("/api/sessions/$sessionId/records")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Record>()
                    .contains(firstRecord, secondRecord)

                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving records for session id: $sessionId", appender.list[0].message)

                verify(mockRecordService, times(1)).getAllBySessionId(sessionId)
            }
    }

    @BeforeEach
    fun setup() {
        val recordController = RecordController(mockRecordService)
        webTestClient = WebTestClient.bindToController(recordController).build()

        logger = LoggerFactory.getLogger(RecordController::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var webTestClient: WebTestClient
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockRecordService = mock<RecordService>()

    private val sessionId = UUID.randomUUID()

    private val firstRecord =
        Record(
            sessionId = sessionId,
            timestamp = Instant.now(),
            longitude = -86.14162,
            latitude = 42.406800000000004,
            altitude = 188.4f,
            intakeAirTemperature = 130,
            boostPressure = 15.6f,
            coolantTemperature = 150,
            engineRpm = 5000,
            speed = 85,
            throttlePosition = 75.6f,
            airFuelRatio = 14.7f,
            oilPressure = 45.0f,
            manifoldPressure = 6.5f,
            massAirFlow = 38.0f,
        )

    private val secondRecord =
        Record(
            sessionId = sessionId,
            timestamp = Instant.now(),
            longitude = 86.14162,
            latitude = -42.406800000000004,
            altitude = 188.0f,
            intakeAirTemperature = 135,
            boostPressure = 15.0f,
            coolantTemperature = 165,
            engineRpm = 5500,
            speed = 80,
            throttlePosition = 75.0f,
            airFuelRatio = 15.9f,
            oilPressure = 46.0f,
            manifoldPressure = 7.0f,
            massAirFlow = 42.0f,
        )
}
