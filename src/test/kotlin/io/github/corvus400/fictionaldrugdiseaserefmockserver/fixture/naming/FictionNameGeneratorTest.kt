package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(
            expected = CollisionSnapshot(total = all.size, uniqueKana = all.size, uniqueKanji = all.size),
            actual = CollisionSnapshot(total = all.size, uniqueKana = kanaSet.size, uniqueKanji = kanjiSet.size),
        )
    }

    @Test
    fun `generated names avoid all PMDA and ICD-10 blacklist entries`() {
        val violations = mutableListOf<String>()
        repeat(times = 120) { index ->
            val id = "drug_${"%04d".format(index + 1)}"
            for (slot in NameSlot.entries) {
                val name = FictionNameGenerator.generate(id = id, slot = slot)
                if (ForbiddenNames.contains(name = name.kana)) {
                    violations += "kana '${name.kana}' is in blacklist (id=$id slot=$slot)"
                }
                if (ForbiddenNames.contains(name = name.kanji)) {
                    violations += "kanji '${name.kanji}' is in blacklist (id=$id slot=$slot)"
                }
                if (ForbiddenNames.containsClassSuffix(name = name.kana)) {
                    violations += "kana '${name.kana}' ends with drug class suffix (id=$id slot=$slot)"
                }
                if (ForbiddenNames.containsClassSuffix(name = name.kanji)) {
                    violations += "kanji '${name.kanji}' ends with drug class suffix (id=$id slot=$slot)"
                }
            }
        }

        assertEquals(expected = emptyList(), actual = violations)
    }

    private data class CollisionSnapshot(
        val total: Int,
        val uniqueKana: Int,
        val uniqueKanji: Int,
    )
}
