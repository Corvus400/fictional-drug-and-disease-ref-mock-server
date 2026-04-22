package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden

class ForbiddenWordChecker(private val forbidden: Set<String>) {
    fun isForbidden(name: String): Boolean {
        TODO("not implemented")
    }

    fun <T> retryUntilClean(
        initialSeed: Long,
        maxRetries: Int = 32,
        build: (Long) -> T,
        extractName: (T) -> String,
        isExcluded: (T) -> Boolean = { false },
    ): T {
        TODO("not implemented")
    }
}
