package com.ryzingtitan.datalogapi.presentation.controllers

import com.ryzingtitan.datalogapi.domain.datalogs.dtos.Datalog
import com.ryzingtitan.datalogapi.domain.datalogs.services.DatalogService
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/sessions"])
class DatalogController(
    private val datalogService: DatalogService,
) {
    private val logger = LoggerFactory.getLogger(DatalogController::class.java)

    @GetMapping("/{sessionId}/datalogs")
    suspend fun getDatalogsBySessionId(
        @PathVariable(name = "sessionId") sessionId: Int,
    ): Flow<Datalog> {
        logger.info("Retrieving datalogs for session id: $sessionId")
        return datalogService.getAllBySessionId(sessionId)
    }
}
