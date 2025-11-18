package com.ryzingtitan.obdtrakapi.cucumber.controllers

import com.ryzingtitan.obdtrakapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.obdtrakapi.domain.datalogs.dtos.Datalog
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.Instant

class DatalogControllerStepDefs {
    @When("the datalogs for session with id {int} are retrieved")
    fun whenTheDatalogsForSessionWithIdAreRetrieved(sessionId: Int) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/sessions/$sessionId/datalogs")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleMultipleDatalogResponse(clientResponse)
                }
        }
    }

    @Then("the following datalogs are returned:")
    fun thenTheFollowingDatalogsAreReturned(table: DataTable) {
        val expectedDatalogs = table.asList(Datalog::class.java)

        assertEquals(expectedDatalogs, returnedDatalogs)
    }

    @DataTableType
    fun mapDatalog(tableRow: Map<String, String>): Datalog =
        Datalog(
            sessionId = tableRow["sessionId"]!!.toInt(),
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

    private suspend fun handleMultipleDatalogResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

        if (clientResponse.statusCode() == HttpStatus.OK) {
            val datalogs = clientResponse.awaitEntityList<Datalog>().body

            if (datalogs != null) {
                returnedDatalogs.addAll(datalogs)
            }
        }
    }

    private val returnedDatalogs = mutableListOf<Datalog>()
}
