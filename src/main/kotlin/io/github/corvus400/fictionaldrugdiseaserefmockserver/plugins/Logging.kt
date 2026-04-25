package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

fun Application.configureLogging() {
    // Phase 9-B run 5: source change to defeat UP-TO-DATE
    install(CallLogging) {
        level = Level.INFO
    }
}
