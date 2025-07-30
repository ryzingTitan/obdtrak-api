package com.ryzingtitan.datalogapi.cucumber.common

import io.cucumber.datatable.DataTable
import io.cucumber.java.Before
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import java.nio.file.Files
import java.nio.file.Path

class FileUploadStepDefs {
    @Given("a file with the following rows:")
    fun givenAFileWithTheFollowingData(table: DataTable) {
        val fileLines = mutableListOf<String>()
        fileLines.addAll(createHeaderRow(table))
        fileLines.addAll(createDataRows(table))

        Files.createDirectory(Path.of("testFiles"))
        Files.write(Path.of("testFiles", "testFile.txt"), fileLines)
    }

    private fun createHeaderRow(table: DataTable): List<String> {
        val headerLine = StringBuilder()
        table.row(0).forEach { headerValue ->
            headerLine.append(headerValue).append(',')
        }
        return listOf(headerLine.toString().trimEnd(','))
    }

    private fun createDataRows(table: DataTable): List<String> = table.tableConverter.toList(table, String::class.java)

    @DataTableType
    fun mapFileLine(tableRow: Map<String, String>): String =
        "${tableRow["Device Time"]}," +
            "${tableRow["Longitude"]}," +
            "${tableRow["Latitude"]}," +
            "${tableRow["Altitude"]}," +
            "${tableRow["Engine Coolant Temperature(°F)"]}," +
            "${tableRow["Engine RPM(rpm)"]}," +
            "${tableRow["Intake Air Temperature(°F)"]}," +
            "${tableRow["Speed (OBD)(mph)"]}," +
            "${tableRow["Throttle Position(Manifold)(%)"]}," +
            "${tableRow["Turbo Boost & Vacuum Gauge(psi)"]}," +
            tableRow["Air Fuel Ratio(Measured)(:1)"].orEmpty()

    @Before
    fun setup() {
        Files.deleteIfExists(Path.of("testFiles", "testFile.txt"))
        Files.deleteIfExists(Path.of("testFiles"))
    }
}
