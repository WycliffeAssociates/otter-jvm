package widgets

import io.reactivex.subjects.PublishSubject

/**
 * This class is used by the selector widget as the go-between
 * to where the data on the selected items is stored
 */

class ComboBoxSelectorViewModel(
        private val updateSelectedLanguages: PublishSubject<ComboBoxSelectionItem>,
        private val preferredLanguage: PublishSubject<ComboBoxSelectionItem>
) {

    private val model = ComboBoxSelectorModel()

    fun addNewValue(selection: ComboBoxSelectionItem) {
        if (!model.selectedData.contains(selection)) {
            model.selectedData.add(0, selection)
            updateSelectedLanguages.onNext(selection)
            setPreferredSelection(selection)
        }
    }

    fun newPreferredSelection(chip: Chip) {
        setPreferredSelection(model.selectedData.first { it.labelText == chip.labelText })
    }

    fun removeSelection(chip: Chip) {
        val selection = model.selectedData.first { it.labelText == chip.labelText }
        model.selectedData.remove(selection)
        updateSelectedLanguages.onNext(selection)
        if (model.selectedData.isNotEmpty()) {
            if (selection == model.preferredSelection) {
                setPreferredSelection(model.selectedData.first())
            } else {
                setPreferredSelection(model.preferredSelection)
            }
        }
    }

    private fun setPreferredSelection(selection: ComboBoxSelectionItem?) {
        // we still want to notify the profile that there is no selected preferred language somehow?
        model.preferredSelection = selection
        if (selection != null) {
            preferredLanguage.onNext(selection)
        }
    }

}