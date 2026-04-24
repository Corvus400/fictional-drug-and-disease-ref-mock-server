package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

/**
 * 単一 fixture 内の **node 違反** (1 エンティティのフィールド単位の不整合) を表す。
 *
 * 例: "CHAPTER_V の disease に diagnosticCriteria が無い" /
 *     "日付が ISO 8601 でない" / "ID 採番が連番違反"。
 * 主に [DrugFixtureValidator] / [DiseaseFixtureValidator] が返す。
 *
 * ## [CrossRefViolation] と統合していない理由
 *
 * drug↔disease の **edge 違反** (存在しない ID への dangling 参照) は
 * [CrossRefViolation] で別型として扱う。共通フィールドは entityType/entityId の
 * 2 つだけで、[FixtureViolation] は `field` + 自由文 `message` という人間向け形状、
 * [CrossRefViolation] は `targetType` + `danglingTargetId` という ID 構造化形状で
 * 情報の持ち方が本質的に異なる。統合すると以下のいずれかが発生する:
 *
 * - 単純 union → 半分 null 可フィールドになり型が意味的ノイズを許す
 * - 自由文への畳み込み → 構造化された dangling 先 ID 情報が失われる
 *
 * 将来「全 violation を 1 コレクションで集約表示する」需要が出たら
 * `sealed interface Violation` 化が候補になるが、現状そのコンシューマーは無く YAGNI。
 * d65949f の unify commit は単体バリデータ内 2 型の統合が目的で、CrossRef は射程外。
 */
data class FixtureViolation(
    val entityType: String,
    val entityId: String,
    val field: String,
    val message: String,
)
