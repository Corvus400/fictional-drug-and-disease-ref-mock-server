package io.github.corvus400.mockserverbase.scenario

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertEquals("second", advanced.currentScenario)
        assertEquals(1, advanced.currentIndex)
    }

    @Test
    fun `advance twice moves to third scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        val advanced = chain.advance().advance()
        assertEquals("third", advanced.currentScenario)
        assertEquals(2, advanced.currentIndex)
    }

    @Test
    fun `advance past end stays at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance().advance().advance()
        assertEquals("second", advanced.currentScenario)
        assertEquals(1, advanced.currentIndex)
    }

    @Test
    fun `isAtEnd is false when not at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        assertFalse(chain.isAtEnd)
        assertFalse(chain.advance().isAtEnd)
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
        assertEquals("only", chain.currentScenario)
        assertTrue(chain.isAtEnd)

        val advanced = chain.advance()
        assertEquals("only", advanced.currentScenario)
        assertTrue(advanced.isAtEnd)
    }

    @Test
    fun `advance does not mutate original chain`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance()
        assertEquals("first", chain.currentScenario)
        assertEquals(0, chain.currentIndex)
        assertEquals("second", advanced.currentScenario)
        assertEquals(1, advanced.currentIndex)
    }
}
