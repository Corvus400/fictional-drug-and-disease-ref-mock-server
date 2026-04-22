package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

object DrugCountryMapping {
    private val BY_ATC_FIRST_LETTER: Map<Char, Country> =
        mapOf(
            'A' to Country.ITALY,
            'C' to Country.CHINA,
            'J' to Country.FRANCE,
            'N' to Country.INDIA,
            'R' to Country.MEXICO,
            'D' to Country.THAILAND,
            'B' to Country.SPAIN,
            'G' to Country.KOREA,
            'H' to Country.TURKEY,
            'L' to Country.VIETNAM,
            'M' to Country.JAPAN,
            'P' to Country.GERMANY,
            'S' to Country.USA,
            'V' to Country.BRAZIL,
        )

    fun of(atcFirstLetter: Char): Country {
        return requireNotNull(BY_ATC_FIRST_LETTER[atcFirstLetter]) {
            "No country mapping for ATC first letter '$atcFirstLetter'"
        }
    }
}
