package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertTrue

class DrugModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs category_atc=A returns total_count=20 with all items atc_code starting with A`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?category_atc=A&page_size=100")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        val violations = mutableListOf<String>()
        violations += "status must be 200 OK but was ${response.status}".unless(response.status == HttpStatusCode.OK)
        violations += "ATC A 群は 120 件中 20 件".unless(
            body["total_count"]?.jsonPrimitive?.content?.toInt() == 20,
        )
        violations += "items.size must be 20 when page_size=100 covers all ATC-A drugs".unless(items?.size == 20)
        items.orEmpty().forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            if (id == null) {
                violations += "item must expose id"
                return@forEach
            }
            val detailResponse = client.get("/v1/drugs/$id")
            violations += "detail GET must succeed for id=$id but was ${detailResponse.status}"
                .unless(detailResponse.status == HttpStatusCode.OK)
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val atcCode = detail["atc_code"]?.jsonPrimitive?.content
            violations += "drug $id must expose atc_code".unless(atcCode != null)
            violations += "item id=$id has atc_code=$atcCode; must start with 'A' under category_atc=A filter"
                .unless(atcCode?.startsWith(prefix = "A") == true)
        }
        assertTrue(actual = violations.isEmpty(), message = "category_atc=A violations: $violations")
    }

    @Test
    fun `GET drugs regulatory_class= returns items all having regulatory_class containing the value`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?regulatory_class=prescription_required&page_size=100")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            val items = body["items"]?.jsonArray
            val violations = mutableListOf<String>()
            violations +=
                "status must be 200 OK but was ${response.status}".unless(response.status == HttpStatusCode.OK)
            violations += "response must include total_count".unless(totalCount != null)
            violations += "total_count=$totalCount must be 1..<120 for regulatory_class=prescription_required"
                .unless(totalCount != null && totalCount in 1 until 120)
            violations += "response must include items array".unless(items != null)
            violations += "filtered items must be non-empty for regulatory_class=prescription_required"
                .unless(items?.isNotEmpty() == true)
            items.orEmpty().forEach { item ->
                val id = item.jsonObject["id"]?.jsonPrimitive?.content
                val regClasses = item.jsonObject["regulatory_class"]?.jsonArray
                val values = regClasses.orEmpty().map { element -> element.jsonPrimitive.content }
                violations += "item must expose id".unless(id != null)
                violations += "item id=$id must expose regulatory_class list".unless(regClasses != null)
                violations +=
                    "item id=$id has regulatory_class=$values; must contain 'prescription_required' under filter"
                        .unless(values.contains(element = "prescription_required"))
            }
            assertTrue(actual = violations.isEmpty(), message = "regulatory_class violations: $violations")
        }

    @Test
    fun `GET drugs route= returns items whose routeOfAdministration serial name equals value`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?route=oral&page_size=100")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        val items = body["items"]?.jsonArray
        val violations = mutableListOf<String>()
        violations += "status must be 200 OK but was ${response.status}".unless(response.status == HttpStatusCode.OK)
        violations += "response must include total_count".unless(totalCount != null)
        violations += "total_count=$totalCount must be 1..<120 for route=oral"
            .unless(totalCount != null && totalCount in 1 until 120)
        violations += "response must include items array".unless(items != null)
        violations += "filtered items must be non-empty for route=oral".unless(items?.isNotEmpty() == true)
        items.orEmpty().forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            if (id == null) {
                violations += "item must expose id"
                return@forEach
            }
            val detailResponse = client.get("/v1/drugs/$id")
            violations += "detail GET must succeed for id=$id but was ${detailResponse.status}"
                .unless(detailResponse.status == HttpStatusCode.OK)
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val routeValue = detail["route_of_administration"]?.jsonPrimitive?.content
            violations += "item id=$id has route_of_administration=$routeValue; must be 'oral' under route=oral filter"
                .unless(routeValue == "oral")
        }
        assertTrue(actual = violations.isEmpty(), message = "route=oral violations: $violations")
    }

    @Test
    fun `GET drugs dosage_form= returns items whose dosageForm serial name equals value`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?dosage_form=tablet&page_size=100")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        val items = body["items"]?.jsonArray
        val violations = mutableListOf<String>()
        violations += "status must be 200 OK but was ${response.status}".unless(response.status == HttpStatusCode.OK)
        violations += "response must include total_count".unless(totalCount != null)
        violations += "total_count=$totalCount must be 1..<120 for dosage_form=tablet"
            .unless(totalCount != null && totalCount in 1 until 120)
        violations += "response must include items array".unless(items != null)
        violations += "filtered items must be non-empty for dosage_form=tablet".unless(items?.isNotEmpty() == true)
        items.orEmpty().forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            if (id == null) {
                violations += "item must expose id"
                return@forEach
            }
            val detailResponse = client.get("/v1/drugs/$id")
            violations += "detail GET must succeed for id=$id but was ${detailResponse.status}"
                .unless(detailResponse.status == HttpStatusCode.OK)
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val dosageFormValue = detail["dosage_form"]?.jsonPrimitive?.content
            violations +=
                "item id=$id has dosage_form=$dosageFormValue; must be 'tablet' under dosage_form=tablet filter"
                    .unless(dosageFormValue == "tablet")
        }
        assertTrue(actual = violations.isEmpty(), message = "dosage_form=tablet violations: $violations")
    }

    @Test
    fun `GET drugs category_atc=A and dosage_form=tablet returns intersection AND filter`() = testApplication {
        application { module() }

        val atcOnly = client.get("/v1/drugs?category_atc=A&page_size=100")
        val formOnly = client.get("/v1/drugs?dosage_form=tablet&page_size=100")
        val intersection = client.get("/v1/drugs?category_atc=A&dosage_form=tablet&page_size=100")

        val atcBody = json.parseToJsonElement(string = atcOnly.bodyAsText()).jsonObject
        val formBody = json.parseToJsonElement(string = formOnly.bodyAsText()).jsonObject
        val intersectionBody = json.parseToJsonElement(string = intersection.bodyAsText()).jsonObject

        val atcTotal = atcBody["total_count"]?.jsonPrimitive?.content?.toInt()
        val formTotal = formBody["total_count"]?.jsonPrimitive?.content?.toInt()
        val intersectionTotal = intersectionBody["total_count"]?.jsonPrimitive?.content?.toInt()
        val violations = mutableListOf<String>()
        violations += "intersection status must be 200 OK but was ${intersection.status}"
            .unless(intersection.status == HttpStatusCode.OK)
        violations += "atc-only response must include total_count".unless(atcTotal != null)
        violations += "form-only response must include total_count".unless(formTotal != null)
        violations += "intersection response must include total_count".unless(intersectionTotal != null)
        violations += "intersection total=$intersectionTotal must be <= min(atc=$atcTotal, form=$formTotal)"
            .unless(
                atcTotal != null &&
                    formTotal != null &&
                    intersectionTotal != null &&
                    intersectionTotal <= atcTotal &&
                    intersectionTotal <= formTotal,
            )

        val items = intersectionBody["items"]?.jsonArray
        violations += "intersection response must include items array".unless(items != null)
        items.orEmpty().forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            if (id == null) {
                violations += "item must expose id"
                return@forEach
            }
            val detailResponse = client.get("/v1/drugs/$id")
            violations += "detail GET must succeed for id=$id but was ${detailResponse.status}"
                .unless(detailResponse.status == HttpStatusCode.OK)
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val atcCode = detail["atc_code"]?.jsonPrimitive?.content
            val dosageFormValue = detail["dosage_form"]?.jsonPrimitive?.content
            violations += "item id=$id must expose atc_code".unless(atcCode != null)
            violations += "item id=$id has dosage_form=$dosageFormValue; must be 'tablet' under AND filter"
                .unless(dosageFormValue == "tablet")
            violations += "item id=$id has atc_code=$atcCode; must start with 'A' under AND filter"
                .unless(atcCode?.startsWith(prefix = "A") == true)
        }
        assertTrue(actual = violations.isEmpty(), message = "category_atc and dosage_form AND violations: $violations")
    }

    private fun String.unless(condition: Boolean): List<String> = if (condition) emptyList() else listOf(this)
}
