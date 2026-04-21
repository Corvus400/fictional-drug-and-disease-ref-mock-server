package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.sample

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.sample.SampleResponse

@Suppress("unused", "RedundantSuppression")
object SampleFixtures : FixtureProvider<SampleResponse> {
    override val scenarios: Map<String, SampleResponse> = mapOf(
        "default" to SampleResponse(id = "sample-1", message = "mock-server-base default"),
        "empty" to SampleResponse(id = "", message = ""),
    )
    override val scenarioTitles: Map<String, String> = mapOf(
        "default" to "デフォルト",
        "empty" to "空レスポンス",
    )

    override fun describeFixture(fixture: SampleResponse): String =
        "id=${fixture.id}, message=${fixture.message}"
}
