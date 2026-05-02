package io.github.corvus400.fictionaldrugdiseaserefmockserver

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.ktor.server.testing.testApplication
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertTrue

class ApplicationStartupTest {
    @Test
    fun `module startup emits fictional data warning`() {
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        val appender = ListAppender<ILoggingEvent>().also { it.start() }
        rootLogger.addAppender(appender)
        try {
            testApplication {
                application { module() }
            }
        } finally {
            rootLogger.detachAppender(appender)
        }

        val warnings = appender.list.filter { it.level == Level.WARN }
        assertTrue(
            warnings.any { it.formattedMessage.contains("FICTIONAL") },
            "startup WARN logs must contain FICTIONAL disclaimer; warnings=${warnings.map { it.formattedMessage }}",
        )
    }
}
