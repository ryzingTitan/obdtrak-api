package com.ryzingtitan.obdtrakapi.presentation.controllers

import com.ryzingtitan.obdtrakapi.domain.records.dtos.Record
import com.ryzingtitan.obdtrakapi.domain.records.services.RecordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/api/sessions"])
class RecordController(
    private val recordService: RecordService,
) {
    private val logger = LoggerFactory.getLogger(RecordController::class.java)

    @GetMapping("/{sessionId}/records")
    @Tag(name = "Records")
    @Operation(summary = "Retrieve all records with the given session id sorted by timestamp ascending")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of records with the given session id sorted by timestamp ascending",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Ensure the request contains all required data",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun getRecordsBySessionId(
        @PathVariable(name = "sessionId") sessionId: UUID,
    ): Flow<Record> {
        logger.info("Retrieving records for session id: $sessionId")
        return recordService.getAllBySessionId(sessionId)
    }
}
