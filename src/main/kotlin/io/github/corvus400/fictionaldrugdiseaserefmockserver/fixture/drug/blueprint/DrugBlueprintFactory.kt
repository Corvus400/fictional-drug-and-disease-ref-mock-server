package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass

/**
 * Declarative factory producing the 120-drug Blueprint inventory per the ATC-first-letter
 * distribution mandated by the drug model specification (§組合せ数計算 §(F) 分布設計).
 *
 * The Blueprint layer only captures classification axes and a seed-determining index.
 * Conditional-required fields (警告 / 取扱い上の注意 / 保険給付上の注意 等) are the Generator
 * layer's responsibility.
 */
object DrugBlueprintFactory {
    fun build(): List<DrugBlueprint> {
        val atcLetters: List<Char> =
            ATC_DISTRIBUTION.flatMap { (letter, count) -> List(count) { letter } }
        return atcLetters.mapIndexed { index, atcLetter ->
            val baseBlueprint =
                DrugBlueprint(
                    index = index,
                    atcFirstLetter = atcLetter,
                    regulatoryClasses =
                    deriveRegulatoryClasses(
                        atcLetter = atcLetter,
                        index = index,
                    ),
                    isBiological = isBiological(atcLetter = atcLetter, index = index),
                    isChronicPrescription = isChronicPrescription(atcLetter = atcLetter),
                    dosageForm = deriveDosageForm(atcLetter = atcLetter, index = index),
                )
            FIXED_OVERRIDES[index]?.invoke(baseBlueprint) ?: baseBlueprint
        }
    }

    /**
     * `FIXED_OVERRIDES` で再 ID 化された blueprint の `index → idOverride` を公開する。
     *
     * disease 側 fixture の `relatedDrugIds` 生成箇所で
     * `drug_NNNN` を override id に置換するために参照する。
     */
    val FIXED_OVERRIDE_IDS_BY_INDEX: Map<Int, String> =
        mapOf(
            80 to "LIQUID_SP_TREDECIM",
            89 to "LIQUID_SP_SLEEP_AID",
        )

    private val FIXED_OVERRIDES: Map<Int, DrugBlueprint.() -> DrugBlueprint> =
        mapOf(
            80 to {
                copy(
                    idOverride = "LIQUID_SP_TREDECIM",
                    dosageForm = DosageForm.LIQUID,
                    regulatoryClasses =
                    setOf(RegulatoryClass.POISON, RegulatoryClass.PRESCRIPTION_REQUIRED),
                    nameOverride =
                    NameOverride(
                        brandKatakana = "トレデキム",
                        genericKatakana = "トレデキム",
                        genericLatin = "tredecim",
                    ),
                    textOverride =
                    FixedDrugTextOverride(
                        appearance = "無色澄明の液体を充填した透明ガラスバイアル " +
                            "(ラベルに「13」表記、銀色アルミシール)",
                        originalSubstanceDescription = "無色澄明の液体である。",
                    ),
                )
            },
            89 to {
                val coinedNames: Pair<CoinedName, CoinedName> = coinSleepAidNames()
                copy(
                    idOverride = "LIQUID_SP_SLEEP_AID",
                    dosageForm = DosageForm.LIQUID,
                    regulatoryClasses =
                    setOf(RegulatoryClass.PSYCHOTROPIC_1, RegulatoryClass.PRESCRIPTION_REQUIRED),
                    nameOverride =
                    NameOverride(
                        brandKatakana = coinedNames.first.katakana,
                        genericKatakana = coinedNames.second.katakana,
                        genericLatin = coinedNames.second.latin,
                    ),
                    textOverride =
                    FixedDrugTextOverride(
                        appearance = "無色澄明の液体を充填した透明ガラスバイアル " +
                            "(白色ラベル、銀色アルミシール)",
                        originalSubstanceDescription = "無色澄明の液体で、わずかに特異な芳香がある。",
                    ),
                )
            },
        )

    private fun coinSleepAidNames(): Pair<CoinedName, CoinedName> {
        val adapter = FixmergeNameAdapter()
        val brandSeed: Long =
            stableHash(
                id = SLEEP_AID_NAME_SEED_TOKEN,
                slot = NameSlot.DRUG_BRAND.ordinal,
                index = 0,
            )
        val genericSeed: Long =
            stableHash(
                id = SLEEP_AID_NAME_SEED_TOKEN,
                slot = NameSlot.DRUG_GENERIC.ordinal,
                index = 0,
            )
        val brand: CoinedName = adapter.coin(slot = NameSlot.DRUG_BRAND, seed = brandSeed)
        val generic: CoinedName = adapter.coin(slot = NameSlot.DRUG_GENERIC, seed = genericSeed)
        return brand to generic
    }

    private const val SLEEP_AID_NAME_SEED_TOKEN: String = "アリサの睡眠薬"

    internal fun deriveDosageForm(atcLetter: Char, index: Int): DosageForm {
        val forms: List<DosageForm> = DOSAGE_FORMS_BY_ATC.getValue(atcLetter)
        return forms[index % forms.size]
    }

    private fun deriveRegulatoryClasses(
        atcLetter: Char,
        index: Int,
    ): Set<RegulatoryClass> =
        when (atcLetter) {
            'N' -> setOf(neuralRegulatoryClass(index = index), RegulatoryClass.PRESCRIPTION_REQUIRED)
            'L' ->
                setOf(
                    antineoplasticRegulatoryClass(index = index),
                    RegulatoryClass.PRESCRIPTION_REQUIRED,
                )
            else -> setOf(RegulatoryClass.ORDINARY)
        }

    private fun neuralRegulatoryClass(index: Int): RegulatoryClass =
        when (index % NEURAL_CYCLE) {
            0 -> RegulatoryClass.NARCOTIC
            1 -> RegulatoryClass.PSYCHOTROPIC_1
            2 -> RegulatoryClass.PSYCHOTROPIC_2
            else -> RegulatoryClass.PSYCHOTROPIC_3
        }

    private fun antineoplasticRegulatoryClass(index: Int): RegulatoryClass =
        when (index % ANTINEOPLASTIC_CYCLE) {
            0 -> RegulatoryClass.POISON
            else -> RegulatoryClass.POTENT
        }

    private fun isBiological(atcLetter: Char, index: Int): Boolean =
        (atcLetter == 'L' || atcLetter == 'J') && (index % BIOLOGICAL_PERIOD == 0)

    private fun isChronicPrescription(atcLetter: Char): Boolean =
        atcLetter == 'A' || atcLetter == 'C'

    private const val NEURAL_CYCLE: Int = 4
    private const val ANTINEOPLASTIC_CYCLE: Int = 2
    private const val BIOLOGICAL_PERIOD: Int = 4

    internal val DOSAGE_FORMS_BY_ATC: Map<Char, List<DosageForm>> =
        mapOf(
            'A' to listOf(
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.GRANULE,
                DosageForm.POWDER,
                DosageForm.LIQUID,
            ),
            'B' to listOf(
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.INJECTION_FORM,
            ),
            'C' to listOf(
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.GRANULE,
                DosageForm.PATCH,
            ),
            'D' to listOf(
                DosageForm.OINTMENT,
                DosageForm.CREAM,
                DosageForm.PATCH,
                DosageForm.LIQUID,
            ),
            'G' to listOf(
                DosageForm.TABLET,
                DosageForm.SUPPOSITORY,
                DosageForm.OINTMENT,
            ),
            'H' to listOf(
                DosageForm.TABLET,
                DosageForm.INJECTION_FORM,
            ),
            'J' to listOf(
                DosageForm.INJECTION_FORM,
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.GRANULE,
            ),
            'L' to listOf(
                DosageForm.INJECTION_FORM,
                DosageForm.TABLET,
                DosageForm.CAPSULE,
            ),
            'M' to listOf(
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.OINTMENT,
                DosageForm.PATCH,
            ),
            'N' to listOf(
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.GRANULE,
                DosageForm.LIQUID,
                DosageForm.PATCH,
            ),
            'P' to listOf(
                DosageForm.TABLET,
                DosageForm.INJECTION_FORM,
            ),
            'R' to listOf(
                DosageForm.INHALER,
                DosageForm.NASAL_SPRAY,
                DosageForm.TABLET,
                DosageForm.CAPSULE,
                DosageForm.LIQUID,
            ),
            'S' to listOf(
                DosageForm.EYE_DROPS,
                DosageForm.OINTMENT,
            ),
            'V' to listOf(
                DosageForm.INJECTION_FORM,
                DosageForm.TABLET,
            ),
        )

    private val ATC_DISTRIBUTION: Map<Char, Int> =
        linkedMapOf(
            'A' to 20,
            'B' to 6,
            'C' to 20,
            'D' to 8,
            'G' to 4,
            'H' to 4,
            'J' to 18,
            'L' to 4,
            'M' to 3,
            'N' to 18,
            'P' to 1,
            'R' to 12,
            'S' to 1,
            'V' to 1,
        )
}
