package org.wycliffeassociates.otter.jvm.app.ui.inject

object Injector {
    val projectDao = DaggerPersistenceComponent
            .builder()
            .build()
            .injectDatabase()
            .getProjectDao()
}