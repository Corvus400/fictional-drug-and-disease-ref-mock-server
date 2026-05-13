// テストメソッド名に日本語を使用する方針（テスト可読性向上）
@file:Suppress("NonAsciiCharacters")

package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertAnyElementTextContains
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertCssClassDefined
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementCount
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementCountAndTextContains
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementCountAndTextContainsAll
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementHasAttributes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementMinimumCountAndFirstTextEquals
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementTextContains
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementTextEquals
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementsAttributeValues
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementsExist
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementsTextEquals
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertJsContains
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertJsFunctionDefined
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertNoElements
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertUsedCssClassesAreDefined
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.parseHtml
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminCatalogTest {
    @Test
    fun `catalog returns OK status`() = testApplication {
        application { module() }

        val response = client.get("/__admin/catalog")

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
    }

    @Test
    fun `catalog returns HTML content type`() = testApplication {
        application { module() }

        val response = client.get("/__admin/catalog")

        assertTrue(
            response.headers["Content-Type"]?.contains(ContentType.Text.Html.toString()) == true,
            message = "contract assertion failed",
        )
    }

    @Test
    fun `catalog contains endpoint entries grouped by tag in API view`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementsExist(listOf("#main-api .tag-section", "#main-api .endpoint"))
    }

    @Test
    fun `catalog contains scenario tables with title and description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementsExist(listOf(".scenario-table", ".sc-name", ".sc-title"))
    }

    @Test
    fun `catalog contains search input`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementCount("#search", 1)
    }

    @Test
    fun `catalog contains filter buttons`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementMinimumCountAndFirstTextEquals(".filter-btn", minimumCount = 2, expectedFirstText = "All")
    }

    @Test
    fun `catalog HTML contains dark mode support`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()

        assertTrue(
            html.contains("prefers-color-scheme"),
            "Should contain dark mode media query",
        )
    }

    @Test
    fun `catalog title is 対応画面・シナリオ・Fixture概要カタログ`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementTextContains("title", "対応画面・シナリオ・Fixture概要カタログ")
    }

    @Test
    fun `catalog h1 is 対応画面・シナリオ・Fixture概要カタログ`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementTextEquals("h1", "対応画面・シナリオ・Fixture概要カタログ")
    }

    @Test
    fun `catalog contains overview section`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementCountAndTextContains(".catalog-overview", expectedCount = 1, expectedText = "Swagger UI")
    }

    @Test
    fun `catalog contains disclaimer banner`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementCountAndTextContainsAll(
            ".disclaimer-banner",
            expectedCount = 1,
            expectedTexts = listOf("FICTIONAL DATA", "架空データ"),
        )
    }

    @Test
    fun `catalog scenario table header uses Fixture Description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertAnyElementTextContains(".scenario-table th", "Fixture Description")
    }

    @Test
    fun `catalog contains view toggle buttons`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementsTextEquals(".view-btn", listOf("画面別", "API別"))
    }

    @Test
    fun `catalog defines toggleSection JS function`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertJsFunctionDefined("toggleSection")
    }

    @Test
    fun `catalog defines toggleEndpoint JS function`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertJsFunctionDefined("toggleEndpoint")
    }

    @Test
    fun `catalog defines handleKey JS function`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertJsFunctionDefined("handleKey")
    }

    @Test
    fun `catalog defines applyFilters JS function`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertJsFunctionDefined("applyFilters")
    }

    @Test
    fun `catalog JS wires search input event listener`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertJsContains("search.addEventListener('input'")
    }

    @Test
    fun `catalog search input has accessibility attributes`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertElementHasAttributes("#search", listOf("aria-label", "autocomplete"))
    }

    @Test
    fun `catalog view toggle buttons have correct data-view values`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertElementsAttributeValues(".view-btn", "data-view", listOf("screen", "api"))
    }

    @Test
    fun `catalog excludes ADMIN and SYSTEM tag sections`() = testApplication {
        application { module() }

        val doc = parseHtml(client.get("/__admin/catalog").bodyAsText())
        doc.assertNoElements(listOf("[data-tag='ADMIN']", "[data-tag='SYSTEM']"))
    }

    @Test
    fun `catalog defines all HTTP method badge styles`() = testApplication {
        application { module() }

        val doc = parseHtml(client.get("/__admin/catalog").bodyAsText())
        listOf("method-GET", "method-POST", "method-PUT", "method-DELETE").forEach {
            doc.assertCssClassDefined(it)
        }
    }

    @Test
    fun `catalog defines all CSS classes used in body`() = testApplication {
        application { module() }

        parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertUsedCssClassesAreDefined()
    }
}
