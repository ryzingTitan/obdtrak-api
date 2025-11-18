package com.ryzingtitan.obdtrakapi.data.datalogs.repositories

import com.ryzingtitan.obdtrakapi.data.datalogs.entities.DatalogEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface DatalogRepository : CoroutineCrudRepository<DatalogEntity, Int> {
    suspend fun findAllBySessionIdOrderByTimestampAsc(sessionId: UUID): Flow<DatalogEntity>

    suspend fun deleteAllBySessionId(sessionId: UUID): Flow<DatalogEntity>
}
