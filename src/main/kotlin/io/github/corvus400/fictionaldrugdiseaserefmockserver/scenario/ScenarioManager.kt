package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement

/**
 * シナリオ管理
 * - Admin API からのオーバーライド管理
 * - 遷移チェーン・フィールドオーバーライド管理
 *
 * @param defaultScenario application.yamlで設定されたデフォルトシナリオ名
 */
class ScenarioManager(
    val defaultScenario: String = "default",
) {
    // Phase 9-B run 4: source change to defeat UP-TO-DATE
    private val mutex = Mutex()

    /** Admin API からのオーバーライド設定 */
    private val overrides = mutableMapOf<String, ScenarioOverride>()

    /** 遷移チェーン: POSTリクエストごとに次のシナリオへ自動遷移 */
    private val transitionChains = mutableMapOf<String, ScenarioTransitionChain>()

    /** フィールドオーバーライド: POSTボディから抽出した値でFixtureのフィールドを上書き */
    private val fieldOverrides = mutableMapOf<String, Map<String, JsonElement>>()

    // ===== Admin API 用（オーバーライド管理） =====

    suspend fun setOverride(name: String, override: ScenarioOverride) = mutex.withLock {
        overrides[name] = override
    }

    suspend fun getOverride(name: String): ScenarioOverride? = mutex.withLock {
        overrides[name]
    }

    suspend fun getAllOverrides(): Map<String, ScenarioOverride> = mutex.withLock {
        overrides.toMap()
    }

    suspend fun reset() {
        mutex.withLock {
            overrides.clear()
            transitionChains.clear()
            fieldOverrides.clear()
        }
    }

    // ===== 遷移チェーン管理 =====

    suspend fun setTransitionChain(name: String, scenarios: List<String>) = mutex.withLock {
        val chain = ScenarioTransitionChain(scenarios = scenarios)
        transitionChains[name] = chain
        overrides[name] = ScenarioOverride(state = chain.currentScenario)
    }

    suspend fun advanceTransition(name: String) = mutex.withLock {
        val chain = transitionChains[name] ?: return@withLock
        val advanced = chain.advance()
        transitionChains[name] = advanced
        overrides[name] = ScenarioOverride(state = advanced.currentScenario)
    }

    suspend fun getAllTransitionChains(): Map<String, ScenarioTransitionChain> = mutex.withLock {
        transitionChains.toMap()
    }

    // ===== フィールドオーバーライド管理 =====

    suspend fun getFieldOverrides(name: String): Map<String, JsonElement> = mutex.withLock {
        fieldOverrides[name].orEmpty()
    }

    suspend fun mergeFieldOverrides(name: String, newFields: Map<String, JsonElement>) = mutex.withLock {
        val existing = fieldOverrides[name].orEmpty()
        fieldOverrides[name] = existing + newFields
    }
}
