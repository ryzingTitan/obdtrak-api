package com.ryzingtitan.datalogapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DatalogApiApplication

fun main(args: Array<String>) {
    runApplication<DatalogApiApplication>(arrayOf(args).contentDeepToString())
}
