package app.ui

import data.model.Language
import io.reactivex.subjects.PublishSubject
import javafx.embed.swing.JFXPanel
import org.junit.Test
import widgets.Chip

class TestLanguageSelectorViewModel {
    private val languages = listOf(
            Language(0, "ENG", "English", true, "0"),
            Language(0, "MAN", "Mandarin", true, "1"),
            Language(0, "ESP", "Spanish", true, "2"),
            Language(0, "ARA", "Arabic", true, "3"),
            Language(0, "RUS", "Russian", true, "4"),
            Language(0, "AAR", "Afrikaans", true, "5"),
            Language(0, "HEB", "Hebrew", true, "6"),
            Language(0, "JAP", "Japanese", true, "7")
    )
    private val updateSelected = PublishSubject.create<Language>()
    private val updatePreferred = PublishSubject.create<Language>()

    // Check to see if the newly added language is passed correctly through the observable
    @Test
    fun addNewChipTest() {
        val languageSelectorViewModel = LanguageSelectorViewModel(updateSelected, updatePreferred, languages)

        var actual : Language? = null
        updateSelected.subscribe { actual = it }
        val expected = languages[0]

        languageSelectorViewModel.addNewValue(LanguageSelectionItem(expected))

        assert(actual == expected)
    }

    // Check and see if the new preferred language is correctly passed through the observable
    @Test
    fun newPreferredLanguageTest() {
        val languageSelectorViewModel = LanguageSelectorViewModel(updateSelected, updatePreferred, languages)
        languages.map { languageSelectorViewModel.addNewValue(LanguageSelectionItem(it)) }

        var actual : Language? = null
        updatePreferred.subscribe { actual = it }
        val expected = languages[0]
        JFXPanel() // this allows TornadoFX (JavaFX) items to be made without being inside an application
        val chip = Chip(
                expected.slug,
                expected.name,
                languageSelectorViewModel::removeLanguage,
                languageSelectorViewModel::removeLanguage
        )
        languageSelectorViewModel.newPreferredLanguage(chip)

        assert(actual == expected)
    }

    // Check and see if the language to be removed is passed correctly through the observable
    @Test
    fun removeLanguageTest() {
        val languageSelectorViewModel = LanguageSelectorViewModel(updateSelected, updatePreferred, languages)
        languages.map { languageSelectorViewModel.addNewValue(LanguageSelectionItem(it)) }

        var actual : Language? = null
        updateSelected.subscribe { actual = it }
        val expected = languages[0]
        JFXPanel() // this allows TornadoFX (JavaFX) items to be made without being inside an application
        val chip = Chip(
                expected.slug,
                expected.name,
                languageSelectorViewModel::removeLanguage,
                languageSelectorViewModel::removeLanguage
        )
        languageSelectorViewModel.removeLanguage(chip)

        assert(actual == expected)
    }
}
