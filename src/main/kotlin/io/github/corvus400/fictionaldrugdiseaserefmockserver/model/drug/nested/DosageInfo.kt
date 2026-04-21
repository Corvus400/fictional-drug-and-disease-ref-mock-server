package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import kotlinx.serialization.Serializable

@Serializable
data class DosageInfo(
    val standardDosage: String,
    val ageSpecificDosage: List<AgeDosage> = emptyList(),
    val renalAdjustment: List<RenalDose> = emptyList(),
    val hepaticAdjustment: List<HepaticDose> = emptyList(),
)

@Serializable
data class AgeDosage(
    val range: AgeRange,
    val dose: String,
)

@Serializable
data class AgeRange(
    val minAgeMonths: Int?,
    val maxAgeMonths: Int?,
    val label: String,
)

@Serializable
data class RenalDose(
    val range: CrClRange,
    val dose: String,
)

@Serializable
data class CrClRange(
    val minMlPerMin: Int?,
    val maxMlPerMin: Int?,
    val severity: RenalSeverity,
    val label: String,
)

@Serializable
data class HepaticDose(
    val severity: HepaticSeverity,
    val dose: String,
)
