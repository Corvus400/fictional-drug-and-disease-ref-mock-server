package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.EpidemiologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Exam
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SeverityInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import kotlinx.serialization.Serializable

@Serializable
data class Disease(
    val id: String,
    val name: String,
    val nameKana: String,
    val nameEnglish: String? = null,
    val icd10Chapter: Icd10Chapter,
    val medicalDepartment: List<MedicalDepartment>,
    val chronicity: Chronicity,
    val infectious: Boolean,
    val synonyms: List<String> = emptyList(),
    val summary: String,
    val epidemiology: EpidemiologyInfo? = null,
    val etiology: String,
    val symptoms: SymptomInfo,
    val diagnosticCriteria: DiagnosticCriteriaInfo,
    val requiredExams: List<Exam> = emptyList(),
    val severityGrading: SeverityInfo? = null,
    val differentialDiagnoses: List<String> = emptyList(),
    val complications: List<String> = emptyList(),
    val treatments: TreatmentInfo,
    val prognosis: String? = null,
    val prevention: List<String> = emptyList(),
    val relatedDrugIds: List<String> = emptyList(),
    val relatedDiseaseIds: List<String> = emptyList(),
    val revisedAt: String,
)
