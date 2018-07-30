package app.ui

import data.model.Language
import io.reactivex.subjects.PublishSubject
import widgets.Chip
import widgets.ComboBoxSelectionItem

/**
 * This class is used by the selector widget as the go-between
 * to where the data on the selected items is stored
 */

class LanguageSelectorViewModel(
        private val updateSelectedLanguages: PublishSubject<Language>,
        private val preferredLanguage: PublishSubject<Language>,
        languages: List<Language>
) {

    private val model = LanguageSelectorModel(languages)

    fun addNewValue(selection: ComboBoxSelectionItem) {
        val language = model.itemToLanguage(selection)
        if (language != null && !model.selectedData.contains(language)) {
            model.selectedData.add(0, language)
            updateSelectedLanguages.onNext(language)
            setPreferredLanguage(language)
        }
    }

    fun newPreferredLanguage(chip: Chip) {
        setPreferredLanguage(model.selectedData.first { it.toTextView() == chip.labelText })
    }

    fun removeLanguage(chip: Chip) {
        val language = model.selectedData.first { it.toTextView() == chip.labelText }
        model.selectedData.remove(language)
        updateSelectedLanguages.onNext(language)
        if (model.selectedData.isNotEmpty()) {
            if (language == model.preferredSelection) {
                setPreferredLanguage(model.selectedData.first())
            } else {
                setPreferredLanguage(model.preferredSelection)
            }
        }
    }

    private fun setPreferredLanguage(language: Language?) {
        // we still want to notify the profile that there is no selected preferred language somehow?
        model.preferredSelection = language
        if (language != null) {
            preferredLanguage.onNext(language)
        }
    }

}