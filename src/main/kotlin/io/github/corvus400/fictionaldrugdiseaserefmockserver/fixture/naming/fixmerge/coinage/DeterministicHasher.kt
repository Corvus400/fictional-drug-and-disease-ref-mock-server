package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage

import java.nio.ByteBuffer
import java.security.MessageDigest

object DeterministicHasher {
    private const val ALGORITHM = "SHA-256"

    fun hash(seed: Long, salt: String): ByteArray {
        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(ByteBuffer.allocate(Long.SIZE_BYTES).putLong(seed).array())
        digest.update(salt.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }

    fun pickIndex(seed: Long, salt: String, size: Int): Int {
        require(size > 0) { "size must be > 0 but was $size" }
        val bytes = hash(seed = seed, salt = salt)
        val buffer = ByteBuffer.wrap(bytes)
        val raw = buffer.long and Long.MAX_VALUE
        return (raw % size.toLong()).toInt()
    }
}
