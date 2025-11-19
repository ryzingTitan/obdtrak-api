package com.ryzingtitan.obdtrakapi.cucumber.common

import io.cucumber.java.After
import kotlinx.coroutines.runBlocking
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await

class CommonRepositoryStepDefs(
    private val databaseClient: DatabaseClient,
) {
    @After
    fun resetDatabase() {
        runBlocking {
            databaseClient.sql("DELETE FROM records").await()
            databaseClient.sql("DELETE FROM sessions").await()
            databaseClient.sql("DELETE FROM tracks").await()
            databaseClient.sql("DELETE FROM cars").await()
        }
    }
}
