package app.ui

import data.model.Language
import widgets.ComboBoxSelectionItem
import org.junit.Test

class TestLanguageSelectionItem {

    val language = Language(0, "ENG", "English", true, "0")
    val languageTwo = Language(0, "ESP", "Spanish", true, "1")

    // Tests whether or not the LanguageSelectionItem class returns a ComboBoxSelectionItem.
    @Test
    fun isComboBoxSelectionItem() {
        assert(LanguageSelectionItem(language) is ComboBoxSelectionItem)
    }
    // Tests whether the correct string is output from the Language selection.
    @Test
    fun isLabelTextCorrect() {
        val expected = "ENG (English)"
        val actual = LanguageSelectionItem(language).labelText
        assert(expected == actual)

        val expectedTwo = "Nananananananananananananananana BATMAN"
        val actualTwo = LanguageSelectionItem(languageTwo).labelText
        assert(expectedTwo != actualTwo)
    }
    // Tests whether filterText is a list.
    @Test
    fun isFilterTextAList() {
        assert(LanguageSelectionItem(language).filterText is List)
    }
    // Tests whether language list exists and whether it contains the correct values.
    @Test
    fun isLanguageListCreated() {
        assert(LanguageSelectionItem(language).filterText[0] == "English")
        assert(LanguageSelectionItem(languageTwo).filterText[1] == "ESP")
        assert(LanguageSelectionItem(language).filterText[2] == "0")
    }
}