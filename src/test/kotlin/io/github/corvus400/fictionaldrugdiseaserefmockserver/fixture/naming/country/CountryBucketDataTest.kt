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
    fun `all 14 countries have a bucket entry`() {
        assertEquals(14, Country.entries.size)
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
        for (country in Country.entries) {
            val bucket = requireNotNull(CountryBucketData.BY_COUNTRY[country]) {
                "missing bucket entry for country=$country"
            }
            assertTrue(
                bucket.beverage.size >= 10,
                "beverage size < 10 for country=$country (was ${bucket.beverage.size})",
            )
            assertTrue(
                bucket.cities.size >= 10,
                "cities size < 10 for country=$country (was ${bucket.cities.size})",
            )
        }
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
