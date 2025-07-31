package com.ryzingtitan.datalogapi.cucumber.common

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
            databaseClient.sql("DELETE FROM datalogs").await()
            databaseClient.sql("ALTER TABLE datalogs ALTER COLUMN ID RESTART WITH 1").await()
            databaseClient.sql("DELETE FROM sessions").await()
            databaseClient.sql("ALTER TABLE sessions ALTER COLUMN ID RESTART WITH 1").await()
            databaseClient.sql("DELETE FROM tracks").await()
            databaseClient.sql("ALTER TABLE tracks ALTER COLUMN ID RESTART WITH 1").await()
            databaseClient.sql("DELETE FROM cars").await()
            databaseClient.sql("ALTER TABLE cars ALTER COLUMN ID RESTART WITH 1").await()
        }
    }
}
