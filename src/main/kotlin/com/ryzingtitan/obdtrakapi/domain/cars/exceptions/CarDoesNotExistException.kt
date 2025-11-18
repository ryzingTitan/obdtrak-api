package com.ryzingtitan.obdtrakapi.domain.cars.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class CarDoesNotExistException(
    message: String,
) : Exception(message)
