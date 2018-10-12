package org.wycliffeassociates.otter.jvm.app

import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.ImportLanguages
import org.wycliffeassociates.otter.common.domain.PluginActions
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.menu.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.ProjectHomeView
import tornadofx.*
import java.io.File
import java.time.LocalDate

class MyApp : App(Workspace::class) {
    init {
        importStylesheet(ProjectPageStylesheet::class)
        importStylesheet(ViewTakesStylesheet::class)
        importStylesheet(MainMenuStylesheet::class)
        workspace.header.removeFromParent()
        workspace.add(MainMenu())
    }
    override fun onBeforeShow(view:UIComponent) {
        workspace.dock<ProjectHomeView>()
    }
}
//launch the org.wycliffeassociates.otter.jvm.app
fun main(args: Array<String>) {
    initApp()

    launch<MyApp>(args)
}

private fun initApp() {
    ImportLanguages(
            File(ClassLoader.getSystemResource("langnames.json").toURI()),
            Injector.languageRepo
    ).import().subscribe()
}
