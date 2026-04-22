package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

/**
 * OpenAPI仕様生成およびSwagger UI / ReDocを設定
 *
 * - /openapi.json: OpenAPI仕様（JSON形式）
 * - /swagger: Swagger UI
 * - /redoc: ReDoc
 *
 * スキーマはkotlinx.serializationから自動生成。
 * Fixtureオブジェクトがexample値として自動シリアライズされる。
 * ContentNegotiation と同じ [AppJson] (snake_case naming strategy) を使用することで、
 * 実レスポンス body と OpenAPI example のキー表記を一致させる。
 */
fun Application.configureOpenAPI() {
    val json = AppJson

    install(OpenApi) {
        info {
            title = "Mock Server API"
            version = "1.0.0"
            description = buildApiDescription()
        }

        schemas {
            generator = SchemaGenerator.kotlinx(json)
        }

        examples {
            exampleEncoder = ExampleEncoder.kotlinx(json)
        }

        tags {
            ApiTag.entries.forEach { apiTag ->
                tag(apiTag.tagName) { description = apiTag.description }
            }
        }
    }

    routing {
        route("/openapi.json") {
            openApi()
        }

        route("/swagger") {
            swaggerUI("/openapi.json")
        }

        route("/redoc") {
            redoc("/openapi.json")
        }
    }
}

/**
 * OpenAPI info.description を動的生成する
 *
 * ApiTag.entries からAPIカテゴリ一覧を自動生成するため、
 * タグの追加・変更時に description のメンテナンスが不要。
 */
private fun buildApiDescription(): String {
    val categoryList = ApiTag.entries.joinToString(separator = "\n") { apiTag ->
        "- **${apiTag.tagName}**: ${apiTag.description}"
    }
    return """
        |架空医薬品・疾病 Mock Server は、Flutter/iOS/Androidアプリ開発用のシナリオベースモックサーバーです。
        |
        |## APIカテゴリ
        |$categoryList
        |
        |## シナリオ切り替え方法
        |
        |### 1. X-Mock-Scenario ヘッダー（リクエスト単位）
        |```bash
        |curl -H "X-Mock-Scenario: empty" http://localhost:8080/api/sample
        |```
        |
        |### 2. Admin API（サーバー全体のオーバーライド）
        |```bash
        |# シナリオ設定
        |curl -X POST http://localhost:8080/__admin/configs/sample \
        |  -H "Content-Type: application/json" \
        |  -d '{"state": "empty"}'
        |
        |# 全状態リセット
        |curl -X POST http://localhost:8080/__admin/reset
        |```
        |
        |### シナリオ解決の優先順位
        |1. X-Mock-Scenario ヘッダー（最優先）
        |2. Admin API override
        |3. デフォルトシナリオ
    """.trimMargin()
}
