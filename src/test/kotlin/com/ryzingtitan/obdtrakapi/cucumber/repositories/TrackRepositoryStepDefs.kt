package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.tracks.entities.TrackEntity
import com.ryzingtitan.obdtrakapi.data.tracks.repositories.TrackRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.util.UUID
import kotlin.test.assertEquals

class TrackRepositoryStepDefs(
    private val trackRepository: TrackRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following tracks exist:")
    fun givenTheFollowingTracksExist(existingTracks: List<TrackEntity>) {
        existingTracks.forEach { trackEntity ->
            r2dbcEntityTemplate.insert(trackEntity).block()
        }
    }

    @Then("the following tracks will exist:")
    fun thenTheFollowingTracksWillExist(expectedTracks: List<TrackEntity>) {
        runBlocking {
            val actualTracks = trackRepository.findAll().toList()

            assertEquals(expectedTracks.size, actualTracks.size)

            expectedTracks.forEachIndexed { index, expectedTrack ->
                assertEquals(expectedTrack.name, actualTracks[index].name)
                assertEquals(expectedTrack.latitude, actualTracks[index].latitude)
                assertEquals(expectedTrack.longitude, actualTracks[index].longitude)
            }
        }
    }

    @DataTableType
    fun mapTrackEntity(tableRow: Map<String, String>): TrackEntity =
        TrackEntity(
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            name = tableRow["name"].orEmpty(),
            longitude = tableRow["longitude"]!!.toDouble(),
            latitude = tableRow["latitude"]!!.toDouble(),
        )
}
