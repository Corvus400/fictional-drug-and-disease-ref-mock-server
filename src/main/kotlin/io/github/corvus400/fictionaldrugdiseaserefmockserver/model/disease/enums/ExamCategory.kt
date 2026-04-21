package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ExamCategory {
    @SerialName("血液検査")
    BLOOD_TEST,

    @SerialName("画像検査")
    IMAGING,

    @SerialName("生理検査")
    PHYSIOLOGICAL,

    @SerialName("病理検査")
    PATHOLOGY,

    @SerialName("問診")
    INTERVIEW,
}
