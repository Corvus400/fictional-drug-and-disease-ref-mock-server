package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ExamCategory {
    /** 血液検査 */
    @SerialName("blood_test")
    BLOOD_TEST,

    /** 画像検査 */
    @SerialName("imaging")
    IMAGING,

    /** 生理検査 */
    @SerialName("physiological")
    PHYSIOLOGICAL,

    @SerialName("病理検査")
    PATHOLOGY,

    @SerialName("問診")
    INTERVIEW,
}
