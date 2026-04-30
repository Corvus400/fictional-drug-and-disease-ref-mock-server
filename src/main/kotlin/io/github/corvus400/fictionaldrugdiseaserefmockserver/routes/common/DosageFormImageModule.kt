package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.dosageFormImageModule() {
    routing {
        get("/images/dosage_form/{form}") {
            val form = call.parameters["form"] ?: return@get
            val bytes = Thread.currentThread().contextClassLoader
                .getResourceAsStream("images/dosage_form/$form.png")
                ?.use { it.readBytes() }
                ?: return@get
            call.respondBytes(bytes, ContentType.Image.PNG)
        }
    }
}
