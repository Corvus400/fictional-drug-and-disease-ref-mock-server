package io.github.corvus400.mockserverbase.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
                // iOS対応: デフォルト値のフィールドもJSONに含める
                // iOSではisBeginner, isPrimePass等がnon-optionalのため必須
                encodeDefaults = true
            }
        )
    }
}
