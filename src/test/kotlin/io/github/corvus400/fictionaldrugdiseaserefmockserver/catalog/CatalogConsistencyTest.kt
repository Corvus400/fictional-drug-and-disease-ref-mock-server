package io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.ktor.client.request.get
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CatalogConsistencyTest {
    @BeforeTest
    fun setUp() {
        EndpointRegistry.clear()
    }

    @Test
    fun `all ScreenMapping endpointNames exist in EndpointRegistry`() = testApplication {
        application { module() }

        // アプリケーションを初期化してEndpointRegistryを投入する
        client.get("/health")

        val screenMappingNames = ScreenMapping.getAllEndpointNames()
        val registryNames = EndpointRegistry.getAll().map { it.endpointName }.toSet()

        val missingInRegistry = screenMappingNames - registryNames
        assertTrue(
            missingInRegistry.isEmpty(),
            "ScreenMappingに存在するがEndpointRegistryに未登録のendpointName: $missingInRegistry",
        )
    }

    @Test
    fun `all EndpointRegistry endpointNames excluding ADMIN and SYSTEM exist in ScreenMapping`() = testApplication {
        application { module() }

        // アプリケーションを初期化してEndpointRegistryを投入する
        client.get("/health")

        val registryNames = EndpointRegistry.getAll()
            .filter { it.tag != ApiTag.ADMIN && it.tag != ApiTag.SYSTEM }
            .map { it.endpointName }
            .toSet()
        val screenMappingNames = ScreenMapping.getAllEndpointNames()

        val missingInScreenMapping = registryNames - screenMappingNames
        assertTrue(
            missingInScreenMapping.isEmpty(),
            "EndpointRegistryに存在するがScreenMappingに未登録のendpointName: $missingInScreenMapping",
        )
    }
}
