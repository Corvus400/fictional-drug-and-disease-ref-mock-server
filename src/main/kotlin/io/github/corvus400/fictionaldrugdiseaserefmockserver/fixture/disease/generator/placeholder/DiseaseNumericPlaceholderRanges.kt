package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import java.util.Locale

private fun Double.formatOneDecimal(): String = String.format(Locale.ROOT, "%.1f", this)

private sealed interface DiseaseNumericFormatter {
    fun render(seed: Long): String
}

private class PlainIntFormatter(
    val range: IntRange,
    val multiplier: Int = 1,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val value = ValueRangeGenerator.pickInRange(seed, range) * multiplier
        return value.toString()
    }
}

private class UnitIntFormatter(
    val range: IntRange,
    val multiplier: Int = 1,
    val unit: String,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val value = ValueRangeGenerator.pickInRange(seed, range) * multiplier
        return "$value $unit"
    }
}

private class CommaIntFormatter(
    val range: IntRange,
    val multiplier: Int = 1,
    val unit: String,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val value = ValueRangeGenerator.pickInRange(seed, range) * multiplier
        val formatted = String.format(Locale.ROOT, "%,d", value)
        return "$formatted $unit"
    }
}

private class UnitDoubleFormatter(
    val range: IntRange,
    val scaleFactor: Double,
    val unit: String,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val value = ValueRangeGenerator.pickInRange(seed, range) * scaleFactor
        val formatted = value.formatOneDecimal()
        return "$formatted$unit"
    }
}

private class RatioFormatter(
    val numeratorRange: IntRange,
    val denominator: Int,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val numerator = ValueRangeGenerator.pickInRange(seed, numeratorRange)
        return "$numerator:$denominator"
    }
}

private class PlainIntRangeFormatter(
    val startRange: IntRange,
    val startMultiplier: Int,
    val endOffset: Int,
) : DiseaseNumericFormatter {
    override fun render(seed: Long): String {
        val start = ValueRangeGenerator.pickInRange(seed, startRange) * startMultiplier
        val end = start + endOffset
        return "$start-$end"
    }
}

object DiseaseNumericPlaceholderRanges {
    fun resolve(
        key: String,
        seed: Long,
    ): String {
        val formatter =
            FORMATTERS[key]
                ?: error(
                    "Unknown category-D placeholder key '$key'. " +
                        "DiseaseNumericPlaceholderRanges covers only the 9 Disease category-D numeric keys. " +
                        "Other categories (A_MEDICAL_VOCABULARY / B_SELF_REFERENCE) are resolved " +
                        "by DiseasePlaceholderDictionary.",
                )
        return formatter.render(seed)
    }

    private val FORMATTERS: Map<String, DiseaseNumericFormatter> =
        mapOf(
            "annualIncidence" to CommaIntFormatter(range = 1..40, multiplier = 500, unit = "例"),
            "evaluationDuration" to UnitIntFormatter(range = 1..12, multiplier = 2, unit = "週間"),
            "gradeCount" to PlainIntFormatter(range = 3..5),
            "peakAgeYears" to PlainIntRangeFormatter(startRange = 2..6, startMultiplier = 10, endOffset = 10),
            "prevalenceRate" to PlainIntFormatter(range = 1..50),
            "prognosisRate" to UnitDoubleFormatter(range = 100..190, scaleFactor = 0.5, unit = "%"),
            "severityThreshold" to UnitIntFormatter(range = 4..16, multiplier = 5, unit = "点"),
            "sexRatio" to RatioFormatter(numeratorRange = 1..5, denominator = 1),
            "supportingFindingCount" to PlainIntFormatter(range = 1..5),
        )
}
