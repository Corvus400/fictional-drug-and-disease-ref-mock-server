package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlinx.serialization.Serializable

@Serializable
data class DrugSummary(
    val id: String,
    val brandName: String,
    val genericName: String,
    val therapeuticCategoryName: String,
    val regulatoryClass: List<RegulatoryClass>,
    val dosageForm: DosageForm,
)

fun Drug.toSummary(): DrugSummary =
    DrugSummary(
        id = id,
        brandName = brandName,
        genericName = genericName,
        therapeuticCategoryName = therapeuticCategoryName,
        regulatoryClass = regulatoryClass,
        dosageForm = dosageForm,
    )
