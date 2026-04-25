package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ModuleRegistration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioModuleRegistration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.StatelessModuleRegistration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.adminRoutes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories.categoriesCatalogEntries
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories.categoriesModule
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common.placeholderImageModule
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease.diseaseCatalogEntries
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease.diseaseModule
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug.drugCatalogEntries
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug.drugModule
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.sample.sampleCatalogEntries
import io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.sample.sampleModule
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application

/**
 * 全モジュールの登録リスト
 *
 * 新規モジュール追加時:
 * 1. モジュールファイルに `val xxxCatalogEntries: List<EndpointEntry>` を定義
 * 2. このリストに ModuleRegistration を追加
 *
 * catalogEntriesが必須パラメータのため、エントリなしでのモジュール追加はコンパイルエラーになる。
 * 起動時に verifyCatalogCoverage() で双方向整合性を検証するため、
 * allModulesに追加せずにモジュール関数を直接呼び出してもサーバーが起動しない。
 */
private val allModules: List<ModuleRegistration> = listOf(
    ScenarioModuleRegistration(
        catalogEntries = sampleCatalogEntries,
        configure = { sm -> sampleModule(sm) },
    ),
    ScenarioModuleRegistration(
        catalogEntries = drugCatalogEntries,
        configure = { sm -> drugModule(sm) },
    ),
    ScenarioModuleRegistration(
        catalogEntries = diseaseCatalogEntries,
        configure = { sm -> diseaseModule(sm) },
    ),
    StatelessModuleRegistration(
        catalogEntries = categoriesCatalogEntries,
        configure = { categoriesModule() },
    ),
)

fun Application.configureRouting(
    scenarioManager: ScenarioManager,
    config: MockServerConfig,
) {
    // Admin Routes（ADMIN/SYSTEMタグで起動時検証から除外）
    adminRoutes(
        scenarioManager = scenarioManager,
        enableAdminApi = config.enableAdminApi,
    )

    allModules.forEach { module ->
        module.catalogEntries.forEach { EndpointRegistry.register(it) }
        when (module) {
            is ScenarioModuleRegistration -> module.configure(this, scenarioManager)
            is StatelessModuleRegistration -> module.configure(this)
        }
    }

    // Common Modules（カタログ登録不要のインフラモジュール）
    placeholderImageModule()

    // 起動時検証: カタログ登録の双方向整合性チェック
    verifyCatalogCoverage()
}

/**
 * カタログ登録の双方向整合性を起動時に検証する。
 *
 * 検証内容:
 * 1. allModulesで宣言された全endpointNameがEndpointRegistryに存在するか
 * 2. EndpointRegistryの全endpointName（ADMIN/SYSTEM除外）がallModulesで宣言されているか
 *
 * 不一致があればIllegalStateExceptionをスロー → サーバー起動失敗 → テスト全失敗 → CI不通過
 */
private fun verifyCatalogCoverage() {
    val declaredNames = allModules
        .flatMap { it.catalogEntries }
        .map { it.endpointName }
        .toSet()

    val registeredNames = EndpointRegistry.getAll()
        .filter { it.tag != ApiTag.ADMIN && it.tag != ApiTag.SYSTEM }
        .map { it.endpointName }
        .toSet()

    val missingFromRegistry = declaredNames - registeredNames
    check(missingFromRegistry.isEmpty()) {
        "カタログ未登録のエンドポイントがあります: $missingFromRegistry — " +
            "モジュール内でEndpointRegistry.register()を追加してください"
    }

    val undeclaredInModules = registeredNames - declaredNames
    check(undeclaredInModules.isEmpty()) {
        "Routing.kt未宣言のエンドポイントがあります: $undeclaredInModules — " +
            "allModulesにModuleRegistrationを追加してください"
    }
}
