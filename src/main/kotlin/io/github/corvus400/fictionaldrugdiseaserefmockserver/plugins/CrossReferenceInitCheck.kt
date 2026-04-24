package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.CrossReferenceValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

/**
 * アプリ起動時に fixture 間の参照整合性を検証する fail-fast フック。
 *
 * `CrossReferenceValidator.validate(drugs, diseases)` の結果 (drug→disease /
 * disease→drug / disease→disease dangling) が非空なら `IllegalStateException`
 * を throw し、`Application.module()` からの起動を失敗させる。
 */
object CrossReferenceInitCheck {
    fun run(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ) {
        val violations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseases,
            )
        if (violations.isNotEmpty()) {
            error("Cross-reference violations detected during startup: $violations")
        }
    }
}
