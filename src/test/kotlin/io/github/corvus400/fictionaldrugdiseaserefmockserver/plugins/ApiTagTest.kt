package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiTagTest {
    @Test
    fun `medical data ApiTag descriptions are marked as fictional`() {
        assertEquals(
            expected = emptyList(),
            actual = ApiTag.entries.mapNotNull { apiTag ->
                val marked = apiTag.description.contains("(架空データ)")
                when {
                    apiTag in listOf(ApiTag.DRUG, ApiTag.DISEASE, ApiTag.CATEGORIES) && marked.not() ->
                        "${apiTag.name} description must be marked as fictional medical data"
                    apiTag in listOf(ApiTag.ADMIN, ApiTag.SYSTEM) && marked ->
                        "${apiTag.name} description must not be marked as fictional medical data"
                    else -> null
                }
            },
        )
    }
}
