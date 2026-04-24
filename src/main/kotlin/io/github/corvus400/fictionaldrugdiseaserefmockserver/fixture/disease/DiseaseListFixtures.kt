package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DiseaseFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseSummary

/**
 * `/diseases` 一覧エンドポイント向け FixtureProvider。
 *
 * - `default` シナリオは DI 経由で注入された 80 件の Disease を envelope `DiseaseListResponse` に包む。
 * - `empty` シナリオは空配列 envelope を返し、UI の空状態回帰を検証できる。
 *
 * 起動時に `DiseaseFixtureValidator` で fixture の整合性を fail-fast 検証する。validator が violation を
 * 検出した場合は起動をブロックし、CI で早期に気付ける。
 *
 * `scenarios` / `scenarioTitles` は `FixtureProvider<T>` 契約のためインターフェース経由でのみ読まれる。
 * IntelliJ は具象クラスの直接参照のみ辿るため誤検知が出るので `@Suppress` で固定する
 * (`.claude/rules/kotlin-inspection.md` の FixtureProvider 例外条項)。
 * `RedundantSuppression` 自体も IntelliJ 偽陽性のため同時に抑制する。
 */
@Suppress("unused", "RedundantSuppression")
class DiseaseListFixtures(
    diseases: List<Disease>,
) : FixtureProvider<DiseaseListResponse> {
    init {
        val violations = DiseaseFixtureValidator.validate(diseases = diseases)
        require(value = violations.isEmpty()) {
            "DiseaseListFixtures violations: $violations"
        }
    }

    private val defaultSummaries: List<DiseaseSummary> = diseases.map { disease ->
        DiseaseSummary(
            id = disease.id,
            name = disease.name,
            icd10Chapter = disease.icd10Chapter,
            medicalDepartment = disease.medicalDepartment,
            chronicity = disease.chronicity,
        )
    }

    override val scenarios: Map<String, DiseaseListResponse> = mapOf(
        "default" to DiseaseListResponse(items = defaultSummaries),
        "empty" to DiseaseListResponse(items = emptyList()),
    )

    override val scenarioTitles: Map<String, String> = mapOf(
        "default" to "デフォルト (80件)",
        "empty" to "空レスポンス",
    )

    override fun describeFixture(fixture: DiseaseListResponse): String =
        "items=${fixture.items.size}"
}
