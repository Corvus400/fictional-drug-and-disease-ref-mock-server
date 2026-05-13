package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.ForbiddenNames
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CountryBucketDataTest {
    private val katakanaRegex: Regex = Regex(pattern = "^[゠-ヿー]+$")

    @Test
    fun `Country enum has 14 entries`() {
        assertEquals(
            expected = 14,
            actual = Country.entries.size,
            message = "Country enum must contain the 14 supported country buckets",
        )
    }

    @Test
    fun `all countries have a bucket entry`() {
        for (country in Country.entries) {
            assertNotNull(
                CountryBucketData.BY_COUNTRY[country],
                "missing bucket entry for country=$country",
            )
        }
    }

    @Test
    fun `every country has exactly 20 cuisine items`() {
        for (country in Country.entries) {
            val bucket = requireNotNull(CountryBucketData.BY_COUNTRY[country]) {
                "missing bucket entry for country=$country"
            }
            assertEquals(20, bucket.cuisine.size, "cuisine size mismatch for country=$country")
        }
    }

    @Test
    fun `every country has at least 10 beverage and 10 cities items`() {
        val violations = Country.entries.flatMap { country ->
            val bucket = requireNotNull(CountryBucketData.BY_COUNTRY[country]) {
                "missing bucket entry for country=$country"
            }
            listOfNotNull(
                "beverage size < 10 for country=$country (was ${bucket.beverage.size})".takeIf {
                    bucket.beverage.size < 10
                },
                "cities size < 10 for country=$country (was ${bucket.cities.size})".takeIf {
                    bucket.cities.size < 10
                },
            )
        }

        assertEquals(expected = emptyList(), actual = violations)
    }

    @Test
    fun `every bucket entry is katakana only`() {
        for ((country, bucket) in CountryBucketData.BY_COUNTRY) {
            val entries = bucket.cuisine + bucket.beverage + bucket.cities
            for (entry in entries) {
                assertTrue(
                    katakanaRegex.matches(input = entry),
                    "non-katakana entry in country=$country: '$entry'",
                )
            }
        }
    }

    @Test
    fun `no bucket entry collides with ForbiddenNames`() {
        for ((country, bucket) in CountryBucketData.BY_COUNTRY) {
            val entries = bucket.cuisine + bucket.beverage + bucket.cities
            for (entry in entries) {
                assertFalse(
                    ForbiddenNames.contains(name = entry),
                    "forbidden-name collision in country=$country: '$entry'",
                )
            }
        }
    }

    @Test
    fun `repository returns the same bucket as the data map`() {
        for (country in Country.entries) {
            val expected = requireNotNull(CountryBucketData.BY_COUNTRY[country]) {
                "missing bucket entry for country=$country"
            }
            val actual = CountryBucketRepository.of(country = country)
            assertEquals(expected, actual, "repository mismatch for country=$country")
        }
    }
}
