package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import com.ryzingtitan.obdtrakapi.data.sessions.repositories.SessionRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class SessionRepositoryStepDefs(
    private val sessionRepository: SessionRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following sessions exist:")
    fun givenTheFollowingSessionsExist(existingSessions: List<SessionEntity>) {
        existingSessions.forEach { sessionEntity ->
            r2dbcEntityTemplate.insert(sessionEntity).block()
        }
    }

    @Then("the following sessions will exist:")
    fun thenTheFollowingSessionsWillExist(expectedSessions: List<SessionEntity>) {
        runBlocking {
            val actualSessions = sessionRepository.findAll().toList()

            assertEquals(expectedSessions.size, actualSessions.size)

            expectedSessions.forEachIndexed { index, expectedSession ->
                assertEquals(expectedSession.userEmail, actualSessions[index].userEmail)
                assertEquals(expectedSession.userFirstName, actualSessions[index].userFirstName)
                assertEquals(expectedSession.userLastName, actualSessions[index].userLastName)
                assertEquals(expectedSession.startTime, actualSessions[index].startTime)
                assertEquals(expectedSession.endTime, actualSessions[index].endTime)
                assertEquals(expectedSession.trackId, actualSessions[index].trackId)
                assertEquals(expectedSession.carId, actualSessions[index].carId)
            }
        }
    }

    @DataTableType
    fun mapSessionEntity(tableRow: Map<String, String>): SessionEntity =
        SessionEntity(
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            userEmail = tableRow["userEmail"].orEmpty(),
            userFirstName = tableRow["userFirstName"].orEmpty(),
            userLastName = tableRow["userLastName"].orEmpty(),
            startTime = Instant.parse(tableRow["startTime"].orEmpty()),
            endTime = Instant.parse(tableRow["endTime"].orEmpty()),
            trackId = UUID.fromString(tableRow["trackId"]),
            carId = UUID.fromString(tableRow["carId"]),
        )
}
