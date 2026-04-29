package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * オーバーライド適用後の解決済みシナリオ（ジェネリクス版）
 */
data class ResolvedScenarioWithFixture<T>(
    val fixture: T,
    val delayMs: Long = 0,
    val status: HttpStatusCode = HttpStatusCode.OK,
    val headers: Map<String, String> = emptyMap(),
)

/**
 * X-Mock-Scenarioヘッダーで指定されたシナリオを格納するためのAttributeKey
 */
val ScenarioHeaderKey = AttributeKey<String>("ScenarioHeader")

/**
 * X-Mock-Scenarioヘッダーを解析するプラグイン
 */
val ScenarioInterceptorPlugin = createApplicationPlugin(name = "ScenarioInterceptor") {
    onCall { call ->
        val scenarioHeader = call.request.header("X-Mock-Scenario")
        if (scenarioHeader != null) {
            call.attributes.put(ScenarioHeaderKey, scenarioHeader)
        }
    }
}

/**
 * X-Mock-Scenarioヘッダーを解析するインターセプターを設定
 *
 * リクエストに X-Mock-Scenario ヘッダーが含まれている場合、
 * その値を call.attributes に格納し、各エンドポイントで優先的に使用する。
 *
 * 使用例:
 * ```
 * curl -H "X-Mock-Scenario: empty" http://localhost:8080/drugs
 * ```
 */
fun Application.configureScenarioInterceptor() {
    install(ScenarioInterceptorPlugin)
}

/**
 * シナリオを解決し、オーバーライド設定を適用した結果を返す拡張関数
 *
 * 優先順位:
 * 1. X-Mock-Scenarioヘッダーの値
 * 2. ScenarioManagerのoverride設定（Admin APIで設定された値）
 * 3. デフォルト値（引数で指定）
 *
 * delayMs, statusCode, headersはScenarioOverrideから取得
 */
suspend fun <T> ApplicationCall.resolveScenarioWithOverride(
    scenarioManager: ScenarioManager,
    endpointName: String,
    default: String,
    fixtureProvider: (String) -> T,
): ResolvedScenarioWithFixture<T> {
    val headerScenario = attributes.getOrNull(ScenarioHeaderKey)
    val override = scenarioManager.getOverride(endpointName)

    val scenarioName = headerScenario ?: override?.state ?: default
    val fixture = fixtureProvider(scenarioName)

    return ResolvedScenarioWithFixture(
        fixture = fixture,
        delayMs = override?.delayMs ?: 0,
        status = override?.statusCode?.let { HttpStatusCode.fromValue(it) } ?: HttpStatusCode.OK,
        headers = override?.headers.orEmpty(),
    )
}

/**
 * ResolvedScenarioWithFixtureの設定を適用してレスポンスを送信
 *
 * - delayMsが設定されていれば遅延を適用
 * - headersが設定されていればレスポンスヘッダーに追加
 * - statusとfixtureでレスポンスを送信
 */
suspend inline fun <reified T : Any> ApplicationCall.respondWithScenario(
    resolved: ResolvedScenarioWithFixture<T>,
) {
    if (resolved.delayMs > 0) {
        delay(resolved.delayMs)
    }
    resolved.headers.forEach { (key, value) ->
        response.headers.append(key, value)
    }
    respond(resolved.status, resolved.fixture)
}

/**
 * ResolvedScenarioWithFixtureの設定を適用し、フィールドオーバーライドをマージしてレスポンスを送信
 *
 * fieldOverridesが空の場合は通常のレスポンス、
 * 空でない場合はFixtureをJSON化→オーバーライドフィールドをマージ→レスポンスを送信。
 */
suspend inline fun <reified T : Any> ApplicationCall.respondWithScenarioAndOverrides(
    resolved: ResolvedScenarioWithFixture<T>,
    fieldOverrides: Map<String, JsonElement>,
) {
    if (resolved.delayMs > 0) {
        delay(resolved.delayMs)
    }
    resolved.headers.forEach { (key, value) ->
        response.headers.append(key, value)
    }
    if (fieldOverrides.isEmpty()) {
        respond(resolved.status, resolved.fixture)
    } else {
        val fixtureJson = Json.encodeToJsonElement(resolved.fixture).jsonObject
        val merged = JsonObject(fixtureJson.toMutableMap().apply { putAll(fieldOverrides) })
        respond(resolved.status, merged)
    }
}
