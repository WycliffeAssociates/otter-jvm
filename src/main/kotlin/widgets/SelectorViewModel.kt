package widgets

import io.reactivex.subjects.PublishSubject

/**
 * This class is used by the selector widget as the go-between
 * to where the data on the selected items is stored
 */

class SelectorViewModel(
        private val updateSelectedLanguages : PublishSubject<DataSelectionInterface>,
        private val preferredLanguage : PublishSubject<DataSelectionInterface>
) {

    private val model = SelectorModel()

    fun addNewValue(selection : DataSelectionInterface) {
        if (!model.selectedData.contains(selection)) {
            model.selectedData.add(0, selection)
            updateSelectedLanguages.onNext(selection)
            setPreferredSelection(selection)
        }
    }

    fun newPreferredSelection(chip : Chip) {
        setPreferredSelection(model.selectedData.first { it.labelText == chip.labelText })

    }

    fun removeSelection(chip : Chip) {
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


    private fun setPreferredSelection(selection : DataSelectionInterface?) {
        // we still want to notify the profile that there is no selected preferred language somehow?
        model.preferredSelection = selection
        if (selection != null) {
            preferredLanguage.onNext(selection)
        }

    }

}