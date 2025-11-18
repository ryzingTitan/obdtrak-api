package com.ryzingtitan.obdtrakapi.cucumber.controllers

import com.ryzingtitan.obdtrakapi.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.Car
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.CarRequest
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.test.assertEquals

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
    fun whenTheFollowingCarIsCreated(carRequests: List<CarRequest>) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/cars")
                .bodyValue(carRequests.first())
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @When("the following car id {string} is updated:")
    fun whenTheFollowingCarIsUpdated(
        carId: String,
        carRequests: List<CarRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/cars/${UUID.fromString(carId)}")
                .bodyValue(carRequests.first())
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @When("the car with id {string} is deleted")
    fun whenTheCarWithIdIsDeleted(carId: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .delete()
                .uri("/cars/${UUID.fromString(carId)}")
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    handleCarResponse(clientResponse)
                }
        }
    }

    @Then("the following cars are returned:")
    fun thenTheFollowingCarsAreReturned(expectedCars: List<Car>) {
        assertEquals(expectedCars.size, returnedCars.size)

        expectedCars.forEachIndexed { index, expectedCar ->
            assertEquals(expectedCar.year, returnedCars[index].year)
            assertEquals(expectedCar.make, returnedCars[index].make)
            assertEquals(expectedCar.model, returnedCars[index].model)
        }
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
    fun mapCarRequest(tableRow: Map<String, String>): CarRequest =
        CarRequest(
            year = tableRow["year"]!!.toInt(),
            make = tableRow["make"].orEmpty(),
            model = tableRow["model"].orEmpty(),
        )

    @DataTableType
    fun mapCar(tableRow: Map<String, String>): Car =
        Car(
            id = UUID.randomUUID(),
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
