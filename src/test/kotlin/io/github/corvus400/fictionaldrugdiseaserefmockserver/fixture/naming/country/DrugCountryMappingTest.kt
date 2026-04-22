package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

import kotlin.test.Test
import kotlin.test.assertEquals

class DrugCountryMappingTest {
    @Test
    fun `ATC first letter A maps to Italy`() {
        assertEquals(Country.ITALY, DrugCountryMapping.of(atcFirstLetter = 'A'))
    }

    @Test
    fun `ATC first letter C maps to China`() {
        assertEquals(Country.CHINA, DrugCountryMapping.of(atcFirstLetter = 'C'))
    }

    @Test
    fun `ATC first letter J maps to France`() {
        assertEquals(Country.FRANCE, DrugCountryMapping.of(atcFirstLetter = 'J'))
    }

    @Test
    fun `ATC first letter N maps to India`() {
        assertEquals(Country.INDIA, DrugCountryMapping.of(atcFirstLetter = 'N'))
    }

    @Test
    fun `ATC first letter R maps to Mexico`() {
        assertEquals(Country.MEXICO, DrugCountryMapping.of(atcFirstLetter = 'R'))
    }

    @Test
    fun `ATC first letter D maps to Thailand`() {
        assertEquals(Country.THAILAND, DrugCountryMapping.of(atcFirstLetter = 'D'))
    }

    @Test
    fun `ATC first letter B maps to Spain`() {
        assertEquals(Country.SPAIN, DrugCountryMapping.of(atcFirstLetter = 'B'))
    }

    @Test
    fun `ATC first letter G maps to Korea`() {
        assertEquals(Country.KOREA, DrugCountryMapping.of(atcFirstLetter = 'G'))
    }

    @Test
    fun `ATC first letter H maps to Turkey`() {
        assertEquals(Country.TURKEY, DrugCountryMapping.of(atcFirstLetter = 'H'))
    }

    @Test
    fun `ATC first letter L maps to Vietnam`() {
        assertEquals(Country.VIETNAM, DrugCountryMapping.of(atcFirstLetter = 'L'))
    }

    @Test
    fun `ATC first letter M maps to Japan`() {
        assertEquals(Country.JAPAN, DrugCountryMapping.of(atcFirstLetter = 'M'))
    }

    @Test
    fun `ATC first letter P maps to Germany`() {
        assertEquals(Country.GERMANY, DrugCountryMapping.of(atcFirstLetter = 'P'))
    }

    @Test
    fun `ATC first letter S maps to USA`() {
        assertEquals(Country.USA, DrugCountryMapping.of(atcFirstLetter = 'S'))
    }

    @Test
    fun `ATC first letter V maps to Brazil`() {
        assertEquals(Country.BRAZIL, DrugCountryMapping.of(atcFirstLetter = 'V'))
    }
}
