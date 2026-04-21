package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceholderImageRoutesTest {
    @Test
    fun `GET images path returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/images/test.png")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
    }

    @Test
    fun `GET smart base images path returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/smart/base/images/autocancel_banner_navi.png")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
    }

    @Test
    fun `GET smart base images nested path returns PNG`() = testApplication {
        application { module() }

        val response = client.get("/smart/base/images/some/nested/image.png")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
    }
}
