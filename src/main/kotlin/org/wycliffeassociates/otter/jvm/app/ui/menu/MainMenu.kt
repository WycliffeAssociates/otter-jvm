package org.wycliffeassociates.otter.jvm.app.ui.menu

import com.github.thomasnield.rxkotlinfx.observeOnFx
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.domain.ImportResourceContainer
import org.wycliffeassociates.otter.common.domain.PluginActions
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*


class MainMenu : MenuBar() {

    val languageRepo = Injector.languageRepo
    val metadataRepo = Injector.metadataRepo
    val collectionRepo = Injector.collectionRepo
    val directoryProvider = Injector.directoryProvider
    val pluginRepository = Injector.pluginRepository

    var loadingLabel: Menu = Menu()

    init {
        menu("File") {
            item("Import Resource Container") {
                graphic = MaterialIconView(MaterialIcon.INPUT, "20px")
                action {
                    val file = chooseDirectory("Please Select Resource Container to Import")
                    file?.let {
                        loadingLabel.isVisible = true
                        val importer = ImportResourceContainer(
                                languageRepo,
                                metadataRepo,
                                collectionRepo,
                                directoryProvider
                        )
                        importer
                                .import(file)
                                .subscribeOn(Schedulers.io())
                                .observeOnFx()
                                .subscribe {
                                    loadingLabel.isVisible = false
                                }
                    }
                }
            }
            menu("Default Audio Plugin") {
                graphic = MaterialIconView(MaterialIcon.MIC, "20px")
                val pluginToggleGroup = ToggleGroup()

                // Get the plugins from the use case
                val pluginActions = PluginActions(pluginRepository)
                pluginActions
                        .getAllPluginData()
                        .observeOnFx()
                        .doOnSuccess { pluginData ->
                            pluginData.forEach {
                                radiomenuitem(it.name) {
                                    userData = it
                                    action {
                                        pluginActions.setDefaultPluginData(it).subscribe()
                                    }
                                    toggleGroup = pluginToggleGroup
                                }
                            }
                        }
                        // Select the default plugin
                        .flatMapMaybe {
                            pluginActions.getDefaultPluginData()
                        }
                        .observeOnFx()
                        .subscribe { plugin ->
                            pluginToggleGroup
                                    .toggles
                                    .filter { it.userData == plugin }
                                    .firstOrNull()
                                    ?.isSelected = true
                        }
            }
        }
        loadingLabel = menu {
            text = "Loading"
            isDisable = true
            isVisible = false
        }
    }
}