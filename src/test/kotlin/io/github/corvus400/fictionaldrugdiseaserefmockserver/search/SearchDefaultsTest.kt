package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals

class SearchDefaultsTest {
    @Test
    fun `SearchDefaults_DEFAULT_PAGE_SIZE equals 20`() {
        assertEquals(20, SearchDefaults.DEFAULT_PAGE_SIZE)
    }

    @Test
    fun `SearchDefaults_MAX_PAGE_SIZE equals 100`() {
        assertEquals(100, SearchDefaults.MAX_PAGE_SIZE)
    }
}
