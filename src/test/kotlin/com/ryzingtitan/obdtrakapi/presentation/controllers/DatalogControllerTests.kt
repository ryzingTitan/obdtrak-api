package com.ryzingtitan.obdtrakapi.presentation.controllers

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.obdtrakapi.domain.datalogs.dtos.Datalog
import com.ryzingtitan.obdtrakapi.domain.datalogs.services.DatalogService
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

class DatalogControllerTests {
    @Nested
    inner class GetDatalogsBySessionId {
        @Test
        fun `returns 'OK' status with session data that matches the request parameter`() =
            runTest {
                whenever(mockDatalogService.getAllBySessionId(SESSION_ID))
                    .thenReturn(flowOf(firstDatalog, secondDatalog))

                webTestClient
                    .get()
                    .uri("/api/sessions/$SESSION_ID/datalogs")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Datalog>()
                    .contains(firstDatalog, secondDatalog)

                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving datalogs for session id: $SESSION_ID", appender.list[0].message)

                verify(mockDatalogService, times(1)).getAllBySessionId(SESSION_ID)
            }
    }

    @BeforeEach
    fun setup() {
        val datalogController = DatalogController(mockDatalogService)
        webTestClient = WebTestClient.bindToController(datalogController).build()

        logger = LoggerFactory.getLogger(DatalogController::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var webTestClient: WebTestClient
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockDatalogService = mock<DatalogService>()

    private val firstDatalog =
        Datalog(
            sessionId = SESSION_ID,
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
        )

    private val secondDatalog =
        Datalog(
            sessionId = SESSION_ID,
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
        )

    companion object DatalogControllerTestConstants {
        private const val SESSION_ID = 5
    }
}
