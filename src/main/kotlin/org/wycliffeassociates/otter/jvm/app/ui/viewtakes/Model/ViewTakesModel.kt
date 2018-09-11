package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.io.File
import java.util.*

 class ViewTakesModel() {

    var selectedTake : Take by property(Take(4,"07/12/18",File("Desktop/3afcc3a2.mp3")))
    val selectedTakeProperty = getProperty(ViewTakesModel::selectedTake)
    var alteranteTakes: ObservableList<Take> by property(
            FXCollections.observableList(listOf(Take(1,"02/22/13",File("Desktop/3afcc3a2.mp3")),
                    Take(2,"02/22/18",File("Desktop/3afcc3a2.mp3")),
                    Take(3,"05/22/18",File("Desktop/3afcc3a2.mp3"))).toMutableList()))
     val alternateTakesProperty  =getProperty(ViewTakesModel::alteranteTakes)
//    val Verse
}

//mock takes
data class Take (
        val take_num : Int,
        val date_modified : String,
        val audio_path : File
        )
