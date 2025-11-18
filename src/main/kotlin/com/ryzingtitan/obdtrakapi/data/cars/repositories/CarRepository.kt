package com.ryzingtitan.obdtrakapi.data.cars.repositories

import com.ryzingtitan.obdtrakapi.data.cars.entities.CarEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CarRepository : CoroutineCrudRepository<CarEntity, Int> {
    suspend fun findByYearManufacturedAndMakeAndModel(
        yearManufactured: Int,
        make: String,
        model: String,
    ): CarEntity?
}
