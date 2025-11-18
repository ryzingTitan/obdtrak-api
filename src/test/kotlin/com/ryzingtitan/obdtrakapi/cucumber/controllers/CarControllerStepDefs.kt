package com.ryzingtitan.obdtrakapi.cucumber.controllers

import com.ryzingtitan.obdtrakapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.Car
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange

class CarControllerStepDefs {
    @When("all cars are retrieved")
    fun whenAllCarsAreRetrieved() {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/cars")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleMultipleCarResponse(clientResponse)
                }
        }
    }

    @When("the following car is created:")
    fun whenTheFollowingCarIsCreated(table: DataTable) {
        val car = table.asList(Car::class.java).first()

        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/cars")
                .body(BodyInserters.fromValue(car))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @When("the following car is updated:")
    fun whenTheFollowingCarIsUpdated(table: DataTable) {
        val car = table.asList(Car::class.java).first()

        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/cars/${car.id}")
                .body(BodyInserters.fromValue(car))
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @When("the car with id {int} is deleted")
    fun whenTheCarWithIdIsDeleted(carId: Int) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .delete()
                .uri("/cars/$carId")
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @Then("the following cars are returned:")
    fun thenTheFollowingCarsAreReturned(table: DataTable) {
        val expectedCars = table.asList(Car::class.java)

        assertEquals(expectedCars, returnedCars)
    }

    private suspend fun handleCarResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus
        if (clientResponse.statusCode().is2xxSuccessful) {
            val car = clientResponse.awaitEntity<Car>().body

            if (car != null) {
                returnedCars.add(car)
            }
        }
    }

    @DataTableType
    fun mapCar(tableRow: Map<String, String>): Car =
        Car(
            id = tableRow["id"]?.toIntOrNull(),
            year = tableRow["year"]!!.toInt(),
            make = tableRow["make"].orEmpty(),
            model = tableRow["model"].orEmpty(),
        )

    private suspend fun handleMultipleCarResponse(clientResponse: ClientResponse) {
        CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

        if (clientResponse.statusCode() == HttpStatus.OK) {
            val cars = clientResponse.awaitEntityList<Car>().body

            if (cars != null) {
                returnedCars.addAll(cars)
            }
        }
    }

    private val returnedCars = mutableListOf<Car>()
}
