package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import com.ryzingtitan.obdtrakapi.data.sessions.repositories.SessionRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.util.UUID

class SessionRepositoryStepDefs(
    private val sessionRepository: SessionRepository,
) {
    @Given("the following sessions exist:")
    fun givenTheFollowingSessionsExist(table: DataTable) {
        val sessions = table.asList(SessionEntity::class.java)

        runBlocking {
            sessionRepository.saveAll(sessions).collect()
        }
    }

    @Then("the following sessions will exist:")
    fun thenTheFollowingSessionsWillExist(table: DataTable) {
        val expectedSessions = table.asList(SessionEntity::class.java)

        val actualSessions = mutableListOf<SessionEntity>()
        runBlocking {
            sessionRepository.findAll().collect { sessionEntity ->
                actualSessions.add(sessionEntity)
            }
        }

        assertEquals(expectedSessions, actualSessions)
    }

    @DataTableType
    fun mapSessionEntity(tableRow: Map<String, String>): SessionEntity =
        SessionEntity(
            id = tableRow["id"]?.toIntOrNull(),
            userEmail = tableRow["userEmail"].orEmpty(),
            userFirstName = tableRow["userFirstName"].orEmpty(),
            userLastName = tableRow["userLastName"].orEmpty(),
            startTime = Instant.parse(tableRow["startTime"].orEmpty()),
            endTime = Instant.parse(tableRow["endTime"].orEmpty()),
            trackId = UUID.fromString(tableRow["trackId"]),
            carId = UUID.fromString(tableRow["carId"]),
        )
}
