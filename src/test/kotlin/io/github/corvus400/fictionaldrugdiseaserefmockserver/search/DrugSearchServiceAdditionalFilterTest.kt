package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReaction
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PrecautionPopulation
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugSearchServiceAdditionalFilterTest {
    @Test
    fun `applyAdditionalFilters with adverseReactionKeyword filters by serious name partial match`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                serious = listOf(seriousReaction(name = "xxx症候群")),
            ),
            stubDrug(
                id = "drug_0002",
                serious = listOf(seriousReaction(name = "別の重篤副作用")),
            ),
            stubDrug(
                id = "drug_0003",
                serious = listOf(
                    seriousReaction(name = "別"),
                    seriousReaction(name = "重度のxxx反応"),
                ),
            ),
            stubDrug(id = "drug_0004", serious = emptyList()),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "xxx",
        )
        assertEquals(listOf("drug_0001", "drug_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with null adverseReactionKeyword returns items unchanged`() {
        val items = listOf(
            stubDrug(id = "drug_0001"),
            stubDrug(id = "drug_0002"),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = null,
        )
        assertEquals(items, result)
    }

    @Test
    fun `applyAdditionalFilters with blank adverseReactionKeyword returns items unchanged`() {
        val items = listOf(
            stubDrug(id = "drug_0001"),
            stubDrug(id = "drug_0002"),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "   ",
        )
        assertEquals(items, result)
    }

    @Test
    fun `applyAdditionalFilters matches when adverseReactionKeyword in over5Percent only (not serious)`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                serious = emptyList(),
                other = AdverseReactionByFrequency(over5Percent = listOf("yyy性発疹")),
            ),
            stubDrug(
                id = "drug_0002",
                serious = listOf(seriousReaction(name = "別の重篤副作用")),
                other = AdverseReactionByFrequency(over5Percent = listOf("無関係")),
            ),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "yyy",
        )
        assertEquals(listOf("drug_0001"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters matches when adverseReactionKeyword in between1And5Percent`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                serious = emptyList(),
                other = AdverseReactionByFrequency(between1And5Percent = listOf("yyy性下痢")),
            ),
            stubDrug(
                id = "drug_0002",
                serious = emptyList(),
                other = AdverseReactionByFrequency(between1And5Percent = listOf("無関係")),
            ),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "yyy",
        )
        assertEquals(listOf("drug_0001"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters matches when adverseReactionKeyword in under1Percent`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                serious = emptyList(),
                other = AdverseReactionByFrequency(under1Percent = listOf("yyy性肝障害")),
            ),
            stubDrug(
                id = "drug_0002",
                serious = emptyList(),
                other = AdverseReactionByFrequency(under1Percent = listOf("無関係")),
            ),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "yyy",
        )
        assertEquals(listOf("drug_0001"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters matches when adverseReactionKeyword in frequencyUnknown`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                serious = emptyList(),
                other = AdverseReactionByFrequency(frequencyUnknown = listOf("yyy性めまい")),
            ),
            stubDrug(
                id = "drug_0002",
                serious = emptyList(),
                other = AdverseReactionByFrequency(frequencyUnknown = listOf("無関係")),
            ),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            adverseReactionKeyword = "yyy",
        )
        assertEquals(listOf("drug_0001"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with precautionCategories=PREGNANT keeps only drugs containing PREGNANT`() {
        val items = listOf(
            stubDrug(
                id = "drug_0001",
                precautions = listOf(
                    precautionPopulation(category = PrecautionPopulationCategory.PREGNANT),
                ),
            ),
            stubDrug(
                id = "drug_0002",
                precautions = listOf(
                    precautionPopulation(category = PrecautionPopulationCategory.GERIATRIC),
                ),
            ),
            stubDrug(
                id = "drug_0003",
                precautions = listOf(
                    precautionPopulation(category = PrecautionPopulationCategory.RENAL_IMPAIRMENT),
                    precautionPopulation(category = PrecautionPopulationCategory.PREGNANT),
                ),
            ),
            stubDrug(id = "drug_0004", precautions = emptyList()),
        )
        val result = DrugSearchService.applyAdditionalFilters(
            items = items,
            precautionCategories = listOf(PrecautionPopulationCategory.PREGNANT),
        )
        assertEquals(listOf("drug_0001", "drug_0003"), result.map { it.id })
    }

    private fun precautionPopulation(category: PrecautionPopulationCategory): PrecautionPopulation =
        PrecautionPopulation(
            category = category,
            note = "テスト注意文",
        )

    private fun seriousReaction(name: String): AdverseReaction = AdverseReaction(
        name = name,
        frequency = FrequencyBand.UNKNOWN,
        symptom = "症状",
        initialSigns = "初期症状",
        countermeasure = "投与中止",
    )

    private fun stubDrug(
        id: String,
        serious: List<AdverseReaction> = emptyList(),
        other: AdverseReactionByFrequency = AdverseReactionByFrequency(),
        precautions: List<PrecautionPopulation> = emptyList(),
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
            adverseReactions = AdverseReactionInfo(
                serious = serious,
                other = other,
            ),
            precautionsForSpecificPopulations = precautions,
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
