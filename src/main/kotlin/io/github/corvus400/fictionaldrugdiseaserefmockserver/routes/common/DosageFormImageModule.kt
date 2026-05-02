package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.routing
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

private val dosageFormImageMetadata = EndpointMetadata(
    path = "/v1/images/dosage-forms/{form}",
    method = HttpMethod.Get,
    endpointName = "dosageFormImage",
    tag = ApiTag.DRUG,
    summary = "剤形画像を取得する",
)

private val dosageFormImageScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(
        name = "default",
        title = "デフォルト",
        description = "13 種類の剤形 (capsule/cream/eye_drops/granule/inhaler/injection_form/liquid/" +
            "nasal_spray/ointment/patch/powder/suppository/tablet) の PNG 画像を返す。" +
            "?size=S|M|Original でリサイズ対応",
    ),
)

val dosageFormImageCatalogEntries: List<EndpointEntry> = listOf(
    dosageFormImageMetadata.toEntry(scenarios = dosageFormImageScenarios),
)

fun Application.dosageFormImageRoute() {
    routing {
        get("/v1/images/dosage-forms/{form}", {
            summary = dosageFormImageMetadata.summary
            description = dosageFormImageScenarios.first().description
            tags(dosageFormImageMetadata.tag.tagName)
            request {
                pathParameter<String>("form") {
                    description = "剤形名 (例: tablet, capsule)"
                }
                queryParameter<String>("size") {
                    description = "S|M|Original (省略時 Original)"
                    required = false
                }
            }
            response {
                code(HttpStatusCode.OK) {
                    description = "指定 form の PNG 画像"
                    body<ByteArray> {
                        mediaTypes(ContentType.Image.PNG)
                    }
                }
                code(HttpStatusCode.NotFound) {
                    description = "指定 form の画像が存在しない"
                }
                code(HttpStatusCode.BadRequest) {
                    description = "size に S/M/Original 以外を指定"
                }
            }
        }) {
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
    }
}

fun Application.drugImageRoute() {
    routing {
        get("/v1/images/drugs/{drugId}") {
            val drugId = call.parameters["drugId"] ?: return@get
            val size = when (call.request.queryParameters["size"]) {
                "S" -> ImageSize.S
                "M" -> ImageSize.M
                null, "Original" -> ImageSize.ORIGINAL
                else -> return@get call.respond(HttpStatusCode.BadRequest)
            }
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
    val originalBytes = Thread.currentThread().contextClassLoader
        .getResourceAsStream(resourcePath)
        ?.use { it.readBytes() }
        ?: return null
    if (size == ImageSize.ORIGINAL) return originalBytes

    val originalImage = ImageIO.read(ByteArrayInputStream(originalBytes)) ?: return null
    val resizedImage = ImageResizer.resize(originalImage, size)
    return ByteArrayOutputStream().use { output ->
        ImageIO.write(resizedImage, "PNG", output)
        output.toByteArray()
    }
}
