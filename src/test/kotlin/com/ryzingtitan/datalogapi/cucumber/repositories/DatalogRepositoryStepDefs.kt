package com.ryzingtitan.datalogapi.cucumber.repositories

import com.ryzingtitan.datalogapi.data.datalogs.entities.DatalogEntity
import com.ryzingtitan.datalogapi.data.datalogs.repositories.DatalogRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant

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
    fun thenTheFollowingDatalogsWillExist(table: DataTable) {
        val expectedDatalogs = table.asList(DatalogEntity::class.java)

        val actualDatalogs = mutableListOf<DatalogEntity>()
        runBlocking {
            datalogRepository.findAll().collect { datalog ->
                actualDatalogs.add(datalog)
            }
        }

        assertEquals(expectedDatalogs, actualDatalogs)
    }

    @DataTableType
    fun mapDatalogEntity(tableRow: Map<String, String>): DatalogEntity =
        DatalogEntity(
            id = tableRow["id"]?.toLong(),
            sessionId = tableRow["sessionId"]?.toInt(),
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
