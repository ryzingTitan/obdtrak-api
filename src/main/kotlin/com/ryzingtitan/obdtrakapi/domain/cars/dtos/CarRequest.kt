package com.ryzingtitan.obdtrakapi.domain.cars.dtos

data class CarRequest(
    val year: Int,
    val make: String,
    val model: String,
)
