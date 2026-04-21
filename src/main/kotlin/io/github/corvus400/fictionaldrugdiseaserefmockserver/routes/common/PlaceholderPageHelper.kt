package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * プレースホルダーHTMLページ情報
 *
 * Mock Server で WebView 表示確認用のプレースホルダーHTMLを提供するためのデータクラス。
 * 特集ページ、キャンペーンページ等、種別を問わず汎用的に使用できる。
 */
data class PlaceholderPage(
    val path: String,
    val title: String,
    val category: String,
)

/**
 * プレースホルダーHTMLページのルートを一括登録する
 *
 * 各 [PlaceholderPage] の path に対して GET ルートを登録し、
 * title と category を含む最小限のHTMLを返す。
 */
fun Route.placeholderPages(pages: List<PlaceholderPage>) {
    pages.forEach { page ->
        get(page.path) {
            call.respondText(
                contentType = ContentType.Text.Html,
                text = placeholderHtml(
                    title = page.title,
                    category = page.category,
                    path = page.path,
                ),
            )
        }
    }
}

private fun placeholderHtml(
    title: String,
    category: String,
    path: String,
): String =
    """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>$title</title>
    </head>
    <body>
        <h1>$title</h1>
        <p>$category - Mock Server</p>
        <p>パス: $path</p>
    </body>
    </html>
    """.trimIndent()
