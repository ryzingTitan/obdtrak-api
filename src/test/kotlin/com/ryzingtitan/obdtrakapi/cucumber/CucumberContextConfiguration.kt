package com.ryzingtitan.obdtrakapi.cucumber

import com.ryzingtitan.obdtrakapi.ObdTrakApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(
    classes = [ObdTrakApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class CucumberContextConfiguration
