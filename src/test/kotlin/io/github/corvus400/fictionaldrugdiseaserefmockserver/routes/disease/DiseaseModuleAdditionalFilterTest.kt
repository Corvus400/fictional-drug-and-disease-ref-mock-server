package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLParameter
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseModuleAdditionalFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with symptom_keyword X returns filtered items`() = testApplication {
        application { module() }

        val keyword = "発熱".encodeURLParameter()
        val response = client.get(urlString = "/v1/diseases?symptom_keyword=$keyword&page_size=100")

        assertEquals(
            expected = CountFilterSnapshot(status = HttpStatusCode.OK, totalCountInFilteredRange = true),
            actual = countFilterSnapshot(response = response),
            message = "symptom_keyword=発熱 must filter total_count to 1..<80",
        )
    }

    @Test
    fun `GET diseases under empty scenario with symptom_keyword X returns HTTP 200 and empty items`() =
        testApplication {
            application { module() }

            val keyword = "発熱".encodeURLParameter()
            val response = client.get(urlString = "/v1/diseases?symptom_keyword=$keyword") {
                header(key = "X-Mock-Scenario", value = "empty")
            }

            assertOkEmptyItems(response = response)
        }

    @Test
    fun `GET diseases with onset_pattern ACUTE and CHRONIC returns 200 OK`() = testApplication {
        application { module() }

        val response = client.get(urlString = ONSET_PATTERN_ACUTE_OR_CHRONIC_URL)

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
    }

    @Test
    fun `GET diseases with onset_pattern ACUTE filters total_count to strict subset`() = testApplication {
        application { module() }

        val singleTotal = client.get(urlString = ONSET_PATTERN_ACUTE_URL).totalCount()

        assertTrue(
            actual = singleTotal in 1 until DEFAULT_DISEASE_COUNT,
            message = "onset_pattern=ACUTE must filter total_count to 1..<80, got $singleTotal",
        )
    }

    @Test
    fun `GET diseases with onset_pattern ACUTE and CHRONIC filters total_count to strict subset`() = testApplication {
        application { module() }

        val orTotal = client.get(urlString = ONSET_PATTERN_ACUTE_OR_CHRONIC_URL).totalCount()

        assertTrue(
            actual = orTotal in 1 until DEFAULT_DISEASE_COUNT,
            message = "onset_pattern=ACUTE&onset_pattern=CHRONIC must filter total_count to 1..<80, got $orTotal",
        )
    }

    @Test
    fun `GET diseases with onset_pattern ACUTE and CHRONIC total_count is at least ACUTE only`() = testApplication {
        application { module() }

        val singleTotal = client.get(urlString = ONSET_PATTERN_ACUTE_URL).totalCount()
        val orTotal = client.get(urlString = ONSET_PATTERN_ACUTE_OR_CHRONIC_URL).totalCount()

        assertTrue(
            actual = orTotal >= singleTotal,
            message = "OR-filtered total_count=$orTotal must be >= single ACUTE total_count=$singleTotal",
        )
    }

    @Test
    fun `GET diseases with exam_category IMAGING returns filtered items`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?exam_category=IMAGING&page_size=100")

        assertEquals(
            expected = CountFilterSnapshot(status = HttpStatusCode.OK, totalCountInFilteredRange = true),
            actual = countFilterSnapshot(response = response),
            message = "exam_category=IMAGING must filter total_count to 1..<80",
        )
    }

    @Test
    fun `GET diseases with has_pharmacological_treatment true returns items with non-empty pharmacological`() =
        testApplication {
            application { module() }

            val response = client.get(
                urlString = "/v1/diseases?has_pharmacological_treatment=true&page_size=100",
            )

            assertEquals(
                expected = CountFilterSnapshot(status = HttpStatusCode.OK, totalCountInFilteredRange = true),
                actual = countFilterSnapshot(response = response),
                message = "has_pharmacological_treatment=true must filter total_count to 1..<80",
            )
        }

    @Test
    fun `GET diseases under empty scenario with has_pharmacological_treatment true returns HTTP 200 and empty items`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?has_pharmacological_treatment=true") {
                header(key = "X-Mock-Scenario", value = "empty")
            }

            assertOkEmptyItems(response = response)
        }

    @Test
    fun `GET diseases with has_pharmacological_treatment false returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = HAS_PHARMACOLOGICAL_TREATMENT_FALSE_URL)

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET diseases with has_pharmacological_treatment false filters total_count to strict subset`() =
        testApplication {
            application { module() }

            val falseTotal = client.get(urlString = HAS_PHARMACOLOGICAL_TREATMENT_FALSE_URL).totalCount()

            assertTrue(
                actual = falseTotal in 1 until DEFAULT_DISEASE_COUNT,
                message = "has_pharmacological_treatment=false must filter total_count to 1..<80, got $falseTotal",
            )
        }

    @Test
    fun `GET diseases with has_pharmacological_treatment true and false partition default diseases`() =
        testApplication {
            application { module() }

            val trueTotal = client.get(urlString = HAS_PHARMACOLOGICAL_TREATMENT_TRUE_URL).totalCount()
            val falseTotal = client.get(urlString = HAS_PHARMACOLOGICAL_TREATMENT_FALSE_URL).totalCount()

            assertEquals(
                expected = DEFAULT_DISEASE_COUNT,
                actual = trueTotal + falseTotal,
                message = "true and false pharmacological filters must partition the 80 default diseases",
            )
        }

    @Test
    fun `GET diseases with has_severity_grading true returns items with non-null severityGrading`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?has_severity_grading=true&page_size=100")

            assertEquals(
                expected = CountFilterSnapshot(status = HttpStatusCode.OK, totalCountInFilteredRange = true),
                actual = countFilterSnapshot(response = response),
                message = "has_severity_grading=true must filter total_count to 1..<80",
            )
        }

    @Test
    fun `GET diseases under empty scenario with has_severity_grading true returns HTTP 200 and empty items`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?has_severity_grading=true") {
                header(key = "X-Mock-Scenario", value = "empty")
            }

            assertOkEmptyItems(response = response)
        }

    @Test
    fun `GET diseases with symptom_keyword X and has_severity_grading true returns 200 OK`() =
        testApplication {
            application { module() }

            val keyword = "発熱".encodeURLParameter()
            val response = client.get(urlString = symptomAndSeverityUrl(keyword = keyword))

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET diseases with symptom_keyword X and severity total_count is no larger than single filters`() =
        testApplication {
            application { module() }

            val keyword = "発熱".encodeURLParameter()
            val symptomTotal = client.get(urlString = symptomKeywordUrl(keyword = keyword)).totalCount()
            val severityTotal = client.get(urlString = HAS_SEVERITY_GRADING_TRUE_URL).totalCount()
            val andTotal = client.get(urlString = symptomAndSeverityUrl(keyword = keyword)).totalCount()
            val singleMin = minOf(a = symptomTotal, b = severityTotal)

            assertTrue(
                actual = andTotal <= singleMin,
                message = "AND total_count=$andTotal must be <= min(symptom=$symptomTotal, severity=$severityTotal)",
            )
        }

    @Test
    fun `GET diseases with symptom_keyword X and severity total_count is smaller than a single filter`() =
        testApplication {
            application { module() }

            val keyword = "発熱".encodeURLParameter()
            val symptomTotal = client.get(urlString = symptomKeywordUrl(keyword = keyword)).totalCount()
            val severityTotal = client.get(urlString = HAS_SEVERITY_GRADING_TRUE_URL).totalCount()
            val andTotal = client.get(urlString = symptomAndSeverityUrl(keyword = keyword)).totalCount()

            assertTrue(
                actual = andTotal < symptomTotal || andTotal < severityTotal,
                message = "AND total_count=$andTotal must be smaller than at least one single filter " +
                    "(symptom=$symptomTotal, severity=$severityTotal)",
            )
        }

    @Test
    fun `GET diseases with onset_pattern INVALID returns HTTP 400 and INVALID_ONSET_PATTERN error`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?onset_pattern=INVALID")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val message = body["message"]?.jsonPrimitive?.content

            assertEquals(
                expected = ErrorSnapshot(
                    status = HttpStatusCode.BadRequest,
                    code = "INVALID_ONSET_PATTERN",
                    messageMentionsInvalid = true,
                ),
                actual = ErrorSnapshot(
                    status = response.status,
                    code = body["code"]?.jsonPrimitive?.content,
                    messageMentionsInvalid = message?.contains(other = "INVALID") == true,
                ),
                message = "ErrorResponse message=$message must mention rejected value INVALID",
            )
        }

    @Test
    fun `GET diseases with exam_category INVALID returns HTTP 400 and INVALID_EXAM_CATEGORY error`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?exam_category=INVALID")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val message = body["message"]?.jsonPrimitive?.content
            assertEquals(
                expected = ErrorSnapshot(
                    status = HttpStatusCode.BadRequest,
                    code = "INVALID_EXAM_CATEGORY",
                    messageMentionsInvalid = true,
                ),
                actual = ErrorSnapshot(
                    status = response.status,
                    code = body["code"]?.jsonPrimitive?.content,
                    messageMentionsInvalid = message?.contains(other = "INVALID") == true,
                ),
            )
        }

    private suspend fun io.ktor.client.statement.HttpResponse.totalCount(): Int {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertNotNull(actual = totalCount, message = "response must include numeric total_count")
        return totalCount
    }

    private suspend fun countFilterSnapshot(
        response: io.ktor.client.statement.HttpResponse,
    ): CountFilterSnapshot {
        val totalCount = response.totalCount()
        return CountFilterSnapshot(
            status = response.status,
            totalCountInFilteredRange = totalCount in 1 until DEFAULT_DISEASE_COUNT,
        )
    }

    private suspend fun assertOkEmptyItems(response: io.ktor.client.statement.HttpResponse) {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertEquals(
            expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
            actual = EmptyEnvelopeSnapshot(
                status = response.status,
                totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull(),
                itemsSize = items?.size,
            ),
        )
    }

    private data class EmptyEnvelopeSnapshot(
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )

    private data class ErrorSnapshot(
        val status: HttpStatusCode,
        val code: String?,
        val messageMentionsInvalid: Boolean,
    )

    private data class CountFilterSnapshot(
        val status: HttpStatusCode,
        val totalCountInFilteredRange: Boolean,
    )

    private companion object {
        const val DEFAULT_DISEASE_COUNT = 80
        const val ONSET_PATTERN_ACUTE_URL = "/v1/diseases?onset_pattern=ACUTE&page_size=100"
        const val ONSET_PATTERN_ACUTE_OR_CHRONIC_URL =
            "/v1/diseases?onset_pattern=ACUTE&onset_pattern=CHRONIC&page_size=100"
        const val HAS_PHARMACOLOGICAL_TREATMENT_TRUE_URL =
            "/v1/diseases?has_pharmacological_treatment=true&page_size=100"
        const val HAS_PHARMACOLOGICAL_TREATMENT_FALSE_URL =
            "/v1/diseases?has_pharmacological_treatment=false&page_size=100"
        const val HAS_SEVERITY_GRADING_TRUE_URL = "/v1/diseases?has_severity_grading=true&page_size=100"
    }

    private fun symptomKeywordUrl(keyword: String): String =
        "/v1/diseases?symptom_keyword=$keyword&page_size=100"

    private fun symptomAndSeverityUrl(keyword: String): String =
        "/v1/diseases?symptom_keyword=$keyword&has_severity_grading=true&page_size=100"
}
