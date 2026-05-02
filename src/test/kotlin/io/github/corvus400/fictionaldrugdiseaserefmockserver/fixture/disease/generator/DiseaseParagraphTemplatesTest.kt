package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import kotlin.test.Test
import kotlin.test.assertContains

class DiseaseParagraphTemplatesTest {
    @Test
    fun `every disease paragraph template contains fictional marker`() {
        DiseaseParagraphTemplates.templates.forEach { (field, bodies) ->
            bodies.forEachIndexed { index, body ->
                assertContains(
                    body,
                    "(架空)",
                    message = "template '$field[$index]' lacks fictional marker",
                )
            }
        }
    }
}
