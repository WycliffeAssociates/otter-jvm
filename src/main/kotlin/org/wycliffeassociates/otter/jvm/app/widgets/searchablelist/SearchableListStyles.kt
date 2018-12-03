package org.wycliffeassociates.otter.jvm.app.widgets.searchablelist

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class SearchableListStyles : Stylesheet() {
    companion object {
        val searchableList by cssclass("wa-searchable-list")
        val searchFieldContainer by cssclass("wa-search-field-container")
        val searchField by cssclass("wa-search-field")
        val searchListView by cssclass("wa-search-list-view")
        val icon by cssclass("wa-searchable-list-icon")
        val separator by cssclass()

        fun searchIcon(size: String = "1em") = MaterialIconView(MaterialIcon.SEARCH, size)
    }

    init {

        separator {
            padding = box(15.px)
            backgroundColor += Color.TRANSPARENT
        }

        searchableList {
            searchFieldContainer {
                alignment = Pos.CENTER_LEFT
            }
        }
    }
}