package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden

class ForbiddenWordChecker(private val forbidden: Set<String>) {
    fun isForbidden(name: String): Boolean {
        return forbidden.contains(name.lowercase())
    }

    fun <T> retryUntilClean(
        initialSeed: Long,
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        build: (Long) -> T,
        extractName: (T) -> String,
        isExcluded: (T) -> Boolean = { false },
    ): T {
        require(maxRetries > 0) { "maxRetries must be > 0 but was $maxRetries" }
        var seed = initialSeed
        var lastResult: T? = null
        for (attempt in 0 until maxRetries) {
            val candidate = build(seed)
            lastResult = candidate
            val forbiddenHit = isForbidden(name = extractName(candidate))
            val excludedHit = isExcluded(candidate)
            if (forbiddenHit.not() && excludedHit.not()) {
                return candidate
            }
            seed += 1
        }
        return requireNotNull(lastResult) { "retryUntilClean executed zero iterations (maxRetries=$maxRetries)" }
    }

    companion object {
        const val DEFAULT_MAX_RETRIES: Int = 32
    }
}
