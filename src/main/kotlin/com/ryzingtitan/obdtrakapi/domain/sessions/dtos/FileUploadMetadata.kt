package com.ryzingtitan.obdtrakapi.domain.sessions.dtos

import java.util.UUID

data class FileUploadMetadata(
    val fileName: String,
    val trackId: UUID,
    val carId: UUID,
    val userEmail: String,
    val userFirstName: String,
    val userLastName: String,
)
