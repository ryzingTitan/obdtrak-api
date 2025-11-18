package com.ryzingtitan.obdtrakapi.data.sessions.repositories

import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant
import java.util.UUID

interface SessionRepository : CoroutineCrudRepository<SessionEntity, UUID> {
    suspend fun findAllByUserEmail(userEmail: String): Flow<SessionEntity>

    suspend fun findByUserEmailAndStartTimeAndEndTime(
        userEmail: String,
        startTime: Instant,
        endTime: Instant,
    ): SessionEntity?
}
