package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object DiseaseCountryMapping {
    private val COUNTRIES: List<Country> = Country.entries

    fun of(chapter: Icd10Chapter): Country {
        return COUNTRIES[chapter.ordinal % COUNTRIES.size]
    }
}
