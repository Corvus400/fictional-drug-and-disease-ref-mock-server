package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common

import kotlinx.serialization.Serializable

@Serializable
data class AtcEntry(
    val code: String,
    val label: String,
)

@Serializable
data class TherapeuticCategoryEntry(
    val id: String,
    val label: String,
)

@Serializable
data class Icd10ChapterEntry(
    val roman: String,
    val code: String,
    val label: String,
)

@Serializable
data class LabeledEntry(
    val value: String,
    val label: String,
)

@Serializable
data class CategoriesResponse(
    val atc: List<AtcEntry>,
    val therapeuticCategories: List<TherapeuticCategoryEntry>,
    val routeOfAdministration: List<LabeledEntry>,
    val dosageForm: List<LabeledEntry>,
    val regulatoryClass: List<LabeledEntry>,
    val icd10Chapters: List<Icd10ChapterEntry>,
    val medicalDepartments: List<LabeledEntry>,
)
