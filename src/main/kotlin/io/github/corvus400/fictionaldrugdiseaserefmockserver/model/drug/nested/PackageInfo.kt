package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import kotlinx.serialization.Serializable

@Serializable
data class PackageInfo(
    val size: String,
    val storageCondition: StorageCondition,
    val expirationMonths: Int,
)

@Serializable
data class StorageCondition(
    val temperature: StorageTemperature,
    val lightProtection: Boolean,
    val moistureProtection: Boolean,
    val additionalNote: String? = null,
)
