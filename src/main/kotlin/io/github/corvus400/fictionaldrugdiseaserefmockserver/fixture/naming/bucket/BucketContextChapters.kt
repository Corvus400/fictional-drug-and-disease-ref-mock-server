package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.AtcIcd10Mapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory

object BucketContextChapters {
    fun pickChapter(
        context: BucketContextKey,
        seed: Long,
    ): Icd10Chapter? =
        when (context) {
            is BucketContextKey.DiseaseContext -> context.chapter
            is BucketContextKey.DrugContext -> {
                val category =
                    TherapeuticCategory.fromAtcInitial(initial = context.atcInitial)
                        ?: error("Unknown ATC initial '${context.atcInitial}'")
                ValueRangeGenerator.pickOne(seed = seed, candidates = AtcIcd10Mapping.chaptersFor(category))
            }
            BucketContextKey.Global -> null
        }
}
