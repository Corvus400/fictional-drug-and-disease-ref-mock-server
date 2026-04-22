package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

internal object DiseasePlaceholderContractMessages {
    const val TASK_ORDER_RULE: String =
        """
        TASK ORDER VIOLATION:
        Placeholder keys MUST NOT be added to DiseaseParagraphTemplates before
        their replacement logic exists in DiseasePlaceholderDictionary.

        Correct sequence:
          (1) Add resolver case to DiseasePlaceholderDictionary
          (2) Run tests and confirm resolve() succeeds
          (3) THEN add the '{{...}}' placeholder to a template string

        DO NOT bypass this error with try/catch, runCatching, or null-fallback.
        That reintroduces PR #205's raw-placeholder leak (Issue #206) on the disease side (Issue #215).
        """

    fun unknownPlaceholderError(key: String): String =
        "Unknown placeholder '{{$key}}' found in DiseaseParagraphTemplates but not in DiseasePlaceholderDictionary." +
            TASK_ORDER_RULE.trimIndent()

    fun residualDelimiterDetected(
        pattern: String,
        firstOccurrences: List<String>,
    ): String =
        "Raw placeholder(s) matching $pattern detected in disease JSON. " +
            "This test enforces: no placeholder may leak into API response." +
            TASK_ORDER_RULE.trimIndent() +
            "\n\nFirst occurrences: $firstOccurrences"
}
