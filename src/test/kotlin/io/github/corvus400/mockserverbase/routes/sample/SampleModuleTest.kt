package io.github.corvus400.mockserverbase.routes.sample

import io.github.corvus400.mockserverbase.module
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SampleModuleTest {
    @Test
    fun `GET api sample returns default scenario`() = testApplication {
        application { module() }

        val response = client.get("/api/sample")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("sample-1"))
    }

    @Test
    fun `GET api sample with X-Mock-Scenario empty returns empty response`() = testApplication {
        application { module() }

        val response = client.get("/api/sample") {
            header("X-Mock-Scenario", "empty")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains(""""id":"""""))
    }
}
