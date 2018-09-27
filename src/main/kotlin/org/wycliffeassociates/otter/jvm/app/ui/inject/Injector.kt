package org.wycliffeassociates.otter.jvm.app.ui.inject

<<<<<<< HEAD
import org.wycliffeassociates.otter.jvm.persistence.injection.DaggerPersistenceComponent


=======
>>>>>>> package name changes, copypaste from ui branch
object Injector {
    private val database = DaggerPersistenceComponent
            .builder()
            .build()
            .injectDatabase()
    val projectDao = database.getProjectDao()
    val chapterDao =database.getChapterDao()
<<<<<<< HEAD

=======
>>>>>>> package name changes, copypaste from ui branch
    val bookDao = database.getBookDao()

    val chunkDao = database.getChunkDao()

    val takesDao = database.getTakesDao()
<<<<<<< HEAD
}

=======
}
>>>>>>> package name changes, copypaste from ui branch
