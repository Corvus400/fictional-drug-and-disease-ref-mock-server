package io.github.corvus400.fictionaldrugdiseaserefmockserver

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class LicensePresenceTest {
    @Test
    fun `repository has LICENSE file`() {
        val licensePath = Path.of("LICENSE")

        assertTrue(
            Files.exists(licensePath),
            "LICENSE must exist"
        )
    }

    @Test
    fun `repository LICENSE declares MIT License`() {
        val licensePath = Path.of("LICENSE")

        assertTrue(
            Files.readString(licensePath).contains("MIT License"),
            "LICENSE must contain the literal MIT License heading",
        )
    }
}
