package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import kotlinx.serialization.Serializable

@Serializable
data class DiseaseSummary(
    val id: String,
    val name: String,
    val icd10Chapter: Icd10Chapter,
    val medicalDepartment: List<MedicalDepartment>,
    val chronicity: Chronicity,
    val infectious: Boolean,
)
