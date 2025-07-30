package com.ryzingtitan.datalogapi.domain.tracks.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.GONE)
class TrackDoesNotExistException(
    message: String,
) : Exception(message)
