package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.ForbiddenNames

sealed interface BucketViolation {
    val bucketKey: String

    data class ForbiddenStartKana(
        override val bucketKey: String,
        val entry: String,
        val start: Char,
    ) : BucketViolation

    data class ForbiddenName(
        override val bucketKey: String,
        val entry: String,
    ) : BucketViolation

    data class DuplicateEntry(
        override val bucketKey: String,
        val entry: String,
    ) : BucketViolation

    data class TooSmall(
        override val bucketKey: String,
        val size: Int,
    ) : BucketViolation

    data class BlankEntry(
        override val bucketKey: String,
    ) : BucketViolation
}

object BucketEntryValidator {
    val FORBIDDEN_START_KANA: Set<Char> =
        setOf('ン', 'ー', 'ッ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ヮ', 'ヵ', 'ヶ')

    fun validate(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation> {
        return blankEntryViolations(bucketKey = bucketKey, entries = entries) +
            forbiddenStartViolations(bucketKey = bucketKey, entries = entries) +
            forbiddenNameViolations(bucketKey = bucketKey, entries = entries) +
            duplicateViolations(bucketKey = bucketKey, entries = entries) +
            tooSmallViolations(bucketKey = bucketKey, entries = entries)
    }

    fun validateAll(buckets: Map<String, List<String>>): List<BucketViolation> =
        buckets.flatMap { (bucketKey, entries) -> validate(bucketKey = bucketKey, entries = entries) }

    fun requireValid(buckets: Map<String, List<String>>) {
        val violations = validateAll(buckets = buckets)
        check(violations.isEmpty()) {
            "Bucket seed entries failed validation: " + violations.joinToString()
        }
    }

    private fun blankEntryViolations(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation.BlankEntry> =
        entries
            .filter { entry -> entry.isBlank() }
            .map { BucketViolation.BlankEntry(bucketKey = bucketKey) }
            .distinct()

    private fun forbiddenStartViolations(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation.ForbiddenStartKana> =
        entries.mapNotNull { entry ->
            val trimmed = entry.trim()
            val start = trimmed.firstOrNull()
            if (start != null && start in FORBIDDEN_START_KANA) {
                BucketViolation.ForbiddenStartKana(bucketKey = bucketKey, entry = entry, start = start)
            } else {
                null
            }
        }

    private fun forbiddenNameViolations(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation.ForbiddenName> =
        entries
            .filter { entry -> ForbiddenNames.contains(name = entry) }
            .map { entry -> BucketViolation.ForbiddenName(bucketKey = bucketKey, entry = entry) }

    private fun duplicateViolations(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation.DuplicateEntry> =
        entries
            .groupingBy { entry -> entry }
            .eachCount()
            .filterValues { count -> count > 1 }
            .keys
            .map { entry -> BucketViolation.DuplicateEntry(bucketKey = bucketKey, entry = entry) }

    private fun tooSmallViolations(
        bucketKey: String,
        entries: List<String>,
    ): List<BucketViolation.TooSmall> =
        if (entries.size < MIN_BUCKET_SIZE) {
            listOf(BucketViolation.TooSmall(bucketKey = bucketKey, size = entries.size))
        } else {
            emptyList()
        }

    private const val MIN_BUCKET_SIZE: Int = 5
}
