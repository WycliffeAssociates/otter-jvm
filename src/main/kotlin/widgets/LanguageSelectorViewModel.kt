package widgets

import data.Language
import io.reactivex.subjects.PublishSubject

class LanguageSelectorViewModel(
        private val selectedLanguages : PublishSubject<List<Language>>,
        private val preferredLanguage : PublishSubject<Language>
) {

    private val model = LanguageSelectorModel()

    fun addNewLanguage(language : Language) {
        if (!model.selectedLanguages.contains(language)) {
            model.selectedLanguages.add(0, language)
            selectedLanguages.onNext(model.selectedLanguages)
            setPreferredLanguage(language)
        }
    }

    fun newPreferredLanguage(chip : Chip) {
        setPreferredLanguage(model.selectedLanguages.filter { it.toTextView() == chip.labelText }[0])

    }

    fun removeLanguage(chip : Chip) {
        val language = model.selectedLanguages.filter { it.toTextView() == chip.labelText }[0]
        model.selectedLanguages.remove(language)
        selectedLanguages.onNext(model.selectedLanguages)
        if (model.selectedLanguages.isNotEmpty()) {
            if (language == model.preferredLanguage) {
                setPreferredLanguage(model.selectedLanguages[0])
            } else {
                setPreferredLanguage(model.preferredLanguage)
            }
        }

    }


    private fun setPreferredLanguage(language : Language?) {
        // TODO : we still want to notify the profile that there is no selected preferred language somehow?
        model.preferredLanguage = language
        if (language != null) {
            preferredLanguage.onNext(language)
        }

    }

}