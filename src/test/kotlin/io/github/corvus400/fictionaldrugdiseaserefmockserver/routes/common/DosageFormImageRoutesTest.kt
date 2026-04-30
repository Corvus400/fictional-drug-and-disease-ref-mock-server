package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.math.max
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

    @Test
    fun `GET dosage form image with S returns one eighth of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/images/dosage_form/tablet?size=Original").bodyAsBytes())
        val small = decodeImage(client.get("/images/dosage_form/tablet?size=S").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 8, max(small.width, small.height))
    }

    @Test
    fun `GET dosage form image with M returns one quarter of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/images/dosage_form/tablet?size=Original").bodyAsBytes())
        val medium = decodeImage(client.get("/images/dosage_form/tablet?size=M").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 4, max(medium.width, medium.height))
    }

    @Test
    fun `GET dosage form image without size returns Original`() = testApplication {
        application { module() }

        val explicitOriginal = decodeImage(client.get("/images/dosage_form/tablet?size=Original").bodyAsBytes())
        val defaultOriginal = decodeImage(client.get("/images/dosage_form/tablet").bodyAsBytes())

        assertEquals(explicitOriginal.width, defaultOriginal.width)
        assertEquals(explicitOriginal.height, defaultOriginal.height)
    }

    @Test
    fun `GET unknown dosage form image returns 404`() = testApplication {
        application { module() }

        val response = client.get("/images/dosage_form/UNKNOWN_FORM")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET dosage form image with invalid size returns 400`() = testApplication {
        application { module() }

        val response = client.get("/images/dosage_form/tablet?size=invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private fun decodeImage(bytes: ByteArray) = ImageIO.read(ByteArrayInputStream(bytes))
}
