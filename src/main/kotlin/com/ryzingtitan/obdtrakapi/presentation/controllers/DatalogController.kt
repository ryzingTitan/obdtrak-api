package com.ryzingtitan.obdtrakapi.presentation.controllers

import com.ryzingtitan.obdtrakapi.domain.datalogs.dtos.Datalog
import com.ryzingtitan.obdtrakapi.domain.datalogs.services.DatalogService
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
class DatalogController(
    private val datalogService: DatalogService,
) {
    private val logger = LoggerFactory.getLogger(DatalogController::class.java)

    @GetMapping("/{sessionId}/datalogs")
    @Tag(name = "Datalogs")
    @Operation(summary = "Retrieve all datalogs with the given session id sorted by timestamp ascending")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of datalogs with the given session id sorted by timestamp ascending",
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
    suspend fun getDatalogsBySessionId(
        @PathVariable(name = "sessionId") sessionId: UUID,
    ): Flow<Datalog> {
        logger.info("Retrieving datalogs for session id: $sessionId")
        return datalogService.getAllBySessionId(sessionId)
    }
}
