package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DosageFormPackageSizeTest {
    @Test
    fun `pickSize for INHALER returns size that does not contain the 錠 unit`() {
        val result: String =
            DosageFormPackageSize.pickSize(form = DosageForm.INHALER, drugId = "drug_0001")
        assertFalse(
            actual = result.contains("錠"),
            message = "INHALER size must not contain '錠', got '$result'",
        )
    }

    @Test
    fun `pickSize returns the same String for the same form and drugId`() {
        val first = DosageFormPackageSize.pickSize(form = DosageForm.INHALER, drugId = "drug_0001")
        val second = DosageFormPackageSize.pickSize(form = DosageForm.INHALER, drugId = "drug_0001")
        assertEquals(expected = first, actual = second, message = "pickSize must be deterministic")
    }

    @Test
    fun `pickSize result never contains a forbidden unit for its form`() {
        val firstViolation: String? =
            DosageForm.entries.firstNotNullOfOrNull { form ->
                val forbidden = FORBIDDEN_UNITS.getValue(form)
                (0 until DRUG_INVENTORY_SIZE).firstNotNullOfOrNull { index ->
                    val drugId = drugIdOf(index = index)
                    val result = DosageFormPackageSize.pickSize(form = form, drugId = drugId)
                    val hit = forbidden.firstOrNull { unit -> result.contains(unit) }
                    if (hit != null) {
                        "pickSize($form, $drugId)='$result' contains forbidden unit '$hit'"
                    } else {
                        null
                    }
                }
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `pickSize result contains at least one required unit for its form`() {
        val firstViolation: String? =
            DosageForm.entries.firstNotNullOfOrNull { form ->
                val required = REQUIRED_UNITS.getValue(form)
                (0 until DRUG_INVENTORY_SIZE).firstNotNullOfOrNull { index ->
                    val drugId = drugIdOf(index = index)
                    val result = DosageFormPackageSize.pickSize(form = form, drugId = drugId)
                    val hit = required.any { unit -> result.contains(unit) }
                    if (!hit) {
                        "pickSize($form, $drugId)='$result' missing all required units $required"
                    } else {
                        null
                    }
                }
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `pickSize for TABLET satisfies total equals perPtp times ptpCount`() {
        val pattern = Regex("""^(\d+) 錠 \((\d+) 錠 × (\d+) PTP\)$""")
        val firstViolation: String? =
            (0 until DRUG_INVENTORY_SIZE).firstNotNullOfOrNull { index ->
                val drugId = drugIdOf(index = index)
                val result = DosageFormPackageSize.pickSize(form = DosageForm.TABLET, drugId = drugId)
                val match =
                    pattern.matchEntire(result)
                        ?: return@firstNotNullOfOrNull "pickSize(TABLET, $drugId)='$result' shape mismatch"
                val (total, perPtp, ptpCount) =
                    Triple(
                        match.groupValues[1].toInt(),
                        match.groupValues[2].toInt(),
                        match.groupValues[3].toInt(),
                    )
                if (total != perPtp * ptpCount) {
                    "pickSize(TABLET, $drugId)='$result' total=$total != " +
                        "perPtp×ptpCount=${perPtp * ptpCount}"
                } else {
                    null
                }
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `pickSize for TABLET produces ptpCount within configured range`() {
        val pattern = Regex("""^\d+ 錠 \(\d+ 錠 × (\d+) PTP\)$""")
        val expectedRange: IntRange = DosageFormPackageSize.PTP_COUNT_RANGE
        val firstViolation: String? =
            (0 until DRUG_INVENTORY_SIZE).firstNotNullOfOrNull { index ->
                val drugId = drugIdOf(index = index)
                val result = DosageFormPackageSize.pickSize(form = DosageForm.TABLET, drugId = drugId)
                val match =
                    pattern.matchEntire(result)
                        ?: return@firstNotNullOfOrNull "shape mismatch: $result"
                val ptpCount = match.groupValues[1].toInt()
                if (ptpCount !in expectedRange) {
                    "pickSize(TABLET, $drugId)='$result' ptpCount=$ptpCount out of $expectedRange"
                } else {
                    null
                }
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `pickSize for TABLET covers every value in PTP_COUNT_RANGE`() {
        val pattern = Regex("""^\d+ 錠 \(\d+ 錠 × (\d+) PTP\)$""")
        val observed: Set<Int> =
            (0 until DRUG_INVENTORY_SIZE).mapNotNull { index ->
                val result = DosageFormPackageSize.pickSize(form = DosageForm.TABLET, drugId = drugIdOf(index))
                pattern.matchEntire(result)?.groupValues?.get(1)?.toInt()
            }.toSet()
        val missing: Set<Int> = DosageFormPackageSize.PTP_COUNT_RANGE.toSet() - observed
        assertEquals(
            expected = emptySet(),
            actual = missing,
            message = "pickSize ptpCount must cover all values in " +
                "${DosageFormPackageSize.PTP_COUNT_RANGE}, missing=$missing",
        )
    }

    @Test
    fun `pickSize for INJECTION_FORM matches mL times count container shape`() {
        val pattern = Regex("""^\d+ mL × \d+ (アンプル|バイアル)$""")
        val firstViolation: String? =
            (0 until DRUG_INVENTORY_SIZE).firstNotNullOfOrNull { index ->
                val drugId = drugIdOf(index = index)
                val result = DosageFormPackageSize.pickSize(form = DosageForm.INJECTION_FORM, drugId = drugId)
                if (!pattern.matches(result)) {
                    "pickSize(INJECTION_FORM, $drugId)='$result' shape mismatch"
                } else {
                    null
                }
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    private companion object {
        const val DRUG_INVENTORY_SIZE: Int = 120

        fun drugIdOf(index: Int): String =
            "drug_${index.toString().padStart(length = 4, padChar = '0')}"

        val REQUIRED_UNITS: Map<DosageForm, Set<String>> =
            mapOf(
                DosageForm.TABLET to setOf("錠"),
                DosageForm.CAPSULE to setOf("カプセル"),
                DosageForm.POWDER to setOf("g", "包"),
                DosageForm.GRANULE to setOf("g", "包"),
                DosageForm.LIQUID to setOf("mL", "瓶"),
                DosageForm.INJECTION_FORM to setOf("mL"),
                DosageForm.OINTMENT to setOf("g", "チューブ"),
                DosageForm.CREAM to setOf("g", "チューブ"),
                DosageForm.PATCH to setOf("枚", "袋"),
                DosageForm.EYE_DROPS to setOf("mL", "本"),
                DosageForm.SUPPOSITORY to setOf("個"),
                DosageForm.INHALER to setOf("本", "噴霧"),
                DosageForm.NASAL_SPRAY to setOf("本", "mL"),
            )

        val FORBIDDEN_UNITS: Map<DosageForm, Set<String>> =
            mapOf(
                DosageForm.TABLET to emptySet(),
                DosageForm.CAPSULE to setOf("錠"),
                DosageForm.POWDER to setOf("錠", "PTP"),
                DosageForm.GRANULE to setOf("錠", "PTP"),
                DosageForm.LIQUID to setOf("錠", "PTP"),
                DosageForm.INJECTION_FORM to setOf("錠", "PTP"),
                DosageForm.OINTMENT to setOf("錠", "PTP", "mL"),
                DosageForm.CREAM to setOf("錠", "PTP", "mL"),
                DosageForm.PATCH to setOf("錠", "PTP"),
                DosageForm.EYE_DROPS to setOf("錠", "PTP"),
                DosageForm.SUPPOSITORY to setOf("錠"),
                DosageForm.INHALER to setOf("錠", "PTP", "mL"),
                DosageForm.NASAL_SPRAY to setOf("錠", "PTP", "噴霧"),
            )
    }
}
