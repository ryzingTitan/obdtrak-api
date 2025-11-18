package com.ryzingtitan.obdtrakapi.domain.sessions.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.obdtrakapi.data.datalogs.entities.DatalogEntity
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.obdtrakapi.domain.sessions.dtos.FileUploadMetadata
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import java.time.Instant
import java.util.UUID

class FileParsingServiceTests {
    @Nested
    inner class Parse {
        @Test
        fun `parses file correctly when it contains valid session data`() =
            runTest {
                val dataBufferFactory = DefaultDataBufferFactory()
                val dataBuffer = dataBufferFactory.wrap(validFileData.toByteArray())

                val datalogs = fileParsingService.parse(FileUpload(flowOf(dataBuffer), fileUploadMetadata))

                assertEquals(listOf(firstDatalog), datalogs)
                assertEquals(2, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Beginning to parse file: testFile.txt", appender.list[0].message)
                assertEquals(Level.INFO, appender.list[1].level)
                assertEquals("File parsing completed for file: testFile.txt", appender.list[1].message)
            }

        @Test
        fun `parses file correctly when session id is null`() =
            runTest {
                val dataBufferFactory = DefaultDataBufferFactory()
                val dataBuffer = dataBufferFactory.wrap(validFileData.toByteArray())

                val datalogs =
                    fileParsingService.parse(
                        FileUpload(
                            flowOf(dataBuffer),
                            fileUploadMetadata.copy(sessionId = null),
                        ),
                    )

                assertEquals(listOf(firstDatalog.copy(sessionId = null)), datalogs)
                assertEquals(2, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Beginning to parse file: testFile.txt", appender.list[0].message)
                assertEquals(Level.INFO, appender.list[1].level)
                assertEquals("File parsing completed for file: testFile.txt", appender.list[1].message)
            }

        @Test
        fun `parses file correctly when air fuel ratio column is missing`() =
            runTest {
                val dataBufferFactory = DefaultDataBufferFactory()
                val dataBuffer = dataBufferFactory.wrap(missingAirFuelRatioFileData.toByteArray())

                val datalogs = fileParsingService.parse(FileUpload(flowOf(dataBuffer), fileUploadMetadata))

                assertEquals(listOf(firstDatalog.copy(airFuelRatio = null)), datalogs)
                assertEquals(2, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Beginning to parse file: testFile.txt", appender.list[0].message)
                assertEquals(Level.INFO, appender.list[1].level)
                assertEquals("File parsing completed for file: testFile.txt", appender.list[1].message)
            }

        @Test
        fun `parses file correctly when it contains invalid session data`() =
            runTest {
                val dataBufferFactory = DefaultDataBufferFactory()
                val dataBuffer = dataBufferFactory.wrap(invalidFileData.toByteArray())

                val datalogs = fileParsingService.parse(FileUpload(flowOf(dataBuffer), fileUploadMetadata))

                assertEquals(listOf(secondDatalog), datalogs)
                assertEquals(2, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Beginning to parse file: testFile.txt", appender.list[0].message)
                assertEquals(Level.INFO, appender.list[1].level)
                assertEquals("File parsing completed for file: testFile.txt", appender.list[1].message)
            }

        @Test
        fun `parses file correctly when it contains unparseable session data`() =
            runTest {
                val dataBufferFactory = DefaultDataBufferFactory()
                val dataBuffer = dataBufferFactory.wrap(unparseableFileData.toByteArray())

                val datalogs = fileParsingService.parse(FileUpload(flowOf(dataBuffer), fileUploadMetadata))

                assertEquals(emptyList<DatalogEntity>(), datalogs)
                assertEquals(3, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Beginning to parse file: testFile.txt", appender.list[0].message)
                assertEquals(Level.ERROR, appender.list[1].level)
                assertEquals(
                    "Unable to parse row: Sat Oct 21 16:22:38 EDT 2023," +
                        "Device Time,-86.14162999999999,42.406800000000004,10.260987281799316,1.7999999523162842," +
                        "188.4,340.0299987792969,7.44,0.16,1.97,-0.2,14.7,155,5500,123,86,95.5,16.5",
                    appender.list[1].message,
                )
                assertEquals(Level.INFO, appender.list[2].level)
                assertEquals("File parsing completed for file: testFile.txt", appender.list[2].message)
            }
    }

    @BeforeEach
    fun setup() {
        fileParsingService = FileParsingService()

        logger = LoggerFactory.getLogger(FileParsingService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var fileParsingService: FileParsingService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val firstLineTimestamp = Instant.parse("2022-09-18T18:15:47.963Z")
    private val secondLineTimestamp = Instant.parse("2022-09-18T18:18:47.968Z")
    private val trackId = UUID.randomUUID()
    private val carId = UUID.randomUUID()

    private val fileUploadMetadata =
        FileUploadMetadata(
            fileName = "testFile.txt",
            sessionId = SESSION_ID,
            trackId = trackId,
            carId = carId,
            userEmail = USER_EMAIL,
            userFirstName = USER_FIRST_NAME,
            userLastName = USER_LAST_NAME,
        )

    private val validFileData =
        """
        GPS Time, Device Time, Longitude, Latitude,GPS Speed (Meters/second), Horizontal Dilution of Precision, Altitude, Bearing, G(x), G(y), G(z), G(calibrated), Air Fuel Ratio(Measured)(:1),Engine Coolant Temperature(°F),Engine RPM(rpm),Intake Air Temperature(°F),Speed (OBD)(mph),Throttle Position(Manifold)(%),Turbo Boost & Vacuum Gauge(psi)
        Sat Oct 21 16:22:38 EDT 2023,$FIRST_LINE_DEVICE_TIME,$FIRST_LINE_LONGITUDE,$FIRST_LINE_LATITUDE,10.260987281799316,1.7999999523162842,$FIRST_LINE_ALTITUDE,340.0299987792969,7.44,0.16,1.97,-0.2,$FIRST_LINE_AIR_FUEL_RATIO,$FIRST_LINE_COOLANT_TEMPERATURE,$FIRST_LINE_ENGINE_RPM,$FIRST_LINE_INTAKE_AIR_TEMPERATURE,$FIRST_LINE_SPEED,$FIRST_LINE_THROTTLE_POSITION,$FIRST_LINE_BOOST_PRESSURE
        """.trimIndent()

    private val missingAirFuelRatioFileData =
        """
        GPS Time, Device Time, Longitude, Latitude,GPS Speed (Meters/second), Horizontal Dilution of Precision, Altitude, Bearing, G(x), G(y), G(z), G(calibrated),Engine Coolant Temperature(°F),Engine RPM(rpm),Intake Air Temperature(°F),Speed (OBD)(mph),Throttle Position(Manifold)(%),Turbo Boost & Vacuum Gauge(psi)
        Sat Oct 21 16:22:38 EDT 2023,$FIRST_LINE_DEVICE_TIME,$FIRST_LINE_LONGITUDE,$FIRST_LINE_LATITUDE,10.260987281799316,1.7999999523162842,$FIRST_LINE_ALTITUDE,340.0299987792969,7.44,0.16,1.97,-0.2,$FIRST_LINE_COOLANT_TEMPERATURE,$FIRST_LINE_ENGINE_RPM,$FIRST_LINE_INTAKE_AIR_TEMPERATURE,$FIRST_LINE_SPEED,$FIRST_LINE_THROTTLE_POSITION,$FIRST_LINE_BOOST_PRESSURE
        """.trimIndent()

    private val invalidFileData =
        """
        GPS Time, Device Time, Longitude, Latitude,GPS Speed (Meters/second), Horizontal Dilution of Precision, Altitude, Bearing, G(x), G(y), G(z), G(calibrated), Air Fuel Ratio(Measured)(:1),Engine Coolant Temperature(°F),Engine RPM(rpm),Intake Air Temperature(°F),Speed (OBD)(mph),Throttle Position(Manifold)(%),Turbo Boost & Vacuum Gauge(psi)
        Sat Oct 21 16:22:38 EDT 2023,$SECOND_LINE_DEVICE_TIME,$SECOND_LINE_LONGITUDE,$SECOND_LINE_LATITUDE,10.260987281799316,1.7999999523162842,$SECOND_LINE_ALTITUDE,340.0299987792969,7.44,0.16,1.97,-0.2,$SECOND_LINE_AIR_FUEL_RATIO,$SECOND_LINE_COOLANT_TEMPERATURE,$SECOND_LINE_ENGINE_RPM,$SECOND_LINE_INTAKE_AIR_TEMPERATURE,$SECOND_LINE_SPEED,$SECOND_LINE_THROTTLE_POSITION,$SECOND_LINE_BOOST_PRESSURE
        """.trimIndent()

    private val unparseableFileData =
        """
        GPS Time, Device Time, Longitude, Latitude,GPS Speed (Meters/second), Horizontal Dilution of Precision, Altitude, Bearing, G(x), G(y), G(z), G(calibrated), Air Fuel Ratio(Measured)(:1),Engine Coolant Temperature(°F),Engine RPM(rpm),Intake Air Temperature(°F),Speed (OBD)(mph),Throttle Position(Manifold)(%),Turbo Boost & Vacuum Gauge(psi)
        Sat Oct 21 16:22:38 EDT 2023,Device Time,$FIRST_LINE_LONGITUDE,$FIRST_LINE_LATITUDE,10.260987281799316,1.7999999523162842,$FIRST_LINE_ALTITUDE,340.0299987792969,7.44,0.16,1.97,-0.2,$FIRST_LINE_AIR_FUEL_RATIO,$FIRST_LINE_COOLANT_TEMPERATURE,$FIRST_LINE_ENGINE_RPM,$FIRST_LINE_INTAKE_AIR_TEMPERATURE,$FIRST_LINE_SPEED,$FIRST_LINE_THROTTLE_POSITION,$FIRST_LINE_BOOST_PRESSURE
        """.trimIndent()

    private val firstDatalog =
        DatalogEntity(
            sessionId = SESSION_ID,
            timestamp = firstLineTimestamp,
            longitude = FIRST_LINE_LONGITUDE,
            latitude = FIRST_LINE_LATITUDE,
            altitude = FIRST_LINE_ALTITUDE,
            intakeAirTemperature = FIRST_LINE_INTAKE_AIR_TEMPERATURE,
            boostPressure = FIRST_LINE_BOOST_PRESSURE,
            coolantTemperature = FIRST_LINE_COOLANT_TEMPERATURE,
            engineRpm = FIRST_LINE_ENGINE_RPM,
            speed = FIRST_LINE_SPEED,
            throttlePosition = FIRST_LINE_THROTTLE_POSITION,
            airFuelRatio = FIRST_LINE_AIR_FUEL_RATIO,
        )

    private val secondDatalog =
        DatalogEntity(
            sessionId = SESSION_ID,
            timestamp = secondLineTimestamp,
            longitude = SECOND_LINE_LONGITUDE,
            latitude = SECOND_LINE_LATITUDE,
            altitude = SECOND_LINE_ALTITUDE,
            intakeAirTemperature = null,
            boostPressure = null,
            coolantTemperature = null,
            engineRpm = null,
            speed = null,
            throttlePosition = null,
            airFuelRatio = null,
        )

    companion object FileParsingServiceTestConstants {
        const val USER_EMAIL = "test@test.com"
        const val USER_FIRST_NAME = "test"
        const val USER_LAST_NAME = "tester"
        const val SESSION_ID = 1

        const val FIRST_LINE_DEVICE_TIME = "18-Sep-2022 14:15:47.963"
        const val FIRST_LINE_LONGITUDE = -86.14162999999999
        const val FIRST_LINE_LATITUDE = 42.406800000000004
        const val FIRST_LINE_ALTITUDE = 188.4f
        const val FIRST_LINE_INTAKE_AIR_TEMPERATURE = 123
        const val FIRST_LINE_BOOST_PRESSURE = 16.5f
        const val FIRST_LINE_COOLANT_TEMPERATURE = 155
        const val FIRST_LINE_ENGINE_RPM = 5500
        const val FIRST_LINE_SPEED = 86
        const val FIRST_LINE_THROTTLE_POSITION = 95.5f
        const val FIRST_LINE_AIR_FUEL_RATIO = 14.7f

        const val SECOND_LINE_DEVICE_TIME = "18-Sep-2022 14:18:47.968"
        const val SECOND_LINE_LONGITUDE = 86.14162999999999
        const val SECOND_LINE_LATITUDE = -42.406800000000004
        const val SECOND_LINE_ALTITUDE = 188.0f
        const val SECOND_LINE_INTAKE_AIR_TEMPERATURE = "-"
        const val SECOND_LINE_BOOST_PRESSURE = "-"
        const val SECOND_LINE_COOLANT_TEMPERATURE = "-"
        const val SECOND_LINE_ENGINE_RPM = "-"
        const val SECOND_LINE_SPEED = "-"
        const val SECOND_LINE_THROTTLE_POSITION = "-"
        const val SECOND_LINE_AIR_FUEL_RATIO = "-"
    }
}
