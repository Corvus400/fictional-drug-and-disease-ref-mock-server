package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with icd10_chapter=I returns items all having icd10_chapter==I`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/diseases?icd10_chapter=I")

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "icd10_chapter=I must return a non-empty items array " +
                "(fixture distribution for CHAPTER_I is 6)",
        )
        val expectedSerialName = Icd10Chapter.CHAPTER_I.declaredSerialName()
        items.forEachIndexed { index, item ->
            assertEquals(
                expected = expectedSerialName,
                actual = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content,
                message = "items[$index].icd10_chapter must equal CHAPTER_I serialName when " +
                    "query=icd10_chapter=I (item=${item.jsonObject})",
            )
        }
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertEquals(
            expected = 6,
            actual = totalCount,
            message = "icd10_chapter=I total_count must equal the fixture distribution for CHAPTER_I (= 6)",
        )
    }

    @Test
    fun `GET diseases with icd10_chapter=II returns items all having icd10_chapter==II`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/diseases?icd10_chapter=II")

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "icd10_chapter=II must return a non-empty items array " +
                "(fixture distribution for CHAPTER_II is 6)",
        )
        val expectedSerialName = Icd10Chapter.CHAPTER_II.declaredSerialName()
        items.forEachIndexed { index, item ->
            assertEquals(
                expected = expectedSerialName,
                actual = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content,
                message = "items[$index].icd10_chapter must equal CHAPTER_II serialName when " +
                    "query=icd10_chapter=II (item=${item.jsonObject})",
            )
        }
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertEquals(
            expected = 6,
            actual = totalCount,
            message = "icd10_chapter=II total_count must equal the fixture distribution for CHAPTER_II (= 6)",
        )
    }

    private fun Icd10Chapter.declaredSerialName(): String =
        Icd10Chapter.serializer().descriptor.getElementName(index = ordinal)
}
