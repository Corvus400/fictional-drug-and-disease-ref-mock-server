package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLParameter
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
    fun `GET diseases with icd10_chapter=chapter_i returns items all having icd10_chapter==chapter_i`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?icd10_chapter=chapter_i")

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(actual = items, message = "response body must have an items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "icd10_chapter=chapter_i must return a non-empty items array " +
                    "(fixture distribution for CHAPTER_I is 6)",
            )
            val expectedSerialName = Icd10Chapter.CHAPTER_I.declaredSerialName()
            items.forEachIndexed { index, item ->
                assertEquals(
                    expected = expectedSerialName,
                    actual = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content,
                    message = "items[$index].icd10_chapter must equal CHAPTER_I serialName when " +
                        "query=icd10_chapter=chapter_i (item=${item.jsonObject})",
                )
            }
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 6,
                actual = totalCount,
                message = "icd10_chapter=chapter_i total_count must equal the fixture distribution for CHAPTER_I (= 6)",
            )
        }

    @Test
    fun `GET diseases with icd10_chapter=chapter_ii returns items all having icd10_chapter==chapter_ii`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?icd10_chapter=chapter_ii")

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(actual = items, message = "response body must have an items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "icd10_chapter=chapter_ii must return a non-empty items array " +
                    "(fixture distribution for CHAPTER_II is 6)",
            )
            val expectedSerialName = Icd10Chapter.CHAPTER_II.declaredSerialName()
            items.forEachIndexed { index, item ->
                assertEquals(
                    expected = expectedSerialName,
                    actual = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content,
                    message = "items[$index].icd10_chapter must equal CHAPTER_II serialName when " +
                        "query=icd10_chapter=chapter_ii (item=${item.jsonObject})",
                )
            }
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 6,
                actual = totalCount,
                message = "icd10_chapter=chapter_ii total_count must equal the fixture distribution " +
                    "for CHAPTER_II (= 6)",
            )
        }

    @Test
    fun `GET diseases with department=PSYCHIATRY returns items whose medicalDepartment contains PSYCHIATRY`() =
        testApplication {
            application { module() }

            val expectedSerialName = MedicalDepartment.PSYCHIATRY.declaredSerialName()
            val encodedDepartment = expectedSerialName.encodeURLParameter()
            val response = client.get(
                urlString = "/v1/diseases?department=$encodedDepartment&page_size=100",
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(actual = items, message = "response body must have an items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "department=$expectedSerialName must return a non-empty items array " +
                    "(PSYCHIATRY is the primary department for CHAPTER_V)",
            )
            items.forEachIndexed { index, item ->
                val departments = item.jsonObject["medical_department"]?.jsonArray
                assertNotNull(
                    actual = departments,
                    message = "items[$index].medical_department must exist (item=${item.jsonObject})",
                )
                val serialNames = departments.map { it.jsonPrimitive.content }
                assertTrue(
                    actual = serialNames.contains(element = expectedSerialName),
                    message = "items[$index].medical_department must contain '$expectedSerialName' when " +
                        "query=department=$expectedSerialName (item=${item.jsonObject})",
                )
            }
        }

    @Test
    fun `GET diseases with chronicity=ACUTE returns items whose chronicity == value`() = testApplication {
        application { module() }

        val expectedSerialName = Chronicity.ACUTE.declaredSerialName()
        val encodedChronicity = expectedSerialName.encodeURLParameter()
        val response = client.get(
            urlString = "/v1/diseases?chronicity=$encodedChronicity&page_size=100",
        )

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "chronicity=$expectedSerialName must return a non-empty items array " +
                "(ACUTE is the chronicity for CHAPTER_I and several default chapters)",
        )
        items.forEachIndexed { index, item ->
            assertEquals(
                expected = expectedSerialName,
                actual = item.jsonObject["chronicity"]?.jsonPrimitive?.content,
                message = "items[$index].chronicity must equal '$expectedSerialName' when " +
                    "query=chronicity=$expectedSerialName (item=${item.jsonObject})",
            )
        }
    }

    @Test
    fun `GET diseases with infectious=true returns items whose infectious == true`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?infectious=true&page_size=100")

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "infectious=true must return a non-empty items array " +
                "(CHAPTER_I blueprints populate infectious=true for 6 fixtures)",
        )
        items.forEachIndexed { index, item ->
            assertEquals(
                expected = true,
                actual = item.jsonObject["infectious"]?.jsonPrimitive?.content?.toBooleanStrictOrNull(),
                message = "items[$index].infectious must equal true when " +
                    "query=infectious=true (item=${item.jsonObject})",
            )
        }
    }

    @Test
    fun `GET diseases with icd10_chapter=chapter_i and infectious=true returns intersection (AND filter)`() =
        testApplication {
            application { module() }

            val response = client.get(
                urlString = "/v1/diseases?icd10_chapter=chapter_i&infectious=true&page_size=100",
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(actual = items, message = "response body must have an items array")
            val expectedChapter = Icd10Chapter.CHAPTER_I.declaredSerialName()
            assertTrue(
                actual = items.isNotEmpty(),
                message = "icd10_chapter=chapter_i & infectious=true must return a non-empty items array " +
                    "(CHAPTER_I fixtures all have infectious=true by blueprint contract)",
            )
            items.forEachIndexed { index, item ->
                val obj = item.jsonObject
                assertEquals(
                    expected = expectedChapter,
                    actual = obj["icd10_chapter"]?.jsonPrimitive?.content,
                    message = "items[$index].icd10_chapter must equal '$expectedChapter' when " +
                        "query=icd10_chapter=chapter_i&infectious=true (item=$obj)",
                )
                assertEquals(
                    expected = true,
                    actual = obj["infectious"]?.jsonPrimitive?.content?.toBooleanStrictOrNull(),
                    message = "items[$index].infectious must equal true when " +
                        "query=icd10_chapter=chapter_i&infectious=true (item=$obj)",
                )
            }
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 6,
                actual = totalCount,
                message = "intersection total_count must equal CHAPTER_I distribution (= 6) since " +
                    "all CHAPTER_I fixtures are infectious=true",
            )
        }

    @Test
    fun `GET diseases with legacy roman icd10_chapter=I returns total_count zero`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?icd10_chapter=I&page_size=100")

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isEmpty(),
            message = "items must be empty when legacy roman key 'I' is provided " +
                "(only @SerialName snake_case keys are accepted)",
        )
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertEquals(
            expected = 0,
            actual = totalCount,
            message = "total_count must equal 0 when legacy roman key 'I' is provided",
        )
    }

    @Test
    fun `GET diseases with invalid department returns zero results`() = testApplication {
        application { module() }

        assertZeroResultsForInvalidFilter(
            client = client,
            url = "/v1/diseases?department=invalid&page_size=100",
        )
    }

    @Test
    fun `GET diseases with invalid infectious returns zero results`() = testApplication {
        application { module() }

        assertZeroResultsForInvalidFilter(
            client = client,
            url = "/v1/diseases?infectious=invalid&page_size=100",
        )
    }

    @Test
    fun `GET diseases with invalid has_pharmacological_treatment returns zero results`() = testApplication {
        application { module() }

        assertZeroResultsForInvalidFilter(
            client = client,
            url = "/v1/diseases?has_pharmacological_treatment=invalid&page_size=100",
        )
    }

    @Test
    fun `GET diseases with invalid has_severity_grading returns zero results`() = testApplication {
        application { module() }

        assertZeroResultsForInvalidFilter(
            client = client,
            url = "/v1/diseases?has_severity_grading=invalid&page_size=100",
        )
    }

    private suspend fun assertZeroResultsForInvalidFilter(client: HttpClient, url: String) {
        val response = client.get(urlString = url)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertEquals(
            expected = 0,
            actual = totalCount,
            message = "total_count must equal 0 when invalid filter value is provided: $url",
        )
        assertTrue(
            actual = items.isEmpty(),
            message = "items must be empty when invalid filter value is provided: $url",
        )
    }

    private fun Chronicity.declaredSerialName(): String =
        Chronicity.serializer().descriptor.getElementName(index = ordinal)

    private fun Icd10Chapter.declaredSerialName(): String =
        Icd10Chapter.serializer().descriptor.getElementName(index = ordinal)

    private fun MedicalDepartment.declaredSerialName(): String =
        MedicalDepartment.serializer().descriptor.getElementName(index = ordinal)
}
