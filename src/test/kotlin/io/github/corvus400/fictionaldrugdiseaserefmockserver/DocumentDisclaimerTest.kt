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
        val disclaimer = if (Files.exists(disclaimerPath)) {
            Files.readString(disclaimerPath)
        } else {
            ""
        }

        val violations = listOfNotNull(
            "README must contain DISCLAIMER heading".takeUnless { readme.contains("## DISCLAIMER") },
            "README must contain Japanese medical-decision warning"
                .takeUnless { readme.contains("医療判断に使用してはなりません") },
            "README must link to DISCLAIMER.md".takeUnless { readme.contains("[DISCLAIMER.md](DISCLAIMER.md)") },
            "DISCLAIMER.md must exist".takeUnless { Files.exists(disclaimerPath) },
            "DISCLAIMER.md must contain the full configured disclaimer"
                .takeUnless { disclaimer.contains(Disclaimer.FULL_JA_EN) },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "Documentation disclaimer contract violations:\n" + violations.joinToString("\n"),
        )
    }
}
