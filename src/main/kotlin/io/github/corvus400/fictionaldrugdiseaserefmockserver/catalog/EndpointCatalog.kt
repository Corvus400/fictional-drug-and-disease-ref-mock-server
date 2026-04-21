package io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application

data class ScenarioMeta(
    val name: String,
    val title: String,
    val description: String,
)

/**
 * エンドポイントのメタデータ（path/method/endpointName/tag/summaryの単一定義箇所）
 *
 * 各モジュールでprivate valとして1回だけ定義し、catalogEntries生成とルート定義の両方から参照する。
 * これにより知識の重複（DRY違反）を防止する。
 */
data class EndpointMetadata(
    val path: String,
    val method: HttpMethod = HttpMethod.Get,
    val endpointName: String,
    val tag: ApiTag,
    val summary: String,
)

/**
 * EndpointMetadataからEndpointEntryを生成する。
 * scenariosはFixtureProvider.scenarioMetas等から取得する。
 */
fun EndpointMetadata.toEntry(scenarios: List<ScenarioMeta>): EndpointEntry =
    EndpointEntry(
        path = path,
        method = method,
        endpointName = endpointName,
        tag = tag,
        summary = summary,
        scenarios = scenarios,
    )

data class EndpointEntry(
    val path: String,
    val method: HttpMethod,
    val endpointName: String,
    val tag: ApiTag,
    val summary: String,
    val scenarios: List<ScenarioMeta>,
)

/**
 * モジュール登録エントリ
 *
 * 全モジュールはこのデータクラスをRouting.ktのallModulesリストに登録する必要がある。
 * catalogEntriesが必須パラメータのため、カタログ登録なしでのモジュール追加はコンパイルエラーになる。
 *
 * @param catalogEntries このモジュールが提供するEndpointEntryのリスト
 * @param configure モジュールのルート設定関数
 */
data class ModuleRegistration(
    val catalogEntries: List<EndpointEntry>,
    val configure: Application.(ScenarioManager) -> Unit,
)

object EndpointRegistry {
    private val entries = mutableListOf<EndpointEntry>()
    private val registered = mutableSetOf<String>()

    fun register(entry: EndpointEntry) {
        val key = "${entry.method.value}:${entry.path}"
        if (registered.add(key)) {
            entries.add(entry)
        }
    }

    fun getAll(): List<EndpointEntry> = entries.toList()

    fun clear() {
        entries.clear()
        registered.clear()
    }
}
