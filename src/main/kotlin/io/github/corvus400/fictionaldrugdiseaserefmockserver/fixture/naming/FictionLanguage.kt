package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

/**
 * Phoneme and syllable tables for the fictional "Fikusumāji" language used to synthesize
 * fiction drug / disease names.
 *
 * Declarative reference data (spec = implementation); not a TDD target. See the
 * Fikusumāji language specification (音素体系 / 音節表) for the canonical source.
 */
object FictionLanguage {
    /** Initial consonant phonemes, including palatalized (拗音) clusters. */
    val initialConsonants: List<String> = listOf(
        "k", "s", "t", "n", "h", "m", "y", "r", "w", "g", "z", "d", "b", "p",
        "ky", "sh", "ch", "ny", "hy", "my", "ry", "gy", "by", "py",
    )

    /** Vowel phonemes. */
    val vowels: List<String> = listOf("a", "i", "u", "e", "o")

    /**
     * Final consonant phonemes appendable to a syllable.
     * Empty strings are included to raise the probability of open (vowel-final) syllables.
     */
    val finalConsonants: List<String> = listOf("n", "nn", "", "", "")

    /**
     * Romaji → Katakana syllable lookup.
     * Covers the cross product of [initialConsonants] × [vowels] that is phonotactically
     * valid in modern Japanese, plus stand-alone vowels and the moraic nasal "n".
     */
    val romajiToKana: Map<String, String> = mapOf(
        "a" to "ア", "i" to "イ", "u" to "ウ", "e" to "エ", "o" to "オ",
        "ka" to "カ", "ki" to "キ", "ku" to "ク", "ke" to "ケ", "ko" to "コ",
        "sa" to "サ", "shi" to "シ", "su" to "ス", "se" to "セ", "so" to "ソ",
        "ta" to "タ", "chi" to "チ", "tsu" to "ツ", "te" to "テ", "to" to "ト",
        "na" to "ナ", "ni" to "ニ", "nu" to "ヌ", "ne" to "ネ", "no" to "ノ",
        "ha" to "ハ", "hi" to "ヒ", "fu" to "フ", "he" to "ヘ", "ho" to "ホ",
        "ma" to "マ", "mi" to "ミ", "mu" to "ム", "me" to "メ", "mo" to "モ",
        "ya" to "ヤ", "yu" to "ユ", "yo" to "ヨ",
        "ra" to "ラ", "ri" to "リ", "ru" to "ル", "re" to "レ", "ro" to "ロ",
        "wa" to "ワ", "wo" to "ヲ",
        "n" to "ン",
        "ga" to "ガ", "gi" to "ギ", "gu" to "グ", "ge" to "ゲ", "go" to "ゴ",
        "za" to "ザ", "ji" to "ジ", "zu" to "ズ", "ze" to "ゼ", "zo" to "ゾ",
        "da" to "ダ", "de" to "デ", "do" to "ド",
        "ba" to "バ", "bi" to "ビ", "bu" to "ブ", "be" to "ベ", "bo" to "ボ",
        "pa" to "パ", "pi" to "ピ", "pu" to "プ", "pe" to "ペ", "po" to "ポ",
        "kya" to "キャ", "kyu" to "キュ", "kyo" to "キョ",
        "sha" to "シャ", "shu" to "シュ", "sho" to "ショ",
        "cha" to "チャ", "chu" to "チュ", "cho" to "チョ",
        "nya" to "ニャ", "nyu" to "ニュ", "nyo" to "ニョ",
        "hya" to "ヒャ", "hyu" to "ヒュ", "hyo" to "ヒョ",
        "mya" to "ミャ", "myu" to "ミュ", "myo" to "ミョ",
        "rya" to "リャ", "ryu" to "リュ", "ryo" to "リョ",
        "gya" to "ギャ", "gyu" to "ギュ", "gyo" to "ギョ",
        "ja" to "ジャ", "ju" to "ジュ", "jo" to "ジョ",
        "bya" to "ビャ", "byu" to "ビュ", "byo" to "ビョ",
        "pya" to "ピャ", "pyu" to "ピュ", "pyo" to "ピョ",
    )

    /**
     * Syllable-count ranges per [NamePattern].
     * Pattern A = short (2-3), Pattern B = standard (3-4), Pattern C = long tail (4-6).
     */
    val syllableRangeByPattern: Map<NamePattern, IntRange> = mapOf(
        NamePattern.PATTERN_A to 2..3,
        NamePattern.PATTERN_B to 3..4,
        NamePattern.PATTERN_C to 4..6,
    )
}

/**
 * Name-shape patterns that select the syllable-count range used by the generator.
 */
enum class NamePattern { PATTERN_A, PATTERN_B, PATTERN_C }
