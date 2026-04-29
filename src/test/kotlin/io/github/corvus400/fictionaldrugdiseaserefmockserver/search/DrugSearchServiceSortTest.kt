package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugSearchServiceSortTest {
    @Test
    fun `applySort returns items sorted by revisedAt descending when sort is REVISED_AT_DESC`() {
        val items = listOf(
            drugWith(id = "drug_0001", revisedAt = "2026-01-10"),
            drugWith(id = "drug_0002", revisedAt = "2026-03-20"),
            drugWith(id = "drug_0003", revisedAt = "2026-02-01"),
        )
        val sorted = DrugSearchService.applySort(items = items, sort = DrugSortKey.REVISED_AT_DESC)
        assertEquals(listOf("drug_0002", "drug_0003", "drug_0001"), sorted.map { it.id })
    }

    @Test
    fun `applySort breaks revisedAt ties by id descending when sort is REVISED_AT_DESC`() {
        val items = listOf(
            drugWith(id = "drug_0001", revisedAt = "2026-04-23"),
            drugWith(id = "drug_0002", revisedAt = "2026-04-23"),
            drugWith(id = "drug_0003", revisedAt = "2026-04-23"),
        )
        val sorted = DrugSearchService.applySort(items = items, sort = DrugSortKey.REVISED_AT_DESC)
        assertEquals(listOf("drug_0003", "drug_0002", "drug_0001"), sorted.map { it.id })
    }

    @Test
    fun `applySort sorts by brandNameKana ascending when BRAND_NAME_KANA_ASC is specified`() {
        val items = listOf(
            drugWith(id = "drug_0001", brandNameKana = "サンブル"),
            drugWith(id = "drug_0002", brandNameKana = "アイウエ"),
            drugWith(id = "drug_0003", brandNameKana = "カキクケ"),
        )
        val sorted = DrugSearchService.applySort(items = items, sort = DrugSortKey.BRAND_NAME_KANA_ASC)
        assertEquals(listOf("drug_0002", "drug_0003", "drug_0001"), sorted.map { it.id })
    }

    @Test
    fun `applySort sorts by atcCode ascending when ATC_CODE_ASC is specified`() {
        val items = listOf(
            drugWith(id = "drug_0001", atcCode = "C09AA02"),
            drugWith(id = "drug_0002", atcCode = "A10BA02"),
            drugWith(id = "drug_0003", atcCode = "B01AC06"),
        )
        val sorted = DrugSearchService.applySort(items = items, sort = DrugSortKey.ATC_CODE_ASC)
        assertEquals(listOf("drug_0002", "drug_0003", "drug_0001"), sorted.map { it.id })
    }

    @Test
    fun `applySort sorts by therapeuticCategoryName ascending when THERAPEUTIC_CATEGORY_NAME_ASC is specified`() {
        val items = listOf(
            drugWith(id = "drug_0002", therapeuticCategoryName = "B降圧薬"),
            drugWith(id = "drug_0003", therapeuticCategoryName = "C抗菌薬"),
            drugWith(id = "drug_0001", therapeuticCategoryName = "A解熱鎮痛薬"),
        )
        val sorted = DrugSearchService.applySort(items = items, sort = DrugSortKey.THERAPEUTIC_CATEGORY_NAME_ASC)
        assertEquals(listOf("drug_0001", "drug_0002", "drug_0003"), sorted.map { it.id })
    }

    private fun drugWith(
        id: String,
        revisedAt: String = "2026-01-01",
        brandNameKana: String = "テストハンバイメイ",
        atcCode: String = "N02BE01",
        therapeuticCategoryName: String = "経口鎮痛薬",
    ): Drug =
        Drug(
            id = id,
            genericName = "テスト一般名",
            brandName = "テスト販売名",
            brandNameKana = brandNameKana,
            atcCode = atcCode,
            therapeuticCategoryName = therapeuticCategoryName,
            regulatoryClass = listOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
            dosageForm = DosageForm.TABLET,
            routeOfAdministration = RouteOfAdministration.ORAL,
            composition = CompositionInfo(
                activeIngredient = "サンプルシン",
                activeIngredientAmount = Dose(amount = 100.0, unit = DoseUnit.MG, per = "1 錠中"),
                appearance = "白色の円形フィルムコーティング錠",
            ),
            contraindications = listOf(
                NumberedParagraph(order = 1, content = "本剤の成分に対し過敏症の既往歴のある患者"),
            ),
            indications = listOf(IndicationItem(order = 1, content = "各種疾患における鎮痛")),
            dosage = DosageInfo(standardDosage = "通常、成人には 1 回 100 mg を経口投与"),
            adverseReactions = AdverseReactionInfo(other = AdverseReactionByFrequency()),
            packages = listOf(
                PackageInfo(
                    size = "100 錠 (10 錠 × 10 PTP)",
                    storageCondition = StorageCondition(
                        temperature = StorageTemperature.ROOM_TEMPERATURE,
                        lightProtection = false,
                        moistureProtection = false,
                    ),
                    expirationMonths = 36,
                ),
            ),
            manufacturer = "架空製薬株式会社",
            revisedAt = revisedAt,
        )
}
