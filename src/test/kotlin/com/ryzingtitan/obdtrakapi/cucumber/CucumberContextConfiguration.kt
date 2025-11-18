package com.ryzingtitan.obdtrakapi.cucumber

import com.ryzingtitan.obdtrakapi.ObdTrakApiApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(
    classes = [ObdTrakApiApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class CucumberContextConfiguration
