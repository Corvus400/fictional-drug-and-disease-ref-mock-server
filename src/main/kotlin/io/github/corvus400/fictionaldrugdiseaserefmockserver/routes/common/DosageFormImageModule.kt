package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun Application.dosageFormImageModule() {
    routing {
        get("/images/dosage_form/{form}") {
            val form = call.parameters["form"] ?: return@get
            val size = when (call.request.queryParameters["size"]) {
                "S" -> ImageSize.S
                "M" -> ImageSize.M
                null, "Original" -> ImageSize.ORIGINAL
                else -> return@get call.respond(HttpStatusCode.BadRequest)
            }
            val bytes = loadImageBytes(resourcePath = "images/dosage_form/$form.png", size = size)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondBytes(bytes, ContentType.Image.PNG)
        }

        get("/images/drug/{drugId}") {
            val drugId = call.parameters["drugId"] ?: return@get
            val bytes = loadImageBytes(resourcePath = "images/drug/$drugId.png", size = ImageSize.ORIGINAL)
                ?: ByteArrayOutputStream().use { output ->
                    ImageIO.write(BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "PNG", output)
                    output.toByteArray()
                }
            call.respondBytes(bytes, ContentType.Image.PNG)
        }
    }
}

private fun loadImageBytes(
    resourcePath: String,
    size: ImageSize,
): ByteArray? {
    val originalImage = Thread.currentThread().contextClassLoader
        .getResourceAsStream(resourcePath)
        ?.use { ImageIO.read(it) }
        ?: return null
    val resizedImage = ImageResizer.resize(originalImage, size)
    return ByteArrayOutputStream().use { output ->
        ImageIO.write(resizedImage, "PNG", output)
        output.toByteArray()
    }
}
