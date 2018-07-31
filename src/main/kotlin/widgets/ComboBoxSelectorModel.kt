package widgets

/**
 * This class holds a list of selected items and tracks which one is the preferred item.
 *
 * @author Caleb Benedick
 */
class ComboBoxSelectorModel {
    var preferredSelection : ComboBoxSelectionItem? = null
    val selectedData : MutableList<ComboBoxSelectionItem> = mutableListOf()
}