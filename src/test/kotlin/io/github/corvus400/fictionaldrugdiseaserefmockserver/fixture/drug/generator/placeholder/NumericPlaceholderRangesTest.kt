package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumericPlaceholderRangesTest {
    @Test
    fun `resolve returns non-blank string for every category-D key`() {
        CATEGORY_D_KEYS.forEach { key ->
            val seed = stableHash(id = key, slot = 0, index = 0)
            val value = NumericPlaceholderRanges.resolve(key, seed)
            assertTrue(
                value.isNotBlank(),
                "resolve('$key', $seed) returned blank; every category-D key must yield non-empty numeric text",
            )
        }
    }

    @Test
    fun `resolve is deterministic for identical key and seed`() {
        val key = "doseAmount"
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val first = NumericPlaceholderRanges.resolve(key, seed)
        val second = NumericPlaceholderRanges.resolve(key, seed)
        assertEquals(first, second)
    }

    @Test
    fun `resolve output matches expected format regex for every category-D key`() {
        CATEGORY_D_FORMAT_REGEX.forEach { (key, pattern) ->
            val regex = pattern.toRegex()
            repeat(FORMAT_SAMPLE_SIZE) { index ->
                val seed = stableHash(id = "drug_${index.toString().padStart(4, '0')}", slot = 0, index = 0)
                val value = NumericPlaceholderRanges.resolve(key, seed)
                assertTrue(
                    regex.matches(value),
                    "resolve('$key', $seed) = '$value' does not match expected format /$pattern/",
                )
            }
        }
    }

    @Test
    fun `resolve renders dot-decimal regardless of JVM default locale`() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.GERMANY)
            DECIMAL_BEARING_KEYS.forEach { key ->
                val regex = requireNotNull(CATEGORY_D_FORMAT_REGEX[key]) { "regex missing for '$key'" }.toRegex()
                repeat(LOCALE_SAMPLE_SIZE) { index ->
                    val seed = stableHash(id = "drug_${index.toString().padStart(4, '0')}", slot = 0, index = 0)
                    val value = NumericPlaceholderRanges.resolve(key, seed)
                    assertFalse(
                        value.contains(','),
                        "resolve('$key', $seed) = '$value' contains ',' — numeric formatting must be " +
                            "locale-independent (use Locale.ROOT / String.format(Locale.ROOT, ...)); " +
                            "otherwise Apple Container / CI in de_DE / fr_FR / similar locales emits " +
                            "comma decimals that break regex contracts.",
                    )
                    assertTrue(
                        regex.matches(value),
                        "resolve('$key', $seed) = '$value' does not match " +
                            "/${CATEGORY_D_FORMAT_REGEX[key]}/ under GERMANY locale",
                    )
                }
            }
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    private companion object {
        const val FORMAT_SAMPLE_SIZE = 50
        const val LOCALE_SAMPLE_SIZE = 20

        val DECIMAL_BEARING_KEYS =
            listOf(
                "cmax",
                "cnsRatio",
                "dosePerKg",
                "efficacyRate",
                "fecalExcretionRatio",
                "foodEffectRatio",
                "halfLife",
                "ic50",
                "pKa",
                "referenceRange",
                "tmax",
                "urinaryExcretionRatio",
                "volumeOfDistribution",
            )

        val CATEGORY_D_KEYS =
            listOf(
                "auc",
                "bioavailability",
                "cmax",
                "cnsRatio",
                "doseAmount",
                "dosePerKg",
                "durationDays",
                "durationWeeks",
                "efficacyRate",
                "fecalExcretionRatio",
                "foodEffectRatio",
                "frequency",
                "halfLife",
                "ic50",
                "interval",
                "maxDailyDose",
                "meltingPoint",
                "packageSize",
                "patientCount",
                "pKa",
                "proteinBinding",
                "pValue",
                "reductionRatio",
                "referenceRange",
                "reimbursementDurationDays",
                "retentionRate",
                "stableDuration",
                "storageTemperature",
                "tmax",
                "totalDailyDose",
                "urinaryExcretionRatio",
                "volumeOfDistribution",
            )

        val CATEGORY_D_FORMAT_REGEX: Map<String, String> =
            mapOf(
                "auc" to """^\d+(\.\d+)?\s?(ng·h/mL|μg·h/mL)$""",
                "bioavailability" to """^\d+\s?%$""",
                "cmax" to """^\d+(\.\d+)?\s?(ng/mL|μg/mL)$""",
                "cnsRatio" to """^\d+(\.\d+)?\s?%$""",
                "doseAmount" to """^\d+(\.\d+)?\s?(μg|mg|g)$""",
                "dosePerKg" to """^\d+(\.\d+)?\s?mg/kg$""",
                "durationDays" to """^\d+\s?日間?$""",
                "durationWeeks" to """^\d+\s?週間$""",
                "efficacyRate" to """^\d+(\.\d+)?\s?%$""",
                "fecalExcretionRatio" to """^\d+(\.\d+)?\s?%$""",
                "foodEffectRatio" to """^\d+(\.\d+)?\s?倍$""",
                "frequency" to """^\d+\s?回$""",
                "halfLife" to """^\d+(\.\d+)?\s?時間$""",
                "ic50" to """^\d+(\.\d+)?\s?nM$""",
                "interval" to """^\d+\s?時間$""",
                "maxDailyDose" to """^\d+(\.\d+)?\s?(mg|g)$""",
                "meltingPoint" to """^\d+\s?℃$""",
                "packageSize" to """^\d+\s?(錠|包|本)$""",
                "patientCount" to """^\d+\s?例$""",
                "pKa" to """^\d+\.\d$""",
                "proteinBinding" to """^\d+(\.\d+)?\s?%$""",
                "pValue" to """^(< 0\.\d{3}|0\.\d{3})$""",
                "reductionRatio" to """^\d+(\.\d+)?\s?%$""",
                "referenceRange" to """^\d+(\.\d+)?\s?-\s?\d+(\.\d+)?\s?(mg/dL|μg/mL|nmol/L)$""",
                "reimbursementDurationDays" to """^\d+\s?日$""",
                "retentionRate" to """^\d+(\.\d+)?\s?%$""",
                "stableDuration" to """^\d+\s?(か月|年)$""",
                "storageTemperature" to """^\d+\s?-\s?\d+\s?℃$""",
                "tmax" to """^\d+(\.\d+)?\s?時間$""",
                "totalDailyDose" to """^\d+(\.\d+)?\s?(mg|g)$""",
                "urinaryExcretionRatio" to """^\d+(\.\d+)?\s?%$""",
                "volumeOfDistribution" to """^\d+(\.\d+)?\s?L/kg$""",
            )

        init {
            check(CATEGORY_D_KEYS.size == 32) {
                "CATEGORY_D_KEYS must contain exactly 32 keys; got ${CATEGORY_D_KEYS.size}"
            }
            check(CATEGORY_D_FORMAT_REGEX.keys == CATEGORY_D_KEYS.toSet()) {
                "CATEGORY_D_FORMAT_REGEX keys must equal CATEGORY_D_KEYS set"
            }
        }
    }
}
