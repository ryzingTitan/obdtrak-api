package com.ryzingtitan.obdtrakapi.presentation.controllers

import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUploadMetadata
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.Session
import com.ryzingtitan.obdtrakapi.domain.sessions.services.SessionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.collect
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.UUID

@RestController
@RequestMapping(path = ["/api/sessions"])
class SessionController(
    private val sessionService: SessionService,
) {
    @GetMapping
    @Tag(name = "Session")
    @Operation(summary = "Retrieve all sessions")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of sessions",
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
    suspend fun getAllSessionsByUser(
        @RequestParam userEmail: String,
    ): Flow<Session> {
        logger.info("Retrieving all sessions for user: $userEmail")
        return sessionService.getAllByUser(userEmail)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Suppress("LongParameterList")
    @Tag(name = "Session Administration")
    @Operation(summary = "Create new sessions")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Sessions created successfully",
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
                responseCode = "409",
                description = "Conflict - Session already exists",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun createSession(
        @RequestPart(name = "userEmail") userEmail: String,
        @RequestPart(name = "userFirstName") userFirstName: String,
        @RequestPart(name = "userLastName") userLastName: String,
        @RequestPart(name = "trackId") trackId: String,
        @RequestPart(name = "carId") carId: String,
        @RequestPart(name = "uploadFiles") uploadFiles: Flux<FilePart>,
    ) {
        uploadFiles.collect { uploadFile ->
            val fileUpload =
                FileUpload(
                    file = uploadFile.content().asFlow(),
                    metadata =
                        FileUploadMetadata(
                            fileName = uploadFile.filename(),
                            carId = UUID.fromString(carId),
                            trackId = UUID.fromString(trackId),
                            userEmail = userEmail,
                            userFirstName = userFirstName,
                            userLastName = userLastName,
                        ),
                )
            sessionService.create(fileUpload)
        }
    }

    @PutMapping("/{sessionId}")
    @Suppress("LongParameterList")
    @Tag(name = "Session Administration")
    @Operation(summary = "Update an existing session")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Session updated successfully",
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
                responseCode = "404",
                description = "Not Found - Session does not exist",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun updateSession(
        @RequestPart(name = "userEmail") userEmail: String,
        @RequestPart(name = "userFirstName") userFirstName: String,
        @RequestPart(name = "userLastName") userLastName: String,
        @RequestPart(name = "trackId") trackId: String,
        @RequestPart(name = "carId") carId: String,
        @RequestPart(name = "uploadFile") uploadFile: FilePart,
        @PathVariable(name = "sessionId") sessionId: UUID,
    ) {
        val fileUpload =
            FileUpload(
                file = uploadFile.content().asFlow(),
                metadata =
                    FileUploadMetadata(
                        fileName = uploadFile.filename(),
                        trackId = UUID.fromString(trackId),
                        carId = UUID.fromString(carId),
                        userEmail = userEmail,
                        userFirstName = userFirstName,
                        userLastName = userLastName,
                    ),
            )
        sessionService.update(fileUpload, sessionId)
    }

    private val logger = LoggerFactory.getLogger(SessionController::class.java)
}
