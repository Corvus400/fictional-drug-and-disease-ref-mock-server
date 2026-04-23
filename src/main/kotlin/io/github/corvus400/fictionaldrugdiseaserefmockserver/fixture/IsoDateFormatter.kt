package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ISO 8601 形式の日付文字列を生成する共通フォーマッタ。
 *
 * 仕様表記は `YYYY-MM-DD` (大文字) だが、Java の DateTimeFormatter パターン文字は
 * 小文字 `yyyy` が標準年 (year-of-era)、大文字 `YYYY` は week-based-year で
 * 年末 (例: 2019-12-31) を翌年として出力する既知の罠がある。本実装は文字列パターンを
 * 手書きせず `DateTimeFormatter.ISO_LOCAL_DATE` を使うことで罠を原理的に回避する。
 *
 * `formatDateTime` は秒粒度が固定の `yyyy-MM-dd'T'HH:mm:ss` (小文字 y 必須) を使う。
 * `ISO_LOCAL_DATE_TIME` はナノ秒まで出力する可能性があり本プロジェクトの「秒まで」
 * 要件と不一致のため採用しない。
 */
object IsoDateFormatter {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)

    fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)
}
