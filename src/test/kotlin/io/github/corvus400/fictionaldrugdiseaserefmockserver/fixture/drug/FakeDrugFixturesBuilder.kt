package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition

class FakeDrugFixturesBuilder {
    private val drugsByCategory: MutableMap<TherapeuticCategory, List<String>> = mutableMapOf()

    fun withCategory(
        category: TherapeuticCategory,
        ids: List<String>,
    ): FakeDrugFixturesBuilder {
        drugsByCategory[category] = ids
        return this
    }

    fun build(): List<Drug> =
        drugsByCategory.flatMap { (category, ids) ->
            ids.map { id -> drug(id = id, category = category) }
        }

    private fun drug(
        id: String,
        category: TherapeuticCategory,
    ): Drug =
        Drug(
            id = id,
            genericName = "テスト一般名$id",
            brandName = "テスト販売名$id",
            brandNameKana = "テストハンバイメイ",
            atcCode = "${category.atcInitial}99ZZ${id.takeLast(n = 2)}",
            therapeuticCategoryName = category.displayName,
            regulatoryClass = listOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
            dosageForm = DosageForm.TABLET,
            routeOfAdministration = RouteOfAdministration.ORAL,
            composition = CompositionInfo(
                activeIngredient = "テスト成分$id",
                activeIngredientAmount = Dose(amount = 10.0, unit = DoseUnit.MG, per = "1 錠中"),
                appearance = "白色の円形錠",
            ),
            contraindications = listOf(NumberedParagraph(order = 1, content = "テスト禁忌")),
            indications = listOf(IndicationItem(order = 1, content = "テスト適応")),
            dosage = DosageInfo(standardDosage = "通常、成人には 1 回 1 錠を経口投与する。"),
            adverseReactions = AdverseReactionInfo(other = AdverseReactionByFrequency()),
            packages = listOf(
                PackageInfo(
                    size = "100 錠",
                    storageCondition = StorageCondition(
                        temperature = StorageTemperature.ROOM_TEMPERATURE,
                        lightProtection = false,
                        moistureProtection = false,
                    ),
                    expirationMonths = 36,
                ),
            ),
            manufacturer = "テスト製薬",
            revisedAt = "2026-01-01",
        )
}
