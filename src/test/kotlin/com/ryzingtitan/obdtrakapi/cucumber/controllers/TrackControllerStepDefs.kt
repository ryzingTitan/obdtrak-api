package com.ryzingtitan.obdtrakapi.cucumber.controllers

import com.ryzingtitan.obdtrakapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.obdtrakapi.domain.tracks.dtos.Track
import com.ryzingtitan.obdtrakapi.domain.tracks.dtos.TrackRequest
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.test.assertEquals

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
    fun whenTheFollowingTrackIsCreated(trackRequests: List<TrackRequest>) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/tracks")
                .bodyValue(trackRequests.first())
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @When("the following track id {string} is updated:")
    fun whenTheFollowingTrackIsUpdated(
        trackId: String,
        trackRequests: List<TrackRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/tracks/${UUID.fromString(trackId)}")
                .bodyValue(trackRequests.first())
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @When("the track with id {string} is deleted")
    fun whenTheTrackWithIdIsDeleted(trackId: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .delete()
                .uri("/tracks/${UUID.fromString(trackId)}")
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleTrackResponse(clientResponse)
                }
        }
    }

    @Then("the following tracks are returned:")
    fun thenTheFollowingTracksAreReturned(expectedTracks: List<Track>) {
        assertEquals(expectedTracks.size, returnedTracks.size)

        expectedTracks.forEachIndexed { index, expectedTrack ->
            assertEquals(expectedTrack.name, returnedTracks[index].name)
            assertEquals(expectedTrack.latitude, returnedTracks[index].latitude)
            assertEquals(expectedTrack.longitude, returnedTracks[index].longitude)
        }
    }

    private suspend fun handleTrackResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus
        if (clientResponse.statusCode().is2xxSuccessful) {
            val track = clientResponse.awaitEntity<Track>().body

            if (track != null) {
                returnedTracks.add(track)
            }
        }
    }

    @DataTableType
    fun mapTrackRequest(tableRow: Map<String, String>): TrackRequest =
        TrackRequest(
            name = tableRow["name"].orEmpty(),
            longitude = tableRow["longitude"]!!.toDouble(),
            latitude = tableRow["latitude"]!!.toDouble(),
        )

    @DataTableType
    fun mapTrack(tableRow: Map<String, String>): Track =
        Track(
            id = UUID.randomUUID(),
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
