package com.ryzingtitan.obdtrakapi.domain.sessions.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class SessionDoesNotExistException(
    message: String,
) : Exception(message)
