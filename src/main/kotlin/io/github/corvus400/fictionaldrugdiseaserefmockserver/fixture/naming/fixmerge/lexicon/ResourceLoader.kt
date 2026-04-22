package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class LexiconEntryDto(
    val token: String,
    val frequency: Int,
    val pos: String,
    val meaning: String,
    @SerialName("meaning_kana") val meaningKana: String,
    @SerialName("pattern_guess") val patternGuess: String,
    val katakana: String,
    @SerialName("source_lines") val sourceLines: List<String> = emptyList(),
) {
    fun toEntry(): LexiconEntry {
        return LexiconEntry(
            token = token,
            frequency = frequency,
            pos = PartOfSpeech.fromString(value = pos),
            meaning = meaning,
            meaningKana = meaningKana,
            pattern = Pattern.fromString(value = patternGuess),
            katakana = katakana,
            sourceLines = sourceLines,
        )
    }
}

object ResourceLoader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun readText(resourcePath: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: error("Resource not found on classpath: $resourcePath")
        return stream.use { it.readBytes().toString(Charsets.UTF_8) }
    }

    fun loadLexicon(resourcePath: String = "fixmerge/lexicon.json"): List<LexiconEntry> {
        val raw = readText(resourcePath = resourcePath)
        val dtoList = json.decodeFromString<List<LexiconEntryDto>>(string = raw)
        return dtoList.map { it.toEntry() }
    }

    fun loadKanaIndex(resourcePath: String = "fixmerge/kana_index.json"): Map<String, List<String>> {
        val raw = readText(resourcePath = resourcePath)
        return json.decodeFromString(string = raw)
    }
}
