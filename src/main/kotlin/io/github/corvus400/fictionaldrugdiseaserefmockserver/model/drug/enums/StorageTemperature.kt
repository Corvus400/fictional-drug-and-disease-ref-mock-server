package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StorageTemperature {
    /** 室温 */
    @SerialName("room_temperature")
    ROOM_TEMPERATURE,

    /** 冷所 */
    @SerialName("cold")
    COLD,

    @SerialName("冷凍")
    FROZEN,
}
