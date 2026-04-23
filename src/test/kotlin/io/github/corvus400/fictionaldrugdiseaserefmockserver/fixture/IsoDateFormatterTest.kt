package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class IsoDateFormatterTest {
    @Test
    fun `formatDate returns ISO 8601 YYYY-MM-DD`() {
        val date = LocalDate.of(2026, 4, 23)
        assertEquals(
            expected = "2026-04-23",
            actual = IsoDateFormatter.formatDate(date = date),
        )
    }

    @Test
    fun `formatDate zero-pads the year for boundary values`() {
        val date = LocalDate.of(1, 1, 1)
        assertEquals(
            expected = "0001-01-01",
            actual = IsoDateFormatter.formatDate(date = date),
        )
    }

    @Test
    fun `formatDate uses standard year not week-based-year for end-of-year dates`() {
        // 2019-12-31 は ISO 週番号では 2020 年の第 1 週に属する。
        // Java パターンの大文字 YYYY を誤用していれば "2020-12-31" を返して失敗する。
        // 本実装は ISO_LOCAL_DATE を使うため常に暦年を返す。
        val date = LocalDate.of(2019, 12, 31)
        assertEquals(
            expected = "2019-12-31",
            actual = IsoDateFormatter.formatDate(date = date),
        )
    }

    @Test
    fun `formatDateTime returns ISO 8601 YYYY-MM-DDThh_mm_ss with seconds granularity`() {
        val dateTime = LocalDateTime.of(2026, 4, 23, 9, 30, 45)
        assertEquals(
            expected = "2026-04-23T09:30:45",
            actual = IsoDateFormatter.formatDateTime(dateTime = dateTime),
        )
    }

    @Test
    fun `formatDateTime zero-pads hour minute and second`() {
        val dateTime = LocalDateTime.of(2026, 4, 23, 0, 0, 0)
        assertEquals(
            expected = "2026-04-23T00:00:00",
            actual = IsoDateFormatter.formatDateTime(dateTime = dateTime),
        )
    }
}
