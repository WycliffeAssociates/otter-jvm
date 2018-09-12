package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.Take
import tornadofx.*
import java.io.File
import java.util.*

class ViewTakesModel() {

    val takes = listOf(
            Take(
                    1,
                    Calendar.Builder().setDate(2018, 8, 8).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    true
            ),
            Take(
                    2,
                    Calendar.Builder().setDate(2018, 9, 9).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    false
            ),
            Take(
                    3,
                    Calendar.Builder().setDate(2018, 9, 18).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    true
            ),
            Take(
                    4,
                    Calendar.Builder().setDate(2018, 10, 12).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    false
            ),
            Take(
                    12,
                    Calendar.Builder().setDate(2018, 10, 13).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    true
            ),
            Take(
                    21,
                    Calendar.Builder().setDate(2018, 10, 14).build().time,
                    File("/Users/nathanshanko/Desktop/test1.wav"),
                    false
            )

    )
    var selectedTake: Take by property(Take(
            5,
            Calendar.Builder().setDate(2018, 8, 8).build().time,
            File("/Users/nathanshanko/Desktop/test1.wav"),
            true
    ))
    val selectedTakeProperty = getProperty(ViewTakesModel::selectedTake)
    var alteranteTakes: ObservableList<Take> by property(
            FXCollections.observableList(takes.toMutableList()))
    val alternateTakesProperty = getProperty(ViewTakesModel::alteranteTakes)
//    val Verse

}

