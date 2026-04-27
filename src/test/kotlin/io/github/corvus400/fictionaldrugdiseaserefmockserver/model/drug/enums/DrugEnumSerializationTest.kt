package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugEnumSerializationTest {
    @Test
    fun `RegulatoryClass PRESCRIPTION_REQUIRED serializes to JSON string prescription_required`() {
        val json = AppJson.encodeToString(RegulatoryClass.PRESCRIPTION_REQUIRED)
        assertEquals("\"prescription_required\"", json)
    }

    @Test
    fun `DosageForm TABLET serializes to JSON string 錠剤`() {
        val json = AppJson.encodeToString(DosageForm.TABLET)
        assertEquals("\"錠剤\"", json)
    }

    @Test
    fun `RouteOfAdministration ORAL serializes to JSON string 内服`() {
        val json = AppJson.encodeToString(RouteOfAdministration.ORAL)
        assertEquals("\"内服\"", json)
    }

    @Test
    fun `PrecautionPopulationCategory PREGNANT serializes to JSON string 妊婦`() {
        val json = AppJson.encodeToString(PrecautionPopulationCategory.PREGNANT)
        assertEquals("\"妊婦\"", json)
    }

    @Test
    fun `HepaticSeverity MODERATE serializes to JSON string 中等度`() {
        val json = AppJson.encodeToString(HepaticSeverity.MODERATE)
        assertEquals("\"中等度\"", json)
    }

    @Test
    fun `RenalSeverity END_STAGE serializes to JSON string 末期`() {
        val json = AppJson.encodeToString(RenalSeverity.END_STAGE)
        assertEquals("\"末期\"", json)
    }

    @Test
    fun `FrequencyBand OVER_5_PERCENT serializes to JSON string five-percent-or-more`() {
        val json = AppJson.encodeToString(FrequencyBand.OVER_5_PERCENT)
        assertEquals("\"5%以上\"", json)
    }

    @Test
    fun `DoseUnit MG serializes to JSON string mg`() {
        val json = AppJson.encodeToString(DoseUnit.MG)
        assertEquals("\"mg\"", json)
    }

    @Test
    fun `StorageTemperature COLD serializes to JSON string 冷所`() {
        val json = AppJson.encodeToString(StorageTemperature.COLD)
        assertEquals("\"冷所\"", json)
    }
}
