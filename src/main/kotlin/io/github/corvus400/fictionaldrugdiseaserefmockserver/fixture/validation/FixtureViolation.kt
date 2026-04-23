package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

data class FixtureViolation(
    val entityType: String,
    val entityId: String,
    val field: String,
    val message: String,
)
