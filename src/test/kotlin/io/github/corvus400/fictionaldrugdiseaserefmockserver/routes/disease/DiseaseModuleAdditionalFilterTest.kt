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
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "symptom_keyword=発熱 filter must return HTTP 200",
        )
        val totalCount = response.totalCount()
        assertTrue(
            actual = totalCount in 1 until DEFAULT_DISEASE_COUNT,
            message = "symptom_keyword=発熱 must filter total_count to 1..<80, got $totalCount",
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
    fun `GET diseases with onset_pattern ACUTE and CHRONIC returns OR-filtered items`() = testApplication {
        application { module() }

        val singleTotal = client.get(urlString = "/v1/diseases?onset_pattern=ACUTE&page_size=100").totalCount()
        val orResponse = client.get(
            urlString = "/v1/diseases?onset_pattern=ACUTE&onset_pattern=CHRONIC&page_size=100",
        )

        val orTotal = orResponse.totalCount()
        val violations = listOfNotNull(
            "OR status must be 200 OK but was ${orResponse.status}".takeUnless {
                orResponse.status == HttpStatusCode.OK
            },
            "onset_pattern=ACUTE must filter total_count to 1..<80, got $singleTotal"
                .takeUnless { singleTotal in 1 until DEFAULT_DISEASE_COUNT },
            "onset_pattern=ACUTE&onset_pattern=CHRONIC must filter total_count to 1..<80, got $orTotal"
                .takeUnless { orTotal in 1 until DEFAULT_DISEASE_COUNT },
            "OR-filtered total_count=$orTotal must be >= single ACUTE total_count=$singleTotal"
                .takeUnless { orTotal >= singleTotal },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "onset_pattern OR filter violations: $violations",
        )
    }

    @Test
    fun `GET diseases with exam_category IMAGING returns filtered items`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?exam_category=IMAGING&page_size=100")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "exam_category=IMAGING filter must return HTTP 200",
        )
        val totalCount = response.totalCount()
        assertTrue(
            actual = totalCount in 1 until DEFAULT_DISEASE_COUNT,
            message = "exam_category=IMAGING must filter total_count to 1..<80, got $totalCount",
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
                expected = HttpStatusCode.OK,
                actual = response.status,
                message = "has_pharmacological_treatment=true filter must return HTTP 200",
            )
            val totalCount = response.totalCount()
            assertTrue(
                actual = totalCount in 1 until DEFAULT_DISEASE_COUNT,
                message = "has_pharmacological_treatment=true must filter total_count to 1..<80, got $totalCount",
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
    fun `GET diseases with has_pharmacological_treatment false returns items with empty pharmacological`() =
        testApplication {
            application { module() }

            val falseResponse = client.get(
                urlString = "/v1/diseases?has_pharmacological_treatment=false&page_size=100",
            )
            val trueTotal = client.get(
                urlString = "/v1/diseases?has_pharmacological_treatment=true&page_size=100",
            ).totalCount()

            val falseTotal = falseResponse.totalCount()
            val violations = listOfNotNull(
                "false filter status must be 200 OK but was ${falseResponse.status}".takeUnless {
                    falseResponse.status == HttpStatusCode.OK
                },
                "has_pharmacological_treatment=false must filter total_count to 1..<80, got $falseTotal"
                    .takeUnless { falseTotal in 1 until DEFAULT_DISEASE_COUNT },
                "true and false pharmacological filters must partition the 80 default diseases"
                    .takeUnless { trueTotal + falseTotal == DEFAULT_DISEASE_COUNT },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "has_pharmacological_treatment=false violations: $violations",
            )
        }

    @Test
    fun `GET diseases with has_severity_grading true returns items with non-null severityGrading`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?has_severity_grading=true&page_size=100")

            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status,
                message = "has_severity_grading=true filter must return HTTP 200",
            )
            val totalCount = response.totalCount()
            assertTrue(
                actual = totalCount in 1 until DEFAULT_DISEASE_COUNT,
                message = "has_severity_grading=true must filter total_count to 1..<80, got $totalCount",
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
    fun `GET diseases with symptom_keyword X and has_severity_grading true returns AND intersection`() =
        testApplication {
            application { module() }

            val keyword = "発熱".encodeURLParameter()
            val symptomTotal = client.get(
                urlString = "/v1/diseases?symptom_keyword=$keyword&page_size=100",
            ).totalCount()
            val severityTotal = client.get(
                urlString = "/v1/diseases?has_severity_grading=true&page_size=100",
            ).totalCount()
            val andResponse = client.get(
                urlString = "/v1/diseases?symptom_keyword=$keyword&has_severity_grading=true&page_size=100",
            )

            val andTotal = andResponse.totalCount()
            val singleMin = minOf(a = symptomTotal, b = severityTotal)
            val violations = listOfNotNull(
                "AND status must be 200 OK but was ${andResponse.status}".takeUnless {
                    andResponse.status == HttpStatusCode.OK
                },
                "AND total_count=$andTotal must be <= min(symptom=$symptomTotal, severity=$severityTotal)"
                    .takeUnless { andTotal <= singleMin },
                (
                    "AND total_count=$andTotal must be smaller than at least one single filter " +
                        "(symptom=$symptomTotal, severity=$severityTotal)"
                    ).takeUnless { andTotal < symptomTotal || andTotal < severityTotal },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "symptom_keyword and severity AND violations: $violations",
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

    private companion object {
        const val DEFAULT_DISEASE_COUNT = 80
    }
}
