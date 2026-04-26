package io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo

fun sampleDiseases(n: Int): List<Disease> =
    (1..n).map { index ->
        sampleDisease(id = "disease_${index.toString().padStart(4, '0')}")
    }

fun sampleDisease(id: String): Disease =
    Disease(
        id = id,
        name = "サンプル疾患$id",
        nameKana = "サンプルシッカン",
        icd10Chapter = Icd10Chapter.CHAPTER_I,
        medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
        chronicity = Chronicity.ACUTE,
        infectious = false,
        summary = "サンプル概要",
        etiology = "サンプル病因",
        symptoms = SymptomInfo(mainSymptoms = listOf("頭痛")),
        diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf("基準A")),
        treatments = TreatmentInfo(),
        revisedAt = "2026-04-26",
    )
