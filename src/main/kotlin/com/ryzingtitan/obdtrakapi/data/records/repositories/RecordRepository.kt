package com.ryzingtitan.obdtrakapi.data.records.repositories

import com.ryzingtitan.obdtrakapi.data.records.entities.RecordEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface RecordRepository : CoroutineCrudRepository<RecordEntity, UUID> {
    suspend fun findAllBySessionIdOrderByTimestampAsc(sessionId: UUID): Flow<RecordEntity>

    suspend fun deleteAllBySessionId(sessionId: UUID): Flow<RecordEntity>
}
