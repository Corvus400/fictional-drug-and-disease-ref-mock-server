package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.ClinicalResultSection
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.InteractionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.OverdoseInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacokineticsInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PhysicochemicalInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PrecautionPopulation
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Reference
import kotlinx.serialization.Serializable

@Serializable
data class Drug(
    val id: String,
    val genericName: String,
    val brandName: String,
    val brandNameKana: String,
    val atcCode: String,
    val yjCode: String? = null,
    val therapeuticCategoryName: String,
    val regulatoryClass: List<RegulatoryClass>,
    val dosageForm: DosageForm,
    val routeOfAdministration: RouteOfAdministration,
    val composition: CompositionInfo,
    val warning: List<NumberedParagraph> = emptyList(),
    val contraindications: List<NumberedParagraph>,
    val indications: List<IndicationItem>,
    val indicationsRelatedPrecautions: List<NumberedParagraph> = emptyList(),
    val dosage: DosageInfo,
    val dosageRelatedPrecautions: List<NumberedParagraph> = emptyList(),
    val importantPrecautions: List<NumberedParagraph> = emptyList(),
    val precautionsForSpecificPopulations: List<PrecautionPopulation> = emptyList(),
    val interactions: InteractionInfo? = null,
    val adverseReactions: AdverseReactionInfo,
    val effectsOnLabTests: List<NumberedParagraph> = emptyList(),
    val overdose: OverdoseInfo? = null,
    val administrationPrecautions: List<NumberedParagraph> = emptyList(),
    val otherPrecautions: List<NumberedParagraph> = emptyList(),
    val pharmacokinetics: PharmacokineticsInfo? = null,
    val clinicalResults: List<ClinicalResultSection> = emptyList(),
    val pharmacology: PharmacologyInfo? = null,
    val physicochemicalProperties: PhysicochemicalInfo? = null,
    val handlingPrecautions: List<NumberedParagraph> = emptyList(),
    val approvalConditions: List<NumberedParagraph> = emptyList(),
    val packages: List<PackageInfo>,
    val references: List<Reference> = emptyList(),
    val insuranceNotes: List<NumberedParagraph> = emptyList(),
    val manufacturer: String,
    val revisedAt: String,
    val relatedDiseaseIds: List<String> = emptyList(),
)
