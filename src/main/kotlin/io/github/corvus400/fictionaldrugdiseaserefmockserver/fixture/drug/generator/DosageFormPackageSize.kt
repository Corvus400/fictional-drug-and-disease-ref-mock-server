package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

object DosageFormPackageSize {
    fun pickSize(
        form: DosageForm,
        drugId: String,
    ): String {
        return when (form) {
            DosageForm.TABLET -> formatTabletLike(drugId = drugId, unit = "錠")
            DosageForm.CAPSULE -> formatTabletLike(drugId = drugId, unit = "カプセル")
            DosageForm.SUPPOSITORY -> formatTabletLike(drugId = drugId, unit = "個")
            DosageForm.POWDER -> formatPowderSachet(drugId = drugId)
            DosageForm.GRANULE -> formatPowderSachet(drugId = drugId)
            DosageForm.LIQUID -> formatLiquidBottle(drugId = drugId)
            DosageForm.INJECTION_FORM -> formatInjectionVial(drugId = drugId)
            DosageForm.OINTMENT -> formatOintmentTube(drugId = drugId)
            DosageForm.CREAM -> formatOintmentTube(drugId = drugId)
            DosageForm.PATCH -> formatPatchPack(drugId = drugId)
            DosageForm.EYE_DROPS -> formatEyeDropBottle(drugId = drugId)
            DosageForm.INHALER -> formatInhalerCanister(drugId = drugId)
            DosageForm.NASAL_SPRAY -> formatNasalSprayBottle(drugId = drugId)
        }
    }

    private fun formatTabletLike(
        drugId: String,
        unit: String,
    ): String {
        val perPtp = pickValue(drugId = drugId, slotSubKey = "tabletLike.perPtp", range = PER_PTP_RANGE)
        val ptpCount = pickValue(drugId = drugId, slotSubKey = "tabletLike.ptpCount", range = PTP_COUNT_RANGE)
        val total = perPtp * ptpCount
        return "$total $unit ($perPtp $unit × $ptpCount PTP)"
    }

    private fun formatPowderSachet(drugId: String): String {
        val gramsPerSachet = pickValue(drugId = drugId, slotSubKey = "powder.grams", range = SACHET_GRAMS_RANGE)
        val sachetCount = pickValue(drugId = drugId, slotSubKey = "powder.count", range = SACHET_COUNT_RANGE)
        return "$sachetCount 包 ($gramsPerSachet g × $sachetCount 包)"
    }

    private fun formatLiquidBottle(drugId: String): String {
        val mlPerBottle = pickValue(drugId = drugId, slotSubKey = "liquid.ml", range = LIQUID_ML_RANGE)
        val bottleCount = pickValue(drugId = drugId, slotSubKey = "liquid.bottles", range = LIQUID_BOTTLE_RANGE)
        return "$mlPerBottle mL × $bottleCount 瓶"
    }

    private fun formatInjectionVial(drugId: String): String {
        val mlPerVial = pickValue(drugId = drugId, slotSubKey = "injection.ml", range = INJECTION_ML_RANGE)
        val vialCount = pickValue(drugId = drugId, slotSubKey = "injection.count", range = INJECTION_VIAL_RANGE)
        val container =
            ValueRangeGenerator.pickOne(
                seed =
                stableHash(
                    id = "$drugId#injection.container",
                    slot = DrugFieldSlot.PACKAGE_SIZE_PICK.ordinal,
                    index = 0,
                ),
                candidates = INJECTION_CONTAINERS,
            )
        return "$mlPerVial mL × $vialCount $container"
    }

    private fun formatOintmentTube(drugId: String): String {
        val grams = pickValue(drugId = drugId, slotSubKey = "ointment.grams", range = OINTMENT_GRAMS_RANGE)
        return "$grams g × 1 本 (チューブ)"
    }

    private fun formatPatchPack(drugId: String): String {
        val sheetsPerBag = pickValue(drugId = drugId, slotSubKey = "patch.sheets", range = PATCH_SHEETS_RANGE)
        val bagCount = pickValue(drugId = drugId, slotSubKey = "patch.bags", range = PATCH_BAG_RANGE)
        return "$sheetsPerBag 枚 × $bagCount 袋"
    }

    private fun formatEyeDropBottle(drugId: String): String {
        val mlPerBottle = pickValue(drugId = drugId, slotSubKey = "eyeDrop.ml", range = EYE_DROP_ML_RANGE)
        val bottleCount = pickValue(drugId = drugId, slotSubKey = "eyeDrop.bottles", range = EYE_DROP_BOTTLE_RANGE)
        return "$mlPerBottle mL × $bottleCount 本"
    }

    private fun formatInhalerCanister(drugId: String): String {
        val puffsPerCanister = pickValue(drugId = drugId, slotSubKey = "inhaler.puffs", range = INHALER_PUFF_RANGE)
        val canisterCount = pickValue(drugId = drugId, slotSubKey = "inhaler.count", range = INHALER_COUNT_RANGE)
        return if (canisterCount == 1) {
            "1 本 ($puffsPerCanister 噴霧)"
        } else {
            "$canisterCount 本 ($puffsPerCanister 噴霧 × $canisterCount)"
        }
    }

    private fun formatNasalSprayBottle(drugId: String): String {
        val mlPerCanister = pickValue(drugId = drugId, slotSubKey = "nasal.ml", range = NASAL_ML_RANGE)
        val canisterCount = pickValue(drugId = drugId, slotSubKey = "nasal.count", range = NASAL_COUNT_RANGE)
        return if (canisterCount == 1) {
            "1 本 ($mlPerCanister mL)"
        } else {
            "$canisterCount 本 ($mlPerCanister mL × $canisterCount)"
        }
    }

    private fun pickValue(
        drugId: String,
        slotSubKey: String,
        range: IntRange,
    ): Int =
        ValueRangeGenerator.pickInRange(
            seed =
            stableHash(
                id = "$drugId#$slotSubKey",
                slot = DrugFieldSlot.PACKAGE_SIZE_PICK.ordinal,
                index = 0,
            ),
            range = range,
        )

    internal val PER_PTP_RANGE: IntRange = 10..10
    internal val PTP_COUNT_RANGE: IntRange = 3..10
    internal val SACHET_GRAMS_RANGE: IntRange = 1..3
    internal val SACHET_COUNT_RANGE: IntRange = 30..100
    internal val LIQUID_ML_RANGE: IntRange = 60..500
    internal val LIQUID_BOTTLE_RANGE: IntRange = 1..5
    internal val INJECTION_ML_RANGE: IntRange = 1..20
    internal val INJECTION_VIAL_RANGE: IntRange = 1..10
    internal val INJECTION_CONTAINERS: List<String> = listOf("アンプル", "バイアル")
    internal val OINTMENT_GRAMS_RANGE: IntRange = 5..50
    internal val PATCH_SHEETS_RANGE: IntRange = 7..14
    internal val PATCH_BAG_RANGE: IntRange = 1..4
    internal val EYE_DROP_ML_RANGE: IntRange = 1..15
    internal val EYE_DROP_BOTTLE_RANGE: IntRange = 1..10
    internal val INHALER_PUFF_RANGE: IntRange = 60..200
    internal val INHALER_COUNT_RANGE: IntRange = 1..2
    internal val NASAL_ML_RANGE: IntRange = 5..15
    internal val NASAL_COUNT_RANGE: IntRange = 1..2
}
