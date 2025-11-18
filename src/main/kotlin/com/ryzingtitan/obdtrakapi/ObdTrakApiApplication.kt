package com.ryzingtitan.obdtrakapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ObdTrakApiApplication

fun main(args: Array<String>) {
    runApplication<ObdTrakApiApplication>(arrayOf(args).contentDeepToString())
}
