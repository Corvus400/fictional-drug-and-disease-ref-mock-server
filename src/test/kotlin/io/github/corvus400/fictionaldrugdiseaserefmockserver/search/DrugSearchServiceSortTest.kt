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

    private fun drugWith(
        id: String,
        revisedAt: String,
    ): Drug =
        Drug(
            id = id,
            genericName = "テスト一般名",
            brandName = "テスト販売名",
            brandNameKana = "テストハンバイメイ",
            atcCode = "N02BE01",
            therapeuticCategoryName = "経口鎮痛薬",
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
