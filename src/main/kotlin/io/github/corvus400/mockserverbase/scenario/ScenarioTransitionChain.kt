package io.github.corvus400.mockserverbase.scenario

import kotlinx.serialization.Serializable

/**
 * シナリオ遷移チェーン
 * POSTリクエストごとに次のシナリオへ自動遷移する仕組み
 *
 * Admin API で設定し、POSTリクエスト時に advanceTransition() で進行する。
 * チェーン末尾に到達した場合は最後のシナリオを返し続ける。
 */
@Serializable
data class ScenarioTransitionChain(
    val scenarios: List<String>,
    val currentIndex: Int = 0,
) {
    val currentScenario: String
        get() = scenarios[currentIndex.coerceAtMost(scenarios.lastIndex)]

    fun advance(): ScenarioTransitionChain =
        copy(currentIndex = (currentIndex + 1).coerceAtMost(scenarios.lastIndex))

    val isAtEnd: Boolean
        get() = currentIndex >= scenarios.lastIndex
}
