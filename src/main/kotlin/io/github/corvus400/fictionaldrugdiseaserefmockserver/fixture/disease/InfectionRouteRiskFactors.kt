package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

internal object InfectionRouteRiskFactors {
    const val DEFAULT_CHAPTER_I_RISK_FACTOR: String = "飛沫伝播"

    private val keywords: List<String> =
        listOf("感染", "接触", "飛沫", "空気", "経口", "媒介", "伝播", "曝露")

    fun containsKeyword(riskFactor: String): Boolean {
        return keywords.any { keyword -> keyword in riskFactor }
    }
}
