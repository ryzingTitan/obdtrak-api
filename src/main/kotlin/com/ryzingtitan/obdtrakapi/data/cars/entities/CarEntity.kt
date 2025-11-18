package com.ryzingtitan.obdtrakapi.data.cars.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("cars")
data class CarEntity(
    @Id
    val id: Int? = null,
    val yearManufactured: Int,
    val make: String,
    val model: String,
)
