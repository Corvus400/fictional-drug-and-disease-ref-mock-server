package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

private const val PMDA_RESOURCE_PATH: String = "/fixture/forbidden-names-pmda.txt"
private const val ICD10_RESOURCE_PATH: String = "/fixture/forbidden-names-icd10.txt"
private const val CLASS_SUFFIX_RESOURCE_PATH: String = "/fixture/forbidden-class-suffixes.txt"
private const val COMMENT_PREFIX: String = "#"

/**
 * フィクション名生成時に衝突を避けるべき実在名称のブラックリスト。
 * PMDA (医薬品) と ICD-10 (疾患) の 2 ソースを起動時に遅延ロードし、
 * 1 回だけファイル読込を行う。resource ファイルは改行区切り、
 * [COMMENT_PREFIX] 始まりのコメント行と空行は無視する。
 *
 * フィクスマージ語仕様 - ForbiddenNames (Phase 4-5) の実装。
 * 初期 30 語 + 30 語の最小単位で開始し、衝突検出の都度 resource を拡張する運用とする。
 */
object ForbiddenNames {
    private val pmdaList: Set<String> by lazy { loadResource(path = PMDA_RESOURCE_PATH) }
    private val icd10List: Set<String> by lazy { loadResource(path = ICD10_RESOURCE_PATH) }
    private val classSuffixList: Set<String> by lazy { loadResource(path = CLASS_SUFFIX_RESOURCE_PATH) }

    val all: Set<String> by lazy { pmdaList + icd10List }

    fun contains(name: String): Boolean = all.contains(name)

    fun containsClassSuffix(name: String): Boolean = classSuffixList.any { suffix -> name.endsWith(suffix) }

    private fun loadResource(path: String): Set<String> {
        val stream = requireNotNull(this::class.java.getResourceAsStream(path)) {
            "Resource not found: $path"
        }
        return stream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith(COMMENT_PREFIX) }
                .toSet()
        }
    }
}
