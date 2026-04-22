package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator

private const val UNIT_SALT: Long = 0x5A5A5A5A5A5A5A5AL
private const val RANGE_END_SALT: Long = 0x3C3C3C3C3C3C3C3CL

private fun derive(
    seed: Long,
    salt: Long,
): Long = seed xor salt

private fun Double.formatNumeric(): String = "%.1f".format(this)

object NumericPlaceholderRanges {
    fun resolve(
        key: String,
        seed: Long,
    ): String {
        val builder =
            BUILDERS[key]
                ?: error(
                    "Unknown category-D placeholder key '$key'. " +
                        "NumericPlaceholderRanges covers only the 32 category-D numeric keys. " +
                        "Other categories (A/B/C) are resolved by DrugPlaceholderDictionary.",
                )
        return builder(seed)
    }

    private val BUILDERS: Map<String, (Long) -> String> =
        mapOf(
            "auc" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 0..98)
                val value = 100 + bucket * 50
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("ng·h/mL", "μg·h/mL"))
                "$value $unit"
            },
            "bioavailability" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 10..99)} %"
            },
            "cmax" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..200)
                val value = bucket * 0.5
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("ng/mL", "μg/mL"))
                "${value.formatNumeric()} $unit"
            },
            "cnsRatio" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..500)
                val value = bucket * 0.1
                "${value.formatNumeric()} %"
            },
            "doseAmount" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..100)
                val value = bucket * 5
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("μg", "mg", "g"))
                "$value $unit"
            },
            "dosePerKg" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..200)
                val value = bucket * 0.1
                "${value.formatNumeric()} mg/kg"
            },
            "durationDays" to { seed ->
                val n = ValueRangeGenerator.pickInRange(seed, 1..90)
                val suffix = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("日", "日間"))
                "$n $suffix"
            },
            "durationWeeks" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 1..52)} 週間"
            },
            "efficacyRate" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 100..999)
                val value = bucket * 0.1
                "${value.formatNumeric()} %"
            },
            "fecalExcretionRatio" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 0..990)
                val value = bucket * 0.1
                "${value.formatNumeric()} %"
            },
            "foodEffectRatio" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 5..30)
                val value = bucket * 0.1
                "${value.formatNumeric()} 倍"
            },
            "frequency" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 1..4)} 回"
            },
            "halfLife" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 2..48)
                val value = bucket * 0.5
                "${value.formatNumeric()} 時間"
            },
            "ic50" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..1000)
                val value = bucket * 0.1
                "${value.formatNumeric()} nM"
            },
            "interval" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..6)
                "${bucket * 4} 時間"
            },
            "maxDailyDose" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..30)
                val value = bucket * 100
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("mg", "g"))
                "$value $unit"
            },
            "meltingPoint" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 80..300)} ℃"
            },
            "packageSize" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..10)
                val value = bucket * 10
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("錠", "包", "本"))
                "$value $unit"
            },
            "patientCount" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..20)
                "${bucket * 100} 例"
            },
            "pKa" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 20..100)
                val value = bucket * 0.1
                value.formatNumeric()
            },
            "proteinBinding" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 10..99)} %"
            },
            "pValue" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..50)
                val threeDigits = bucket.toString().padStart(3, '0')
                "< 0.$threeDigits"
            },
            "reductionRatio" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..19)
                "${bucket * 5} %"
            },
            "referenceRange" to { seed ->
                val startBucket = ValueRangeGenerator.pickInRange(seed, 10..80)
                val endBucket = ValueRangeGenerator.pickInRange(
                    derive(seed, RANGE_END_SALT),
                    (startBucket + 5)..(startBucket + 50)
                )
                val start = startBucket * 0.1
                val end = endBucket * 0.1
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("mg/dL", "μg/mL", "nmol/L"))
                "${start.formatNumeric()} - ${end.formatNumeric()} $unit"
            },
            "reimbursementDurationDays" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 14..180)} 日"
            },
            "retentionRate" to { seed ->
                "${ValueRangeGenerator.pickInRange(seed, 10..99)} %"
            },
            "stableDuration" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..10)
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("か月", "年"))
                "${bucket * 6} $unit"
            },
            "storageTemperature" to { seed ->
                val start = ValueRangeGenerator.pickInRange(seed, 1..20)
                val end = ValueRangeGenerator.pickInRange(derive(seed, RANGE_END_SALT), (start + 1)..(start + 10))
                "$start - $end ℃"
            },
            "tmax" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..24)
                val value = bucket * 0.5
                "${value.formatNumeric()} 時間"
            },
            "totalDailyDose" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..30)
                val value = bucket * 100
                val unit = ValueRangeGenerator.pickOne(derive(seed, UNIT_SALT), listOf("mg", "g"))
                "$value $unit"
            },
            "urinaryExcretionRatio" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 0..990)
                val value = bucket * 0.1
                "${value.formatNumeric()} %"
            },
            "volumeOfDistribution" to { seed ->
                val bucket = ValueRangeGenerator.pickInRange(seed, 1..100)
                val value = bucket * 0.1
                "${value.formatNumeric()} L/kg"
            },
        )
}
