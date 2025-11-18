package com.ryzingtitan.obdtrakapi.data.sessions.repositories

import com.ryzingtitan.obdtrakapi.data.sessions.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface SessionRepository : CoroutineCrudRepository<SessionEntity, Int> {
    suspend fun findAllByUserEmail(userEmail: String): Flow<SessionEntity>

    suspend fun findByUserEmailAndStartTimeAndEndTime(
        userEmail: String,
        startTime: Instant,
        endTime: Instant,
    ): SessionEntity?
}
