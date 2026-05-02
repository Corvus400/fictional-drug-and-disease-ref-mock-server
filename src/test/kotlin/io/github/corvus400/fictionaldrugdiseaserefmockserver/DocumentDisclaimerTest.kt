package io.github.corvus400.fictionaldrugdiseaserefmockserver

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.Disclaimer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class DocumentDisclaimerTest {
    @Test
    fun `README and DISCLAIMER document fictional data warning`() {
        val readme = Files.readString(Path.of("README.md"))
        val disclaimerPath = Path.of("DISCLAIMER.md")

        assertTrue(readme.contains("## DISCLAIMER"))
        assertTrue(readme.contains("医療判断に使用してはなりません"))
        assertTrue(readme.contains("[DISCLAIMER.md](DISCLAIMER.md)"))
        assertTrue(Files.exists(disclaimerPath), "DISCLAIMER.md must exist")
        assertTrue(Files.readString(disclaimerPath).contains(Disclaimer.FULL_JA_EN))
    }
}
