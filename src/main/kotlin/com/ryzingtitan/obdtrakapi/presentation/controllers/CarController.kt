package com.ryzingtitan.obdtrakapi.presentation.controllers

import com.ryzingtitan.obdtrakapi.domain.cars.dtos.Car
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.CarRequest
import com.ryzingtitan.obdtrakapi.domain.cars.services.CarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/api/cars"])
class CarController(
    private val carService: CarService,
) {
    @GetMapping
    @Tag(name = "Cars")
    @Operation(summary = "Retrieve all cars")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of cars",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    fun getCars(): Flow<Car> = carService.getAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Tag(name = "Car Administration")
    @Operation(summary = "Create a new car")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Car created successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Ensure the request contains all required data",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict - Car already exists",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun createCar(
        @RequestBody carRequest: CarRequest,
    ): Car = carService.create(carRequest)

    @PutMapping("/{carId}")
    @Tag(name = "Car Administration")
    @Operation(summary = "Update an existing car")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Car updated successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Ensure the request contains all required data",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found - Car does not exist",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun updateCar(
        @PathVariable(name = "carId") carId: UUID,
        @RequestBody carRequest: CarRequest,
    ): Car = carService.update(carId, carRequest)

    @DeleteMapping("/{carId}")
    @Tag(name = "Car Administration")
    @Operation(summary = "Delete an existing car")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Car deleted successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Ensure the request contains all required data",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun deleteCar(
        @PathVariable(name = "carId") carId: UUID,
    ) {
        carService.delete(carId)
    }
}
