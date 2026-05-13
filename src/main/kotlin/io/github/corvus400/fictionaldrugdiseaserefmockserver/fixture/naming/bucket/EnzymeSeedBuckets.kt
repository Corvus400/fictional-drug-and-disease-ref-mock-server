package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

object EnzymeSeedBuckets {
    val global: List<String> =
        listOf(
            "CYP3A4",
            "CYP2D6",
            "CYP2C9",
            "CYP2C19",
            "CYP1A2",
            "CYP2B6",
            "CYP2E1",
            "UGT1A1",
            "UGT2B7",
            "NAT2",
        )
}

object EnzymeSeedBucketRepository {
    fun get(): List<String> = EnzymeSeedBuckets.global
}
