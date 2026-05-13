package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs category_atc=A returns 200 OK`() = testApplication {
        application { module() }

        val response = client.get(ATC_A_URL)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
    }

    @Test
    fun `GET drugs category_atc=A returns total_count 20`() = testApplication {
        application { module() }

        val totalCount = client.get(ATC_A_URL).totalCount()

        assertEquals(expected = 20, actual = totalCount, message = "ATC A 群は 120 件中 20 件")
    }

    @Test
    fun `GET drugs category_atc=A returns all ATC-A items`() = testApplication {
        application { module() }

        val response = client.get(ATC_A_URL)

        assertEquals(
            expected = null,
            actual = firstDetailFieldViolation(
                client = client,
                response = response,
                fieldName = "atc_code",
                predicateDescription = "start with 'A'",
            ) { value -> value?.startsWith(prefix = "A") == true },
        )
    }

    @Test
    fun `GET drugs regulatory_class=prescription_required returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(PRESCRIPTION_REQUIRED_URL)

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET drugs regulatory_class=prescription_required filters total_count to strict subset`() =
        testApplication {
            application { module() }

            val totalCount = client.get(PRESCRIPTION_REQUIRED_URL).totalCount()

            assertTrue(
                actual = totalCount in 1 until DEFAULT_DRUG_COUNT,
                message = "total_count=$totalCount must be 1..<120 for regulatory_class=prescription_required",
            )
        }

    @Test
    fun `GET drugs regulatory_class=prescription_required returns non-empty items`() =
        testApplication {
            application { module() }

            val itemsSize = client.get(PRESCRIPTION_REQUIRED_URL).items().size

            assertTrue(actual = itemsSize > 0, message = "filtered items must be non-empty")
        }

    @Test
    fun `GET drugs regulatory_class=prescription_required returns only matching items`() =
        testApplication {
            application { module() }

            val items = client.get(PRESCRIPTION_REQUIRED_URL).items()
            val firstViolation = items.firstNotNullOfOrNull { item ->
                val id = item.jsonObject["id"]?.jsonPrimitive?.content
                val values = item.jsonObject["regulatory_class"]?.jsonArray.orEmpty()
                    .map { element -> element.jsonPrimitive.content }
                "item id=$id has regulatory_class=$values; must contain 'prescription_required'"
                    .takeUnless { values.contains(element = "prescription_required") }
            }

            assertEquals(expected = null, actual = firstViolation)
        }

    @Test
    fun `GET drugs route=oral returns 200 OK`() = testApplication {
        application { module() }

        val response = client.get(ORAL_ROUTE_URL)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
    }

    @Test
    fun `GET drugs route=oral filters total_count to strict subset`() = testApplication {
        application { module() }

        val totalCount = client.get(ORAL_ROUTE_URL).totalCount()

        assertTrue(actual = totalCount in 1 until DEFAULT_DRUG_COUNT, message = "total_count=$totalCount")
    }

    @Test
    fun `GET drugs route=oral returns non-empty items`() = testApplication {
        application { module() }

        val itemsSize = client.get(ORAL_ROUTE_URL).items().size

        assertTrue(actual = itemsSize > 0, message = "filtered items must be non-empty for route=oral")
    }

    @Test
    fun `GET drugs route=oral returns only oral detail items`() = testApplication {
        application { module() }

        val response = client.get(ORAL_ROUTE_URL)

        assertEquals(
            expected = null,
            actual = firstDetailFieldViolation(
                client = client,
                response = response,
                fieldName = "route_of_administration",
                predicateDescription = "equal 'oral'",
            ) { value -> value == "oral" },
        )
    }

    @Test
    fun `GET drugs dosage_form=tablet returns 200 OK`() = testApplication {
        application { module() }

        val response = client.get(TABLET_DOSAGE_FORM_URL)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
    }

    @Test
    fun `GET drugs dosage_form=tablet filters total_count to strict subset`() = testApplication {
        application { module() }

        val totalCount = client.get(TABLET_DOSAGE_FORM_URL).totalCount()

        assertTrue(actual = totalCount in 1 until DEFAULT_DRUG_COUNT, message = "total_count=$totalCount")
    }

    @Test
    fun `GET drugs dosage_form=tablet returns non-empty items`() = testApplication {
        application { module() }

        val itemsSize = client.get(TABLET_DOSAGE_FORM_URL).items().size

        assertTrue(actual = itemsSize > 0, message = "filtered items must be non-empty for dosage_form=tablet")
    }

    @Test
    fun `GET drugs dosage_form=tablet returns only tablet detail items`() = testApplication {
        application { module() }

        val response = client.get(TABLET_DOSAGE_FORM_URL)

        assertEquals(
            expected = null,
            actual = firstDetailFieldViolation(
                client = client,
                response = response,
                fieldName = "dosage_form",
                predicateDescription = "equal 'tablet'",
            ) { value -> value == "tablet" },
        )
    }

    @Test
    fun `GET drugs category_atc=A and dosage_form=tablet returns 200 OK`() = testApplication {
        application { module() }

        val response = client.get(ATC_A_AND_TABLET_URL)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
    }

    @Test
    fun `GET drugs category_atc=A and dosage_form=tablet total_count is no larger than either single filter`() =
        testApplication {
            application { module() }

            val atcTotal = client.get(ATC_A_URL).totalCount()
            val formTotal = client.get(TABLET_DOSAGE_FORM_URL).totalCount()
            val intersectionTotal = client.get(ATC_A_AND_TABLET_URL).totalCount()

            assertTrue(
                actual = intersectionTotal <= atcTotal && intersectionTotal <= formTotal,
                message = "intersection total=$intersectionTotal must be <= min(atc=$atcTotal, form=$formTotal)",
            )
        }

    @Test
    fun `GET drugs category_atc=A and dosage_form=tablet returns only ATC-A detail items`() = testApplication {
        application { module() }

        val response = client.get(ATC_A_AND_TABLET_URL)

        assertEquals(
            expected = null,
            actual = firstDetailFieldViolation(
                client = client,
                response = response,
                fieldName = "atc_code",
                predicateDescription = "start with 'A'",
            ) { value -> value?.startsWith(prefix = "A") == true },
        )
    }

    @Test
    fun `GET drugs category_atc=A and dosage_form=tablet returns only tablet detail items`() = testApplication {
        application { module() }

        val response = client.get(ATC_A_AND_TABLET_URL)

        assertEquals(
            expected = null,
            actual = firstDetailFieldViolation(
                client = client,
                response = response,
                fieldName = "dosage_form",
                predicateDescription = "equal 'tablet'",
            ) { value -> value == "tablet" },
        )
    }

    private suspend fun HttpResponse.totalCount(): Int =
        json.parseToJsonElement(string = bodyAsText()).jsonObject["total_count"]
            ?.jsonPrimitive
            ?.content
            ?.toIntOrNull()
            ?: error("response must include numeric total_count")

    private suspend fun HttpResponse.items(): JsonArray =
        json.parseToJsonElement(string = bodyAsText()).jsonObject["items"]
            ?.jsonArray
            ?: error("response must include items array")

    private suspend fun firstDetailFieldViolation(
        client: HttpClient,
        response: HttpResponse,
        fieldName: String,
        predicateDescription: String,
        predicate: (String?) -> Boolean,
    ): String? =
        response.items().firstNotNullOfOrNull { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
                ?: return@firstNotNullOfOrNull "item must expose id"
            val detailResponse = client.get("/v1/drugs/$id")
            if (detailResponse.status != HttpStatusCode.OK) {
                return@firstNotNullOfOrNull "detail GET must succeed for id=$id but was ${detailResponse.status}"
            }
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val actualValue = detail[fieldName]?.jsonPrimitive?.content
            "item id=$id has $fieldName=$actualValue; must $predicateDescription"
                .takeUnless { predicate(actualValue) }
        }

    private companion object {
        const val DEFAULT_DRUG_COUNT = 120
        const val ATC_A_URL = "/v1/drugs?category_atc=A&page_size=100"
        const val PRESCRIPTION_REQUIRED_URL = "/v1/drugs?regulatory_class=prescription_required&page_size=100"
        const val ORAL_ROUTE_URL = "/v1/drugs?route=oral&page_size=100"
        const val TABLET_DOSAGE_FORM_URL = "/v1/drugs?dosage_form=tablet&page_size=100"
        const val ATC_A_AND_TABLET_URL = "/v1/drugs?category_atc=A&dosage_form=tablet&page_size=100"
    }
}
