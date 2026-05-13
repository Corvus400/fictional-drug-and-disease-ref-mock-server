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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DosageFormImageRoutesTest {
    @Test
    fun `GET v1 dosage form image with Original returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/dosage-forms/tablet?size=Original")

        assertEquals(PngResponseSnapshot(), response.pngSnapshot())
    }

    @Test
    fun `GET v1 drug override image with Original returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/drugs/drug_0089?size=Original")

        assertEquals(PngResponseSnapshot(), response.pngSnapshot())
    }

    @Test
    fun `GET dosage form image with Original returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/dosage-forms/tablet?size=Original")

        assertEquals(PngResponseSnapshot(), response.pngSnapshot())
    }

    @Test
    fun `GET dosage form image with S returns one eighth of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=Original").bodyAsBytes())
        val small = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=S").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 8, max(small.width, small.height))
    }

    @Test
    fun `GET dosage form image with M returns one quarter of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=Original").bodyAsBytes())
        val medium = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=M").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 4, max(medium.width, medium.height))
    }

    @Test
    fun `GET dosage form image with S renders non-black pixels`() = testApplication {
        application { module() }

        val small = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=S").bodyAsBytes())

        assertTrue(hasVisibleNonBlackPixel(small), "S image should contain at least one visible non-black pixel")
    }

    @Test
    fun `GET dosage form image with M renders non-black pixels`() = testApplication {
        application { module() }

        val medium = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=M").bodyAsBytes())

        assertTrue(hasVisibleNonBlackPixel(medium), "M image should contain at least one visible non-black pixel")
    }

    @Test
    fun `GET dosage form image without size returns Original`() = testApplication {
        application { module() }

        val explicitOriginal = decodeImage(client.get("/v1/images/dosage-forms/tablet?size=Original").bodyAsBytes())
        val defaultOriginal = decodeImage(client.get("/v1/images/dosage-forms/tablet").bodyAsBytes())

        assertEquals(explicitOriginal.dimensions(), defaultOriginal.dimensions())
    }

    @Test
    fun `GET dosage form image with Original returns source bytes for indexed PNG`() = testApplication {
        application { module() }

        val responseBytes = client.get("/v1/images/dosage-forms/tablet?size=Original").bodyAsBytes()

        assertContentEquals(resourceBytes("images/dosage_form/tablet.png"), responseBytes)
    }

    @Test
    fun `GET dosage form image with Original returns source bytes for RGBA PNG`() = testApplication {
        application { module() }

        val responseBytes = client.get("/v1/images/dosage-forms/capsule?size=Original").bodyAsBytes()

        assertContentEquals(resourceBytes("images/dosage_form/capsule.png"), responseBytes)
    }

    @Test
    fun `GET drug override image with Original returns source bytes`() = testApplication {
        application { module() }

        val responseBytes = client.get("/v1/images/drugs/drug_0080?size=Original").bodyAsBytes()

        assertContentEquals(resourceBytes("images/drug/drug_0080.png"), responseBytes)
    }

    @Test
    fun `GET unknown dosage form image returns 404`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/dosage-forms/UNKNOWN_FORM")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET dosage form image with invalid size returns 400`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/dosage-forms/tablet?size=invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET drug override image with Original returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/drugs/drug_0089?size=Original")

        assertEquals(PngResponseSnapshot(), response.pngSnapshot())
    }

    @Test
    fun `GET drug override image with S returns one eighth of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/v1/images/drugs/drug_0080?size=Original").bodyAsBytes())
        val small = decodeImage(client.get("/v1/images/drugs/drug_0080?size=S").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 8, max(small.width, small.height))
    }

    @Test
    fun `GET drug override image with M returns one quarter of original long edge`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/v1/images/drugs/drug_0080?size=Original").bodyAsBytes())
        val medium = decodeImage(client.get("/v1/images/drugs/drug_0080?size=M").bodyAsBytes())

        assertEquals(max(original.width, original.height) / 4, max(medium.width, medium.height))
    }

    @Test
    fun `GET drug override image with Original returns full size`() = testApplication {
        application { module() }

        val original = decodeImage(client.get("/v1/images/drugs/drug_0080?size=Original").bodyAsBytes())

        assertEquals(ImageDimensions(width = 512, height = 768), original.dimensions())
    }

    @Test
    fun `GET unknown drug override image returns 404`() = testApplication {
        application { module() }

        val response = client.get("/v1/images/drugs/drug_9999?size=Original")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    private fun decodeImage(bytes: ByteArray) = ImageIO.read(ByteArrayInputStream(bytes))

    private fun io.ktor.client.statement.HttpResponse.pngSnapshot(): PngResponseSnapshot =
        PngResponseSnapshot(
            status = status,
            contentType = contentType()?.withoutParameters(),
        )

    private fun java.awt.image.BufferedImage.dimensions(): ImageDimensions =
        ImageDimensions(width = width, height = height)

    private data class PngResponseSnapshot(
        val status: HttpStatusCode = HttpStatusCode.OK,
        val contentType: ContentType? = ContentType.Image.PNG,
    )

    private data class ImageDimensions(
        val width: Int,
        val height: Int,
    )

    private fun resourceBytes(path: String): ByteArray =
        requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(path)) {
            "resource not found: $path"
        }.use { it.readBytes() }

    private fun hasVisibleNonBlackPixel(image: java.awt.image.BufferedImage): Boolean =
        (0 until image.height).any { y ->
            (0 until image.width).any { x ->
                val argb = image.getRGB(x, y)
                val alpha = argb ushr 24 and 0xff
                val rgb = argb and 0x00ffffff
                alpha > 0 && rgb != 0
            }
        }
}
