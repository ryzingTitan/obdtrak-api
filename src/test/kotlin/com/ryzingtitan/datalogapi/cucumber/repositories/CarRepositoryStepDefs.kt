package com.ryzingtitan.datalogapi.cucumber.repositories

import com.ryzingtitan.datalogapi.data.cars.entities.CarEntity
import com.ryzingtitan.datalogapi.data.cars.repositories.CarRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

class CarRepositoryStepDefs(
    private val carRepository: CarRepository,
) {
    @Given("the following cars exist:")
    fun givenTheFollowingCarsExist(table: DataTable) {
        val cars = table.asList(CarEntity::class.java)

        runBlocking {
            carRepository.saveAll(cars).collect()
        }
    }

    @Then("the following cars will exist:")
    fun thenTheFollowingCarsWillExist(table: DataTable) {
        val expectedCars = table.asList(CarEntity::class.java)

        val actualCars = mutableListOf<CarEntity>()
        runBlocking {
            carRepository.findAll().collect { car ->
                actualCars.add(car)
            }
        }

        assertEquals(expectedCars, actualCars)
    }

    @DataTableType
    fun mapCarEntity(tableRow: Map<String, String>): CarEntity =
        CarEntity(
            id = tableRow["id"]?.toIntOrNull(),
            yearManufactured = tableRow["yearManufactured"]!!.toInt(),
            make = tableRow["make"].orEmpty(),
            model = tableRow["model"].orEmpty(),
        )
}
