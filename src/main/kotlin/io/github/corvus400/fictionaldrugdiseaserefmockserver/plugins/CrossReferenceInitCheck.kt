package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

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
        // Red-1: dangling なしで throw しないことのみを固定する。
        // Red-2 以降のサイクルで CrossReferenceValidator 呼び出しと throw を段階的に追加する。
    }
}
