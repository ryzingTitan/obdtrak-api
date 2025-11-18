package com.ryzingtitan.obdtrakapi.domain.sessions.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class SessionAlreadyExistsException(
    message: String,
) : Exception(message)
