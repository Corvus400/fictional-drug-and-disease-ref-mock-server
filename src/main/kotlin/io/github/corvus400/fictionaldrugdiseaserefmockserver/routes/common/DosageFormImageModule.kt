package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun Application.dosageFormImageModule() {
    routing {
        get("/images/dosage_form/{form}") {
            val form = call.parameters["form"] ?: return@get
            val originalImage = Thread.currentThread().contextClassLoader
                .getResourceAsStream("images/dosage_form/$form.png")
                ?.use { ImageIO.read(it) }
                ?: return@get
            val size = when (call.request.queryParameters["size"]) {
                "S" -> ImageSize.S
                "M" -> ImageSize.M
                else -> ImageSize.ORIGINAL
            }
            val resizedImage = ImageResizer.resize(originalImage, size)
            val bytes = ByteArrayOutputStream().use { output ->
                ImageIO.write(resizedImage, "PNG", output)
                output.toByteArray()
            }
            call.respondBytes(bytes, ContentType.Image.PNG)
        }
    }
}
