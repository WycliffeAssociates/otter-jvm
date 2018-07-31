package widgets

import app.ui.LanguageSelectionItem
import data.model.Language
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class TestComboBoxSelectorViewModel {
    val language = Language(0, "ENG", "English", true, "0")
    val languageTwo = Language(0, "ESP", "Spanish", true, "1")
    val CBSelectionItemOne = LanguageSelectionItem(language)
    val CBSelectionItemTwo = LanguageSelectionItem(languageTwo)

    val updateSelected = PublishSubject.create<ComboBoxSelectionItem>()
    val preferredLanguage = PublishSubject.create<ComboBoxSelectionItem>()

    val viewModel = ComboBoxSelectorViewModel(updateSelected, preferredLanguage)
    // val model = ComboBoxSelectorModel()

    // Tests whether a new language can be added, as well as whether the selected language is updated.
    @Test
    fun canAddNewValue() {
        var actual : ComboBoxSelectionItem? = null
        var expected : ComboBoxSelectionItem = CBSelectionItemOne
        updateSelected.subscribe { actual = it }
        viewModel.addNewValue(CBSelectionItemOne)
        assert(actual == expected)

        // checks whether updateSelected gets updated when a new language is added
        var expectedTwo : ComboBoxSelectionItem = CBSelectionItemTwo
        viewModel.addNewValue(CBSelectionItemTwo)
        assert(actual == expectedTwo)

        // checks whether it will add a language that's already been added
        viewModel.addNewValue(CBSelectionItemOne)
        assert(actual != expected) // it must not be the 'new' value
        assert(actual == expectedTwo) // it must still be the old value
    }
}