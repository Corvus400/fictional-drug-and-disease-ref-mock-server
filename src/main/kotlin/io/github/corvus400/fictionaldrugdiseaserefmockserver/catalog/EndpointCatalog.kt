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
 * モジュール登録エントリの型階層。
 *
 * 全モジュールは sealed interface 実装のいずれかを Routing.kt の allModules リストに登録する。
 * catalogEntries が必須プロパティのため、カタログ登録なしでのモジュール追加はコンパイルエラーになる。
 *
 * シナリオ切替の有無を **型レベルで分離する** ことで:
 * - シナリオ切替を必要としないモジュールが ScenarioManager を `_` で捨てる無意味な引数を持たずに済む
 * - Routing.kt の dispatch 側で `when` の網羅性チェックが効き、新種モジュール追加時の漏れを防ぐ
 * - モジュール作成者は意図 (シナリオ持つ / 持たない) を選択することを強制される
 *
 * 実装: [ScenarioModuleRegistration] / [StatelessModuleRegistration]
 */
sealed interface ModuleRegistration {
    val catalogEntries: List<EndpointEntry>
}

/**
 * X-Mock-Scenario ヘッダ / Admin API オーバーライドに対応するモジュール用の登録エントリ。
 * drug / disease 等の scenarioRoute ベースのモジュールはこちらを使う。
 */
data class ScenarioModuleRegistration(
    override val catalogEntries: List<EndpointEntry>,
    val configure: Application.(ScenarioManager) -> Unit,
) : ModuleRegistration

/**
 * シナリオ切替を持たない単一レスポンス系モジュール用の登録エントリ。
 * categories のような静的メタデータ API はこちらを使い、ScenarioManager を受け取らない。
 */
data class StatelessModuleRegistration(
    override val catalogEntries: List<EndpointEntry>,
    val configure: Application.() -> Unit,
) : ModuleRegistration

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
