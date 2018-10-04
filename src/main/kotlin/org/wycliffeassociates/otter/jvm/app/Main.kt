package org.wycliffeassociates.otter.jvm.app

import org.wycliffeassociates.otter.common.domain.ImportLanguages
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.menu.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.ProjectCreationWizard
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.view.ProjectPage
import org.wycliffeassociates.otter.jvm.persistence.repositories.AudioPluginRepository
import tornadofx.*
import java.io.File

class MyApp : App(Workspace::class) {
    init {
        workspace.header.removeFromParent()
        workspace.add(MainMenu())
    }
    override fun onBeforeShow(view:UIComponent) {
        workspace.dock<ProjectPage>()

    }
}
//launch the org.wycliffeassociates.otter.jvm.app
fun main(args: Array<String>) {
    ImportLanguages(
            File(ClassLoader.getSystemResource("langnames.json").toURI()),
            Injector.languageRepo
    )
            .import()
            .onErrorComplete()
            .subscribe()
    Injector
            .pluginRepository
            .getAll()
            .subscribe { plugins ->
                DefaultPluginPreference.defaultPluginData = plugins.firstOrNull()
            }
    launch<MyApp>(args)
}