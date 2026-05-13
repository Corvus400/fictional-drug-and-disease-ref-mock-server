package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScenarioTransitionChainTest {

    @Test
    fun `currentScenario returns first scenario initially`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        assertEquals("first", chain.currentScenario)
    }

    @Test
    fun `advance moves to next scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        val advanced = chain.advance()
        assertEquals(
            expected = TransitionSnapshot(currentScenario = "second", currentIndex = 1),
            actual = advanced.snapshot(),
        )
    }

    @Test
    fun `advance twice moves to third scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        val advanced = chain.advance().advance()
        assertEquals(
            expected = TransitionSnapshot(currentScenario = "third", currentIndex = 2),
            actual = advanced.snapshot(),
        )
    }

    @Test
    fun `advance past end stays at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance().advance().advance()
        assertEquals(
            expected = TransitionSnapshot(currentScenario = "second", currentIndex = 1),
            actual = advanced.snapshot(),
        )
    }

    @Test
    fun `isAtEnd is false when not at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        assertEquals(listOf(false, false), listOf(chain.isAtEnd, chain.advance().isAtEnd))
    }

    @Test
    fun `isAtEnd is true when at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance()
        assertTrue(advanced.isAtEnd)
    }

    @Test
    fun `single element chain is always at end`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("only"),
        )
        val advanced = chain.advance()
        assertEquals(
            expected = listOf(
                TransitionAtEndSnapshot(currentScenario = "only", isAtEnd = true),
                TransitionAtEndSnapshot(currentScenario = "only", isAtEnd = true),
            ),
            actual = listOf(chain.atEndSnapshot(), advanced.atEndSnapshot()),
        )
    }

    @Test
    fun `advance does not mutate original chain`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance()
        assertEquals(
            expected = listOf(
                TransitionSnapshot(currentScenario = "first", currentIndex = 0),
                TransitionSnapshot(currentScenario = "second", currentIndex = 1),
            ),
            actual = listOf(chain.snapshot(), advanced.snapshot()),
        )
    }

    private fun ScenarioTransitionChain.snapshot(): TransitionSnapshot =
        TransitionSnapshot(currentScenario = currentScenario, currentIndex = currentIndex)

    private fun ScenarioTransitionChain.atEndSnapshot(): TransitionAtEndSnapshot =
        TransitionAtEndSnapshot(currentScenario = currentScenario, isAtEnd = isAtEnd)

    private data class TransitionSnapshot(
        val currentScenario: String,
        val currentIndex: Int,
    )

    private data class TransitionAtEndSnapshot(
        val currentScenario: String,
        val isAtEnd: Boolean,
    )
}
