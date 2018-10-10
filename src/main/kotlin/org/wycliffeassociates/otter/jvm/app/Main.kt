package org.wycliffeassociates.otter.jvm.app

import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.domain.ImportLanguages
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.menu.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.ProjectCreationWizard
import tornadofx.*
import java.io.File

class MyApp : App(Workspace::class) {
    init {
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

    //Initialize bible and testament collections
    val bible = Collection(1, "bible", "bible", "Bible", null)
    val ot = Collection(1, "bible-ot", "testament", "Old Testament", null)
    val nt = Collection(2, "bible-nt", "testament", "New Testament", null)
    val collectionRepo = Injector.collectionRepo
    collectionRepo.insert(bible).blockingGet()
    collectionRepo.insert(ot).blockingGet()
    collectionRepo.insert(nt).blockingGet()
}
