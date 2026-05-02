package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import java.util.concurrent.ConcurrentHashMap

private const val MAX_RETRIES: Int = 32

/**
 * Phase 4-1〜4-5 の基盤コンポーネント (StableHash / FictionLanguage / KanaAssembler /
 * KanjiAtejiRules / ForbiddenNames) を統合し、(id, slot) から決定論的にフィクスマージ語を
 * 生成するエントリポイント。
 *
 * ## 決定論性スコープ
 *
 * - **同一セッション内**: 同じ (id, slot) は cache hit によって常に同じ [FictionName] を返す。
 * - **セッション横断**: kana / kanji 採番は他の (id, slot) との衝突回避結果に依存するため、
 *   `generate` を呼ぶ順序が変わると採用結果が変わる場合がある。Phase 5 Generator 層では
 *   全 fixture を ID 昇順で一括生成することで決定論性を担保する。
 *
 * ## 衝突回避
 *
 * KanaAssembler の出力空間 (PATTERN_A は 2-3 syllable) は 120 ids × 複数 NameSlot を
 * 重複なく埋めるには余裕が限られるため、ブラックリスト + 既出セットの双方を回避するまで
 * `index` を +1 して最大 [MAX_RETRIES] 回リトライする。超過時は fail-fast で `error()`。
 *
 * ## テスト時のリセット
 *
 * テストクラスは内部キャッシュを汚染しないよう `@BeforeTest` で [reset] を呼ぶこと。
 */
object FictionNameGenerator {
    private val cache: MutableMap<CacheKey, FictionName> = ConcurrentHashMap()
    private val usedKana: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val usedKanji: MutableSet<String> = ConcurrentHashMap.newKeySet()

    @Synchronized
    fun generate(
        id: String,
        slot: NameSlot,
    ): FictionName {
        val key = CacheKey(id = id, slot = slot)
        val cached = cache[key]
        if (cached != null) {
            return cached
        }
        val pattern = slot.pattern
        for (retry in 0..MAX_RETRIES) {
            val seed = stableHash(id = id, slot = slot.ordinal, index = retry)
            val kana = KanaAssembler.assemble(seed = seed, pattern = pattern)
            val kanji = KanjiAtejiRules.toAteji(kana = kana, seed = seed)
            val isBlacklisted =
                ForbiddenNames.contains(kana) ||
                    ForbiddenNames.contains(kanji) ||
                    ForbiddenNames.containsClassSuffix(kana) ||
                    ForbiddenNames.containsClassSuffix(kanji)
            val isAlreadyUsed = kana in usedKana || kanji in usedKanji
            if (isBlacklisted || isAlreadyUsed) {
                continue
            }
            val result = FictionName(kana = kana, kanji = kanji, pattern = pattern)
            cache[key] = result
            usedKana.add(kana)
            usedKanji.add(kanji)
            return result
        }
        error(
            "FictionNameGenerator failed to find unique non-blacklisted name " +
                "after $MAX_RETRIES retries for id=$id slot=$slot",
        )
    }

    /**
     * 内部キャッシュ・既出セットをクリアする。テストの独立性確保専用。
     */
    @Synchronized
    fun reset() {
        cache.clear()
        usedKana.clear()
        usedKanji.clear()
    }

    private data class CacheKey(
        val id: String,
        val slot: NameSlot,
    )
}
