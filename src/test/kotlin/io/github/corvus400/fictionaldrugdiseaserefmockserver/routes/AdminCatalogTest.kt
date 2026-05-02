// テストメソッド名に日本語を使用する方針（テスト可読性向上）
@file:Suppress("NonAsciiCharacters")

package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertAttribute
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertCssClassDefined
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementCount
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertElementExists
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertHasAttribute
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertJsContains
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertJsFunctionDefined
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.assertNoElement
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
    fun `catalog returns HTML with OK status`() = testApplication {
        application { module() }

        val response = client.get("/__admin/catalog")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(
            response.headers["Content-Type"]?.contains(ContentType.Text.Html.toString()) == true,
        )
    }

    @Test
    fun `catalog contains endpoint entries grouped by tag in API view`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementExists(
            "#main-api .tag-section",
            "Should have at least one tag section in API view",
        )
        doc.assertElementExists(
            "#main-api .endpoint",
            "Should have at least one endpoint card in API view",
        )
    }

    @Test
    fun `catalog contains scenario tables with title and description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementExists(".scenario-table", "Should have scenario tables")
        doc.assertElementExists(".sc-name", "Should have scenario names")
        doc.assertElementExists(".sc-title", "Should have scenario titles")
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

        val filterBtns = doc.select(".filter-btn")
        doc.assertElementExists(".filter-btn", "Should have filter buttons (All + tags)")
        assertTrue(filterBtns.size > 1, "Should have filter buttons (All + tags)")

        val allBtn = filterBtns.first()
        assertEquals("All", allBtn?.text())
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

        val title = doc.assertElementExists("title").text()
        assertTrue(
            title.contains("対応画面・シナリオ・Fixture概要カタログ"),
            "Title should contain 対応画面・シナリオ・Fixture概要カタログ",
        )

        val h1 = doc.assertElementExists("h1").text()
        assertEquals(
            "対応画面・シナリオ・Fixture概要カタログ",
            h1,
            "H1 should be 対応画面・シナリオ・Fixture概要カタログ",
        )
    }

    @Test
    fun `catalog contains overview section`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementCount(".catalog-overview", 1)
        val overview = doc.assertElementExists(".catalog-overview")
        assertTrue(
            overview.text().contains("Swagger UI"),
            "Overview should mention Swagger UI",
        )
    }

    @Test
    fun `catalog contains disclaimer banner`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        doc.assertElementCount(".disclaimer-banner", 1)
        val banner = doc.assertElementExists(".disclaimer-banner")
        assertTrue(banner.text().contains("FICTIONAL DATA"))
        assertTrue(banner.text().contains("架空データ"))
    }

    @Test
    fun `catalog scenario table header uses Fixture Description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        val headers = doc.select(".scenario-table th")
        val headerTexts = headers.map { it.text() }
        assertTrue(
            headerTexts.contains("Fixture Description"),
            "Should have 'Fixture Description' column header, got: $headerTexts",
        )
    }

    @Test
    fun `catalog contains view toggle buttons`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = parseHtml(html)

        val viewBtns = doc.select(".view-btn")
        doc.assertElementCount(".view-btn", 2)
        assertEquals("画面別", viewBtns[0].text())
        assertEquals("API別", viewBtns[1].text())
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

        val searchInput = parseHtml(client.get("/__admin/catalog").bodyAsText())
            .assertElementExists("#search")
        searchInput.assertHasAttribute("aria-label")
        searchInput.assertHasAttribute("autocomplete")
    }

    @Test
    fun `catalog view toggle buttons have correct data-view values`() = testApplication {
        application { module() }

        val viewBtns = parseHtml(client.get("/__admin/catalog").bodyAsText())
            .select(".view-btn")
        viewBtns[0].assertAttribute("data-view", "screen")
        viewBtns[1].assertAttribute("data-view", "api")
    }

    @Test
    fun `catalog excludes ADMIN and SYSTEM tag sections`() = testApplication {
        application { module() }

        val doc = parseHtml(client.get("/__admin/catalog").bodyAsText())
        doc.assertNoElement("[data-tag='ADMIN']")
        doc.assertNoElement("[data-tag='SYSTEM']")
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
