package com.ryzingtitan.obdtrakapi.domain.cars.services

import com.ryzingtitan.obdtrakapi.data.cars.entities.CarEntity
import com.ryzingtitan.obdtrakapi.data.cars.repositories.CarRepository
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.Car
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.CarRequest
import com.ryzingtitan.obdtrakapi.domain.cars.exceptions.CarAlreadyExistsException
import com.ryzingtitan.obdtrakapi.domain.cars.exceptions.CarDoesNotExistException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CarService(
    private val carRepository: CarRepository,
) {
    suspend fun create(carRequest: CarRequest): Car {
        val existingCar =
            carRepository.findByYearManufacturedAndMakeAndModel(carRequest.year, carRequest.make, carRequest.model)

        if (existingCar != null) {
            val message = "${carRequest.year} ${carRequest.make} ${carRequest.model} already exists"
            logger.error(message)
            throw CarAlreadyExistsException(message)
        }

        val carEntity =
            carRepository
                .save(
                    CarEntity(
                        yearManufactured = carRequest.year,
                        make = carRequest.make,
                        model = carRequest.model,
                    ),
                )

        logger.info("Created car ${carRequest.year} ${carRequest.make} ${carRequest.model}")

        return Car(
            id = carEntity.id!!,
            year = carEntity.yearManufactured,
            make = carEntity.make,
            model = carEntity.model,
        )
    }

    suspend fun update(
        carId: UUID,
        carRequest: CarRequest,
    ): Car {
        val existingCar = carRepository.findById(carId)

        if (existingCar == null) {
            val message = "${carRequest.year} ${carRequest.make} ${carRequest.model} does not exist"
            logger.error(message)
            throw CarDoesNotExistException(message)
        }

        val updatedCarEntity =
            carRepository.save(
                CarEntity(
                    id = carId,
                    yearManufactured = carRequest.year,
                    make = carRequest.make,
                    model = carRequest.model,
                ),
            )

        logger.info("Updated car ${carRequest.year} ${carRequest.make} ${carRequest.model}")

        return Car(
            id = updatedCarEntity.id!!,
            year = updatedCarEntity.yearManufactured,
            make = updatedCarEntity.make,
            model = updatedCarEntity.model,
        )
    }

    fun getAll(): Flow<Car> {
        logger.info("Retrieving all cars")

        return carRepository.findAll().map { carEntity ->
            Car(
                id = carEntity.id!!,
                year = carEntity.yearManufactured,
                make = carEntity.make,
                model = carEntity.model,
            )
        }
    }

    suspend fun delete(carId: UUID) {
        carRepository.deleteById(carId)
        logger.info("Deleted car with id $carId")
    }

    private val logger: Logger = LoggerFactory.getLogger(CarService::class.java)
}
