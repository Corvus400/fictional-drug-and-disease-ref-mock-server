package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FixtureViolationTest {
    @Test
    fun `two FixtureViolations with the same values are equal and share hashCode`() {
        val first = FixtureViolation(
            entityType = "drug",
            entityId = "drug_0001",
            field = "contraindications",
            message = "size >= 1",
        )
        val second = FixtureViolation(
            entityType = "drug",
            entityId = "drug_0001",
            field = "contraindications",
            message = "size >= 1",
        )

        assertEquals(expected = first, actual = second)
        assertEquals(expected = first.hashCode(), actual = second.hashCode())
    }

    @Test
    fun `copy overrides only the given fields and keeps the rest`() {
        val original = FixtureViolation(
            entityType = "drug",
            entityId = "drug_0001",
            field = "contraindications",
            message = "size >= 1",
        )

        val copied = original.copy(entityType = "disease", entityId = "disease_0001")

        assertEquals(expected = "disease", actual = copied.entityType)
        assertEquals(expected = "disease_0001", actual = copied.entityId)
        assertEquals(expected = original.field, actual = copied.field)
        assertEquals(expected = original.message, actual = copied.message)
        assertNotEquals(illegal = original, actual = copied)
    }
}
