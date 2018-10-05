package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.widgets.Take
import tornadofx.*
import java.io.File
import java.util.*

class ViewTakesModel() {

    val takes = listOf(
            Take(
                    1,
                    Calendar.Builder().setDate(2018, 8, 8).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    true
            ),
            Take(
                    2,
                    Calendar.Builder().setDate(2018, 9, 9).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    false
            ),
            Take(
                    3,
                    Calendar.Builder().setDate(2018, 9, 18).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    true
            ),
            Take(
                    4,
                    Calendar.Builder().setDate(2018, 10, 12).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    false
            ),
            Take(
                    12,
                    Calendar.Builder().setDate(2018, 10, 13).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    true
            ),
            Take(
                    21,
                    Calendar.Builder().setDate(2018, 10, 14).build().time,
                    File("/Users/nathanshanko/Documents/test1.wav"),
                    false
            )

    )

    var alteranteTakes: ObservableList<Take> by property(
            FXCollections.observableList(takes.toMutableList()))
    val alternateTakesProperty = getProperty(ViewTakesModel::alteranteTakes)

    var selectedTake: Node by property()
    var selectedTakeProperty = getProperty(ViewTakesModel::selectedTake)

    var takeToCompare: Node by property()
    var takeToCompareProperty = getProperty(ViewTakesModel::takeToCompare)

    var comparingTake: Boolean by property(false)
    var comparingTakeProperty = getProperty(ViewTakesModel::comparingTake)

    var draggingTake: Boolean by property(false)
    var draggingTakeProperty = getProperty(ViewTakesModel::draggingTake)

    var draggingShadow: Node by property (Rectangle())
    var draggingShadowProperty = getProperty(ViewTakesModel::draggingShadow)


}

