package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

object CountryBucketRepository {
    fun of(country: Country): CountryBucket {
        return requireNotNull(CountryBucketData.BY_COUNTRY[country]) {
            "No bucket registered for country=$country"
        }
    }
}
