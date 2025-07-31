package com.ryzingtitan.datalogapi.cucumber.controllers

import com.ryzingtitan.datalogapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.datalogapi.cucumber.common.CommonControllerStepDefs.CommonControllerStepDefsSharedState.responseStatus
import com.ryzingtitan.datalogapi.cucumber.dtos.RequestData
import com.ryzingtitan.datalogapi.domain.sessions.dtos.Session
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.Instant

class SessionControllerStepDefs {
    @When("the sessions are retrieved for user {string}")
    fun whenTheSessionsAreRetrievedForUser(userEmail: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/sessions?userEmail=$userEmail")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleMultipleSessionResponse(clientResponse)
                }
        }
    }

    @When("the file is uploaded for a session with the following data:")
    fun theFileIsUploadedForSessionWithTheFollowingData(table: DataTable) {
        val requestData = table.asList(RequestData::class.java)

        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder.part("userEmail", requestData.first().userEmail)
        multipartBodyBuilder.part("userLastName", requestData.first().userLastName)
        multipartBodyBuilder.part("userFirstName", requestData.first().userFirstName)
        multipartBodyBuilder.part("trackId", requestData.first().trackId.toString())
        multipartBodyBuilder.part("carId", requestData.first().carId.toString())
        multipartBodyBuilder.part("uploadFiles", FileSystemResource("testFiles/testFile.txt"))
        val multiPartData = multipartBodyBuilder.build()

        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/sessions")
                .body(BodyInserters.fromMultipartData(multiPartData))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleSessionResponse(clientResponse)
                }
        }
    }

    @When("the file is uploaded for a session with the following data and session id {int}:")
    fun theFileIsUploadedForSessionWithTheFollowingDataAndSessionId(
        sessionId: Int,
        table: DataTable,
    ) {
        val requestData = table.asList(RequestData::class.java)

        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder.part("userEmail", requestData.first().userEmail)
        multipartBodyBuilder.part("userLastName", requestData.first().userLastName)
        multipartBodyBuilder.part("userFirstName", requestData.first().userFirstName)
        multipartBodyBuilder.part("trackId", requestData.first().trackId)
        multipartBodyBuilder.part("carId", requestData.first().carId)
        multipartBodyBuilder.part("uploadFile", FileSystemResource("testFiles/testFile.txt"))
        val multiPartData = multipartBodyBuilder.build()

        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/sessions/$sessionId")
                .body(BodyInserters.fromMultipartData(multiPartData))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleSessionResponse(clientResponse)
                }
        }
    }

    @Then("the following sessions are returned:")
    fun thenTheFollowingSessionsAreReturned(table: DataTable) {
        val expectedSessions = table.asList(Session::class.java)

        assertEquals(expectedSessions, returnedSessions)
    }

    @DataTableType
    fun mapRequestData(tableRow: Map<String, String>): RequestData =
        RequestData(
            trackId = tableRow["trackId"].orEmpty(),
            carId = tableRow["carId"].orEmpty(),
            userFirstName = tableRow["userFirstName"].orEmpty(),
            userLastName = tableRow["userLastName"].orEmpty(),
            userEmail = tableRow["userEmail"].orEmpty(),
        )

    @DataTableType
    fun mapSession(tableRow: Map<String, String>): Session =
        Session(
            id = tableRow["id"]!!.toInt(),
            startTime = Instant.parse(tableRow["startTime"].orEmpty()),
            endTime = Instant.parse(tableRow["endTime"].orEmpty()),
            trackName = tableRow["trackName"].orEmpty(),
            trackLatitude = tableRow["trackLatitude"]!!.toDouble(),
            trackLongitude = tableRow["trackLongitude"]!!.toDouble(),
            carYear = tableRow["carYear"]!!.toInt(),
            carMake = tableRow["carMake"].orEmpty(),
            carModel = tableRow["carModel"].orEmpty(),
        )

    private fun handleSessionResponse(clientResponse: ClientResponse) {
        responseStatus = clientResponse.statusCode() as HttpStatus
        CommonControllerStepDefs.locationHeader =
            clientResponse.headers().header(HttpHeaders.LOCATION).firstOrNull() ?: ""
    }

    private suspend fun handleMultipleSessionResponse(clientResponse: ClientResponse) {
        responseStatus = clientResponse.statusCode() as HttpStatus

        if (clientResponse.statusCode() == HttpStatus.OK) {
            val sessionList = clientResponse.awaitEntityList<Session>().body

            if (sessionList != null) {
                returnedSessions.addAll(sessionList)
            }
        }
    }

    private val returnedSessions = mutableListOf<Session>()
}
