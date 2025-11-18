package com.ryzingtitan.obdtrakapi.domain.datalogs.services

import com.ryzingtitan.obdtrakapi.data.datalogs.repositories.DatalogRepository
import com.ryzingtitan.obdtrakapi.domain.datalogs.dtos.Datalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

@Service
class DatalogService(
    private val datalogRepository: DatalogRepository,
) {
    suspend fun getAllBySessionId(sessionId: Int): Flow<Datalog> =
        datalogRepository
            .findAllBySessionIdOrderByTimestampAsc(sessionId)
            .map { datalogEntity ->
                Datalog(
                    datalogEntity.sessionId!!,
                    datalogEntity.timestamp.truncatedTo(ChronoUnit.MILLIS),
                    datalogEntity.longitude,
                    datalogEntity.latitude,
                    datalogEntity.altitude,
                    datalogEntity.intakeAirTemperature,
                    datalogEntity.boostPressure,
                    datalogEntity.coolantTemperature,
                    datalogEntity.engineRpm,
                    datalogEntity.speed,
                    datalogEntity.throttlePosition,
                    datalogEntity.airFuelRatio,
                )
            }
}
