package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

sealed interface BucketContextKey {
    data class DrugContext(val atcInitial: Char) : BucketContextKey {
        init {
            require(atcInitial.isLetter()) {
                "atcInitial must be a letter, got '$atcInitial'"
            }
        }
    }

    data class DiseaseContext(val chapter: Icd10Chapter) : BucketContextKey

    data object Global : BucketContextKey

    companion object {
        fun from(drug: Drug): BucketContextKey =
            DrugContext(atcInitial = drug.atcCode.first())

        fun from(disease: Disease): BucketContextKey =
            DiseaseContext(chapter = disease.icd10Chapter)
    }
}
