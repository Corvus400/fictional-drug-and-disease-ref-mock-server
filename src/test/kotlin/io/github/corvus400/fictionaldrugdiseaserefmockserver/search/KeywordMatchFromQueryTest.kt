package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `KeywordMatch.fromQuery` の lower-case 厳密性を固定する spec テスト。
 *
 * Drug 側 PR #286 の `fromQuery` 仕様 (lower-case のみ受理、不正値は安全側で `PARTIAL` フォールバック)
 * に Disease 側 PR #287 を後追いで揃えた refactor (commit f76b8f2) の振る舞いを Red 駆動で固定する。
 * 修正前の `value?.uppercase()` 版は `"PREFIX"` (大文字) を受理して `PREFIX` を返すため、
 * `fromQuery("PREFIX") == PARTIAL` のアサートが Red になり、修正後 (lower-case 厳密) で Green になる。
 */
class KeywordMatchFromQueryTest {
    @Test
    fun `fromQuery returns PARTIAL for null`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = null))
    }

    @Test
    fun `fromQuery returns PARTIAL for empty string`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = ""))
    }

    @Test
    fun `fromQuery returns PARTIAL for lower-case 'partial'`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = "partial"))
    }

    @Test
    fun `fromQuery returns PREFIX for lower-case 'prefix'`() {
        assertEquals(expected = KeywordMatch.PREFIX, actual = KeywordMatch.fromQuery(value = "prefix"))
    }

    @Test
    fun `fromQuery falls back to PARTIAL for upper-case 'PREFIX' (lower-case strict)`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = "PREFIX"))
    }

    @Test
    fun `fromQuery falls back to PARTIAL for mixed-case 'Prefix' (lower-case strict)`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = "Prefix"))
    }

    @Test
    fun `fromQuery falls back to PARTIAL for unrecognized value`() {
        assertEquals(expected = KeywordMatch.PARTIAL, actual = KeywordMatch.fromQuery(value = "prefxxx"))
    }
}
