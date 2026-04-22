package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden.ForbiddenWordChecker
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Lexicon
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.LexiconEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Pattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

class Coiner(
    lexicon: Lexicon,
    private val converter: io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface.LatinToKatakanaConverter,
    private val checker: ForbiddenWordChecker,
) {
    private val poolA: List<String> = collectFragmentPool(entries = lexicon.filterByPattern(pattern = Pattern.A))
    private val poolB: List<String> = collectFragmentPool(entries = lexicon.filterByPattern(pattern = Pattern.B))
    private val poolC: List<String> = collectFragmentPool(entries = lexicon.filterByPattern(pattern = Pattern.C))
    private val poolAll: List<String> = (poolA + poolB + poolC).distinct()

    fun coin(
        slot: NameSlot,
        seed: Long,
        excludeKatakanaSet: Set<String> = emptySet(),
    ): CoinedName {
        return coinWithPattern(
            pattern = slot.defaultPattern,
            seed = seed,
            excludeKatakanaSet = excludeKatakanaSet,
        )
    }

    fun coinWithPattern(
        pattern: Pattern,
        seed: Long,
        excludeKatakanaSet: Set<String> = emptySet(),
    ): CoinedName {
        return checker.retryUntilClean(
            initialSeed = seed,
            build = { currentSeed -> buildSingleAttempt(pattern = pattern, seed = currentSeed) },
            extractName = { it.latin },
            isExcluded = { candidate -> excludeKatakanaSet.contains(candidate.katakana) },
        )
    }

    private fun buildSingleAttempt(pattern: Pattern, seed: Long): CoinedName {
        val joiner = joinerFor(pattern = pattern)
        val (headPool, tailPool) = poolsFor(pattern = pattern)
        require(headPool.isNotEmpty() && tailPool.isNotEmpty()) {
            "Lexicon lacks source fragments for coinage pattern=$pattern"
        }
        val head = headPool[DeterministicHasher.pickIndex(seed = seed, salt = "head", size = headPool.size)]
        val tail = tailPool[DeterministicHasher.pickIndex(seed = seed, salt = "tail", size = tailPool.size)]
        val latin = head + joiner + tail
        val katakana = converter.convertWord(word = latin)
        return CoinedName(
            latin = latin,
            katakana = katakana,
            mixedSurface = latin,
            pattern = pattern,
        )
    }

    private fun poolsFor(pattern: Pattern): Pair<List<String>, List<String>> {
        return when (pattern) {
            Pattern.A -> poolA to poolA
            Pattern.B -> poolB to poolAll
            Pattern.C -> poolC to poolAll
            Pattern.UNKNOWN -> poolAll to poolAll
        }
    }

    private fun joinerFor(pattern: Pattern): String {
        return when (pattern) {
            Pattern.A -> "·"
            Pattern.B -> "'"
            Pattern.C -> "-"
            Pattern.UNKNOWN -> ""
        }
    }

    companion object {
        private fun collectFragmentPool(entries: List<LexiconEntry>): List<String> {
            return entries
                .flatMap { SyllableSplitter.split(token = it.token) }
                .filter { it.isNotBlank() }
                .distinct()
        }
    }
}
