package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Pattern

enum class NameSlot(val defaultPattern: Pattern) {
    DRUG_BRAND(defaultPattern = Pattern.C),
    DRUG_GENERIC(defaultPattern = Pattern.B),
    DRUG_INACTIVE_INGREDIENT(defaultPattern = Pattern.A),
    DRUG_MANUFACTURER(defaultPattern = Pattern.A),
    DISEASE_NAME(defaultPattern = Pattern.B),
    DISEASE_ALIAS(defaultPattern = Pattern.B),
    DISEASE_DIFFERENTIAL(defaultPattern = Pattern.B),
    DISEASE_COMPLICATION(defaultPattern = Pattern.C),
    DRUG_APPEARANCE(defaultPattern = Pattern.A),
    DRUG_ORIGINAL_DESCRIPTION(defaultPattern = Pattern.A),
}
