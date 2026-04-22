package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

private const val ATEJI_SLOT_SALT: Long = 0x3779B97F4A7C1581L
private const val ATEJI_INDEX_STRIDE: Long = -0x61C8864680B583EBL // 0x9E3779B97F4A7C15 as signed Long
private const val ATEJI_FALLBACK_KANJI: Char = '无' // U+65E0, CJK Unified Ideographs fallback for unmapped kana

/**
 * カタカナ文字列から決定論的に当て字漢字を導出するユーティリティ。
 * 各カナ 1 文字に対して漢字候補リストを持ち、seed とインデックスから候補を 1 つ選ぶため、
 * 同じ (kana, seed) に対する出力は常に同一となる。未登録カナは [ATEJI_FALLBACK_KANJI] に
 * フォールバックし、出力は常に CJK Unified Ideographs (0x4E00..0x9FFF) 内に収まる。
 *
 * フィクスマージ語仕様 - 日本語要素表層 Layer (KANJI_ATEJI) の実装。候補辞書は現状 50 音 +
 * 濁音 / 半濁音 / 拗音の基本集合を網羅し、KanaAssembler が出力し得る全カナを覆う。
 */
object KanjiAtejiRules {
    private val katakanaToKanjiCandidates: Map<Char, List<Char>> = mapOf(
        // 母音
        'ア' to listOf('亜', '阿', '安'),
        'イ' to listOf('伊', '依', '医'),
        'ウ' to listOf('宇', '羽', '雨'),
        'エ' to listOf('江', '恵', '榎'),
        'オ' to listOf('於', '央', '桜'),
        // カ行
        'カ' to listOf('加', '賀', '嘉'),
        'キ' to listOf('紀', '貴', '希'),
        'ク' to listOf('久', '空', '玖'),
        'ケ' to listOf('介', '計', '慶'),
        'コ' to listOf('子', '湖', '虎'),
        // サ行
        'サ' to listOf('佐', '沙', '紗'),
        'シ' to listOf('司', '志', '史'),
        'ス' to listOf('寿', '須', '朱'),
        'セ' to listOf('世', '瀬', '勢'),
        'ソ' to listOf('楚', '宗', '祖'),
        // タ行
        'タ' to listOf('太', '多', '田'),
        'チ' to listOf('千', '知', '智'),
        'ツ' to listOf('津', '都', '通'),
        'テ' to listOf('天', '帝', '手'),
        'ト' to listOf('登', '斗', '渡'),
        // ナ行
        'ナ' to listOf('奈', '那', '南'),
        'ニ' to listOf('仁', '二', '爾'),
        'ヌ' to listOf('奴', '怒', '努'),
        'ネ' to listOf('音', '寧', '根'),
        'ノ' to listOf('乃', '野', '埜'),
        // ハ行
        'ハ' to listOf('波', '巴', '葉'),
        'ヒ' to listOf('比', '妃', '飛'),
        'フ' to listOf('不', '富', '芙'),
        'ヘ' to listOf('辺', '部', '閉'),
        'ホ' to listOf('保', '歩', '甫'),
        // マ行
        'マ' to listOf('真', '磨', '麻'),
        'ミ' to listOf('美', '実', '弥'),
        'ム' to listOf('夢', '武', '務'),
        'メ' to listOf('女', '芽', '愛'),
        'モ' to listOf('茂', '望', '喪'),
        // ヤ行
        'ヤ' to listOf('也', '弥', '耶'),
        'ユ' to listOf('由', '悠', '優'),
        'ヨ' to listOf('世', '与', '余'),
        // ラ行
        'ラ' to listOf('良', '羅', '来'),
        'リ' to listOf('利', '理', '里'),
        'ル' to listOf('瑠', '琉', '流'),
        'レ' to listOf('礼', '玲', '麗'),
        'ロ' to listOf('呂', '露', '炉'),
        // ワ行
        'ワ' to listOf('和', '環', '倭'),
        'ヲ' to listOf('乎', '緒', '尾'),
        // 撥音
        'ン' to listOf('运', '雲', '隠'),
        // 濁音
        'ガ' to listOf('雅', '賀', '臥'),
        'ギ' to listOf('儀', '義', '技'),
        'グ' to listOf('具', '求', '遇'),
        'ゲ' to listOf('芸', '牙', '迎'),
        'ゴ' to listOf('吾', '悟', '護'),
        'ザ' to listOf('座', '雑', '挫'),
        'ジ' to listOf('治', '慈', '示'),
        'ズ' to listOf('図', '厨', '逗'),
        'ゼ' to listOf('是', '銭', '膳'),
        'ゾ' to listOf('蔵', '造', '臓'),
        'ダ' to listOf('陀', '駄', '堕'),
        'ヂ' to listOf('遅', '地', '池'),
        'ヅ' to listOf('豆', '通', '図'),
        'デ' to listOf('出', '伝', '弟'),
        'ド' to listOf('土', '度', '努'),
        'バ' to listOf('馬', '場', '芭'),
        'ビ' to listOf('尾', '琵', '眉'),
        'ブ' to listOf('舞', '武', '部'),
        'ベ' to listOf('倍', '辺', '米'),
        'ボ' to listOf('母', '保', '募'),
        // 半濁音
        'パ' to listOf('巴', '波', '把'),
        'ピ' to listOf('比', '飛', '皮'),
        'プ' to listOf('布', '普', '不'),
        'ペ' to listOf('陛', '平', '変'),
        'ポ' to listOf('歩', '甫', '保'),
        // 拗音 (小書き)
        'ャ' to listOf('也'),
        'ュ' to listOf('由'),
        'ョ' to listOf('与'),
    )

    fun toAteji(
        kana: String,
        seed: Long,
    ): String {
        val builder = StringBuilder(kana.length)
        for ((index, kanaChar) in kana.withIndex()) {
            val candidates = katakanaToKanjiCandidates[kanaChar].orEmpty()
            if (candidates.isEmpty()) {
                builder.append(ATEJI_FALLBACK_KANJI)
                continue
            }
            val pickSeed = seed xor ATEJI_SLOT_SALT xor (index.toLong() * ATEJI_INDEX_STRIDE)
            val pickIndex = pickPositive(pickSeed) % candidates.size
            builder.append(candidates[pickIndex])
        }
        return builder.toString()
    }

    private fun pickPositive(value: Long): Int = (value and Long.MAX_VALUE).toInt() and Int.MAX_VALUE
}
