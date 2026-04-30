package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun Application.dosageFormImageModule() {
    routing {
        get("/images/dosage_form/{form}") {
            val size = ImageSize.fromQueryValue(call.request.queryParameters["size"])
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val form = call.parameters["form"] ?: return@get call.respond(HttpStatusCode.NotFound)
            val bytes = loadImageBytes(resourcePath = "images/dosage_form/$form.png", size = size)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondBytes(bytes, ContentType.Image.PNG)
        }

        get("/images/drug/{drugId}") {
            val size = ImageSize.fromQueryValue(call.request.queryParameters["size"])
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val drugId = call.parameters["drugId"] ?: return@get call.respond(HttpStatusCode.NotFound)
            val bytes = loadImageBytes(resourcePath = "images/drug/$drugId.png", size = size)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondBytes(bytes, ContentType.Image.PNG)
        }
    }
}

private fun loadImageBytes(
    resourcePath: String,
    size: ImageSize,
): ByteArray? {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath) ?: return null
    val originalImage = inputStream.use { ImageIO.read(it) } ?: return null
    val resizedImage = ImageResizer.resize(originalImage = originalImage, size = size)
    return ByteArrayOutputStream().use { output ->
        ImageIO.write(resizedImage, "PNG", output)
        output.toByteArray()
    }
}
