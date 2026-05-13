package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BucketEntryValidatorTest {
    @Test
    fun `validate detects forbidden kana start entries`() {
        val violations =
            BucketEntryValidator.validate(
                bucketKey = "test",
                entries = listOf("ンテスト", "安全語", "ー長音", "別語一", "別語二"),
            )

        assertEquals(
            expected = listOf(
                BucketViolation.ForbiddenStartKana(bucketKey = "test", entry = "ンテスト", start = 'ン'),
                BucketViolation.ForbiddenStartKana(bucketKey = "test", entry = "ー長音", start = 'ー'),
            ),
            actual = violations,
        )
    }

    @Test
    fun `validate covers all forbidden kana starts`() {
        val entries = BucketEntryValidator.FORBIDDEN_START_KANA.map { kana -> "$kana${kana}テスト" }

        val violations = BucketEntryValidator.validate(bucketKey = "test", entries = entries)

        assertEquals(
            expected = BucketEntryValidator.FORBIDDEN_START_KANA.toSet(),
            actual = violations
                .filterIsInstance<BucketViolation.ForbiddenStartKana>()
                .map { violation -> violation.start }
                .toSet(),
        )
    }

    @Test
    fun `validate detects ForbiddenNames all match`() {
        val violations = BucketEntryValidator.validate(bucketKey = "test", entries = listOf("ロキソニン"))

        assertTrue(
            actual = violations.any { violation ->
                violation == BucketViolation.ForbiddenName(bucketKey = "test", entry = "ロキソニン")
            },
            message = "validator must detect real drug or disease name seed entries",
        )
    }

    @Test
    fun `validate detects duplicate entries within bucket`() {
        val violations =
            BucketEntryValidator.validate(
                bucketKey = "test",
                entries = listOf("安全語", "重複語", "重複語", "別語", "別語"),
            )

        assertEquals(
            expected = listOf(
                BucketViolation.DuplicateEntry(bucketKey = "test", entry = "重複語"),
                BucketViolation.DuplicateEntry(bucketKey = "test", entry = "別語"),
            ),
            actual = violations,
        )
    }

    @Test
    fun `validate detects bucket size below minimum`() {
        val violations = BucketEntryValidator.validate(bucketKey = "test", entries = listOf("一", "二", "三", "四"))

        assertTrue(
            actual = BucketViolation.TooSmall(bucketKey = "test", size = 4) in violations,
            message = "bucket size below 5 must be rejected",
        )
    }

    @Test
    fun `validate detects blank entries`() {
        val violations = BucketEntryValidator.validate(bucketKey = "test", entries = listOf("安全語", " ", "別語"))

        assertTrue(
            actual = BucketViolation.BlankEntry(bucketKey = "test") in violations,
            message = "blank bucket entries must be rejected",
        )
    }

    @Test
    fun `requireValid throws when violations exist`() {
        val result =
            runCatching {
                BucketEntryValidator.requireValid(
                    buckets = mapOf("test" to listOf("安全語", "ンテスト")),
                )
            }

        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull()?.message.orEmpty().contains("ForbiddenStartKana"))
    }
}
