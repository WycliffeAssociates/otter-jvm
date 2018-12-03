package org.wycliffeassociates.otter.jvm.app.ui.splash.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import org.wycliffeassociates.otter.common.domain.languages.ImportLanguages
import org.wycliffeassociates.otter.common.domain.plugins.ImportAudioPlugins
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.menu.view.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.view.ProjectHomeView
import tornadofx.ViewModel
import tornadofx.Workspace
import tornadofx.add
import tornadofx.removeFromParent
import java.util.concurrent.TimeUnit

class SplashScreenViewModel : ViewModel() {
    val progressProperty = SimpleDoubleProperty(0.0)
    val shouldCloseProperty = SimpleBooleanProperty(false)

    init {
        initApp()
                .observeOnFx()
                .subscribe {
                    progressProperty.value = it
                    if (progressProperty.value == 1.0) {
                        val workspace = find<Workspace>()
                        workspace.header.removeFromParent()
                        workspace.add(MainMenu())
                        workspace.dock<ProjectHomeView>()
                        workspace.openWindow(owner = null)
                        shouldCloseProperty.value = true
                    }
                }
    }

    private fun initApp(): Observable<Double> {
        return Observable
                .fromPublisher<Double> {
                    it.onNext(0.0)
                    val injector: Injector = tornadofx.find()
                    it.onNext(0.25)

                    val initialized = injector.preferences.appInitialized()
                            .blockingGet()
                    if (!initialized) {
                        // Needs initialization
                        ImportLanguages(ClassLoader.getSystemResourceAsStream("content/langnames.json"), injector.languageRepo)
                                .import()
                                .onErrorComplete()
                                .subscribe()

                        injector.preferences.setAppInitialized(true).subscribe()
                    }
                    it.onNext(0.5)

                    // Always import new plugins
                    ImportAudioPlugins(injector.audioPluginRegistrar, injector.directoryProvider)
                            .importAll()
                            .andThen(injector.pluginRepository.initSelected())
                            .blockingAwait()
                    it.onNext(0.75)

                    // Always clean up database
                    injector.takeRepository
                            .removeNonExistentTakes()
                            .blockingAwait()
                    it.onNext(0.99)
                    Observable.timer(50, TimeUnit.MILLISECONDS).blockingFirst()
                    it.onNext(1.0)
                }.subscribeOn(Schedulers.io())
    }
}