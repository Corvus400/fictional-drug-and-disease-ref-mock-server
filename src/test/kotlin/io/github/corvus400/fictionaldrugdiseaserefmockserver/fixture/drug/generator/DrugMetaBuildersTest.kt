package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class DrugMetaBuildersTest {
    @Test
    fun `buildPharmacokinetics fields contain no raw placeholder delimiters`() {
        val pk = DrugMetaBuilders.buildPharmacokinetics(id = SAMPLE_ID)
        val textFields =
            listOf(
                "bloodConcentration" to pk.bloodConcentration,
                "absorption" to pk.absorption,
                "distribution" to pk.distribution,
                "metabolism" to pk.metabolism,
                "excretion" to pk.excretion,
            )
        textFields.forEach { (fieldName, nullableValue) ->
            val value = assertNotNull(nullableValue, "buildPharmacokinetics.$fieldName must be non-null")
            assertFalse(
                actual = "{{" in value || "}}" in value,
                message =
                    "buildPharmacokinetics.$fieldName must contain no raw '{{...}}' after " +
                        "Dictionary wiring; got='$value'",
            )
        }
    }

    private companion object {
        const val SAMPLE_ID: String = "drug_0001"
    }
}
