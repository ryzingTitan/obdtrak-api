package com.ryzingtitan.datalogapi.domain.sessions.services

import com.ryzingtitan.datalogapi.data.datalogs.entities.DatalogEntity
import com.ryzingtitan.datalogapi.domain.sessions.dtos.FileUpload
import com.ryzingtitan.datalogapi.domain.sessions.dtos.FileUploadMetadata
import kotlinx.coroutines.flow.map
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class FileParsingService {
    private val logger: Logger = LoggerFactory.getLogger(FileParsingService::class.java)

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun parse(fileUpload: FileUpload): List<DatalogEntity> {
        logger.info("Beginning to parse file: ${fileUpload.metadata.fileName}")

        val datalogs = mutableListOf<DatalogEntity>()
        val fileData = StringBuilder()

        fileUpload.file
            .map { dataBuffer ->
                dataBuffer.asInputStream().bufferedReader()
            }.collect { reader ->
                fileData.append(reader.readText())
            }

        val reader = fileData.toString().reader()
        val records = csvFormat.parse(reader)

        records.forEach { record ->
            try {
                val datalog = createDatalog(record, fileUpload.metadata)
                datalogs.add(datalog)
            } catch (exception: Exception) {
                logger.error("Unable to parse row: ${record.values().joinToString(",")}")
            }
        }

        logger.info("File parsing completed for file: ${fileUpload.metadata.fileName}")

        return datalogs
    }

    private suspend fun createDatalog(
        row: CSVRecord,
        metadata: FileUploadMetadata,
    ): DatalogEntity {
        val datalogTimestamp = parseRowTimestamp(row["Device Time"])
        val longitude = row["Longitude"].toDouble()
        val latitude = row["Latitude"].toDouble()
        val altitude = row["Altitude"].toFloat()
        val intakeAirTemperature = row["Intake Air Temperature(°F)"].toFloatOrNull()?.toInt()
        val boostPressure = row["Turbo Boost & Vacuum Gauge(psi)"].toFloatOrNull()
        val coolantTemperature = row["Engine Coolant Temperature(°F)"].toFloatOrNull()?.toInt()
        val engineRpm = row["Engine RPM(rpm)"].toFloatOrNull()?.toInt()
        val speed = row["Speed (OBD)(mph)"].toFloatOrNull()?.toInt()
        val throttlePosition = row["Throttle Position(Manifold)(%)"].toFloatOrNull()

        var airFuelRatio: Float? = null
        if (row.isMapped("Air Fuel Ratio(Measured)(:1)")) {
            airFuelRatio = row["Air Fuel Ratio(Measured)(:1)"].toFloatOrNull()
        }

        return DatalogEntity(
            sessionId = metadata.sessionId,
            timestamp = datalogTimestamp,
            longitude = longitude,
            latitude = latitude,
            altitude = altitude,
            intakeAirTemperature = intakeAirTemperature,
            boostPressure = boostPressure,
            coolantTemperature = coolantTemperature,
            engineRpm = engineRpm,
            speed = speed,
            throttlePosition = throttlePosition,
            airFuelRatio = airFuelRatio,
        )
    }

    private fun parseRowTimestamp(rowTimestamp: String): Instant {
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss.SSS").withZone(ZoneId.of("America/New_York"))
        val parsedDateTime = dateTimeFormatter.parse(rowTimestamp)
        return Instant.from(parsedDateTime)
    }

    private val csvFormat =
        CSVFormat
            .DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .get()
}
