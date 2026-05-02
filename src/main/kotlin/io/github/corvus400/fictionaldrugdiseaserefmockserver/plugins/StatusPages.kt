package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            if (call.request.path().startsWithKnownRoutePrefix()) {
                return@status
            }
            call.respond(
                status,
                ErrorResponse(
                    code = "NOT_FOUND",
                    message = "Route not found",
                ),
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    code = "INTERNAL_SERVER_ERROR",
                    message = cause.message ?: "Unknown error",
                ),
            )
        }
    }
}

private fun String.startsWithKnownRoutePrefix(): Boolean =
    listOf("/v1/drugs", "/drugs", "/diseases", "/categories", "/__admin", "/health").any { prefix ->
        startsWith(prefix)
    }
