package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `DiseaseKeywordTarget.fromQuery` の lower-case 厳密性を固定する spec テスト。
 *
 * Drug 側 PR #286 の `DrugKeywordTarget.fromQuery` 仕様 (lower-case のみ受理、不正値は安全側で
 * 既定値フォールバック) に Disease 側 PR #287 を後追いで揃えた refactor (commit f76b8f2) の
 * 振る舞いを Red 駆動で固定する。修正前の `value?.uppercase()` 版は `"NAME"` (大文字) を受理して
 * `NAME` を返すため、`fromQuery("NAME_ENGLISH") == NAME` のアサート (大文字は既定値に落ちる仕様)
 * が Red になり、修正後 (lower-case 厳密) で Green になる。
 */
class DiseaseKeywordTargetFromQueryTest {
    @Test
    fun `fromQuery returns NAME for null`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = null),
        )
    }

    @Test
    fun `fromQuery returns NAME for empty string`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = ""),
        )
    }

    @Test
    fun `fromQuery returns NAME for lower-case 'name'`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = "name"),
        )
    }

    @Test
    fun `fromQuery returns NAME_ENGLISH for lower-case 'name_english'`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME_ENGLISH,
            actual = DiseaseKeywordTarget.fromQuery(value = "name_english"),
        )
    }

    @Test
    fun `fromQuery returns SYNONYMS for lower-case 'synonyms'`() {
        assertEquals(
            expected = DiseaseKeywordTarget.SYNONYMS,
            actual = DiseaseKeywordTarget.fromQuery(value = "synonyms"),
        )
    }

    @Test
    fun `fromQuery falls back to NAME for upper-case 'NAME_ENGLISH' (lower-case strict)`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = "NAME_ENGLISH"),
        )
    }

    @Test
    fun `fromQuery falls back to NAME for upper-case 'SYNONYMS' (lower-case strict)`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = "SYNONYMS"),
        )
    }

    @Test
    fun `fromQuery falls back to NAME for unrecognized value`() {
        assertEquals(
            expected = DiseaseKeywordTarget.NAME,
            actual = DiseaseKeywordTarget.fromQuery(value = "title"),
        )
    }
}
