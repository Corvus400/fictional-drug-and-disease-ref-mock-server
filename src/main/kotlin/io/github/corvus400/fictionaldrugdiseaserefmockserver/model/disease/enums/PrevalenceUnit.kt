package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PrevalenceUnit {
    /** 人口対 */
    @SerialName("per_population")
    PER_POPULATION,

    @SerialName("患者対")
    PER_PATIENT,

    @SerialName("出生対")
    PER_BIRTH,
}
