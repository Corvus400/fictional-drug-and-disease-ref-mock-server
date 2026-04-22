package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

internal object PlaceholderDelimiter {
    const val OPEN: String = "{{"
    const val CLOSE: String = "}}"
    val REGEX: Regex = Regex("""\{\{([a-zA-Z0-9]+)\}\}""")
}
