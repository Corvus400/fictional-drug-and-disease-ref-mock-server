package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface.KanaNormalizer

class Lexicon(
    private val entries: List<LexiconEntry>,
    private val kanaIndex: Map<String, List<String>>,
) {
    private val byToken: Map<String, LexiconEntry> = entries.associateBy { it.token }
    private val byMeaning: Map<String, List<LexiconEntry>> = entries.groupBy { it.meaning }

    val size: Int get() = entries.size

    fun all(): List<LexiconEntry> {
        return entries
    }

    fun lookupByFx(fixmerge: String): LexiconEntry? {
        return byToken[fixmerge]
    }

    fun lookupByJa(japanese: String): LexiconEntry? {
        byMeaning[japanese]?.firstOrNull()?.let { return it }
        return entries.firstOrNull { it.meaning.startsWith(japanese) }
    }

    fun lookupByKana(kana: String): LexiconEntry? {
        val normalized = KanaNormalizer.hiraganaToKatakana(input = kana)
        val tokens = kanaIndex[normalized] ?: return null
        for (token in tokens) {
            byToken[token]?.let { return it }
        }
        return null
    }

    fun filterByPos(pos: PartOfSpeech): List<LexiconEntry> {
        return entries.filter { it.pos == pos }
    }

    fun filterByPattern(pattern: Pattern): List<LexiconEntry> {
        return entries.filter { it.pattern == pattern }
    }

    companion object {
        fun load(): Lexicon {
            return Lexicon(
                entries = ResourceLoader.loadLexicon(),
                kanaIndex = ResourceLoader.loadKanaIndex(),
            )
        }
    }
}
