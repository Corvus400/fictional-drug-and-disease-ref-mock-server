package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.Coiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden.ForbiddenWordChecker
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Lexicon
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface.LatinToKatakanaConverter

interface FixmergeEngine {
    /**
     * [excludeKatakanaSet] に含まれるカタカナ表記が出た場合、engine 内部で seed+1 の
     * retry を行い、除外集合に該当しない CoinedName を返す。既存呼び出し (2 引数) は
     * `emptySet()` を forward するため従来挙動を維持する。mock-server 独自拡張。
     */
    fun coinName(
        slot: NameSlot,
        seed: Long,
        excludeKatakanaSet: Set<String> = emptySet(),
    ): CoinedName
}

class DefaultFixmergeEngine(
    private val coiner: Coiner,
) : FixmergeEngine {
    override fun coinName(
        slot: NameSlot,
        seed: Long,
        excludeKatakanaSet: Set<String>,
    ): CoinedName {
        return coiner.coin(slot = slot, seed = seed, excludeKatakanaSet = excludeKatakanaSet)
    }
}

object FixmergeEngineFactory {
    fun default(forbiddenBlacklist: Set<String> = emptySet()): FixmergeEngine {
        val lexicon = Lexicon.load()
        val converter = LatinToKatakanaConverter.load()
        val checker = ForbiddenWordChecker(forbidden = forbiddenBlacklist)
        val coiner = Coiner(
            lexicon = lexicon,
            converter = converter,
            checker = checker,
        )
        return DefaultFixmergeEngine(coiner = coiner)
    }
}
