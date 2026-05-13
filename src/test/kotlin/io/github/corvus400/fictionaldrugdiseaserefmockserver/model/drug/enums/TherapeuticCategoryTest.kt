package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TherapeuticCategoryTest {
    @Test
    fun `entries expose Japanese displayName and ATC initial`() {
        assertEquals(
            expected = mapOf(
                TherapeuticCategory.ALIMENTARY_METABOLISM to ("消化器系および代謝" to 'A'),
                TherapeuticCategory.NERVOUS_SYSTEM to ("神経系" to 'N'),
            ),
            actual = mapOf(
                TherapeuticCategory.ALIMENTARY_METABOLISM to
                    (
                        TherapeuticCategory.ALIMENTARY_METABOLISM.displayName to
                            TherapeuticCategory.ALIMENTARY_METABOLISM.atcInitial
                        ),
                TherapeuticCategory.NERVOUS_SYSTEM to
                    (TherapeuticCategory.NERVOUS_SYSTEM.displayName to TherapeuticCategory.NERVOUS_SYSTEM.atcInitial),
            ),
            message = "TherapeuticCategory displayName and ATC initial contract must stay pinned",
        )
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
            expected = mapOf(
                'A' to TherapeuticCategory.ALIMENTARY_METABOLISM,
                'N' to TherapeuticCategory.NERVOUS_SYSTEM,
                'Z' to null,
            ),
            actual = listOf('A', 'N', 'Z').associateWith { TherapeuticCategory.fromAtcInitial(it) },
            message = "TherapeuticCategory.fromAtcInitial must resolve known ATC initials and reject unknown initials",
        )
    }
}
