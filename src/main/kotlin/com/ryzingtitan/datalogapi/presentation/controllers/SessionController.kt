package com.ryzingtitan.datalogapi.presentation.controllers

import com.ryzingtitan.datalogapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.datalogapi.domain.sessions.dtos.FileUploadMetadata
import com.ryzingtitan.datalogapi.domain.sessions.dtos.Session
import com.ryzingtitan.datalogapi.domain.sessions.services.SessionService
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
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux

@RestController
@RequestMapping(path = ["/api/sessions"])
class SessionController(
    private val sessionService: SessionService,
) {
    @GetMapping
    suspend fun getAllSessionsByUser(
        @RequestParam userEmail: String,
    ): Flow<Session> {
        logger.info("Retrieving all sessions for user: $userEmail")
        return sessionService.getAllByUser(userEmail)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Suppress("LongParameterList")
    suspend fun createSession(
        @RequestPart(name = "userEmail") userEmail: String,
        @RequestPart(name = "userFirstName") userFirstName: String,
        @RequestPart(name = "userLastName") userLastName: String,
        @RequestPart(name = "trackId") trackId: String,
        @RequestPart(name = "carId") carId: String,
        @RequestPart(name = "uploadFiles") uploadFiles: Flux<FilePart>,
        exchange: ServerWebExchange,
    ) {
        uploadFiles.collect { uploadFile ->
            val fileUpload =
                FileUpload(
                    file = uploadFile.content().asFlow(),
                    metadata =
                        FileUploadMetadata(
                            fileName = uploadFile.filename(),
                            sessionId = null,
                            carId = carId.toInt(),
                            trackId = trackId.toInt(),
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
    suspend fun updateSession(
        @RequestPart(name = "userEmail") userEmail: String,
        @RequestPart(name = "userFirstName") userFirstName: String,
        @RequestPart(name = "userLastName") userLastName: String,
        @RequestPart(name = "trackId") trackId: String,
        @RequestPart(name = "carId") carId: String,
        @RequestPart(name = "uploadFile") uploadFile: FilePart,
        @PathVariable(name = "sessionId") sessionId: Int,
    ) {
        val fileUpload =
            FileUpload(
                file = uploadFile.content().asFlow(),
                metadata =
                    FileUploadMetadata(
                        fileName = uploadFile.filename(),
                        sessionId = sessionId,
                        trackId = trackId.toInt(),
                        carId = carId.toInt(),
                        userEmail = userEmail,
                        userFirstName = userFirstName,
                        userLastName = userLastName,
                    ),
            )
        sessionService.update(fileUpload)
    }

    private val logger = LoggerFactory.getLogger(SessionController::class.java)
}
