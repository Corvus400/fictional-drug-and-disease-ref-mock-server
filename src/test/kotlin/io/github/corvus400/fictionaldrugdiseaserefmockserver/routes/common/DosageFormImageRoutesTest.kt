package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class DosageFormImageRoutesTest {
    @Test
    fun `GET dosage form image with Original returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/images/dosage_form/tablet?size=Original")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
    }
}
