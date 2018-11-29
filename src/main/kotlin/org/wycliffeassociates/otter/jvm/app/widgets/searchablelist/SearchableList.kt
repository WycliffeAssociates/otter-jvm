package org.wycliffeassociates.otter.jvm.app.widgets.searchablelist

import com.jfoenix.controls.JFXTextField
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class SearchableList<T>(listItems: ObservableList<T>, outputValue: Property<T>, autoSelect: Boolean = false) : VBox() {
    var autoSelect: Boolean by property(autoSelect)
    fun autoSelectProperty() = getProperty(SearchableList<T>::autoSelect)
    private var itemFilter: (String) -> ObservableList<T> = { listItems }

    var value: T by property()
    fun valueProperty() = getProperty(SearchableList<T>::value)

    var searchField: JFXTextField by singleAssign()
    var listView: ListView<T> by singleAssign()

    init {
        importStylesheet<SearchableListStyles>()
        addClass(SearchableListStyles.searchableList)
        outputValue.bindBidirectional(valueProperty())
        hbox {
            addClass(SearchableListStyles.searchFieldContainer)
            add(SearchableListStyles.searchIcon().apply { addClass(SearchableListStyles.icon) })
            searchField = JFXTextField()
            searchField.addClass(SearchableListStyles.searchField)
            searchField.hgrow = Priority.ALWAYS
            add(searchField)
        }
        listView = listview(listItems) {
            addClass(SearchableListStyles.searchListView)
            multiSelect(false)
            val nullSafeWriteableProperty = object: SimpleObjectProperty<T?>() {
                override fun set(value: T?) {
                    if (value != null) {
                        selectionModel.select(value)
                        super.set(value)
                    } else {
                        searchField.clear()
                        selectionModel.clearSelection()
                    }
                }
            }
            nullSafeWriteableProperty.bind(selectionModel.selectedItemProperty())
            valueProperty().bindBidirectional(nullSafeWriteableProperty)
            searchField.textProperty().onChange { _ ->
                val query = searchField.text
                items = itemFilter(query)
                if (autoSelectProperty().value && items.isNotEmpty()) selectionModel.selectFirst()
            }
        }
    }

    fun filter(newFilter: (String) -> ObservableList<T>) {
        itemFilter = newFilter
    }
}

fun <T> EventTarget.searchablelist(listItems: ObservableList<T>, value: Property<T>, init: SearchableList<T>.() -> Unit)
        = SearchableList(listItems, value).attachTo(this, init)