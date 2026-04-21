package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Mock Server用の動的日付生成ヘルパー
 * サーバー起動時点の日時をベースに、常に未来の日付を返す
 */
object MockDateHelper {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    private val dayOfWeekJapanese = mapOf(
        DayOfWeek.MONDAY to "月",
        DayOfWeek.TUESDAY to "火",
        DayOfWeek.WEDNESDAY to "水",
        DayOfWeek.THURSDAY to "木",
        DayOfWeek.FRIDAY to "金",
        DayOfWeek.SATURDAY to "土",
        DayOfWeek.SUNDAY to "日",
    )

    /**
     * 注文変更期限: 現在日時 + 7日の10:00:00
     * フォーマット: "yyyy/MM/dd HH:mm:ss"
     */
    fun cancelLimit(): String {
        val deadline = LocalDateTime.now()
            .plusDays(7)
            .withHour(10)
            .withMinute(0)
            .withSecond(0)
        return deadline.format(dateTimeFormatter)
    }

    /**
     * お届け予定日: 現在日時 + 14日
     * フォーマット: "yyyy/MM/dd"
     */
    fun shippingDate(): String {
        val shipping = LocalDateTime.now().plusDays(14)
        return shipping.format(dateFormatter)
    }

    /**
     * お届け予定日時: 現在日時 + 14日の00:00:00
     * フォーマット: "yyyy/MM/dd HH:mm:ss"
     */
    fun receiverDate(): String {
        val shipping = LocalDateTime.now()
            .plusDays(14)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
        return shipping.format(dateTimeFormatter)
    }

    /**
     * 注文期限キャプション: cancelLimit日付の日本語フォーマット
     * フォーマット: "M/d(曜) 10:00まで"
     */
    fun orderDeadlineCaption(): String {
        val deadline = LocalDateTime.now().plusDays(7)
        val dayJp = dayOfWeekJapanese[deadline.dayOfWeek]
        return "${deadline.monthValue}/${deadline.dayOfMonth}($dayJp) 10:00まで"
    }

    // ========================================
    // 請求一覧API用（ISO 8601形式）
    // Android側のStringUtils.getDateStringsByISOString()が
    // "yyyy-MM-dd'T'HH:mm:ss" 形式でパースするため、この形式を使用する
    // ========================================

    private val isoDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    /**
     * YYYYMM形式の文字列をYearMonthにパースする
     * パース失敗時はnullを返す
     */
    private fun parseYearMonth(billingYm: String): YearMonth? {
        if (billingYm.length != 6) return null
        val year = billingYm.substring(0, 4).toIntOrNull() ?: return null
        val month = billingYm.substring(4, 6).toIntOrNull() ?: return null
        return try {
            YearMonth.of(year, month)
        } catch (_: java.time.DateTimeException) {
            null
        }
    }

    /**
     * 請求日: 前月25日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingDate(): String {
        val date = LocalDate.now().minusMonths(1).withDayOfMonth(25)
        return date.atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 請求対象期間開始日: 前月1日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingPeriodFrom(): String {
        val date = LocalDate.now().minusMonths(1).withDayOfMonth(1)
        return date.atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 請求対象期間終了日: 前月末日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingPeriodTo(): String {
        val date = LocalDate.now().minusMonths(1)
        val lastDay = date.withDayOfMonth(date.lengthOfMonth())
        return lastDay.atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 決済予定日: 当月25日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingSettlementDate(): String {
        val date = LocalDate.now().withDayOfMonth(25)
        return date.atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 注文出荷日: 前月の指定日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingShippingDate(dayOfMonth: Int): String {
        val lastMonth = LocalDate.now().minusMonths(1)
        val safeDay = dayOfMonth.coerceAtMost(lastMonth.lengthOfMonth())
        val date = lastMonth.withDayOfMonth(safeDay)
        return date.atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 出荷日ラベル用曜日: billingShippingDateの曜日を「（月）」形式で返す
     * BillingOrderViewHolderで MM/dd + shippingDateLabel と連結表示されるため
     */
    fun billingShippingDateLabel(dayOfMonth: Int): String {
        val lastMonth = LocalDate.now().minusMonths(1)
        val safeDay = dayOfMonth.coerceAtMost(lastMonth.lengthOfMonth())
        val date = lastMonth.withDayOfMonth(safeDay)
        val dayJp = dayOfWeekJapanese[date.dayOfWeek]
        return "（$dayJp）"
    }

    // ========================================
    // 請求一覧API用（billingYm指定版）
    // billingYm(YYYYMM)に基づき、そのbilling月に対応する日付を生成する。
    // STG実APIの日付マッピング:
    //   billingDate = YYYY-MM-01（billing月の1日）
    //   periodCoverdFrom = 前月1日
    //   periodCoverdTo = 前月末日
    //   settlementDate = YYYY-MM-17（billing月の17日）
    //   shippingDate = 前月の指定日
    // ========================================

    /**
     * 請求日: billing月の1日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingDate(billingYm: String): String {
        val ym = parseYearMonth(billingYm) ?: return billingDate()
        return ym.atDay(1).atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 請求対象期間開始日: billing月の前月1日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingPeriodFrom(billingYm: String): String {
        val ym = parseYearMonth(billingYm) ?: return billingPeriodFrom()
        return ym.minusMonths(1).atDay(1).atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 請求対象期間終了日: billing月の前月末日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingPeriodTo(billingYm: String): String {
        val ym = parseYearMonth(billingYm) ?: return billingPeriodTo()
        return ym.minusMonths(1).atEndOfMonth().atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 決済予定日: billing月の17日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingSettlementDate(billingYm: String): String {
        val ym = parseYearMonth(billingYm) ?: return billingSettlementDate()
        return ym.atDay(17).atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 注文出荷日: billing月の前月の指定日
     * フォーマット: ISO 8601 "yyyy-MM-dd'T'HH:mm:ss"
     */
    fun billingShippingDate(billingYm: String, dayOfMonth: Int): String {
        val ym = parseYearMonth(billingYm) ?: return billingShippingDate(dayOfMonth = dayOfMonth)
        val prevMonth = ym.minusMonths(1)
        val safeDay = dayOfMonth.coerceAtMost(prevMonth.lengthOfMonth())
        return prevMonth.atDay(safeDay).atStartOfDay().format(isoDateTimeFormatter)
    }

    /**
     * 出荷日ラベル用曜日: billingShippingDateの曜日を「（月）」形式で返す
     */
    fun billingShippingDateLabel(billingYm: String, dayOfMonth: Int): String {
        val ym = parseYearMonth(billingYm) ?: return billingShippingDateLabel(dayOfMonth = dayOfMonth)
        val prevMonth = ym.minusMonths(1)
        val safeDay = dayOfMonth.coerceAtMost(prevMonth.lengthOfMonth())
        val date = prevMonth.atDay(safeDay)
        val dayJp = dayOfWeekJapanese[date.dayOfWeek]
        return "（$dayJp）"
    }

    /**
     * 現在日付 + 指定週 + 指定日のフォーマット済み文字列
     * PausedInfo等で使用（次回到着予定日、次回注文開始日等）
     * フォーマット: "yyyy/MM/dd HH:mm:ss"
     */
    fun weekLater(weeks: Int, days: Int = 0): String {
        val dateTime = LocalDate.now().plusWeeks(weeks.toLong()).plusDays(days.toLong()).atStartOfDay()
        return dateTime.format(dateTimeFormatter)
    }
}
