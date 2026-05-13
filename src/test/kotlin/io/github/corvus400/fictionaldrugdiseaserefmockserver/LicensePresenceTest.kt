package io.github.corvus400.fictionaldrugdiseaserefmockserver

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class LicensePresenceTest {
    @Test
    fun `repository has MIT LICENSE`() {
        val licensePath = Path.of("LICENSE")

        assertTrue(
            Files.exists(licensePath),
            "LICENSE must exist"
        )
        assertTrue(
            Files.readString(licensePath).contains("MIT License"),
            "contract assertion failed"
        )
    }
}
