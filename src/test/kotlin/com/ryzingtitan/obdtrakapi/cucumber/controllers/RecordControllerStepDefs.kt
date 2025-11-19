package com.ryzingtitan.obdtrakapi.cucumber.controllers

import com.ryzingtitan.obdtrakapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.obdtrakapi.domain.records.dtos.Record
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
import java.util.UUID

class RecordControllerStepDefs {
    @When("the records for session with id {string} are retrieved")
    fun whenTheRecordsForSessionWithIdAreRetrieved(sessionId: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/sessions/${UUID.fromString(sessionId)}/records")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleMultipleRecordResponse(clientResponse)
                }
        }
    }

    @Then("the following records are returned:")
    fun thenTheFollowingRecordsAreReturned(expectedRecords: List<Record>) {
        assertEquals(expectedRecords, returnedRecords)
    }

    @DataTableType
    fun mapRecord(tableRow: Map<String, String>): Record =
        Record(
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

    private suspend fun handleMultipleRecordResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

        if (clientResponse.statusCode() == HttpStatus.OK) {
            val records = clientResponse.awaitEntityList<Record>().body

            if (records != null) {
                returnedRecords.addAll(records)
            }
        }
    }

    private val returnedRecords = mutableListOf<Record>()
}
