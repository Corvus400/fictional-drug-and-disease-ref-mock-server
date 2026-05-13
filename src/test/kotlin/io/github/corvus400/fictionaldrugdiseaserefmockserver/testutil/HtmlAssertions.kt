package io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/** HTML文字列をjsoupでパースする */
fun parseHtml(html: String): Document = Jsoup.parse(html)

/** CSSセレクタに一致する要素が存在することを検証し、最初の要素を返す */
fun Document.assertElementExists(
    cssSelector: String,
    message: String? = null,
): Element {
    val element = selectFirst(cssSelector)
    assertNotNull(
        actual = element,
        message = message ?: "Expected element matching '$cssSelector' but none found",
    )
    return element
}

/** CSSセレクタに一致する要素数を検証する */
fun Document.assertElementCount(
    cssSelector: String,
    expected: Int,
) {
    val actual = select(cssSelector).size
    assertEquals(
        expected = expected,
        actual = actual,
        message = "Expected $expected elements matching '$cssSelector' but found $actual",
    )
}

/** CSSセレクタごとの存在有無をまとめて検証する */
fun Document.assertElementsExist(cssSelectors: Collection<String>) {
    val missingSelectors = cssSelectors
        .filter { cssSelector -> select(cssSelector).isEmpty() }

    assertEquals(
        expected = emptyList(),
        actual = missingSelectors,
        message = "Expected elements for selectors but none found: $missingSelectors",
    )
}

/** CSSセレクタに一致する要素数と、最初の要素が含むテキストをまとめて検証する */
fun Document.assertElementCountAndTextContains(
    cssSelector: String,
    expectedCount: Int,
    expectedText: String,
) {
    val elements = select(cssSelector)
    assertEquals(
        expected = ElementCountAndTextContainsSnapshot(
            count = expectedCount,
            firstElementContainsText = true,
        ),
        actual = ElementCountAndTextContainsSnapshot(
            count = elements.size,
            firstElementContainsText = elements.firstOrNull()?.text()?.contains(expectedText) == true,
        ),
        message = "Expected $expectedCount '$cssSelector' elements and first text to contain '$expectedText'",
    )
}

/** CSSセレクタに一致する要素数と、最初の要素が指定テキストをすべて含むことをまとめて検証する */
fun Document.assertElementCountAndTextContainsAll(
    cssSelector: String,
    expectedCount: Int,
    expectedTexts: Collection<String>,
) {
    val elements = select(cssSelector)
    val text = elements.firstOrNull()?.text().orEmpty()
    assertEquals(
        expected = ElementCountAndMissingTextsSnapshot(count = expectedCount, missingTexts = emptyList()),
        actual = ElementCountAndMissingTextsSnapshot(
            count = elements.size,
            missingTexts = expectedTexts.filterNot { expectedText -> text.contains(expectedText) },
        ),
        message = "Expected $expectedCount '$cssSelector' elements and first text to contain $expectedTexts",
    )
}

/** CSSセレクタに一致する要素数の下限と、最初の要素テキストをまとめて検証する */
fun Document.assertElementMinimumCountAndFirstTextEquals(
    cssSelector: String,
    minimumCount: Int,
    expectedFirstText: String,
) {
    val elements = select(cssSelector)
    assertEquals(
        expected = ElementMinimumCountAndFirstTextSnapshot(
            hasAtLeastMinimumCount = true,
            firstText = expectedFirstText,
        ),
        actual = ElementMinimumCountAndFirstTextSnapshot(
            hasAtLeastMinimumCount = elements.size >= minimumCount,
            firstText = elements.firstOrNull()?.text(),
        ),
        message = "Expected at least $minimumCount '$cssSelector' elements and first text '$expectedFirstText'",
    )
}

/** CSSセレクタに一致する要素群のテキスト一覧を順序付きで検証する */
fun Document.assertElementsTextEquals(
    cssSelector: String,
    expectedTexts: List<String>,
) {
    assertEquals(
        expected = expectedTexts,
        actual = select(cssSelector).map { element -> element.text() },
        message = "Expected '$cssSelector' texts to be $expectedTexts",
    )
}

/** CSSセレクタに一致する要素群のどれかが指定テキストを含むことを検証する */
fun Document.assertAnyElementTextContains(
    cssSelector: String,
    expectedText: String,
) {
    val actualTexts = select(cssSelector).map { element -> element.text() }
    assertTrue(
        actual = actualTexts.any { actualText -> actualText.contains(expectedText) },
        message = "Expected one '$cssSelector' text to contain '$expectedText' but was $actualTexts",
    )
}

/** CSSセレクタに一致する最初の要素のテキストが指定文字列を含むことを検証する */
fun Document.assertElementTextContains(
    cssSelector: String,
    expectedText: String,
) {
    val actualText = selectFirst(cssSelector)?.text()
    assertTrue(
        actual = actualText?.contains(expectedText) == true,
        message = "Expected '$cssSelector' text to contain '$expectedText' but was '$actualText'",
    )
}

/** CSSセレクタに一致する最初の要素のテキストが指定文字列と一致することを検証する */
fun Document.assertElementTextEquals(
    cssSelector: String,
    expectedText: String,
) {
    val actualText = selectFirst(cssSelector)?.text()
    assertEquals(
        expected = expectedText,
        actual = actualText,
        message = "Expected '$cssSelector' text to be '$expectedText' but was '$actualText'",
    )
}

/** CSSセレクタごとの存在しないことをまとめて検証する */
fun Document.assertNoElements(cssSelectors: Collection<String>) {
    val presentSelectors = cssSelectors
        .filter { cssSelector -> select(cssSelector).isNotEmpty() }

    assertEquals(
        expected = emptyList(),
        actual = presentSelectors,
        message = "Expected no elements for selectors but found: $presentSelectors",
    )
}

/** CSSセレクタに一致する要素が存在しないことを検証する */
fun Document.assertNoElement(cssSelector: String) {
    val elements = select(cssSelector)
    if (elements.isNotEmpty()) {
        fail("Expected no elements matching '$cssSelector' but found ${elements.size}")
    }
}

/** 要素の属性値を検証する */
fun Element.assertAttribute(
    name: String,
    value: String,
) {
    val actual = attr(name)
    assertEquals(
        expected = value,
        actual = actual,
        message = "Expected attribute '$name' to be '$value' but was '$actual' on <${tagName()}>",
    )
}

/** 要素に属性が存在することを検証する */
fun Element.assertHasAttribute(name: String) {
    assertTrue(
        actual = hasAttr(name),
        message = "Expected attribute '$name' on <${tagName()}> but not found",
    )
}

/** CSSセレクタに一致する最初の要素が指定属性をすべて持つことを検証する */
fun Document.assertElementHasAttributes(
    cssSelector: String,
    attributeNames: Collection<String>,
) {
    val element = selectFirst(cssSelector)
    val missingAttributes = attributeNames
        .filterNot { attributeName -> element?.hasAttr(attributeName) == true }

    assertEquals(
        expected = emptyList(),
        actual = missingAttributes,
        message = "Expected '$cssSelector' to have attributes but missing: $missingAttributes",
    )
}

/** CSSセレクタに一致する要素群の指定属性値を順序付きで検証する */
fun Document.assertElementsAttributeValues(
    cssSelector: String,
    attributeName: String,
    expectedValues: List<String>,
) {
    assertEquals(
        expected = expectedValues,
        actual = select(cssSelector).map { element -> element.attr(attributeName) },
        message = "Expected '$cssSelector' attribute '$attributeName' values to be $expectedValues",
    )
}

private data class ElementCountAndTextContainsSnapshot(
    val count: Int,
    val firstElementContainsText: Boolean,
)

private data class ElementCountAndMissingTextsSnapshot(
    val count: Int,
    val missingTexts: List<String>,
)

private data class ElementMinimumCountAndFirstTextSnapshot(
    val hasAtLeastMinimumCount: Boolean,
    val firstText: String?,
)

/** scriptタグ内にJavaScript関数定義が存在することを検証する */
fun Document.assertJsFunctionDefined(name: String) {
    val scriptTexts = select("script").joinToString(separator = "\n") { it.data() }
    val pattern = Regex("""function\s+$name\s*\(""")
    assertTrue(
        actual = pattern.containsMatchIn(scriptTexts),
        message = "Expected JS function '$name' to be defined in <script> tags",
    )
}

/** scriptタグ内に指定文字列が含まれることを検証する */
fun Document.assertJsContains(snippet: String) {
    val scriptTexts = select("script").joinToString(separator = "\n") { it.data() }
    assertTrue(
        actual = scriptTexts.contains(snippet),
        message = "Expected <script> tags to contain '$snippet'",
    )
}

/** styleタグ内にCSSクラスが定義されていることを検証する */
fun Document.assertCssClassDefined(className: String) {
    val styleTexts = select("style").joinToString(separator = "\n") { it.data() }
    assertTrue(
        actual = styleTexts.contains(".$className"),
        message = "Expected CSS class '.$className' to be defined in <style> tags",
    )
}

/**
 * HTML要素で使用されている全CSSクラスがstyleタグ内に定義されていることを検証する
 *
 * body内の要素の class 属性から全クラス名を収集し、
 * styleタグ内に `.className` が出現するかチェックする。
 *
 * @param excludeClasses スタイル定義を持たないマーカークラス等、チェック対象外にするクラス名
 */
fun Document.assertUsedCssClassesAreDefined(excludeClasses: Set<String> = emptySet()) {
    val styleTexts = select("style").joinToString(separator = "\n") { it.data() }
    val usedClasses = body().allElements
        .flatMap { it.classNames() }
        .filter { it.isNotBlank() }
        .toSet()

    val undefinedClasses = usedClasses
        .minus(excludeClasses)
        .filter { className ->
            styleTexts.contains(".$className").not()
        }

    if (undefinedClasses.isNotEmpty()) {
        fail(
            "The following CSS classes are used in HTML but not defined in <style>: " +
                undefinedClasses.sorted().joinToString(separator = ", "),
        )
    }
}
