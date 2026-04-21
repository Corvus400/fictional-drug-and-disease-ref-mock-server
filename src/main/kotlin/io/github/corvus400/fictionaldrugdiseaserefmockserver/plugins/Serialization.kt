package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
val AppJson: Json = Json {
    prettyPrint = false
    isLenient = true
    ignoreUnknownKeys = true
    // iOS対応: デフォルト値のフィールドもJSONに含める
    // iOSではisBeginner, isPrimePass等がnon-optionalのため必須
    encodeDefaults = true
    namingStrategy = JsonNamingStrategy.SnakeCase
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(AppJson)
    }
}
