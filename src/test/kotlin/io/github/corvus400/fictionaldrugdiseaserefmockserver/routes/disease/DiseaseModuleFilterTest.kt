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
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with icd10_chapter=chapter_i returns items all having icd10_chapter==chapter_i`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?icd10_chapter=chapter_i")

            assertEquals(
                expected = DiseaseFilterSnapshot(
                    status = HttpStatusCode.OK,
                    hasItems = true,
                    totalCountMatches = true,
                    itemViolations = emptyList(),
                ),
                actual = fieldEqualsSnapshot(
                    response = response,
                    fieldName = "icd10_chapter",
                    expectedValue = Icd10Chapter.CHAPTER_I.declaredSerialName(),
                    expectedTotalCount = 6,
                ),
                message = "icd10_chapter=chapter_i must return only CHAPTER_I items",
            )
        }

    @Test
    fun `GET diseases with icd10_chapter=chapter_ii returns items all having icd10_chapter==chapter_ii`() =
        testApplication {
            application { module() }

            val response = client.get(urlString = "/v1/diseases?icd10_chapter=chapter_ii")

            assertEquals(
                expected = DiseaseFilterSnapshot(
                    status = HttpStatusCode.OK,
                    hasItems = true,
                    totalCountMatches = true,
                    itemViolations = emptyList(),
                ),
                actual = fieldEqualsSnapshot(
                    response = response,
                    fieldName = "icd10_chapter",
                    expectedValue = Icd10Chapter.CHAPTER_II.declaredSerialName(),
                    expectedTotalCount = 6,
                ),
                message = "icd10_chapter=chapter_ii must return only CHAPTER_II items",
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

            assertEquals(
                expected = DiseaseFilterSnapshot(
                    status = HttpStatusCode.OK,
                    hasItems = true,
                    totalCountMatches = true,
                    itemViolations = emptyList(),
                ),
                actual = departmentSnapshot(response = response, expectedDepartment = expectedSerialName),
                message = "department=PSYCHIATRY must return only items containing PSYCHIATRY",
            )
        }

    @Test
    fun `GET diseases with chronicity=ACUTE returns items whose chronicity == value`() = testApplication {
        application { module() }

        val expectedSerialName = Chronicity.ACUTE.declaredSerialName()
        val encodedChronicity = expectedSerialName.encodeURLParameter()
        val response = client.get(
            urlString = "/v1/diseases?chronicity=$encodedChronicity&page_size=100",
        )

        assertEquals(
            expected = DiseaseFilterSnapshot(
                status = HttpStatusCode.OK,
                hasItems = true,
                totalCountMatches = true,
                itemViolations = emptyList(),
            ),
            actual = fieldEqualsSnapshot(
                response = response,
                fieldName = "chronicity",
                expectedValue = expectedSerialName,
            ),
            message = "chronicity=ACUTE must return only ACUTE items",
        )
    }

    @Test
    fun `GET diseases with infectious=true returns items whose infectious == true`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?infectious=true&page_size=100")

        assertEquals(
            expected = DiseaseFilterSnapshot(
                status = HttpStatusCode.OK,
                hasItems = true,
                totalCountMatches = true,
                itemViolations = emptyList(),
            ),
            actual = booleanFieldSnapshot(response = response, fieldName = "infectious", expectedValue = true),
            message = "infectious=true must return only infectious items",
        )
    }

    @Test
    fun `GET diseases with icd10_chapter=chapter_i and infectious=true returns intersection (AND filter)`() =
        testApplication {
            application { module() }

            val response = client.get(
                urlString = "/v1/diseases?icd10_chapter=chapter_i&infectious=true&page_size=100",
            )

            val expectedChapter = Icd10Chapter.CHAPTER_I.declaredSerialName()
            assertEquals(
                expected = DiseaseFilterSnapshot(
                    status = HttpStatusCode.OK,
                    hasItems = true,
                    totalCountMatches = true,
                    itemViolations = emptyList(),
                ),
                actual = andFieldSnapshot(
                    response = response,
                    expectedChapter = expectedChapter,
                    expectedInfectious = true,
                    expectedTotalCount = 6,
                ),
                message = "icd10_chapter=chapter_i&infectious=true must return the AND intersection",
            )
        }

    @Test
    fun `GET diseases with legacy roman icd10_chapter=I returns total_count zero`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?icd10_chapter=I&page_size=100")

        assertEquals(
            expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
            actual = emptyEnvelopeSnapshot(response = response),
            message = "legacy roman icd10_chapter=I query must return HTTP 200 with empty results",
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

        assertEquals(
            expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
            actual = emptyEnvelopeSnapshot(response = response),
            message = "invalid filter value must return an empty envelope: $url",
        )
    }

    private suspend fun fieldEqualsSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        fieldName: String,
        expectedValue: String,
        expectedTotalCount: Int? = null,
    ): DiseaseFilterSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        return DiseaseFilterSnapshot(
            status = response.status,
            hasItems = items?.isNotEmpty() == true,
            totalCountMatches = expectedTotalCount == null || totalCount == expectedTotalCount,
            itemViolations = items.orEmpty().mapIndexedNotNull { index, item ->
                val actual = item.jsonObject[fieldName]?.jsonPrimitive?.content
                "items[$index].$fieldName must equal '$expectedValue' but was '$actual' (item=${item.jsonObject})"
                    .takeUnless { actual == expectedValue }
            },
        )
    }

    private suspend fun departmentSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        expectedDepartment: String,
    ): DiseaseFilterSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        return DiseaseFilterSnapshot(
            status = response.status,
            hasItems = items?.isNotEmpty() == true,
            totalCountMatches = true,
            itemViolations = items.orEmpty().mapIndexedNotNull { index, item ->
                val departments = item.jsonObject["medical_department"]?.jsonArray
                val serialNames = departments.orEmpty().map { it.jsonPrimitive.content }
                "items[$index].medical_department must contain '$expectedDepartment' (item=${item.jsonObject})"
                    .takeUnless { expectedDepartment in serialNames }
            },
        )
    }

    private suspend fun booleanFieldSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        fieldName: String,
        expectedValue: Boolean,
    ): DiseaseFilterSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        return DiseaseFilterSnapshot(
            status = response.status,
            hasItems = items?.isNotEmpty() == true,
            totalCountMatches = true,
            itemViolations = items.orEmpty().mapIndexedNotNull { index, item ->
                val actual = item.jsonObject[fieldName]?.jsonPrimitive?.content?.toBooleanStrictOrNull()
                "items[$index].$fieldName must equal $expectedValue but was $actual (item=${item.jsonObject})"
                    .takeUnless { actual == expectedValue }
            },
        )
    }

    private suspend fun andFieldSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        expectedChapter: String,
        expectedInfectious: Boolean,
        expectedTotalCount: Int,
    ): DiseaseFilterSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        return DiseaseFilterSnapshot(
            status = response.status,
            hasItems = items?.isNotEmpty() == true,
            totalCountMatches = totalCount == expectedTotalCount,
            itemViolations = items.orEmpty().flatMapIndexed { index, item ->
                val obj = item.jsonObject
                listOfNotNull(
                    "items[$index].icd10_chapter must equal '$expectedChapter' (item=$obj)"
                        .takeUnless { obj["icd10_chapter"]?.jsonPrimitive?.content == expectedChapter },
                    "items[$index].infectious must equal $expectedInfectious (item=$obj)"
                        .takeUnless {
                            obj["infectious"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() == expectedInfectious
                        },
                )
            },
        )
    }

    private suspend fun emptyEnvelopeSnapshot(
        response: io.ktor.client.statement.HttpResponse,
    ): EmptyEnvelopeSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        return EmptyEnvelopeSnapshot(
            status = response.status,
            totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull(),
            itemsSize = body["items"]?.jsonArray?.size,
        )
    }

    private fun Chronicity.declaredSerialName(): String =
        Chronicity.serializer().descriptor.getElementName(index = ordinal)

    private fun Icd10Chapter.declaredSerialName(): String =
        Icd10Chapter.serializer().descriptor.getElementName(index = ordinal)

    private fun MedicalDepartment.declaredSerialName(): String =
        MedicalDepartment.serializer().descriptor.getElementName(index = ordinal)

    private data class DiseaseFilterSnapshot(
        val status: HttpStatusCode,
        val hasItems: Boolean,
        val totalCountMatches: Boolean,
        val itemViolations: List<String>,
    )

    private data class EmptyEnvelopeSnapshot(
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )
}
