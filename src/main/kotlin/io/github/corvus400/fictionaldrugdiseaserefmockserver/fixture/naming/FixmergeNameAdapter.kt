package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.FixmergeEngine
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.FixmergeEngineFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import java.util.concurrent.ConcurrentHashMap

/**
 * `FixmergeEngine` を stateful にラップし、`usedKatakanaSet` を保持する。
 *
 * 同一インスタンスを複数の Generator に注入することで、drug 側と disease 側で
 * katakana 重複を回避できる (PR 7 engine 欠陥調査で確定した解決策 C)。
 * engine 側の `coinName(..., excludeKatakanaSet)` は除外集合に該当しない CoinedName を
 * 返すため、Adapter は得た katakana を `usedKatakanaSet` に追加して次回の呼び出しに備える。
 */
class FixmergeNameAdapter(private val engine: FixmergeEngine = DEFAULT_ENGINE) {
    private val usedKatakanaSet: MutableSet<String> = ConcurrentHashMap.newKeySet()

    fun coin(slot: NameSlot, seed: Long): CoinedName {
        val coined = engine.coinName(slot = slot, seed = seed, excludeKatakanaSet = usedKatakanaSet)
        usedKatakanaSet.add(coined.katakana)
        return coined
    }

    companion object {
        private val DEFAULT_ENGINE: FixmergeEngine by lazy {
            FixmergeEngineFactory.default(forbiddenBlacklist = ForbiddenNames.all)
        }
    }
}
