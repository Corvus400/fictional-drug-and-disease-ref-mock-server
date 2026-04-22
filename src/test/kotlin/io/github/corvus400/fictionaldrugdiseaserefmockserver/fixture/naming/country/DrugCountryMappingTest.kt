package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

import kotlin.test.Test
import kotlin.test.assertEquals

class DrugCountryMappingTest {
    private val expectedByAtcFirstLetter: Map<Char, Country> =
        mapOf(
            'A' to Country.ITALY,
            'B' to Country.SPAIN,
            'C' to Country.CHINA,
            'D' to Country.THAILAND,
            'G' to Country.KOREA,
            'H' to Country.TURKEY,
            'J' to Country.FRANCE,
            'L' to Country.VIETNAM,
            'M' to Country.JAPAN,
            'N' to Country.INDIA,
            'P' to Country.GERMANY,
            'R' to Country.MEXICO,
            'S' to Country.USA,
            'V' to Country.BRAZIL,
        )

    @Test
    fun `all 14 ATC first letters map to the expected country`() {
        assertEquals(14, expectedByAtcFirstLetter.size)
        for ((atc, expected) in expectedByAtcFirstLetter) {
            val actual = DrugCountryMapping.of(atcFirstLetter = atc)
            assertEquals(expected, actual, "atcFirstLetter=$atc")
        }
    }
}
