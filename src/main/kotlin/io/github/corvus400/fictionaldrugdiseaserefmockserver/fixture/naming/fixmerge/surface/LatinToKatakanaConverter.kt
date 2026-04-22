package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface

class LatinToKatakanaConverter(private val ruleset: LatinToKanaRuleset) {
    fun convert(input: String): String {
        val tokens = splitOnWordBoundary(input = input)
        return tokens.joinToString(separator = " ") { convertWord(word = it) }
    }

    fun convertWord(word: String): String {
        if (word.isEmpty()) {
            return ""
        }
        val override = ruleset.overrides[word]
        if (override != null) {
            return override
        }
        val normalized = normalize(input = word)
        if (normalized.isEmpty()) {
            return ""
        }
        return applyRules(input = normalized)
    }

    private fun splitOnWordBoundary(input: String): List<String> {
        val boundaries = ruleset.wordBoundaryCharacters
        if (boundaries.isEmpty()) {
            return listOf(input)
        }
        val result = mutableListOf<String>()
        val current = StringBuilder()
        for (ch in input) {
            if (ch in boundaries) {
                if (current.isNotEmpty()) {
                    result.add(current.toString())
                    current.clear()
                }
            } else {
                current.append(ch)
            }
        }
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }
        return result
    }

    private fun normalize(input: String): String {
        val builder = StringBuilder(input.length)
        for (ch in input) {
            if (ch in ruleset.stripCharacters) {
                continue
            }
            builder.append(ch)
        }
        val stripped = builder.toString()
        return if (ruleset.caseFoldLower) {
            stripped.lowercase()
        } else {
            stripped
        }
    }

    private fun applyRules(input: String): String {
        val result = StringBuilder()
        var index = 0
        while (index < input.length) {
            val matched = tryLongestMatch(input = input, start = index)
            val coda = ruleset.finalConsonantRules[input[index].toString()]
            when {
                matched != null -> {
                    result.append(matched.second)
                    index += matched.first
                }
                coda != null -> {
                    result.append(coda)
                    index += 1
                }
                else -> {
                    index += 1
                }
            }
        }
        return result.toString()
    }

    private fun tryLongestMatch(input: String, start: Int): Pair<Int, String>? {
        var bestLength = 0
        var bestReplacement: String? = null
        for ((key, value) in ruleset.clusterRules) {
            if (key.length > bestLength && input.startsWith(prefix = key, startIndex = start)) {
                bestLength = key.length
                bestReplacement = value
            }
        }
        for ((key, value) in ruleset.digraphRules) {
            if (key.length > bestLength && input.startsWith(prefix = key, startIndex = start)) {
                bestLength = key.length
                bestReplacement = value
            }
        }
        for ((key, value) in ruleset.syllableRules) {
            if (key.length > bestLength && input.startsWith(prefix = key, startIndex = start)) {
                bestLength = key.length
                bestReplacement = value
            }
        }
        for ((key, value) in ruleset.vowelRules) {
            if (key.length > bestLength && input.startsWith(prefix = key, startIndex = start)) {
                bestLength = key.length
                bestReplacement = value
            }
        }
        val replacement = bestReplacement ?: return null
        return bestLength to replacement
    }

    companion object {
        fun load(): LatinToKatakanaConverter {
            return LatinToKatakanaConverter(ruleset = LatinToKanaRuleset.load())
        }
    }
}
