package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

private const val FNV_OFFSET_BASIS_64: Long = -3750763034362895579L
private const val FNV_PRIME_64: Long = 1099511628211L
private const val GOLDEN_RATIO_64: Long = -7046029254386353131L
private const val BYTE_MASK: Long = 0xFF

fun stableHash(
    id: String,
    slot: Int,
    index: Int,
): Long {
    var hash = FNV_OFFSET_BASIS_64
    for (byte in id.toByteArray(Charsets.UTF_8)) {
        hash = (hash xor (byte.toLong() and BYTE_MASK)) * FNV_PRIME_64
    }
    return hash xor (slot.toLong() * GOLDEN_RATIO_64) xor (index.toLong() * GOLDEN_RATIO_64.inv())
}
