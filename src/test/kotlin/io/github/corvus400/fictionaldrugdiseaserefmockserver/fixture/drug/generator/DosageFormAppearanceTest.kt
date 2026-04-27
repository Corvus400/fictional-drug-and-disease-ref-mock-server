package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DosageFormAppearanceTest {
    @Test
    fun `pickAppearance returns the same String for the same form and drugId`() {
        val firstCall: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        val secondCall: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        assertEquals(
            expected = firstCall,
            actual = secondCall,
            message = "pickAppearance must be deterministic for the same (form, drugId)",
        )
    }

    @Test
    fun `pickAppearance for TABLET returns one of the TABLET variants`() {
        val tabletVariants: Set<String> = EXPECTED_APPEARANCE_VARIANTS.getValue(DosageForm.TABLET)
        val result: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        assertTrue(
            actual = result in tabletVariants,
            message = "pickAppearance(TABLET, _) must be one of TABLET variants, got '$result'",
        )
    }

    @Test
    fun `pickAppearance for every DosageForm and drug index returns a value in the form variants`() {
        DosageForm.entries.forEach { form ->
            val expected: Set<String> = EXPECTED_APPEARANCE_VARIANTS.getValue(form)
            (0 until DRUG_INVENTORY_SIZE).forEach { index ->
                val drugId: String = drugIdOf(index = index)
                val result: String =
                    DosageFormAppearance.pickAppearance(form = form, drugId = drugId)
                assertTrue(
                    actual = result in expected,
                    message = "pickAppearance($form, $drugId) returned '$result' " +
                        "which is not in the expected $form variants",
                )
            }
        }
    }

    @Test
    fun `pickOriginalSubstanceDescription returns the same String for the same form and drugId`() {
        val firstCall: String =
            DosageFormAppearance.pickOriginalSubstanceDescription(
                form = DosageForm.TABLET,
                drugId = "drug_0001",
            )
        val secondCall: String =
            DosageFormAppearance.pickOriginalSubstanceDescription(
                form = DosageForm.TABLET,
                drugId = "drug_0001",
            )
        assertEquals(
            expected = firstCall,
            actual = secondCall,
            message = "pickOriginalSubstanceDescription must be deterministic for the same input",
        )
    }

    @Test
    fun `pickOriginalSubstanceDescription for every form and drug index returns a value in the form variants`() {
        DosageForm.entries.forEach { form ->
            val expected: Set<String> =
                EXPECTED_ORIGINAL_SUBSTANCE_DESCRIPTION_VARIANTS.getValue(form)
            (0 until DRUG_INVENTORY_SIZE).forEach { index ->
                val drugId: String = drugIdOf(index = index)
                val result: String =
                    DosageFormAppearance.pickOriginalSubstanceDescription(
                        form = form,
                        drugId = drugId,
                    )
                assertTrue(
                    actual = result in expected,
                    message = "pickOriginalSubstanceDescription($form, $drugId) returned '$result' " +
                        "which is not in the expected $form variants",
                )
            }
        }
    }

    private companion object {
        const val DRUG_INVENTORY_SIZE: Int = 120

        fun drugIdOf(index: Int): String =
            "drug_${index.toString().padStart(length = 4, padChar = '0')}"

        val EXPECTED_APPEARANCE_VARIANTS: Map<DosageForm, Set<String>> =
            mapOf(
                DosageForm.TABLET to
                    setOf(
                        "白色と淡青色の二層フィルムコート錠",
                        "白色のフィルムコート錠 (PTP 包装)",
                        "淡青色の素錠 (割線あり)",
                    ),
                DosageForm.CAPSULE to
                    setOf(
                        "青色不透明と無色透明の硬カプセル (内容物: 黄色顆粒)",
                        "青色不透明の硬カプセル",
                        "白色不透明の硬カプセル (PTP 包装)",
                    ),
                DosageForm.POWDER to
                    setOf(
                        "白色の細粒散剤 (アルミ分包)",
                        "白色〜微黄色の散剤",
                        "白色の口腔内崩壊散剤",
                    ),
                DosageForm.GRANULE to
                    setOf(
                        "微黄白色の顆粒剤 (アルミ分包)",
                        "白色の細粒顆粒",
                        "淡黄色の腸溶性顆粒",
                    ),
                DosageForm.LIQUID to
                    setOf(
                        "茶褐色澄明の経口液剤 (アンバーガラス瓶入り)",
                        "無色澄明の経口液剤",
                        "微黄色澄明のシロップ剤",
                    ),
                DosageForm.INJECTION_FORM to
                    setOf(
                        "無色澄明の注射液 (バイアル)",
                        "白色の凍結乾燥粉末 (用時溶解、バイアル)",
                        "微黄色澄明のプレフィルドシリンジ注射液",
                    ),
                DosageForm.OINTMENT to
                    setOf(
                        "白色〜微黄色の軟膏 (チューブ入り)",
                        "淡黄色の親水軟膏",
                        "白色の油脂性軟膏",
                    ),
                DosageForm.CREAM to
                    setOf(
                        "白色のクリーム剤 (チューブ入り)",
                        "微黄色の親水クリーム",
                        "白色の水中油型クリーム",
                    ),
                DosageForm.PATCH to
                    setOf(
                        "淡黄色の経皮吸収型貼付剤 (アルミ袋単包)",
                        "肌色の薬用パップ剤",
                        "白色の経皮吸収型マトリックスパッチ",
                    ),
                DosageForm.EYE_DROPS to
                    setOf(
                        "無色澄明の点眼液 (PE 容器入り)",
                        "微黄色澄明の点眼液",
                        "無色澄明の懸濁性点眼液",
                    ),
                DosageForm.SUPPOSITORY to
                    setOf(
                        "白色の砲弾型坐剤 (PTP 包装)",
                        "淡黄色の油脂性坐剤",
                        "白色の親水性坐剤",
                    ),
                DosageForm.INHALER to
                    setOf(
                        "白色の定量噴霧式吸入剤 (アルミ製エアゾール容器)",
                        "白色のドライパウダー吸入剤",
                        "無色透明の吸入用懸濁液",
                    ),
                DosageForm.NASAL_SPRAY to
                    setOf(
                        "無色澄明の点鼻液 (定量噴霧式 PE 容器)",
                        "微白色懸濁性の点鼻液",
                        "無色澄明の点鼻スプレー (霧状噴霧)",
                    ),
            )

        val EXPECTED_ORIGINAL_SUBSTANCE_DESCRIPTION_VARIANTS: Map<DosageForm, Set<String>> =
            mapOf(
                DosageForm.TABLET to
                    setOf(
                        "白色の結晶性粉末である。",
                        "白色〜微黄色の結晶性粉末である。",
                        "白色の粉末で、においはない。",
                    ),
                DosageForm.CAPSULE to
                    setOf(
                        "ほぼ白色の結晶性粉末で、わずかに苦味を有する。",
                        "微黄色の結晶又は結晶性粉末である。",
                        "白色の粉末で、においはなく、わずかに苦味がある。",
                    ),
                DosageForm.POWDER to
                    setOf(
                        "白色の流動性のある微細粉末で、わずかに吸湿性を示す。",
                        "白色〜微黄色の細かな結晶性粉末である。",
                        "白色の吸湿性粉末である。",
                    ),
                DosageForm.GRANULE to
                    setOf(
                        "微黄白色の結晶性粉末である。",
                        "白色の結晶性粉末で、わずかに苦味がある。",
                        "白色〜淡黄色の粉末である。",
                    ),
                DosageForm.LIQUID to
                    setOf(
                        "茶褐色澄明の液体である。",
                        "無色澄明の液体で、特異な芳香がある。",
                        "微黄色澄明の粘稠性液体である。",
                    ),
                DosageForm.INJECTION_FORM to
                    setOf(
                        "白色の凍結乾燥粉末である。",
                        "無色澄明の液体である。",
                        "白色〜微黄色の結晶性粉末で、水に溶けやすい。",
                    ),
                DosageForm.OINTMENT to
                    setOf(
                        "白色の均質な半固形軟膏で、塗布性に富む。",
                        "微黄色〜淡黄色の油状物質である。",
                        "白色のろう様物質である。",
                    ),
                DosageForm.CREAM to
                    setOf(
                        "白色の均質なエマルションで、なめらかな塗布性を示す。",
                        "微黄色の親水クリーム状半固形物で、わずかに特異臭がある。",
                        "白色の水中油型エマルションで、軽い塗布感がある。",
                    ),
                DosageForm.PATCH to
                    setOf(
                        "淡黄色の経皮吸収型半固形物で、均質な粘着性を示す。",
                        "白色〜微黄色のマトリックス層で、可塑性に富む。",
                        "白色のろう様固体である。",
                    ),
                DosageForm.EYE_DROPS to
                    setOf(
                        "無色澄明の点眼用水溶液で、生理的 pH に調整されている。",
                        "無色澄明の等張水溶液である。",
                        "微黄色澄明の点眼用無菌水溶液である。",
                    ),
                DosageForm.SUPPOSITORY to
                    setOf(
                        "白色の砲弾形の固体で、体温で溶融する。",
                        "淡黄色のろう様物質である。",
                        "白色〜微黄色の油脂性半固形物である。",
                    ),
                DosageForm.INHALER to
                    setOf(
                        "白色の微細結晶性粉末である。",
                        "無色澄明の液体で、揮発性がある。",
                        "白色の細粒子状粉末で、吸湿性がある。",
                    ),
                DosageForm.NASAL_SPRAY to
                    setOf(
                        "無色澄明の鼻腔投与用噴霧液で、わずかに芳香がある。",
                        "無色澄明の鼻腔投与用水溶液である。",
                        "微黄色澄明の点鼻スプレー用懸濁液である。",
                    ),
            )
    }
}
