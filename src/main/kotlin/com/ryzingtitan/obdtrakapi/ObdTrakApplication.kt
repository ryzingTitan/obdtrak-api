package com.ryzingtitan.obdtrakapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ObdTrakApplication

fun main(args: Array<String>) {
    runApplication<ObdTrakApplication>(arrayOf(args).contentDeepToString())
}
