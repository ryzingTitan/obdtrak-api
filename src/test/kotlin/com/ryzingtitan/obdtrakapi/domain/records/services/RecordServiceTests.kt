package com.ryzingtitan.obdtrakapi.domain.records.services

import com.ryzingtitan.obdtrakapi.data.records.entities.RecordEntity
import com.ryzingtitan.obdtrakapi.data.records.repositories.RecordRepository
import com.ryzingtitan.obdtrakapi.domain.records.dtos.Record
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class RecordServiceTests {
    @Nested
    inner class GetAllBySessionId {
        @Test
        fun `returns all records with the session id that is provided`() =
            runTest {
                whenever(mockRecordRepository.findAllBySessionIdOrderByTimestampAsc(sessionId))
                    .thenReturn(flowOf(firstRecordEntity, secondRecordEntity))

                val records = recordService.getAllBySessionId(sessionId)

                assertEquals(listOf(firstExpectedRecord, secondExpectedRecord), records.toList())
            }
    }

    @BeforeEach
    fun setup() {
        recordService = RecordService(mockRecordRepository)
        reset(mockRecordRepository)
    }

    private lateinit var recordService: RecordService

    private val mockRecordRepository = mock<RecordRepository>()

    private val timestamp = Instant.now()
    private val sessionId = UUID.randomUUID()

    private val firstRecordEntity =
        RecordEntity(
            sessionId = sessionId,
            timestamp = timestamp,
            longitude = -86.14162,
            latitude = 42.406800000000004,
            altitude = 188.4f,
            intakeAirTemperature = 135,
            boostPressure = 15.6f,
            coolantTemperature = 150,
            engineRpm = 5000,
            speed = 85,
            throttlePosition = 75.6f,
            airFuelRatio = 15.8f,
        )

    private val secondRecordEntity =
        RecordEntity(
            sessionId = sessionId,
            timestamp = timestamp,
            longitude = 86.14162,
            latitude = -42.406800000000004,
            altitude = 188.0f,
            intakeAirTemperature = null,
            boostPressure = null,
            coolantTemperature = null,
            engineRpm = null,
            speed = null,
            throttlePosition = null,
            airFuelRatio = null,
        )

    private val firstExpectedRecord =
        Record(
            sessionId = sessionId,
            timestamp = timestamp.truncatedTo(ChronoUnit.MILLIS),
            longitude = -86.14162,
            latitude = 42.406800000000004,
            altitude = 188.4f,
            intakeAirTemperature = 135,
            boostPressure = 15.6f,
            coolantTemperature = 150,
            engineRpm = 5000,
            speed = 85,
            throttlePosition = 75.6f,
            airFuelRatio = 15.8f,
        )

    private val secondExpectedRecord =
        Record(
            sessionId = sessionId,
            timestamp = timestamp.truncatedTo(ChronoUnit.MILLIS),
            longitude = 86.14162,
            latitude = -42.406800000000004,
            altitude = 188.0f,
            intakeAirTemperature = null,
            boostPressure = null,
            coolantTemperature = null,
            engineRpm = null,
            speed = null,
            throttlePosition = null,
            airFuelRatio = null,
        )
}
