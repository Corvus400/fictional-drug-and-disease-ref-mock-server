package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

object DosageFormAppearance {
    private val appearanceVariants: Map<DosageForm, List<String>> =
        mapOf(
            DosageForm.TABLET to
                listOf(
                    "白色と淡青色の二層フィルムコート錠",
                    "白色のフィルムコート錠 (PTP 包装)",
                    "淡青色の素錠 (割線あり)",
                ),
            DosageForm.CAPSULE to
                listOf(
                    "青色不透明と無色透明の硬カプセル (内容物: 黄色顆粒)",
                    "青色不透明の硬カプセル",
                    "白色不透明の硬カプセル (PTP 包装)",
                ),
            DosageForm.POWDER to
                listOf(
                    "白色の細粒散剤 (アルミ分包)",
                    "白色〜微黄色の散剤",
                    "白色の口腔内崩壊散剤",
                ),
            DosageForm.GRANULE to
                listOf(
                    "微黄白色の顆粒剤 (アルミ分包)",
                    "白色の細粒顆粒",
                    "淡黄色の腸溶性顆粒",
                ),
            DosageForm.LIQUID to
                listOf(
                    "茶褐色澄明の経口液剤 (アンバーガラス瓶入り)",
                    "無色澄明の経口液剤",
                    "微黄色澄明のシロップ剤",
                ),
            DosageForm.INJECTION_FORM to
                listOf(
                    "無色澄明の注射液 (バイアル)",
                    "白色の凍結乾燥粉末 (用時溶解、バイアル)",
                    "微黄色澄明のプレフィルドシリンジ注射液",
                ),
            DosageForm.OINTMENT to
                listOf(
                    "白色〜微黄色の軟膏 (チューブ入り)",
                    "淡黄色の親水軟膏",
                    "白色の油脂性軟膏",
                ),
            DosageForm.CREAM to
                listOf(
                    "白色のクリーム剤 (チューブ入り)",
                    "微黄色の親水クリーム",
                    "白色の水中油型クリーム",
                ),
            DosageForm.PATCH to
                listOf(
                    "淡黄色の経皮吸収型貼付剤 (アルミ袋単包)",
                    "肌色の薬用パップ剤",
                    "白色の経皮吸収型マトリックスパッチ",
                ),
            DosageForm.EYE_DROPS to
                listOf(
                    "無色澄明の点眼液 (PE 容器入り)",
                    "微黄色澄明の点眼液",
                    "無色澄明の懸濁性点眼液",
                ),
            DosageForm.SUPPOSITORY to
                listOf(
                    "白色の砲弾型坐剤 (PTP 包装)",
                    "淡黄色の油脂性坐剤",
                    "白色の親水性坐剤",
                ),
            DosageForm.INHALER to
                listOf(
                    "白色の定量噴霧式吸入剤 (アルミ製エアゾール容器)",
                    "白色のドライパウダー吸入剤",
                    "無色透明の吸入用懸濁液",
                ),
            DosageForm.NASAL_SPRAY to
                listOf(
                    "無色澄明の点鼻液 (定量噴霧式 PE 容器)",
                    "微白色懸濁性の点鼻液",
                    "無色澄明の点鼻スプレー (霧状噴霧)",
                ),
        )

    private val originalSubstanceDescriptionVariants: Map<DosageForm, List<String>> =
        mapOf(
            DosageForm.TABLET to
                listOf(
                    "白色の結晶性粉末である。",
                    "白色〜微黄色の結晶性粉末である。",
                    "白色の粉末で、においはない。",
                ),
            DosageForm.CAPSULE to
                listOf(
                    "白色の結晶性粉末である。",
                    "微黄色の結晶又は結晶性粉末である。",
                    "白色の粉末で、においはなく、わずかに苦味がある。",
                ),
            DosageForm.POWDER to
                listOf(
                    "白色の粉末で、においはない。",
                    "白色〜微黄色の細かな結晶性粉末である。",
                    "白色の吸湿性粉末である。",
                ),
            DosageForm.GRANULE to
                listOf(
                    "微黄白色の結晶性粉末である。",
                    "白色の結晶性粉末で、わずかに苦味がある。",
                    "白色〜淡黄色の粉末である。",
                ),
            DosageForm.LIQUID to
                listOf(
                    "茶褐色澄明の液体である。",
                    "無色澄明の液体で、特異な芳香がある。",
                    "白色の結晶性粉末で、水に易溶である。",
                ),
            DosageForm.INJECTION_FORM to
                listOf(
                    "白色の凍結乾燥粉末である。",
                    "無色澄明の液体である。",
                    "白色〜微黄色の結晶性粉末で、水に溶けやすい。",
                ),
            DosageForm.OINTMENT to
                listOf(
                    "白色の結晶性粉末である。",
                    "微黄色〜淡黄色の油状物質である。",
                    "白色のろう様物質である。",
                ),
            DosageForm.CREAM to
                listOf(
                    "白色の結晶性粉末である。",
                    "微黄色の結晶性粉末で、わずかに特異臭がある。",
                    "白色の粉末で、水に難溶である。",
                ),
            DosageForm.PATCH to
                listOf(
                    "白色〜微黄色の結晶性粉末である。",
                    "無色透明の結晶である。",
                    "白色のろう様固体である。",
                ),
            DosageForm.EYE_DROPS to
                listOf(
                    "白色の結晶性粉末で、水に溶けやすい。",
                    "無色澄明の液体である。",
                    "白色〜微黄色の結晶性粉末である。",
                ),
            DosageForm.SUPPOSITORY to
                listOf(
                    "白色の結晶性粉末である。",
                    "淡黄色のろう様物質である。",
                    "白色〜微黄色の結晶又は結晶性粉末である。",
                ),
            DosageForm.INHALER to
                listOf(
                    "白色の微細結晶性粉末である。",
                    "無色澄明の液体で、揮発性がある。",
                    "白色の細粒子状粉末で、吸湿性がある。",
                ),
            DosageForm.NASAL_SPRAY to
                listOf(
                    "白色の結晶性粉末で、水に易溶である。",
                    "無色澄明の液体である。",
                    "白色〜微黄色の結晶である。",
                ),
        )

    fun pickAppearance(
        form: DosageForm,
        drugId: String,
    ): String {
        val variants: List<String> = appearanceVariants.getValue(key = form)
        val seed: Long =
            stableHash(
                id = drugId,
                slot = NameSlot.DRUG_APPEARANCE.ordinal,
                index = 0,
            )
        return variants[normalize(seed = seed, modulus = variants.size)]
    }

    fun pickOriginalSubstanceDescription(
        form: DosageForm,
        drugId: String,
    ): String {
        val variants: List<String> = originalSubstanceDescriptionVariants.getValue(key = form)
        val seed: Long =
            stableHash(
                id = drugId,
                slot = NameSlot.DRUG_ORIGINAL_DESCRIPTION.ordinal,
                index = 0,
            )
        return variants[normalize(seed = seed, modulus = variants.size)]
    }

    private fun normalize(
        seed: Long,
        modulus: Int,
    ): Int {
        val raw: Int = (seed % modulus).toInt()
        if (raw < 0) {
            return raw + modulus
        }
        return raw
    }
}
