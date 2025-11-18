package com.ryzingtitan.obdtrakapi.data.cars.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("cars")
data class CarEntity(
    @Id
    val id: UUID? = null,
    val yearManufactured: Int,
    val make: String,
    val model: String,
)
