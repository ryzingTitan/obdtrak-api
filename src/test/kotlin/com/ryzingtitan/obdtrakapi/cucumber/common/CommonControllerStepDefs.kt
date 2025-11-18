package com.ryzingtitan.obdtrakapi.cucumber.common

import com.nimbusds.jwt.SignedJWT
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient

class CommonControllerStepDefs {
    @Given("the user has a valid authorization token")
    fun theUserHasAValidAuthorizationToken() {
        authorizationToken =
            mockOAuth2Server
                .issueToken("default", "someclientid", DefaultOAuth2TokenCallback())
    }

    @Given("the user has an invalid authorization token")
    fun theUserHasAnInvalidAuthorizationToken() {
        authorizationToken = null
    }

    @Then("the request response status is {string}")
    fun theRequestResponseStatusIs(statusCode: String) {
        assertEquals(statusCode, responseStatus?.name)
    }

    @Before
    fun setup() {
        webClient = WebClient.create("http://localhost:$port/api")

        mockOAuth2Server = MockOAuth2Server()
        mockOAuth2Server.start(8088)
    }

    @After
    fun teardown() {
        mockOAuth2Server.shutdown()
    }

    @LocalServerPort
    private val port = 0

    companion object CommonControllerStepDefsSharedState {
        internal var responseStatus: HttpStatus? = null
        internal var authorizationToken: SignedJWT? = null

        internal lateinit var webClient: WebClient
        internal lateinit var mockOAuth2Server: MockOAuth2Server
    }
}
