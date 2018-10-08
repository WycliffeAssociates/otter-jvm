package org.wycliffeassociates.otter.jvm.app.ui.menu

import com.github.thomasnield.rxkotlinfx.observeOnFx
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.control.MenuBar
import javafx.scene.control.ToggleGroup
import org.wycliffeassociates.otter.common.domain.ImportResourceContainer
import org.wycliffeassociates.otter.jvm.app.DefaultPluginPreference
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*


class MainMenu : MenuBar() {

    val languageRepo = Injector.languageRepo
    val metadataRepo = Injector.metadataRepo
    val collectionRepo = Injector.collectionRepo
    val directoryProvider = Injector.directoryProvider
    val pluginRepo = Injector.pluginRepository

    init {
        with(this) {
            menu("File") {
                item("Import Resource Container") {
                    action {
                        val file = chooseDirectory("Please Select Resource Container to Import")
                        file?.let {
                            val importer = ImportResourceContainer(languageRepo, metadataRepo, collectionRepo, directoryProvider)
                            importer.import(file)
                        }
                    }
                }
                menu("Default Audio Plugin") {
                    pluginRepo
                            .getAll()
                            .observeOnFx()
                            .subscribe { pluginData ->
                                val pluginToggleGroup = ToggleGroup()
                                pluginData.forEach {
                                    radiomenuitem(it.name) {
                                        // TODO: Get default from plugin repo
                                        if (it.id == DefaultPluginPreference.defaultPluginData?.id) {
                                            // This is the current default
                                            isSelected = true
                                        }
                                        action {
                                            DefaultPluginPreference.defaultPluginData = it
                                        }
                                        toggleGroup = pluginToggleGroup
                                    }
                                }
                            }
                }
            }

        }
    }
}