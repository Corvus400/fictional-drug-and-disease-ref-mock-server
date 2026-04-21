package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.CatalogHtmlRenderer
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioOverride
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.adminRoutes(
    scenarioManager: ScenarioManager,
    enableAdminApi: Boolean = true,
) {
    routing {
        get("/health", {
            summary = "ヘルスチェック"
            tags(ApiTag.SYSTEM.tagName)
            description = "サーバーの稼働状態を確認する。"
            response {
                code(HttpStatusCode.OK) {
                    description = "サーバー稼働中"
                }
            }
        }) {
            call.respond(mapOf("status" to "ok"))
        }

        if (enableAdminApi.not()) return@routing

        route("/__admin", {}) {
            // 全シナリオオーバーライドを取得
            get("/configs", {
                summary = "全シナリオオーバーライド取得"
                tags(ApiTag.ADMIN.tagName)
                description = "現在設定されている全エンドポイントのシナリオオーバーライドを取得する。"
                response {
                    code(HttpStatusCode.OK) {
                        description = "エンドポイント名→シナリオオーバーライドのマップ"
                    }
                }
            }) {
                call.respond(scenarioManager.getAllOverrides())
            }

            // シナリオオーバーライドを設定
            post("/configs/{name}", {
                summary = "シナリオオーバーライド設定"
                tags(ApiTag.ADMIN.tagName)
                description = "指定エンドポイントのシナリオオーバーライドを設定する。"
                request {
                    pathParameter<String>("name") {
                        description = "エンドポイント名"
                    }
                    body<ScenarioOverride> {
                        description = "シナリオオーバーライド設定"
                    }
                }
                response {
                    code(HttpStatusCode.OK) {
                        description = "設定成功"
                    }
                }
            }) {
                val name = call.parameters["name"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val override = call.receive<ScenarioOverride>()
                scenarioManager.setOverride(name, override)
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // 遷移チェーン: 単一エンドポイント設定
            post("/transitions/{name}", {
                summary = "遷移チェーン設定"
                tags(ApiTag.ADMIN.tagName)
                description = "指定エンドポイントの遷移チェーン（リクエストごとにシナリオが順次切り替わる設定）を設定する。"
                request {
                    pathParameter<String>("name") {
                        description = "エンドポイント名"
                    }
                    body<TransitionChainRequest> {
                        description = "遷移チェーン設定（シナリオ名のリスト）"
                    }
                }
                response {
                    code(HttpStatusCode.OK) {
                        description = "設定成功"
                    }
                }
            }) {
                val name = call.parameters["name"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<TransitionChainRequest>()
                if (request.scenarios.isEmpty()) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "scenarios must not be empty"),
                    )
                }
                scenarioManager.setTransitionChain(
                    name = name,
                    scenarios = request.scenarios,
                )
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // 遷移チェーン: 一括設定
            post("/transitions", {
                summary = "遷移チェーン一括設定"
                tags(ApiTag.ADMIN.tagName)
                description = "複数エンドポイントの遷移チェーンを一括で設定する。"
                request {
                    body<BatchTransitionsRequest> {
                        description = "エンドポイント名→シナリオリストのマップ"
                    }
                }
                response {
                    code(HttpStatusCode.OK) {
                        description = "設定成功"
                    }
                }
            }) {
                val request = call.receive<BatchTransitionsRequest>()
                request.transitions.forEach { (name, scenarios) ->
                    scenarioManager.setTransitionChain(
                        name = name,
                        scenarios = scenarios,
                    )
                }
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // 遷移チェーン: 全取得（デバッグ用）
            get("/transitions", {
                summary = "遷移チェーン全取得"
                tags(ApiTag.ADMIN.tagName)
                description = "現在設定されている全遷移チェーンを取得する。"
                response {
                    code(HttpStatusCode.OK) {
                        description = "エンドポイント名→遷移チェーン情報のマップ"
                    }
                }
            }) {
                call.respond(scenarioManager.getAllTransitionChains())
            }

            post("/reset", {
                summary = "全状態リセット"
                tags(ApiTag.ADMIN.tagName)
                description = "全シナリオオーバーライド、遷移チェーン、フィールドオーバーライドをリセットする。"
                response {
                    code(HttpStatusCode.OK) {
                        description = "リセット成功"
                    }
                }
            }) {
                scenarioManager.reset()
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            }

            // ===== エンドポイントカタログ =====

            get("/catalog", {
                summary = "エンドポイントカタログ（HTML）"
                tags(ApiTag.ADMIN.tagName)
                description = "全エンドポイントの画面・シナリオ・Fixture対応表をHTMLで返す。"
                response {
                    code(HttpStatusCode.OK) {
                        description = "HTMLカタログページ"
                    }
                }
            }) {
                val html = CatalogHtmlRenderer.render(entries = EndpointRegistry.getAll())
                call.respondText(text = html, contentType = ContentType.Text.Html)
            }
        }
    }
}

/**
 * Admin APIリクエストDTO。call.receive<T>()でデシリアライズされる。
 * IntelliJのunused constructor偽陽性を抑制。RedundantSuppression自体もIntelliJ偽陽性。
 */
@Suppress("unused", "RedundantSuppression")
@Serializable
data class TransitionChainRequest(val scenarios: List<String>)

@Suppress("unused", "RedundantSuppression")
@Serializable
data class BatchTransitionsRequest(val transitions: Map<String, List<String>>)
