package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StorageTemperature {
    /** 室温 */
    @SerialName("room_temperature")
    ROOM_TEMPERATURE,

    @SerialName("冷所")
    COLD,

    @SerialName("冷凍")
    FROZEN,
}
