package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.records.entities.RecordEntity
import com.ryzingtitan.obdtrakapi.data.records.repositories.RecordRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class RecordRepositoryStepDefs(
    private val recordRepository: RecordRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following records exist:")
    fun givenTheFollowingRecordsExist(existingRecords: List<RecordEntity>) {
        existingRecords.forEach { carEntity ->
            r2dbcEntityTemplate.insert(carEntity).block()
        }
    }

    @Then("the following records will exist:")
    fun thenTheFollowingRecordsWillExist(expectedRecords: List<RecordEntity>) {
        runBlocking {
            val actualRecords = recordRepository.findAll().toList()

            assertEquals(expectedRecords.size, actualRecords.size)

            expectedRecords.forEachIndexed { index, expectedRecord ->
                if (expectedRecord.sessionId != UUID.fromString("00000000-0000-0000-0000-000000000000")) {
                    assertEquals(expectedRecord.sessionId, actualRecords[index].sessionId)
                }

                assertEquals(expectedRecord.timestamp, actualRecords[index].timestamp)
                assertEquals(expectedRecord.longitude, actualRecords[index].longitude)
                assertEquals(expectedRecord.latitude, actualRecords[index].latitude)
                assertEquals(expectedRecord.altitude, actualRecords[index].altitude)
                assertEquals(expectedRecord.intakeAirTemperature, actualRecords[index].intakeAirTemperature)
                assertEquals(expectedRecord.boostPressure, actualRecords[index].boostPressure)
                assertEquals(expectedRecord.coolantTemperature, actualRecords[index].coolantTemperature)
                assertEquals(expectedRecord.engineRpm, actualRecords[index].engineRpm)
                assertEquals(expectedRecord.speed, actualRecords[index].speed)
                assertEquals(expectedRecord.throttlePosition, actualRecords[index].throttlePosition)
                assertEquals(expectedRecord.airFuelRatio, actualRecords[index].airFuelRatio)
                assertEquals(expectedRecord.oilPressure, actualRecords[index].oilPressure)
                assertEquals(expectedRecord.manifoldPressure, actualRecords[index].manifoldPressure)
                assertEquals(expectedRecord.massAirFlow, actualRecords[index].massAirFlow)
            }
        }
    }

    @DataTableType
    fun mapRecordEntity(tableRow: Map<String, String>): RecordEntity =
        RecordEntity(
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            sessionId = UUID.fromString(tableRow["sessionId"]),
            timestamp = Instant.parse(tableRow["timestamp"].orEmpty()),
            longitude = tableRow["longitude"]!!.toDouble(),
            latitude = tableRow["latitude"]!!.toDouble(),
            altitude = tableRow["altitude"]!!.toFloat(),
            intakeAirTemperature = tableRow["intakeAirTemperature"]?.toIntOrNull(),
            boostPressure = tableRow["boostPressure"]?.toFloatOrNull(),
            coolantTemperature = tableRow["coolantTemperature"]?.toIntOrNull(),
            engineRpm = tableRow["engineRpm"]?.toIntOrNull(),
            speed = tableRow["speed"]?.toIntOrNull(),
            throttlePosition = tableRow["throttlePosition"]?.toFloatOrNull(),
            airFuelRatio = tableRow["airFuelRatio"]?.toFloatOrNull(),
            oilPressure = tableRow["oilPressure"]?.toFloatOrNull(),
            manifoldPressure = tableRow["manifoldPressure"]?.toFloatOrNull(),
            massAirFlow = tableRow["massAirFlow"]?.toFloatOrNull(),
        )
}
