package com.ryzingtitan.datalogapi.cucumber.repositories

import com.ryzingtitan.datalogapi.data.tracks.entities.TrackEntity
import com.ryzingtitan.datalogapi.data.tracks.repositories.TrackRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

class TrackRepositoryStepDefs(
    private val trackRepository: TrackRepository,
) {
    @Given("the following tracks exist:")
    fun givenTheFollowingTracksExist(table: DataTable) {
        val tracks = table.tableConverter.toList<TrackEntity>(table, TrackEntity::class.java)

        runBlocking {
            trackRepository.saveAll(tracks).collect()
        }
    }

    @Then("the following tracks will exist:")
    fun thenTheFollowingTracksWillExist(table: DataTable) {
        val expectedTracks = table.tableConverter.toList<TrackEntity>(table, TrackEntity::class.java)

        val actualTracks = mutableListOf<TrackEntity>()
        runBlocking {
            trackRepository.findAll().collect { track ->
                actualTracks.add(track)
            }
        }

        assertEquals(expectedTracks, actualTracks)
    }

    @DataTableType
    fun mapTrackEntity(tableRow: Map<String, String>): TrackEntity =
        TrackEntity(
            id = tableRow["id"]?.toIntOrNull(),
            name = tableRow["name"].orEmpty(),
            longitude = tableRow["longitude"]!!.toDouble(),
            latitude = tableRow["latitude"]!!.toDouble(),
        )
}
