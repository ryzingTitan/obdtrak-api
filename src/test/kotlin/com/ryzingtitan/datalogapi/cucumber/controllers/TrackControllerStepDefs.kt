package com.ryzingtitan.datalogapi.cucumber.controllers

import com.ryzingtitan.datalogapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.datalogapi.domain.tracks.dtos.Track
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange

class TrackControllerStepDefs {
    @When("all tracks are retrieved")
    fun whenAllTracksAreRetrieved() {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/tracks")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleMultipleTrackResponse(clientResponse)
                }
        }
    }

    @When("the following track is created:")
    fun whenTheFollowingTrackIsCreated(table: DataTable) {
        val track = table.tableConverter.toList<Track>(table, Track::class.java).first()

        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/tracks")
                .body(BodyInserters.fromValue(track))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @When("the following track is updated:")
    fun whenTheFollowingTrackIsUpdated(table: DataTable) {
        val track = table.tableConverter.toList<Track>(table, Track::class.java).first()

        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/tracks/${track.id}")
                .body(BodyInserters.fromValue(track))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @When("the track with id {int} is deleted")
    fun whenTheTrackWithIdIsDeleted(trackId: Int) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .delete()
                .uri("/tracks/$trackId")
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @Then("the following tracks are returned:")
    fun thenTheFollowingTracksAreReturned(table: DataTable) {
        val expectedTracks = table.tableConverter.toList<Track>(table, Track::class.java)

        assertEquals(expectedTracks, returnedTracks)
    }

    private fun handleTrackResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus
        CommonControllerStepDefs.locationHeader =
            clientResponse.headers().header(HttpHeaders.LOCATION).firstOrNull() ?: ""
    }

    @DataTableType
    fun mapTrack(tableRow: Map<String, String>): Track =
        Track(
            id = tableRow["id"]?.toIntOrNull(),
            name = tableRow["name"].orEmpty(),
            longitude = tableRow["longitude"]!!.toDouble(),
            latitude = tableRow["latitude"]!!.toDouble(),
        )

    private suspend fun handleMultipleTrackResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

        if (clientResponse.statusCode() == HttpStatus.OK) {
            val tracks = clientResponse.awaitEntityList<Track>().body

            if (tracks != null) {
                returnedTracks.addAll(tracks)
            }
        }
    }

    private val returnedTracks = mutableListOf<Track>()
}
