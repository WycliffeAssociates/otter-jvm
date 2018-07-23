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
            setPreferedLanguage(language)
        }
    }

    fun setPreferedLanguage(language : Language?) {
        // TODO : we still want to notify the profile that there is no selected preferred language somehow?
        if (language != null) {
            model.preferredLanguage = language
            preferredLanguage.onNext(language)
        }
    }

    fun removeLanguage(language : Language) {
        model.selectedLanguages.remove(language)
        selectedLanguages.onNext(model.selectedLanguages)
        if (model.selectedLanguages.isNotEmpty()) {
            if (language == model.preferredLanguage) {
                setPreferedLanguage(model.selectedLanguages[0])
            } else {
                setPreferedLanguage(model.preferredLanguage)
            }
        }

    }

}