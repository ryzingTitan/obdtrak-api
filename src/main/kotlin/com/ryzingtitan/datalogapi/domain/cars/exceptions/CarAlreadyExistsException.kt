package com.ryzingtitan.datalogapi.domain.cars.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT)
class CarAlreadyExistsException(
    message: String,
) : Exception(message)
