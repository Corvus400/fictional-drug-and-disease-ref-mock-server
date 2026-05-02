package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import kotlin.test.Test
import kotlin.test.assertContains

class DrugParagraphTemplatesTest {
    @Test
    fun `every drug paragraph template contains fictional marker`() {
        DrugParagraphTemplates.templates.forEach { (field, bodies) ->
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
