package io.github.corvus400.mockserverbase.testutil

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
