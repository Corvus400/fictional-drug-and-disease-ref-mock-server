package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

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
        assertEquals(
            "second",
            advanced.currentScenario,
            "contract assertion failed"
        )
        assertEquals(
            1,
            advanced.currentIndex,
            "contract assertion failed"
        )
    }

    @Test
    fun `advance twice moves to third scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        val advanced = chain.advance().advance()
        assertEquals(
            "third",
            advanced.currentScenario,
            "contract assertion failed"
        )
        assertEquals(
            2,
            advanced.currentIndex,
            "contract assertion failed"
        )
    }

    @Test
    fun `advance past end stays at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance().advance().advance()
        assertEquals(
            "second",
            advanced.currentScenario,
            "contract assertion failed"
        )
        assertEquals(
            1,
            advanced.currentIndex,
            "contract assertion failed"
        )
    }

    @Test
    fun `isAtEnd is false when not at last scenario`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second", "third"),
        )
        assertFalse(
            chain.isAtEnd,
            "contract assertion failed"
        )
        assertFalse(
            chain.advance().isAtEnd,
            "contract assertion failed"
        )
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
        assertEquals(
            "only",
            chain.currentScenario,
            "contract assertion failed"
        )
        assertTrue(
            chain.isAtEnd,
            "contract assertion failed"
        )

        val advanced = chain.advance()
        assertEquals(
            "only",
            advanced.currentScenario,
            "contract assertion failed"
        )
        assertTrue(
            advanced.isAtEnd,
            "contract assertion failed"
        )
    }

    @Test
    fun `advance does not mutate original chain`() {
        val chain = ScenarioTransitionChain(
            scenarios = listOf("first", "second"),
        )
        val advanced = chain.advance()
        assertEquals(
            "first",
            chain.currentScenario,
            "contract assertion failed"
        )
        assertEquals(
            0,
            chain.currentIndex,
            "contract assertion failed"
        )
        assertEquals(
            "second",
            advanced.currentScenario,
            "contract assertion failed"
        )
        assertEquals(
            1,
            advanced.currentIndex,
            "contract assertion failed"
        )
    }
}
