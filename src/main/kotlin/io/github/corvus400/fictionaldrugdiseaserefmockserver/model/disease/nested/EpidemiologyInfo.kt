package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import kotlinx.serialization.Serializable

@Serializable
data class EpidemiologyInfo(
    val prevalence: Prevalence? = null,
    val onsetAgeRange: OnsetAgeRange? = null,
    val sexRatio: SexDistribution? = null,
    val riskFactors: List<String> = emptyList(),
)

@Serializable
data class Prevalence(
    val rate: Double?,
    val denominator: Int?,
    val unit: PrevalenceUnit = PrevalenceUnit.PER_POPULATION,
    val label: String,
)

@Serializable
data class OnsetAgeRange(
    val minAgeYears: Int?,
    val maxAgeYears: Int?,
    val label: String,
)

@Serializable
data class SexDistribution(
    val maleRatio: Int,
    val femaleRatio: Int,
    val note: String? = null,
)
