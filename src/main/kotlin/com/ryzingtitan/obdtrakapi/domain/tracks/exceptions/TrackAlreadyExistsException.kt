package com.ryzingtitan.obdtrakapi.domain.tracks.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class TrackAlreadyExistsException(
    message: String,
) : Exception(message)
