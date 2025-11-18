package com.ryzingtitan.obdtrakapi.data.cars.repositories

import com.ryzingtitan.obdtrakapi.data.cars.entities.CarEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface CarRepository : CoroutineCrudRepository<CarEntity, UUID> {
    suspend fun findByYearManufacturedAndMakeAndModel(
        yearManufactured: Int,
        make: String,
        model: String,
    ): CarEntity?
}
