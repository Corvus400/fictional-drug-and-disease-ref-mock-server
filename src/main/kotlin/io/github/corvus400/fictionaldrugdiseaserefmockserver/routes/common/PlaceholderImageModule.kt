package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * プレースホルダー画像配信ルート
 *
 * Fixture内の /images/ パスへのリクエストに対して、プレースホルダー画像を返す。
 * Banner等のUI要素は画像読み込み失敗時に非表示になるため、Mock環境でも
 * 有効な画像レスポンスを返すことで表示を可能にする。
 */
fun Application.placeholderImageModule() {
    routing {
        get("/images/{path...}") {
            call.respondBytes(placeholderImageBytes, ContentType.Image.PNG)
        }
        get("/smart/base/images/{path...}") {
            call.respondBytes(placeholderImageBytes, ContentType.Image.PNG)
        }
        get("/shopping/image/{path...}") {
            call.respondBytes(placeholderImageBytes, ContentType.Image.PNG)
        }
    }
}

/** L25,L28,L31の3ルートハンドラで使用。by lazy delegateに対するIntelliJ誤検知。RedundantSuppression自体もIntelliJ偽陽性。 */
@Suppress("unused", "RedundantSuppression")
private val placeholderImageBytes: ByteArray by lazy {
    val width = 400
    val height = 200
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = image.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.color = Color(0x66, 0x88, 0x44)
    g.fillRect(0, 0, width, height)
    g.color = Color.WHITE
    g.font = Font("SansSerif", Font.BOLD, 24)
    val text = "Mock Image"
    val metrics = g.fontMetrics
    g.drawString(text, (width - metrics.stringWidth(text)) / 2, height / 2 + metrics.ascent / 2)
    g.dispose()
    ByteArrayOutputStream().also {
        ImageIO.write(image, "PNG", it)
    }.toByteArray()
}
