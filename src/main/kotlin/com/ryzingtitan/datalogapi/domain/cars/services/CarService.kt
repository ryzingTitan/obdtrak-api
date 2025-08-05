package com.ryzingtitan.datalogapi.domain.cars.services

import com.ryzingtitan.datalogapi.data.cars.entities.CarEntity
import com.ryzingtitan.datalogapi.data.cars.repositories.CarRepository
import com.ryzingtitan.datalogapi.domain.cars.dtos.Car
import com.ryzingtitan.datalogapi.domain.cars.exceptions.CarAlreadyExistsException
import com.ryzingtitan.datalogapi.domain.cars.exceptions.CarDoesNotExistException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CarService(
    private val carRepository: CarRepository,
) {
    suspend fun create(car: Car): Car {
        val existingCar = carRepository.findByYearManufacturedAndMakeAndModel(car.year, car.make, car.model)

        if (existingCar != null) {
            val message = "${car.year} ${car.make} ${car.model} already exists"
            logger.error(message)
            throw CarAlreadyExistsException(message)
        }

        val carEntity =
            carRepository
                .save(
                    CarEntity(
                        yearManufactured = car.year,
                        make = car.make,
                        model = car.model,
                    ),
                )

        logger.info("Created car ${car.year} ${car.make} ${car.model}")

        return Car(
            id = carEntity.id,
            year = carEntity.yearManufactured,
            make = carEntity.make,
            model = carEntity.model,
        )
    }

    suspend fun update(car: Car): Car {
        val existingCar = carRepository.findById(car.id!!)

        if (existingCar == null) {
            val message = "${car.year} ${car.make} ${car.model} does not exist"
            logger.error(message)
            throw CarDoesNotExistException(message)
        }

        val updatedCarEntity =
            carRepository.save(
                CarEntity(
                    id = car.id,
                    yearManufactured = car.year,
                    make = car.make,
                    model = car.model,
                ),
            )

        logger.info("Updated car ${car.year} ${car.make} ${car.model}")

        return Car(
            id = updatedCarEntity.id,
            year = updatedCarEntity.yearManufactured,
            make = updatedCarEntity.make,
            model = updatedCarEntity.model,
        )
    }

    fun getAll(): Flow<Car> {
        logger.info("Retrieving all cars")

        return carRepository.findAll().map { carEntity ->
            Car(
                id = carEntity.id,
                year = carEntity.yearManufactured,
                make = carEntity.make,
                model = carEntity.model,
            )
        }
    }

    suspend fun delete(carId: Int) {
        carRepository.deleteById(carId)
        logger.info("Deleted car with id $carId")
    }

    private val logger: Logger = LoggerFactory.getLogger(CarService::class.java)
}
