package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.ResourceLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class LatinToKanaRuleset(
    val overrides: Map<String, String>,
    val digraphRules: List<Pair<String, String>>,
    val syllableRules: List<Pair<String, String>>,
    val vowelRules: List<Pair<String, String>>,
    val finalConsonantRules: Map<String, String>,
    val clusterRules: Map<String, String>,
    val stripCharacters: Set<Char>,
    val wordBoundaryCharacters: Set<Char>,
    val caseFoldLower: Boolean,
) {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        fun load(resourcePath: String = "fixmerge/latin_to_kana.json"): LatinToKanaRuleset {
            val raw = ResourceLoader.readText(resourcePath = resourcePath)
            val root = json.parseToJsonElement(string = raw).jsonObject
            return buildFromJson(root = root)
        }

        private fun buildFromJson(root: JsonObject): LatinToKanaRuleset {
            val normalize = root["normalize"]?.jsonObject
            val strip = normalize?.get("strip_characters")?.jsonArray
                ?.map { it.jsonPrimitive.content.first() }
                ?.toSet()
                .orEmpty()
            val boundary = normalize?.get("word_boundary_characters")?.jsonArray
                ?.map { it.jsonPrimitive.content.first() }
                ?.toSet()
                .orEmpty()
            val caseFold = normalize?.get("case_fold")?.jsonPrimitive?.content == "lower"
            return LatinToKanaRuleset(
                overrides = readStringMap(element = root["overrides"]),
                digraphRules = readPairList(element = root["digraph_rules"]),
                syllableRules = readPairList(element = root["syllable_rules"]),
                vowelRules = readPairList(element = root["vowel_rules"]),
                finalConsonantRules = readStringMap(element = root["final_consonant_rules"]),
                clusterRules = readStringMap(element = root["cluster_rules"]),
                stripCharacters = strip,
                wordBoundaryCharacters = boundary,
                caseFoldLower = caseFold,
            )
        }

        private fun readStringMap(element: JsonElement?): Map<String, String> {
            val obj = element?.jsonObject ?: return emptyMap()
            val result = mutableMapOf<String, String>()
            for ((key, value) in obj) {
                if (key.startsWith("_")) {
                    continue
                }
                if (value is JsonPrimitive && value.isString) {
                    result[key] = value.content
                }
            }
            return result
        }

        private fun readPairList(element: JsonElement?): List<Pair<String, String>> {
            val arr = element?.jsonArray ?: return emptyList()
            return arr.mapNotNull { item ->
                val pair = item as? JsonArray ?: return@mapNotNull null
                if (pair.size < 2) {
                    return@mapNotNull null
                }
                pair[0].jsonPrimitive.content to pair[1].jsonPrimitive.content
            }
        }
    }
}
