package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.datalogs.entities.DatalogEntity
import com.ryzingtitan.obdtrakapi.data.datalogs.repositories.DatalogRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class DatalogRepositoryStepDefs(
    private val datalogRepository: DatalogRepository,
) {
    @Given("the following datalogs exist:")
    fun givenTheFollowingDatalogsExist(table: DataTable) {
        val datalogEntities = table.asList(DatalogEntity::class.java)

        datalogEntities.forEach { datalog ->
            runBlocking {
                datalogRepository.save(datalog)
            }
        }
    }

    @Then("the following datalogs will exist:")
    fun thenTheFollowingDatalogsWillExist(expectedDatalogs: List<DatalogEntity>) {
        runBlocking {
            val actualDatalogs = datalogRepository.findAll().toList()

            assertEquals(expectedDatalogs.size, actualDatalogs.size)

            expectedDatalogs.forEachIndexed { index, expectedDatalog ->
                if (expectedDatalog.sessionId != UUID.fromString("00000000-0000-0000-0000-000000000000")) {
                    assertEquals(expectedDatalog.sessionId, actualDatalogs[index].sessionId)
                }

                assertEquals(expectedDatalog.timestamp, actualDatalogs[index].timestamp)
                assertEquals(expectedDatalog.longitude, actualDatalogs[index].longitude)
                assertEquals(expectedDatalog.latitude, actualDatalogs[index].latitude)
                assertEquals(expectedDatalog.altitude, actualDatalogs[index].altitude)
                assertEquals(expectedDatalog.intakeAirTemperature, actualDatalogs[index].intakeAirTemperature)
                assertEquals(expectedDatalog.boostPressure, actualDatalogs[index].boostPressure)
                assertEquals(expectedDatalog.coolantTemperature, actualDatalogs[index].coolantTemperature)
                assertEquals(expectedDatalog.engineRpm, actualDatalogs[index].engineRpm)
                assertEquals(expectedDatalog.speed, actualDatalogs[index].speed)
                assertEquals(expectedDatalog.throttlePosition, actualDatalogs[index].throttlePosition)
                assertEquals(expectedDatalog.airFuelRatio, actualDatalogs[index].airFuelRatio)
            }
        }
    }

    @DataTableType
    fun mapDatalogEntity(tableRow: Map<String, String>): DatalogEntity =
        DatalogEntity(
            id = tableRow["id"]?.toLong(),
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
        )
}
