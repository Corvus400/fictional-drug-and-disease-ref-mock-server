package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage

object SyllableSplitter {
    private val BOUNDARY_CHARS = setOf('·', '-', '\'', ' ')

    fun split(token: String): List<String> {
        if (token.isEmpty()) {
            return emptyList()
        }
        val parts = mutableListOf<String>()
        val current = StringBuilder()
        for (index in token.indices) {
            val ch = token[index]
            if (ch in BOUNDARY_CHARS) {
                if (current.isNotEmpty()) {
                    parts.add(current.toString())
                    current.clear()
                }
                continue
            }
            if (isCamelBoundary(token = token, index = index) && current.isNotEmpty()) {
                parts.add(current.toString())
                current.clear()
            }
            current.append(ch)
        }
        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }
        return parts
    }

    private fun isCamelBoundary(token: String, index: Int): Boolean {
        if (index == 0) {
            return false
        }
        val prev = token[index - 1]
        val curr = token[index]
        if (prev.isLowerCase() && curr.isUpperCase()) {
            return true
        }
        if (index + 1 < token.length) {
            val next = token[index + 1]
            if (prev.isUpperCase() && curr.isUpperCase() && next.isLowerCase()) {
                return true
            }
        }
        return false
    }
}
