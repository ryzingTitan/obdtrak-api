package com.ryzingtitan.obdtrakapi.domain.records.services

import com.ryzingtitan.obdtrakapi.data.records.repositories.RecordRepository
import com.ryzingtitan.obdtrakapi.domain.records.dtos.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class RecordService(
    private val recordRepository: RecordRepository,
) {
    suspend fun getAllBySessionId(sessionId: UUID): Flow<Record> =
        recordRepository
            .findAllBySessionIdOrderByTimestampAsc(sessionId)
            .map { recordEntity ->
                Record(
                    recordEntity.sessionId!!,
                    recordEntity.timestamp.truncatedTo(ChronoUnit.MILLIS),
                    recordEntity.longitude,
                    recordEntity.latitude,
                    recordEntity.altitude,
                    recordEntity.intakeAirTemperature,
                    recordEntity.boostPressure,
                    recordEntity.coolantTemperature,
                    recordEntity.engineRpm,
                    recordEntity.speed,
                    recordEntity.throttlePosition,
                    recordEntity.airFuelRatio,
                )
            }
}
