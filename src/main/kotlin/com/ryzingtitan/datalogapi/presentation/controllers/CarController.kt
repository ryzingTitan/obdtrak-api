package com.ryzingtitan.datalogapi.presentation.controllers

import com.ryzingtitan.datalogapi.domain.cars.dtos.Car
import com.ryzingtitan.datalogapi.domain.cars.services.CarService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping(path = ["/api/cars"])
class CarController(
    private val careService: CarService,
) {
    @GetMapping
    fun getCars(): Flow<Car> = careService.getAll()

    @PostMapping
    suspend fun createCar(
        @RequestBody car: Car,
        response: ServerHttpResponse,
        exchange: ServerWebExchange,
    ) {
        val carId = careService.create(car)

        response.headers.add(
            HttpHeaders.LOCATION,
            "${exchange.request.uri}/$carId",
        )
        response.statusCode = HttpStatus.CREATED
    }

    @PutMapping("/{carId}")
    suspend fun updateCar(
        @PathVariable(name = "carId") carId: Int,
        @RequestBody car: Car,
    ) {
        careService.update(car.copy(id = carId))
    }

    @DeleteMapping("/{carId}")
    suspend fun deleteCar(
        @PathVariable(name = "carId") carId: Int,
    ) {
        careService.delete(carId)
    }
}
