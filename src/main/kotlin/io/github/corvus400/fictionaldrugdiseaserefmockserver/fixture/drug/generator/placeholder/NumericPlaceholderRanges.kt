package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import java.util.Locale

private const val UNIT_SALT: Long = 0x5A5A5A5A5A5A5A5AL
private const val RANGE_END_SALT: Long = 0x3C3C3C3C3C3C3C3CL

private fun derive(
    seed: Long,
    salt: Long,
): Long = seed xor salt

private fun pickUnit(
    seed: Long,
    units: List<String>,
): String = if (units.size == 1) units.first() else ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), units)

private fun Double.formatOneDecimal(): String = String.format(Locale.ROOT, "%.1f", this)

private sealed interface NumericFormatter {
    fun render(seed: Long): String
}

private class IntFormatter(
    val range: IntRange,
    val multiplier: Int = 1,
    val offset: Int = 0,
    val units: List<String>,
) : NumericFormatter {
    override fun render(seed: Long): String {
        val value = offset + ValueRangeGenerator.pickInRange(seed, range) * multiplier
        return if (units.isEmpty()) value.toString() else "$value ${pickUnit(seed, units)}"
    }
}

private class DoubleFormatter(
    val range: IntRange,
    val scaleFactor: Double,
    val units: List<String> = emptyList(),
) : NumericFormatter {
    override fun render(seed: Long): String {
        val value = ValueRangeGenerator.pickInRange(seed, range) * scaleFactor
        val formatted = value.formatOneDecimal()
        return if (units.isEmpty()) formatted else "$formatted ${pickUnit(seed, units)}"
    }
}

private class InequalityFormatter(
    val range: IntRange,
    val prefix: String,
    val zeroPadWidth: Int,
) : NumericFormatter {
    override fun render(seed: Long): String {
        val bucket = ValueRangeGenerator.pickInRange(seed, range)
        return "$prefix${bucket.toString().padStart(zeroPadWidth, '0')}"
    }
}

private class IntRangeFormatter(
    val startRange: IntRange,
    val endOffsetRange: IntRange,
    val unit: String,
) : NumericFormatter {
    override fun render(seed: Long): String {
        val start = ValueRangeGenerator.pickInRange(seed, startRange)
        val end =
            ValueRangeGenerator.pickInRange(
                derive(seed, RANGE_END_SALT),
                (start + endOffsetRange.first)..(start + endOffsetRange.last),
            )
        return "$start - $end $unit"
    }
}

private class DoubleRangeFormatter(
    val startRange: IntRange,
    val endOffsetRange: IntRange,
    val scaleFactor: Double,
    val units: List<String>,
) : NumericFormatter {
    override fun render(seed: Long): String {
        val startBucket = ValueRangeGenerator.pickInRange(seed, startRange)
        val endBucket =
            ValueRangeGenerator.pickInRange(
                derive(seed, RANGE_END_SALT),
                (startBucket + endOffsetRange.first)..(startBucket + endOffsetRange.last),
            )
        val start = (startBucket * scaleFactor).formatOneDecimal()
        val end = (endBucket * scaleFactor).formatOneDecimal()
        return "$start - $end ${pickUnit(seed, units)}"
    }
}

object NumericPlaceholderRanges {
    fun resolve(
        key: String,
        seed: Long,
    ): String {
        val formatter =
            FORMATTERS[key]
                ?: error(
                    "Unknown category-D placeholder key '$key'. " +
                        "NumericPlaceholderRanges covers only the 33 category-D numeric keys. " +
                        "Other categories (A/B/C) are resolved by DrugPlaceholderDictionary.",
                )
        return formatter.render(seed)
    }

    private val FORMATTERS: Map<String, NumericFormatter> =
        mapOf(
            "auc" to IntFormatter(range = 0..98, multiplier = 50, offset = 100, units = listOf("ng·h/mL", "μg·h/mL")),
            "bioavailability" to IntFormatter(range = 10..99, units = listOf("%")),
            "cmax" to DoubleFormatter(range = 1..200, scaleFactor = 0.5, units = listOf("ng/mL", "μg/mL")),
            "cnsRatio" to DoubleFormatter(range = 1..500, scaleFactor = 0.1, units = listOf("%")),
            "doseAmount" to IntFormatter(range = 1..100, multiplier = 5, units = listOf("μg", "mg", "g")),
            "doseCount" to IntFormatter(range = 1..3, units = emptyList()),
            "dosePerKg" to DoubleFormatter(range = 1..200, scaleFactor = 0.1, units = listOf("mg/kg")),
            "durationDays" to IntFormatter(range = 1..90, units = listOf("日", "日間")),
            "durationWeeks" to IntFormatter(range = 1..52, units = listOf("週間")),
            "efficacyRate" to DoubleFormatter(range = 100..999, scaleFactor = 0.1, units = listOf("%")),
            "fecalExcretionRatio" to DoubleFormatter(range = 0..990, scaleFactor = 0.1, units = listOf("%")),
            "foodEffectRatio" to DoubleFormatter(range = 5..30, scaleFactor = 0.1, units = listOf("倍")),
            "frequency" to IntFormatter(range = 1..4, units = emptyList()),
            "halfLife" to DoubleFormatter(range = 2..48, scaleFactor = 0.5, units = listOf("時間")),
            "ic50" to DoubleFormatter(range = 1..1000, scaleFactor = 0.1, units = listOf("nM")),
            "interval" to IntFormatter(range = 1..6, multiplier = 4, units = listOf("時間")),
            "maxDailyDose" to IntFormatter(range = 1..30, multiplier = 100, units = listOf("mg", "g")),
            "meltingPoint" to IntFormatter(range = 80..300, units = listOf("℃")),
            "packageSize" to IntFormatter(range = 1..10, multiplier = 10, units = listOf("錠", "包", "本")),
            "patientCount" to IntFormatter(range = 1..20, multiplier = 100, units = listOf("例")),
            "pKa" to DoubleFormatter(range = 20..100, scaleFactor = 0.1),
            "proteinBinding" to IntFormatter(range = 10..99, units = listOf("%")),
            "pValue" to InequalityFormatter(range = 1..50, prefix = "< 0.", zeroPadWidth = 3),
            "reductionRatio" to IntFormatter(range = 1..19, multiplier = 5, units = listOf("%")),
            "referenceRange" to
                DoubleRangeFormatter(
                    startRange = 10..80,
                    endOffsetRange = 5..50,
                    scaleFactor = 0.1,
                    units = listOf("mg/dL", "μg/mL", "nmol/L"),
                ),
            "reimbursementDurationDays" to IntFormatter(range = 14..180, units = listOf("日")),
            "retentionRate" to IntFormatter(range = 10..99, units = listOf("%")),
            "stableDuration" to IntFormatter(range = 1..10, multiplier = 6, units = listOf("か月", "年")),
            "storageTemperature" to IntRangeFormatter(startRange = 1..20, endOffsetRange = 1..10, unit = "℃"),
            "tmax" to DoubleFormatter(range = 1..24, scaleFactor = 0.5, units = listOf("時間")),
            "totalDailyDose" to IntFormatter(range = 1..30, multiplier = 100, units = listOf("mg", "g")),
            "urinaryExcretionRatio" to DoubleFormatter(range = 0..990, scaleFactor = 0.1, units = listOf("%")),
            "volumeOfDistribution" to DoubleFormatter(range = 1..100, scaleFactor = 0.1, units = listOf("L/kg")),
        )
}
