package org.wycliffeassociates.otter.jvm.app.widgets.workbookheader

import com.jfoenix.controls.JFXCheckBox
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.statusindicator.control.statusindicator
import tornadofx.*
import java.util.function.Predicate

data class FilterOption<T>(val text: String, val predicate: Predicate<T>)

class WorkbookHeader<T> : VBox() {

    val labelTextProperty = SimpleStringProperty()
    var labelText by labelTextProperty

    val filterOptionProperty = SimpleObjectProperty<FilterOption<T>?>()
    var filterOption by filterOptionProperty

    val progressBarTrackFillProperty = SimpleObjectProperty<Color>(Color.ORANGE)
    var progressBarTrackFill by progressBarTrackFillProperty

    val workbookProgressProperty = SimpleDoubleProperty(0.5)
    var workbookProgress by workbookProgressProperty

    private val filterOptionTextProperty = SimpleStringProperty()

    init {
        importStylesheet<WorkbookHeaderStyles>()

        filterOptionProperty.onChange {
            filterOptionTextProperty.set(it?.text)
        }

        addClass(WorkbookHeaderStyles.workbookHeader)
        spacing = 10.0
        hbox {
            label(labelTextProperty) {
                managedProperty().bind(!labelTextProperty.isEmpty)
            }
            region {
                hgrow = Priority.ALWAYS
            }
            add(
                JFXCheckBox().apply {
                    isDisableVisualFocus = true
                    textProperty().bind(filterOptionTextProperty)
                    managedProperty().bind(filterOptionProperty.isNotNull)
                    visibleProperty().bind(filterOptionProperty.isNotNull)
                    action {
                        if (isSelected) {
                            // TODO: Tell view model to filter list
                        } else {
                            // TODO: Tell view model to remove filter from list
                        }
                    }
                }
            )
        }
        add(
            statusindicator {
                hgrow = Priority.ALWAYS
                primaryFillProperty().bind(progressBarTrackFillProperty)
                accentFillProperty.bind(progressBarTrackFillProperty)
                trackFill = Color.LIGHTGRAY
                prefHeight = 15.0
                barHeight = 18.0
                indicatorRadius = 10.0
                progressProperty().bind(workbookProgressProperty)
            }
        )
    }
}

fun <T> workbookheader(init: WorkbookHeader<T>.() -> Unit = {}): WorkbookHeader<T> {
    val wh = WorkbookHeader<T>()
    wh.init()
    return wh
}
