package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.AtcEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.CategoriesResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.Icd10ChapterEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.LabeledEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.TherapeuticCategoryEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import kotlinx.serialization.serializer

class CategoriesFixture(
    private val drugs: List<Drug>,
) {
    fun build(): CategoriesResponse = CategoriesResponse(
        atc = ATC_ANATOMICAL_GROUPS.map { group ->
            AtcEntry(code = group.firstLetter, label = group.therapeuticName)
        },
        therapeuticCategories = drugs.map { drug ->
            TherapeuticCategoryEntry(
                id = drug.therapeuticCategoryName.toCategoryId(),
                label = drug.therapeuticCategoryName,
            )
        }.distinctBy { entry -> entry.id },
        routeOfAdministration = enumLabeledEntries<RouteOfAdministration>(),
        dosageForm = enumLabeledEntries<DosageForm>(),
        regulatoryClass = enumLabeledEntries<RegulatoryClass>(),
        icd10Chapters = buildIcd10Chapters(),
        medicalDepartments = enumLabeledEntries<MedicalDepartment>(),
    )

    private fun buildIcd10Chapters(): List<Icd10ChapterEntry> {
        val descriptor = serializer<Icd10Chapter>().descriptor
        return Icd10Chapter.entries.map { chapter ->
            Icd10ChapterEntry(
                roman = chapter.chapterKey,
                code = ICD10_CHAPTER_CODES.getValue(key = chapter),
                label = descriptor.getElementName(index = chapter.ordinal),
            )
        }
    }

    companion object {
        /**
         * 列挙型 [T] の宣言順に `@SerialName` 値を取り出し、`value` / `label` 双方に詰めた
         * `LabeledEntry` リストを返す。
         *
         * `value` は `/drugs?route=<value>` 等のフィルタクエリで API クライアントが指定する
         * `@SerialName` 値 (日本語キー) と一致させる。`label` は同じ値を表示用に再利用する。
         * これにより一覧 API のフィルタ仕様 (例: `regulatory_class`, `route`, `dosage_form`)
         * と `/categories` の選択肢メタデータが同一語彙で結合される。
         */
        private inline fun <reified T : Enum<T>> enumLabeledEntries(): List<LabeledEntry> {
            val descriptor = serializer<T>().descriptor
            return enumValues<T>().map { entry ->
                val serialName = descriptor.getElementName(index = entry.ordinal)
                LabeledEntry(value = serialName, label = serialName)
            }
        }

        /**
         * ATC 第 1 階層 (解剖学的グループ) の単一情報源。
         * 14 グループ (A,B,C,D,G,H,J,L,M,N,P,R,S,V) ごとに以下を集約する:
         *   - firstLetter: ATC コード先頭 1 文字 (`AtcEntry.code` / `/drugs?atc=` クエリ)
         *   - therapeuticName: 日本語治療領域名 (`AtcEntry.label` / `Drug.therapeuticCategoryName`)
         *   - slug: ASCII URL-safe id (`TherapeuticCategoryEntry.id`)
         */
        private data class AtcAnatomicalGroup(
            val firstLetter: String,
            val therapeuticName: String,
            val slug: String,
        )

        private val ATC_ANATOMICAL_GROUPS: List<AtcAnatomicalGroup> = listOf(
            AtcAnatomicalGroup(firstLetter = "A", therapeuticName = "消化器系および代謝", slug = "alimentary_metabolism"),
            AtcAnatomicalGroup(firstLetter = "B", therapeuticName = "血液および造血器", slug = "blood"),
            AtcAnatomicalGroup(firstLetter = "C", therapeuticName = "循環器系", slug = "cardiovascular"),
            AtcAnatomicalGroup(firstLetter = "D", therapeuticName = "皮膚科用", slug = "dermatologicals"),
            AtcAnatomicalGroup(
                firstLetter = "G",
                therapeuticName = "泌尿生殖器系およびホルモン製剤",
                slug = "genito_urinary_hormones",
            ),
            AtcAnatomicalGroup(firstLetter = "H", therapeuticName = "全身性ホルモン製剤", slug = "systemic_hormones"),
            AtcAnatomicalGroup(firstLetter = "J", therapeuticName = "感染症治療薬", slug = "antiinfectives"),
            AtcAnatomicalGroup(
                firstLetter = "L",
                therapeuticName = "抗腫瘍剤および免疫調節剤",
                slug = "antineoplastic_immunomodulators",
            ),
            AtcAnatomicalGroup(firstLetter = "M", therapeuticName = "筋骨格系", slug = "musculo_skeletal"),
            AtcAnatomicalGroup(firstLetter = "N", therapeuticName = "神経系", slug = "nervous"),
            AtcAnatomicalGroup(firstLetter = "P", therapeuticName = "抗寄生虫剤", slug = "antiparasitic"),
            AtcAnatomicalGroup(firstLetter = "R", therapeuticName = "呼吸器系", slug = "respiratory"),
            AtcAnatomicalGroup(firstLetter = "S", therapeuticName = "感覚器", slug = "sensory"),
            AtcAnatomicalGroup(firstLetter = "V", therapeuticName = "その他", slug = "various"),
        )

        private val SLUG_BY_THERAPEUTIC_NAME: Map<String, String> =
            ATC_ANATOMICAL_GROUPS.associate { group -> group.therapeuticName to group.slug }

        private fun String.toCategoryId(): String =
            SLUG_BY_THERAPEUTIC_NAME[this]
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
    }
}
