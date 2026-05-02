package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Phase 11-10b の Red 駆動テスト: `/diseases` ハンドラに `keyword` / `keyword_match` /
 * `keyword_target` クエリ受付と `DiseaseSearchService.applyKeyword` 統合を要求する。
 *
 * 親 Issue 本文の Red サンプルは `keyword=` (空文字) で `filtered < total` を要求するが、
 * `DiseaseSearchService` は `keyword.isNullOrBlank()` で全件素通しのため成立しない
 * (Issue 本文のプレースホルダ `<existing>` が脱落した typo と判断、`DiseaseSearchService`
 * の仕様変更は #110 でクローズ済みのため本フェーズではしない)。
 *
 * 本テストは確定的に絞り込まれる "存在しない" キーワードを使い、未統合状態 (現状) では
 * 受付されず全件返るため `total_count == 0` が外れて Red、ハンドラ統合後は 0 件で Green。
 */
class DiseaseModuleKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with non-matching keyword returns total_count 0 instead of full set`() = testApplication {
        application { module() }

        val total = client.get(urlString = "/v1/diseases?page_size=100").totalCount()
        val filtered =
            client.get(
                urlString = "/v1/diseases?keyword=zzznotexistzzz" +
                    "&keyword_target=name&keyword_match=partial&page_size=100",
            ).totalCount()

        assertTrue(
            actual = total > 0,
            message = "precondition: default scenario must populate diseases (total=$total)",
        )
        assertEquals(
            expected = 0,
            actual = filtered,
            message = "non-matching keyword must filter total to 0 (filtered=$filtered total=$total)",
        )
    }

    /**
     * Phase 11-10b の refactor commit (f76b8f2) で `summariesByScenario[scenario] ?:
     * summariesByScenario.values.first()` パターン (旧 `applyListFilters` 経路 / Drug 側
     * `DrugListFixtures.resolve` とも同等) を `diseasesByScenario[scenario].orEmpty()` で
     * 上書きしてしまった regression を防止する。X-Mock-Scenario ヘッダに未登録キーを渡した
     * とき、`.orEmpty()` 版だと 0 件で返るが、本来は default シナリオ (80 件) にフォールバック
     * すべき。
     */
    @Test
    fun `GET diseases with unknown X-Mock-Scenario falls back to default and returns full set`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases?page_size=100") {
            headers {
                append(name = "X-Mock-Scenario", value = "nonexistent-scenario-key")
            }
        }

        val total = response.totalCount()
        assertEquals(
            expected = 80,
            actual = total,
            message = "unknown X-Mock-Scenario must fall back to default (80 items), " +
                "not return empty (got total=$total)",
        )
    }

    /**
     * Phase 11-11b の検証テスト (#112): default シナリオ (80 件) に対して
     * 実在 keyword でのヒット件数が `MIN_FILTERED_COUNT until DEFAULT_TOTAL_COUNT`
     * の範囲で正しく返ることを確認する。
     *
     * keyword は `knownDiseaseKeyword()` 経由で default シナリオ先頭エントリの `name`
     * 先頭 [KEYWORD_PREFIX_LENGTH] 文字から動的に派生させる (SSOT)。fixmerge レキシコン
     * 由来で fixture 名は起動時生成のためハードコードに耐えない。
     *
     * ## トートロジー回避設計 (Drug 側 PR #289 整合)
     *
     * 単純な `filtered >= 1` だと抽出元 disease 自身が必ずマッチする 1 件で常に成立し、
     * 「フィルタが他の disease を除外した」ことを検証できない (定義上の真)。
     * `filtered in MIN_FILTERED_COUNT until DEFAULT_TOTAL_COUNT` (= 2..79) を要求し、
     *
     * - 抽出元 + さらに別の disease にもヒット (≥ 2) で「フィルタが他にも当たる」
     * - 全件 80 未満 (< 80) で「フィルタが何かを除外している」
     *
     * の両面でフィルタが実質機能していることを保証する。
     *
     * `DiseaseModuleKeywordTest` の既存ペア:
     * - non-matching keyword (`zzznotexistzzz`) → 0 件 (negative complement)
     * - 実在 keyword → 2..79 件 (positive complement、本テスト)
     */
    @Test
    fun `GET diseases under default scenario with keyword returns positive filtered count`() = testApplication {
        application { module() }
        client.post(urlString = "/__admin/configs/diseaseList") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state": "default"}""")
        }

        val unfilteredCount = client.get(urlString = "/v1/diseases?page_size=100").totalCount()
        assertEquals(
            expected = DEFAULT_TOTAL_COUNT,
            actual = unfilteredCount,
            message = "precondition: default シナリオは $DEFAULT_TOTAL_COUNT 件であるべき (got $unfilteredCount)",
        )

        val keyword = knownDiseaseKeyword(client = client)
        val encodedKeyword = URLEncoder.encode(keyword, Charsets.UTF_8)
        val response = client.get(
            urlString = "/v1/diseases?keyword=$encodedKeyword" +
                "&keyword_target=name&keyword_match=partial&page_size=100",
        )
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val filteredCount = response.totalCount()
        assertTrue(
            actual = filteredCount in MIN_FILTERED_COUNT until DEFAULT_TOTAL_COUNT,
            message = "keyword=$keyword must filter default scenario count to " +
                "$MIN_FILTERED_COUNT until $DEFAULT_TOTAL_COUNT (got $filteredCount)",
        )

        client.post(urlString = "/__admin/reset")
    }

    /**
     * Phase 11-12b の検証テスト (#113): `configs/diseaseList` で `empty` シナリオに切替えた
     * 状態で keyword クエリ付き `/diseases` を呼んでも 200 OK + items 0 件 + total_count=0 が
     * 返ることを保証する。
     *
     * `DiseaseSearchService.applyKeyword(emptyList(), ...)` が空 list を素通しで返す挙動と、
     * Route ハンドラがシナリオ解決後に keyword フィルタを直列適用する経路の合成を retrograde
     * guard する (search/ メタテスト #102 で `DiseaseSearchService` に scenarioManager 参照が
     * ないことと併せて scenario × keyword の直交性を担保)。Drug 側 #101 と対をなす。
     */
    @Test
    fun `GET diseases under empty scenario with keyword returns items size zero and 200`() = testApplication {
        application { module() }
        client.post(urlString = "/__admin/configs/diseaseList") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state": "empty"}""")
        }

        val response = client.get(
            urlString = "/v1/diseases?keyword=whatever&keyword_target=name&keyword_match=partial",
        )
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertNotNull(actual = totalCount, message = "response body must have a numeric total_count")
        assertEquals(
            expected = 0,
            actual = totalCount,
            message = "empty scenario × keyword must yield total_count=0 (got $totalCount)",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have items array")
        assertTrue(
            actual = items.isEmpty(),
            message = "empty scenario × keyword must yield empty items array (got size=${items.size})",
        )

        client.post(urlString = "/__admin/reset")
    }

    /**
     * default シナリオで少なくとも 1 件にヒットする keyword を返す SSOT ヘルパー。
     *
     * `/diseases?page_size=1` で先頭エントリの `name` を取得し先頭 [KEYWORD_PREFIX_LENGTH]
     * 文字を抜粋する。`keyword_target=name` + `keyword_match=partial` 検索が抽出元の
     * 疾患を必ずヒットさせるため `total_count >= 1` を保証する。
     */
    private suspend fun knownDiseaseKeyword(client: HttpClient): String {
        val response = client.get(urlString = "/v1/diseases?page_size=1")
        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val firstName = body["items"]
            ?.jsonArray
            ?.firstOrNull()
            ?.jsonObject?.get(key = "name")
            ?.jsonPrimitive?.content
        assertNotNull(actual = firstName, message = "default scenario must have at least one disease item")
        val keyword = firstName.take(n = KEYWORD_PREFIX_LENGTH)
        assertTrue(
            actual = keyword.isNotEmpty(),
            message = "first disease name must be non-empty (got '$firstName')",
        )
        return keyword
    }

    private suspend fun HttpResponse.totalCount(): Int {
        assertEquals(expected = HttpStatusCode.OK, actual = status)
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertNotNull(actual = totalCount, message = "response body must have a numeric total_count")
        return totalCount
    }

    companion object {
        private const val KEYWORD_PREFIX_LENGTH: Int = 2
        private const val MIN_FILTERED_COUNT: Int = 2
        private const val DEFAULT_TOTAL_COUNT: Int = 80
    }
}
