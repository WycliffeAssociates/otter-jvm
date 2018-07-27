package widgets

/**
 * This interface is used in Selector as the data from which is selected in the widget
 */

interface ComboBoxSelectionItem {
    val labelText : String
    val filterText : List<String>
}