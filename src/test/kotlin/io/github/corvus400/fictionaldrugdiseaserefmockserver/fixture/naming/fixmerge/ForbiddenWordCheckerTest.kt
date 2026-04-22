package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden.ForbiddenWordChecker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForbiddenWordCheckerTest {
    @Test
    fun `isForbidden returns true for word in injected blacklist`() {
        val checker = ForbiddenWordChecker(forbidden = setOf("asuponate"))
        assertTrue(checker.isForbidden(name = "asuponate"))
    }

    @Test
    fun `isForbidden returns false for word not in blacklist`() {
        val checker = ForbiddenWordChecker(forbidden = setOf("asuponate"))
        assertFalse(checker.isForbidden(name = "hungement"))
    }

    @Test
    fun `retryUntilClean returns first non-forbidden build result`() {
        val checker = ForbiddenWordChecker(forbidden = setOf("banana"))
        val result = checker.retryUntilClean(
            initialSeed = 0L,
            build = { seed -> if (seed == 0L) "banana" else "apple" },
            extractName = { it },
        )
        assertEquals(expected = "apple", actual = result)
    }
}
