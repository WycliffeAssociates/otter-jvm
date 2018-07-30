package widgets

import app.ui.imageLoader
import data.model.*
import io.reactivex.subjects.PublishSubject
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import java.io.File
import java.io.FileInputStream
import java.util.*


/**
 * Bellow main and app are used to demo
 */

fun main(args: Array<String>) {
    launch<LanguagesDemoApp>(args)
}

class LanguagesDemoApp : App(MainView::class, SelectorStyle::class)

/**
 * Above main and app are used to demo
 */

class MainView : View() {


    private val updateSelectedTargets = PublishSubject.create<DataSelectionInterface>()
    private val updateSelectedSources = PublishSubject.create<DataSelectionInterface>()
    private val preferredTarget = PublishSubject.create<DataSelectionInterface>()
    private val preferredSource = PublishSubject.create<DataSelectionInterface>()


    private val languageVals = listOf(Language(0, "ENG", "English", true, "0"),Language(0, "MAN", "Mandarin", true, "1"),Language(0, "ESP", "Spanish", true, "2"),Language(0, "ARA", "Arabic", true, "3"), Language(0, "RUS", "Russian", true, "4"), Language(0, "AAR", "Afrikaans", true, "5"), Language(0, "HEB", "Hebrew", true, "6"), Language(0, "JAP", "Japanese", true, "7"))
    private val hint = "Try English or ENG"

    private val selections = languageVals.map { LanguageSelection(it) }

    init {
        messages = ResourceBundle.getBundle("MyView")
    }

    override val root = borderpane {

        vgrow = Priority.NEVER
        setPrefSize(800.0, 400.0)

        left {
            // Target Language ComboBox
            add(Selector(
                    selections,
                    "Target Languages",
                    hint,
                    c(messages["UI_PRIMARY"]),
                    updateSelectedTargets,
                    preferredTarget
            ))

            this.left.addClass(SelectorStyle.selector)
            this.left.addClass(SelectorStyle.chip)
        }


        right {
            // Source Language ComboBox
            add(Selector(
                    selections,
                    "Source Languages",
                    hint,
                    c(messages["UI_SECONDARY"]),
                    updateSelectedSources,
                    preferredSource
            ))

            this.right.addClass(SelectorStyle.selector)
            this.right.addClass(SelectorStyle.chip)
        }

    }

}