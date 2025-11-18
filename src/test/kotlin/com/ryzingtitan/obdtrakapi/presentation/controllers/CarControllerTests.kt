package com.ryzingtitan.obdtrakapi.presentation.controllers

import com.ryzingtitan.obdtrakapi.domain.cars.dtos.Car
import com.ryzingtitan.obdtrakapi.domain.cars.dtos.CarRequest
import com.ryzingtitan.obdtrakapi.domain.cars.services.CarService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.UUID

class CarControllerTests {
    @Nested
    inner class GetCars {
        @Test
        fun `returns 'OK' status with all cars`() =
            runTest {
                whenever(mockCarService.getAll()).thenReturn(flowOf(firstCar, secondCar))

                webTestClient
                    .get()
                    .uri("/api/cars")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Car>()
                    .contains(firstCar, secondCar)

                verify(mockCarService, times(1)).getAll()
            }
    }

    @Nested
    inner class CreateCar {
        @Test
        fun `returns 'CREATED' status and creates new car`() =
            runTest {
                whenever(mockCarService.create(firstCarRequest)).thenReturn(firstCar)

                webTestClient
                    .mutateWith(mockJwt())
                    .post()
                    .uri("/api/cars")
                    .bodyValue(firstCarRequest)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<Car>()
                    .isEqualTo(firstCar)

                verify(mockCarService, times(1)).create(firstCarRequest)
            }
    }

    @Nested
    inner class UpdateCar {
        @Test
        fun `returns 'OK' status and updates car`() =
            runTest {
                whenever(mockCarService.update(firstCarId, firstCarRequest)).thenReturn(firstCar)

                webTestClient
                    .mutateWith(mockJwt())
                    .put()
                    .uri("/api/cars/$firstCarId")
                    .bodyValue(firstCarRequest)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody<Car>()
                    .isEqualTo(firstCar)

                verify(mockCarService, times(1)).update(firstCarId, firstCarRequest)
            }
    }

    @Nested
    inner class DeleteCar {
        @Test
        fun `returns 'OK' status and deletes car`() =
            runTest {
                webTestClient
                    .mutateWith(mockJwt())
                    .delete()
                    .uri("/api/cars/$firstCarId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk

                verify(mockCarService, times(1)).delete(firstCarId)
            }
    }

    @BeforeEach
    fun setup() {
        val carController = CarController(mockCarService)
        webTestClient = WebTestClient.bindToController(carController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockCarService = mock<CarService>()

    private val firstCarId = UUID.randomUUID()
    private val secondCarId = UUID.randomUUID()

    private val firstCarRequest =
        CarRequest(
            year = FIRST_CAR_YEAR,
            make = FIRST_CAR_MAKE,
            model = FIRST_CAR_MODEL,
        )

    private val firstCar =
        Car(
            id = firstCarId,
            year = FIRST_CAR_YEAR,
            make = FIRST_CAR_MAKE,
            model = FIRST_CAR_MODEL,
        )

    private val secondCar =
        Car(
            id = secondCarId,
            year = SECOND_CAR_YEAR,
            make = SECOND_CAR_MAKE,
            model = SECOND_CAR_MODEL,
        )

    companion object CarControllerTestConstants {
        const val FIRST_CAR_YEAR = 1999
        const val FIRST_CAR_MAKE = "Chevrolet"
        const val FIRST_CAR_MODEL = "Corvette"

        const val SECOND_CAR_YEAR = 2001
        const val SECOND_CAR_MAKE = "Volkswagen"
        const val SECOND_CAR_MODEL = "Jetta"
    }
}
