package com.ryzingtitan.obdtrakapi.cucumber.repositories

import com.ryzingtitan.obdtrakapi.data.cars.entities.CarEntity
import com.ryzingtitan.obdtrakapi.data.cars.repositories.CarRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.util.UUID
import kotlin.test.assertEquals

class CarRepositoryStepDefs(
    private val carRepository: CarRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following cars exist:")
    fun givenTheFollowingCarsExist(existingCars: List<CarEntity>) {
        existingCars.forEach { carEntity ->
            r2dbcEntityTemplate.insert(carEntity).block()
        }
    }

    @Then("the following cars will exist:")
    fun thenTheFollowingCarsWillExist(expectedCars: List<CarEntity>) {
        runBlocking {
            val actualCars = carRepository.findAll().toList()

            assertEquals(expectedCars.size, actualCars.size)

            expectedCars.forEachIndexed { index, expectedCar ->
                assertEquals(expectedCar.yearManufactured, actualCars[index].yearManufactured)
                assertEquals(expectedCar.make, actualCars[index].make)
                assertEquals(expectedCar.model, actualCars[index].model)
            }
        }
    }

    @DataTableType
    fun mapCarEntity(tableRow: Map<String, String>): CarEntity =
        CarEntity(
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            yearManufactured = tableRow["yearManufactured"]!!.toInt(),
            make = tableRow["make"].orEmpty(),
            model = tableRow["model"].orEmpty(),
        )
}
