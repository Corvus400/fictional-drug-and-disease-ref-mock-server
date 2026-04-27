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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs category_atc=A returns total_count=20 with all items atc_code starting with A`() = testApplication {
        application { module() }

        val response = client.get("/drugs?category_atc=A&page_size=100")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = 20,
            actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "ATC A 群は 120 件中 20 件 (医薬品モデル仕様の組合せ数計算)",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = 20,
            actual = items.size,
            message = "items.size must be 20 when page_size=100 covers all ATC-A drugs",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val detailResponse = client.get("/drugs/$id")
            assertEquals(HttpStatusCode.OK, detailResponse.status, "detail GET must succeed for id=$id")
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val atcCode = detail["atc_code"]?.jsonPrimitive?.content
            assertNotNull(atcCode, "drug $id must expose atc_code")
            assertTrue(
                actual = atcCode.startsWith(prefix = "A"),
                message = "item id=$id has atc_code=$atcCode; must start with 'A' under category_atc=A filter",
            )
        }
    }

    @Test
    fun `GET drugs regulatory_class= returns items all having regulatory_class containing the value`() =
        testApplication {
            application { module() }

            val response = client.get("/drugs?regulatory_class=prescription_required&page_size=100")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(totalCount, "response must include total_count")
            assertTrue(
                actual = totalCount in 1 until 120,
                message = "total_count=$totalCount must be 1..<120 for regulatory_class=prescription_required",
            )
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response must include items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "filtered items must be non-empty for regulatory_class=prescription_required",
            )
            items.forEach { item ->
                val id = item.jsonObject["id"]?.jsonPrimitive?.content
                assertNotNull(id, "item must expose id")
                val regClasses = item.jsonObject["regulatory_class"]?.jsonArray
                assertNotNull(regClasses, "item id=$id must expose regulatory_class list")
                val values = regClasses.map { element -> element.jsonPrimitive.content }
                assertTrue(
                    actual = values.contains(element = "prescription_required"),
                    message =
                    "item id=$id has regulatory_class=$values; must contain " +
                        "'prescription_required' under filter",
                )
            }
        }

    @Test
    fun `GET drugs route= returns items whose routeOfAdministration serial name equals value`() = testApplication {
        application { module() }

        val response = client.get("/drugs?route=oral&page_size=100")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        assertTrue(
            actual = totalCount in 1 until 120,
            message = "total_count=$totalCount must be 1..<120 for route=oral",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "filtered items must be non-empty for route=oral",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val detailResponse = client.get("/drugs/$id")
            assertEquals(HttpStatusCode.OK, detailResponse.status, "detail GET must succeed for id=$id")
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val routeValue = detail["route_of_administration"]?.jsonPrimitive?.content
            assertEquals(
                expected = "oral",
                actual = routeValue,
                message = "item id=$id has route_of_administration=$routeValue; must be 'oral' under route=oral filter",
            )
        }
    }

    @Test
    fun `GET drugs dosage_form= returns items whose dosageForm serial name equals value`() = testApplication {
        application { module() }

        val response = client.get("/drugs?dosage_form=錠剤&page_size=100")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        assertTrue(
            actual = totalCount in 1 until 120,
            message = "total_count=$totalCount must be 1..<120 for dosage_form=錠剤",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertTrue(
            actual = items.isNotEmpty(),
            message = "filtered items must be non-empty for dosage_form=錠剤",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val detailResponse = client.get("/drugs/$id")
            assertEquals(HttpStatusCode.OK, detailResponse.status, "detail GET must succeed for id=$id")
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val dosageFormValue = detail["dosage_form"]?.jsonPrimitive?.content
            assertEquals(
                expected = "錠剤",
                actual = dosageFormValue,
                message = "item id=$id has dosage_form=$dosageFormValue; must be '錠剤' under dosage_form=錠剤 filter",
            )
        }
    }

    @Test
    fun `GET drugs category_atc=A and dosage_form=錠剤 returns intersection AND filter`() = testApplication {
        application { module() }

        val atcOnly = client.get("/drugs?category_atc=A&page_size=100")
        val formOnly = client.get("/drugs?dosage_form=錠剤&page_size=100")
        val intersection = client.get("/drugs?category_atc=A&dosage_form=錠剤&page_size=100")

        assertEquals(HttpStatusCode.OK, intersection.status)
        val atcBody = json.parseToJsonElement(string = atcOnly.bodyAsText()).jsonObject
        val formBody = json.parseToJsonElement(string = formOnly.bodyAsText()).jsonObject
        val intersectionBody = json.parseToJsonElement(string = intersection.bodyAsText()).jsonObject

        val atcTotal = atcBody["total_count"]?.jsonPrimitive?.content?.toInt()
        val formTotal = formBody["total_count"]?.jsonPrimitive?.content?.toInt()
        val intersectionTotal = intersectionBody["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(atcTotal, "atc-only response must include total_count")
        assertNotNull(formTotal, "form-only response must include total_count")
        assertNotNull(intersectionTotal, "intersection response must include total_count")
        assertTrue(
            actual = intersectionTotal <= atcTotal && intersectionTotal <= formTotal,
            message = "intersection total=$intersectionTotal must be <= min(atc=$atcTotal, form=$formTotal)",
        )

        val items = intersectionBody["items"]?.jsonArray
        assertNotNull(items, "intersection response must include items array")
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val detailResponse = client.get("/drugs/$id")
            assertEquals(HttpStatusCode.OK, detailResponse.status, "detail GET must succeed for id=$id")
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val atcCode = detail["atc_code"]?.jsonPrimitive?.content
            val dosageFormValue = detail["dosage_form"]?.jsonPrimitive?.content
            assertNotNull(atcCode, "item id=$id must expose atc_code")
            assertEquals(
                expected = "錠剤",
                actual = dosageFormValue,
                message = "item id=$id has dosage_form=$dosageFormValue; must be '錠剤' under AND filter",
            )
            assertTrue(
                actual = atcCode.startsWith(prefix = "A"),
                message = "item id=$id has atc_code=$atcCode; must start with 'A' under AND filter",
            )
        }
    }
}
