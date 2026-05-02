package io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog

/**
 * エンドポイント名 → 画面タグのマッピング
 *
 * 全エンドポイントは少なくとも1つの画面に紐付ける必要がある。
 * CatalogConsistencyTestが双方向の整合性を検証する。
 */
object ScreenMapping {
    private val mapping: Map<String, List<ScreenTag>> = mapOf(
        "drugDetail" to listOf(ScreenTag.DRUG),
        "drugList" to listOf(ScreenTag.DRUG),
        "diseaseDetail" to listOf(ScreenTag.DISEASE),
        "diseaseList" to listOf(ScreenTag.DISEASE),
        // /categories は Drug/Disease 検索画面のフィルタカテゴリ取得用に共通参照される
        "categories" to listOf(ScreenTag.DRUG, ScreenTag.DISEASE),
        "dosageFormImage" to listOf(ScreenTag.DRUG),
    )

    /** エンドポイント名から所属画面のリストを取得する */
    fun getScreens(endpointName: String): List<ScreenTag> =
        mapping[endpointName].orEmpty()

    /** EndpointEntryのリストを画面単位でグルーピングする */
    fun getEndpointsByScreen(
        entries: List<EndpointEntry>,
    ): Map<ScreenTag, List<EndpointEntry>> {
        val result = mutableMapOf<ScreenTag, MutableList<EndpointEntry>>()
        for (entry in entries) {
            val screens = getScreens(endpointName = entry.endpointName)
            for (screen in screens) {
                result.getOrPut(screen) { mutableListOf() }.add(entry)
            }
        }
        return ScreenTag.entries
            .filter { it in result }
            .associateWith { result.getValue(it).toList() }
    }

    fun getAllEndpointNames(): Set<String> = mapping.keys
}
