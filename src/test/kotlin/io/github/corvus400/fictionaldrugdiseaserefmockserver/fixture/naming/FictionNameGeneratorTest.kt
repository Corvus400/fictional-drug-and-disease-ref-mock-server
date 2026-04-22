package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FictionNameGeneratorTest {
    @BeforeTest
    fun resetGeneratorState() {
        FictionNameGenerator.reset()
    }

    @Test
    fun `generate returns identical FictionName for identical id and slot`() {
        val first = FictionNameGenerator.generate(id = "drug_0001", slot = NameSlot.GENERIC_NAME)
        val second = FictionNameGenerator.generate(id = "drug_0001", slot = NameSlot.GENERIC_NAME)
        assertEquals(first, second)
    }

    @Test
    fun `generate produces zero collisions across 120 ids and all NameSlot values`() {
        val all = buildList {
            repeat(times = 120) { index ->
                val id = "drug_${"%04d".format(index + 1)}"
                for (slot in NameSlot.entries) {
                    add(FictionNameGenerator.generate(id = id, slot = slot))
                }
            }
        }
        val kanaSet = all.map { it.kana }.toSet()
        val kanjiSet = all.map { it.kanji }.toSet()
        assertEquals(expected = all.size, actual = kanaSet.size, message = "kana collision detected")
        assertEquals(expected = all.size, actual = kanjiSet.size, message = "kanji collision detected")
    }

    @Test
    fun `generated names avoid all PMDA and ICD-10 blacklist entries`() {
        repeat(times = 120) { index ->
            val id = "drug_${"%04d".format(index + 1)}"
            for (slot in NameSlot.entries) {
                val name = FictionNameGenerator.generate(id = id, slot = slot)
                assertFalse(
                    actual = ForbiddenNames.contains(name = name.kana),
                    message = "kana '${name.kana}' is in blacklist (id=$id slot=$slot)",
                )
                assertFalse(
                    actual = ForbiddenNames.contains(name = name.kanji),
                    message = "kanji '${name.kanji}' is in blacklist (id=$id slot=$slot)",
                )
            }
        }
    }
}
