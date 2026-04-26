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

class DrugSearchServiceKeywordTest {
    @Test
    fun `applyKeyword with null keyword returns items unchanged`() {
        val items = sampleDrugs(3)
        val result = DrugSearchService.applyKeyword(
            items = items,
            keyword = null,
            match = KeywordMatch.PARTIAL,
            target = DrugKeywordTarget.BOTH,
        )
        assertEquals(items, result)
    }

    @Test
    fun `applyKeyword with target GENERIC and partial match filters by genericName contains`() {
        val items = listOf(
            stubDrug(id = "drug_0001", genericName = "サンプルチン"),
            stubDrug(id = "drug_0002", genericName = "別名称"),
        )
        val result = DrugSearchService.applyKeyword(
            items = items,
            keyword = "ルチ",
            match = KeywordMatch.PARTIAL,
            target = DrugKeywordTarget.GENERIC,
        )
        assertEquals(listOf("drug_0001"), result.map { it.id })
    }

    @Test
    fun `applyKeyword with target BRAND matches brandName OR brandNameKana`() {
        val items = listOf(
            stubDrug(id = "drug_0001", brandName = "スーパーX錠", brandNameKana = "スーパーエックスジョウ"),
            stubDrug(id = "drug_0002", brandName = "別製品", brandNameKana = "ベツセイヒン"),
            stubDrug(id = "drug_0003", brandName = "異名", brandNameKana = "スーパー"),
        )
        val result = DrugSearchService.applyKeyword(
            items = items,
            keyword = "スーパー",
            match = KeywordMatch.PARTIAL,
            target = DrugKeywordTarget.BRAND,
        )
        assertEquals(listOf("drug_0001", "drug_0003"), result.map { it.id })
    }

    private fun sampleDrugs(n: Int): List<Drug> = (1..n).map { index ->
        stubDrug(id = "drug_%04d".format(index))
    }

    private fun stubDrug(
        id: String,
        genericName: String = "テスト一般名",
        brandName: String = "テスト販売名",
        brandNameKana: String = "テストハンバイメイ",
    ): Drug =
        Drug(
            id = id,
            genericName = genericName,
            brandName = brandName,
            brandNameKana = brandNameKana,
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
            revisedAt = "2024-03-01",
        )
}
