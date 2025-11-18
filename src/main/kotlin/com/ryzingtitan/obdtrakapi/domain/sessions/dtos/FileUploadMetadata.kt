package com.ryzingtitan.obdtrakapi.domain.sessions.dtos

import java.util.UUID

data class FileUploadMetadata(
    val fileName: String,
    val sessionId: Int?,
    val trackId: UUID,
    val carId: Int,
    val userEmail: String,
    val userFirstName: String,
    val userLastName: String,
)
