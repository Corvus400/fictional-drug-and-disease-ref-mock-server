package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.AtcEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.CategoriesResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.Icd10ChapterEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.TherapeuticCategoryEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import kotlinx.serialization.serializer

class CategoriesFixture(
    private val drugs: List<Drug>,
) {
    fun build(): CategoriesResponse = CategoriesResponse(
        atc = TherapeuticCategory.entries.map { category ->
            AtcEntry(code = category.atcInitial.toString(), label = category.displayName)
        },
        therapeuticCategories = drugs.map { drug ->
            val category = drug.therapeuticCategoryName.toTherapeuticCategory()
            TherapeuticCategoryEntry(
                id = category.categoryId,
                label = category.displayName,
            )
        }.distinctBy { entry -> entry.id },
        routeOfAdministration = enumSerialNames<RouteOfAdministration>(),
        dosageForm = enumSerialNames<DosageForm>(),
        regulatoryClass = enumSerialNames<RegulatoryClass>(),
        icd10Chapters = buildIcd10Chapters(),
        medicalDepartments = enumSerialNames<MedicalDepartment>(),
    )

    private fun buildIcd10Chapters(): List<Icd10ChapterEntry> = Icd10Chapter.entries.map { chapter ->
        Icd10ChapterEntry(
            roman = chapter.chapterKey,
            code = ICD10_CHAPTER_CODES.getValue(key = chapter),
            label = ICD10_CHAPTER_LABELS_JA.getValue(key = chapter),
        )
    }

    companion object {
        /**
         * 列挙型 [T] の宣言順に `@SerialName` 値を取り出して `List<String>` を返す。
         *
         * 返却値は `/drugs?route=<value>` 等のフィルタクエリで API クライアントが指定する
         * `@SerialName` 値 (英語 snake_case キー) と一致する。一覧 API のフィルタ仕様
         * (例: `regulatory_class`, `route`, `dosage_form`) と `/categories` の選択肢
         * メタデータが同一語彙で結合される。
         *
         * i18n を持たない本モック環境では `value` / `label` の二重化は冗長 (両者が同一の
         * 英語キーになる) ため、`List<String>` 形式の単一語彙集合のみを公開する。
         */
        private inline fun <reified T : Enum<T>> enumSerialNames(): List<String> {
            val descriptor = serializer<T>().descriptor
            return enumValues<T>().map { entry ->
                descriptor.getElementName(index = entry.ordinal)
            }
        }

        private fun String.toTherapeuticCategory(): TherapeuticCategory =
            TherapeuticCategory.fromDisplayName(displayName = this)
                ?: error("unknown therapeutic category name '$this'")

        /**
         * ICD-10 章別の標準コード範囲。WHO ICD-10 の章コード仕様に揃える。
         */
        private val ICD10_CHAPTER_CODES: Map<Icd10Chapter, String> = mapOf(
            Icd10Chapter.CHAPTER_I to "A00-B99",
            Icd10Chapter.CHAPTER_II to "C00-D48",
            Icd10Chapter.CHAPTER_III to "D50-D89",
            Icd10Chapter.CHAPTER_IV to "E00-E90",
            Icd10Chapter.CHAPTER_V to "F00-F99",
            Icd10Chapter.CHAPTER_VI to "G00-G99",
            Icd10Chapter.CHAPTER_VII to "H00-H59",
            Icd10Chapter.CHAPTER_VIII to "H60-H95",
            Icd10Chapter.CHAPTER_IX to "I00-I99",
            Icd10Chapter.CHAPTER_X to "J00-J99",
            Icd10Chapter.CHAPTER_XI to "K00-K93",
            Icd10Chapter.CHAPTER_XII to "L00-L99",
            Icd10Chapter.CHAPTER_XIII to "M00-M99",
            Icd10Chapter.CHAPTER_XIV to "N00-N99",
            Icd10Chapter.CHAPTER_XV to "O00-O99",
            Icd10Chapter.CHAPTER_XVI to "P00-P96",
            Icd10Chapter.CHAPTER_XVII to "Q00-Q99",
            Icd10Chapter.CHAPTER_XVIII to "R00-R99",
            Icd10Chapter.CHAPTER_XIX to "S00-T98",
            Icd10Chapter.CHAPTER_XX to "V01-Y98",
            Icd10Chapter.CHAPTER_XXI to "Z00-Z99",
            Icd10Chapter.CHAPTER_XXII to "U00-U85",
        )

        /**
         * ICD-10 全 22 章の日本語名 (UI 表示用)。`atc[].label` (日本語治療領域名) や
         * `therapeutic_categories[].label` (日本語薬効分類名) と意味を揃え、`/categories`
         * 配下の全 `label` フィールドを「人間可読な日本語名」で統一する。
         *
         * `Icd10Chapter` enum の `@SerialName` 値 (`chapter_i` 等) は `/diseases?icd10_chapter=`
         * クエリの ASCII キーとしての役割を担うため、UI 表示は本マスタを単一情報源として独立供給する。
         */
        private val ICD10_CHAPTER_LABELS_JA: Map<Icd10Chapter, String> = mapOf(
            Icd10Chapter.CHAPTER_I to "感染症および寄生虫症",
            Icd10Chapter.CHAPTER_II to "新生物",
            Icd10Chapter.CHAPTER_III to "血液および造血器の疾患ならびに免疫機構の障害",
            Icd10Chapter.CHAPTER_IV to "内分泌、栄養および代謝疾患",
            Icd10Chapter.CHAPTER_V to "精神および行動の障害",
            Icd10Chapter.CHAPTER_VI to "神経系の疾患",
            Icd10Chapter.CHAPTER_VII to "眼および付属器の疾患",
            Icd10Chapter.CHAPTER_VIII to "耳および乳様突起の疾患",
            Icd10Chapter.CHAPTER_IX to "循環器系の疾患",
            Icd10Chapter.CHAPTER_X to "呼吸器系の疾患",
            Icd10Chapter.CHAPTER_XI to "消化器系の疾患",
            Icd10Chapter.CHAPTER_XII to "皮膚および皮下組織の疾患",
            Icd10Chapter.CHAPTER_XIII to "筋骨格系および結合組織の疾患",
            Icd10Chapter.CHAPTER_XIV to "腎尿路生殖器系の疾患",
            Icd10Chapter.CHAPTER_XV to "妊娠、分娩および産褥",
            Icd10Chapter.CHAPTER_XVI to "周産期に発生した病態",
            Icd10Chapter.CHAPTER_XVII to "先天奇形、変形および染色体異常",
            Icd10Chapter.CHAPTER_XVIII to "症状、徴候および異常臨床所見・異常検査所見で他に分類されないもの",
            Icd10Chapter.CHAPTER_XIX to "損傷、中毒およびその他の外因の影響",
            Icd10Chapter.CHAPTER_XX to "傷病および死亡の外因",
            Icd10Chapter.CHAPTER_XXI to "健康状態に影響を及ぼす要因および保健サービスの利用",
            Icd10Chapter.CHAPTER_XXII to "特殊目的用コード",
        )
    }
}
