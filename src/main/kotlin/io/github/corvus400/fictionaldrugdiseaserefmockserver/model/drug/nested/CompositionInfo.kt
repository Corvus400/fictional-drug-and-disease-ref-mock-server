package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import kotlinx.serialization.Serializable

@Serializable
data class CompositionInfo(
    val activeIngredient: String,
    val activeIngredientAmount: Dose,
    val inactiveIngredients: List<String> = emptyList(),
    val appearance: String,
    val identificationCode: String? = null,
)

@Serializable
data class Dose(
    val amount: Double,
    val unit: DoseUnit,
    val per: String? = null,
)
