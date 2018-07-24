package widgets

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.paint.Color
import recources.UIColors
import tornadofx.*
import java.util.*

/**
 * Below main and app are used to demo
 */

fun main(args: Array<String>) {
    println("hello")

    launch<LanguagesDemoApp>(args)
}

class LanguagesDemoApp : App(MainView::class, LanguageSelectionStyle::class)

/**
 * Above main and app are used to demo
 */


class MainView : View() {

    private val newTarget = SimpleStringProperty()
    private val newSource = SimpleStringProperty()

    private val selectedTargets = mutableListOf<String>().observable()
    private val selectedSources = mutableListOf<String>().observable()


    private val languages = listOf("English", "Spanish", "Gaul", "Czechoslovakian", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew", "English", "Spanish", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew", "English", "Spanish", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew", "English", "Spanish", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew", "English", "Spanish", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew", "English", "Spanish", "French", "Russian", "Engrish", "Sppanish", "Arabic", "MandArin", "Afrikaans", "Hebrew")

    private val hint = "Try English"

    init { // pulls correct language
        messages = ResourceBundle.getBundle("MyView")
    }

    override val root = hbox {

        setPrefSize(800.0, 400.0)
        alignment = Pos.CENTER

        // TODO: Add 'target language' and 'source language' to the resource bundle language files

        // Target Language ComboBox
        add(LanguageSelection(
                FXCollections.observableList(languages),
                newTarget,
                messages["targetLanguages"],
                hint,
                Color.valueOf(UIColors.UI_PRIMARY),
                LanguageSelectionStyle.targetLanguageSelector,
                LanguageSelectionStyle.makeItHoverPRIMARY,
                selectedTargets
        ))

        // Source Language ComboBox
        add(LanguageSelection(
                FXCollections.observableList(languages),
                newSource,
                messages["sourceLanguages"],
                hint,
                Color.valueOf(UIColors.UI_SECINDARY),
                LanguageSelectionStyle.sourceLanguageSelector,
                LanguageSelectionStyle.makeItHoverSECONDARY,
                selectedSources
        ))

    }

}