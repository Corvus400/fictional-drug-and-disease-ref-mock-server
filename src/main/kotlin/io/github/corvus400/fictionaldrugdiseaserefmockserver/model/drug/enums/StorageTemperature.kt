package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StorageTemperature {
    @SerialName("室温")
    ROOM_TEMPERATURE,

    @SerialName("冷所")
    COLD,

    @SerialName("冷凍")
    FROZEN,
}
