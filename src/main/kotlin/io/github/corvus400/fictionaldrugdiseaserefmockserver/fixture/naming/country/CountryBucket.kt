package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

/**
 * 国別の seed 辞書 (BucketNameCoiner の sourceToken 供給元)。
 *
 * 3 フィールドは意味的カテゴリで分離されており、それぞれに入れる語は当該国の実在語に限る:
 * - [cuisine]: 当該国の料理名 (主菜/副菜/デザート/麺類等の実在料理)
 * - [beverage]: 当該国の飲料名 (酒類/茶/ジュース/コーヒー等の実在飲料)
 * - [cities]: 当該国の都市名 (実在する市/区/地域の固有地名)
 *
 * 各 list の長さ・要素は決定論的 hash 入力として消費される。カテゴリ違反語を投入しても
 * kana 先頭文字のガードテスト等は緑のままになり得るが、設計意図違反のためマージ不可。
 * 差替時は [CountryBucketData] のドキュメント参照。
 */
data class CountryBucket(
    val cuisine: List<String>,
    val beverage: List<String>,
    val cities: List<String>,
)
