package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class FixtureViolationTest {
    @Test
    fun `two FixtureViolations with the same values are equal`() {
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

        assertEquals(
            expected = first,
            actual = second,
            "contract assertion failed"
        )
    }

    @Test
    fun `two FixtureViolations with the same values share hashCode`() {
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

        assertEquals(
            expected = first.hashCode(),
            actual = second.hashCode(),
            "contract assertion failed"
        )
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

        assertEquals(
            expected = CopiedViolationSnapshot(
                entityType = "disease",
                entityId = "disease_0001",
                field = original.field,
                message = original.message,
                differsFromOriginal = true,
            ),
            actual = CopiedViolationSnapshot(
                entityType = copied.entityType,
                entityId = copied.entityId,
                field = copied.field,
                message = copied.message,
                differsFromOriginal = copied != original,
            ),
            "contract assertion failed"
        )
    }

    private data class CopiedViolationSnapshot(
        val entityType: String,
        val entityId: String,
        val field: String,
        val message: String,
        val differsFromOriginal: Boolean,
    )
}
