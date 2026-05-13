package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AgeRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PkParameter

internal object DrugSeedDerivedValues {
    fun standardDoseAmount(
        id: String,
        form: DosageForm,
    ): Double {
        val spec = doseAmountSpec(form = form)
        val seed = stableHash(id = id, slot = DrugFieldSlot.STANDARD_DOSE_AMOUNT_PICK.ordinal, index = 0)
        return ValueRangeGenerator.pickInRange(seed = seed, range = spec.range).toDouble() / spec.scale
    }

    fun molecularFormula(
        id: String,
        atcInitial: Char,
    ): String {
        val formulaSeed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 0)
        val carbonBase = carbonBaseFor(atcInitial = atcInitial.uppercaseChar())
        val carbon = carbonBase + ValueRangeGenerator.pickInRange(seed = formulaSeed, range = 0..7)
        val hydrogen =
            carbon + ValueRangeGenerator.pickInRange(
                seed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 1),
                range = 8..26,
            )
        val nitrogen =
            ValueRangeGenerator.pickInRange(
                seed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 2),
                range = 1..5,
            )
        val oxygen =
            ValueRangeGenerator.pickInRange(
                seed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 3),
                range = 1..8,
            )
        val sulfur =
            ValueRangeGenerator.pickInRange(
                seed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 4),
                range = 0..1,
            )
        val chlorine =
            ValueRangeGenerator.pickInRange(
                seed = stableHash(id = id, slot = DrugFieldSlot.MOLECULAR_FORMULA_PICK.ordinal, index = 5),
                range = 0..1,
            )
        return buildString {
            append("C$carbon")
            append("H$hydrogen")
            append("N$nitrogen")
            append("O$oxygen")
            if (sulfur > 0) append("S$sulfur")
            if (chlorine > 0) append("Cl$chlorine")
        }
    }

    fun pharmacokineticParameters(id: String): List<PkParameter> =
        listOf(
            PkParameter(
                name = "Cmax",
                value = "${decimalValue(id = id, index = 0, range = 12..96, scale = 10)} μg/mL",
            ),
            PkParameter(
                name = "T1/2",
                value = "${decimalValue(id = id, index = 1, range = 15..240, scale = 10)} 時間",
            ),
            PkParameter(
                name = "AUC",
                value = "${decimalValue(id = id, index = 2, range = 80..720, scale = 10)} μg・時/mL",
            ),
        )

    fun lightProtection(
        id: String,
        form: DosageForm,
        isBiological: Boolean,
    ): Boolean =
        isBiological ||
            form in LIGHT_SENSITIVE_FORMS ||
            booleanFromSeed(id = id, slot = DrugFieldSlot.STORAGE_LIGHT_PICK, index = 0)

    fun moistureProtection(
        id: String,
        form: DosageForm,
    ): Boolean =
        form in MOISTURE_SENSITIVE_FORMS ||
            booleanFromSeed(id = id, slot = DrugFieldSlot.STORAGE_MOISTURE_PICK, index = 0)

    fun pediatricAgeRange(
        id: String,
        form: DosageForm,
    ): AgeRange {
        val candidates = pediatricAgeRangeCandidates(form = form)
        val seed = stableHash(id = id, slot = DrugFieldSlot.PEDIATRIC_AGE_RANGE_PICK.ordinal, index = 0)
        return ValueRangeGenerator.pickOne(seed = seed, candidates = candidates)
    }

    fun clinicalResultHeading(
        id: String,
        offset: Int,
    ): String {
        val seed = stableHash(id = id, slot = DrugFieldSlot.CLINICAL_RESULT_HEADING_PICK.ordinal, index = offset + 1)
        return ValueRangeGenerator.pickOne(seed = seed, candidates = CLINICAL_RESULT_HEADINGS)
    }

    private fun decimalValue(
        id: String,
        index: Int,
        range: IntRange,
        scale: Int,
    ): String {
        val seed = stableHash(id = id, slot = DrugFieldSlot.PK_PARAMETER_PICK.ordinal, index = index)
        return (ValueRangeGenerator.pickInRange(seed = seed, range = range).toDouble() / scale).toString()
    }

    private fun booleanFromSeed(
        id: String,
        slot: DrugFieldSlot,
        index: Int,
    ): Boolean {
        val seed = stableHash(id = id, slot = slot.ordinal, index = index)
        return ValueRangeGenerator.pickInRange(seed = seed, range = 0..1) == 1
    }

    private fun doseAmountSpec(form: DosageForm): DoseAmountSpec =
        when (form) {
            DosageForm.TABLET,
            DosageForm.CAPSULE,
            DosageForm.SUPPOSITORY,
            -> DoseAmountSpec(range = 25..500, scale = 1)
            DosageForm.POWDER,
            DosageForm.GRANULE,
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> DoseAmountSpec(range = 5..100, scale = 10)
            DosageForm.LIQUID,
            DosageForm.EYE_DROPS,
            DosageForm.NASAL_SPRAY,
            -> DoseAmountSpec(range = 10..200, scale = 10)
            DosageForm.INJECTION_FORM -> DoseAmountSpec(range = 5..1000, scale = 10)
            DosageForm.PATCH,
            DosageForm.INHALER,
            -> DoseAmountSpec(range = 25..250, scale = 1)
        }

    private fun carbonBaseFor(atcInitial: Char): Int =
        when (atcInitial) {
            'A' -> 14
            'B' -> 16
            'C' -> 18
            'D' -> 12
            'G' -> 20
            'H' -> 22
            'J' -> 15
            'L' -> 24
            'M' -> 17
            'N' -> 19
            'P' -> 13
            'R' -> 11
            'S' -> 10
            else -> 21
        }

    private fun pediatricAgeRangeCandidates(form: DosageForm): List<AgeRange> =
        when (form) {
            DosageForm.EYE_DROPS,
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> listOf(
                AgeRange(minAgeMonths = 1, maxAgeMonths = 24, label = "1 か月以上 2 歳未満"),
                AgeRange(minAgeMonths = 24, maxAgeMonths = 72, label = "2 歳以上 6 歳未満"),
                AgeRange(minAgeMonths = 72, maxAgeMonths = 144, label = "6 歳以上 12 歳未満"),
            )
            DosageForm.INJECTION_FORM,
            DosageForm.SUPPOSITORY,
            -> listOf(
                AgeRange(minAgeMonths = 6, maxAgeMonths = 72, label = "6 か月以上 6 歳未満"),
                AgeRange(minAgeMonths = 72, maxAgeMonths = 144, label = "6 歳以上 12 歳未満"),
                AgeRange(minAgeMonths = 144, maxAgeMonths = 216, label = "12 歳以上 18 歳未満"),
            )
            else -> listOf(
                AgeRange(minAgeMonths = 24, maxAgeMonths = 72, label = "2 歳以上 6 歳未満"),
                AgeRange(minAgeMonths = 72, maxAgeMonths = 144, label = "6 歳以上 12 歳未満"),
                AgeRange(minAgeMonths = 144, maxAgeMonths = 216, label = "12 歳以上 18 歳未満"),
            )
        }

    private data class DoseAmountSpec(
        val range: IntRange,
        val scale: Int,
    )

    private val LIGHT_SENSITIVE_FORMS: Set<DosageForm> =
        setOf(DosageForm.INJECTION_FORM, DosageForm.EYE_DROPS, DosageForm.LIQUID)
    private val MOISTURE_SENSITIVE_FORMS: Set<DosageForm> =
        setOf(DosageForm.TABLET, DosageForm.CAPSULE, DosageForm.POWDER, DosageForm.GRANULE)
    private val CLINICAL_RESULT_HEADINGS: List<String> =
        listOf("有効性", "安全性", "長期投与試験", "用量反応", "部分集団解析", "投与継続性")
}
