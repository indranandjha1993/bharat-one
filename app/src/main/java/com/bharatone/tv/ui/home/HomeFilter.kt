package com.bharatone.tv.ui.home

import com.bharatone.tv.data.Channel
import com.bharatone.tv.ui.theme.languageGlyph

sealed interface Filter {
    data object All : Filter
    data class Genre(val name: String) : Filter
    data class Language(val name: String) : Filter
}

sealed interface FilterItem {
    data class Heading(val text: String) : FilterItem
    data class Chip(val label: String, val filter: Filter, val glyph: String?) : FilterItem
}

private val LANGUAGE_ORDER = listOf("Hindi", "English", "Urdu")

fun applyFilter(channels: List<Channel>, filter: Filter): List<Channel> = when (filter) {
    Filter.All -> channels
    is Filter.Genre -> channels.filter { it.category == filter.name }
    is Filter.Language -> channels.filter { it.language == filter.name }
}

/** Builds the filter rail: All, then a Genre group, then a Language group (each language shown with its script). */
fun buildFilterItems(channels: List<Channel>, genreOrder: List<String>): List<FilterItem> {
    val genres = genreOrder.filter { genre -> channels.any { it.category == genre } }
    val languages = channels.map { it.language }.distinct().sortedWith(
        compareBy(
            { LANGUAGE_ORDER.indexOf(it).let { i -> if (i < 0) Int.MAX_VALUE else i } },
            { it },
        ),
    )
    return buildList {
        add(FilterItem.Chip("All", Filter.All, null))
        add(FilterItem.Heading("GENRE"))
        genres.forEach { add(FilterItem.Chip(it, Filter.Genre(it), null)) }
        add(FilterItem.Heading("LANGUAGE"))
        languages.forEach { add(FilterItem.Chip(it, Filter.Language(it), languageGlyph(it))) }
    }
}
