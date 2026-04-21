// テストメソッド名に日本語を使用する方針（テスト可読性向上）
@file:Suppress("NonAsciiCharacters")

package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.jsoup.Jsoup
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
        val doc = Jsoup.parse(html)

        val tagSections = doc.select("#main-api .tag-section")
        assertTrue(tagSections.isNotEmpty(), "Should have at least one tag section in API view")

        val endpointCards = doc.select("#main-api .endpoint")
        assertTrue(endpointCards.isNotEmpty(), "Should have at least one endpoint card in API view")
    }

    @Test
    fun `catalog contains scenario tables with title and description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = Jsoup.parse(html)

        val scenarioTables = doc.select(".scenario-table")
        assertTrue(scenarioTables.isNotEmpty(), "Should have scenario tables")

        val scenarioNames = doc.select(".sc-name")
        assertTrue(scenarioNames.isNotEmpty(), "Should have scenario names")

        val scenarioTitles = doc.select(".sc-title")
        assertTrue(scenarioTitles.isNotEmpty(), "Should have scenario titles")
    }

    @Test
    fun `catalog contains search input`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = Jsoup.parse(html)

        val searchInput = doc.select("#search")
        assertEquals(1, searchInput.size, "Should have search input")
    }

    @Test
    fun `catalog contains filter buttons`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = Jsoup.parse(html)

        val filterBtns = doc.select(".filter-btn")
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
        val doc = Jsoup.parse(html)

        val title = doc.select("title").text()
        assertTrue(
            title.contains("対応画面・シナリオ・Fixture概要カタログ"),
            "Title should contain 対応画面・シナリオ・Fixture概要カタログ",
        )

        val h1 = doc.select("h1").text()
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
        val doc = Jsoup.parse(html)

        val overview = doc.select(".catalog-overview")
        assertEquals(1, overview.size, "Should have catalog overview section")
        assertTrue(
            overview.text().contains("Swagger UI"),
            "Overview should mention Swagger UI",
        )
    }

    @Test
    fun `catalog scenario table header uses Fixture Description`() = testApplication {
        application { module() }

        val html = client.get("/__admin/catalog").bodyAsText()
        val doc = Jsoup.parse(html)

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
        val doc = Jsoup.parse(html)

        val viewBtns = doc.select(".view-btn")
        assertEquals(2, viewBtns.size, "Should have 2 view toggle buttons")
        assertEquals("画面別", viewBtns[0].text())
        assertEquals("API別", viewBtns[1].text())
    }
}
