package com.ryzingtitan.obdtrakapi.cucumber

import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasspathResource("features")
class CucumberTestRunner
