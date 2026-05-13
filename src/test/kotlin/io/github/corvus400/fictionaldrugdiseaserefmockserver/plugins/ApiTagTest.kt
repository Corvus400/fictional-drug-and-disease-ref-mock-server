package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApiTagTest {
    @Test
    fun `medical data ApiTag descriptions are marked as fictional`() {
        listOf(ApiTag.DRUG, ApiTag.DISEASE, ApiTag.CATEGORIES).forEach { apiTag ->
            assertTrue(
                apiTag.description.contains("(架空データ)"),
                "contract assertion failed"
            )
        }
        listOf(ApiTag.ADMIN, ApiTag.SYSTEM).forEach { apiTag ->
            assertFalse(
                apiTag.description.contains("(架空データ)"),
                "contract assertion failed"
            )
        }
    }
}
