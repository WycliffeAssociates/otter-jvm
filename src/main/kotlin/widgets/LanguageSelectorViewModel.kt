package widgets

import data.Language
import io.reactivex.subjects.PublishSubject

class LanguageSelectorViewModel(
        private val updateSelectedLanguages : PublishSubject<Language>,
        private val preferredLanguage : PublishSubject<Language>
) {

    private val model = LanguageSelectorModel()

    fun addNewLanguage(language : Language) {
        if (!model.selectedLanguages.contains(language)) {
            model.selectedLanguages.add(0, language)
            updateSelectedLanguages.onNext(language)
            setPreferredLanguage(language)
        }
    }

    fun newPreferredLanguage(chip : Chip) {
        setPreferredLanguage(model.selectedLanguages.filter { it.toTextView() == chip.labelText }[0])

    }

    fun removeLanguage(chip : Chip) {
        val language = model.selectedLanguages.filter { it.toTextView() == chip.labelText }[0]
        model.selectedLanguages.remove(language)
        updateSelectedLanguages.onNext(language)
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