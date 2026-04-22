package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

internal object DiseasePlaceholderDelimiter {
    const val OPEN: String = "{{"
    const val CLOSE: String = "}}"
    val REGEX: Regex = Regex("""\{\{([a-zA-Z0-9]+)\}\}""")
}
