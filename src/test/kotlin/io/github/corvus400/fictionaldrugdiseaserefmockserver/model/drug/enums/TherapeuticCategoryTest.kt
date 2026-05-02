package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TherapeuticCategoryTest {
    @Test
    fun `entries expose Japanese displayName and ATC initial`() {
        assertEquals(
            expected = "消化器系および代謝",
            actual = TherapeuticCategory.ALIMENTARY_METABOLISM.displayName,
        )
        assertEquals(expected = 'A', actual = TherapeuticCategory.ALIMENTARY_METABOLISM.atcInitial)

        assertEquals(expected = "神経系", actual = TherapeuticCategory.NERVOUS_SYSTEM.displayName)
        assertEquals(expected = 'N', actual = TherapeuticCategory.NERVOUS_SYSTEM.atcInitial)
    }

    @Test
    fun `fromQueryOrThrow resolves SCREAMING_SNAKE_CASE enum name`() {
        assertEquals(
            expected = TherapeuticCategory.ALIMENTARY_METABOLISM,
            actual = TherapeuticCategory.fromQueryOrThrow(raw = "ALIMENTARY_METABOLISM"),
        )
    }

    @Test
    fun `fromQueryOrThrow rejects unknown value with query parameter name`() {
        val error = assertFailsWith<IllegalArgumentException> {
            TherapeuticCategory.fromQueryOrThrow(raw = "UNKNOWN_CATEGORY")
        }

        assertEquals(expected = "Unknown therapeutic_category: UNKNOWN_CATEGORY", actual = error.message)
    }

    @Test
    fun `fromAtcInitial resolves ATC first letter or null`() {
        assertEquals(
            expected = TherapeuticCategory.ALIMENTARY_METABOLISM,
            actual = TherapeuticCategory.fromAtcInitial(initial = 'A'),
        )
        assertEquals(expected = TherapeuticCategory.NERVOUS_SYSTEM, actual = TherapeuticCategory.fromAtcInitial('N'))
        assertNull(actual = TherapeuticCategory.fromAtcInitial(initial = 'Z'))
    }
}
