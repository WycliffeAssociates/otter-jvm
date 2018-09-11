package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model

import tornadofx.*
import java.io.File
import java.util.*

 class ViewTakesModel(selectedTake: Take? = null, alternateTakes: List<Take>? = null) {

    var selectedTake : Take by property(selectedTake)
    val selectedTakeProperty = getProperty(ViewTakesModel::selectedTake)
    var alteranteTakes: List<Take> by property(alternateTakes)
     val alternateTakesProperty  =getProperty(ViewTakesModel::alteranteTakes)
//             listOf(Take(1,"02/22/13",File("Desktop/3afcc3a2.mp3")),
//                     Take(2,"02/22/18",File("Desktop/3afcc3a2.mp3")),
//                     Take(3,"05/22/18",File("Desktop/3afcc3a2.mp3")))
//    val Verse
}

//mock takes
data class Take (
        val take_num : Int,
        val date_modified : String,
        val audio_path : File
        )
